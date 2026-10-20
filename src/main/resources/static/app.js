const state = {
    username: localStorage.getItem('javalearn.username') || '',
    topics: [],
    progress: null,
    currentSlug: null,
};

const $ = (id) => document.getElementById(id);

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text ?? '';
    return div.innerHTML;
}

function toast(message, isError = false) {
    const el = $('toast');
    el.textContent = message;
    el.className = `toast${isError ? ' error' : ''}`;
    clearTimeout(toast.timer);
    toast.timer = setTimeout(() => el.classList.add('hidden'), 2600);
}

function topicProgress(slug) {
    return state.progress?.topics.find((t) => t.slug === slug);
}

function renderHeader() {
    const loggedIn = Boolean(state.username);
    $('login-form').classList.toggle('hidden', loggedIn);
    $('learner').classList.toggle('hidden', !loggedIn);
    if (!loggedIn) return;
    const overall = state.progress?.overallPercentage ?? 0;
    $('learner-name').textContent = state.username;
    $('overall-bar').style.width = `${overall}%`;
    $('overall-text').textContent = `${overall}% complete`;
}

function renderTopicList() {
    $('topic-list').innerHTML = state.topics.map((topic) => {
        const progress = topicProgress(topic.slug);
        const pct = progress?.completionPercentage ?? 0;
        const best = progress?.bestQuizPercentage;
        return `
            <li class="${topic.slug === state.currentSlug ? 'active' : ''}" data-slug="${topic.slug}">
                <div class="topic-title">${escapeHtml(topic.title)}</div>
                <div class="topic-meta">
                    <span class="badge ${topic.level.toLowerCase()}">${topic.level}</span>
                    ${best != null ? `<span>Quiz best ${best}%</span>` : `<span>${topic.lessonCount} lessons</span>`}
                </div>
                <div class="bar"><div style="width:${pct}%"></div></div>
            </li>`;
    }).join('');
}

async function openTopic(slug) {
    state.currentSlug = slug;
    renderTopicList();
    const [topic, questions] = await Promise.all([api.topic(slug), api.questions(slug)]);
    const completed = new Set(state.progress?.completedLessonIds ?? []);

    $('content').innerHTML = `
        <section class="topic-head">
            <span class="badge ${topic.level.toLowerCase()}">${topic.level}</span>
            <h1>${escapeHtml(topic.title)}</h1>
            <p>${escapeHtml(topic.summary)}</p>
        </section>
        ${topic.lessons.map((lesson, i) => `
            <article class="lesson">
                <h2>${i + 1}. ${escapeHtml(lesson.title)}</h2>
                <p>${escapeHtml(lesson.content)}</p>
                ${lesson.codeExample ? `<pre><code>${escapeHtml(lesson.codeExample.trim())}</code></pre>` : ''}
                <button class="complete ${completed.has(lesson.id) ? 'done' : ''}" data-lesson="${lesson.id}">
                    ${completed.has(lesson.id) ? '✓ Completed' : 'Mark as complete'}
                </button>
            </article>`).join('')}
        <section class="quiz">
            <h2>Practice quiz</h2>
            <form id="quiz-form">
                ${questions.map((q, i) => `
                    <fieldset data-question="${q.id}">
                        <legend>${i + 1}. ${escapeHtml(q.prompt)}</legend>
                        ${q.options.map((option, index) => `
                            <label><input type="radio" name="q${q.id}" value="${index}"> ${escapeHtml(option)}</label>
                        `).join('')}
                        <p class="explanation hidden"></p>
                    </fieldset>`).join('')}
                <button type="submit">Submit answers</button>
                <div id="quiz-score" class="score hidden"></div>
            </form>
        </section>`;

    $('content').querySelectorAll('button.complete').forEach((button) => {
        button.addEventListener('click', () => completeLesson(Number(button.dataset.lesson)));
    });
    $('quiz-form').addEventListener('submit', (event) => submitQuiz(event, slug));
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

function requireLogin() {
    if (state.username) return true;
    toast('Enter your name first so progress can be saved', true);
    $('username').focus();
    return false;
}

async function completeLesson(lessonId) {
    if (!requireLogin()) return;
    try {
        state.progress = await api.completeLesson(lessonId, state.username);
        renderHeader();
        await openTopic(state.currentSlug);
        toast('Lesson marked complete');
    } catch (error) {
        toast(error.message, true);
    }
}

async function submitQuiz(event, slug) {
    event.preventDefault();
    if (!requireLogin()) return;
    const answers = {};
    event.target.querySelectorAll('fieldset').forEach((fieldset) => {
        const chosen = fieldset.querySelector('input:checked');
        if (chosen) answers[fieldset.dataset.question] = Number(chosen.value);
    });
    if (Object.keys(answers).length === 0) {
        toast('Answer at least one question', true);
        return;
    }
    try {
        const result = await api.submitQuiz(slug, state.username, answers);
        result.feedback.forEach((item) => {
            const fieldset = event.target.querySelector(`fieldset[data-question="${item.questionId}"]`);
            fieldset.classList.remove('right', 'wrong');
            fieldset.classList.add(item.correct ? 'right' : 'wrong');
            fieldset.querySelectorAll('label').forEach((label, index) => {
                label.classList.toggle('correct-option', index === item.correctOption);
            });
            const explanation = fieldset.querySelector('.explanation');
            explanation.textContent = item.explanation;
            explanation.classList.remove('hidden');
        });
        const score = $('quiz-score');
        score.textContent = `You scored ${result.score}/${result.total} (${result.percentage}%)`;
        score.classList.remove('hidden');
        state.progress = await api.progress(state.username);
        renderHeader();
        renderTopicList();
    } catch (error) {
        toast(error.message, true);
    }
}

async function loadProgress() {
    if (!state.username) {
        state.progress = null;
        return;
    }
    try {
        state.progress = await api.progress(state.username);
    } catch (error) {
        // A brand-new learner has no record until their first lesson or quiz.
        state.progress = null;
        if (error.status !== 404) toast(error.message, true);
    }
}

async function refresh() {
    await loadProgress();
    renderHeader();
    renderTopicList();
}

$('login-form').addEventListener('submit', async (event) => {
    event.preventDefault();
    state.username = $('username').value.trim();
    if (!state.username) return;
    localStorage.setItem('javalearn.username', state.username);
    await refresh();
    toast(`Welcome, ${state.username}!`);
});

$('logout').addEventListener('click', async () => {
    state.username = '';
    localStorage.removeItem('javalearn.username');
    await refresh();
});

$('topic-list').addEventListener('click', (event) => {
    const item = event.target.closest('li[data-slug]');
    if (item) openTopic(item.dataset.slug).catch((error) => toast(error.message, true));
});

(async function init() {
    try {
        state.topics = await api.topics();
        await refresh();
    } catch (error) {
        toast(`Could not load topics: ${error.message}`, true);
    }
})();
