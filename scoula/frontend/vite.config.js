import { fileURLToPath, URL } from 'node:url';

import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import vueDevTools from 'vite-plugin-vue-devtools';

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue(), vueDevTools()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        // /api/xxx로 요청 -> http://localhost:8080/api/xxx
      },
    },
  },
  build: {
    outDir:
      'C:/KB_Fullstack/10_Spring+Vue/scoula/backend/src/main/webapp/resources',
  },
});
