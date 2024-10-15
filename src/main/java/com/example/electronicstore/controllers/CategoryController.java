package com.example.electronicstore.controllers;

import com.example.electronicstore.dtos.*;
import com.example.electronicstore.services.CategoryService;
import com.example.electronicstore.services.FileService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private FileService fileService;

    @Value("${category.profile.coverimage.path}")
    private String coverimageupload;

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    //create
    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        //call service to save object
        CategoryDto categoryDto1 = categoryService.create(categoryDto);
        return new ResponseEntity<>(categoryDto1, HttpStatus.CREATED);
    }

    //update
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable String categoryId,
                                                      @RequestBody CategoryDto categoryDto) {
        CategoryDto updatedCategory = categoryService.update(categoryDto, categoryId);
        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }

    //delete
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponseMessage> deleteCategory(@PathVariable String categoryId) {
        categoryService.delete(categoryId);
        ApiResponseMessage responseMessage = ApiResponseMessage.builder().message("Category is deleted Successfully !!").status(HttpStatus.OK).success(true).build();
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    //getall
    @GetMapping
    public ResponseEntity<PageableResponse<CategoryDto>> getAll(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "title", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ) {
        PageableResponse<CategoryDto> pageableResponse = categoryService.getAll(pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(pageableResponse, HttpStatus.OK);
    }

    //get single
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> getSingle(@PathVariable String categoryId) {
        CategoryDto categoryDto = categoryService.get(categoryId);
        return ResponseEntity.ok(categoryDto);
    }

    //searchcategory
    @GetMapping("/search/{keywords}")
    public ResponseEntity<List<CategoryDto>> searchCategory(@PathVariable String keywords) {
        return new ResponseEntity<>(categoryService.searchCategory(keywords), HttpStatus.OK);
    }

    @PostMapping("/coverimage/{categoryId}")
    public ResponseEntity<ImageResponse> uploadCoverImage(@RequestParam("coverImage") MultipartFile coverimage, @PathVariable String categoryId) throws IOException {
        String imageName = fileService.uploadFile(coverimage, coverimageupload);
        CategoryDto category = categoryService.get(categoryId);
        category.setCoverImage(imageName);
        CategoryDto categoryDto = categoryService.update(category, categoryId);
        ImageResponse imageResponse = ImageResponse.builder().imageName(imageName).success(true).status(HttpStatus.CREATED).build();

        return new ResponseEntity<>(imageResponse, HttpStatus.CREATED);
    }

    @GetMapping("coverimage/{categoryId}")
    public void serverCategoryImage(@PathVariable String categoryId, HttpServletResponse response) throws IOException {
        CategoryDto category = categoryService.get(categoryId);
        logger.info("Cover Image name : {} ", category.getCoverImage());
        InputStream resource = fileService.getResource(coverimageupload, category.getCoverImage());
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        StreamUtils.copy(resource, response.getOutputStream());

    }

    }

