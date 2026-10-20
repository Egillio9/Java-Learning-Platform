import { Link } from 'react-router-dom'
import Navbar from '../components/Navbar'
import { useAuth } from '../context/AuthContext'
import { useFetch } from '../hooks/useFetch'
import { statsApi } from '../api/endpoints'

const FEATURES = [
  {
    icon: '⚡',
    title: 'Real online judge',
    text: 'Write Java in a VS Code-grade editor. Your code is compiled and run against hidden test cases in an isolated JVM, with LeetCode-style verdicts.',
  },
  {
    icon: '🧭',
    title: 'Topic-wise DSA track',
    text: 'Arrays, strings, hashing, two pointers, sliding window, stacks, linked lists, trees, graphs, heaps and dynamic programming, from easy to hard.',
  },
  {
    icon: '📚',
    title: 'Structured Java course',
    text: 'Short lessons with runnable examples, from syntax and OOP to collections, generics, streams and concurrency, each ending in a graded quiz.',
  },
  {
    icon: '📈',
    title: 'Progress you can see',
    text: 'A personal dashboard with solved counts, acceptance rate, streaks and submission history, plus a public leaderboard.',
  },
]

const SNIPPET = `class Solution {
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> seen = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            Integer j = seen.get(target - nums[i]);
            if (j != null) return new int[] {j, i};
            seen.put(nums[i], i);
        }
        return new int[0];
    }
}`

export default function Landing() {
  const { user } = useAuth()
  const { data: stats } = useFetch(() => statsApi.platform(), [])
  const ctaTarget = user ? '/app' : '/login'

  return (
    <div className="landing">
      <Navbar />

      <section className="hero">
        <div className="hero-copy">
          <span className="eyebrow">Java · DSA · Interview prep</span>
          <h1>
            Learn Java.<br />
            <span className="gradient-text">Crack the coding interview.</span>
          </h1>
          <p className="hero-sub">
            JavaLearn pairs a structured Java course with a real online judge. Read a concept, solve a problem,
            get instant feedback, and watch your progress grow.
          </p>
          <div className="hero-actions">
            <Link to={ctaTarget} className="btn btn-primary btn-lg">
              {user ? 'Go to dashboard' : 'Start coding, it’s free'}
            </Link>
            <Link to="/problems" className="btn btn-secondary btn-lg">Browse problems</Link>
          </div>
          {stats && (
            <dl className="hero-stats">
              <div><dt>{stats.problems}</dt><dd>coding problems</dd></div>
              <div><dt>{stats.lessons}</dt><dd>lessons</dd></div>
              <div><dt>{stats.topics}</dt><dd>topics</dd></div>
              <div><dt>{stats.submissions}</dt><dd>submissions judged</dd></div>
            </dl>
          )}
        </div>

        <div className="hero-visual" aria-hidden="true">
          <div className="mock-window">
            <div className="mock-bar">
              <span className="mock-dot red" /><span className="mock-dot yellow" /><span className="mock-dot green" />
              <span className="mock-title">Solution.java · Two Sum</span>
            </div>
            <pre className="mock-code">{SNIPPET}</pre>
            <div className="mock-result">
              <span className="verdict verdict-ok">Accepted</span>
              <span className="muted">8 / 8 test cases passed · Runtime 1 ms</span>
            </div>
          </div>
        </div>
      </section>

      <section className="features">
        <h2>Everything you need to go from first class to final round</h2>
        <div className="feature-grid">
          {FEATURES.map((feature) => (
            <article key={feature.title} className="feature-card">
              <span className="feature-icon">{feature.icon}</span>
              <h3>{feature.title}</h3>
              <p>{feature.text}</p>
            </article>
          ))}
        </div>
      </section>

      <section className="how">
        <h2>How it works</h2>
        <ol className="steps">
          <li><strong>Sign in</strong> with your name or Google account. No setup, nothing to install.</li>
          <li><strong>Learn</strong> a topic with short lessons, then lock it in with a quiz.</li>
          <li><strong>Practise</strong> on problems: run against examples, then submit against hidden tests.</li>
          <li><strong>Track</strong> streaks, acceptance rate and your rank on the leaderboard.</li>
        </ol>
        <Link to={ctaTarget} className="btn btn-primary btn-lg">{user ? 'Continue learning' : 'Create your free account'}</Link>
      </section>

      <footer className="footer">
        <span>© {new Date().getFullYear()} JavaLearn · Built with Spring Boot &amp; React by Himanshu Kumar</span>
        <span className="footer-links">
          <Link to="/privacy">Privacy</Link>
          <a href="https://github.com/Egillio9/Java-Learning-Platform" target="_blank" rel="noreferrer">Source on GitHub</a>
        </span>
      </footer>
    </div>
  )
}
