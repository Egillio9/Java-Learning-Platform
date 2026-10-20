import { Link } from 'react-router-dom'

export default function Logo({ to = '/' }) {
  return (
    <Link to={to} className="logo" aria-label="JavaLearn home">
      <span className="logo-mark" aria-hidden="true">
        <svg viewBox="0 0 64 64" width="26" height="26">
          <rect width="64" height="64" rx="14" fill="var(--accent)" />
          <path d="M22 40c0 4 4 6 10 6s10-2 10-6M20 34h22a6 6 0 0 1 0 12" fill="none" stroke="#0f1117"
                strokeWidth="4" strokeLinecap="round" />
          <path d="M28 14c-3 4 3 6 0 10M36 12c-3 4 3 6 0 10" fill="none" stroke="#0f1117" strokeWidth="3.5"
                strokeLinecap="round" />
        </svg>
      </span>
      <span className="logo-text">
        Java<strong>Learn</strong>
      </span>
    </Link>
  )
}
