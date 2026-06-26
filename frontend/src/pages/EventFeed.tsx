import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api, Event, Paged } from '../services/api'
import Paginator from '../components/Paginator'

export default function EventFeed() {
  const [data, setData] = useState<Paged<Event> | null>(null)
  const [page, setPage] = useState(0)
  const [filter, setFilter] = useState<string>('ALL')
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    setLoading(true)
    api.getAllEvents(page, 50).then(setData).finally(() => setLoading(false))
  }, [page])

  const diseases = data
    ? ['ALL', ...Array.from(new Set(data.content.map(e => e.diseaseName)))]
    : ['ALL']

  const events = data?.content.filter(e => filter === 'ALL' || e.diseaseName === filter) ?? []

  return (
    <div className="container page">
      <h1>Event Feed</h1>
      <p className="subtitle">All updates across tracked diseases, newest first.</p>

      <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '1.5rem', flexWrap: 'wrap' }}>
        {diseases.map(d => (
          <button
            key={d}
            onClick={() => setFilter(d)}
            style={{
              background: filter === d ? 'var(--accent)' : 'var(--surface)',
              border: '1px solid var(--border)',
              color: 'var(--text)',
              padding: '4px 14px',
              borderRadius: '999px',
              cursor: 'pointer',
              fontSize: '0.82rem',
            }}
          >
            {d}
          </button>
        ))}
      </div>

      {loading && <div className="loading">Loading events...</div>}

      {!loading && (
        <div className="event-list">
          {events.length === 0 && (
            <div style={{ color: 'var(--muted)' }}>No events yet — ingestion runs every 8 hours.</div>
          )}
          {events.map(e => (
            <div key={e.id} className={`event-item ${e.impactScore >= 7 ? 'impact-high' : e.impactScore >= 5 ? 'impact-med' : ''}`}>
              <div className="event-summary">{e.summary}</div>
              <div className="event-meta">
                <Link to={`/disease/${e.diseaseSlug}`} style={{ color: 'var(--accent-light)' }}>{e.diseaseName}</Link>
                <span>{e.eventType.replace(/_/g, ' ')}</span>
                <span>Impact {e.impactScore}/10</span>
                <span>{new Date(e.createdAt).toLocaleString()}</span>
              </div>
            </div>
          ))}
        </div>
      )}

      {data && data.totalPages > 1 && (
        <Paginator page={page} totalPages={data.totalPages} totalElements={data.totalElements}
          onPrev={() => setPage(p => p - 1)} onNext={() => setPage(p => p + 1)} />
      )}
    </div>
  )
}
