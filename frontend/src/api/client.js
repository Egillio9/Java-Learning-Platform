/** Thin fetch wrapper: JSON in/out, session cookie included, errors carry the server's message. */
export class ApiError extends Error {
  constructor(status, message) {
    super(message)
    this.status = status
  }
}

export async function request(path, { method = 'GET', body } = {}) {
  const response = await fetch(path, {
    method,
    credentials: 'same-origin',
    headers: body !== undefined ? { 'Content-Type': 'application/json' } : undefined,
    body: body !== undefined ? JSON.stringify(body) : undefined,
  })
  if (response.status === 204) return null
  const text = await response.text()
  const data = text ? safeParse(text) : null
  if (!response.ok) {
    const message = data?.message || defaultMessage(response.status)
    throw new ApiError(response.status, message)
  }
  return data
}

function safeParse(text) {
  try {
    return JSON.parse(text)
  } catch {
    return null
  }
}

function defaultMessage(status) {
  if (status === 401) return 'Please sign in to continue.'
  if (status === 503) return 'The server is busy or waking up. Try again in a few seconds.'
  return `Request failed (${status})`
}
