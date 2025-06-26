<script setup>
import { computed } from 'vue';
import MenuItem from './MenuItem.vue';
import AccountMenuItem from './AccountMenuItem.vue';
import LogoutMenuItem from './LogoutMenuItem.vue';
import config from '@/config';

// 분해 할당 - 우측에서 이름이 같은 것
const { login, join } = config.accoutMenus;

const islogin = computed(() => false); // 임시: 로그인하지 않음
const username = computed(() => ''); // 임시: 사용자명 없음
</script>

<template>
  <ul class="navbar-nav ms-auto">
    <template v-if="islogin">
      <AccountMenuItem :username="username" />
      <LogoutMenuItem />
    </template>
    <template v-else>
      <MenuItem :menu="login" />
      <MenuItem :menu="join" />
    </template>
  </ul>
</template>
