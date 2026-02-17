import { useState } from 'react'
import './App.css'

function App() {
  const [longUrl, setLongUrl] = useState('')
  const [alias, setAlias] = useState('')
  const [result, setResult] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    setError('')
    setResult(null)

    try {
      const response = await fetch('/api/v1/shorten', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          longUrl,
          customAlias: alias || null
        }),
      })

      if (!response.ok) {
        throw new Error('Failed to shorten: ' + response.statusText)
      }

      const data = await response.json()
      setResult(data)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const copyToClipboard = () => {
    if (result) {
      navigator.clipboard.writeText(result.shortUrl)
      alert('Copied to clipboard!')
    }
  }

  return (
    <div className="container">
      <h1>Short.ly</h1>
      <p className="subtitle">Shorten your links, expand your reach.</p>

      <form onSubmit={handleSubmit}>
        <div className="input-group">
          <input
            type="url"
            placeholder="Paste your long URL here..."
            value={longUrl}
            onChange={(e) => setLongUrl(e.target.value)}
            required
          />
          <input
            type="text"
            placeholder="Custom alias (optional)"
            value={alias}
            onChange={(e) => setAlias(e.target.value)}
            maxLength={10}
          />
        </div>

        <button type="submit" disabled={loading}>
          {loading ? 'Shortening...' : 'Shorten URL'}
        </button>
      </form>

      {error && <div className="error-msg">{error}</div>}

      {result && (
        <div className="result-card">
          <p>Success! Here's your short link:</p>
          <div className="short-url">
            <a href={result.shortUrl} target="_blank" rel="noopener noreferrer">
              {result.shortUrl}
            </a>
          </div>
          <button className="copy-btn" onClick={copyToClipboard}>
            Copy to Clipboard
          </button>
        </div>
      )}
    </div>
  )
}

export default App
