import { Link } from 'react-router-dom'

export default function NotFound() {
  return (
    <div className="page not-found">
      <h1>404</h1>
      <p className="muted">That page does not exist. Maybe it threw an ArrayIndexOutOfBoundsException.</p>
      <Link to="/" className="btn btn-primary">Back home</Link>
    </div>
  )
}
