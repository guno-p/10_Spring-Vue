import { ref, computed, reactive } from 'vue';
import { defineStore } from 'pinia';
import axios from 'axios';

const initState = {
  token: '', // 접근 토큰(JWT)
  user: {
    username: '', // 사용자 ID
    email: '', // Email
    roles: [], // 권한 목록
  },
  avatarTimeStamp: Date.now(), // (1) 아바타 이미지 경로에 추가할 쿼리스트링 타임스탬프 - 변경을 위한 랜덤값
};

export const useAuthStore = defineStore('auth', () => {
  const state = ref({ ...initState });

  const isLogin = computed(() => !!state.value.user.username); // 로그인 여부
  const username = computed(() => state.value.user.username); // 로그인 사용자 ID
  const email = computed(() => state.value.user.email); // 로그인 사용자 email

  // (2) 로그인 여부에 따라 아바타 이미지 다운로드 주소 변경
  const avatarUrl = computed(() =>
    state.value.user.username // 로그인 상태라면
      ? `/api/member/${state.value.user.username}/avatar?t=${state.value.avatarTimeStamp}`
      : null
  );

  // (3) 아바타 업데이트 액션 추가
  const updateAvatar = () => {
    state.value.avatarTimeStamp = Date.now();
    localStorage.setItem('auth', JSON.stringify(state.value));
  };

  const login = async (member) => {
    // 실제 API 호출 <- 추가
    const { data } = await axios.post('/api/auth/login', member);
    state.value = { ...data }; // 서버 응답 데이터로 상태 업데이트
    // localStorage에 상태 저장
    localStorage.setItem('auth', JSON.stringify(state.value));
  };

  const logout = () => {
    localStorage.clear();
    state.value = { ...initState };
  };

  const getToken = () => state.value.token;

  const load = () => {
    const auth = localStorage.getItem('auth');
    if (auth != null) {
      state.value = JSON.parse(auth);
      console.log(state.value);
    }
  };

  const changeProfile = (member) => {
    state.value.user.email = member.email;
    localStorage.setItem('auth', JSON.stringify(state.value));
  };

  load();

  return {
    state,
    username,
    email,
    isLogin,
    changeProfile,
    login,
    logout,
    getToken,

    // (4) avatar 관련 return 추가
    avatarUrl,
    updateAvatar,
  };
});
