import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Form, Input, Button, Card, Typography, message, Select, Space } from 'antd';
import { UserOutlined, LockOutlined, GlobalOutlined } from '@ant-design/icons';
import { authAPI } from '../services/api';
import { LoginForm } from '../types';
import { MD5Util } from '../utils/md5';
import { SessionManager } from '../utils/session';

const { Title } = Typography;

const LoginPage: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [phoneValue, setPhoneValue] = useState('');
  const [countryCode, setCountryCode] = useState('+1');
  const navigate = useNavigate();

  // Country code options
  const countryOptions = [
    { value: '+86', label: '+86 🇨🇳', format: 'chinese' },
    { value: '+1', label: '+1 🇺🇸', format: 'us' }
  ];

  // Format phone number based on country code
  const formatPhoneNumber = (value: string, code: string) => {
    const digits = value.replace(/\D/g, '');
    
    if (code === '+86') {
      // Chinese format: 138-1234-5678 (11 digits)
      if (digits.length <= 3) return digits;
      if (digits.length <= 7) return `${digits.slice(0, 3)}-${digits.slice(3)}`;
      return `${digits.slice(0, 3)}-${digits.slice(3, 7)}-${digits.slice(7, 11)}`;
    } else if (code === '+1') {
      // US format: 201-234-5678 (10 digits, no leading 1)
      if (digits.length <= 3) return digits;
      if (digits.length <= 6) return `${digits.slice(0, 3)}-${digits.slice(3)}`;
      return `${digits.slice(0, 3)}-${digits.slice(3, 6)}-${digits.slice(6, 10)}`;
    }
    return digits;
  };

  const handlePhoneChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const formatted = formatPhoneNumber(e.target.value, countryCode);
    setPhoneValue(formatted);
  };

  const handleCountryCodeChange = (value: string) => {
    setCountryCode(value);
    // Re-format the phone number with the new country code
    const formatted = formatPhoneNumber(phoneValue.replace(/\D/g, ''), value);
    setPhoneValue(formatted);
  };

  const onFinish = async (values: LoginForm) => {
    setLoading(true);
    try {
      // Hash the password using MD5 before sending to backend
      // This matches the client-side hashing in the original Thymeleaf implementation
      const hashedPassword = MD5Util.inputPassToMidPass(values.password);
      
      // Remove formatting from phone number before sending to backend
      const cleanPhoneNumber = values.mobile.replace(/\D/g, '');
      
      // For US numbers, we need to add the leading 1 for the backend
      const finalPhoneNumber = countryCode === '+1' ? `1${cleanPhoneNumber}` : cleanPhoneNumber;
      
      const loginData = {
        mobile: finalPhoneNumber,
        password: hashedPassword
      };
      
      const response = await authAPI.login(loginData);
      
      if (response.code === 200) {
        message.success('Login successful!');
        // Store user ticket for session management
        if (response.object) {
          SessionManager.setUserTicket(response.object as string);
        }
        navigate('/goods');
      } else {
        // Handle specific error codes
        let errorMessage = response.message || 'Login failed';
        
        switch (response.code) {
          case 500210:
            errorMessage = 'Invalid phone number or password. Please check your credentials.';
            break;
          case 500211:
            errorMessage = 'Invalid phone number format. Please check your phone number.';
            break;
          case 500212:
            errorMessage = 'User does not exist. Please check your phone number or create an account.';
            break;
          case 500213:
            errorMessage = 'Invalid request parameters. Please try again.';
            break;
          default:
            errorMessage = response.message || 'Login failed. Please try again.';
        }
        
        message.error(errorMessage);
      }
    } catch (error: any) {
      // Handle network errors or other exceptions
      let errorMessage = 'Login failed. Please try again.';
      
      if (error.response) {
        // Server responded with error status
        const errorData = error.response.data;
        if (errorData && errorData.message) {
          errorMessage = errorData.message;
        }
      } else if (error.request) {
        // Network error
        errorMessage = 'Network error. Please check your connection.';
      }
      
      message.error(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{
      minHeight: '100vh',
      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      padding: '20px'
    }}>
      <Card
        style={{
          width: '100%',
          maxWidth: 400,
          boxShadow: '0 8px 32px rgba(0,0,0,0.1)',
          borderRadius: '16px',
          backdropFilter: 'blur(10px)',
          background: 'rgba(255,255,255,0.95)'
        }}
      >
        <div style={{ textAlign: 'center', marginBottom: '32px' }}>
          <Title level={2} style={{ color: '#1890ff', marginBottom: '8px' }}>
            BlitzBuy
          </Title>
          <Title level={4} style={{ color: '#666', fontWeight: 'normal' }}>
            Flash Sale Platform
          </Title>
        </div>

        <Form
          name="login"
          onFinish={onFinish}
          autoComplete="off"
          size="large"
        >
          <Form.Item
            name="mobile"
            rules={[
              { required: true, message: 'Please input your phone number!' },
              { 
                validator: (_, value) => {
                  // Skip validation if value is empty (required rule will handle it)
                  if (!value || !value.trim()) {
                    return Promise.resolve();
                  }
                  
                  const cleanValue = value.replace(/\D/g, '');
                  const expectedLength = countryCode === '+86' ? 11 : 10;
                  // For Chinese: 11 digits starting with 1[3-9]
                  // For US: 10 digits with valid area code (2-9) and exchange (2-9)
                  const pattern = countryCode === '+86' ? /^1[3-9]\d{9}$/ : /^[2-9]\d{2}[2-9]\d{6}$/;
                  
                  if (cleanValue.length === expectedLength && pattern.test(cleanValue)) {
                    return Promise.resolve();
                  }
                  return Promise.reject(new Error(`Please enter a valid ${countryCode === '+86' ? 'Chinese' : 'US'} phone number!`));
                }
              }
            ]}
          >
            <Space.Compact style={{ display: 'flex' }}>
              <Select
                value={countryCode}
                onChange={handleCountryCodeChange}
                style={{ width: '120px' }}
                options={countryOptions}
                prefix={<GlobalOutlined />}
              />
              <Input
                prefix={<UserOutlined />}
                placeholder=""
                style={{ flex: 1, borderRadius: '8px' }}
                value={phoneValue}
                onChange={handlePhoneChange}
                maxLength={countryCode === '+86' ? 13 : 12} // Allow for formatting characters
              />
            </Space.Compact>
          </Form.Item>

          <Form.Item
            name="password"
            rules={[
              { required: true, message: 'Please input your password!' },
              { min: 6, message: 'Password must be at least 6 characters!' }
            ]}
          >
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="Password"
              style={{ borderRadius: '8px' }}
            />
          </Form.Item>

          <Form.Item style={{ marginBottom: '16px' }}>
            <Button
              type="primary"
              htmlType="submit"
              loading={loading}
              block
              style={{
                height: '48px',
                borderRadius: '8px',
                fontSize: '16px',
                fontWeight: 'bold'
              }}
            >
              Log In
            </Button>
          </Form.Item>
        </Form>

        <div style={{ textAlign: 'center', color: '#999', fontSize: '14px' }}>
          <p>Demo Accounts:</p>
          <p>🇨🇳 +86: 135-0000-1001, password: 123456</p>
          <p>🇺🇸 +1: 201-200-1000, password: 123456</p>
          <p style={{ marginTop: '8px', fontSize: '12px' }}>
            Password will be hashed client-side using MD5 before transmission
          </p>
        </div>

        {/* Sign Up Button */}
        <div style={{ marginTop: '20px', textAlign: 'center' }}>
          <Button
            type="default"
            size="large"
            style={{
              height: '48px',
              borderRadius: '8px',
              fontSize: '16px',
              fontWeight: 'bold',
              border: '2px solid #1890ff',
              color: '#1890ff',
              background: 'transparent'
            }}
            onClick={() => {
              // TODO: Implement sign up functionality
              message.info('Sign up functionality coming soon!');
            }}
          >
            Sign Up
          </Button>
        </div>
      </Card>
    </div>
  );
};

export default LoginPage;
