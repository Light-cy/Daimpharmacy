import React, { useState, useEffect } from 'react';
import { doc, setDoc, getDocs, collection } from 'firebase/firestore';
import { ref, uploadBytes, getDownloadURL } from 'firebase/storage';
import { db, storage } from './firebase';

const MedicineModal = React.memo(({ medicine, onClose }) => {
  const [formData, setFormData] = useState({
    name: '',
    formula: '',
    category: '',
    price: '',
    stock: '',
    imageType: 'url', // 'url' or 'upload'
    imageUri: ''
  });
  
  const [categories, setCategories] = useState([]);
  const [file, setFile] = useState(null);
  const [preview, setPreview] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    // Fetch categories for the chips selection
    const fetchCategories = async () => {
      try {
        const snap = await getDocs(collection(db, 'categories'));
        const cats = [];
        snap.forEach(doc => cats.push(doc.data()));
        setCategories(cats);
        // Auto-select first category if adding new
        if (cats.length > 0 && !medicine) {
          setFormData(prev => ({ ...prev, category: cats[0].name }));
        }
      } catch (e) {
        console.error("Failed to load categories", e);
      }
    };
    fetchCategories();

    if (medicine) {
      setFormData({
        name: medicine.name || '',
        formula: medicine.formula || '',
        category: medicine.category || '',
        price: medicine.price || '',
        stock: medicine.stock || '',
        imageType: 'url',
        imageUri: medicine.imageUri || medicine.imageUrl || ''
      });
      setPreview(medicine.imageUri || medicine.imageUrl || null);
    }
  }, [medicine]);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleFileChange = (e) => {
    if (e.target.files && e.target.files[0]) {
      const selectedFile = e.target.files[0];
      setFile(selectedFile);
      setPreview(URL.createObjectURL(selectedFile));
    }
  };

  const handleSave = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      let finalImageUri = formData.imageUri;

      // Handle Firebase Storage Upload if option B is selected
      if (formData.imageType === 'upload' && file) {
        const fileRef = ref(storage, `medicines/${Date.now()}_${file.name}`);
        const snapshot = await uploadBytes(fileRef, file);
        finalImageUri = await getDownloadURL(snapshot.ref);
      }

      // Generate integer ID for mobile compatibility if adding new
      // Use time modulo to ensure a positive integer
      const medId = medicine?.id || Math.floor(Date.now() / 1000) % 2147483647;

      const medData = {
        id: medId,
        name: formData.name,
        formula: formData.formula,
        category: formData.category,
        price: parseFloat(formData.price) || 0,
        stock: parseInt(formData.stock) || 0,
        imageUri: finalImageUri,
        imageUrl: finalImageUri, // For backwards compatibility with some mobile entities
        isAvailable: medicine?.isAvailable ?? true
      };

      await setDoc(doc(db, 'medicines', medId.toString()), medData);
      onClose();
    } catch (err) {
      console.error("Error saving medicine:", err);
      alert("Failed to save medicine. Check console or Storage Rules.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, background: 'rgba(0,0,0,0.5)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000, padding: '24px' }}>
      <div className="card" style={{ width: '100%', maxWidth: '600px', maxHeight: '90vh', overflowY: 'auto', background: '#fff' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid var(--border-color)', paddingBottom: '16px', marginBottom: '16px' }}>
          <h2 style={{ margin: 0, fontSize: '1.25rem' }}>{medicine ? 'Edit Medicine' : 'Add New Medicine'}</h2>
          <button onClick={onClose} style={{ background: 'none', border: 'none', fontSize: '1.5rem', cursor: 'pointer' }}>&times;</button>
        </div>

        <form onSubmit={handleSave}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
            <div className="input-group">
              <label>Medicine Name</label>
              <input name="name" value={formData.name} onChange={handleChange} className="input-field" required />
            </div>
            <div className="input-group">
              <label>Formula</label>
              <input name="formula" value={formData.formula} onChange={handleChange} className="input-field" required />
            </div>
          </div>

          <div className="input-group" style={{ marginTop: '16px' }}>
            <label>Select Category</label>
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '8px', marginTop: '8px' }}>
              {categories.map(cat => (
                <div 
                  key={cat.id} 
                  onClick={() => setFormData({...formData, category: cat.name})}
                  style={{
                    padding: '6px 12px',
                    borderRadius: '999px',
                    cursor: 'pointer',
                    fontSize: '0.85rem',
                    fontWeight: '500',
                    background: formData.category === cat.name ? 'var(--primary-color)' : '#f1f5f9',
                    color: formData.category === cat.name ? '#fff' : '#475569',
                    transition: 'all 0.2s'
                  }}
                >
                  {cat.name}
                </div>
              ))}
            </div>
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px', marginTop: '16px' }}>
            <div className="input-group">
              <label>Price (Rs.)</label>
              <input type="number" step="0.01" name="price" value={formData.price} onChange={handleChange} className="input-field" required />
            </div>
            <div className="input-group">
              <label>Stock Quantity</label>
              <input type="number" name="stock" value={formData.stock} onChange={handleChange} className="input-field" required />
            </div>
          </div>

          <div className="input-group" style={{ marginTop: '16px' }}>
            <label>Medicine Image</label>
            <div style={{ display: 'flex', gap: '16px', marginBottom: '8px' }}>
              <label style={{ display: 'flex', alignItems: 'center', gap: '6px', cursor: 'pointer', fontWeight: 'normal' }}>
                <input type="radio" name="imageType" value="url" checked={formData.imageType === 'url'} onChange={handleChange} />
                Web URL
              </label>
              <label style={{ display: 'flex', alignItems: 'center', gap: '6px', cursor: 'pointer', fontWeight: 'normal' }}>
                <input type="radio" name="imageType" value="upload" checked={formData.imageType === 'upload'} onChange={handleChange} />
                Upload File
              </label>
            </div>

            {formData.imageType === 'url' ? (
              <input 
                type="url" 
                name="imageUri" 
                value={formData.imageUri} 
                onChange={(e) => {
                  handleChange(e);
                  setPreview(e.target.value);
                }} 
                className="input-field" 
                placeholder="https://example.com/image.jpg" 
              />
            ) : (
              <input 
                type="file" 
                accept="image/*" 
                onChange={handleFileChange} 
                className="input-field" 
                style={{ padding: '8px' }}
                required={!medicine && !preview}
              />
            )}
          </div>

          {preview && (
            <div style={{ marginTop: '12px', border: '1px solid var(--border-color)', borderRadius: '8px', padding: '8px', width: 'fit-content' }}>
              <p style={{ margin: '0 0 8px 0', fontSize: '0.8rem', color: 'var(--text-muted)' }}>Image Preview:</p>
              <img 
                src={preview} 
                alt="Preview" 
                style={{ maxWidth: '120px', maxHeight: '120px', borderRadius: '4px', objectFit: 'cover' }} 
                onError={(e) => { 
                  e.target.onerror = null; 
                  e.target.src = "data:image/svg+xml;charset=utf-8,%3Csvg xmlns='http://www.w3.org/2000/svg' width='80' height='80' viewBox='0 0 80 80'%3E%3Crect width='80' height='80' fill='%23e2e8f0'/%3E%3Ctext x='50%25' y='50%25' font-size='30' text-anchor='middle' dy='.3em'%3E💊%3C/text%3E%3C/svg%3E"; 
                }} 
              />
            </div>
          )}

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px', marginTop: '32px' }}>
            <button type="button" onClick={onClose} className="btn" style={{ background: '#f1f5f9' }}>Cancel</button>
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? 'Saving...' : 'Save Medicine'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
});

export default MedicineModal;
