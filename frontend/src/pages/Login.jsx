import { useState } from 'react'
import { Navigate, useNavigate, useSearchParams } from 'react-router-dom'
import Logo from '../components/Logo'
import { useAuth } from '../context/AuthContext'
import { authApi } from '../api/endpoints'

const NAME_PATTERN = /^[\p{L}0-9 ._-]+$/u

export default function Login() {
  const { user, providers, signInAsGuest, loading } = useAuth()
  const [params] = useSearchParams()
  const navigate = useNavigate()
  const [name, setName] = useState('')
  const [error, setError] = useState(params.get('error') === 'google' ? 'Google sign-in failed. Please try again.' : '')
  const [submitting, setSubmitting] = useState(false)
  const next = safeNext(params.get('next'))

  if (!loading && user) return <Navigate to={next} replace />

  const handleSubmit = async (event) => {
    event.preventDefault()
    const trimmed = name.trim()
    if (trimmed.length < 2) return setError('Please enter at least 2 characters.')
    if (!NAME_PATTERN.test(trimmed)) return setError('Use letters, numbers, spaces, dots, dashes or underscores.')
    setSubmitting(true)
    setError('')
    try {
      await signInAsGuest(trimmed)
      navigate(next, { replace: true })
    } catch (err) {
      setError(err.message)
      setSubmitting(false)
    }
  }

  return (
    <div className="auth-page">
      <div className="auth-card">
        <Logo />
        <h1>Welcome to JavaLearn</h1>
        <p className="muted">Sign in to save your progress, submit solutions and join the leaderboard.</p>

        {providers.google && (
          <>
            <a className="btn btn-google" href={authApi.googleUrl}>
              <svg viewBox="0 0 48 48" width="18" height="18" aria-hidden="true">
                <path fill="#FFC107" d="M43.6 20.5H42V20H24v8h11.3C33.7 32.7 29.2 36 24 36c-6.6 0-12-5.4-12-12s5.4-12 12-12c3.1 0 5.8 1.2 7.9 3.1l5.7-5.7C34 6.1 29.3 4 24 4 12.9 4 4 12.9 4 24s8.9 20 20 20 20-8.9 20-20c0-1.3-.1-2.4-.4-3.5z" />
                <path fill="#FF3D00" d="M6.3 14.7l6.6 4.8C14.7 15.1 19 12 24 12c3.1 0 5.8 1.2 7.9 3.1l5.7-5.7C34 6.1 29.3 4 24 4 16.3 4 9.7 8.3 6.3 14.7z" />
                <path fill="#4CAF50" d="M24 44c5.2 0 9.9-2 13.4-5.2l-6.2-5.2C29.2 35.1 26.7 36 24 36c-5.2 0-9.6-3.3-11.3-7.9l-6.5 5C9.5 39.6 16.2 44 24 44z" />
                <path fill="#1976D2" d="M43.6 20.5H42V20H24v8h11.3c-.8 2.2-2.2 4.2-4.1 5.6l6.2 5.2C37 39.2 44 34 44 24c0-1.3-.1-2.4-.4-3.5z" />
              </svg>
              Continue with Google
            </a>
            <div className="divider"><span>or continue as a guest</span></div>
          </>
        )}

        <form onSubmit={handleSubmit} className="auth-form" noValidate>
          <label htmlFor="name" className="field-label">What should we call you?</label>
          <input id="name" className="input" value={name} onChange={(e) => setName(e.target.value)}
                 placeholder="e.g. Himanshu" maxLength={30} autoComplete="nickname" autoFocus />
          {error && <p className="form-error" role="alert">{error}</p>}
          <button className="btn btn-primary btn-block" disabled={submitting}>
            {submitting ? 'Signing in…' : 'Continue'}
          </button>
        </form>
        <p className="fine-print">
          Guest progress is linked to your name. Use the same name next time to pick up where you left off.
        </p>
      </div>
    </div>
  )
}

/** Only allow in-app redirects, never an external URL. */
function safeNext(next) {
  return next && next.startsWith('/') && !next.startsWith('//') ? next : '/app'
}
