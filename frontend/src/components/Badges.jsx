import { DIFFICULTY_LABELS, VERDICT_LABELS } from '../utils/format'

export function DifficultyBadge({ difficulty, plain = false }) {
  const className = `difficulty difficulty-${difficulty?.toLowerCase()}${plain ? ' plain' : ''}`
  return <span className={className}>{DIFFICULTY_LABELS[difficulty] ?? difficulty}</span>
}

export function VerdictBadge({ verdict }) {
  const ok = verdict === 'ACCEPTED'
  return <span className={`verdict ${ok ? 'verdict-ok' : 'verdict-bad'}`}>{VERDICT_LABELS[verdict] ?? verdict}</span>
}

export function StatusIcon({ status }) {
  if (status === 'SOLVED') {
    return (
      <span className="status-icon solved" title="Solved" aria-label="Solved">
        <svg viewBox="0 0 24 24" width="18" height="18"><path d="M5 12.5l4.5 4.5L19 7.5" fill="none" stroke="currentColor" strokeWidth="2.4" strokeLinecap="round" strokeLinejoin="round" /></svg>
      </span>
    )
  }
  if (status === 'ATTEMPTED') {
    return (
      <span className="status-icon attempted" title="Attempted" aria-label="Attempted">
        <svg viewBox="0 0 24 24" width="18" height="18"><circle cx="12" cy="12" r="7" fill="none" stroke="currentColor" strokeWidth="2.2" strokeDasharray="4 3" /></svg>
      </span>
    )
  }
  return <span className="status-icon" aria-hidden="true" />
}

export function Tag({ children }) {
  return <span className="tag">{children}</span>
}
