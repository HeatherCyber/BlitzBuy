import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import './App.css';

// Import pages
import LoginPage from './pages/LoginPage';
import GoodsListPage from './pages/GoodsListPage';
import GoodsDetailPage from './pages/GoodsDetailPage';
import OrderDetailPage from './pages/OrderDetailPage';
import { SessionManager } from './utils/session';

function App() {
  // Check if user is logged in
  const isLoggedIn = SessionManager.isLoggedIn();

  return (
    <ConfigProvider>
      <Router>
        <div className="App">
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/goods" element={<GoodsListPage />} />
            <Route path="/goods/:id" element={<GoodsDetailPage />} />
            <Route path="/order/:id" element={<OrderDetailPage />} />
            <Route 
              path="/" 
              element={
                isLoggedIn ? 
                <Navigate to="/goods" replace /> : 
                <Navigate to="/login" replace />
              } 
            />
          </Routes>
        </div>
      </Router>
    </ConfigProvider>
  );
}

export default App;
