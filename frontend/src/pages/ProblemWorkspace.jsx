import { useCallback, useEffect, useRef, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import CodeEditor from '../components/CodeEditor'
import ConsolePanel from '../components/ConsolePanel'
import Markdown from '../components/Markdown'
import Navbar from '../components/Navbar'
import { DifficultyBadge, Tag, VerdictBadge } from '../components/Badges'
import { ErrorState, PageLoader, Spinner } from '../components/Feedback'
import { problemsApi } from '../api/endpoints'
import { useAuth } from '../context/AuthContext'
import { useFetch } from '../hooks/useFetch'
import { timeAgo } from '../utils/format'

const draftKey = (slug) => `javalearn.draft.${slug}`

function readDraft(slug) {
  try {
    return localStorage.getItem(draftKey(slug))
  } catch {
    return null
  }
}

function writeDraft(slug, code) {
  try {
    localStorage.setItem(draftKey(slug), code)
  } catch {
    // Private mode or storage full: drafts are a convenience only.
  }
}

export default function ProblemWorkspace() {
  const { slug } = useParams()
  const { data: problem, error, loading, reload, setData } = useFetch(() => problemsApi.detail(slug), [slug])

  if (loading) return <WorkspaceFrame><PageLoader /></WorkspaceFrame>
  if (error) return <WorkspaceFrame><ErrorState error={error} onRetry={reload} /></WorkspaceFrame>
  return (
    <WorkspaceFrame>
      <Workspace key={problem.slug} problem={problem}
                 onSubmitted={(verdict) => setData({
                   ...problem,
                   submissions: problem.submissions + 1,
                   status: verdict === 'ACCEPTED' ? 'SOLVED' : problem.status === 'SOLVED' ? 'SOLVED' : 'ATTEMPTED',
                 })} />
    </WorkspaceFrame>
  )
}

function WorkspaceFrame({ children }) {
  return (
    <div className="workspace-shell">
      <Navbar compact />
      {children}
    </div>
  )
}

function Workspace({ problem, onSubmitted }) {
  const { user } = useAuth()
  const navigate = useNavigate()
  const [code, setCode] = useState(() => readDraft(problem.slug) ?? problem.starterCode)
  const [inputs, setInputs] = useState(problem.samples.map((s) => s.input))
  const [leftTab, setLeftTab] = useState('description')
  const [consoleTab, setConsoleTab] = useState('testcase')
  const [busy, setBusy] = useState(null)
  const [result, setResult] = useState(null)
  const [submissionsVersion, setSubmissionsVersion] = useState(0)
  const [split, setSplit] = useState(44)
  const dragging = useRef(false)

  useEffect(() => {
    const id = setTimeout(() => writeDraft(problem.slug, code), 400)
    return () => clearTimeout(id)
  }, [code, problem.slug])

  const requireSignIn = useCallback(() => {
    if (user) return true
    navigate(`/login?next=${encodeURIComponent(`/problems/${problem.slug}`)}`)
    return false
  }, [user, navigate, problem.slug])

  const run = useCallback(async () => {
    if (busy || !requireSignIn()) return
    const samples = problem.samples.map((s) => s.input)
    const custom = inputs.length === samples.length && inputs.every((input, i) => input === samples[i]) ? null : inputs
    setBusy('run')
    setConsoleTab('result')
    try {
      setResult({ kind: 'run', report: await problemsApi.run(problem.slug, code, custom) })
    } catch (err) {
      setResult({ error: err.message })
    } finally {
      setBusy(null)
    }
  }, [busy, requireSignIn, problem, inputs, code])

  const submit = useCallback(async () => {
    if (busy || !requireSignIn()) return
    setBusy('submit')
    setConsoleTab('result')
    try {
      const report = await problemsApi.submit(problem.slug, code)
      setResult({ kind: 'submit', report })
      setSubmissionsVersion((v) => v + 1)
      onSubmitted(report.verdict)
    } catch (err) {
      setResult({ error: err.message })
    } finally {
      setBusy(null)
    }
  }, [busy, requireSignIn, problem.slug, code, onSubmitted])

  const resetCode = () => {
    if (window.confirm('Reset to the starter code? Your current code will be lost.')) setCode(problem.starterCode)
  }

  useEffect(() => {
    const move = (event) => {
      if (!dragging.current) return
      const percent = (event.clientX / window.innerWidth) * 100
      setSplit(Math.min(70, Math.max(25, percent)))
    }
    const stop = () => {
      dragging.current = false
      document.body.classList.remove('resizing')
    }
    window.addEventListener('mousemove', move)
    window.addEventListener('mouseup', stop)
    return () => {
      window.removeEventListener('mousemove', move)
      window.removeEventListener('mouseup', stop)
    }
  }, [])

  return (
    <div className="workspace" style={{ '--split': `${split}%` }}>
      <section className="pane pane-left">
        <div className="pane-tabs" role="tablist">
          {[['description', 'Description'], ['submissions', 'Submissions'], ['solution', 'Solution']].map(([id, label]) => (
            <button key={id} role="tab" aria-selected={leftTab === id}
                    className={`pane-tab${leftTab === id ? ' active' : ''}`} onClick={() => setLeftTab(id)}>
              {label}
            </button>
          ))}
        </div>
        <div className="pane-scroll">
          {leftTab === 'description' && <Description problem={problem} />}
          {leftTab === 'submissions' && (
            user ? <SubmissionList slug={problem.slug} version={submissionsVersion} onLoad={(c) => setCode(c)} />
              : <SignInPrompt slug={problem.slug} what="see your submissions" />
          )}
          {leftTab === 'solution' && (
            user ? <SolutionView slug={problem.slug} solved={problem.status === 'SOLVED'} />
              : <SignInPrompt slug={problem.slug} what="unlock the solution" />
          )}
        </div>
      </section>

      <div className="splitter" role="separator" aria-orientation="vertical"
           onMouseDown={() => { dragging.current = true; document.body.classList.add('resizing') }} />

      <section className="pane pane-right">
        <div className="editor-toolbar">
          <span className="lang-chip">Java 17</span>
          <div className="toolbar-actions">
            <button className="icon-btn" onClick={resetCode} title="Reset to starter code">↺ Reset</button>
            <button className="btn btn-secondary btn-sm" onClick={run} disabled={!!busy} title="Ctrl + Enter">
              {busy === 'run' ? 'Running…' : '▶ Run'}
            </button>
            <button className="btn btn-success btn-sm" onClick={submit} disabled={!!busy} title="Ctrl + Shift + Enter">
              {busy === 'submit' ? 'Judging…' : 'Submit'}
            </button>
          </div>
        </div>
        <div className="editor-host">
          <CodeEditor value={code} onChange={setCode} onRun={run} onSubmit={submit} />
        </div>
        <ConsolePanel params={problem.params} inputs={inputs} setInputs={setInputs}
                      tab={consoleTab} setTab={setConsoleTab} busy={busy} result={result} />
      </section>
    </div>
  )
}

function Description({ problem }) {
  return (
    <article className="description">
      <div className="problem-nav">
        <Link to="/problems" className="link small">← All problems</Link>
        <span className="problem-nav-arrows">
          {problem.previousSlug && <Link to={`/problems/${problem.previousSlug}`} className="icon-btn" title="Previous problem">‹</Link>}
          {problem.nextSlug && <Link to={`/problems/${problem.nextSlug}`} className="icon-btn" title="Next problem">›</Link>}
        </span>
      </div>
      <h1>{problem.title}</h1>
      <div className="problem-meta">
        <DifficultyBadge difficulty={problem.difficulty} />
        {problem.status === 'SOLVED' && <span className="solved-chip">✓ Solved</span>}
        <span className="muted small">
          {problem.submissions ? `Acceptance ${problem.acceptanceRate}% · ${problem.submissions} submissions` : 'Be the first to submit'}
        </span>
      </div>
      <Markdown source={problem.description} />
      <div className="tag-row">{problem.tags.map((t) => <Tag key={t}>{t}</Tag>)}</div>
    </article>
  )
}

function SubmissionList({ slug, version, onLoad }) {
  // eslint-disable-next-line react-hooks/exhaustive-deps
  const { data, loading, error } = useFetch(() => problemsApi.submissions(slug), [slug, version])
  const [open, setOpen] = useState(null)

  if (loading) return <Spinner />
  if (error) return <p className="console-error">{error.message}</p>
  if (!data.length) return <p className="muted pad">No submissions yet. Press Submit to judge your code.</p>

  return (
    <ul className="submission-list">
      {data.map((s) => (
        <li key={s.id}>
          <button className="submission-row" onClick={() => setOpen(open === s.id ? null : s.id)}>
            <VerdictBadge verdict={s.verdict} />
            <span className="muted small">{s.passed}/{s.total} passed</span>
            <span className="muted small">{s.runtimeMs != null ? `${s.runtimeMs} ms` : '—'}</span>
            <span className="muted small">{timeAgo(s.createdAt)}</span>
          </button>
          {open === s.id && (
            <div className="submission-code">
              <pre>{s.code}</pre>
              <button className="btn btn-secondary btn-sm" onClick={() => onLoad(s.code)}>Load into editor</button>
            </div>
          )}
        </li>
      ))}
    </ul>
  )
}

function SolutionView({ slug, solved }) {
  const { data, loading, error } = useFetch(
    () => (solved ? problemsApi.solution(slug) : Promise.resolve(null)), [slug, solved])
  if (!solved) {
    return (
      <div className="locked">
        <span className="lock" aria-hidden="true">🔒</span>
        <h3>Solve it first</h3>
        <p className="muted">The reference solution unlocks after your first accepted submission.</p>
      </div>
    )
  }
  if (loading) return <Spinner />
  if (error) return <p className="console-error">{error.message}</p>
  return (
    <div className="solution">
      <h3>Reference solution</h3>
      <p className="muted small">One clean approach. Compare its time and space complexity with yours.</p>
      <pre className="code-block">{data.code}</pre>
    </div>
  )
}

function SignInPrompt({ slug, what }) {
  return (
    <div className="locked">
      <h3>Sign in to {what}</h3>
      <Link to={`/login?next=${encodeURIComponent(`/problems/${slug}`)}`} className="btn btn-primary">Sign in</Link>
    </div>
  )
}
