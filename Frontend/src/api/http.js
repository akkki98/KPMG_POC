import { API_BASE_URL } from '../config';

export async function apiRequest(path, options = {}, token) {
  const headers = { 'Content-Type': 'application/json', ...(options.headers || {}) };
  if (token) headers['Authorization'] = `Bearer ${token}`;
  const res = await fetch(`${API_BASE_URL}${path}`, { ...options, headers });
  if (!res.ok) {
    let msg;
    try { msg = await res.json(); } catch { msg = await res.text(); }
    throw new Error(typeof msg === 'string' ? msg : (msg.message || res.status));
  }
  if (res.status === 204) return null;
  return res.json();
}
