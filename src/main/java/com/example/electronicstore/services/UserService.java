package com.example.electronicstore.services;

import com.example.electronicstore.dtos.PageableResponse;
import com.example.electronicstore.dtos.UserDto;
import com.example.electronicstore.entities.User;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, String userId);

    void deleteUser(String userId);

    PageableResponse<UserDto> getAllUsers(int pageNumber, int pageSize, String sortBy, String sortDir);

    UserDto getUserById(String userId);

    UserDto getUserByEmail(String email);

    List<UserDto> searchUser(String keyword);

}
