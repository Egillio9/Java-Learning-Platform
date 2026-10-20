import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react'
import { authApi } from '../api/endpoints'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)
  const [providers, setProviders] = useState({ google: false })

  useEffect(() => {
    Promise.allSettled([authApi.me(), authApi.providers()]).then(([me, available]) => {
      if (me.status === 'fulfilled') setUser(me.value)
      if (available.status === 'fulfilled') setProviders(available.value)
      setLoading(false)
    })
  }, [])

  const signInAsGuest = useCallback(async (name) => {
    const signedIn = await authApi.guest(name)
    setUser(signedIn)
    return signedIn
  }, [])

  const signOut = useCallback(async () => {
    try {
      await authApi.logout()
    } finally {
      setUser(null)
    }
  }, [])

  const value = useMemo(
    () => ({ user, loading, providers, signInAsGuest, signOut }),
    [user, loading, providers, signInAsGuest, signOut],
  )
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) throw new Error('useAuth must be used inside <AuthProvider>')
  return context
}
