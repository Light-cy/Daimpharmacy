import React, { useState, useEffect, useMemo, useCallback } from 'react';
import { collection, onSnapshot, doc, deleteDoc, query, orderBy, limit, startAfter, endBefore, limitToLast } from 'firebase/firestore';
import { db } from './firebase';

// Debounce Hook
function useDebounce(value, delay) {
  const [debouncedValue, setDebouncedValue] = useState(value);
  useEffect(() => {
    const handler = setTimeout(() => setDebouncedValue(value), delay);
    return () => clearTimeout(handler);
  }, [value, delay]);
  return debouncedValue;
}

const DataTable = React.memo(({ title, collectionName, columns, renderActions, onAdd, onEdit, searchKeys }) => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  
  // Search & Pagination States
  const [searchQuery, setSearchQuery] = useState('');
  const debouncedSearch = useDebounce(searchQuery, 300);
  
  const [pageData, setPageData] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 20;

  const [confirmDelete, setConfirmDelete] = useState(null);

  useEffect(() => {
    // We load all data for real-time search/analytics compatibility, but we memoize slicing it for the view.
    // Real cursor pagination requires complex indexing for search/sort, so we use virtual/local pagination
    // over the cached collection to maintain lightning speed and offline support without index limits.
    const unsub = onSnapshot(collection(db, collectionName), (snapshot) => {
      const items = [];
      // Use a strict _docId field for operations because the internal 'id' field might be an integer (e.g. for Medicines)
      snapshot.forEach(doc => items.push({ id: doc.id, ...doc.data(), _docId: doc.id }));
      setData(items);
      setLoading(false);
    });
    return () => unsub();
  }, [collectionName]);

  const handleDeleteConfirm = useCallback(async () => {
    if (confirmDelete) {
      try {
        await deleteDoc(doc(db, collectionName, confirmDelete.toString()));
        setConfirmDelete(null);
      } catch (err) {
        console.error("Delete error:", err);
        alert("Failed to delete record.");
      }
    }
  }, [collectionName, confirmDelete]);

  const filteredData = useMemo(() => {
    if (!debouncedSearch || !searchKeys || searchKeys.length === 0) return data;
    const query = debouncedSearch.toLowerCase();
    return data.filter(item => searchKeys.some(key => {
      const val = item[key];
      return val && val.toString().toLowerCase().includes(query);
    }));
  }, [data, debouncedSearch, searchKeys]);

  // Paginate filtered data locally to simulate cursor pagination for UI performance
  const paginatedData = useMemo(() => {
    const startIdx = (currentPage - 1) * itemsPerPage;
    return filteredData.slice(startIdx, startIdx + itemsPerPage);
  }, [filteredData, currentPage]);

  const totalPages = Math.ceil(filteredData.length / itemsPerPage);

  return (
    <div className="card" style={{ marginTop: '24px' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px', flexWrap: 'wrap', gap: '16px' }}>
        <h3 style={{ color: 'var(--text-main)', fontSize: '1.25rem', margin: 0 }}>{title}</h3>
        
        <div style={{ display: 'flex', gap: '12px', alignItems: 'center' }}>
          {searchKeys && searchKeys.length > 0 && (
            <input 
              type="text" 
              placeholder="Search..." 
              value={searchQuery}
              onChange={(e) => { setSearchQuery(e.target.value); setCurrentPage(1); }}
              className="input-field"
              style={{ width: '250px', margin: 0 }}
            />
          )}
          {onAdd && (
            <button onClick={onAdd} className="btn btn-primary" style={{ padding: '8px 16px', fontSize: '0.85rem', whiteSpace: 'nowrap' }}>
              + Add New
            </button>
          )}
        </div>
      </div>

      {loading ? (
        <div style={{ overflowX: 'auto' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
            <thead>
              <tr style={{ borderBottom: '1px solid var(--border-color)' }}>
                {columns.map((col, idx) => (
                  <th key={idx} style={{ padding: '12px 16px', color: 'var(--text-muted)', fontWeight: '600', fontSize: '0.875rem' }}>
                    {col.label}
                  </th>
                ))}
                <th style={{ padding: '12px 16px', textAlign: 'right' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {[1, 2, 3].map(i => (
                <tr key={i} style={{ borderBottom: '1px solid var(--border-color)' }}>
                  {columns.map((col, idx) => (
                    <td key={idx} style={{ padding: '12px 16px' }}>
                      <div className="shimmer" style={{ height: '20px', width: idx === 0 ? '60%' : '100%', borderRadius: '4px' }}></div>
                    </td>
                  ))}
                  <td style={{ padding: '12px 16px' }}>
                    <div className="shimmer" style={{ height: '24px', width: '80px', borderRadius: '4px', float: 'right' }}></div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ) : filteredData.length === 0 ? (
        <div style={{ color: 'var(--text-muted)' }}>No records found matching your search.</div>
      ) : (
        <div style={{ overflowX: 'auto' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
            <thead>
              <tr style={{ borderBottom: '1px solid var(--border-color)' }}>
                {columns.map((col, idx) => (
                  <th key={idx} style={{ padding: '12px 16px', color: 'var(--text-muted)', fontWeight: '600', fontSize: '0.875rem' }}>
                    {col.label}
                  </th>
                ))}
                <th style={{ padding: '12px 16px', textAlign: 'right' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {paginatedData.map((row) => (
                <tr key={row._docId} style={{ borderBottom: '1px solid var(--border-color)', transition: 'background 0.2s' }}>
                  {columns.map((col, idx) => (
                    <td key={idx} style={{ padding: '12px 16px', fontSize: '0.95rem' }}>
                      {col.render ? col.render(row) : row[col.key]}
                    </td>
                  ))}
                  <td style={{ padding: '12px 16px', width: '100px' }}>
                    {renderActions ? renderActions(row) : (
                      <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', alignItems: 'flex-end' }}>
                        {onEdit && (
                          <button 
                            onClick={() => onEdit(row)}
                            style={{ background: '#f0f9ff', border: '1px solid #bae6fd', color: '#0284c7', cursor: 'pointer', fontWeight: '600', padding: '6px 16px', borderRadius: '6px', fontSize: '0.8rem', width: '100%', transition: 'all 0.2s', textAlign: 'center' }}>
                            Edit
                          </button>
                        )}
                        {confirmDelete === row._docId ? (
                          <div style={{ display: 'flex', flexDirection: 'column', gap: '4px', width: '100%' }}>
                            <button 
                              onClick={handleDeleteConfirm}
                              style={{ background: '#ef4444', border: 'none', color: '#fff', cursor: 'pointer', fontWeight: 'bold', padding: '6px 12px', borderRadius: '6px', fontSize: '0.8rem', textAlign: 'center' }}>
                              Sure?
                            </button>
                            <button 
                              onClick={() => setConfirmDelete(null)}
                              style={{ background: '#e2e8f0', border: 'none', color: '#475569', cursor: 'pointer', fontWeight: 'bold', padding: '6px 12px', borderRadius: '6px', fontSize: '0.8rem', textAlign: 'center' }}>
                              Cancel
                            </button>
                          </div>
                        ) : (
                          <button 
                            onClick={() => setConfirmDelete(row._docId)}
                            style={{ background: '#fef2f2', border: '1px solid #fecaca', color: '#ef4444', cursor: 'pointer', fontWeight: '600', padding: '6px 16px', borderRadius: '6px', fontSize: '0.8rem', width: '100%', transition: 'all 0.2s', textAlign: 'center' }}>
                            Delete
                          </button>
                        )}
                      </div>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          
          {totalPages > 1 && (
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '16px', paddingTop: '16px', borderTop: '1px solid var(--border-color)' }}>
              <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Showing {(currentPage - 1) * itemsPerPage + 1} to {Math.min(currentPage * itemsPerPage, filteredData.length)} of {filteredData.length} records</span>
              <div style={{ display: 'flex', gap: '8px' }}>
                <button 
                  onClick={() => setCurrentPage(p => Math.max(1, p - 1))}
                  disabled={currentPage === 1}
                  className="btn" style={{ padding: '6px 12px', background: currentPage === 1 ? '#f1f5f9' : '#fff', color: currentPage === 1 ? '#94a3b8' : 'var(--text-main)' }}>
                  Previous
                </button>
                <button 
                  onClick={() => setCurrentPage(p => Math.min(totalPages, p + 1))}
                  disabled={currentPage === totalPages}
                  className="btn" style={{ padding: '6px 12px', background: currentPage === totalPages ? '#f1f5f9' : '#fff', color: currentPage === totalPages ? '#94a3b8' : 'var(--text-main)' }}>
                  Next
                </button>
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
});

export default DataTable;
