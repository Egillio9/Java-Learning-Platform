const api = {
    async request(path, options = {}) {
        const response = await fetch(path, {
            headers: { 'Content-Type': 'application/json' },
            ...options,
        });
        const body = await response.json().catch(() => null);
        if (!response.ok) {
            const error = new Error(body?.message || `Request failed (${response.status})`);
            error.status = response.status;
            throw error;
        }
        return body;
    },

    topics: () => api.request('/api/topics'),
    topic: (slug) => api.request(`/api/topics/${encodeURIComponent(slug)}`),
    questions: (slug) => api.request(`/api/topics/${encodeURIComponent(slug)}/questions`),
    submitQuiz: (slug, username, answers) => api.request(`/api/topics/${encodeURIComponent(slug)}/quiz`, {
        method: 'POST',
        body: JSON.stringify({ username, answers }),
    }),
    completeLesson: (lessonId, username) => api.request(`/api/lessons/${lessonId}/complete`, {
        method: 'POST',
        body: JSON.stringify({ username }),
    }),
    progress: (username) => api.request(`/api/learners/${encodeURIComponent(username)}/progress`),
};
