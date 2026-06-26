import { NavLink } from 'react-router-dom'

export default function Nav() {
  return (
    <nav>
      <div className="container inner">
        <span className="brand">BDIT</span>
        <NavLink to="/" className={({ isActive }) => isActive ? 'active' : ''} end>Dashboard</NavLink>
        <NavLink to="/events" className={({ isActive }) => isActive ? 'active' : ''}>Event Feed</NavLink>
      </div>
    </nav>
  )
}
