import { Link } from 'react-router-dom'
import Avatar from '../components/Avatar'
import { DifficultyBadge, VerdictBadge } from '../components/Badges'
import { ErrorState, PageLoader } from '../components/Feedback'
import ProgressRing from '../components/ProgressRing'
import { learnApi, statsApi } from '../api/endpoints'
import { useFetch } from '../hooks/useFetch'
import { timeAgo } from '../utils/format'

export default function Dashboard() {
  const dashboard = useFetch(() => statsApi.dashboard(), [])
  const progress = useFetch(() => learnApi.progress(), [])

  if (dashboard.loading) return <PageLoader />
  if (dashboard.error) return <ErrorState error={dashboard.error} onRetry={dashboard.reload} />

  const d = dashboard.data
  const solvedTotal = d.solved.easy + d.solved.medium + d.solved.hard
  const availableTotal = d.available.easy + d.available.medium + d.available.hard
  const lessonPct = d.totalLessons ? Math.round((d.lessonsCompleted / d.totalLessons) * 100) : 0

  return (
    <div className="page dashboard">
      <header className="dash-header">
        <Avatar name={d.displayName} src={d.avatarUrl} size={56} />
        <div>
          <h1>Welcome back, {d.displayName.split(' ')[0]} 👋</h1>
          <p className="muted">
            {d.streakDays > 0 ? `🔥 ${d.streakDays}-day streak. Keep it going!` : 'Solve a problem today to start a streak.'}
            {d.rank && <> · Rank <strong>#{d.rank}</strong> on the leaderboard</>}
          </p>
        </div>
        {d.nextProblem && (
          <Link to={`/problems/${d.nextProblem.slug}`} className="btn btn-primary dash-cta">
            Next up: {d.nextProblem.title} →
          </Link>
        )}
      </header>

      <div className="dash-grid">
        <section className="card solved-card">
          <h2 className="card-title">Problems solved</h2>
          <div className="solved-body">
            <ProgressRing total={availableTotal} segments={[
              { value: d.solved.easy, color: 'var(--easy)' },
              { value: d.solved.medium, color: 'var(--medium)' },
              { value: d.solved.hard, color: 'var(--hard)' },
            ]}>
              <strong>{solvedTotal}</strong>
              <span>/ {availableTotal}</span>
            </ProgressRing>
            <ul className="difficulty-bars">
              {[['Easy', 'easy'], ['Medium', 'medium'], ['Hard', 'hard']].map(([label, key]) => (
                <li key={key}>
                  <div className="bar-head">
                    <span className={`difficulty difficulty-${key} plain`}>{label}</span>
                    <span>{d.solved[key]} <span className="muted">/ {d.available[key]}</span></span>
                  </div>
                  <div className="bar"><div className={`bar-fill fill-${key}`}
                       style={{ width: `${d.available[key] ? (d.solved[key] / d.available[key]) * 100 : 0}%` }} /></div>
                </li>
              ))}
            </ul>
          </div>
        </section>

        <section className="card stat-card">
          <span className="stat-label">Acceptance rate</span>
          <span className="stat-value">{d.acceptanceRate}%</span>
          <span className="muted small">{d.acceptedSubmissions} accepted of {d.submissions} submissions</span>
        </section>

        <section className="card stat-card">
          <span className="stat-label">Course progress</span>
          <span className="stat-value">{lessonPct}%</span>
          <div className="bar"><div className="bar-fill fill-accent" style={{ width: `${lessonPct}%` }} /></div>
          <span className="muted small">{d.lessonsCompleted} of {d.totalLessons} lessons completed</span>
        </section>

        <section className="card stat-card">
          <span className="stat-label">Current streak</span>
          <span className="stat-value">{d.streakDays} <small>day{d.streakDays === 1 ? '' : 's'}</small></span>
          <span className="muted small">Member since {new Date(d.joinedAt).toLocaleDateString()}</span>
        </section>

        <section className="card stat-card">
          <span className="stat-label">Leaderboard</span>
          <span className="stat-value">{d.rank ? `#${d.rank}` : '—'}</span>
          <Link to="/leaderboard" className="link small">
            {d.rank ? 'See who is ahead of you' : 'Solve a problem to get ranked'}
          </Link>
        </section>

        <section className="card recent-card">
          <div className="card-head">
            <h2 className="card-title">Recent submissions</h2>
            <Link to="/problems" className="link">All problems</Link>
          </div>
          {d.recent.length === 0 ? (
            <p className="muted">No submissions yet. <Link to="/problems/fizz-buzz" className="link">Try Fizz Buzz</Link> to get started.</p>
          ) : (
            <ul className="recent-list">
              {d.recent.map((item) => (
                <li key={item.id}>
                  <Link to={`/problems/${item.slug}`} className="recent-title">{item.title}</Link>
                  <DifficultyBadge difficulty={item.difficulty} plain />
                  <VerdictBadge verdict={item.verdict} />
                  <span className="muted small">{timeAgo(item.createdAt)}</span>
                </li>
              ))}
            </ul>
          )}
        </section>

        <section className="card path-card">
          <div className="card-head">
            <h2 className="card-title">Java learning path</h2>
            <Link to="/learn" className="link">Open course</Link>
          </div>
          {progress.data ? (
            <ul className="path-list">
              {progress.data.topics.map((topic) => (
                <li key={topic.slug}>
                  <Link to={`/learn/${topic.slug}`}>{topic.title}</Link>
                  <div className="bar"><div className="bar-fill fill-accent" style={{ width: `${topic.completionPercentage}%` }} /></div>
                  <span className="muted small">
                    {topic.bestQuizPercentage != null ? `Quiz ${topic.bestQuizPercentage}%` : `${topic.lessonsCompleted}/${topic.totalLessons}`}
                  </span>
                </li>
              ))}
            </ul>
          ) : (
            <p className="muted">Loading…</p>
          )}
        </section>
      </div>
    </div>
  )
}
