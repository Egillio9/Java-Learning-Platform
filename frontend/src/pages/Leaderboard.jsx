import Avatar from '../components/Avatar'
import { EmptyState, ErrorState, PageLoader } from '../components/Feedback'
import { statsApi } from '../api/endpoints'
import { useAuth } from '../context/AuthContext'
import { useFetch } from '../hooks/useFetch'

const MEDALS = { 1: '🥇', 2: '🥈', 3: '🥉' }

export default function Leaderboard() {
  const { user } = useAuth()
  const { data, error, loading, reload } = useFetch(() => statsApi.leaderboard(), [])

  if (loading) return <PageLoader />
  if (error) return <ErrorState error={error} onRetry={reload} />

  return (
    <div className="page leaderboard-page">
      <header className="page-header">
        <div>
          <h1>Leaderboard</h1>
          <p className="muted">10 points per problem solved and 2 per lesson completed.</p>
        </div>
      </header>

      {data.length === 0 ? (
        <EmptyState title="No one on the board yet">
          <p className="muted">Solve a problem to claim the top spot.</p>
        </EmptyState>
      ) : (
        <div className="table-wrap">
          <table className="leader-table">
            <thead>
              <tr><th className="col-rank">Rank</th><th>Learner</th><th className="col-num">Solved</th><th className="col-num">Lessons</th><th className="col-num">Points</th></tr>
            </thead>
            <tbody>
              {data.map((entry) => (
                <tr key={entry.learnerId} className={entry.learnerId === user?.id ? 'is-you' : ''}>
                  <td className="col-rank">{MEDALS[entry.rank] ?? `#${entry.rank}`}</td>
                  <td>
                    <span className="leader-name">
                      <Avatar name={entry.displayName} src={entry.avatarUrl} size={28} />
                      {entry.displayName}
                      {entry.learnerId === user?.id && <span className="you-chip">You</span>}
                    </span>
                  </td>
                  <td className="col-num">{entry.solved}</td>
                  <td className="col-num">{entry.lessonsCompleted}</td>
                  <td className="col-num"><strong>{entry.score}</strong></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
