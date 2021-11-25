<template id="user-profile">
  <app-frame>
    <div class="user-profile-data">
      <ul v-if="user">
        <dt>User ID</dt>
        <dd>{{user.id}}</dd>
        <dt>Name</dt>
        <dd>{{user.name}}</dd>
        <dt>Email</dt>
        <dd>{{user.email}}</dd>
        <dt>Birthday</dt>
        <dd>{{user.userDetails.dateOfBirth}}</dd>
        <dt>Salary</dt>
        <dd>{{user.userDetails.salary}}</dd>
      </ul>
    </div>

    <div>
        <button v-on:click="back">Back</button>
    </div>
  </app-frame>
</template>
<script>
Vue.component("user-profile", {
  template: "#user-profile",
  data: () => ({
    user: null,
  }),
  created() {
    const userId = this.$javalin.pathParams["user-id"];
    fetchAuth.get(`/api/users/${userId}`)
        .then(response => this.user = response.result)
        .catch((error) => alert(`Error while fetching user: ${error}`));
  },
  methods: {
    back(){
      window.location.href = getViewUrl("/users");
    }
  }
});
</script>
<style>
  .user-profile-data dt {
    font-weight: bold;
  }
</style>