interface Props { phase: string | null }

export default function PhaseBadge({ phase }: Props) {
  if (!phase) return <span className="badge badge-gray">Unknown</span>
  const p = phase.toUpperCase()
  if (p.includes('4')) return <span className="badge badge-green">{phase}</span>
  if (p.includes('3')) return <span className="badge badge-blue">{phase}</span>
  if (p.includes('2')) return <span className="badge badge-yellow">{phase}</span>
  if (p.includes('1')) return <span className="badge badge-gray">{phase}</span>
  return <span className="badge badge-gray">{phase}</span>
}
