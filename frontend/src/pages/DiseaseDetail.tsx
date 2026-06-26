import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { api, Disease, Trial, Publication, Event, Paged } from '../services/api'
import PhaseBadge from '../components/PhaseBadge'
import StatusBadge from '../components/StatusBadge'
import Paginator from '../components/Paginator'

type Tab = 'trials' | 'papers' | 'events'

export default function DiseaseDetail() {
  const { slug } = useParams<{ slug: string }>()
  const [disease, setDisease] = useState<Disease | null>(null)
  const [tab, setTab] = useState<Tab>('trials')

  const [trials, setTrials] = useState<Paged<Trial> | null>(null)
  const [trialsPage, setTrialsPage] = useState(0)

  const [papers, setPapers] = useState<Paged<Publication> | null>(null)
  const [papersPage, setPapersPage] = useState(0)

  const [events, setEvents] = useState<Paged<Event> | null>(null)
  const [eventsPage, setEventsPage] = useState(0)

  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (!slug) return
    api.getDisease(slug).then(setDisease).catch(() => setError('Disease not found'))
  }, [slug])

  useEffect(() => {
    if (!slug) return
    setLoading(true)
    api.getTrials(slug, trialsPage).then(setTrials).finally(() => setLoading(false))
  }, [slug, trialsPage])

  useEffect(() => {
    if (!slug) return
    setLoading(true)
    api.getPapers(slug, papersPage).then(setPapers).finally(() => setLoading(false))
  }, [slug, papersPage])

  useEffect(() => {
    if (!slug) return
    setLoading(true)
    api.getDiseaseEvents(slug, eventsPage).then(setEvents).finally(() => setLoading(false))
  }, [slug, eventsPage])

  if (error) return <div className="container page"><div className="error-msg">{error}</div></div>

  return (
    <div className="container page">
      <div className="breadcrumb">
        <Link to="/">Dashboard</Link>
        <span>›</span>
        <span>{disease?.name ?? slug}</span>
      </div>

      <h1>{disease?.name ?? '...'}</h1>
      {disease && (
        <p className="subtitle">
          {disease.trialCount} clinical trials · {disease.publicationCount} publications
        </p>
      )}

      <div className="tabs">
        <button className={`tab-btn ${tab === 'trials' ? 'active' : ''}`} onClick={() => setTab('trials')}>
          Clinical Trials {trials ? `(${trials.totalElements})` : ''}
        </button>
        <button className={`tab-btn ${tab === 'papers' ? 'active' : ''}`} onClick={() => setTab('papers')}>
          Publications {papers ? `(${papers.totalElements})` : ''}
        </button>
        <button className={`tab-btn ${tab === 'events' ? 'active' : ''}`} onClick={() => setTab('events')}>
          Event Timeline {events ? `(${events.totalElements})` : ''}
        </button>
      </div>

      {loading && <div className="loading">Loading...</div>}

      {!loading && tab === 'trials' && trials && (
        <>
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Title</th>
                  <th>Phase</th>
                  <th>Status</th>
                  <th>Sponsor</th>
                  <th>Updated</th>
                </tr>
              </thead>
              <tbody>
                {trials.content.length === 0 && (
                  <tr><td colSpan={5} style={{ color: 'var(--muted)' }}>No trials yet — data refreshes every 8 hours.</td></tr>
                )}
                {trials.content.map(t => (
                  <tr key={t.id}>
                    <td>
                      {t.sourceUrl
                        ? <a href={t.sourceUrl} target="_blank" rel="noopener noreferrer">{t.title}</a>
                        : t.title}
                      <div style={{ fontSize: '0.75rem', color: 'var(--muted)', marginTop: 2 }}>{t.nctId}</div>
                    </td>
                    <td><PhaseBadge phase={t.phase} /></td>
                    <td><StatusBadge status={t.status} /></td>
                    <td style={{ fontSize: '0.82rem' }}>{t.sponsor ?? '—'}</td>
                    <td style={{ fontSize: '0.78rem', color: 'var(--muted)', whiteSpace: 'nowrap' }}>
                      {t.lastUpdated ? new Date(t.lastUpdated).toLocaleDateString() : '—'}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          {trials.totalPages > 1 && (
            <Paginator page={trialsPage} totalPages={trials.totalPages} totalElements={trials.totalElements}
              onPrev={() => setTrialsPage(p => p - 1)} onNext={() => setTrialsPage(p => p + 1)} />
          )}
        </>
      )}

      {!loading && tab === 'papers' && papers && (
        <>
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Title</th>
                  <th>Journal</th>
                  <th>Authors</th>
                  <th>Published</th>
                </tr>
              </thead>
              <tbody>
                {papers.content.length === 0 && (
                  <tr><td colSpan={4} style={{ color: 'var(--muted)' }}>No publications yet — data refreshes every 8 hours.</td></tr>
                )}
                {papers.content.map(p => (
                  <tr key={p.id}>
                    <td>
                      {p.sourceUrl
                        ? <a href={p.sourceUrl} target="_blank" rel="noopener noreferrer">{p.title}</a>
                        : p.title}
                    </td>
                    <td style={{ fontSize: '0.82rem' }}>{p.journal ?? '—'}</td>
                    <td style={{ fontSize: '0.78rem', color: 'var(--muted)' }}>
                      {p.authors ? p.authors.split(',').slice(0, 3).join(', ') + (p.authors.split(',').length > 3 ? ' et al.' : '') : '—'}
                    </td>
                    <td style={{ fontSize: '0.78rem', color: 'var(--muted)', whiteSpace: 'nowrap' }}>
                      {p.publishedDate ?? '—'}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          {papers.totalPages > 1 && (
            <Paginator page={papersPage} totalPages={papers.totalPages} totalElements={papers.totalElements}
              onPrev={() => setPapersPage(p => p - 1)} onNext={() => setPapersPage(p => p + 1)} />
          )}
        </>
      )}

      {!loading && tab === 'events' && events && (
        <>
          <div className="event-list">
            {events.content.length === 0 && (
              <div style={{ color: 'var(--muted)' }}>No events yet.</div>
            )}
            {events.content.map(e => (
              <div key={e.id} className={`event-item ${e.impactScore >= 7 ? 'impact-high' : e.impactScore >= 5 ? 'impact-med' : ''}`}>
                <div className="event-summary">{e.summary}</div>
                <div className="event-meta">
                  <span>{e.eventType.replace(/_/g, ' ')}</span>
                  <span>Impact {e.impactScore}/10</span>
                  <span>{new Date(e.createdAt).toLocaleString()}</span>
                </div>
              </div>
            ))}
          </div>
          {events.totalPages > 1 && (
            <Paginator page={eventsPage} totalPages={events.totalPages} totalElements={events.totalElements}
              onPrev={() => setEventsPage(p => p - 1)} onNext={() => setEventsPage(p => p + 1)} />
          )}
        </>
      )}
    </div>
  )
}
