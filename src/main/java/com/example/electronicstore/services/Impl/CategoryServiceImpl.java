package com.example.electronicstore.services.Impl;

import com.example.electronicstore.dtos.CategoryDto;
import com.example.electronicstore.dtos.PageableResponse;
import com.example.electronicstore.entities.Category;
import com.example.electronicstore.exceptions.ResourceNotFoundException;
import com.example.electronicstore.helper.Helper;
import com.example.electronicstore.repositories.CategoryRepository;
import com.example.electronicstore.services.CategoryService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper mapper;

    @Value("${category.profile.coverimage.path}")
    private String imagePath;

    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Override
    public CategoryDto create(CategoryDto categoryDto) {
        // creating category id randomly

        String categoryId = UUID.randomUUID().toString();
        categoryDto.setCategoryId(categoryId);

        Category category =mapper.map(categoryDto, Category.class);
        Category savedCategory = categoryRepository.save(category);
        return mapper.map(savedCategory, CategoryDto.class);
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto, String categoryId) {

        //get category of given id
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found by given id !!"));

        //update category details
        category.setTitle(categoryDto.getTitle());
        category.setDescription(categoryDto.getDescription());
        category.setCoverImage(categoryDto.getCoverImage());
        Category updatedCategory = categoryRepository.save(category);
        return mapper.map(updatedCategory, CategoryDto.class);
    }

    @Override
    public void delete(String categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found by given id !!"));

        String fullPath = imagePath + category.getCoverImage();
        try {
            Path path = Paths.get(fullPath);
            Files.delete(path);
        }catch (NoSuchFileException ex) {
            logger.info("Category image not found in folder");
            ex.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        categoryRepository.delete(category);

    }

    @Override
    public PageableResponse<CategoryDto> getAll(int pageNumder, int pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("desc")) ? (Sort.by(sortBy).descending()) : (Sort.by(sortBy).ascending());
        Pageable pageable = PageRequest.of(pageNumder, pageSize, sort);
        Page<Category> page = categoryRepository.findAll(pageable);
        PageableResponse<CategoryDto> pageableResponse = Helper.getPageableResponse(page, CategoryDto.class);
        return pageableResponse;
    }

    @Override
    public CategoryDto get(String categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found by given id !!"));
        return mapper.map(category, CategoryDto.class);
    }

    @Override
    public List<CategoryDto> searchCategory(String keyword) {
        List<Category> categories = categoryRepository.findByTitleContaining(keyword);
        return categories.stream().map(this::entityToDto).collect(Collectors.toList());
    }


    private CategoryDto entityToDto(Category savedCategory){

        return mapper.map(savedCategory,CategoryDto.class);
    }

    private Category dtoToEntity(CategoryDto Categorydto) {

        return mapper.map(Categorydto,Category.class);
    }
}
