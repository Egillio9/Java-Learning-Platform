import { useEffect, useRef } from 'react'
import Editor from '@monaco-editor/react'
import { Spinner } from './Feedback'

/**
 * Monaco (the editor behind VS Code) configured for Java.
 * Ctrl/Cmd+Enter runs the code, Ctrl/Cmd+Shift+Enter submits it.
 */
export default function CodeEditor({ value, onChange, onRun, onSubmit, fontSize = 14 }) {
  const handlers = useRef({ onRun, onSubmit })
  useEffect(() => {
    handlers.current = { onRun, onSubmit }
  }, [onRun, onSubmit])

  const handleMount = (editor, monaco) => {
    editor.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyCode.Enter, () => handlers.current.onRun?.())
    editor.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyMod.Shift | monaco.KeyCode.Enter,
      () => handlers.current.onSubmit?.())
    monaco.editor.defineTheme('javalearn-dark', {
      base: 'vs-dark',
      inherit: true,
      rules: [],
      colors: { 'editor.background': '#14171f', 'editorLineNumber.foreground': '#4b5263' },
    })
    monaco.editor.setTheme('javalearn-dark')
    editor.focus()
  }

  return (
    <Editor
      height="100%"
      language="java"
      theme="vs-dark"
      value={value}
      onChange={(next) => onChange(next ?? '')}
      onMount={handleMount}
      loading={<Spinner label="Loading editor" />}
      options={{
        fontSize,
        fontFamily: "'JetBrains Mono', Consolas, monospace",
        fontLigatures: true,
        minimap: { enabled: false },
        scrollBeyondLastLine: false,
        automaticLayout: true,
        tabSize: 4,
        renderLineHighlight: 'all',
        padding: { top: 12 },
        smoothScrolling: true,
        bracketPairColorization: { enabled: true },
      }}
    />
  )
}
