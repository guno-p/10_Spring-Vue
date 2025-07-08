import './assets/main.css';
import 'bootstrap/dist/css/bootstrap.css'; // Bootstrap CSS 추가
import 'vue-awesome-paginate/dist/style.css'; // 페이지네이션 스타일

import { createApp } from 'vue';
import { createPinia } from 'pinia';
import VueAwesomePaginate from 'vue-awesome-paginate'; // 페이지네이션 컴포넌트
import { useKakao } from 'vue3-kakao-maps/@utils';

import App from './App.vue';
import router from './router';

// JavaScript 키로 Kakao API 초기화
const rest_api_key = 'a80290d0d6e32f2e6c852690f6831073';
useKakao(rest_api_key, ['services']); // services 라이브러리 포함

const app = createApp(App);
app.use(VueAwesomePaginate);
app.use(createPinia());
app.use(router);
app.mount('#app');
