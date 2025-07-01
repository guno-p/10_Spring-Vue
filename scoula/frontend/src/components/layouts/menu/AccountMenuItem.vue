<script setup>
import { useAuthStore } from '@/stores/auth';
import { computed } from 'vue';

const auth = useAuthStore();
//const avatar = `/api/member/${props.username}/avatar`; // 동적 아바타 생성
const avatar = computed(() => auth.avatarUrl);

const props = defineProps({ username: String });
</script>

<template>
  <li class="nav-item">
    <router-link class="nav-link" to="/auth/profile">
      <img :src="avatar" class="avatar avatar-sm" />
      {{ username }}
    </router-link>
  </li>
</template>
