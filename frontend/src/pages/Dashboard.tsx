import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api, Disease } from '../services/api'

export default function Dashboard() {
  const [diseases, setDiseases] = useState<Disease[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const navigate = useNavigate()

  useEffect(() => {
    api.getDiseases()
      .then(setDiseases)
      .catch(() => setError('Could not load data. Is the backend running?'))
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <div className="loading">Loading diseases...</div>
  if (error) return <div className="container page"><div className="error-msg">{error}</div></div>

  return (
    <div className="container page">
      <h1>Disease Intelligence Dashboard</h1>
      <p className="subtitle">Real-time tracking of ALS, Alzheimer's Disease, and Pancreatic Cancer research progress.</p>

      <div className="card-grid">
        {diseases.map(d => (
          <div className="card" key={d.id} onClick={() => navigate(`/disease/${d.slug}`)}>
            <h3>{d.name}</h3>
            <p style={{ fontSize: '0.82rem', color: 'var(--muted)' }}>
              Tracking clinical trials, publications, and regulatory events.
            </p>
            <div className="meta">
              <div className="stat">
                <strong>{d.trialCount}</strong>
                Trials tracked
              </div>
              <div className="stat">
                <strong>{d.publicationCount}</strong>
                Publications
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
