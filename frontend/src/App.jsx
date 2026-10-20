import { Route, Routes } from 'react-router-dom'
import Layout from './components/Layout'
import ProtectedRoute from './components/ProtectedRoute'
import Dashboard from './pages/Dashboard'
import Landing from './pages/Landing'
import Leaderboard from './pages/Leaderboard'
import Learn from './pages/Learn'
import Login from './pages/Login'
import NotFound from './pages/NotFound'
import ProblemWorkspace from './pages/ProblemWorkspace'
import Problems from './pages/Problems'
import TopicPage from './pages/TopicPage'

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Landing />} />
      <Route path="/login" element={<Login />} />
      <Route path="/problems/:slug" element={<ProblemWorkspace />} />
      <Route element={<Layout />}>
        <Route path="/app" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
        <Route path="/problems" element={<Problems />} />
        <Route path="/learn" element={<Learn />} />
        <Route path="/learn/:slug" element={<TopicPage />} />
        <Route path="/leaderboard" element={<Leaderboard />} />
        <Route path="*" element={<NotFound />} />
      </Route>
    </Routes>
  )
}
