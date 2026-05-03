import React, { useState, useEffect, useCallback, useRef } from 'react'
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
    message,
    Empty,
    Space
} from 'antd'
import {
    DownloadOutlined,
    FolderOpenOutlined,
    PlusOutlined,
    EditOutlined,
    DeleteOutlined,
    SearchOutlined,
    BookOutlined,
    FilterOutlined,
    TagOutlined
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
    const isMounted = useRef(true)
    
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

    const [searchCount, setSearchCount] = useState<number>(0)
    const [lastSearchTime, setLastSearchTime] = useState<number>(0)

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
            const currentTime = Date.now()
            setLastSearchTime(currentTime)
            
            if (res.code === 200 && res.data) {
                const dataList = res.data.list || []
                setBookmarks(dataList)
                setTotal(res.data.total)
                setSearchCount(dataList.length)
                if (dataList.length > 0) {
                    message.success(t('bookmark.searchSuccess', { count: dataList.length }))
                } else if (keyword || selectedCategory || selectedTag) {
                    message.info(t('bookmark.noSearchResults'))
                }
            } else {
                setBookmarks([])
                setTotal(0)
                setSearchCount(0)
            }
        } catch (error: any) {
            console.error('Fetch bookmarks error:', error)
            setBookmarks([])
            setTotal(0)
            setSearchCount(0)
            const errorMsg = error?.response?.data?.message || error?.message || t('common.loadFailed')
            message.error(errorMsg)
        } finally {
            setLoading(false)
        }
    }, [keyword, selectedCategory, selectedTag, t])

    const fetchCategories = useCallback(async () => {
        try {
            const res = await listCategories()
            console.log('Fetch categories response:', res)
            if (isMounted.current && res.code === 200 && res.data) {
                setCategories(res.data)
                console.log('Categories updated:', res.data)
            }
        } catch (error) {
            console.error('Failed to fetch categories', error)
            message.error(t('bookmark.loadCategoriesFailed'))
        }
    }, [t])

    const fetchTags = useCallback(async () => {
        try {
            const res = await listTags()
            console.log('Fetch tags response:', res)
            if (isMounted.current && res.code === 200 && res.data) {
                setTags(res.data)
                console.log('Tags updated:', res.data)
            }
        } catch (error) {
            console.error('Failed to fetch tags', error)
            message.error(t('bookmark.loadTagsFailed'))
        }
    }, [t])

    useEffect(() => {
        return () => {
            isMounted.current = false
        }
    }, [])

    useEffect(() => {
        fetchBookmarks()
        fetchCategories()
        fetchTags()
    }, [fetchBookmarks, fetchCategories, fetchTags])

    const handleSearch = () => {
        fetchBookmarks(1)
    }

    const handleClearFilters = () => {
        setKeyword('')
        setSelectedCategory(undefined)
        setSelectedTag('')
        bookmarkForm.resetFields(['url', 'title', 'description', 'tags', 'categoryId'])
        fetchBookmarks(1)
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
            tags: bookmark.tags ? bookmark.tags.split(',') : [],
            categoryId: bookmark.categoryId
        })
        setShowBookmarkModal(true)
    }

    const handleSaveBookmark = async () => {
        try {
            const values = await bookmarkForm.validateFields()
            
            const tagsArray = values.tags as string[] || []
            const tagsString = tagsArray.length > 0 ? tagsArray.join(',') : undefined

            if (editingBookmark) {
                const updateData: BookmarkUpdateRequest = {
                    title: values.title || undefined,
                    description: values.description || undefined,
                    tags: tagsString,
                    categoryId: values.categoryId
                }
                await updateBookmark(editingBookmark.id, updateData)
                message.success(t('common.updateSuccess'))
            } else {
                const createData: BookmarkCreateRequest = {
                    url: values.url,
                    title: values.title || undefined,
                    description: values.description || undefined,
                    tags: tagsString,
                    categoryId: values.categoryId
                }
                await createBookmark(createData)
                message.success(t('common.createSuccess'))
            }
            setShowBookmarkModal(false)
            fetchBookmarks()
        } catch (error: any) {
            console.error('Save bookmark error:', error)
            const errorMsg = error?.response?.data?.message || error?.message || t('common.operationFailed')
            message.error(errorMsg)
        }
    }

    const handleDeleteBookmark = async (id: number) => {
        try {
            await deleteBookmark(id)
            message.success(t('common.deleteSuccess'))
            fetchBookmarks()
        } catch (error: any) {
            const errorMsg = error?.response?.data?.message || error?.message || t('common.deleteFailed')
            message.error(errorMsg)
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
                icon: values.icon || undefined,
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
        } catch (error: any) {
            const errorMsg = error?.response?.data?.message || error?.message || t('common.operationFailed')
            message.error(errorMsg)
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
            const errorMsg = error?.response?.data?.message || error?.message || t('common.deleteFailed')
            message.error(errorMsg)
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
                color: values.color || undefined
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
            const errorMsg = error?.response?.data?.message || error?.message || t('common.operationFailed')
            message.error(errorMsg)
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
        } catch (error: any) {
            const errorMsg = error?.response?.data?.message || error?.message || t('common.deleteFailed')
            message.error(errorMsg)
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
            message.success(t('bookmark.importSuccess'))
            fetchBookmarks()
            fetchCategories()
        } catch (error: any) {
            const errorMsg = error?.message || t('bookmark.importFailed')
            message.error(errorMsg)
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
                message.success(t('bookmark.exportSuccess'))
            }
        } catch (error: any) {
            const errorMsg = error?.message || t('bookmark.exportFailed')
            message.error(errorMsg)
        }
    }

    const columns: ColumnType<BookmarkDTO>[] = [
        {
            title: (
                <span className="flex items-center gap-1">
                    <BookOutlined size={14} />
                    {t('bookmark.website')}
                </span>
            ),
            dataIndex: 'title',
            key: 'title',
            ellipsis: true,
            width: 220,
            render: (_, record) => (
                <div className="flex items-center gap-2">
                    {record.faviconUrl && (
                        <img
                            src={record.faviconUrl}
                            alt=""
                            className="w-6 h-6 rounded object-cover"
                            onError={(e: React.SyntheticEvent<HTMLImageElement>) => {
                                (e.target as HTMLImageElement).style.display = 'none'
                            }}
                        />
                    )}
                    <a
                        href={record.url}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="hover:text-blue-500 transition-colors truncate flex-1"
                    >
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
            width: 250
        },
        {
            title: (
                <span className="flex items-center gap-1">
                    <FolderOpenOutlined size={14} />
                    {t('bookmark.category')}
                </span>
            ),
            dataIndex: 'categoryName',
            key: 'categoryName',
            width: 120,
            render: (text) => text || <span className="text-gray-400">-</span>
        },
        {
            title: (
                <span className="flex items-center gap-1">
                    <TagOutlined size={14} />
                    {t('bookmark.tags')}
                </span>
            ),
            dataIndex: 'tags',
            key: 'tags',
            width: 180,
            render: (text) => {
                if (!text) return <span className="text-gray-400">-</span>
                return text.split(',').map((tag: string, index: number) => (
                    <Tag key={index} className="mr-1 mb-1">
                        {tag}
                    </Tag>
                ))
            }
        },
        {
            title: t('bookmark.createTime'),
            dataIndex: 'createTime',
            key: 'createTime',
            width: 150,
            render: (time) => dayjs(time).format('YYYY-MM-DD HH:mm')
        },
        {
            title: t('common.action'),
            key: 'action',
            width: 120,
            render: (_, record) => (
                <Space size="small">
                    <Button
                        type="text"
                        icon={<EditOutlined size={14} />}
                        onClick={() => handleEditBookmark(record)}
                        className="text-gray-600 hover:text-blue-500"
                    />
                    <Popconfirm
                        title={t('bookmark.confirmDelete')}
                        onConfirm={() => handleDeleteBookmark(record.id)}
                        okText={t('common.yes')}
                        cancelText={t('common.no')}
                    >
                        <Button type="text" danger icon={<DeleteOutlined size={14} />} />
                    </Popconfirm>
                </Space>
            )
        }
    ]

    const hasActiveFilters = keyword || selectedCategory !== undefined || selectedTag

    return (
        <Content className="p-6">
            <Card
                title={
                    <div className="flex items-center gap-2">
                        <BookOutlined />
                        <span>{t('menu.bookmark manager')}</span>
                    </div>
                }
                className="mb-6"
                extra={
                    lastSearchTime > 0 && (
                        <span className="text-sm text-gray-500">
                            {t('bookmark.searchCount', { count: searchCount, total })}
                        </span>
                    )
                }
            >
                <div className="flex flex-wrap items-center gap-3">
                    <Search
                        placeholder={t('bookmark.searchPlaceholder')}
                        allowClear
                        enterButton={<SearchOutlined />}
                        size="middle"
                        style={{ width: 320 }}
                        value={keyword}
                        onChange={(e: React.ChangeEvent<HTMLInputElement>) => setKeyword(e.target.value)}
                        onSearch={handleSearch}
                        onPressEnter={handleSearch}
                    />

                    <div className="flex items-center gap-2">
                        <Select
                            placeholder={t('bookmark.selectCategory')}
                            allowClear
                            style={{ width: 160 }}
                            value={selectedCategory}
                            onChange={(value: number | undefined) => {
                                setSelectedCategory(value)
                                fetchBookmarks(1)
                            }}
                            showSearch
                            optionFilterProp="children"
                        >
                            {categories.map((cat) => (
                                <Option key={cat.id} value={cat.id}>{cat.name}</Option>
                            ))}
                        </Select>

                        <Select
                            placeholder={t('bookmark.selectTag')}
                            allowClear
                            style={{ width: 160 }}
                            value={selectedTag}
                            onChange={(value: string) => {
                                setSelectedTag(value)
                                fetchBookmarks(1)
                            }}
                            showSearch
                            optionFilterProp="children"
                        >
                            {tags.map((tag) => (
                                <Option key={tag.id} value={tag.name}>
                                    <span style={{ color: tag.color }}>{tag.name}</span>
                                </Option>
                            ))}
                        </Select>
                    </div>

                    {hasActiveFilters && (
                        <Button
                            type="text"
                            onClick={handleClearFilters}
                            className="text-gray-500 hover:text-gray-700"
                        >
                            {t('bookmark.clearFilters')}
                        </Button>
                    )}

                    <div className="flex items-center gap-2 ml-auto">
                        <Upload
                            accept=".json"
                            showUploadList={false}
                            beforeUpload={(file: File) => {
                                handleImport(file)
                                return false
                            }}
                        >
                            <Button icon={<FolderOpenOutlined />} size="middle">
                                {t('bookmark.import')}
                            </Button>
                        </Upload>

                        <Button
                            icon={<DownloadOutlined />}
                            size="middle"
                            onClick={handleExport}
                        >
                            {t('bookmark.export')}
                        </Button>

                        <Button
                            type="primary"
                            icon={<PlusOutlined />}
                            size="middle"
                            onClick={handleAddBookmark}
                        >
                            {t('bookmark.addBookmark')}
                        </Button>
                    </div>
                </div>

                {hasActiveFilters && (
                    <div className="mt-3 flex items-center gap-2 flex-wrap">
                        <FilterOutlined size={14} className="text-gray-500" />
                        <span className="text-sm text-gray-500 mr-2">{t('bookmark.activeFilters')}:</span>
                        {keyword && (
                            <Tag closable onClose={() => { setKeyword(''); fetchBookmarks(1); }}>
                                {t('bookmark.keyword')}: {keyword}
                            </Tag>
                        )}
                        {selectedCategory && (
                            <Tag closable onClose={() => { setSelectedCategory(undefined); fetchBookmarks(1); }}>
                                {t('bookmark.category')}: {categories.find(c => c.id === selectedCategory)?.name}
                            </Tag>
                        )}
                        {selectedTag && (
                            <Tag closable onClose={() => { setSelectedTag(''); fetchBookmarks(1); }}>
                                {t('bookmark.tag')}: {selectedTag}
                            </Tag>
                        )}
                    </div>
                )}
            </Card>

            <div className="grid grid-cols-12 gap-6">
                <div className="col-span-3">
                    <Card
                        title={
                            <div className="flex items-center gap-2">
                                <FolderOpenOutlined />
                                <span>{t('bookmark.categories')}</span>
                                <span className="ml-auto text-xs text-gray-400">{categories.length}</span>
                            </div>
                        }
                        className="mb-4"
                        size="small"
                    >
                        <div className="space-y-1">
                            {categories.length === 0 ? (
                                <Empty
                                    description={t('bookmark.noCategories')}
                                    image={Empty.PRESENTED_IMAGE_SIMPLE}
                                />
                            ) : (
                                categories.map((cat) => (
                                    <div
                                        key={cat.id}
                                        className={`flex items-center justify-between p-2 rounded-lg cursor-pointer transition-all ${
                                            selectedCategory === cat.id
                                                ? 'bg-blue-50 text-blue-600 border border-blue-200'
                                                : 'hover:bg-gray-50'
                                        }`}
                                        onClick={() => {
                                            setSelectedCategory(selectedCategory === cat.id ? undefined : cat.id)
                                            fetchBookmarks(1)
                                        }}
                                    >
                                        <span className="truncate">{cat.name}</span>
                                        <div className="flex items-center gap-1 ml-2">
                                            <button
                                                className="p-1 hover:text-blue-500 opacity-0 group-hover:opacity-100 transition-opacity"
                                                onClick={(e) => {
                                                    e.stopPropagation()
                                                    handleEditCategory(cat)
                                                }}
                                            >
                                                <EditOutlined size={12} />
                                            </button>
                                            <button
                                                className="p-1 hover:text-red-500 opacity-0 group-hover:opacity-100 transition-opacity"
                                                onClick={(e) => {
                                                    e.stopPropagation()
                                                    handleDeleteCategory(cat.id)
                                                }}
                                            >
                                                <DeleteOutlined size={12} />
                                            </button>
                                        </div>
                                    </div>
                                ))
                            )}
                        </div>
                        <Button
                            type="dashed"
                            icon={<PlusOutlined />}
                            className="w-full mt-4"
                            onClick={handleAddCategory}
                        >
                            {t('bookmark.addCategory')}
                        </Button>
                    </Card>

                    <Card
                        title={
                            <div className="flex items-center gap-2">
                                <TagOutlined />
                                <span>{t('bookmark.tags')}</span>
                                <span className="ml-auto text-xs text-gray-400">{tags.length}</span>
                            </div>
                        }
                        size="small"
                    >
                        <div className="flex flex-wrap gap-2">
                            {tags.length === 0 ? (
                                <Empty
                                    description={t('bookmark.noTags')}
                                    image={Empty.PRESENTED_IMAGE_SIMPLE}
                                />
                            ) : (
                                tags.map((tag) => (
                                    <Tag
                                        key={tag.id}
                                        color={tag.color}
                                        closable
                                        className={`cursor-pointer transition-all ${selectedTag === tag.name ? 'ring-2 ring-offset-1' : ''}`}
                                        onClick={() => {
                                            setSelectedTag(selectedTag === tag.name ? '' : tag.name)
                                            fetchBookmarks(1)
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
                            type="dashed"
                            icon={<PlusOutlined />}
                            className="w-full mt-4"
                            onClick={handleAddTag}
                        >
                            {t('bookmark.addTag')}
                        </Button>
                    </Card>
                </div>

                <div className="col-span-9">
                    <Card size="small">
                        {bookmarks.length === 0 ? (
                            <Empty
                                description={hasActiveFilters ? t('bookmark.noSearchResults') : t('bookmark.addFirstBookmark')}
                                image={Empty.PRESENTED_IMAGE_SIMPLE}
                            >
                                <Button type="primary" icon={<PlusOutlined />} onClick={handleAddBookmark}>
                                    {t('bookmark.addBookmark')}
                                </Button>
                            </Empty>
                        ) : (
                            <Table
                                columns={columns}
                                dataSource={bookmarks}
                                loading={loading}
                                rowKey="id"
                                pagination={{
                                    total,
                                    pageSizeOptions: ['10', '20', '50', '100'],
                                    showSizeChanger: true,
                                    showTotal: (total, range) => `${range[0]}-${range[1]} ${t('bookmark.of')} ${total}`,
                                    onChange: (page, pageSize) => fetchBookmarks(page, pageSize)
                                }}
                                scroll={{ x: 'max-content' }}
                                className="custom-table"
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
                width={520}
                okText={t('common.confirm')}
                cancelText={t('common.cancel')}
            >
                <Form form={bookmarkForm} layout="vertical">
                    <Form.Item
                        name="url"
                        label={t('bookmark.url')}
                        rules={[{ required: true, message: t('bookmark.enterUrl') }]}
                    >
                        <Input
                            placeholder={t('bookmark.enterUrl')}
                            size="large"
                        />
                    </Form.Item>
                    <Form.Item name="title" label={t('bookmark.title')}>
                        <Input
                            placeholder={t('bookmark.enterTitle')}
                            size="large"
                        />
                    </Form.Item>
                    <Form.Item name="description" label={t('bookmark.description')}>
                        <Input.TextArea
                            placeholder={t('bookmark.enterDescription')}
                            rows={3}
                            size="large"
                        />
                    </Form.Item>
                    <Form.Item name="categoryId" label={t('bookmark.category')}>
                        <Select
                            placeholder={t('bookmark.selectCategory')}
                            size="large"
                        >
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
                            size="large"
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
                width={420}
                okText={t('common.confirm')}
                cancelText={t('common.cancel')}
            >
                <Form form={categoryForm} layout="vertical">
                    <Form.Item
                        name="name"
                        label={t('bookmark.categoryName')}
                        rules={[{ required: true, message: t('bookmark.enterCategoryName') }]}
                    >
                        <Input
                            placeholder={t('bookmark.enterCategoryName')}
                            size="large"
                        />
                    </Form.Item>
                    <Form.Item name="icon" label={t('bookmark.icon')}>
                        <Input
                            placeholder={t('bookmark.enterIcon')}
                            size="large"
                        />
                    </Form.Item>
                    <Form.Item name="sortOrder" label={t('bookmark.sortOrder')}>
                        <Input
                            type="number"
                            placeholder={t('bookmark.enterSortOrder')}
                            size="large"
                        />
                    </Form.Item>
                </Form>
            </Modal>

            <Modal
                title={editingTag ? t('bookmark.editTag') : t('bookmark.addTag')}
                open={showTagModal}
                onCancel={() => setShowTagModal(false)}
                onOk={handleSaveTag}
                width={420}
                okText={t('common.confirm')}
                cancelText={t('common.cancel')}
            >
                <Form form={tagForm} layout="vertical">
                    <Form.Item
                        name="name"
                        label={t('bookmark.tagName')}
                        rules={[{ required: true, message: t('bookmark.enterTagName') }]}
                    >
                        <Input
                            placeholder={t('bookmark.enterTagName')}
                            size="large"
                        />
                    </Form.Item>
                    <Form.Item name="color" label={t('bookmark.color')}>
                        <Input
                            type="color"
                            defaultValue="#1890ff"
                            size="large"
                            style={{ height: 40 }}
                        />
                    </Form.Item>
                </Form>
            </Modal>
        </Content>
    )
}

export default BookmarkManager