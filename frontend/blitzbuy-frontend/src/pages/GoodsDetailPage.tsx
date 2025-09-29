import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { 
  Typography, 
  Card, 
  Button, 
  Row, 
  Col, 
  Spin, 
  message, 
  Tag, 
  Input,
  Image,
  Space,
  Statistic,
  Divider
} from 'antd';
import { 
  ArrowLeftOutlined,
  ShoppingCartOutlined,
  ClockCircleOutlined,
  FireOutlined,
  EyeOutlined,
  ReloadOutlined,
  CheckCircleOutlined
} from '@ant-design/icons';
import { goodsAPI, flashSaleAPI } from '../services/api';
import { GoodsVo } from '../types';
import { getImageUrl, getFallbackImage } from '../utils/image';

const { Title, Text, Paragraph } = Typography;

interface FlashSaleStatus {
  status: number; // 0: not started, 1: in progress, 2: ended
  remainSeconds: number;
}

const GoodsDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [goods, setGoods] = useState<GoodsVo | null>(null);
  const [loading, setLoading] = useState(true);
  const [flashSaleStatus, setFlashSaleStatus] = useState<FlashSaleStatus>({ status: 0, remainSeconds: 0 });
  const [captcha, setCaptcha] = useState('');
  const [captchaUrl, setCaptchaUrl] = useState('');
  const [showCaptcha, setShowCaptcha] = useState(false);
  const [flashSalePath, setFlashSalePath] = useState('');
  const [flashSaleLoading, setFlashSaleLoading] = useState(false);
  const [hasPurchased, setHasPurchased] = useState(false);

  useEffect(() => {
    if (id) {
      fetchGoodsDetail();
      checkPurchaseStatus();
    }
  }, [id]);

  useEffect(() => {
    if (goods) {
      calculateFlashSaleStatus();
      const interval = setInterval(calculateFlashSaleStatus, 1000);
      return () => clearInterval(interval);
    }
  }, [goods]);

  const fetchGoodsDetail = async () => {
    try {
      setLoading(true);
      const response = await goodsAPI.getGoodsDetail(Number(id));
      if (response.code === 200) {
        setGoods(response.object as GoodsVo);
      } else {
        message.error(response.message || 'Failed to load goods detail');
        navigate('/goods');
      }
    } catch (error) {
      message.error('Failed to load goods detail');
      console.error('Error fetching goods detail:', error);
      navigate('/goods');
    } finally {
      setLoading(false);
    }
  };

  const checkPurchaseStatus = async () => {
    try {
      const response = await flashSaleAPI.checkPurchase(Number(id));
      if (response.code === 500501) { // REPEAT_PURCHASE
        setHasPurchased(true);
        // Clear any existing flash sale path since user already purchased
        setFlashSalePath('');
        setShowCaptcha(false);
      }
    } catch (error) {
      // Ignore errors for this check - it's not critical
    }
  };

  const calculateFlashSaleStatus = () => {
    if (!goods) return;

    const now = new Date();
    const startTime = new Date(goods.startTime);
    const endTime = new Date(goods.endTime);

    let status = 0;
    let remainSeconds = 0;

    if (now < startTime) {
      status = 0;
      remainSeconds = Math.floor((startTime.getTime() - now.getTime()) / 1000);
    } else if (now >= startTime && now <= endTime) {
      status = 1;
      remainSeconds = Math.floor((endTime.getTime() - now.getTime()) / 1000);
    } else {
      status = 2;
      remainSeconds = -1;
    }

    setFlashSaleStatus({ status, remainSeconds });
  };

  const formatTime = (seconds: number): string => {
    if (seconds < 0) return 'Ended';
    
    const days = Math.floor(seconds / (24 * 60 * 60));
    const hours = Math.floor((seconds % (24 * 60 * 60)) / (60 * 60));
    const minutes = Math.floor((seconds % (60 * 60)) / 60);
    const secs = seconds % 60;

    if (days > 0) {
      return `${days}d ${hours.toString().padStart(2, '0')}h ${minutes.toString().padStart(2, '0')}m ${secs.toString().padStart(2, '0')}s`;
    } else {
      return `${hours.toString().padStart(2, '0')}h ${minutes.toString().padStart(2, '0')}m ${secs.toString().padStart(2, '0')}s`;
    }
  };

  const getStatusInfo = () => {
    switch (flashSaleStatus.status) {
      case 0:
        return { text: 'Not Started', color: 'default', icon: <ClockCircleOutlined /> };
      case 1:
        return { text: 'Flash Sale Active', color: 'success', icon: <FireOutlined /> };
      case 2:
        return { text: 'Ended', color: 'error', icon: <ClockCircleOutlined /> };
      default:
        return { text: 'Unknown', color: 'default', icon: <ClockCircleOutlined /> };
    }
  };

  const handleGetCaptcha = async () => {
    if (!id) return;
    
    try {
      const captchaUrl = flashSaleAPI.getCaptcha(Number(id));
      setCaptchaUrl(captchaUrl);
      setShowCaptcha(true);
      setCaptcha(''); // Clear previous captcha input
      message.success('Captcha loaded');
    } catch (error) {
      message.error('Failed to load captcha');
    }
  };

  const handleVerifyCaptcha = async () => {
    if (!captcha.trim()) {
      message.error('Please enter captcha');
      return;
    }

    try {
      const response = await flashSaleAPI.getFlashSalePath(Number(id), captcha);
      
      if (response && response.code === 200) {
        setFlashSalePath(response.object as string);
        setShowCaptcha(false);
        message.success('Captcha verified! Ready to purchase.');
      } else {
        message.error(response?.message || 'Captcha verification failed');
        handleGetCaptcha(); // Refresh captcha
      }
    } catch (error: any) {
      message.error('Captcha verification failed');
      handleGetCaptcha(); // Refresh captcha
    }
  };

  const handleFlashSale = async () => {
    if (!flashSalePath) {
      message.error('Please verify captcha first');
      return;
    }

    // Check if user is logged in
    const userTicket = localStorage.getItem('userTicket');
    
    if (!userTicket) {
      message.error('Please login first');
      navigate('/login');
      return;
    }

    setFlashSaleLoading(true);
    try {
      const response = await flashSaleAPI.doFlashSale(flashSalePath, Number(id));
      
      if (response && response.code === 200) {
        message.success('Flash sale successful! Redirecting to order...');
        setHasPurchased(true); // Mark as purchased
        setTimeout(() => {
          navigate(`/order/${response.object}`);
        }, 1500);
      } else if (response && response.code === 500501) {
        // REPEAT_PURCHASE error
        message.error('You have already purchased this flash sale item. Limited to 1 per customer.');
        setHasPurchased(true);
      } else if (response && response.code === 500500) {
        // NO_STOCK error
        message.error('Sorry, this item is out of stock. Please try another item.');
      } else {
        message.error(response?.message || 'Flash sale failed');
      }
    } catch (error: any) {
      message.error('Flash sale failed');
    } finally {
      setFlashSaleLoading(false);
    }
  };

  if (loading) {
    return (
      <div style={{ 
        display: 'flex', 
        justifyContent: 'center', 
        alignItems: 'center', 
        minHeight: '50vh' 
      }}>
        <Spin size="large" />
      </div>
    );
  }

  if (!goods) {
    return (
      <div style={{ textAlign: 'center', padding: '60px' }}>
        <Title level={3}>Product not found</Title>
        <Button type="primary" onClick={() => navigate('/goods')}>
          Back to Products
        </Button>
      </div>
    );
  }

  const statusInfo = getStatusInfo();

  return (
    <div style={{ padding: '24px', backgroundColor: '#f5f5f5', minHeight: '100vh' }}>
      {/* Header */}
      <div style={{ marginBottom: '24px' }}>
        <Button 
          icon={<ArrowLeftOutlined />} 
          onClick={() => navigate('/goods')}
          style={{ marginBottom: '16px' }}
        >
          Back to Products
        </Button>
        
        <Title level={2} style={{ margin: 0, color: '#c9302c' }}>
          <FireOutlined /> Flash Sale Details
        </Title>
      </div>

      <Row gutter={[24, 24]}>
        {/* Product Image */}
        <Col xs={24} md={12}>
          <Card>
            <div style={{ textAlign: 'center' }}>
              <Image
                src={getImageUrl(goods.imageUrl)}
                alt={goods.name}
                style={{ 
                  width: '100%', 
                  maxWidth: '400px',
                  height: '400px',
                  objectFit: 'contain'
                }}
                fallback={getFallbackImage()}
              />
            </div>
          </Card>
        </Col>

        {/* Product Info */}
        <Col xs={24} md={12}>
          <Card>
            <Space direction="vertical" size="large" style={{ width: '100%' }}>
              {/* Product Name */}
              <div>
                <Title level={3} style={{ margin: 0 }}>{goods.name}</Title>
                <Paragraph type="secondary" style={{ marginTop: '8px' }}>
                  {goods.description || 'Premium quality product with amazing features'}
                </Paragraph>
              </div>

              {/* Pricing */}
              <div>
                <Row gutter={16}>
                  <Col span={12}>
                    <Statistic
                      title="Original Price"
                      value={goods.price}
                      prefix="$"
                      valueStyle={{ textDecoration: 'line-through', color: '#999' }}
                    />
                  </Col>
                  <Col span={12}>
                    <Statistic
                      title="Flash Sale Price"
                      value={goods.flashSalePrice}
                      prefix="$"
                      valueStyle={{ color: '#cf1322', fontSize: '24px' }}
                    />
                  </Col>
                </Row>
              </div>

              {/* Flash Sale Status */}
              <div>
                <div style={{ 
                  padding: '12px 16px',
                  backgroundColor: statusInfo.color === 'green' ? '#f6ffed' : 
                                 statusInfo.color === 'red' ? '#fff2f0' : '#fffbe6',
                  border: `1px solid ${statusInfo.color === 'green' ? '#b7eb8f' : 
                                   statusInfo.color === 'red' ? '#ffccc7' : '#ffe58f'}`,
                  borderRadius: '6px',
                  textAlign: 'center'
                }}>
                  <Tag 
                    color={statusInfo.color} 
                    icon={statusInfo.icon}
                    style={{ fontSize: '14px', padding: '4px 12px', marginBottom: '8px' }}
                  >
                    {statusInfo.text}
                  </Tag>
                  
                  {flashSaleStatus.status !== 2 && (
                    <div>
                      <Text strong style={{ fontSize: '16px', color: statusInfo.color === 'green' ? '#52c41a' : '#cf1322' }}>
                        <ClockCircleOutlined /> {formatTime(flashSaleStatus.remainSeconds)}
                      </Text>
                    </div>
                  )}
                </div>
              </div>

              {/* Stock Information */}
              <div>
                <Statistic
                  title="Flash Sale Stock"
                  value={goods.flashSaleStock}
                  prefix={<ShoppingCartOutlined />}
                  suffix="items left"
                  valueStyle={{ 
                    color: goods.flashSaleStock === 0 ? '#ff4d4f' : 
                           goods.flashSaleStock <= 5 ? '#faad14' : '#52c41a' 
                  }}
                />
                {goods.flashSaleStock === 0 && (
                  <div style={{ 
                    textAlign: 'center', 
                    marginTop: '8px',
                    padding: '8px',
                    backgroundColor: '#fff2f0',
                    border: '1px solid #ffccc7',
                    borderRadius: '4px'
                  }}>
                    <Text style={{ color: '#ff4d4f', fontSize: '14px' }}>
                      ⚠️ Out of Stock
                    </Text>
                  </div>
                )}
                {goods.flashSaleStock > 0 && goods.flashSaleStock <= 5 && (
                  <div style={{ 
                    textAlign: 'center', 
                    marginTop: '8px',
                    padding: '8px',
                    backgroundColor: '#fffbe6',
                    border: '1px solid #ffe58f',
                    borderRadius: '4px'
                  }}>
                    <Text style={{ color: '#faad14', fontSize: '14px' }}>
                      ⚡ Low Stock - Hurry Up!
                    </Text>
                  </div>
                )}
              </div>

              <Divider />

              {/* Flash Sale Actions */}
              <div>
                {flashSaleStatus.status === 0 && (
                  <div style={{ textAlign: 'center' }}>
                    <Text type="secondary">
                      Flash sale has not started yet. Come back later!
                    </Text>
                  </div>
                )}

                {flashSaleStatus.status === 1 && (
                  <Space direction="vertical" size="middle" style={{ width: '100%' }}>
                    {goods?.flashSaleStock === 0 ? (
                      <div style={{ 
                        textAlign: 'center', 
                        padding: '24px',
                        backgroundColor: '#f5f5f5',
                        border: '2px solid #d9d9d9',
                        borderRadius: '8px',
                        boxShadow: '0 2px 8px rgba(0, 0, 0, 0.1)'
                      }}>
                        <Text style={{ color: '#8c8c8c', fontSize: '28px', marginBottom: '16px' }}>
                          📦
                        </Text>
                        <div>
                          <Text strong style={{ color: '#8c8c8c', fontSize: '18px', display: 'block', marginBottom: '8px' }}>
                            Out of Stock
                          </Text>
                          <Text type="secondary" style={{ fontSize: '14px' }}>
                            This flash sale item is currently out of stock. Please check back later.
                          </Text>
                        </div>
                      </div>
                    ) : hasPurchased ? (
                      <div style={{ 
                        textAlign: 'center', 
                        padding: '24px',
                        backgroundColor: '#fff2f0',
                        border: '2px solid #ff4d4f',
                        borderRadius: '8px',
                        boxShadow: '0 2px 8px rgba(255, 77, 79, 0.15)'
                      }}>
                        <CheckCircleOutlined style={{ color: '#ff4d4f', fontSize: '28px', marginBottom: '16px' }} />
                        <div>
                          <Text strong style={{ color: '#ff4d4f', fontSize: '18px', display: 'block', marginBottom: '8px' }}>
                            ✅ Already Purchased
                          </Text>
                          <Text type="secondary" style={{ fontSize: '14px' }}>
                            You have already purchased this flash sale item. Limited to 1 per customer.
                          </Text>
                        </div>
                      </div>
                    ) : !showCaptcha && !flashSalePath && (
                      <Button 
                        type="primary" 
                        size="large" 
                        block
                        icon={<EyeOutlined />}
                        onClick={handleGetCaptcha}
                      >
                        Get Captcha & Start Flash Sale
                      </Button>
                    )}

                    {showCaptcha && (
                      <div style={{ textAlign: 'center' }}>
                        <Text strong>Please verify the captcha:</Text>
                        <div style={{ margin: '16px 0' }}>
                          <Image
                            src={captchaUrl}
                            alt="Captcha"
                            style={{ border: '1px solid #d9d9d9', borderRadius: '4px' }}
                          />
                          <Button 
                            icon={<ReloadOutlined />} 
                            onClick={handleGetCaptcha}
                            style={{ marginLeft: '8px' }}
                          >
                            Refresh
                          </Button>
                        </div>
                        <Input
                          placeholder="Enter captcha"
                          value={captcha}
                          onChange={(e) => setCaptcha(e.target.value)}
                          style={{ marginBottom: '12px' }}
                          onPressEnter={handleVerifyCaptcha}
                        />
                        <Button 
                          type="primary" 
                          onClick={handleVerifyCaptcha}
                          block
                        >
                          Verify Captcha
                        </Button>
                      </div>
                    )}

                    {flashSalePath && !hasPurchased && (
                      <div>
                        <div style={{ 
                          textAlign: 'center', 
                          marginBottom: '16px',
                          padding: '16px',
                          backgroundColor: '#f6ffed',
                          border: '2px solid #52c41a',
                          borderRadius: '8px',
                          boxShadow: '0 2px 8px rgba(82, 196, 26, 0.15)'
                        }}>
                          <CheckCircleOutlined style={{ color: '#52c41a', fontSize: '24px', marginBottom: '12px' }} />
                          <div>
                            <Text strong style={{ color: '#52c41a', fontSize: '18px', display: 'block', marginBottom: '8px' }}>
                              🚀 Flash Sale Ready!
                            </Text>
                            <Text type="secondary" style={{ fontSize: '14px' }}>
                              You can now proceed with the purchase
                            </Text>
                          </div>
                        </div>
                        <Button 
                          type="primary" 
                          size="large" 
                          block
                          loading={flashSaleLoading}
                          icon={<ShoppingCartOutlined />}
                          onClick={handleFlashSale}
                          style={{ 
                            backgroundColor: '#cf1322',
                            borderColor: '#cf1322',
                            height: '48px',
                            fontSize: '16px',
                            fontWeight: 'bold'
                          }}
                        >
                          {flashSaleLoading ? 'Processing...' : 'Buy Now - Flash Sale!'}
                        </Button>
                      </div>
                    )}
                  </Space>
                )}

                {flashSaleStatus.status === 2 && (
                  <div style={{ textAlign: 'center' }}>
                    <Text type="secondary">
                      Flash sale has ended. Thank you for your interest!
                    </Text>
                  </div>
                )}
              </div>
            </Space>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default GoodsDetailPage;
