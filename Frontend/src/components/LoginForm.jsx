import React, { useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { AuthContext } from '../auth/AuthContext';

export default function LoginForm() {
  const { login } = useContext(AuthContext);
  const navigate = useNavigate();
  const [form,setForm]=useState({email:'',password:''});
  const [msg,setMsg]=useState('');
  const [loading,setLoading]=useState(false);

  const submit=async e=>{
    e.preventDefault(); setMsg(''); setLoading(true);
  try{ await login(form.email, form.password); setMsg('Logged in'); navigate('/'); }
    catch(err){ setMsg('Login failed'); }
    finally{ setLoading(false); }
  };

  return (
    <form onSubmit={submit} className="panel">
      <h2>Login</h2>
      <input type="email" placeholder="Email" value={form.email} onChange={e=>setForm({...form,email:e.target.value})} required />
      <input type="password" placeholder="Password" value={form.password} onChange={e=>setForm({...form,password:e.target.value})} required />
      <button disabled={loading}>{loading?'Logging in...':'Login'}</button>
      {msg && <div className="msg">{msg}</div>}
    </form>
  );
}
