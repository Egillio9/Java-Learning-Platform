import { useState } from 'react'
import { VerdictBadge } from './Badges'
import { Spinner } from './Feedback'
import { labelInput } from '../utils/format'

/**
 * Bottom panel of the workspace: editable test inputs ("Testcase" tab) and the latest
 * Run / Submit outcome ("Result" tab).
 */
export default function ConsolePanel({ params, inputs, setInputs, tab, setTab, busy, result }) {
  const [selected, setSelected] = useState(0)
  const current = Math.min(selected, Math.max(inputs.length - 1, 0))

  const updateInput = (value) => setInputs(inputs.map((input, i) => (i === current ? value : input)))
  const addCase = () => {
    if (inputs.length >= 5) return
    setInputs([...inputs, inputs[current] ?? ''])
    setSelected(inputs.length)
  }
  const removeCase = (index) => {
    if (inputs.length <= 1) return
    setInputs(inputs.filter((_, i) => i !== index))
    setSelected(Math.max(0, index - 1))
  }

  return (
    <section className="console">
      <div className="console-tabs" role="tablist">
        <button role="tab" aria-selected={tab === 'testcase'} className={`console-tab${tab === 'testcase' ? ' active' : ''}`}
                onClick={() => setTab('testcase')}>Testcase</button>
        <button role="tab" aria-selected={tab === 'result'} className={`console-tab${tab === 'result' ? ' active' : ''}`}
                onClick={() => setTab('result')}>Test Result</button>
      </div>

      <div className="console-body">
        {tab === 'testcase' && (
          <>
            <div className="case-pills">
              {inputs.map((_, index) => (
                <span key={index} className={`case-pill${index === current ? ' active' : ''}`}>
                  <button onClick={() => setSelected(index)}>Case {index + 1}</button>
                  {inputs.length > 1 && (
                    <button className="case-remove" aria-label={`Remove case ${index + 1}`} onClick={() => removeCase(index)}>×</button>
                  )}
                </span>
              ))}
              {inputs.length < 5 && <button className="case-add" onClick={addCase} aria-label="Add test case">+</button>}
            </div>
            <label className="field-label">
              Input <span className="muted">(one argument per line{params?.length ? `: ${params.join(', ')}` : ''})</span>
            </label>
            <textarea className="case-input" spellCheck={false} value={inputs[current] ?? ''}
                      onChange={(event) => updateInput(event.target.value)} rows={4} />
          </>
        )}

        {tab === 'result' && <ResultView busy={busy} result={result} params={params} />}
      </div>
    </section>
  )
}

function ResultView({ busy, result, params }) {
  const [selected, setSelected] = useState(0)

  if (busy) {
    return (
      <div className="console-placeholder">
        <Spinner label="Judging" />
        <p>{busy === 'submit' ? 'Judging against all test cases…' : 'Running your code…'}</p>
      </div>
    )
  }
  if (!result) {
    return <p className="console-placeholder muted">Run your code to see results here. Shortcut: Ctrl + Enter.</p>
  }
  if (result.error) {
    return <div className="console-error">{result.error}</div>
  }

  const { kind, report } = result
  const cases = kind === 'run' ? report.cases : report.failedCase ? [report.failedCase] : []
  const current = cases[Math.min(selected, Math.max(cases.length - 1, 0))]

  return (
    <div className="result">
      <div className="result-head">
        <VerdictBadge verdict={report.verdict} />
        <span className="muted">
          {report.passed} / {report.total} {kind === 'submit' ? 'test cases passed' : 'cases passed'}
          {report.runtimeMs != null && report.verdict !== 'COMPILE_ERROR' && ` · Runtime ${report.runtimeMs} ms`}
        </span>
      </div>

      {kind === 'submit' && report.verdict === 'ACCEPTED' && (
        <div className="accepted-banner">
          {report.firstSolve ? '🎉 First solve, +10 points on the leaderboard.' : 'All hidden test cases passed.'}
        </div>
      )}

      {report.message && report.verdict !== 'WRONG_ANSWER' && <pre className="console-error">{report.message}</pre>}

      {cases.length > 0 && (
        <>
          {kind === 'run' && (
            <div className="case-pills">
              {cases.map((item, index) => (
                <span key={index} className={`case-pill${index === selected ? ' active' : ''}`}>
                  <button onClick={() => setSelected(index)}>
                    <span className={`dot ${item.passed ? 'dot-ok' : 'dot-bad'}`} /> Case {index + 1}
                  </button>
                </span>
              ))}
            </div>
          )}
          {kind === 'submit' && <p className="muted small">Last failing test case:</p>}
          {current && <CaseDetail item={current} params={params} />}
        </>
      )}
    </div>
  )
}

function CaseDetail({ item, params }) {
  return (
    <div className="case-detail">
      <Block label="Input" value={labelInput(item.input, params)} />
      {item.stdout && <Block label="Stdout" value={item.stdout} />}
      {item.error ? (
        <Block label="Error" value={item.error} tone="bad" />
      ) : (
        <Block label="Output" value={item.output ?? ''} tone={item.passed ? 'ok' : 'bad'} />
      )}
      <Block label="Expected" value={item.expected ?? '—'} />
    </div>
  )
}

function Block({ label, value, tone }) {
  return (
    <div className="io-block">
      <span className="io-label">{label}</span>
      <pre className={`io-value${tone ? ` io-${tone}` : ''}`}>{value}</pre>
    </div>
  )
}
