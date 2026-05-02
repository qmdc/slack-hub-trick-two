import React, { useState, useEffect, useRef } from 'react';
import { Button, Input, Layout, List, message, Avatar, Popconfirm, Tooltip } from 'antd';
import { PlusOutlined, MessageOutlined, DeleteOutlined, SmileOutlined, FrownOutlined, FrownFilled, MehOutlined, CoffeeOutlined, HeartOutlined } from '@ant-design/icons';
import { getSessionList, createSession, deleteSession, getChatRecords, sendChatMessage } from '../../apis/modules/emotionChat';
import type { SessionListResponse, ChatRecordResponse, EmotionChatRequest } from '../../apis/modules/emotionChat';

const { Sider, Content } = Layout;

const emotionIcons: Record<string, React.ReactNode> = {
    happy: <SmileOutlined style={{ color: '#FFD700' }} />,
    sad: <FrownOutlined style={{ color: '#4169E1' }} />,
    angry: <FrownFilled style={{ color: '#FF4444' }} />,
    anxious: <MehOutlined style={{ color: '#FFA500' }} />,
    tired: <CoffeeOutlined style={{ color: '#8B4513' }} />,
    neutral: <HeartOutlined style={{ color: '#999999' }} />,
};

const emotionLabels: Record<string, string> = {
    happy: '开心',
    sad: '难过',
    angry: '生气',
    anxious: '焦虑',
    tired: '疲惫',
    neutral: '平静',
};

const EmotionChat: React.FC = () => {
    const [sessions, setSessions] = useState<SessionListResponse[]>([]);
    const [currentSession, setCurrentSession] = useState<SessionListResponse | null>(null);
    const [messages, setMessages] = useState<ChatRecordResponse[]>([]);
    const [inputValue, setInputValue] = useState('');
    const [loading, setLoading] = useState(false);
    const [sessionLoading, setSessionLoading] = useState(false);
    const messagesEndRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        loadSessions();
    }, []);

    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    }, [messages]);

    const loadSessions = async () => {
        try {
            setSessionLoading(true);
            const response = await getSessionList();
            if (response.code === 200) {
                setSessions(response.data || []);
            }
        } catch (error) {
            message.error('加载会话列表失败');
        } finally {
            setSessionLoading(false);
        }
    };

    const handleCreateSession = async () => {
        try {
            const response = await createSession();
            if (response.code === 200) {
                message.success('创建会话成功');
                loadSessions();
            }
        } catch (error) {
            message.error('创建会话失败');
        }
    };

    const handleDeleteSession = async (sessionId: number) => {
        try {
            const response = await deleteSession(sessionId);
            if (response.code === 200) {
                message.success('删除会话成功');
                if (currentSession?.id === sessionId) {
                    setCurrentSession(null);
                    setMessages([]);
                }
                loadSessions();
            }
        } catch (error) {
            message.error('删除会话失败');
        }
    };

    const handleSelectSession = async (session: SessionListResponse) => {
        setCurrentSession(session);
        try {
            const response = await getChatRecords(session.id);
            if (response.code === 200) {
                setMessages(response.data || []);
            }
        } catch (error) {
            message.error('加载聊天记录失败');
        }
    };

    const handleSendMessage = async () => {
        if (!inputValue.trim() || !currentSession) return;

        setLoading(true);
        try {
            const request: EmotionChatRequest = {
                sessionId: currentSession.id,
                message: inputValue.trim(),
            };

            const userMessage: ChatRecordResponse = {
                id: Date.now(),
                message: inputValue.trim(),
                isUser: 1,
                emotion: '',
                createTime: Date.now(),
            };
            setMessages(prev => [...prev, userMessage]);
            setInputValue('');

            const response = await sendChatMessage(request);
            if (response.code === 200 && response.data) {
                const botMessage: ChatRecordResponse = {
                    id: Date.now() + 1,
                    message: response.data.response,
                    isUser: 0,
                    emotion: response.data.emotion,
                    createTime: Date.now(),
                };
                setMessages(prev => [...prev, botMessage]);
                loadSessions();
            }
        } catch (error) {
            message.error('发送消息失败');
        } finally {
            setLoading(false);
        }
    };

    const handleKeyPress = (e: React.KeyboardEvent) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            handleSendMessage();
        }
    };

    return (
        <Layout style={{ height: '100vh' }}>
            <Sider width={280} theme="light" style={{ borderRight: '1px solid #f0f0f0' }}>
                <div style={{ padding: '16px', borderBottom: '1px solid #f0f0f0' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
                        <h3 style={{ margin: 0 }}>情绪对话</h3>
                        <Button
                            type="primary"
                            size="small"
                            icon={<PlusOutlined />}
                            onClick={handleCreateSession}
                            loading={sessionLoading}
                        >
                            新建对话
                        </Button>
                    </div>
                </div>
                <List
                    dataSource={sessions}
                    loading={sessionLoading}
                    renderItem={session => (
                        <List.Item
                            key={session.id}
                            onClick={() => handleSelectSession(session)}
                            style={{
                                cursor: 'pointer',
                                padding: '12px 16px',
                                borderLeft: currentSession?.id === session.id ? '3px solid #1890ff' : '3px solid transparent',
                                backgroundColor: currentSession?.id === session.id ? '#f5f5f5' : 'transparent',
                            }}
                            extra={
                                <Popconfirm
                                    title="确定删除这个会话吗？"
                                    onConfirm={() => handleDeleteSession(session.id)}
                                    okText="确定"
                                    cancelText="取消"
                                >
                                    <Button size="small" danger icon={<DeleteOutlined />} />
                                </Popconfirm>
                            }
                        >
                            <List.Item.Meta
                                avatar={<MessageOutlined />}
                                title={
                                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                                        <span>{session.sessionName}</span>
                                        {session.lastEmotion && (
                                            <Tooltip title={emotionLabels[session.lastEmotion]}>
                                                {emotionIcons[session.lastEmotion]}
                                            </Tooltip>
                                        )}
                                    </div>
                                }
                                description={
                                    <div style={{ fontSize: '12px', color: '#999', marginTop: '4px' }}>
                                        {session.lastMessage || '暂无消息'}
                                    </div>
                                }
                            />
                        </List.Item>
                    )}
                    style={{ overflow: 'auto', height: 'calc(100vh - 80px)' }}
                />
            </Sider>
            <Content style={{ display: 'flex', flexDirection: 'column', height: '100vh' }}>
                {currentSession ? (
                    <>
                        <div style={{ padding: '16px', borderBottom: '1px solid #f0f0f0', display: 'flex', alignItems: 'center', gap: '12px' }}>
                            <Avatar icon={<MessageOutlined />} />
                            <div>
                                <h4 style={{ margin: 0 }}>{currentSession.sessionName}</h4>
                                {currentSession.lastEmotion && (
                                    <span style={{ fontSize: '12px', color: '#999', display: 'flex', alignItems: 'center', gap: '4px' }}>
                                        当前心情：{emotionIcons[currentSession.lastEmotion]} {emotionLabels[currentSession.lastEmotion]}
                                    </span>
                                )}
                            </div>
                        </div>
                        <div style={{ flex: 1, overflow: 'auto', padding: '16px', backgroundColor: '#fafafa' }}>
                            {messages.length === 0 ? (
                                <div style={{ textAlign: 'center', padding: '100px 0', color: '#999' }}>
                                    <MessageOutlined style={{ fontSize: '48px', marginBottom: '16px' }} />
                                    <p>开始与我聊天吧～</p>
                                </div>
                            ) : (
                                <div>
                                    {messages.map((msg, index) => (
                                        <div
                                            key={msg.id || index}
                                            style={{
                                                display: 'flex',
                                                justifyContent: msg.isUser ? 'flex-end' : 'flex-start',
                                                marginBottom: '16px',
                                            }}
                                        >
                                            <div
                                                style={{
                                                    maxWidth: '70%',
                                                    padding: '12px 16px',
                                                    borderRadius: msg.isUser ? '16px 16px 4px 16px' : '16px 16px 16px 4px',
                                                    backgroundColor: msg.isUser ? '#1890ff' : '#ffffff',
                                                    color: msg.isUser ? '#ffffff' : '#333333',
                                                    boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
                                                }}
                                            >
                                                <p style={{ margin: 0, wordBreak: 'break-word' }}>{msg.message}</p>
                                                {msg.emotion && !msg.isUser && (
                                                    <div style={{ marginTop: '8px', display: 'flex', alignItems: 'center', gap: '4px', fontSize: '12px', opacity: 0.7 }}>
                                                        {emotionIcons[msg.emotion]}
                                                        <span>{emotionLabels[msg.emotion]}</span>
                                                    </div>
                                                )}
                                            </div>
                                        </div>
                                    ))}
                                    <div ref={messagesEndRef} />
                                </div>
                            )}
                        </div>
                        <div style={{ padding: '16px', borderTop: '1px solid #f0f0f0', backgroundColor: '#ffffff' }}>
                            <Input
                                value={inputValue}
                                onChange={e => setInputValue(e.target.value)}
                                onKeyPress={handleKeyPress}
                                placeholder="输入你想说的话..."
                                allowClear
                                style={{ marginBottom: '8px' }}
                            />
                            <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
                                <Button
                                    type="primary"
                                    onClick={handleSendMessage}
                                    loading={loading}
                                    disabled={!inputValue.trim()}
                                >
                                    发送
                                </Button>
                            </div>
                        </div>
                    </>
                ) : (
                    <div style={{ flex: 1, display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#999' }}>
                        <div style={{ textAlign: 'center' }}>
                            <MessageOutlined style={{ fontSize: '64px', marginBottom: '16px', opacity: 0.5 }} />
                            <p>选择一个会话开始聊天</p>
                            <p style={{ fontSize: '12px' }}>或创建新的对话</p>
                        </div>
                    </div>
                )}
            </Content>
        </Layout>
    );
};

export default EmotionChat;