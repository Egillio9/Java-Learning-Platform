import { useMemo, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { DifficultyBadge, StatusIcon, Tag } from '../components/Badges'
import { ErrorState, PageLoader } from '../components/Feedback'
import { problemsApi } from '../api/endpoints'
import { useFetch } from '../hooks/useFetch'

const DIFFICULTIES = ['ALL', 'EASY', 'MEDIUM', 'HARD']
const STATUSES = [
  ['ALL', 'All statuses'],
  ['TODO', 'To do'],
  ['ATTEMPTED', 'Attempted'],
  ['SOLVED', 'Solved'],
]

export default function Problems() {
  const { data, error, loading, reload } = useFetch(() => problemsApi.list(), [])
  const navigate = useNavigate()
  const [query, setQuery] = useState('')
  const [difficulty, setDifficulty] = useState('ALL')
  const [status, setStatus] = useState('ALL')
  const [tag, setTag] = useState('ALL')

  const tags = useMemo(() => [...new Set((data ?? []).flatMap((p) => p.tags))].sort(), [data])
  const filtered = useMemo(() => {
    const q = query.trim().toLowerCase()
    return (data ?? []).filter((p) =>
      (difficulty === 'ALL' || p.difficulty === difficulty) &&
      (status === 'ALL' || p.status === status) &&
      (tag === 'ALL' || p.tags.includes(tag)) &&
      (!q || p.title.toLowerCase().includes(q) || p.tags.some((t) => t.toLowerCase().includes(q))))
  }, [data, query, difficulty, status, tag])

  if (loading) return <PageLoader />
  if (error) return <ErrorState error={error} onRetry={reload} />

  const solved = data.filter((p) => p.status === 'SOLVED').length
  const pickRandom = () => {
    const unsolved = filtered.filter((p) => p.status !== 'SOLVED')
    const pool = unsolved.length ? unsolved : filtered
    if (pool.length) navigate(`/problems/${pool[Math.floor(Math.random() * pool.length)].slug}`)
  }

  return (
    <div className="page problems-page">
      <header className="page-header">
        <div>
          <h1>Problems</h1>
          <p className="muted">Topic-wise DSA interview questions. {solved} of {data.length} solved.</p>
        </div>
        <button className="btn btn-secondary" onClick={pickRandom}>🎲 Pick one for me</button>
      </header>

      <div className="filters">
        <input className="input search" placeholder="Search by title or topic…" value={query}
               onChange={(e) => setQuery(e.target.value)} aria-label="Search problems" />
        <div className="segmented" role="group" aria-label="Difficulty">
          {DIFFICULTIES.map((d) => (
            <button key={d} className={difficulty === d ? 'active' : ''} onClick={() => setDifficulty(d)}>
              {d === 'ALL' ? 'All' : d[0] + d.slice(1).toLowerCase()}
            </button>
          ))}
        </div>
        <select className="input select" value={status} onChange={(e) => setStatus(e.target.value)} aria-label="Status">
          {STATUSES.map(([value, label]) => <option key={value} value={value}>{label}</option>)}
        </select>
        <select className="input select" value={tag} onChange={(e) => setTag(e.target.value)} aria-label="Topic">
          <option value="ALL">All topics</option>
          {tags.map((t) => <option key={t} value={t}>{t}</option>)}
        </select>
      </div>

      <div className="table-wrap">
        <table className="problem-table">
          <thead>
            <tr>
              <th className="col-status" aria-label="Status" />
              <th>Title</th>
              <th className="col-tags">Topics</th>
              <th className="col-num">Acceptance</th>
              <th className="col-diff">Difficulty</th>
            </tr>
          </thead>
          <tbody>
            {filtered.map((problem) => (
              <tr key={problem.slug} onClick={() => navigate(`/problems/${problem.slug}`)}>
                <td className="col-status"><StatusIcon status={problem.status} /></td>
                <td>
                  <Link to={`/problems/${problem.slug}`} className="problem-link" onClick={(e) => e.stopPropagation()}>
                    {data.indexOf(problem) + 1}. {problem.title}
                  </Link>
                </td>
                <td className="col-tags">{problem.tags.slice(0, 3).map((t) => <Tag key={t}>{t}</Tag>)}</td>
                <td className="col-num muted">{problem.submissions ? `${problem.acceptanceRate}%` : '—'}</td>
                <td className="col-diff"><DifficultyBadge difficulty={problem.difficulty} plain /></td>
              </tr>
            ))}
          </tbody>
        </table>
        {filtered.length === 0 && <p className="muted center pad">No problems match these filters.</p>}
      </div>
    </div>
  )
}
