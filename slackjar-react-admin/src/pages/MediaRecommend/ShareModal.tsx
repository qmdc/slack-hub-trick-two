import React, { useState } from 'react';
import { Modal, Input, message } from 'antd';
import { CopyOutlined, CheckOutlined } from '@ant-design/icons';

interface ShareModalProps {
    visible: boolean;
    onCancel: () => void;
    shareUrl: string;
}

const ShareModal: React.FC<ShareModalProps> = ({ visible, onCancel, shareUrl }) => {
    const [copied, setCopied] = useState(false);

    const handleCopy = async () => {
        try {
            await navigator.clipboard.writeText(window.location.origin + shareUrl);
            setCopied(true);
            message.success('链接已复制到剪贴板');
            setTimeout(() => setCopied(false), 2000);
        } catch (err) {
            message.error('复制失败');
        }
    };

    return (
        <Modal
            title="分享链接"
            open={visible}
            onCancel={onCancel}
            footer={null}
        >
            <div style={{ marginBottom: 16 }}>
                <p style={{ margin: 0, marginBottom: 8, color: '#666' }}>复制下方链接分享给朋友：</p>
                <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                    <Input
                        value={window.location.origin + shareUrl}
                        readOnly
                        style={{ flex: 1 }}
                    />
                    <button
                        onClick={handleCopy}
                        style={{
                            padding: '4px 16px',
                            borderRadius: 4,
                            border: '1px solid #1890ff',
                            backgroundColor: copied ? '#1890ff' : '#fff',
                            color: copied ? '#fff' : '#1890ff',
                            cursor: 'pointer',
                            display: 'flex',
                            alignItems: 'center',
                            gap: 4,
                        }}
                    >
                        {copied ? <CheckOutlined /> : <CopyOutlined />}
                        {copied ? '已复制' : '复制'}
                    </button>
                </div>
            </div>
            <p style={{ margin: 0, color: '#999', fontSize: 12 }}>
                分享链接生成后，其他人可以通过链接查看你的书单影单
            </p>
        </Modal>
    );
};

export default ShareModal;