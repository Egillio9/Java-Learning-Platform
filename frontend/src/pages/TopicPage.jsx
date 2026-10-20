import { useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { ErrorState, PageLoader } from '../components/Feedback'
import { learnApi } from '../api/endpoints'
import { useAuth } from '../context/AuthContext'
import { useFetch } from '../hooks/useFetch'

export default function TopicPage() {
  const { slug } = useParams()
  const { user } = useAuth()
  const navigate = useNavigate()
  const topic = useFetch(() => learnApi.topic(slug), [slug])
  const questions = useFetch(() => learnApi.questions(slug), [slug])
  const progress = useFetch(() => (user ? learnApi.progress() : Promise.resolve(null)), [user?.id, slug])
  const [answers, setAnswers] = useState({})
  const [quiz, setQuiz] = useState(null)
  const [message, setMessage] = useState('')

  if (topic.loading || questions.loading) return <PageLoader />
  if (topic.error) return <ErrorState error={topic.error} onRetry={topic.reload} />

  const completed = new Set(progress.data?.completedLessonIds ?? [])
  const signInFirst = () => navigate(`/login?next=${encodeURIComponent(`/learn/${slug}`)}`)

  const complete = async (lessonId) => {
    if (!user) return signInFirst()
    try {
      progress.setData(await learnApi.completeLesson(lessonId))
    } catch (err) {
      setMessage(err.message)
    }
  }

  const submitQuiz = async (event) => {
    event.preventDefault()
    if (!user) return signInFirst()
    if (!Object.keys(answers).length) return setMessage('Answer at least one question first.')
    setMessage('')
    try {
      setQuiz(await learnApi.submitQuiz(slug, answers))
      progress.reload()
    } catch (err) {
      setMessage(err.message)
    }
  }

  const feedback = Object.fromEntries((quiz?.feedback ?? []).map((f) => [f.questionId, f]))
  const t = topic.data

  return (
    <div className="page topic-page">
      <Link to="/learn" className="link small">← All topics</Link>
      <header className="topic-head">
        <span className={`level-chip level-${t.level.toLowerCase()}`}>{t.level}</span>
        <h1>{t.title}</h1>
        <p className="muted">{t.summary}</p>
      </header>

      {t.lessons.map((lesson, index) => (
        <article key={lesson.id} className="card lesson">
          <h2>{index + 1}. {lesson.title}</h2>
          <p className="lesson-text">{lesson.content}</p>
          {lesson.codeExample && <pre className="code-block">{lesson.codeExample.trim()}</pre>}
          <button className={`btn btn-sm ${completed.has(lesson.id) ? 'btn-done' : 'btn-secondary'}`}
                  onClick={() => complete(lesson.id)}>
            {completed.has(lesson.id) ? '✓ Completed' : 'Mark as complete'}
          </button>
        </article>
      ))}

      <section className="card quiz">
        <h2>Quiz: check your understanding</h2>
        <form onSubmit={submitQuiz}>
          {(questions.data ?? []).map((q, index) => {
            const f = feedback[q.id]
            return (
              <fieldset key={q.id} className={`question${f ? (f.correct ? ' right' : ' wrong') : ''}`}>
                <legend>{index + 1}. {q.prompt}</legend>
                {q.options.map((option, optionIndex) => (
                  <label key={optionIndex} className={`option${f && optionIndex === f.correctOption ? ' correct-option' : ''}`}>
                    <input type="radio" name={`q${q.id}`} checked={answers[q.id] === optionIndex}
                           onChange={() => setAnswers({ ...answers, [q.id]: optionIndex })} />
                    {option}
                  </label>
                ))}
                {f && <p className="explanation">{f.explanation}</p>}
              </fieldset>
            )
          })}
          {message && <p className="form-error">{message}</p>}
          <div className="quiz-foot">
            <button className="btn btn-primary">{quiz ? 'Submit again' : 'Submit answers'}</button>
            {quiz && <span className="quiz-score">You scored {quiz.score}/{quiz.total} ({quiz.percentage}%)</span>}
          </div>
        </form>
      </section>
    </div>
  )
}
