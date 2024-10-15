package com.example.electronicstore.services;

import com.example.electronicstore.dtos.CategoryDto;
import com.example.electronicstore.dtos.PageableResponse;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface CategoryService {

    //create
    CategoryDto create(CategoryDto categoryDto);

    //update
    CategoryDto update(CategoryDto categoryDto, String categoryId);

    //delete
    void delete(String categoryId);

    //getall
    PageableResponse<CategoryDto> getAll(int pageNumber, int pageSize, String sortBy, String sortDir);

    //get single category detail
    CategoryDto get(String categoryId);

    //search
    List<CategoryDto> searchCategory(String keyword);

}
