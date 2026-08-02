/**
 * Site-wide medical disclaimer.
 *
 * Rendered on every route, not just the dashboard: people arrive on a disease
 * page or the event feed straight from a shared link, so the disclaimer has to
 * travel with the content rather than sit on the home page only.
 */
export default function Footer() {
  return (
    <footer className="site-footer">
      <div className="container">
        <p className="disclaimer">
          <strong>Informational only — not medical advice.</strong> This site aggregates
          publicly available records from ClinicalTrials.gov and PubMed. It does not
          diagnose, recommend or endorse any treatment, and it is not a route to enrol
          in a trial. Always consult a qualified clinician about your own care.
        </p>
        <p className="footer-meta">
          Data from{' '}
          <a href="https://clinicaltrials.gov" target="_blank" rel="noopener noreferrer">ClinicalTrials.gov</a>
          {' '}and{' '}
          <a href="https://pubmed.ncbi.nlm.nih.gov" target="_blank" rel="noopener noreferrer">PubMed</a>,
          refreshed every 8 hours. Listings link back to the original source.
        </p>
      </div>
    </footer>
  )
}
