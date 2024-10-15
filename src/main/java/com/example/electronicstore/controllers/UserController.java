package com.example.electronicstore.controllers;

import com.example.electronicstore.dtos.ApiResponseMessage;
import com.example.electronicstore.dtos.ImageResponse;
import com.example.electronicstore.dtos.PageableResponse;
import com.example.electronicstore.dtos.UserDto;
import com.example.electronicstore.services.FileService;
import com.example.electronicstore.services.UserService;
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
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private FileService fileService;

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${user.profile.image.path}")
    private String imageUploadPath;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto){
        UserDto userDto1 = userService.createUser(userDto);
        return new ResponseEntity<>(userDto1, HttpStatus.CREATED);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable("userId") String userId,
                                              @Valid @RequestBody UserDto userDto){
        UserDto updatedUsedDto = userService.updateUser(userDto, userId);
        return new ResponseEntity<>(updatedUsedDto, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponseMessage> deleteUser(@PathVariable String userId){
        userService.deleteUser(userId);
        ApiResponseMessage message = ApiResponseMessage.builder().message("User is deleted successfully !!")
                .success(true).status(HttpStatus.OK).build();
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PageableResponse<UserDto>> getAllUsers(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "name", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir

    ){
        return new ResponseEntity<>(userService.getAllUsers(pageNumber, pageSize, sortBy, sortDir),HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable String userId){
        return new ResponseEntity<>(userService.getUserById(userId),HttpStatus.OK);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email){
        return new ResponseEntity<>(userService.getUserByEmail(email),HttpStatus.OK);
    }

    @GetMapping("/search/{keywords}")
    public ResponseEntity<List<UserDto>> searchUser(@PathVariable String keywords){
        return new ResponseEntity<>(userService.searchUser(keywords),HttpStatus.OK);
    }

    @PostMapping("/image/{userId}")
    public ResponseEntity<ImageResponse> uploadUserImage(@RequestParam("userImage") MultipartFile image, @PathVariable String userId) throws IOException {
        String imageName = fileService.uploadFile(image, imageUploadPath);
        UserDto user = userService.getUserById(userId);
        user.setImageName(imageName);
        UserDto userDto = userService.updateUser(user, userId);
        ImageResponse imageResponse = ImageResponse.builder().imageName(imageName).success(true).status(HttpStatus.CREATED).build();

        return new ResponseEntity<>(imageResponse, HttpStatus.CREATED);
    }

    @GetMapping("image/{userId}")
    public void serverUserImage(@PathVariable String userId, HttpServletResponse response) throws IOException {

        UserDto user = userService.getUserById(userId);
        logger.info("User image name : {} ", user.getImageName());
        InputStream resource = fileService.getResource(imageUploadPath, user.getImageName());

        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        StreamUtils.copy(resource, response.getOutputStream());
    }
}
