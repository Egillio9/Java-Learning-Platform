import { useMemo } from 'react'
import { marked } from 'marked'

marked.setOptions({ gfm: true, breaks: false })

/** Renders problem statements, which are authored in this repository (trusted content). */
export default function Markdown({ source }) {
  const html = useMemo(() => marked.parse(source ?? ''), [source])
  return <div className="markdown" dangerouslySetInnerHTML={{ __html: html }} />
}
