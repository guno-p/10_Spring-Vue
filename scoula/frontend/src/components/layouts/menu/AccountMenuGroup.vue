<script setup>
import { computed } from 'vue';
import MenuItem from './MenuItem.vue';
import AccountMenuItem from './AccountMenuItem.vue';
import LogoutMenuItem from './LogoutMenuItem.vue';
import config from '@/config';

import { useAuthStore } from '@/stores/auth.js';

// 분해 할당 - 우측에서 이름이 같은 것
const { login, join } = config.accoutMenus;

const auth = useAuthStore(); // <- 추가
const isLogin = computed(() => auth.isLogin); // 로그인 상태
const username = computed(() => auth.username); // 사용자명
</script>

<template>
  <ul class="navbar-nav ms-auto">
    <template v-if="isLogin">
      <AccountMenuItem :username="username" />
      <LogoutMenuItem />
    </template>
    <template v-else>
      <MenuItem :menu="login" />
      <MenuItem :menu="join" />
    </template>
  </ul>
</template>
