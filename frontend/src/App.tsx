import { Routes, Route } from 'react-router-dom'
import Nav from './components/Nav'
import Dashboard from './pages/Dashboard'
import DiseaseDetail from './pages/DiseaseDetail'
import EventFeed from './pages/EventFeed'

export default function App() {
  return (
    <>
      <Nav />
      <Routes>
        <Route path="/" element={<Dashboard />} />
        <Route path="/disease/:slug" element={<DiseaseDetail />} />
        <Route path="/events" element={<EventFeed />} />
      </Routes>
    </>
  )
}
