import React, { useState, useEffect, useCallback } from 'react'
import {
    Layout,
    Card,
    Input,
    Button,
    Modal,
    Form,
    Tag,
    Select,
    Upload,
    Table,
    Popconfirm,
    message
} from 'antd'
import {
    DownloadOutlined,
    FolderOpenOutlined,
    PlusOutlined,
    EditOutlined,
    DeleteOutlined,
    SearchOutlined,
    BookOutlined
} from '@ant-design/icons'
import { useTranslation } from 'react-i18next'
import type { ColumnType } from 'antd/es/table'
import dayjs from 'dayjs'
import {
    pageQueryBookmarks,
    createBookmark,
    updateBookmark,
    deleteBookmark,
    importBookmarks,
    exportBookmarks,
    listCategories,
    createCategory,
    updateCategory,
    deleteCategory,
    listTags,
    createTag,
    updateTag,
    deleteTag,
    type BookmarkDTO,
    type BookmarkQueryRequest,
    type BookmarkCreateRequest,
    type BookmarkUpdateRequest,
    type BookmarkImportRequest,
    type BookmarkCategoryDTO,
    type BookmarkCategoryRequest,
    type BookmarkTagDTO,
    type BookmarkTagRequest
} from '../../apis/modules/bookmark'

const { Content } = Layout
const { Search } = Input
const { Option } = Select

interface BookmarkManagerProps {
}

const BookmarkManager: React.FC<BookmarkManagerProps> = () => {
    const { t } = useTranslation()
    const [bookmarks, setBookmarks] = useState<BookmarkDTO[]>([])
    const [categories, setCategories] = useState<BookmarkCategoryDTO[]>([])
    const [tags, setTags] = useState<BookmarkTagDTO[]>([])
    const [total, setTotal] = useState(0)
    const [loading, setLoading] = useState(false)
    const [keyword, setKeyword] = useState('')
    const [selectedCategory, setSelectedCategory] = useState<number | undefined>()
    const [selectedTag, setSelectedTag] = useState<string>('')

    const [showBookmarkModal, setShowBookmarkModal] = useState(false)
    const [editingBookmark, setEditingBookmark] = useState<BookmarkDTO | null>(null)
    const [bookmarkForm] = Form.useForm()

    const [showCategoryModal, setShowCategoryModal] = useState(false)
    const [editingCategory, setEditingCategory] = useState<BookmarkCategoryDTO | null>(null)
    const [categoryForm] = Form.useForm()

    const [showTagModal, setShowTagModal] = useState(false)
    const [editingTag, setEditingTag] = useState<BookmarkTagDTO | null>(null)
    const [tagForm] = Form.useForm()

    const fetchBookmarks = useCallback(async (pageNo = 1, pageSize = 10) => {
        setLoading(true)
        try {
            const params: BookmarkQueryRequest = {
                pageNo,
                pageSize,
                keyword: keyword || undefined,
                categoryId: selectedCategory,
                tagName: selectedTag || undefined
            }
            const res = await pageQueryBookmarks(params)
            if (res.code === 200 && res.data) {
                setBookmarks(res.data.list || [])
                setTotal(res.data.total)
            }
        } catch (error) {
            message.error(t('common.loadFailed'))
        } finally {
            setLoading(false)
        }
    }, [keyword, selectedCategory, selectedTag, t])

    const fetchCategories = useCallback(async () => {
        try {
            const res = await listCategories()
            if (res.code === 200 && res.data) {
                setCategories(res.data)
            }
        } catch (error) {
            console.error('Failed to fetch categories', error)
        }
    }, [])

    const fetchTags = useCallback(async () => {
        try {
            const res = await listTags()
            if (res.code === 200 && res.data) {
                setTags(res.data)
            }
        } catch (error) {
            console.error('Failed to fetch tags', error)
        }
    }, [])

    useEffect(() => {
        fetchBookmarks()
        fetchCategories()
        fetchTags()
    }, [fetchBookmarks, fetchCategories, fetchTags])

    const handleSearch = () => {
        fetchBookmarks()
    }

    const handleAddBookmark = () => {
        setEditingBookmark(null)
        bookmarkForm.resetFields()
        setShowBookmarkModal(true)
    }

    const handleEditBookmark = (bookmark: BookmarkDTO) => {
        setEditingBookmark(bookmark)
        bookmarkForm.setFieldsValue({
            url: bookmark.url,
            title: bookmark.title,
            description: bookmark.description,
            tags: bookmark.tags,
            categoryId: bookmark.categoryId
        })
        setShowBookmarkModal(true)
    }

    const handleSaveBookmark = async () => {
        try {
            const values = await bookmarkForm.validateFields()
            if (editingBookmark) {
                const updateData: BookmarkUpdateRequest = {
                    title: values.title,
                    description: values.description,
                    tags: values.tags,
                    categoryId: values.categoryId
                }
                await updateBookmark(editingBookmark.id, updateData)
                message.success(t('common.updateSuccess'))
            } else {
                const createData: BookmarkCreateRequest = {
                    url: values.url,
                    title: values.title,
                    description: values.description,
                    tags: values.tags,
                    categoryId: values.categoryId
                }
                await createBookmark(createData)
                message.success(t('common.createSuccess'))
            }
            setShowBookmarkModal(false)
            fetchBookmarks()
        } catch (error) {
            message.error(t('common.operationFailed'))
        }
    }

    const handleDeleteBookmark = async (id: number) => {
        try {
            await deleteBookmark(id)
            message.success(t('common.deleteSuccess'))
            fetchBookmarks()
        } catch (error) {
            message.error(t('common.deleteFailed'))
        }
    }

    const handleAddCategory = () => {
        setEditingCategory(null)
        categoryForm.resetFields()
        setShowCategoryModal(true)
    }

    const handleEditCategory = (category: BookmarkCategoryDTO) => {
        setEditingCategory(category)
        categoryForm.setFieldsValue({
            name: category.name,
            icon: category.icon,
            sortOrder: category.sortOrder
        })
        setShowCategoryModal(true)
    }

    const handleSaveCategory = async () => {
        try {
            const values = await categoryForm.validateFields()
            const data: BookmarkCategoryRequest = {
                name: values.name,
                icon: values.icon,
                sortOrder: values.sortOrder
            }
            if (editingCategory) {
                await updateCategory(editingCategory.id, data)
                message.success(t('common.updateSuccess'))
            } else {
                await createCategory(data)
                message.success(t('common.createSuccess'))
            }
            setShowCategoryModal(false)
            fetchCategories()
        } catch (error) {
            message.error(t('common.operationFailed'))
        }
    }

    const handleDeleteCategory = async (id: number) => {
        try {
            await deleteCategory(id)
            message.success(t('common.deleteSuccess'))
            fetchCategories()
            if (selectedCategory === id) {
                setSelectedCategory(undefined)
                fetchBookmarks()
            }
        } catch (error: any) {
            message.error(error.response?.data?.message || t('common.deleteFailed'))
        }
    }

    const handleAddTag = () => {
        setEditingTag(null)
        tagForm.resetFields()
        setShowTagModal(true)
    }

    const handleEditTag = (tag: BookmarkTagDTO) => {
        setEditingTag(tag)
        tagForm.setFieldsValue({
            name: tag.name,
            color: tag.color
        })
        setShowTagModal(true)
    }

    const handleSaveTag = async () => {
        try {
            const values = await tagForm.validateFields()
            const data: BookmarkTagRequest = {
                name: values.name,
                color: values.color
            }
            if (editingTag) {
                await updateTag(editingTag.id, data)
                message.success(t('common.updateSuccess'))
            } else {
                await createTag(data)
                message.success(t('common.createSuccess'))
            }
            setShowTagModal(false)
            fetchTags()
        } catch (error: any) {
            message.error(error.response?.data?.message || t('common.operationFailed'))
        }
    }

    const handleDeleteTag = async (id: number) => {
        try {
            await deleteTag(id)
            message.success(t('common.deleteSuccess'))
            fetchTags()
            if (selectedTag && tags.find(t => t.id === id)?.name === selectedTag) {
                setSelectedTag('')
                fetchBookmarks()
            }
        } catch (error) {
            message.error(t('common.deleteFailed'))
        }
    }

    const handleImport = async (file: File) => {
        try {
            const text = await file.text()
            const jsonData = JSON.parse(text)
            const importData: BookmarkImportRequest = {
                bookmarks: jsonData.map((item: { url: string; title?: string; description?: string; tags?: string; category?: string }) => ({
                    url: item.url,
                    title: item.title,
                    description: item.description,
                    tags: item.tags,
                    category: item.category
                }))
            }
            await importBookmarks(importData)
            message.success('导入成功')
            fetchBookmarks()
            fetchCategories()
        } catch (error) {
            message.error('导入失败，请确保文件格式正确')
        }
    }

    const handleExport = async () => {
        try {
            const res = await exportBookmarks()
            if (res.code === 200 && res.data) {
                const blob = new Blob([JSON.stringify(res.data, null, 2)], { type: 'application/json' })
                const url = URL.createObjectURL(blob)
                const a = document.createElement('a')
                a.href = url
                a.download = `bookmarks_${dayjs().format('YYYYMMDD')}.json`
                a.click()
                URL.revokeObjectURL(url)
                message.success('导出成功')
            }
        } catch (error) {
            message.error('导出失败')
        }
    }

    const columns: ColumnType<BookmarkDTO>[] = [
        {
            title: t('bookmark.website'),
            dataIndex: 'title',
            key: 'title',
            ellipsis: true,
            render: (_, record) => (
                <div className="flex items-center gap-2">
                    {record.faviconUrl && (
                        <img src={record.faviconUrl} alt="" className="w-6 h-6 rounded" onError={(e: React.SyntheticEvent<HTMLImageElement>) => {
                            (e.target as HTMLImageElement).style.display = 'none'
                        }} />
                    )}
                    <a href={record.url} target="_blank" rel="noopener noreferrer" className="hover:text-blue-500">
                        {record.title || record.url}
                    </a>
                </div>
            )
        },
        {
            title: t('bookmark.url'),
            dataIndex: 'url',
            key: 'url',
            ellipsis: true,
            width: 200
        },
        {
            title: t('bookmark.category'),
            dataIndex: 'categoryName',
            key: 'categoryName',
            render: (text) => text || '-'
        },
        {
            title: t('bookmark.tags'),
            dataIndex: 'tags',
            key: 'tags',
            render: (text) => {
                if (!text) return '-'
                return text.split(',').map((tag: string, index: number) => (
                    <Tag key={index}>{tag}</Tag>
                ))
            }
        },
        {
            title: t('bookmark.createTime'),
            dataIndex: 'createTime',
            key: 'createTime',
            render: (time) => dayjs(time).format('YYYY-MM-DD HH:mm')
        },
        {
            title: t('common.action'),
            key: 'action',
            render: (_, record) => (
                <div className="flex gap-2">
                    <Button
                        type="text"
                        icon={<EditOutlined />}
                        onClick={() => handleEditBookmark(record)}
                    />
                    <Popconfirm
                        title={t('bookmark.confirmDelete')}
                        onConfirm={() => handleDeleteBookmark(record.id)}
                    >
                        <Button type="text" danger icon={<DeleteOutlined />} />
                    </Popconfirm>
                </div>
            )
        }
    ]

    return (
        <Content className="p-6">
            <Card title={t('menu.bookmark manager')} className="mb-6">
                <div className="flex flex-wrap gap-4 items-center">
                    <Search
                        placeholder={t('bookmark.searchPlaceholder')}
                        allowClear
                        enterButton={<SearchOutlined />}
                        size="middle"
                        style={{ width: 300 }}
                        value={keyword}
                        onChange={(e: React.ChangeEvent<HTMLInputElement>) => setKeyword(e.target.value)}
                        onSearch={handleSearch}
                    />
                    <Select
                        placeholder={t('bookmark.selectCategory')}
                        allowClear
                        style={{ width: 150 }}
                        value={selectedCategory}
                        onChange={(value: number | undefined) => {
                            setSelectedCategory(value)
                            fetchBookmarks()
                        }}
                    >
                        {categories.map((cat) => (
                            <Option key={cat.id} value={cat.id}>{cat.name}</Option>
                        ))}
                    </Select>
                    <Select
                        placeholder={t('bookmark.selectTag')}
                        allowClear
                        style={{ width: 150 }}
                        value={selectedTag}
                        onChange={(value: string) => {
                            setSelectedTag(value)
                            fetchBookmarks()
                        }}
                    >
                        {tags.map((tag) => (
                            <Option key={tag.id} value={tag.name}>
                                <span style={{ color: tag.color }}>{tag.name}</span>
                            </Option>
                        ))}
                    </Select>
                    <div className="flex gap-2 ml-auto">
                        <Upload
                            accept=".json"
                            showUploadList={false}
                            beforeUpload={(file: File) => {
                                handleImport(file)
                                return false
                            }}
                        >
                            <Button icon={<FolderOpenOutlined />}>
                                {t('bookmark.import')}
                            </Button>
                        </Upload>
                        <Button
                            icon={<DownloadOutlined />}
                            onClick={handleExport}
                        >
                            {t('bookmark.export')}
                        </Button>
                        <Button
                            type="primary"
                            icon={<PlusOutlined />}
                            onClick={handleAddBookmark}
                        >
                            {t('bookmark.addBookmark')}
                        </Button>
                    </div>
                </div>
            </Card>

            <div className="grid grid-cols-4 gap-6">
                <div className="col-span-1">
                    <Card title={t('bookmark.categories')} className="mb-4">
                        <div className="space-y-2">
                            {categories.length === 0 ? (
                                <p className="text-gray-400 text-sm">{t('bookmark.noCategories')}</p>
                            ) : (
                                categories.map((cat) => (
                                    <div
                                        key={cat.id}
                                        className={`flex items-center justify-between p-2 rounded cursor-pointer transition-colors ${
                                            selectedCategory === cat.id
                                                ? 'bg-blue-50 text-blue-600'
                                                : 'hover:bg-gray-50'
                                        }`}
                                        onClick={() => {
                                            setSelectedCategory(selectedCategory === cat.id ? undefined : cat.id)
                                            fetchBookmarks()
                                        }}
                                    >
                                        <span>{cat.name}</span>
                                        <div className="flex gap-1">
                                            <button
                                                className="p-1 hover:text-blue-500"
                                                onClick={(e) => {
                                                    e.stopPropagation()
                                                    handleEditCategory(cat)
                                                }}
                                            >
                                                <EditOutlined size={14} />
                                            </button>
                                            <button
                                                className="p-1 hover:text-red-500"
                                                onClick={(e) => {
                                                    e.stopPropagation()
                                                    handleDeleteCategory(cat.id)
                                                }}
                                            >
                                                <DeleteOutlined size={14} />
                                            </button>
                                        </div>
                                    </div>
                                ))
                            )}
                        </div>
                        <Button
                            type="text"
                            icon={<PlusOutlined />}
                            className="w-full mt-4"
                            onClick={handleAddCategory}
                        >
                            {t('bookmark.addCategory')}
                        </Button>
                    </Card>

                    <Card title={t('bookmark.tags')}>
                        <div className="flex flex-wrap gap-2">
                            {tags.length === 0 ? (
                                <p className="text-gray-400 text-sm">{t('bookmark.noTags')}</p>
                            ) : (
                                tags.map((tag) => (
                                    <Tag
                                        key={tag.id}
                                        color={tag.color}
                                        closable
                                        className={`cursor-pointer ${selectedTag === tag.name ? 'ring-2 ring-offset-1' : ''}`}
                                        onClick={() => {
                                            setSelectedTag(selectedTag === tag.name ? '' : tag.name)
                                            fetchBookmarks()
                                        }}
                                        onClose={(e: React.MouseEvent<HTMLElement>) => {
                                            e.preventDefault()
                                            handleDeleteTag(tag.id)
                                        }}
                                    >
                                        {tag.name}
                                    </Tag>
                                ))
                            )}
                        </div>
                        <Button
                            type="text"
                            icon={<PlusOutlined />}
                            className="w-full mt-4"
                            onClick={handleAddTag}
                        >
                            {t('bookmark.addTag')}
                        </Button>
                    </Card>
                </div>

                <div className="col-span-3">
                    <Card>
                        {bookmarks.length === 0 ? (
                            <div className="text-center py-8 text-gray-400">
                                {t('bookmark.noBookmarks')}
                            </div>
                        ) : (
                            <Table
                                columns={columns}
                                dataSource={bookmarks}
                                loading={loading}
                                rowKey="id"
                                pagination={{
                                    total,
                                    onChange: (page, pageSize) => fetchBookmarks(page, pageSize)
                                }}
                            />
                        )}
                    </Card>
                </div>
            </div>

            <Modal
                title={editingBookmark ? t('bookmark.editBookmark') : t('bookmark.addBookmark')}
                open={showBookmarkModal}
                onCancel={() => setShowBookmarkModal(false)}
                onOk={handleSaveBookmark}
            >
                <Form form={bookmarkForm} layout="vertical">
                    <Form.Item
                        name="url"
                        label={t('bookmark.url')}
                        rules={[{ required: true, message: t('bookmark.enterUrl') }]}
                    >
                        <Input placeholder={t('bookmark.enterUrl')} />
                    </Form.Item>
                    <Form.Item name="title" label={t('bookmark.title')}>
                        <Input placeholder={t('bookmark.enterTitle')} />
                    </Form.Item>
                    <Form.Item name="description" label={t('bookmark.description')}>
                        <Input.TextArea placeholder={t('bookmark.enterDescription')} rows={3} />
                    </Form.Item>
                    <Form.Item name="categoryId" label={t('bookmark.category')}>
                        <Select placeholder={t('bookmark.selectCategory')}>
                            {categories.map((cat) => (
                                <Option key={cat.id} value={cat.id}>{cat.name}</Option>
                            ))}
                        </Select>
                    </Form.Item>
                    <Form.Item name="tags" label={t('bookmark.tags')}>
                        <Select
                            mode="tags"
                            tokenSeparators={[',', ' ']}
                            placeholder={t('bookmark.enterTags')}
                            style={{ width: '100%' }}
                        />
                    </Form.Item>
                </Form>
            </Modal>

            <Modal
                title={editingCategory ? t('bookmark.editCategory') : t('bookmark.addCategory')}
                open={showCategoryModal}
                onCancel={() => setShowCategoryModal(false)}
                onOk={handleSaveCategory}
            >
                <Form form={categoryForm} layout="vertical">
                    <Form.Item
                        name="name"
                        label={t('bookmark.categoryName')}
                        rules={[{ required: true, message: t('bookmark.enterCategoryName') }]}
                    >
                        <Input placeholder={t('bookmark.enterCategoryName')} />
                    </Form.Item>
                    <Form.Item name="icon" label={t('bookmark.icon')}>
                        <Input placeholder={t('bookmark.enterIcon')} />
                    </Form.Item>
                    <Form.Item name="sortOrder" label={t('bookmark.sortOrder')}>
                        <Input type="number" placeholder={t('bookmark.enterSortOrder')} />
                    </Form.Item>
                </Form>
            </Modal>

            <Modal
                title={editingTag ? t('bookmark.editTag') : t('bookmark.addTag')}
                open={showTagModal}
                onCancel={() => setShowTagModal(false)}
                onOk={handleSaveTag}
            >
                <Form form={tagForm} layout="vertical">
                    <Form.Item
                        name="name"
                        label={t('bookmark.tagName')}
                        rules={[{ required: true, message: t('bookmark.enterTagName') }]}
                    >
                        <Input placeholder={t('bookmark.enterTagName')} />
                    </Form.Item>
                    <Form.Item name="color" label={t('bookmark.color')}>
                        <Input type="color" defaultValue="#1890ff" />
                    </Form.Item>
                </Form>
            </Modal>
        </Content>
    )
}

export default BookmarkManager