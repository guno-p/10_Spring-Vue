<template>
  <div class="container mt-5">
    <h1 class="mb-4">Login Test</h1>

    <!-- 로그인 X 화면 -->
    <div v-if="!isLoggedIn">
      <form @submit.prevent="handleLogin" class="card p-4 shadow-sm bg-light">
        <div class="mb-3">
          <label for="username" class="form-label">Username</label>
          <input
            type="text"
            v-model="loginForm.username"
            class="form-control"
            id="username"
            required
          />
        </div>

        <div class="mb-3">
          <label for="password" class="form-label">Password</label>
          <input
            type="password"
            v-model="loginForm.password"
            class="form-control"
            id="password"
            required
          />
        </div>

        <button type="submit" class="btn btn-primary w-100">Login</button>
      </form>
    </div>

    <!-- 로그인 O 화면 -->
    <div v-if="isLoggedIn" class="card p-4 shadow-sm bg-light">
      <h2 class="text-success mb-3">Welcome, {{ user.username }}</h2>
      <p class="mb-1"><strong>Email:</strong> {{ user.email }}</p>
      <p>
        <strong>Roles:</strong>
        <span
          class="badge bg-secondary me-1"
          v-for="role in user.roles"
          :key="role"
          >{{ role }}</span
        >
      </p>
      <button @click="handleLogout" class="btn btn-outline-danger mt-3">
        Logout
      </button>
    </div>
  </div>
</template>

<script setup>
import axios from 'axios';
import { ref, onMounted } from 'vue';

// 상태 변수들
const isLoggedIn = ref(false);
const loginForm = ref({
  username: '',
  password: '',
});

// 로그인 후 결과 데이터에서 매핑할 변수
const user = ref({
  username: '',
  email: '',
  roles: [],
});

const handleLogin = async () => {
  try {
    const response = await axios.post('/api/auth/login', loginForm.value);
    console.log('response.data :', response.data);

    // JWT와 사용자 정보 저장
    const { token, user: userInfo } = response.data;
    localStorage.setItem('token', token);
    user.value = userInfo;
    // user 정보 넣기 - JSON 변환 필요
    localStorage.setItem('userInfo', JSON.stringify(userInfo));

    isLoggedIn.value = true;

    // 초기화
    loginForm.value = {
      username: '',
      password: '',
    };
  } catch (e) {
    console.error('로그인 실패:', e);
    alert('로그인 실패: 아이디 또는 비밀번호를 확인하세요');
  }
};

const handleLogout = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('userInfo');

  // 초기화
  isLoggedIn.value = false;
  user.value = {
    username: '',
    email: '',
    roles: [],
  };
};

// 로그인 상태 확인 함수
const checkLoginStatus = () => {
  const token = localStorage.getItem('token');
  const savedUserInfo = localStorage.getItem('userInfo');

  if (token && savedUserInfo) {
    isLoggedIn.value = true;
    user.value = JSON.parse(savedUserInfo);
  } else {
    isLoggedIn.value = false;
    // 초기화
    user.value = {
      username: '',
      email: '',
      roles: [],
    };
  }
};

// 컴포넌트 마운트 시 로그인 상태 확인
onMounted(() => {
  checkLoginStatus();
});
</script>
