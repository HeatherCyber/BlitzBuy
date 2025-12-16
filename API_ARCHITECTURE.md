# BlitzBuy API 架构说明
## 前端迁移后的API结构分析

**当前分支**: `feature/react-frontend-migration`  
**迁移方式**: Thymeleaf (服务端渲染) → React + TypeScript (前后端分离)

---

## 📋 目录
1. [API架构概览](#api架构概览)
2. [新增RESTful API端点](#新增restful-api端点)
3. [保留的传统端点](#保留的传统端点)
4. [API对比表](#api对比表)
5. [前后端交互流程](#前后端交互流程)

---

## 1. API架构概览

### 1.1 架构模式
```
┌─────────────────────────────────────────────────────────┐
│                    前端迁移策略                          │
├─────────────────────────────────────────────────────────┤
│  新增 RESTful API Controllers  (推荐使用)                │
│  ├── /api/v1/auth/**           认证相关                  │
│  ├── /api/v1/goods/**          商品相关                  │
│  ├── /api/v1/flash-sale/**     秒杀相关                  │
│  └── /api/v1/orders/**         订单相关                  │
├─────────────────────────────────────────────────────────┤
│  保留 传统 Controllers (兼容性/特殊功能)                  │
│  ├── /login/**                 登录页面（已废弃）         │
│  ├── /goods/**                 商品页面（已废弃）         │
│  ├── /flashSale/**             秒杀功能（部分保留）       │
│  └── /order/**                 订单页面（已废弃）         │
└─────────────────────────────────────────────────────────┘
```

### 1.2 控制器分类

| 类型 | 注解 | 返回类型 | 使用场景 |
|------|------|---------|----------|
| **RESTful API** | `@RestController` | JSON (`RespBean`) | React前端调用 |
| **传统MVC** | `@Controller` | HTML (Thymeleaf) | 已废弃/兼容 |

---

## 2. 新增RESTful API端点

### 2.1 认证模块 (`AuthController`)
**基础路径**: `/api/v1/auth`

| HTTP方法 | 端点 | 功能 | 请求参数 | 响应 |
|---------|------|------|---------|------|
| POST | `/login` | 用户登录 | `LoginVo` (mobile, password) | `RespBean` (ticket) |
| GET | `/me` | 获取当前用户信息 | Cookie中的userTicket | `RespBean` (User对象) |
| POST | `/logout` | 用户登出 | - | `RespBean` |

**代码位置**: `src/main/java/com/example/blitzbuy/controller/AuthController.java`

**使用示例**:
```typescript
// React前端调用
const response = await authAPI.login({
  mobile: "13500001001",
  password: hashedPassword
});
```

---

### 2.2 商品模块 (`GoodsApiController`)
**基础路径**: `/api/v1/goods`

| HTTP方法 | 端点 | 功能 | 请求参数 | 响应 |
|---------|------|------|---------|------|
| GET | `/` | 获取商品列表 | - | `RespBean` (List&lt;GoodsVo&gt;) |
| GET | `/{id}` | 获取商品详情 | `id`: 商品ID | `RespBean` (GoodsVo) |

**代码位置**: `src/main/java/com/example/blitzbuy/controller/GoodsApiController.java`

**特点**:
- ✅ 需要用户认证（通过Cookie的userTicket）
- ✅ 返回JSON格式数据
- ✅ 包含商品基础信息 + 秒杀信息（GoodsVo）

---

### 2.3 秒杀模块 (`FlashSaleApiController`)
**基础路径**: `/api/v1/flash-sale`

| HTTP方法 | 端点 | 功能 | 请求参数 | 响应 |
|---------|------|------|---------|------|
| GET | `/captcha/{goodsId}` | 获取验证码URL | `goodsId`: 商品ID | `RespBean` (验证码URL) |
| POST | `/path` | 获取秒杀路径 | `goodsId`, `captcha` | `RespBean` (path) |
| GET | `/check-purchase/{goodsId}` | 检查是否已购买 | `goodsId`: 商品ID | `RespBean` |
| POST | `/purchase` | 执行秒杀购买 | `path`, `goodsId` | `RespBean` (orderId) |

**代码位置**: `src/main/java/com/example/blitzbuy/controller/FlashSaleApiController.java`

**注意**: 
- ⚠️ 实际秒杀执行仍使用传统端点 `/flashSale/doFlashSale/{path}`
- ⚠️ 验证码图片生成使用传统端点 `/flashSale/getCaptcha`

---

### 2.4 订单模块 (`OrderApiController`)
**基础路径**: `/api/v1/orders`

| HTTP方法 | 端点 | 功能 | 请求参数 | 响应 |
|---------|------|------|---------|------|
| GET | `/{id}` | 获取订单详情 | `id`: 订单ID | `RespBean` (Order) |
| GET | `/` | 获取用户订单列表 | - | `RespBean` (List&lt;Order&gt;) |
| POST | `/{id}/pay` | 处理订单支付 | `id`: 订单ID | `RespBean` |

**代码位置**: `src/main/java/com/example/blitzbuy/controller/OrderApiController.java`

**特点**:
- ✅ 自动验证订单归属权（订单必须属于当前用户）
- ✅ 支付状态更新（0-未支付 → 1-已支付）

---

## 3. 保留的传统端点

### 3.1 仍在使用的传统端点

| 控制器 | 端点 | 用途 | 状态 |
|--------|------|------|------|
| `FlashSaleController` | `/flashSale/getCaptcha` | 生成验证码图片 | ✅ **仍需使用** |
| `FlashSaleController` | `/flashSale/getFlashSalePath` | 获取秒杀路径 | ⚠️ 备用 |
| `FlashSaleController` | `/flashSale/doFlashSale/{path}` | 执行秒杀购买 | ✅ **仍需使用** |

**原因**:
- **验证码生成**: 返回图片流，不适合RESTful JSON格式
- **秒杀执行**: 复杂的分布式锁逻辑已实现，暂时保留

### 3.2 已废弃的传统端点

| 控制器 | 端点 | 原用途 | 替代方案 |
|--------|------|--------|---------|
| `LoginController` | `/login/toLogin` | 登录页面 | React LoginPage |
| `LoginController` | `/login/doLogin` | 登录处理 | `/api/v1/auth/login` |
| `GoodsController` | `/goods/toList` | 商品列表页面 | React GoodsListPage |
| `GoodsController` | `/goods/toDetail/{id}` | 商品详情页面 | React GoodsDetailPage |
| `OrderController` | `/order/detail` | 订单详情页面 | React OrderDetailPage |

---

## 4. API对比表

### 4.1 登录认证

| 功能 | 旧API (Thymeleaf) | 新API (React) |
|------|------------------|---------------|
| 登录页面 | `GET /login/toLogin` | React路由 `/login` |
| 登录处理 | `POST /login/doLogin` | `POST /api/v1/auth/login` |
| 获取用户信息 | - | `GET /api/v1/auth/me` |
| 登出 | - | `POST /api/v1/auth/logout` |

### 4.2 商品浏览

| 功能 | 旧API (Thymeleaf) | 新API (React) |
|------|------------------|---------------|
| 商品列表 | `GET /goods/toList` | `GET /api/v1/goods` |
| 商品详情 | `GET /goods/toDetail/{id}` | `GET /api/v1/goods/{id}` |

### 4.3 秒杀流程

| 步骤 | 旧API (Thymeleaf) | 新API (React) |
|------|------------------|---------------|
| 1. 获取验证码 | `GET /flashSale/getCaptcha` | **同左** (返回图片) |
| 2. 获取秒杀路径 | `GET /flashSale/getFlashSalePath` | `POST /api/v1/flash-sale/path` |
| 3. 执行秒杀 | `POST /flashSale/doFlashSale/{path}` | **同左** (复用逻辑) |
| 4. 检查结果 | - | `GET /api/v1/flash-sale/check-purchase/{goodsId}` |

### 4.4 订单管理

| 功能 | 旧API (Thymeleaf) | 新API (React) |
|------|------------------|---------------|
| 订单详情 | `GET /order/detail` | `GET /api/v1/orders/{id}` |
| 订单列表 | - | `GET /api/v1/orders` |
| 订单支付 | - | `POST /api/v1/orders/{id}/pay` |

---

## 5. 前后端交互流程

### 5.1 用户登录流程

```
React前端 (http://localhost:3000)
    │
    │ 1. 用户输入手机号+密码
    │    - 客户端MD5加密: MD5(MD5(明文) + 固定salt)
    │
    ├──> POST /api/v1/auth/login
    │    Body: { mobile: "13500001001", password: "hashed..." }
    │
Spring Boot后端 (http://localhost:9090)
    │
    │ 2. 验证密码: MD5(中间密码 + 用户salt) == DB密码
    │ 3. 生成UUID ticket
    │ 4. 存储到Redis: "userTicket:UUID" -> User对象
    │ 5. 设置Cookie: userTicket=UUID
    │
    └──> RespBean { code: 200, object: "ticket-uuid" }
         │
         │ 6. React保存ticket到SessionStorage
         │ 7. 后续请求自动携带Cookie
         │
         └──> 跳转到商品列表页面
```

### 5.2 秒杀完整流程

```
1. 用户浏览商品详情
   GET /api/v1/goods/{id}
   └──> 返回 GoodsVo (包含秒杀价格、库存、时间)

2. 点击"秒杀"按钮
   └──> 弹出验证码输入框
        │
        ├─ 显示验证码图片
        │  <img src="http://localhost:9090/flashSale/getCaptcha?goodsId=1&t=timestamp">
        │  (传统端点，返回JPEG图片流)

3. 用户输入验证码
   POST /api/v1/flash-sale/path (或使用传统端点)
   Body: { goodsId: 1, captcha: "ABC12" }
   └──> RespBean { code: 200, object: "md5-hashed-path" }

4. 执行秒杀
   POST /flashSale/doFlashSale/{path}?goodsId=1
   (传统端点，包含完整的分布式锁逻辑)
   │
   ├─ 检查登录状态
   ├─ 验证path有效性
   ├─ JVM本地内存检查库存
   ├─ Redis预减库存 (Redis分布式锁)
   ├─ 创建订单 (事务)
   └──> RespBean { code: 200, object: orderId }

5. 跳转到订单详情
   GET /api/v1/orders/{orderId}
   └──> 显示订单信息
```

### 5.3 会话管理机制

```
┌─────────────┐
│ React前端   │
│ localhost:  │
│    3000     │
└──────┬──────┘
       │ withCredentials: true (Axios配置)
       │ Cookie: userTicket=uuid-xxx
       │
       ↓
┌──────────────────────────────────────┐
│ Spring Boot后端 (localhost:9090)     │
├──────────────────────────────────────┤
│ 1. UserArgumentResolver 拦截器       │
│    ├─ 从Cookie提取userTicket          │
│    └─ 从Redis获取User对象              │
│                                       │
│ 2. Controller方法                    │
│    public RespBean xxx(User user) {  │
│       // user自动注入                 │
│    }                                  │
└──────────────────────────────────────┘
       │
       ↓
┌──────────────┐
│ Redis        │
│ 192.168.     │
│ 33.10:6379   │
├──────────────┤
│ Key: userTicket:uuid                 │
│ Value: User对象 (JSON序列化)          │
└──────────────┘
```

---

## 6. CORS配置

### 6.1 跨域设置
```java
// WebConfig.java
@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedOrigins("http://localhost:3000") // React前端
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true) // 允许Cookie传递
            .maxAge(3600);
}
```

**关键点**:
- ✅ 允许来自 `localhost:3000` 的请求
- ✅ 支持Cookie传递（`allowCredentials: true`）
- ✅ 支持所有常用HTTP方法

---

## 7. 混合使用说明

### 7.1 为什么保留部分传统端点？

| 端点 | 保留原因 |
|------|---------|
| `/flashSale/getCaptcha` | 返回图片流（JPEG），不适合JSON格式 |
| `/flashSale/doFlashSale/{path}` | 复杂的业务逻辑（分布式锁、Redis、RabbitMQ），避免重复开发 |
| `/flashSale/getFlashSalePath` | 有限流注解 `@AccessLimit`，已有完整实现 |

### 7.2 前端如何调用混合API？

**React前端调用策略**:

```typescript
// services/api.ts

// 1. RESTful API - 使用axios实例
const api = axios.create({
  baseURL: 'http://localhost:9090/api/v1',
  withCredentials: true
});

export const goodsAPI = {
  getGoodsList: () => api.get('/goods'),  // RESTful
};

// 2. 传统端点 - 直接使用完整URL
export const flashSaleAPI = {
  getCaptcha: (goodsId) => 
    `http://localhost:9090/flashSale/getCaptcha?goodsId=${goodsId}`,  // 传统
  
  doFlashSale: (path, goodsId) => 
    axios.post(`http://localhost:9090/flashSale/doFlashSale/${path}?goodsId=${goodsId}`, 
      null, 
      { withCredentials: true }
    ),  // 传统
};
```

---

## 8. 迁移路线图

### 8.1 已完成 ✅
- [x] 新增 AuthController (`/api/v1/auth/**`)
- [x] 新增 GoodsApiController (`/api/v1/goods/**`)
- [x] 新增 FlashSaleApiController (`/api/v1/flash-sale/**`)
- [x] 新增 OrderApiController (`/api/v1/orders/**`)
- [x] React前端完整页面 (Login, GoodsList, GoodsDetail, OrderDetail)
- [x] CORS跨域配置
- [x] Cookie-based会话管理

### 8.2 未来优化 🔮
- [ ] 将 `/flashSale/doFlashSale/{path}` 重构为纯RESTful
- [ ] 使用 JWT Token 替代 Cookie (可选)
- [ ] API网关层（统一路由、限流、监控）
- [ ] GraphQL API (可选)
- [ ] API版本管理策略

---

## 9. 最佳实践

### 9.1 前端调用建议
✅ **优先使用RESTful API** (`/api/v1/**`)  
⚠️ **必要时使用传统端点** (验证码图片、秒杀执行)  
❌ **避免使用已废弃端点** (如 `/login/toLogin`)

### 9.2 错误处理
```typescript
// React前端统一错误处理
api.interceptors.response.use(
  (response) => response.data,  // 返回RespBean
  (error) => {
    if (error.response?.status === 401) {
      // 未认证，跳转登录
      navigate('/login');
    }
    return Promise.reject(error);
  }
);
```

### 9.3 认证状态管理
```typescript
// 使用SessionStorage存储认证状态
export class SessionManager {
  static setUserTicket(ticket: string) {
    sessionStorage.setItem('userTicket', ticket);
  }
  
  static isLoggedIn(): boolean {
    return !!sessionStorage.getItem('userTicket');
  }
}
```

---

## 10. 总结

| 维度 | 说明 |
|------|------|
| **架构模式** | 前后端分离 (React + Spring Boot) |
| **API风格** | RESTful JSON (主) + 传统MVC (辅) |
| **认证方式** | Cookie-based Session (Redis存储) |
| **跨域处理** | CORS配置 (allowCredentials: true) |
| **迁移程度** | 约70%已迁移至RESTful API |
| **兼容性** | 保留关键传统端点确保功能完整 |

---

**文档生成时间**: October 13, 2025  
**当前分支**: feature/react-frontend-migration  
**维护人员**: Heather Wang


