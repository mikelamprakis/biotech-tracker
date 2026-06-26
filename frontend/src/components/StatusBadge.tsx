interface Props { status: string | null }

export default function StatusBadge({ status }: Props) {
  if (!status) return null
  const s = status.toUpperCase()
  if (s.includes('RECRUIT')) return <span className="badge badge-green">{status}</span>
  if (s.includes('COMPLET')) return <span className="badge badge-blue">{status}</span>
  if (s.includes('TERMINAT') || s.includes('WITHDRAW')) return <span className="badge badge-red">{status}</span>
  return <span className="badge badge-gray">{status}</span>
}
