import axios from 'axios';
import { RespBean, LoginForm, GoodsVo } from '../types';
import { SessionManager } from '../utils/session';

// Create axios instance
const api = axios.create({
  baseURL: 'http://localhost:9090/api/v1', // Backend API base URL
  timeout: 10000,
  withCredentials: true, // Include cookies for session management
});

// Request interceptor
api.interceptors.request.use(
  (config) => {
    // Backend uses cookie-based authentication, no headers needed
    // The userTicket cookie is automatically sent with withCredentials: true
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor
api.interceptors.response.use(
  (response) => {
    return response.data;
  },
  (error) => {
    console.error('API Error:', error);
    return Promise.reject(error);
  }
);

// Auth API
export const authAPI = {
  login: (form: LoginForm): Promise<RespBean> => {
    return api.post('/auth/login', form);
  },
  getCurrentUser: (): Promise<RespBean> => {
    return api.get('/auth/me');
  },
  logout: async (): Promise<RespBean> => {
    try {
      const response = await api.post('/auth/logout');
      // Clear session data on logout
      SessionManager.clearSession();
      return response.data;
    } catch (error) {
      // Even if logout fails on server, clear local session
      SessionManager.clearSession();
      throw error;
    }
  },
};

// Goods API
export const goodsAPI = {
  getGoodsList: (): Promise<RespBean> => {
    return api.get('/goods');
  },
  getGoodsDetail: (goodsId: number): Promise<RespBean> => {
    return api.get(`/goods/${goodsId}`);
  },
};

// Flash Sale API
export const flashSaleAPI = {
  getCaptcha: (goodsId: number): string => {
    // Add timestamp to force refresh
    const timestamp = Date.now();
    return `http://localhost:9090/flashSale/getCaptcha?goodsId=${goodsId}&t=${timestamp}`;
  },
  getFlashSalePath: async (goodsId: number, captcha: string): Promise<RespBean> => {
    // Use the working endpoint from FlashSaleController
    const response = await axios.get(`http://localhost:9090/flashSale/getFlashSalePath`, {
      params: { goodsId, captcha },
      withCredentials: true
      // No Authorization header needed - backend uses cookies
    });
    return response.data;
  },
  doFlashSale: async (path: string, goodsId: number): Promise<RespBean> => {
    // Use existing endpoint for now
    // goodsId should be passed as a query parameter, not in request body
    const response = await axios.post(`http://localhost:9090/flashSale/doFlashSale/${path}?goodsId=${goodsId}`, null, {
      withCredentials: true
      // No Authorization header needed - backend uses cookies
    });
    return response.data; // Return the data part of the response
  },
  // For load testing
  doFlashSaleLoadTest: (goodsId: number): Promise<RespBean> => {
    return axios.post('http://localhost:9090/flashSale/doFlashSale/LOAD_TEST', { goodsId }, {
      withCredentials: true
    });
  },
  // Check if user has already purchased this item
  checkPurchase: (goodsId: number): Promise<RespBean> => {
    return api.get(`/flash-sale/check-purchase/${goodsId}`);
  },
};

// Order API
export const orderAPI = {
  getOrderDetail: (orderId: number): Promise<RespBean> => {
    return api.get(`/orders/${orderId}`);
  },
  getUserOrders: (): Promise<RespBean> => {
    return api.get('/orders');
  },
  processPayment: (orderId: number): Promise<RespBean> => {
    return api.post(`/orders/${orderId}/pay`);
  },
};

export default api;
