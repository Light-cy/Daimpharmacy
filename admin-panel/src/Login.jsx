import React, { useState } from 'react';
import { collection, query, where, getDocs } from 'firebase/firestore';
import { db } from './firebase';

function Login({ onLogin }) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleLogin = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      // Query the 'users' collection for matching email/password and admin role
      const usersRef = collection(db, 'users');
      const q = query(usersRef, where('email', '==', email), where('password', '==', password));
      const querySnapshot = await getDocs(q);

      if (querySnapshot.empty) {
        setError('Invalid email or password.');
        setLoading(false);
        return;
      }

      let isAdmin = false;
      let userData = null;

      querySnapshot.forEach((doc) => {
        const data = doc.data();
        if (data.role === 'admin' || data.isAdmin === true) {
          isAdmin = true;
          userData = { id: doc.id, ...data };
        }
      });

      if (isAdmin) {
        localStorage.setItem('adminSession', JSON.stringify(userData));
        onLogin();
      } else {
        setError('Access denied. You do not have admin privileges.');
      }
    } catch (err) {
      console.error("Login error: ", err);
      setError('An error occurred during login. Please try again.');
    }

    setLoading(false);
  };

  return (
    <div className="app-container" style={{ justifyContent: 'center', alignItems: 'center', background: 'var(--bg-color)' }}>
      <div className="glass-panel" style={{ padding: '40px', width: '100%', maxWidth: '420px', textAlign: 'center' }}>
        <h2 style={{ marginBottom: '8px', fontSize: '1.75rem', color: 'var(--primary-dark)' }}>Daimpharmacy</h2>
        <p style={{ color: 'var(--text-muted)', marginBottom: '32px' }}>Admin Dashboard Login</p>
        
        {error && <div style={{ background: '#fee2e2', color: '#b91c1c', padding: '12px', borderRadius: '8px', marginBottom: '20px', fontSize: '0.875rem' }}>{error}</div>}

        <form onSubmit={handleLogin} style={{ textAlign: 'left' }}>
          <div className="input-group">
            <label>Email Address</label>
            <input 
              type="email" 
              className="input-field" 
              placeholder="admin@daimpharmacy.com" 
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>
          <div className="input-group" style={{ marginBottom: '24px' }}>
            <label>Password</label>
            <input 
              type="password" 
              className="input-field" 
              placeholder="••••••••" 
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>
          
          <button type="submit" className="btn btn-primary" style={{ width: '100%', padding: '12px' }} disabled={loading}>
            {loading ? 'Authenticating...' : 'Sign In to Dashboard'}
          </button>
        </form>
      </div>
    </div>
  );
}

export default Login;
