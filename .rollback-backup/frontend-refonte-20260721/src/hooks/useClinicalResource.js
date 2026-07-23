import { useEffect, useState } from 'react'

export function useClinicalResource(loader, dependencies = []) {
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    let active = true
    setLoading(true)
    setError('')

    loader()
      .then((result) => {
        if (active) setData(result)
      })
      .catch((exception) => {
        if (active) setError(exception.message || 'Une erreur inattendue est survenue.')
      })
      .finally(() => {
        if (active) setLoading(false)
      })

    return () => {
      active = false
    }
    // The caller owns the dependency list, like useEffect.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, dependencies)

  return { data, loading, error }
}
