interface Props {
  page: number
  totalPages: number
  totalElements: number
  onPrev: () => void
  onNext: () => void
}

export default function Paginator({ page, totalPages, totalElements, onPrev, onNext }: Props) {
  return (
    <div className="pagination">
      <span>{totalElements} results</span>
      <button onClick={onPrev} disabled={page === 0}>← Prev</button>
      <span>Page {page + 1} / {totalPages}</span>
      <button onClick={onNext} disabled={page >= totalPages - 1}>Next →</button>
    </div>
  )
}
