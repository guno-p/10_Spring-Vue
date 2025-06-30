<template>
  <div class="container mt-5">
    <h1 class="mb-4">Login</h1>

    <!-- 로그인 화면 -->
    <div>
      <form @submit.prevent="handleLogin" class="card p-4 shadow-sm bg-light">
        <div class="mb-3">
          <label for="username" class="form-label">
            <i class="fa-solid fa-user"></i> Username</label
          >
          <input
            type="text"
            v-model="member.username"
            class="form-control"
            id="username"
            required
          />
        </div>

        <div class="mb-3">
          <label for="password" class="form-label">
            <i class="fa-solid fa-lock"></i> Password</label
          >
          <input
            type="password"
            v-model="member.password"
            class="form-control"
            id="password"
            required
          />
        </div>

        <button
          type="submit"
          class="btn btn-primary w-100"
          :disabled="disableSubmit"
        >
          <i class="fa-solid fa-right-to-bracket"></i>
          Login
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router';

const router = useRouter();
const auth = useAuthStore();

// 폼 데이터 관리
const member = reactive({
  username: '',
  password: '',
});

const error = ref('');
const disableSubmit = computed(() => !(member.username && member.password));

const handleLogin = async () => {
  console.log(member);
  try {
    await auth.login(member); // 인증 스토어의 login 액션 호출
    router.push('/'); // 성공 시 홈페이지로 이동
  } catch (e) {
    console.error('로그인 실패:', e);
    alert('로그인 실패: 아이디 또는 비밀번호를 확인하세요');
  }
};
</script>
