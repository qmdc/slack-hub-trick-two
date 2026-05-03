package com.slack.slackjarservice.bookmarkmanager.service;

import com.slack.slackjarservice.bookmarkmanager.model.dto.BookmarkCategoryDTO;
import com.slack.slackjarservice.bookmarkmanager.model.dto.BookmarkDTO;
import com.slack.slackjarservice.bookmarkmanager.model.dto.BookmarkTagDTO;
import com.slack.slackjarservice.bookmarkmanager.model.request.*;
import com.slack.slackjarservice.common.response.PageResult;

import java.util.List;
import java.util.Map;

public interface BookmarkService {

    PageResult<BookmarkDTO> pageQuery(Long userId, BookmarkQueryRequest request);

    BookmarkDTO getById(Long userId, Long id);

    BookmarkDTO create(Long userId, BookmarkCreateRequest request);

    BookmarkDTO update(Long userId, Long id, BookmarkUpdateRequest request);

    boolean delete(Long userId, Long id);

    void importBookmarks(Long userId, BookmarkImportRequest request);

    List<Map<String, Object>> exportBookmarks(Long userId);

    List<BookmarkCategoryDTO> listCategories(Long userId);

    BookmarkCategoryDTO createCategory(Long userId, BookmarkCategoryRequest request);

    BookmarkCategoryDTO updateCategory(Long userId, Long id, BookmarkCategoryRequest request);

    boolean deleteCategory(Long userId, Long id);

    List<BookmarkTagDTO> listTags(Long userId);

    BookmarkTagDTO createTag(Long userId, BookmarkTagRequest request);

    BookmarkTagDTO updateTag(Long userId, Long id, BookmarkTagRequest request);

    boolean deleteTag(Long userId, Long id);
}