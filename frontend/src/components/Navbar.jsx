import { useEffect, useRef, useState } from 'react'
import { Link, NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import Avatar from './Avatar'
import Logo from './Logo'

const LINKS = [
  { to: '/app', label: 'Dashboard', auth: true },
  { to: '/problems', label: 'Problems' },
  { to: '/learn', label: 'Learn' },
  { to: '/leaderboard', label: 'Leaderboard' },
]

export default function Navbar({ compact = false }) {
  const { user, signOut } = useAuth()
  const navigate = useNavigate()
  const [menuOpen, setMenuOpen] = useState(false)
  const menuRef = useRef(null)

  useEffect(() => {
    const close = (event) => menuRef.current && !menuRef.current.contains(event.target) && setMenuOpen(false)
    document.addEventListener('mousedown', close)
    return () => document.removeEventListener('mousedown', close)
  }, [])

  const handleSignOut = async () => {
    setMenuOpen(false)
    await signOut()
    navigate('/')
  }

  return (
    <header className={`navbar${compact ? ' navbar-compact' : ''}`}>
      <div className="navbar-inner">
        <Logo to={user ? '/app' : '/'} />
        <nav className="nav-links" aria-label="Main">
          {LINKS.filter((link) => !link.auth || user).map((link) => (
            <NavLink key={link.to} to={link.to} className={({ isActive }) => `nav-link${isActive ? ' active' : ''}`}>
              {link.label}
            </NavLink>
          ))}
        </nav>
        <div className="nav-right">
          {user ? (
            <div className="user-menu" ref={menuRef}>
              <button className="user-button" onClick={() => setMenuOpen((open) => !open)} aria-expanded={menuOpen}>
                <Avatar name={user.displayName} src={user.avatarUrl} size={30} />
                <span className="user-name">{user.displayName}</span>
              </button>
              {menuOpen && (
                <div className="dropdown" role="menu">
                  <div className="dropdown-header">
                    <strong>{user.displayName}</strong>
                    <span>{user.provider === 'GOOGLE' ? 'Google account' : 'Guest account'}</span>
                  </div>
                  <Link to="/app" className="dropdown-item" onClick={() => setMenuOpen(false)}>Dashboard</Link>
                  <Link to="/leaderboard" className="dropdown-item" onClick={() => setMenuOpen(false)}>Leaderboard</Link>
                  <button className="dropdown-item danger" onClick={handleSignOut}>Sign out</button>
                </div>
              )}
            </div>
          ) : (
            <>
              <Link to="/login" className="btn btn-ghost btn-sm">Sign in</Link>
              <Link to="/login" className="btn btn-primary btn-sm">Get started</Link>
            </>
          )}
        </div>
      </div>
    </header>
  )
}
