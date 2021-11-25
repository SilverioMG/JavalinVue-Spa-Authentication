/**
 * Esta función devuelve las credenciales del usuario (Autenticación Básica (user:password) ó Token) que se
 * reciben desde el servidor una vez que el Usuario ha sido autenticado.
 * Se utilizan para volver a enviarlas en cada petición Http a la api del servidor o cuando se quiere navegar
 * a otra vista y se realiza la petición al servidor para el routing.
 */
class UserLogged {
    constructor(id, name, token, roles){
        this.id = id;
        this.name = name;
        this.token = token;
        this.roles = roles;
    }
}

function getUserLogged(){
    let userLogged = Vue.prototype.$javalin.state.userLogged;
    if(userLogged){
        return new UserLogged(userLogged.id, userLogged.name, userLogged.token, userLogged.roles);
    }

    return null;

}

function setUserLogged(user){
    let userLogged = null;
    if(user){
        userLogged = new UserLogged(user.id, user.name, user.token, user.roles);
    }

    Vue.prototype.$javalin.state.userLogged = userLogged;
}

/**
 * Esta función completa la 'url' de petición de una vista contra el servidor añadiendo las credenciales del usuario
 * logueado (si existen) como query params para realizar la Autorización en el servidor (Backend).
 */
function getViewUrl(viewUrl){
    const userLogged = getUserLogged();
    if(viewUrl && userLogged){
        let separator = (!viewUrl.includes('?')) ? '?' : '&';
        viewUrl = `${viewUrl}${separator}token=${userLogged.token}`;
    }

    return viewUrl;
}

//-------------------------------------- FETCH WRAPPER: ----------------------------------------------------------------
/**
 * Wrapper del objeto 'fetch' para añadir el header de 'Authorization' automáticamente en cada petición Http.
 * También se parse automáticamente el body de las respuestas a Json.
 *
 * Código modificado a partir de: https://jasonwatmore.com/post/2021/09/17/react-fetch-set-authorization-header-for-api-requests-if-user-logged-in
 * @returns {{post: (function(*=, *=): Promise<Response>), get: (function(*=, *=): Promise<Response>), delete: (function(*=, *=): Promise<Response>), put: (function(*=, *=): Promise<Response>)}}
 */
function useFetchWrapper() {
    return {
        get: request('GET'),
        post: request('POST'),
        put: request('PUT'),
        delete: request('DELETE')
    };

    function request(method) {
        return (url, body) => {
            const requestOptions = {
                method,
                headers: authHeader(url)
            };
            if (body) {
                requestOptions.headers['Content-Type'] = 'application/json';
                requestOptions.body = JSON.stringify(body);
            }
            return fetch(url, requestOptions).then(handleResponse);
        }
    }

    // helper functions
    function authHeader(url) {
        const token = getUserLogged()?.token;
        if (token) {
            return { Authorization: `Bearer ${token}` };
        } else {
            return {};
        }
    }

    function handleResponse(response) {
        return response.text().then(text => {
            let data = null;
            try{
                data = JSON.parse(text);
            }
            catch(error){
                console.log(`Http Response ERROR parsing body JSON for request '${response.url}' -> '${text}'`);
            }

            if(response.ok){
                return data;
            }
            else {
                const error = (data?.message) || response.statusText;
                return Promise.reject(error);
            }

            return data;
        });
    }
}

const fetchAuth = useFetchWrapper();