<template id="user-overview">
  <app-frame>
    <div>
      <ul class="user-overview-list">
        <li v-for="user in users">
          <a :href=getViewUrl(`/users/${user.id}`)>{{user.name}} - password: {{user.password}})</a>
        </li>
      </ul>
    </div>

    <div v-if="!isLoged">
      <div>
        <label for="userName">User Name</label>
        <input v-model="userName" id="userName" type="text" placeholder="Enter username"/>
        <label for="userPassword">User Password</label>
        <input v-model="userPassword" id="userPassword" type="text" placeholder="Enter userpassword"/>
      </div>
      <button v-on:click="login">Login</button>
    </div>
    <div v-else>
      <button v-on:click="logout">Logout</button>
    </div>
  </app-frame>
</template>

<script>
Vue.component("user-overview", {
  template: "#user-overview",
  data: () => ({
    users: [],
    userName: '',
    userPassword: '',
    isLoged: (getUserLogged() != null)
  }),
  created() {
    //Como el endpoint "/api/users' no está protegido se puede utlizar tanto 'fetch()' (sin añadir header de Authorization) o 'fetchAuth.get()':
    /*fetch("/api/users")
        .then(res => res.json())
        .then(res => this.users = res.result)
        .catch(() => alert("Error while fetching users"));
     */
    fetchAuth.get("/api/users")
        .then(response => this.users = response.result)
        .catch(() => alert("Error while fetching users"));
  },
  methods: {
    login() {
      let credentials = {
        name: this.userName,
        password: this.userPassword
      };

      fetchAuth.post("/api/auth/", credentials)
          .then(response => {
            setUserLogged(response.result);
            this.isLoged = true;
          })
          .catch((error) => alert(error));
    },
    logout(){
      setUserLogged(null);
      this.isLoged = false;

      window.location.href = getViewUrl("/");
    }
  }
});
</script>

<style>
ul.user-overview-list {
  padding: 0;
  list-style: none;
}
ul.user-overview-list a {
  display: block;
  padding: 16px;
  border-bottom: 1px solid #ddd;
}
ul.user-overview-list a:hover {
  background: #00000010;
}
</style>