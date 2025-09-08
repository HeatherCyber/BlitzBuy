# 📚 BlitzBuy API Documentation

## 🔐 Authentication
All endpoints require session-based authentication via `/login/doLogin`.

---

## 📋 API Endpoints

### 🔑 LoginController
| Method | Endpoint | Description | Parameters |
|--------|----------|-------------|------------|
| GET | `/login/toLogin` | Login page | - |
| POST | `/login/doLogin` | User authentication | `mobile`, `password` |

### 🛍️ GoodsController
| Method | Endpoint | Description | Parameters |
|--------|----------|-------------|------------|
| GET | `/goods/toList` | Product list (cached) | - |
| GET | `/goods/toDetail/{goodsId}` | Product details (cached) | `goodsId` |

### ⚡ FlashSaleController
| Method | Endpoint | Description | Parameters | Rate Limit |
|--------|----------|-------------|------------|------------|
| GET | `/flashSale/getCaptcha` | Generate captcha image | - | - |
| GET | `/flashSale/getFlashSalePath` | Get secure path | `captcha` | 5/5s |
| POST | `/flashSale/doFlashSale/{path}` | Execute flash sale | `path`, `goodsId` | - |
| GET | `/flashSale/getFlashSaleResult` | Get result status | `goodsId` | 20/1s |

### 👤 UserController
| Method | Endpoint | Description | Parameters |
|--------|----------|-------------|------------|
| GET | `/user/info` | User information | - |
| POST | `/user/updatePassword` | Update password | `password`, `mobile` |

---

## 📊 Response Format

### Success
```json
{
  "code": 200,
  "message": "SUCCESS",
  "object": {data}
}
```

### Error
```json
{
  "code": 500210,
  "message": "Invalid user ID or password.",
  "object": null
}
```

**Common Error Codes:**
- `500210`: Login error
- `500211`: Invalid mobile format  
- `500500`: No stock
- `500501`: Repeat purchase
- `500507`: Request too frequent

---

## 🚀 Flash Sale Flow

1. **Login**: `POST /login/doLogin`
2. **Get Captcha**: `GET /flashSale/getCaptcha`
3. **Get Path**: `GET /flashSale/getFlashSalePath?captcha=XXX`
4. **Execute**: `POST /flashSale/doFlashSale/{path}`
5. **Check Result**: `GET /flashSale/getFlashSaleResult?goodsId=X`

---

## 🔒 Security Features
- **Rate Limiting**: Path generation (5/5s), Result polling (20/1s)
- **Captcha**: Required for flash sale path generation
- **Distributed Locking**: Redis-based inventory consistency
- **Session Management**: Redis-backed distributed sessions
