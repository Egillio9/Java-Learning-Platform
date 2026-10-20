import { useCallback, useEffect, useState } from 'react'

/** Runs an async loader on mount (and when deps change) and exposes data / error / reload. */
export function useFetch(loader, deps = []) {
  const [state, setState] = useState({ data: null, error: null, loading: true })

  // eslint-disable-next-line react-hooks/exhaustive-deps
  const load = useCallback(loader, deps)

  const reload = useCallback(() => {
    let active = true
    setState((previous) => ({ ...previous, loading: true, error: null }))
    load()
      .then((data) => active && setState({ data, error: null, loading: false }))
      .catch((error) => active && setState({ data: null, error, loading: false }))
    return () => {
      active = false
    }
  }, [load])

  useEffect(() => reload(), [reload])

  return { ...state, reload, setData: (data) => setState((s) => ({ ...s, data })) }
}
