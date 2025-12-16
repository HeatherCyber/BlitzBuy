import React, { useState, useEffect } from 'react';
import { 
  Typography, 
  Button, 
  Card, 
  Row, 
  Col, 
  Spin, 
  message, 
  Tag, 
  Space,
  Statistic,
  Image
} from 'antd';
import { 
  LogoutOutlined, 
  ShoppingCartOutlined, 
  ClockCircleOutlined,
  FireOutlined,
  EyeOutlined
} from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { SessionManager } from '../utils/session';
import { goodsAPI } from '../services/api';
import { GoodsVo } from '../types';
import { getImageUrl, getFallbackImage } from '../utils/image';

const { Title, Text } = Typography;

const GoodsListPage: React.FC = () => {
  const navigate = useNavigate();
  const [goodsList, setGoodsList] = useState<GoodsVo[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchGoodsList();
  }, []);

  const fetchGoodsList = async () => {
    try {
      setLoading(true);
      const response = await goodsAPI.getGoodsList();
      if (response.code === 200) {
        const goods = response.object as GoodsVo[];
        console.log('Fetched goods:', goods);
        // Debug: Log image URLs
        goods.forEach(good => {
          console.log(`Good ${good.id} (${good.name}): imageUrl = "${good.imageUrl}"`);
        });
        setGoodsList(goods);
      } else {
        message.error(response.message || 'Failed to load goods');
      }
    } catch (error) {
      message.error('Failed to load goods list');
      console.error('Error fetching goods:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    SessionManager.clearSession();
    navigate('/login');
  };

  const handleViewDetail = (goodsId: number) => {
    navigate(`/goods/${goodsId}`);
  };

  const getFlashSaleStatus = (goods: GoodsVo) => {
    const now = new Date();
    const startTime = new Date(goods.startTime);
    const endTime = new Date(goods.endTime);

    if (now < startTime) {
      return { status: 'not-started', text: 'Not Started', color: 'default' };
    } else if (now >= startTime && now <= endTime) {
      return { status: 'in-progress', text: 'Flash Sale Active', color: 'success' };
    } else {
      return { status: 'ended', text: 'Ended', color: 'error' };
    }
  };

  const getRemainingTime = (goods: GoodsVo) => {
    const now = new Date();
    const startTime = new Date(goods.startTime);
    const endTime = new Date(goods.endTime);

    if (now < startTime) {
      const diff = startTime.getTime() - now.getTime();
      const days = Math.floor(diff / (1000 * 60 * 60 * 24));
      const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
      const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
      
      if (days > 0) {
        return `${days}d ${hours}h ${minutes}m until start`;
      } else {
        return `${hours}h ${minutes}m until start`;
      }
    } else if (now >= startTime && now <= endTime) {
      const diff = endTime.getTime() - now.getTime();
      const days = Math.floor(diff / (1000 * 60 * 60 * 24));
      const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
      const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
      
      if (days > 0) {
        return `${days}d ${hours}h ${minutes}m remaining`;
      } else {
        return `${hours}h ${minutes}m remaining`;
      }
    } else {
      return 'Flash sale ended';
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

  return (
    <div style={{ padding: '24px', backgroundColor: '#f5f5f5', minHeight: '100vh' }}>
      {/* Header */}
      <div style={{ 
        display: 'flex', 
        justifyContent: 'space-between', 
        alignItems: 'center', 
        marginBottom: '32px',
        backgroundColor: 'white',
        padding: '20px',
        borderRadius: '8px',
        boxShadow: '0 2px 8px rgba(0,0,0,0.1)'
      }}>
        <div>
          <Title level={2} style={{ margin: 0, color: '#c9302c' }}>
            <FireOutlined /> BlitzBuy Flash Sale
          </Title>
          <Text type="secondary">Limited time offers - Don't miss out!</Text>
        </div>
        <Button 
          type="primary" 
          danger 
          icon={<LogoutOutlined />} 
          onClick={handleLogout}
          size="large"
        >
          Logout
        </Button>
      </div>

      {/* Stats */}
      <Row gutter={16} style={{ marginBottom: '24px' }}>
        <Col span={8}>
          <Card>
            <Statistic
              title="Total Products"
              value={goodsList.length}
              prefix={<ShoppingCartOutlined />}
            />
          </Card>
        </Col>
        <Col span={8}>
          <Card>
            <Statistic
              title="Active Flash Sales"
              value={goodsList.filter(g => getFlashSaleStatus(g).status === 'in-progress').length}
              prefix={<FireOutlined />}
              valueStyle={{ color: '#cf1322' }}
            />
          </Card>
        </Col>
        <Col span={8}>
          <Card>
            <Statistic
              title="Total Stock"
              value={goodsList.reduce((sum, g) => sum + g.flashSaleStock, 0)}
              prefix={<ShoppingCartOutlined />}
            />
          </Card>
        </Col>
      </Row>

      {/* Goods Grid */}
      <Row gutter={[16, 16]}>
        {goodsList.map((goods) => {
          const flashSaleStatus = getFlashSaleStatus(goods);
          const remainingTime = getRemainingTime(goods);
          
          return (
            <Col xs={24} sm={12} md={8} lg={6} key={goods.id}>
              <Card
                hoverable
                cover={
                  <div style={{ height: '160px', overflow: 'hidden', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                    <Image
                      src={getImageUrl(goods.imageUrl)}
                      alt={goods.name}
                      style={{ 
                        width: '120px', 
                        height: '120px', 
                        objectFit: 'contain',
                        maxWidth: '100%',
                        maxHeight: '100%'
                      }}
                      fallback={getFallbackImage()}
                    />
                  </div>
                }
                actions={[
                  <Button 
                    type="primary" 
                    icon={<EyeOutlined />}
                    onClick={() => handleViewDetail(goods.id)}
                  >
                    View Details
                  </Button>
                ]}
              >
                <Card.Meta
                  title={
                    <div>
                      <Text strong style={{ fontSize: '16px' }}>{goods.name}</Text>
                      <br />
                      <Tag color={flashSaleStatus.color} style={{ marginTop: '8px' }}>
                        {flashSaleStatus.text}
                      </Tag>
                    </div>
                  }
                  description={
                    <div style={{ marginTop: '12px' }}>
                      <div style={{ marginBottom: '8px' }}>
                        <Text delete type="secondary">${goods.price}</Text>
                        <Text strong style={{ marginLeft: '8px', color: '#cf1322', fontSize: '18px' }}>
                          ${goods.flashSalePrice}
                        </Text>
                      </div>
                      
                      <div style={{ marginBottom: '8px' }}>
                        <Tag color="blue">
                          Stock: {goods.flashSaleStock}
                        </Tag>
                      </div>
                      
                      <div style={{ fontSize: '12px', color: '#666' }}>
                        <ClockCircleOutlined /> {remainingTime}
                      </div>
                    </div>
                  }
                />
              </Card>
            </Col>
          );
        })}
      </Row>

      {goodsList.length === 0 && (
        <div style={{ textAlign: 'center', padding: '60px' }}>
          <Title level={3}>No flash sale goods available</Title>
          <Text type="secondary">Check back later for amazing deals!</Text>
        </div>
      )}
    </div>
  );
};

export default GoodsListPage;
