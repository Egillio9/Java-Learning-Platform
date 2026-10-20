export function Spinner({ label = 'Loading' }) {
  return (
    <div className="spinner-wrap" role="status">
      <span className="spinner" aria-hidden="true" />
      <span className="sr-only">{label}</span>
    </div>
  )
}

export function PageLoader() {
  return (
    <div className="page-loader">
      <Spinner />
    </div>
  )
}

export function ErrorState({ error, onRetry }) {
  return (
    <div className="empty-state">
      <h3>Something went wrong</h3>
      <p>{error?.message ?? 'Unknown error'}</p>
      {onRetry && (
        <button className="btn btn-secondary" onClick={onRetry}>
          Try again
        </button>
      )}
    </div>
  )
}

export function EmptyState({ title, children }) {
  return (
    <div className="empty-state">
      <h3>{title}</h3>
      {children}
    </div>
  )
}
