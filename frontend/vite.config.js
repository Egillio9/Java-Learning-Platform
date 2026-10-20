import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// The build lands in Spring Boot's static folder so one JAR serves API + UI.
// In dev, Vite proxies API and OAuth routes to the Spring Boot server on :8080.
export default defineConfig({
  plugins: [react()],
  build: {
    outDir: '../src/main/resources/static',
    emptyOutDir: true,
    chunkSizeWarningLimit: 900,
  },
  server: {
    port: 5173,
    proxy: {
      '/api': 'http://localhost:8080',
      '/oauth2': 'http://localhost:8080',
      '/login/oauth2': 'http://localhost:8080',
    },
  },
})
