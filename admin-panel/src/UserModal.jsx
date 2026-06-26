import React, { useState, useEffect } from 'react';
import { doc, setDoc } from 'firebase/firestore';
import { db } from './firebase';

const UserModal = React.memo(({ user, onClose }) => {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    role: 'doctor'
  });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (user) {
      setFormData({
        name: user.name || '',
        email: user.email || '',
        password: user.password || '',
        role: user.role || 'doctor'
      });
    }
  }, [user]);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSave = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      // If editing, use existing ID. If adding, generate new string ID (using email as ID or timestamp).
      // The mobile app expects `id` to be a String.
      const userId = user?.id || `user_${Date.now()}`;
      
      const userData = {
        id: userId,
        name: formData.name,
        email: formData.email,
        password: formData.password,
        role: formData.role,
        isActive: user?.isActive ?? true,
        lastOrderItemsJson: user?.lastOrderItemsJson || ""
      };

      await setDoc(doc(db, 'users', userId), userData);
      onClose();
    } catch (err) {
      console.error("Error saving user:", err);
      alert("Failed to save user.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, background: 'rgba(0,0,0,0.5)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000, padding: '24px' }}>
      <div className="card" style={{ width: '100%', maxWidth: '400px', background: '#fff' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid var(--border-color)', paddingBottom: '16px', marginBottom: '16px' }}>
          <h2 style={{ margin: 0, fontSize: '1.25rem' }}>{user ? 'Edit User' : 'Add New User'}</h2>
          <button onClick={onClose} style={{ background: 'none', border: 'none', fontSize: '1.5rem', cursor: 'pointer' }}>&times;</button>
        </div>

        <form onSubmit={handleSave}>
          <div className="input-group">
            <label>Name</label>
            <input name="name" value={formData.name} onChange={handleChange} className="input-field" required />
          </div>
          <div className="input-group">
            <label>Email</label>
            <input type="email" name="email" value={formData.email} onChange={handleChange} className="input-field" required />
          </div>
          <div className="input-group">
            <label>Password</label>
            <input type="text" name="password" value={formData.password} onChange={handleChange} className="input-field" required />
          </div>
          <div className="input-group">
            <label>Role</label>
            <select name="role" value={formData.role} onChange={handleChange} className="input-field" required style={{ width: '100%' }}>
              <option value="doctor">Doctor</option>
              <option value="admin">Admin</option>
            </select>
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px', marginTop: '24px' }}>
            <button type="button" onClick={onClose} className="btn" style={{ background: '#f1f5f9' }}>Cancel</button>
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? 'Saving...' : 'Save User'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
});

export default UserModal;
