package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.service.error.IdInvalidException;

import java.util.List;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
public class UserController {

    private final UserService userService;
    
    
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUser() {
      return ResponseEntity.ok().body(this.userService.getAllUser());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable ("id") Long id) {
        return ResponseEntity.ok().body(this.userService.getUserById(id));
    }
    
    


    @PostMapping("/users/create")
    public ResponseEntity<User> createNewUser(@RequestBody User user) {
        User newUser=this.userService.handleCreateUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }


@ExceptionHandler(value = IdInvalidException.class)
    public ResponseEntity<String> handleBlogAlreadyExistsException(IdInvalidException idException) {
        return ResponseEntity.badRequest().body("Error");
    }


    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable ("id") Long id){
        this.userService.handleDeleteUser(id);
        return ResponseEntity.ok().body("Delete success");
    }

    @PutMapping("/users")
    public ResponseEntity<User> UpdateUser(@RequestBody User user) { 
        User cur =this.userService.updateUserById(user);
        return ResponseEntity.ok().body(cur);
    }
 
    
}
