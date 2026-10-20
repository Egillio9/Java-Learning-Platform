import { request } from './client'

const enc = encodeURIComponent

export const authApi = {
  me: () => request('/api/auth/me'),
  providers: () => request('/api/auth/providers'),
  guest: (name) => request('/api/auth/guest', { method: 'POST', body: { name } }),
  logout: () => request('/api/auth/logout', { method: 'POST' }),
  googleUrl: '/oauth2/authorization/google',
}

export const problemsApi = {
  list: () => request('/api/problems'),
  detail: (slug) => request(`/api/problems/${enc(slug)}`),
  run: (slug, code, customInputs) =>
    request(`/api/problems/${enc(slug)}/run`, { method: 'POST', body: { code, customInputs } }),
  submit: (slug, code) => request(`/api/problems/${enc(slug)}/submit`, { method: 'POST', body: { code } }),
  submissions: (slug) => request(`/api/problems/${enc(slug)}/submissions`),
  solution: (slug) => request(`/api/problems/${enc(slug)}/solution`),
}

export const learnApi = {
  topics: () => request('/api/topics'),
  topic: (slug) => request(`/api/topics/${enc(slug)}`),
  questions: (slug) => request(`/api/topics/${enc(slug)}/questions`),
  submitQuiz: (slug, answers) => request(`/api/topics/${enc(slug)}/quiz`, { method: 'POST', body: { answers } }),
  completeLesson: (id) => request(`/api/lessons/${id}/complete`, { method: 'POST' }),
  progress: () => request('/api/me/progress'),
}

export const statsApi = {
  dashboard: () => request('/api/me/dashboard'),
  leaderboard: () => request('/api/leaderboard'),
  platform: () => request('/api/stats'),
}
