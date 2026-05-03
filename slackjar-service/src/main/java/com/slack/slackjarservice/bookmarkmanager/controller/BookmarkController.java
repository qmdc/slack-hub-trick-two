package com.slack.slackjarservice.bookmarkmanager.controller;

import com.slack.slackjarservice.bookmarkmanager.model.dto.BookmarkCategoryDTO;
import com.slack.slackjarservice.bookmarkmanager.model.dto.BookmarkDTO;
import com.slack.slackjarservice.bookmarkmanager.model.dto.BookmarkTagDTO;
import com.slack.slackjarservice.bookmarkmanager.model.request.*;
import com.slack.slackjarservice.bookmarkmanager.service.BookmarkService;
import com.slack.slackjarservice.common.base.BaseController;
import com.slack.slackjarservice.common.response.ApiResponse;
import com.slack.slackjarservice.common.response.PageResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bookmark")
@RequiredArgsConstructor
public class BookmarkController extends BaseController {

    private final BookmarkService bookmarkService;

    @PostMapping("/pageQuery")
    public ApiResponse<PageResult<BookmarkDTO>> pageQuery(@RequestBody BookmarkQueryRequest request) {
        return success(bookmarkService.pageQuery(getLoginUserId(), request));
    }

    @GetMapping("/{id}")
    public ApiResponse<BookmarkDTO> getById(@PathVariable Long id) {
        return success(bookmarkService.getById(getLoginUserId(), id));
    }

    @PostMapping
    public ApiResponse<BookmarkDTO> create(@Valid @RequestBody BookmarkCreateRequest request) {
        return success(bookmarkService.create(getLoginUserId(), request));
    }

    @PutMapping("/{id}")
    public ApiResponse<BookmarkDTO> update(@PathVariable Long id, @RequestBody BookmarkUpdateRequest request) {
        return success(bookmarkService.update(getLoginUserId(), id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return success(bookmarkService.delete(getLoginUserId(), id));
    }

    @PostMapping("/import")
    public ApiResponse<Void> importBookmarks(@RequestBody BookmarkImportRequest request) {
        bookmarkService.importBookmarks(getLoginUserId(), request);
        return success();
    }

    @GetMapping("/export")
    public ApiResponse<List<Map<String, Object>>> exportBookmarks() {
        return success(bookmarkService.exportBookmarks(getLoginUserId()));
    }

    @GetMapping("/categories")
    public ApiResponse<List<BookmarkCategoryDTO>> listCategories() {
        return success(bookmarkService.listCategories(getLoginUserId()));
    }

    @PostMapping("/categories")
    public ApiResponse<BookmarkCategoryDTO> createCategory(@Valid @RequestBody BookmarkCategoryRequest request) {
        return success(bookmarkService.createCategory(getLoginUserId(), request));
    }

    @PutMapping("/categories/{id}")
    public ApiResponse<BookmarkCategoryDTO> updateCategory(@PathVariable Long id, @Valid @RequestBody BookmarkCategoryRequest request) {
        return success(bookmarkService.updateCategory(getLoginUserId(), id, request));
    }

    @DeleteMapping("/categories/{id}")
    public ApiResponse<Boolean> deleteCategory(@PathVariable Long id) {
        return success(bookmarkService.deleteCategory(getLoginUserId(), id));
    }

    @GetMapping("/tags")
    public ApiResponse<List<BookmarkTagDTO>> listTags() {
        return success(bookmarkService.listTags(getLoginUserId()));
    }

    @PostMapping("/tags")
    public ApiResponse<BookmarkTagDTO> createTag(@Valid @RequestBody BookmarkTagRequest request) {
        return success(bookmarkService.createTag(getLoginUserId(), request));
    }

    @PutMapping("/tags/{id}")
    public ApiResponse<BookmarkTagDTO> updateTag(@PathVariable Long id, @Valid @RequestBody BookmarkTagRequest request) {
        return success(bookmarkService.updateTag(getLoginUserId(), id, request));
    }

    @DeleteMapping("/tags/{id}")
    public ApiResponse<Boolean> deleteTag(@PathVariable Long id) {
        return success(bookmarkService.deleteTag(getLoginUserId(), id));
    }
}