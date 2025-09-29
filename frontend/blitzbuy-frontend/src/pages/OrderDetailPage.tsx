import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, Descriptions, Button, message, Spin, Tag, Typography, Space, Divider } from 'antd';
import { ArrowLeftOutlined, ShoppingCartOutlined, CheckCircleOutlined, ClockCircleOutlined } from '@ant-design/icons';
import { orderAPI } from '../services/api';
import { Order } from '../types';

const { Title, Text } = Typography;

const OrderDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [order, setOrder] = useState<Order | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (id) {
      fetchOrderDetail();
    }
  }, [id]);

  const fetchOrderDetail = async () => {
    try {
      setLoading(true);
      const response = await orderAPI.getOrderDetail(Number(id));
      
      if (response.code === 200) {
        setOrder(response.object as Order);
      } else {
        message.error(response.message || 'Failed to fetch order details');
        navigate('/goods');
      }
    } catch (error) {
      console.error('Error fetching order detail:', error);
      message.error('Failed to fetch order details');
      navigate('/goods');
    } finally {
      setLoading(false);
    }
  };

  const handleBackToGoods = () => {
    navigate('/goods');
  };

  const handlePayment = async (orderId: number) => {
    try {
      message.loading('Processing payment...', 0);
      
      // Call the payment API
      const response = await orderAPI.processPayment(orderId);
      
      if (response.code === 200) {
        message.destroy(); // Clear loading message
        message.success('Payment successful! Order status updated.');
        
        // Refresh the order details to show updated status
        fetchOrderDetail();
      } else {
        message.destroy(); // Clear loading message
        message.error(response.message || 'Payment failed');
      }
      
    } catch (error: any) {
      message.destroy(); // Clear loading message
      console.error('Payment error:', error);
      message.error('Payment failed. Please try again.');
    }
  };

  const getOrderStatusInfo = (status: number) => {
    switch (status) {
      case 0:
        return { text: 'Pending Payment', color: 'orange', icon: <ClockCircleOutlined /> };
      case 1:
        return { text: 'Paid', color: 'blue', icon: <CheckCircleOutlined /> };
      case 2:
        return { text: 'Shipped', color: 'purple', icon: <CheckCircleOutlined /> };
      case 3:
        return { text: 'Delivered', color: 'green', icon: <CheckCircleOutlined /> };
      default:
        return { text: 'Unknown', color: 'default', icon: <ClockCircleOutlined /> };
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    });
  };

  if (loading) {
    return (
      <div style={{ 
        display: 'flex', 
        justifyContent: 'center', 
        alignItems: 'center', 
        minHeight: '60vh' 
      }}>
        <Spin size="large" />
      </div>
    );
  }

  if (!order) {
    return (
      <div style={{ 
        display: 'flex', 
        justifyContent: 'center', 
        alignItems: 'center', 
        minHeight: '60vh',
        flexDirection: 'column'
      }}>
        <Title level={3}>Order Not Found</Title>
        <Button type="primary" onClick={handleBackToGoods}>
          Back to Goods
        </Button>
      </div>
    );
  }

  const statusInfo = getOrderStatusInfo(order.orderStatus);

  return (
    <div style={{ padding: '24px', maxWidth: '800px', margin: '0 auto' }}>
      <Space direction="vertical" size="large" style={{ width: '100%' }}>
        {/* Header */}
        <Card>
          <div style={{ marginBottom: '24px' }}>
            <Button 
              type="text"
              onClick={handleBackToGoods}
              style={{ 
                color: '#1890ff',
                fontSize: '16px',
                fontWeight: '500',
                padding: '8px 16px',
                height: 'auto',
                marginBottom: '16px'
              }}
            >
              ← Back to Goods
            </Button>
            <div style={{ textAlign: 'center' }}>
              <Title level={2} style={{ margin: 0, color: '#262626' }}>
                Order Details
              </Title>
            </div>
          </div>
          
          <div style={{ textAlign: 'center', marginBottom: '24px' }}>
            {order.orderStatus === 0 ? (
              <>
                <ClockCircleOutlined style={{ fontSize: '48px', color: '#faad14', marginBottom: '16px' }} />
                <Title level={3} style={{ color: '#faad14', margin: 0 }}>
                  Order Placed - Payment Required
                </Title>
                <Text type="secondary">
                  Complete your payment to confirm the order
                </Text>
              </>
            ) : (
              <>
                <CheckCircleOutlined style={{ fontSize: '48px', color: '#52c41a', marginBottom: '16px' }} />
                <Title level={3} style={{ color: '#52c41a', margin: 0 }}>
                  Flash Sale Successful!
                </Title>
                <Text type="secondary">
                  Your order has been placed successfully
                </Text>
              </>
            )}
          </div>
        </Card>

        {/* Order Information */}
        <Card title="Order Information">
          <Descriptions column={1} bordered>
            <Descriptions.Item label="Order ID">
              <Text strong>{order.id}</Text>
            </Descriptions.Item>
            <Descriptions.Item label="Order Status">
              <Tag color={statusInfo.color} icon={statusInfo.icon}>
                {statusInfo.text}
              </Tag>
            </Descriptions.Item>
            <Descriptions.Item label="Goods Name">
              <Text strong>{order.goodsName}</Text>
            </Descriptions.Item>
            <Descriptions.Item label="Quantity">
              {order.goodsCount} item(s)
            </Descriptions.Item>
            <Descriptions.Item label="Unit Price">
              <Text strong style={{ color: '#cf1322' }}>
                ${order.goodsPrice?.toFixed(2)}
              </Text>
            </Descriptions.Item>
            <Descriptions.Item label="Total Amount">
              <Text strong style={{ color: '#cf1322', fontSize: '18px' }}>
                ${((order.goodsPrice || 0) * (order.goodsCount || 1)).toFixed(2)}
              </Text>
            </Descriptions.Item>
            <Descriptions.Item label="Order Channel">
              {order.orderChannel === 1 ? (
                <Tag color="red" icon={<ShoppingCartOutlined />}>
                  Flash Sale
                </Tag>
              ) : (
                <Tag>Regular Order</Tag>
              )}
            </Descriptions.Item>
            <Descriptions.Item label="Order Date">
              {formatDate(order.createTime || new Date().toISOString())}
            </Descriptions.Item>
            {order.payTime && (
              <Descriptions.Item label="Payment Date">
                {formatDate(order.payTime)}
              </Descriptions.Item>
            )}
          </Descriptions>
        </Card>

        {/* Delivery Information */}
        <Card title="Delivery Information">
          <Descriptions column={1} bordered>
            <Descriptions.Item label="Delivery Address">
              <Text>{order.deliveryAddress || 'No address provided'}</Text>
            </Descriptions.Item>
          </Descriptions>
        </Card>

        {/* Action Buttons */}
        <Card>
          <Space size="middle">
            {order.orderStatus === 0 ? (
              <Button 
                type="primary" 
                size="large" 
                style={{ backgroundColor: '#52c41a', borderColor: '#52c41a' }}
                onClick={() => handlePayment(order.id)}
              >
                💳 Pay Now - ${((order.goodsPrice || 0) * (order.goodsCount || 1)).toFixed(2)}
              </Button>
            ) : (
              <Button type="primary" size="large" onClick={handleBackToGoods}>
                Continue Shopping
              </Button>
            )}
            <Button size="large">
              Track Order
            </Button>
            <Button size="large" onClick={handleBackToGoods}>
              Back to Goods
            </Button>
          </Space>
        </Card>
      </Space>
    </div>
  );
};

export default OrderDetailPage;