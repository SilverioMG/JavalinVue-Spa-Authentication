# Web SPA usando JavalinVue Framework: (Autenticación sin 'server sessions' ni 'cookies'. Simulando el enfoque de Auth en una web SPA.)

- Este proyecto es una web SPA utilizando Javalin Framework con VueJS (JavalinVue).
  Frontend y Backend en el mismo servidor.
- El frontend está implementado con Vuejs (views/componentes), el routing se hace
  en el server (esto es genial) y la seguridad también se hace en el server (tanto
  para las vistas vuejs como para las peticiones a la api rest se utiliza el mismo mecanismo
  de seguridad).

<br/>


### Enlaces:

[Javalin Official Site](https://javalin.io/)

[JavalinVue Plugin](https://javalin.io/plugins/javalinvue)

[Clean Vue frontends without the hassle](https://javalin.io/tutorials/simple-frontends-with-javalin-and-vue)


<br/>

### Compilar y ejecutar el Servicio Web:

* mvn clean package
* java -jar target/javalinvue-1.0-SNAPSHOT-jar-with-dependencies.jar
* Open webbrowser on http://127.0.0.1:8000/


<br/>

### Descripción:
Este proyecto es una prueba para implementar la Autenticación en Javalin/JavalinVue
sin tener que utilizar *sessions* ni *cookies*.

Se intenta emular el funcionamiento de la Autenticación en la parte fronted igual que
se hace en una web SPA normal (Angular 2+, React...) donde las credenciales del usuario
(basic, token, JWT...) se guarda en la memoria del navegador mientras se está ejecutando
el código javascript y se envían dichas credenciales hacia el backend en cada petición Http.

Para simular este comportamiento se hace lo siguiente:
- Se utiliza JWT (Json Web Token). La autenciación se realiza por medio de 'nombre de usuario'
  y 'password' y se genera un token JWT que se devuelve al frontend.
- El frontend envía el token JWT en cada petición hacia el servidor.
- El servidor extrae el token JWT de la petición Http en un 'interceptor handler'
  que se ejecuta antes que la 'autorización' del endpoint correspondiente.
  En dicho 'interceptor' se obtiene el token JWT y se añade la info del usuario al 'context'
  de la petición Http para poder recuperarla cuando se realice la 'autorización' en el 'AccessManager'
  de Javalin.
- El 'AccessManager' de Javalin es el encargado de realizar la 'autorización' tanto para los endpoints
  de la api rest como de las vistas JavalinVue para el frontend.
  Se definen los roles o permisos para cada endpoint/vista y en el 'AccessManager' se comprueba
  si el Usuario actual (extraído del token JWT enviado desde el frontend) tiene los roles o permisos
  correspondientes para poder acceder al recurso.
  Esto es genial porque se utiliza el mismo mecanismo de autorización tanto para la api rest como
  para el routing del frontend y ambos se realizan en el mismo punto (en el server).
- Siempre que se haya recibido una petición con token JWT en el server (tanto para la api rest como
  navegar a una nueva vista), se envía automáticamente hacia el frontend al info del usuario autenticado
  por medio de JavalinVue 'stateFunction'. Así desde el fronted siempre se puede utilizar
  la info del usuario autenticado para envíar de nuevo el token JWT hacia el server en cada petición.
  **Este ciclo de envío y recepción del token JWT entre el frontend y el backend es lo que nos permite
  poder prescindir de 'cookies' y 'server sessions' donde se almacenaría dicho token.**

  <br/>
El token JWT se envía desde el frontend al backend (server) de 2 maneras distintas:
- Si se está haciendo una petición Http contra la Api Rest, se envía el token JWT en el
  Header Http 'Authorization' (como es habitual en cualquier api rest).

- Si se está navegando a otra vista desde el frontend, el routing se hace en el server
  y las peticiones no son contra la api rest, sino que son direccionamientos web del tipo
  `<a href=/users>users</a>`
  <br/>
  o desde código javascript `window.location.href='/users'`.
  En ninguno de estos 2 casos se puede envíar Http Header hacia el server, por lo que a la
  hora de navegar hacia otra vista/página, se envía el token JWT como 'query params' en la url.
  Quedando la url de tal manera: `http://localhost:8080/users/?token=eyJhbGci...`

- En el frontend se utiliza la variable *'Vue.prototype.$javalin.state.userLogged'* para obtener
  la info del usuario autenticado y su token JWT. En esta variable javascript se recibe
  la info del usuario enviada desde el server por medio de JavalinVue 'stateFunction' hacia el fronted.
  Y desde el fronted se utiliza esta variable para envíar el token JWT en cada petición hacia el server.

- Se añade un archivo 'authHelper.js' al proyecto, que se carga automáticamente para
  cada vista. Este archivo tiene funciones javascript para ayudarnos a la hora de
  obtener la info del usuario autenticado, realizar peticiones http contra la api rest
  utilizando autenticación y navegar a otras vistas utilizando autenticación.

### Utilizando 'authHelper.js' desde el frontend:
- **function getUserLogged()**: Esta función devuelve la info del usuario autenticado si existe.
  Se recupera la info del usuario directamente de 'Vue.prototype.$javalin.state.userLogged' que es donde
  el servidor le pasa info al frontend después de cada petición por medio de JavalinVue 'stateFunction'.
  Cada vez que se navegue a una nueva vista, con esta función se puede recuperar la info del usuario
  autenticado.


- **function setUserLogged(user)**: Se utiliza para asignar directamente desde el frontend la info
  del usuario autenticado. Se modifica la info de 'Vue.prototype.$javalin.state.userLogged'.
  Solo es aconsejable utilizar esta función para hacer un logout y asignar 'null' a la info
  del usuario autenticado, ya que desde la variable 'Vue.prototype.$javalin.state.userLogged' se obtiene 
  el token JWT cuando se realiza una petición hacia el server y podríamos obtener resultados inesperados 
  al modificar el token JWT.


- **function getViewUrl(viewUrl)**: Esta función se utiliza cada vez que se quiera
  navegar a otra vista/página. Lo que hace es añadir automáticamente el query param
  correspondiente con el token JWT del usuario autenticado (si existe).
  Debe utilizarse para navegar a cualquier vista/página que esté protegida en el server
  y poder realizar correctamente la autorización.

         `<a :href=getViewUrl(`/users/1`)>Navigate to user1 view</a>`

  El resultado sería algo como esto:
        
         `<a :href="/users/1?token=eyJhbGci...">Navigate to user1 view</a>`

- **const fetchAuth = useFetchWrapper()**:  En la constante 'fetchAut' se guarda el objeto
  devuelto por la función 'useFetchWrapper()'.
  Se utiliza para realizar peticiones Http contra la api rest utilizando 'fetch' de
  javascript.
  En este wrapper se añade automáticamente el header 'Authorization' con el token JWT
  del usuario autenticado (si existe).
  También convierte automáticamente la respuesta a formato JSON y obtiene el mensaje
  de error en caso de que se produzca alguno.
  
      `fetchAuth.get("/api/users")
          .then(response => this.users = response.result)
          .catch(() => alert("Error while fetching users"));`

      `fetchAuth.get(`/api/users/${userId}`)
          .then(response => this.user = response.result)
          .catch((error) => alert(`Error while fetching user: ${error}`));`

      `fetchAuth.post("/api/auth/", credentials)
          .then(response => {
            setUserLogged(response.result);
            this.isLoged = true;
          })
          .catch((error) => alert(error));`