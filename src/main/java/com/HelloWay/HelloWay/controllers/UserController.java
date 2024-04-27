package com.HelloWay.HelloWay.controllers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.HelloWay.HelloWay.config.FileUploadUtil;
import com.HelloWay.HelloWay.entities.Image;
import com.HelloWay.HelloWay.entities.User;
import com.HelloWay.HelloWay.services.ImageService;
import com.HelloWay.HelloWay.services.UserService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


@RestController
@RequestMapping("/api/users")
public class UserController {
    final
    UserService userService;

    private ImageService imageService;
    @Autowired
    public UserController(UserService userService, ImageService imageService) {

        this.userService = userService;
        this.imageService = imageService ;
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @ResponseBody
    public User add_New_User(@RequestBody User user) {
        return userService.addUser(user);
    }

    @JsonIgnore
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')") // Protect this endpoint for users with ROLE_ADMIN
    @ResponseBody
    public List<User> all_users(){
        return userService.findAllUsers();
    }


    @GetMapping("/id/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROVIDER', 'USER', 'WAITER')")
    @ResponseBody
    public User user_id(@PathVariable("id") long id){
        return userService.findUserById(id);
    }


    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROVIDER', 'USER', 'WAITER')")
    @ResponseBody
    public ResponseEntity<?> updateUser(@RequestBody User user){
        List<User> existingUsers = userService.findAllUsers();
        User existingUser = userService.findUserById(user.getId());
         existingUsers.remove(existingUser);
         for (User u : existingUsers){
             if (u.getUsername().equals(user.getUsername())){
                 return ResponseEntity.badRequest().body("username exist please try again with an other");

             }
         }
        return ResponseEntity.ok().body( userService.updateUser(user)); }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROVIDER')")
    @ResponseBody
    public void supp_user(@PathVariable("id") long id){
        userService.deleteUser(id); }

    @PostMapping("/{sid}/add-image")
    public void addImage(@PathVariable Long sid, @RequestParam("image") MultipartFile multipartFile) throws IOException {

        User user = userService.findUserById(sid);

        userService.addUser(user);
        if (!multipartFile.isEmpty()) {
            String orgFileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            String ext = orgFileName.substring(orgFileName.lastIndexOf("."));
            String uploadDir = "users-photos/";
            String fileName = "user-" + user.getId() + ext;
            Image img = new Image(multipartFile, fileName, ext, multipartFile.getBytes());
            Image image = imageService.addImageLa(img);
            user.setImage(image);
            FileUploadUtil.saveFile(uploadDir,fileName,multipartFile);
            userService.updateUser(user);
        }
    }



    @RequestMapping(value = "/{sid}/display-image")
    public void getUserPhoto(HttpServletResponse response, @PathVariable("sid") long sid) throws Exception {
        User user = userService.findUserById(sid);
        Image image = user.getImage();

        if(image != null) {
            response.setContentType(image.getFileType());
            InputStream inputStream = new ByteArrayInputStream(image.getData());
            IOUtils.copy(inputStream, response.getOutputStream());
        }
    }

    @GetMapping("/all/paging")
    @PreAuthorize("hasAnyRole('ADMIN')")

    public Page<User> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return userService.getUsers(page, size);
    }

    @GetMapping("/get/moderators")
    @PreAuthorize("hasRole('ADMIN')") // Protect this endpoint for users with ROLE_ADMIN
    public ResponseEntity<?> getAllModerators(){
        List<User> moderators = userService.getAllModerators();
        if (moderators.isEmpty()){
            return ResponseEntity.badRequest().body("not found");
        }
        return ResponseEntity.ok().body(moderators);
    }
}
