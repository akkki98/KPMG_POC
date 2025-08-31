import React,{useContext, useState, useEffect} from 'react';
import {Routes,Route,Link} from 'react-router-dom';
import RegisterForm from './components/RegisterForm';
import LoginForm from './components/LoginForm';
import AdminPending from './components/AdminPending';
import Home from './components/Home';
import { AuthContext } from './auth/AuthContext';

export default function App(){
  const { token, email, logout, isAdmin } = useContext(AuthContext);
  const [dark,setDark] = useState(()=>localStorage.getItem('theme')==='dark');
  useEffect(()=>{
    const cls = dark ? 'theme-dark' : '';
    document.documentElement.className = cls;
    localStorage.setItem('theme', dark ? 'dark':'light');
  },[dark]);
  return (
    <div className="container">
      <nav className="nav">
        <Link to="/">Home</Link>
        <Link to="/register">Register</Link>
        <Link to="/login">Login</Link>
        {isAdmin && <Link to="/admin/pending">Pending</Link>}
        <button className="theme-toggle" title="Toggle theme" onClick={()=>setDark(d=>!d)}>{dark? '☀️':'🌙'}</button>
        {token && <button className="btn outline" onClick={logout}>Logout ({email})</button>}
      </nav>
      <Routes>
  <Route path="/" element={<Home/>} />
        <Route path="/register" element={<RegisterForm/>} />
        <Route path="/login" element={<LoginForm/>} />
        <Route path="/admin/pending" element={<AdminPending/>} />
      </Routes>
    </div>
  );
}
