import { Link } from 'react-router-dom'

const CONTACT = 'hmnsu.kd@gmail.com'
const UPDATED = '20 October 2026'

export default function Privacy() {
  return (
    <div className="page legal-page">
      <Link to="/" className="link small">← Home</Link>
      <h1>Privacy Policy</h1>
      <p className="muted">Last updated {UPDATED}</p>

      <p>
        JavaLearn is a free, non-commercial learning project. This page explains what information it stores
        and why. The short version: only what is needed to save your learning progress.
      </p>

      <h2>What we collect</h2>
      <ul>
        <li><strong>Guest sign-in:</strong> the display name you type in.</li>
        <li>
          <strong>Google sign-in:</strong> your name, email address and profile picture URL, provided by Google
          with the <code>openid</code>, <code>email</code> and <code>profile</code> scopes. We never see or store
          your Google password.
        </li>
        <li>
          <strong>Your activity:</strong> code you submit to the judge, the resulting verdicts, completed lessons
          and quiz scores.
        </li>
        <li><strong>A session cookie</strong> that keeps you signed in. There are no advertising or tracking cookies.</li>
      </ul>

      <h2>How we use it</h2>
      <p>
        Only to run the platform: to show your dashboard, submission history and progress, and to display your
        display name and score on the public leaderboard. Your email address is never shown to other users.
      </p>

      <h2>Sharing</h2>
      <p>We do not sell, rent or share your information with anyone, and we do not use it for advertising.</p>

      <h2>Storage and retention</h2>
      <p>
        Data is stored on the application server hosted on Render. The demo deployment may reset its database
        when the server restarts, which deletes stored data. You can ask for your data to be deleted at any time
        by emailing us.
      </p>

      <h2>Your choices</h2>
      <p>
        You can use JavaLearn as a guest without a Google account. If you signed in with Google, you can revoke
        access at any time from your Google Account's security settings.
      </p>

      <h2>Contact</h2>
      <p>
        Questions or deletion requests: <a className="link" href={`mailto:${CONTACT}`}>{CONTACT}</a>
      </p>
    </div>
  )
}
