import { initials } from '../utils/format'

export default function Avatar({ name, src, size = 32 }) {
  const style = { width: size, height: size, fontSize: Math.round(size * 0.4) }
  if (src) {
    return <img className="avatar" src={src} alt="" style={style} referrerPolicy="no-referrer" />
  }
  return (
    <span className="avatar avatar-initials" style={style} aria-hidden="true">
      {initials(name)}
    </span>
  )
}
