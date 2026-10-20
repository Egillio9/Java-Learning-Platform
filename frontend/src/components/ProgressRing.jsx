/** Circular progress with up to three coloured segments (easy / medium / hard). */
export default function ProgressRing({ segments, total, size = 140, stroke = 10, children }) {
  const radius = (size - stroke) / 2
  const circumference = 2 * Math.PI * radius
  let offset = 0

  return (
    <div className="ring" style={{ width: size, height: size }}>
      <svg width={size} height={size} viewBox={`0 0 ${size} ${size}`} role="img"
           aria-label={`${segments.reduce((sum, s) => sum + s.value, 0)} of ${total}`}>
        <circle cx={size / 2} cy={size / 2} r={radius} fill="none" stroke="var(--surface-3)" strokeWidth={stroke} />
        {segments.filter((segment) => segment.value > 0).map((segment) => {
          const length = total === 0 ? 0 : (segment.value / total) * circumference
          const dash = (
            <circle key={segment.color} cx={size / 2} cy={size / 2} r={radius} fill="none" stroke={segment.color}
                    strokeWidth={stroke} strokeLinecap="round" strokeDasharray={`${length} ${circumference}`}
                    strokeDashoffset={-offset} transform={`rotate(-90 ${size / 2} ${size / 2})`} />
          )
          offset += length
          return dash
        })}
      </svg>
      <div className="ring-label">{children}</div>
    </div>
  )
}
