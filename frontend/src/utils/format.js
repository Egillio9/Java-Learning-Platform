export const VERDICT_LABELS = {
  ACCEPTED: 'Accepted',
  WRONG_ANSWER: 'Wrong Answer',
  COMPILE_ERROR: 'Compile Error',
  RUNTIME_ERROR: 'Runtime Error',
  TIME_LIMIT_EXCEEDED: 'Time Limit Exceeded',
}

export const DIFFICULTY_LABELS = { EASY: 'Easy', MEDIUM: 'Medium', HARD: 'Hard' }

export function timeAgo(iso) {
  if (!iso) return ''
  const seconds = Math.max(1, Math.round((Date.now() - new Date(iso).getTime()) / 1000))
  const steps = [
    [60, 'second'],
    [60, 'minute'],
    [24, 'hour'],
    [30, 'day'],
    [12, 'month'],
  ]
  let value = seconds
  for (const [size, unit] of steps) {
    if (value < size) return `${value} ${unit}${value === 1 ? '' : 's'} ago`
    value = Math.floor(value / size)
  }
  return `${value} year${value === 1 ? '' : 's'} ago`
}

/** "nums = [2,7], target = 9" from the raw one-argument-per-line test input. */
export function labelInput(input, params = []) {
  return input
    .split('\n')
    .map((line, index) => (params[index] ? `${params[index]} = ${line}` : line))
    .join('\n')
}

export function initials(name = '') {
  return (
    name
      .split(/\s+/)
      .filter(Boolean)
      .slice(0, 2)
      .map((part) => part[0].toUpperCase())
      .join('') || '?'
  )
}
