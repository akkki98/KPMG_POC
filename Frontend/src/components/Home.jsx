import React,{useContext} from 'react';
import { AuthContext } from '../auth/AuthContext';

export default function Home(){
  const { token, userName, email, userStatus, isAdmin } = useContext(AuthContext);
  if(!token){
    return (
      <div className="panel">
        <h2>Welcome</h2>
        <p>Please register or login to continue.</p>
      </div>
    );
  }
  return (
    <div className="panel">
      <h2>Welcome {userName || email}</h2>
      <p>Status: <strong>{userStatus}</strong>{userStatus==='PENDING' && ' (awaiting approval)'}.</p>
      {isAdmin && <p>You have administrator privileges.</p>}
    </div>
  );
}