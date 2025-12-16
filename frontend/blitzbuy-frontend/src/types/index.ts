// User types
export interface User {
  id: number;
  nickname: string;
  mobile: string;
  password: string;
  salt: string;
  head: string;
  registerDate: string;
  lastLoginDate: string;
  loginCount: number;
}

// Goods types
export interface Goods {
  id: number;
  name: string;
  imageUrl: string;
  description: string;
  price: number;
  stock: number;
  status: number;
  createTime: string;
  updateTime: string;
}

export interface GoodsVo extends Goods {
  flashSalePrice: number;
  flashSaleStock: number;
  startTime: string;
  endTime: string;
}

// Order types
export interface Order {
  id: number;
  userId: number;
  goodsId: number;
  deliveryAddress: string;
  goodsName: string;
  goodsCount: number;
  goodsPrice: number;
  orderChannel: number;
  orderStatus: number;
  createTime: string;
  payTime: string;
}

export interface FlashSaleOrder {
  id: number;
  userId: number;
  orderId: number;
  goodsId: number;
}

// API Response types
export interface RespBean {
  code: number;
  message: string;
  object: any;
}

// Login types
export interface LoginForm {
  mobile: string;
  password: string;
}

// Flash Sale types
export interface FlashSaleForm {
  goodsId: number;
  captcha: string;
}
