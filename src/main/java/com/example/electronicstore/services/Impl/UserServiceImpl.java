package com.example.electronicstore.services.Impl;

import com.example.electronicstore.dtos.PageableResponse;
import com.example.electronicstore.dtos.UserDto;
import com.example.electronicstore.entities.User;
import com.example.electronicstore.exceptions.ResourceNotFoundException;
import com.example.electronicstore.helper.Helper;
import com.example.electronicstore.repositories.UserRepository;
import com.example.electronicstore.services.UserService;
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
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper mapper;

    @Value("${user.profile.image.path}")
    private String imagePath;

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public UserDto createUser(UserDto userDto) {

        String userId = UUID.randomUUID().toString();
        userDto.setUserId(userId);

        User user = dtoToEntity(userDto);
        User savedUser = userRepository.save(user);
        UserDto newDto = entityToDto(savedUser);
        return newDto;
    }

    @Override
    public UserDto updateUser(UserDto userDto, String userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with given id"));
        user.setName(userDto.getName());
        user.setAbout(userDto.getAbout());
        user.setGender(userDto.getGender());
        user.setPassword(userDto.getPassword());
        user.setImageName(userDto.getImageName());

        User updatedUser = userRepository.save(user);
        UserDto updateDto = entityToDto(updatedUser);
        return updateDto;
    }

    @Override
    public void deleteUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with given id"));

        String fullPath = imagePath + user.getImageName();

        try {
            Path path = Paths.get(fullPath);
            Files.delete(path);
        }catch (NoSuchFileException ex) {
            logger.info("User image nt found in folder");
            ex.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        userRepository.delete(user);
    }

    @Override
    public PageableResponse<UserDto> getAllUsers(int pageNumber, int pageSize, String sortBy, String sortDir) {

        Sort sort = (sortDir.equalsIgnoreCase("desc")) ? (Sort.by(sortBy).descending()): (Sort.by(sortBy).ascending());

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<User> page = userRepository.findAll(pageable);

        PageableResponse<UserDto> response = Helper.getPageableResponse(page, UserDto.class);
        return response;

    }

    @Override
    public UserDto getUserById(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with given id"));
        return entityToDto(user);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        return entityToDto(user);
    }

    @Override
    public List<UserDto> searchUser(String keyword) {
        List<User> users = userRepository.findByNameContaining(keyword);
        List<UserDto> dtoList = users.stream().map(user -> entityToDto(user)).collect(Collectors.toList());
        return dtoList;
    }

    private UserDto entityToDto(User savedUser) {
//        UserDto userDto = UserDto.builder()
//                .userId(savedUser.getUserId())
//                .name(savedUser.getName())
//                .email(savedUser.getEmail())
//                .password(savedUser.getPassword())
//                .about(savedUser.getAbout())
//                .gender(savedUser.getGender())
//                .imageName(savedUser.getImageName()).build();

        return mapper.map(savedUser,UserDto.class);
    }

    private User dtoToEntity(UserDto userDto) {
//    User user = User.builder()
//            .userId(userDto.getUserId())
//            .name(userDto.getName())
//            .email(userDto.getEmail())
//            .password(userDto.getPassword())
//            .about(userDto.getAbout())
//            .gender(userDto.getGender())
//            .imageName(userDto.getImageName()).build();

            return mapper.map(userDto,User.class);
    }

}
