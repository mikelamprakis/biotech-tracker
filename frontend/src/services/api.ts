const BASE = import.meta.env.PROD
  ? 'https://biotech-tracker-kza2.onrender.com/api'
  : '/api'

export interface Disease {
  id: number
  name: string
  slug: string
  trialCount: number
  publicationCount: number
}

export interface Trial {
  id: number
  nctId: string
  title: string
  phase: string | null
  status: string | null
  sponsor: string | null
  startDate: string | null
  lastUpdated: string | null
  sourceUrl: string | null
  diseaseName: string
  companyName: string | null
}

export interface Publication {
  id: number
  pubmedId: string | null
  title: string
  abstrakt: string | null
  authors: string | null
  journal: string | null
  publishedDate: string | null
  sourceUrl: string | null
  diseaseName: string
}

export interface Event {
  id: number
  eventType: string
  summary: string
  impactScore: number
  refType: string | null
  refId: number | null
  createdAt: string
  diseaseName: string
  diseaseSlug: string
}

export interface Paged<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
}

async function get<T>(path: string): Promise<T> {
  const res = await fetch(BASE + path)
  if (!res.ok) throw new Error(`HTTP ${res.status}`)
  return res.json()
}

export const api = {
  getDiseases: () => get<Disease[]>('/diseases'),
  getDisease: (slug: string) => get<Disease>(`/diseases/${slug}`),
  getTrials: (slug: string, page = 0, size = 20) =>
    get<Paged<Trial>>(`/diseases/${slug}/trials?page=${page}&size=${size}`),
  getPapers: (slug: string, page = 0, size = 20) =>
    get<Paged<Publication>>(`/diseases/${slug}/papers?page=${page}&size=${size}`),
  getDiseaseEvents: (slug: string, page = 0, size = 20) =>
    get<Paged<Event>>(`/diseases/${slug}/events?page=${page}&size=${size}`),
  getAllEvents: (page = 0, size = 50) =>
    get<Paged<Event>>(`/events?page=${page}&size=${size}`),
}
