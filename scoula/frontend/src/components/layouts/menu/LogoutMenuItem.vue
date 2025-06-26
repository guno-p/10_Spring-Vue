<script setup>
import { useRouter } from 'vue-router';

const router = useRouter();
const logout = (e) => {
  // 로그아웃
  // pinia 상태 관리 필요
  router.push('/');
};
</script>

<template>
  <!-- 다른 페이지 지정 X, a 태그 가능 -->
  <!--
   이벤트에 대한 설정 
   stop : 버블링을 막겠다 : 부모는 처리 안하겠음
   prevent : default 액션을 차단 > a 태그의 클릭 이벤트 핸들러 차단
             ex) form 을 submit 하지 않겠다.
   -->
  <a href="#" class="nav-link" @click.prevent="logout">
    <i class="fa-solid fa-right-from-bracket"></i>
    로그아웃
  </a>
</template>
