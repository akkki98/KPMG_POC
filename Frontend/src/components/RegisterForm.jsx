import React, { useState } from 'react';
import { apiRequest } from '../api/http';

export default function RegisterForm() {
  const [form, setForm] = useState({ name: '', email: '', password: '' });
  const [msg, setMsg] = useState('');
  const [loading, setLoading] = useState(false);

  const submit = async (e) => {
    e.preventDefault();
    setLoading(true); setMsg('');
    try {
      await apiRequest('/register', { method: 'POST', body: JSON.stringify(form) });
      setMsg('Registered. Await admin approval.');
      setForm({ name: '', email: '', password: '' });
    } catch (e) {
      setMsg('Failed: ' + e.message);
    } finally { setLoading(false); }
  };

  return (
    <form onSubmit={submit} className="panel">
      <h2>Register</h2>
      <input placeholder="Name" value={form.name} onChange={e=>setForm({...form,name:e.target.value})} required />
      <input type="email" placeholder="Email" value={form.email} onChange={e=>setForm({...form,email:e.target.value})} required />
      <input type="password" placeholder="Password" value={form.password} onChange={e=>setForm({...form,password:e.target.value})} required />
      <button disabled={loading}>{loading ? 'Submitting...' : 'Register'}</button>
      {msg && <div className="msg">{msg}</div>}
    </form>
  );
}
