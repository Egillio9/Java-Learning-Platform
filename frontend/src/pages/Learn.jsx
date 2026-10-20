import { Link } from 'react-router-dom'
import { ErrorState, PageLoader } from '../components/Feedback'
import { learnApi } from '../api/endpoints'
import { useAuth } from '../context/AuthContext'
import { useFetch } from '../hooks/useFetch'

export default function Learn() {
  const { user } = useAuth()
  const topics = useFetch(() => learnApi.topics(), [])
  const progress = useFetch(() => (user ? learnApi.progress() : Promise.resolve(null)), [user?.id])

  if (topics.loading) return <PageLoader />
  if (topics.error) return <ErrorState error={topics.error} onRetry={topics.reload} />

  const byTopic = Object.fromEntries((progress.data?.topics ?? []).map((t) => [t.slug, t]))
  const levels = ['BEGINNER', 'INTERMEDIATE', 'ADVANCED']

  return (
    <div className="page learn-page">
      <header className="page-header">
        <div>
          <h1>Java course</h1>
          <p className="muted">
            {topics.data.length} topics, from your first variable to streams and concurrency. Each topic ends with a graded quiz.
            {progress.data && <> You have completed <strong>{progress.data.overallPercentage}%</strong>.</>}
          </p>
        </div>
      </header>

      {levels.map((level) => {
        const items = topics.data.filter((t) => t.level === level)
        if (!items.length) return null
        return (
          <section key={level} className="level-section">
            <h2 className="level-title">{level[0] + level.slice(1).toLowerCase()}</h2>
            <div className="topic-grid">
              {items.map((topic) => {
                const p = byTopic[topic.slug]
                return (
                  <Link key={topic.slug} to={`/learn/${topic.slug}`} className="topic-card">
                    <div className="topic-card-head">
                      <h3>{topic.title}</h3>
                      {p?.completionPercentage === 100 && <span className="solved-chip">✓</span>}
                    </div>
                    <p className="muted">{topic.summary}</p>
                    <div className="topic-card-foot">
                      <span className="muted small">{topic.lessonCount} lessons · {topic.questionCount} quiz questions</span>
                      {p?.bestQuizPercentage != null && <span className="small">Best quiz {p.bestQuizPercentage}%</span>}
                    </div>
                    <div className="bar"><div className="bar-fill fill-accent" style={{ width: `${p?.completionPercentage ?? 0}%` }} /></div>
                  </Link>
                )
              })}
            </div>
          </section>
        )
      })}
    </div>
  )
}
