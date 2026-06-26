import React, { useState, useEffect } from 'react';
import { doc, setDoc } from 'firebase/firestore';
import { db } from './firebase';

const CategoryModal = React.memo(({ category, onClose }) => {
  const [formData, setFormData] = useState({
    name: ''
  });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (category) {
      setFormData({
        name: category.name || ''
      });
    }
  }, [category]);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSave = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      // Create an ID based on name if adding new (e.g. "syrups"), else use existing
      const catId = category?.id || formData.name.toLowerCase().replace(/[^a-z0-9]/g, '');
      
      const catData = {
        id: catId,
        name: formData.name
      };

      await setDoc(doc(db, 'categories', catId), catData);
      onClose();
    } catch (err) {
      console.error("Error saving category:", err);
      alert("Failed to save category.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, background: 'rgba(0,0,0,0.5)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000, padding: '24px' }}>
      <div className="card" style={{ width: '100%', maxWidth: '400px', background: '#fff' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid var(--border-color)', paddingBottom: '16px', marginBottom: '16px' }}>
          <h2 style={{ margin: 0, fontSize: '1.25rem' }}>{category ? 'Edit Category' : 'Add New Category'}</h2>
          <button onClick={onClose} style={{ background: 'none', border: 'none', fontSize: '1.5rem', cursor: 'pointer' }}>&times;</button>
        </div>

        <form onSubmit={handleSave}>
          <div className="input-group">
            <label>Category Name</label>
            <input name="name" value={formData.name} onChange={handleChange} className="input-field" placeholder="e.g. Tablets" required />
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px', marginTop: '24px' }}>
            <button type="button" onClick={onClose} className="btn" style={{ background: '#f1f5f9' }}>Cancel</button>
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? 'Saving...' : 'Save Category'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
});

export default CategoryModal;
