import React, { useState, useEffect, useCallback, Suspense, lazy } from 'react';
import { collection, onSnapshot } from 'firebase/firestore';
import { db } from './firebase';
import DataTable from './DataTable';

const OrderModal = lazy(() => import('./OrderModal'));
const UserModal = lazy(() => import('./UserModal'));
const CategoryModal = lazy(() => import('./CategoryModal'));
const MedicineModal = lazy(() => import('./MedicineModal'));

function Dashboard({ onLogout }) {
  const [activeTab, setActiveTab] = useState(() => {
    const params = new URLSearchParams(window.location.search);
    const tabParam = params.get('tab');
    if (tabParam) {
      window.history.replaceState({}, '', window.location.pathname); // Clear URL
      return tabParam;
    }
    return localStorage.getItem('adminActiveTab') || 'overview';
  });
  const [counts, setCounts] = useState({ users: 0, medicines: 0, orders: 0, categories: 0 });
  const [loadingCounts, setLoadingCounts] = useState({ users: true, medicines: true, orders: true, categories: true });
  const [analytics, setAnalytics] = useState({ doctorStats: [], topMedicines: [], recentOrders: [] });
  const [selectedOrder, setSelectedOrder] = useState(null);
  
  useEffect(() => {
    localStorage.setItem('adminActiveTab', activeTab);
  }, [activeTab]);
  
  // Unified modal state
  const [modalType, setModalType] = useState(null); // 'user', 'category', 'medicine'
  const [editingItem, setEditingItem] = useState(null);

  const handleLogout = () => {
    localStorage.removeItem('adminSession');
    onLogout();
  };

  useEffect(() => {
    // Real-time counts for the overview dashboard (restored for real-time reactivity)
    const unsubUsers = onSnapshot(collection(db, 'users'), snap => { setCounts(prev => ({ ...prev, users: snap.size })); setLoadingCounts(prev => ({ ...prev, users: false })); });
    const unsubMeds = onSnapshot(collection(db, 'medicines'), snap => { setCounts(prev => ({ ...prev, medicines: snap.size })); setLoadingCounts(prev => ({ ...prev, medicines: false })); });
    const unsubCats = onSnapshot(collection(db, 'categories'), snap => { setCounts(prev => ({ ...prev, categories: snap.size })); setLoadingCounts(prev => ({ ...prev, categories: false })); });

    const unsubOrders = onSnapshot(collection(db, 'orders'), snap => { 
      const allOrders = [];
      snap.forEach(doc => allOrders.push({ id: doc.id, ...doc.data() }));
      
      const docStatsMap = {};
      const medStatsMap = {};

      allOrders.forEach(order => {
        const docName = order.doctorName || 'Unknown';
        docStatsMap[docName] = (docStatsMap[docName] || 0) + 1;

        if (order.itemsJson) {
          try {
            const items = JSON.parse(order.itemsJson);
            items.forEach(item => {
              const mName = item.medicineName;
              if (!medStatsMap[mName]) {
                medStatsMap[mName] = { name: mName, timesOrdered: 0, totalQuantity: 0 };
              }
              medStatsMap[mName].timesOrdered += 1;
              medStatsMap[mName].totalQuantity += item.quantity;
            });
          } catch(e) {}
        }
      });

      const doctorStats = Object.keys(docStatsMap).map(name => ({ name, count: docStatsMap[name] })).sort((a,b) => b.count - a.count);
      const topMedicines = Object.values(medStatsMap).sort((a,b) => b.timesOrdered - a.timesOrdered || b.totalQuantity - a.totalQuantity).slice(0, 10);
      const recentOrders = [...allOrders].sort((a,b) => b.createdAt - a.createdAt).slice(0, 10);

      setAnalytics({ doctorStats, topMedicines, recentOrders });
      setCounts(prev => ({ ...prev, orders: snap.size })); 
      setLoadingCounts(prev => ({ ...prev, orders: false })); 
    });

    return () => {
      unsubUsers(); unsubMeds(); unsubOrders(); unsubCats();
    };
  }, []);

  const closeModal = useCallback(() => {
    setModalType(null);
    setEditingItem(null);
  }, []);

  const renderContent = () => {
    switch (activeTab) {
      case 'users':
        return <DataTable 
          title="Manage Users" 
          collectionName="users" 
          columns={[
            { key: 'name', label: 'Name' },
            { key: 'email', label: 'Email' },
            { key: 'role', label: 'Role', render: (row) => <span style={{ padding: '4px 8px', borderRadius: '4px', background: row.role === 'admin' ? '#dbeafe' : '#f1f5f9', color: row.role === 'admin' ? '#1d4ed8' : '#475569', fontSize: '0.8rem', fontWeight: '600' }}>{row.role || 'user'}</span> },
            { key: 'phone', label: 'Phone' }
          ]} 
          onAdd={() => { setEditingItem(null); setModalType('user'); }}
          onEdit={(row) => { setEditingItem(row); setModalType('user'); }}
        />;
      case 'medicines':
        return <DataTable 
          title="Products & Inventory" 
          collectionName="medicines" 
          columns={[
            { 
              key: 'image', 
              label: 'Image', 
              render: (row) => {
                const placeholder = "data:image/svg+xml;charset=utf-8,%3Csvg xmlns='http://www.w3.org/2000/svg' width='80' height='80' viewBox='0 0 80 80'%3E%3Crect width='80' height='80' fill='%23e2e8f0'/%3E%3Ctext x='50%25' y='50%25' font-size='30' text-anchor='middle' dy='.3em'%3E💊%3C/text%3E%3C/svg%3E";
                return (
                  <img 
                    src={row.imageUri || row.imageUrl || placeholder} 
                    alt={row.name} 
                    loading="lazy"
                    style={{ width: '80px', height: '80px', borderRadius: '8px', objectFit: 'cover', background: '#f1f5f9' }}
                    onError={(e) => { e.target.onerror = null; e.target.src = placeholder; }}
                  />
                );
              } 
            },
            { 
              key: 'name', 
              label: 'Medicine Name', 
              render: (row) => (
                <div>
                  <strong style={{ display: 'block', color: '#0f172a', fontSize: '1.05rem' }}>{row.name}</strong>
                  <span style={{ fontSize: '0.85rem', color: '#64748b' }}>{row.formula}</span>
                </div>
              ) 
            },
            { key: 'category', label: 'Category' },
            { key: 'price', label: 'Price (Rs)', render: (row) => `Rs ${row.price}` },
            { key: 'stock', label: 'Stock', render: (row) => <span style={{ color: row.stock > 10 ? '#10b981' : '#ef4444', fontWeight: 'bold' }}>{row.stock}</span> }
          ]} 
          onAdd={() => { setEditingItem(null); setModalType('medicine'); }}
          onEdit={(row) => { setEditingItem(row); setModalType('medicine'); }}
          searchKeys={['name', 'formula']}
        />;
      case 'categories':
        return <DataTable 
          title="Manage Categories" 
          collectionName="categories" 
          columns={[
            { key: 'name', label: 'Category Name' },
            { key: 'description', label: 'Description', render: (row) => row.iconName || 'N/A' }
          ]} 
          onAdd={() => { setEditingItem(null); setModalType('category'); }}
          onEdit={(row) => { setEditingItem(row); setModalType('category'); }}
        />;
      case 'orders':
        return (
          <>
            <DataTable 
              title="Orders Management" 
              collectionName="orders" 
              columns={[
                { key: 'id', label: 'Order ID', render: (row) => row.id.length > 8 ? row.id.substring(0, 8) + '...' : row.id },
                { key: 'doctorName', label: 'Doctor Name' },
                { key: 'date', label: 'Date', render: (row) => new Date(row.createdAt).toLocaleDateString() },
                { key: 'status', label: 'Status', render: (row) => <span style={{ padding: '4px 8px', borderRadius: '4px', background: row.status === 'pending' ? '#fef3c7' : '#d1fae5', color: row.status === 'pending' ? '#d97706' : '#059669', fontSize: '0.8rem', fontWeight: '600', textTransform: 'capitalize' }}>{row.status}</span> }
              ]} 
              renderActions={(row) => (
                <button 
                  onClick={() => setSelectedOrder(row)}
                  style={{ background: '#0ea5e9', border: 'none', color: '#fff', cursor: 'pointer', fontWeight: '500', padding: '6px 12px', borderRadius: '4px' }}>
                  View Slip
                </button>
              )}
              searchKeys={['doctorName']}
            />
            <Suspense fallback={null}>
              <OrderModal order={selectedOrder} onClose={() => setSelectedOrder(null)} />
            </Suspense>
          </>
        );
      case 'overview':
      default:
        return (
          <>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '24px' }}>
              <div className="card" style={{ display: 'flex', flexDirection: 'column', gap: '12px', borderTop: '4px solid var(--primary-color)' }}>
                <h3 style={{ color: 'var(--text-muted)', fontSize: '0.875rem', textTransform: 'uppercase', letterSpacing: '0.05em' }}>Total Users</h3>
                {loadingCounts.users ? <div className="shimmer" style={{ height: '48px', width: '60px', borderRadius: '4px' }}></div> : <div style={{ fontSize: '2.5rem', fontWeight: '700', color: 'var(--primary-dark)' }}>{counts.users}</div>}
              </div>
              <div className="card" style={{ display: 'flex', flexDirection: 'column', gap: '12px', borderTop: '4px solid var(--primary-color)' }}>
                <h3 style={{ color: 'var(--text-muted)', fontSize: '0.875rem', textTransform: 'uppercase', letterSpacing: '0.05em' }}>Medicines</h3>
                {loadingCounts.medicines ? <div className="shimmer" style={{ height: '48px', width: '60px', borderRadius: '4px' }}></div> : <div style={{ fontSize: '2.5rem', fontWeight: '700', color: 'var(--primary-dark)' }}>{counts.medicines}</div>}
              </div>
              <div className="card" style={{ display: 'flex', flexDirection: 'column', gap: '12px', borderTop: '4px solid var(--primary-color)' }}>
                <h3 style={{ color: 'var(--text-muted)', fontSize: '0.875rem', textTransform: 'uppercase', letterSpacing: '0.05em' }}>Orders</h3>
                {loadingCounts.orders ? <div className="shimmer" style={{ height: '48px', width: '60px', borderRadius: '4px' }}></div> : <div style={{ fontSize: '2.5rem', fontWeight: '700', color: 'var(--primary-dark)' }}>{counts.orders}</div>}
              </div>
              <div className="card" style={{ display: 'flex', flexDirection: 'column', gap: '12px', borderTop: '4px solid var(--primary-color)' }}>
                <h3 style={{ color: 'var(--text-muted)', fontSize: '0.875rem', textTransform: 'uppercase', letterSpacing: '0.05em' }}>Categories</h3>
                {loadingCounts.categories ? <div className="shimmer" style={{ height: '48px', width: '60px', borderRadius: '4px' }}></div> : <div style={{ fontSize: '2.5rem', fontWeight: '700', color: 'var(--primary-dark)' }}>{counts.categories}</div>}
              </div>
            </div>
            
            <div style={{ marginTop: '32px', display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(400px, 1fr))', gap: '24px' }}>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
                <div className="card">
                  <h3 style={{ marginBottom: '16px', color: 'var(--text-main)' }}>Orders Per Doctor</h3>
                  {loadingCounts.orders ? <div className="shimmer" style={{ height: '100px', borderRadius: '8px' }}></div> : (
                    <div style={{ overflowX: 'auto' }}>
                      <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
                        <thead>
                          <tr style={{ borderBottom: '1px solid var(--border-color)' }}>
                            <th style={{ padding: '8px 0', color: 'var(--text-muted)' }}>Doctor Name</th>
                            <th style={{ padding: '8px 0', color: 'var(--text-muted)', textAlign: 'right' }}>Total Orders</th>
                          </tr>
                        </thead>
                        <tbody>
                          {analytics.doctorStats.map(stat => (
                            <tr key={stat.name} style={{ borderBottom: '1px solid var(--border-color)' }}>
                              <td style={{ padding: '12px 0', fontWeight: '500' }}>Dr. {stat.name}</td>
                              <td style={{ padding: '12px 0', textAlign: 'right' }}>
                                <span style={{ background: '#f1f8f4', color: '#2e7d32', padding: '4px 12px', borderRadius: '999px', fontWeight: 'bold' }}>{stat.count}</span>
                              </td>
                            </tr>
                          ))}
                          {analytics.doctorStats.length === 0 && <tr><td colSpan="2" style={{ padding: '12px 0', color: 'var(--text-muted)' }}>No data</td></tr>}
                        </tbody>
                      </table>
                    </div>
                  )}
                </div>

                <div className="card">
                  <h3 style={{ marginBottom: '16px', color: 'var(--text-main)' }}>Recent Orders</h3>
                  {loadingCounts.orders ? <div className="shimmer" style={{ height: '150px', borderRadius: '8px' }}></div> : (
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                      {analytics.recentOrders.map(order => {
                        let itemCount = 0;
                        let amount = 0;
                        try {
                          const items = JSON.parse(order.itemsJson);
                          itemCount = items.length;
                          amount = items.reduce((sum, item) => sum + (item.price * item.quantity), 0);
                        } catch(e) {}
                        return (
                          <div key={order.id} style={{ display: 'flex', flexWrap: 'wrap', gap: '12px', justifyContent: 'space-between', alignItems: 'center', padding: '12px', border: '1px solid var(--border-color)', borderRadius: '8px' }}>
                            <div style={{ flex: '1 1 auto' }}>
                              <div style={{ fontWeight: '600', color: 'var(--text-main)' }}>Dr. {order.doctorName}</div>
                              <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>{new Date(order.createdAt).toLocaleString()}</div>
                            </div>
                            <div style={{ textAlign: 'right', flex: '0 0 auto' }}>
                              <div style={{ fontWeight: 'bold', color: 'var(--primary-dark)' }}>Rs {amount.toFixed(2)}</div>
                              <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>{itemCount} items</div>
                            </div>
                            <div style={{ flex: '0 0 auto' }}>
                              <span style={{ padding: '4px 8px', borderRadius: '4px', background: order.status === 'pending' ? '#fef3c7' : '#d1fae5', color: order.status === 'pending' ? '#d97706' : '#059669', fontSize: '0.8rem', fontWeight: '600', textTransform: 'capitalize', display: 'inline-block' }}>{order.status}</span>
                            </div>
                          </div>
                        );
                      })}
                      {analytics.recentOrders.length === 0 && <div style={{ color: 'var(--text-muted)' }}>No orders yet.</div>}
                    </div>
                  )}
                </div>
              </div>

              <div className="card" style={{ height: 'fit-content' }}>
                <h3 style={{ marginBottom: '16px', color: 'var(--text-main)' }}>Top Medicines</h3>
                {loadingCounts.orders ? <div className="shimmer" style={{ height: '200px', borderRadius: '8px' }}></div> : (
                  <div style={{ overflowX: 'auto' }}>
                    <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
                      <thead>
                        <tr style={{ borderBottom: '1px solid var(--border-color)' }}>
                          <th style={{ padding: '8px 0', color: 'var(--text-muted)' }}>Medicine</th>
                          <th style={{ padding: '8px 0', color: 'var(--text-muted)', textAlign: 'center' }}>Orders</th>
                          <th style={{ padding: '8px 0', color: 'var(--text-muted)', textAlign: 'right' }}>Qty Sold</th>
                        </tr>
                      </thead>
                      <tbody>
                        {analytics.topMedicines.map((med, idx) => (
                          <tr key={idx} style={{ borderBottom: '1px solid var(--border-color)' }}>
                            <td style={{ padding: '12px 0', fontWeight: '500' }}>
                              <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                                <span style={{ color: '#94a3b8', fontSize: '0.9rem', width: '20px' }}>#{idx + 1}</span>
                                {med.name}
                              </div>
                            </td>
                            <td style={{ padding: '12px 0', textAlign: 'center', color: '#475569' }}>{med.timesOrdered}x</td>
                            <td style={{ padding: '12px 0', textAlign: 'right', fontWeight: 'bold', color: 'var(--primary-dark)' }}>{med.totalQuantity}</td>
                          </tr>
                        ))}
                        {analytics.topMedicines.length === 0 && <tr><td colSpan="3" style={{ padding: '12px 0', color: 'var(--text-muted)' }}>No data</td></tr>}
                      </tbody>
                    </table>
                  </div>
                )}
              </div>
            </div>
          </>
        );
    }
  };

  return (
    <div className="app-container">
      {/* Sidebar */}
      <aside className="sidebar">
        <div className="sidebar-logo" style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <img src="/logo.svg" alt="DaimPharmacy Logo" style={{ width: '32px', height: '32px' }} />
          <h2>DaimPharmacy</h2>
        </div>
        <nav style={{ display: 'flex', flexDirection: 'column', flex: 1 }}>
          <a href="#" onClick={(e) => { e.preventDefault(); setActiveTab('overview'); }} className={`nav-item ${activeTab === 'overview' ? 'active' : ''}`}>Dashboard Overview</a>
          <a href="#" onClick={(e) => { e.preventDefault(); setActiveTab('users'); }} className={`nav-item ${activeTab === 'users' ? 'active' : ''}`}>Manage Users</a>
          <a href="#" onClick={(e) => { e.preventDefault(); setActiveTab('categories'); }} className={`nav-item ${activeTab === 'categories' ? 'active' : ''}`}>Categories</a>
          <a href="#" onClick={(e) => { e.preventDefault(); setActiveTab('medicines'); }} className={`nav-item ${activeTab === 'medicines' ? 'active' : ''}`}>Products & Inventory</a>
          <a href="#" onClick={(e) => { e.preventDefault(); setActiveTab('orders'); }} className={`nav-item ${activeTab === 'orders' ? 'active' : ''}`}>Orders</a>
          
          <div style={{ marginTop: 'auto' }}>
            <button 
              onClick={handleLogout} 
              className="btn" 
              style={{ width: '100%', background: 'rgba(239, 68, 68, 0.1)', color: '#ef4444', justifyContent: 'flex-start' }}
            >
              Sign Out
            </button>
          </div>
        </nav>
      </aside>

      {/* Main Content */}
      <main className="main-content">
        <header style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '32px' }}>
          <div>
            <h1 style={{ fontSize: '1.875rem', marginBottom: '4px', textTransform: 'capitalize' }}>
              {activeTab === 'overview' ? 'Overview' : activeTab}
            </h1>
            <p style={{ color: 'var(--text-muted)' }}>Manage your pharmacy data seamlessly.</p>
          </div>
        </header>

        {renderContent()}

        {/* Modals rendered with Suspense for code splitting */}
        <Suspense fallback={null}>
          {modalType === 'user' && <UserModal user={editingItem} onClose={closeModal} />}
          {modalType === 'category' && <CategoryModal category={editingItem} onClose={closeModal} />}
          {modalType === 'medicine' && <MedicineModal medicine={editingItem} onClose={closeModal} />}
        </Suspense>
      </main>
    </div>
  );
}

export default Dashboard;
