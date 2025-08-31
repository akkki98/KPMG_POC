import React,{useContext,useEffect,useState} from 'react';
import { AuthContext } from '../auth/AuthContext';
import { apiRequest } from '../api/http';

export default function AdminPending(){
  const { token, isAdmin } = useContext(AuthContext);
  const [users,setUsers]=useState([]);
  const [error,setError]=useState('');
  const [loading,setLoading]=useState(false);
  const [actionMsg,setActionMsg]=useState('');

  const load=()=>{
    if(!token || !isAdmin) return;
    setLoading(true);
    apiRequest('/admin/pending',{},token)
      .then(data=>{ setUsers(data); setError(''); })
      .catch(e=>setError('Load failed: '+e.message))
      .finally(()=>setLoading(false));
  };
  useEffect(()=>{ load(); },[token,isAdmin]);

  const approve=async id=>{
    try{ await apiRequest(`/admin/approve/${id}`,{method:'POST'},token); setUsers(u=>u.filter(x=>x.id!==id)); setActionMsg('Approved'); }
    catch(e){ alert('Approve failed: '+e.message); }
  };
  const reject=async id=>{
    try{ await apiRequest(`/admin/reject/${id}`,{method:'POST'},token); setUsers(u=>u.filter(x=>x.id!==id)); setActionMsg('Rejected'); }
    catch(e){ alert('Reject failed: '+e.message); }
  };

  if(!isAdmin) return <div className="panel"><h2>Admin</h2><p>Admin access required.</p></div>;
  return (
    <div className="panel">
      <h2>Pending Users</h2>
      {loading && <div>Loading...</div>}
      {error && <div className="error">{error}</div>}
      {actionMsg && <div className="msg">{actionMsg}</div>}
      <ul>
        {users.map(u=> <li key={u.id}>{u.email} ({u.name})
          <button className="btn" onClick={()=>approve(u.id)}>Approve</button>
          <button className="btn outline" onClick={()=>reject(u.id)}>Reject</button>
        </li>)}
      </ul>
      {!loading && users.length===0 && <div>No pending users.</div>}
    </div>
  );
}
