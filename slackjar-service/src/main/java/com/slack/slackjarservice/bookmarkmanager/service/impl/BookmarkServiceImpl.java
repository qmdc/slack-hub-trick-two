package com.slack.slackjarservice.bookmarkmanager.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.slack.slackjarservice.bookmarkmanager.dao.BookmarkCategoryDao;
import com.slack.slackjarservice.bookmarkmanager.dao.BookmarkDao;
import com.slack.slackjarservice.bookmarkmanager.dao.BookmarkTagDao;
import com.slack.slackjarservice.bookmarkmanager.entity.Bookmark;
import com.slack.slackjarservice.bookmarkmanager.entity.BookmarkCategory;
import com.slack.slackjarservice.bookmarkmanager.entity.BookmarkTag;
import com.slack.slackjarservice.bookmarkmanager.model.dto.BookmarkCategoryDTO;
import com.slack.slackjarservice.bookmarkmanager.model.dto.BookmarkDTO;
import com.slack.slackjarservice.bookmarkmanager.model.dto.BookmarkTagDTO;
import com.slack.slackjarservice.bookmarkmanager.model.request.*;
import com.slack.slackjarservice.bookmarkmanager.service.BookmarkService;
import com.slack.slackjarservice.common.exception.BusinessException;
import com.slack.slackjarservice.common.enumtype.foundation.ResponseEnum;
import com.slack.slackjarservice.common.response.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkDao bookmarkDao;
    private final BookmarkCategoryDao categoryDao;
    private final BookmarkTagDao tagDao;

    @Override
    public PageResult<BookmarkDTO> pageQuery(Long userId, BookmarkQueryRequest request) {
        Page<Bookmark> page = new Page<>(request.getPageNo(), request.getPageSize());
        IPage<Bookmark> result = bookmarkDao.pageQuery(page, userId, request.getKeyword(), 
                                                       request.getCategoryId(), request.getTagName());
        List<BookmarkDTO> dtoList = result.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return PageResult.of(dtoList, result.getTotal(), request.getPageNo(), request.getPageSize());
    }

    @Override
    public BookmarkDTO getById(Long userId, Long id) {
        Bookmark bookmark = bookmarkDao.selectById(id);
        if (bookmark == null || !userId.equals(bookmark.getUserId())) {
            throw new BusinessException(ResponseEnum.DATA_EXISTS);
        }
        return convertToDTO(bookmark);
    }

    @Override
    @Transactional
    public BookmarkDTO create(Long userId, BookmarkCreateRequest request) {
        Bookmark bookmark = new Bookmark();
        bookmark.setUserId(userId);
        bookmark.setUrl(request.getUrl());
        
        Map<String, String> pageInfo = fetchPageInfo(request.getUrl());
        bookmark.setTitle(request.getTitle() != null && !request.getTitle().isEmpty() 
                         ? request.getTitle() : pageInfo.get("title"));
        bookmark.setFaviconUrl(pageInfo.get("favicon"));
        bookmark.setDescription(request.getDescription());
        bookmark.setTags(request.getTags());
        bookmark.setCategoryId(request.getCategoryId());
        
        bookmarkDao.insert(bookmark);
        return convertToDTO(bookmark);
    }

    @Override
    @Transactional
    public BookmarkDTO update(Long userId, Long id, BookmarkUpdateRequest request) {
        Bookmark bookmark = bookmarkDao.selectById(id);
        if (bookmark == null || !userId.equals(bookmark.getUserId())) {
            throw new BusinessException(ResponseEnum.DATA_EXISTS);
        }
        
        if (request.getTitle() != null) {
            bookmark.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            bookmark.setDescription(request.getDescription());
        }
        if (request.getTags() != null) {
            bookmark.setTags(request.getTags());
        }
        if (request.getCategoryId() != null) {
            bookmark.setCategoryId(request.getCategoryId());
        }
        
        bookmarkDao.updateById(bookmark);
        return convertToDTO(bookmark);
    }

    @Override
    @Transactional
    public boolean delete(Long userId, Long id) {
        Bookmark bookmark = bookmarkDao.selectById(id);
        if (bookmark == null || !userId.equals(bookmark.getUserId())) {
            throw new BusinessException(ResponseEnum.DATA_EXISTS);
        }
        return bookmarkDao.deleteById(id) > 0;
    }

    @Override
    @Transactional
    public void importBookmarks(Long userId, BookmarkImportRequest request) {
        if (request.getBookmarks() == null || request.getBookmarks().isEmpty()) {
            return;
        }
        
        Map<String, Long> categoryMap = categoryDao.selectByUserIdOrderBySort(userId)
                .stream()
                .collect(Collectors.toMap(BookmarkCategory::getName, BookmarkCategory::getId));
        
        for (BookmarkImportRequest.BookmarkItem item : request.getBookmarks()) {
            Bookmark bookmark = new Bookmark();
            bookmark.setUserId(userId);
            bookmark.setUrl(item.getUrl());
            bookmark.setTitle(item.getTitle());
            bookmark.setDescription(item.getDescription());
            bookmark.setTags(item.getTags());
            
            if (item.getCategory() != null) {
                Long categoryId = categoryMap.get(item.getCategory());
                if (categoryId == null) {
                    BookmarkCategory category = new BookmarkCategory();
                    category.setUserId(userId);
                    category.setName(item.getCategory());
                    categoryDao.insert(category);
                    categoryId = category.getId();
                    categoryMap.put(item.getCategory(), categoryId);
                }
                bookmark.setCategoryId(categoryId);
            }
            
            Map<String, String> pageInfo = fetchPageInfo(item.getUrl());
            if (bookmark.getTitle() == null || bookmark.getTitle().isEmpty()) {
                bookmark.setTitle(pageInfo.get("title"));
            }
            bookmark.setFaviconUrl(pageInfo.get("favicon"));
            
            bookmarkDao.insert(bookmark);
        }
    }

    @Override
    public List<Map<String, Object>> exportBookmarks(Long userId) {
        List<Bookmark> bookmarks = bookmarkDao.selectByUserId(userId);
        Map<Long, String> categoryMap = categoryDao.selectByUserIdOrderBySort(userId)
                .stream()
                .collect(Collectors.toMap(BookmarkCategory::getId, BookmarkCategory::getName));
        
        return bookmarks.stream()
                .map(b -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("url", b.getUrl());
                    map.put("title", b.getTitle());
                    map.put("description", b.getDescription());
                    map.put("tags", b.getTags());
                    map.put("category", categoryMap.get(b.getCategoryId()));
                    map.put("createTime", b.getCreateTime());
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<BookmarkCategoryDTO> listCategories(Long userId) {
        return categoryDao.selectByUserIdOrderBySort(userId)
                .stream()
                .map(this::convertCategoryToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookmarkCategoryDTO createCategory(Long userId, BookmarkCategoryRequest request) {
        BookmarkCategory category = new BookmarkCategory();
        category.setUserId(userId);
        category.setName(request.getName());
        category.setIcon(request.getIcon());
        category.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        categoryDao.insert(category);
        return convertCategoryToDTO(category);
    }

    @Override
    @Transactional
    public BookmarkCategoryDTO updateCategory(Long userId, Long id, BookmarkCategoryRequest request) {
        BookmarkCategory category = categoryDao.selectById(id);
        if (category == null || !userId.equals(category.getUserId())) {
            throw new BusinessException(ResponseEnum.DATA_EXISTS);
        }
        category.setName(request.getName());
        category.setIcon(request.getIcon());
        if (request.getSortOrder() != null) {
            category.setSortOrder(request.getSortOrder());
        }
        categoryDao.updateById(category);
        return convertCategoryToDTO(category);
    }

    @Override
    @Transactional
    public boolean deleteCategory(Long userId, Long id) {
        BookmarkCategory category = categoryDao.selectById(id);
        if (category == null || !userId.equals(category.getUserId())) {
            throw new BusinessException(ResponseEnum.DATA_EXISTS);
        }
        
        int count = bookmarkDao.countByCategoryId(id);
        if (count > 0) {
            throw new BusinessException(ResponseEnum.PARAM_ERROR.getCode(), "该分类下存在书签，请先删除书签");
        }
        
        return categoryDao.deleteById(id) > 0;
    }

    @Override
    public List<BookmarkTagDTO> listTags(Long userId) {
        return tagDao.selectByUserId(userId)
                .stream()
                .map(this::convertTagToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookmarkTagDTO createTag(Long userId, BookmarkTagRequest request) {
        BookmarkTag existing = tagDao.selectByUserIdAndName(userId, request.getName());
        if (existing != null) {
            throw new BusinessException(ResponseEnum.PARAM_ERROR.getCode(), "标签已存在");
        }
        
        BookmarkTag tag = new BookmarkTag();
        tag.setUserId(userId);
        tag.setName(request.getName());
        tag.setColor(request.getColor() != null ? request.getColor() : "#1890ff");
        tagDao.insert(tag);
        return convertTagToDTO(tag);
    }

    @Override
    @Transactional
    public BookmarkTagDTO updateTag(Long userId, Long id, BookmarkTagRequest request) {
        BookmarkTag tag = tagDao.selectById(id);
        if (tag == null || !userId.equals(tag.getUserId())) {
            throw new BusinessException(ResponseEnum.DATA_EXISTS);
        }
        
        BookmarkTag existing = tagDao.selectByUserIdAndName(userId, request.getName());
        if (existing != null && !existing.getId().equals(id)) {
            throw new BusinessException(ResponseEnum.PARAM_ERROR.getCode(), "标签名称已存在");
        }
        
        tag.setName(request.getName());
        if (request.getColor() != null) {
            tag.setColor(request.getColor());
        }
        tagDao.updateById(tag);
        return convertTagToDTO(tag);
    }

    @Override
    @Transactional
    public boolean deleteTag(Long userId, Long id) {
        BookmarkTag tag = tagDao.selectById(id);
        if (tag == null || !userId.equals(tag.getUserId())) {
            throw new BusinessException(ResponseEnum.DATA_EXISTS);
        }
        return tagDao.deleteById(id) > 0;
    }

    private BookmarkDTO convertToDTO(Bookmark bookmark) {
        BookmarkDTO dto = new BookmarkDTO();
        dto.setId(bookmark.getId());
        dto.setUrl(bookmark.getUrl());
        dto.setTitle(bookmark.getTitle());
        dto.setFaviconUrl(bookmark.getFaviconUrl());
        dto.setDescription(bookmark.getDescription());
        dto.setTags(bookmark.getTags());
        dto.setCategoryId(bookmark.getCategoryId());
        
        if (bookmark.getCategoryId() != null) {
            BookmarkCategory category = categoryDao.selectById(bookmark.getCategoryId());
            if (category != null) {
                dto.setCategoryName(category.getName());
            }
        }
        
        dto.setCreateTime(bookmark.getCreateTime());
        dto.setUpdateTime(bookmark.getUpdateTime());
        return dto;
    }

    private BookmarkCategoryDTO convertCategoryToDTO(BookmarkCategory category) {
        BookmarkCategoryDTO dto = new BookmarkCategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setIcon(category.getIcon());
        dto.setSortOrder(category.getSortOrder());
        dto.setCreateTime(category.getCreateTime());
        return dto;
    }

    private BookmarkTagDTO convertTagToDTO(BookmarkTag tag) {
        BookmarkTagDTO dto = new BookmarkTagDTO();
        dto.setId(tag.getId());
        dto.setName(tag.getName());
        dto.setColor(tag.getColor());
        dto.setCreateTime(tag.getCreateTime());
        return dto;
    }

    private Map<String, String> fetchPageInfo(String urlStr) {
        Map<String, String> result = new HashMap<>();
        result.put("title", "");
        result.put("favicon", "");
        
        try {
            URL url = new URL(urlStr);
            String baseUrl = url.getProtocol() + "://" + url.getHost();
            
            Document doc = Jsoup.connect(urlStr)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(5000)
                    .get();
            
            String title = doc.title();
            if (title != null && !title.isEmpty()) {
                result.put("title", title);
            }
            
            var faviconLink = doc.select("link[rel~=icon]").first();
            if (faviconLink != null && faviconLink.hasAttr("href")) {
                String faviconHref = faviconLink.attr("href");
                if (!faviconHref.startsWith("http")) {
                    if (faviconHref.startsWith("//")) {
                        faviconHref = url.getProtocol() + ":" + faviconHref;
                    } else if (faviconHref.startsWith("/")) {
                        faviconHref = baseUrl + faviconHref;
                    } else {
                        faviconHref = baseUrl + "/" + faviconHref;
                    }
                }
                result.put("favicon", faviconHref);
            } else {
                result.put("favicon", baseUrl + "/favicon.ico");
            }
        } catch (Exception e) {
            log.warn("Failed to fetch page info for url: {}", urlStr, e);
            result.put("favicon", getDefaultFavicon(urlStr));
        }
        
        return result;
    }

    private String getDefaultFavicon(String urlStr) {
        try {
            URL url = new URL(urlStr);
            return url.getProtocol() + "://" + url.getHost() + "/favicon.ico";
        } catch (Exception e) {
            return "";
        }
    }
}