<template>
  <DefaultLayout>
    <router-view />
  </DefaultLayout>
</template>

<style scoped></style>

<script setup>
import { RouterView } from 'vue-router';
import DefaultLayout from './components/layouts/DefaultLayout.vue';
</script>
