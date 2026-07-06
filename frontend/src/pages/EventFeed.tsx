import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api, Disease, Event, Paged } from '../services/api'
import Paginator from '../components/Paginator'

export default function EventFeed() {
  const [data, setData] = useState<Paged<Event> | null>(null)
  const [diseases, setDiseases] = useState<Disease[]>([])
  const [page, setPage] = useState(0)
  const [filter, setFilter] = useState<string>('ALL')
  const [loading, setLoading] = useState(true)

  // Full disease list for the filter chips — independent of which events are on
  // the current page, so every tracked disease is always selectable.
  useEffect(() => {
    api.getDiseases().then(setDiseases).catch(() => {})
  }, [])

  useEffect(() => {
    setLoading(true)
    api.getAllEvents(page, 50, filter).then(setData).finally(() => setLoading(false))
  }, [page, filter])

  const selectFilter = (slug: string) => {
    setFilter(slug)
    setPage(0)
  }

  const chips = [{ name: 'All', slug: 'ALL' }, ...diseases]
  const events = data?.content ?? []

  return (
    <div className="container page">
      <h1>Event Feed</h1>
      <p className="subtitle">All updates across tracked diseases, newest first.</p>

      <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '1.5rem', flexWrap: 'wrap' }}>
        {chips.map(d => (
          <button
            key={d.slug}
            onClick={() => selectFilter(d.slug)}
            style={{
              background: filter === d.slug ? 'var(--accent)' : 'var(--surface)',
              border: '1px solid var(--border)',
              color: 'var(--text)',
              padding: '4px 14px',
              borderRadius: '999px',
              cursor: 'pointer',
              fontSize: '0.82rem',
            }}
          >
            {d.name}
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
