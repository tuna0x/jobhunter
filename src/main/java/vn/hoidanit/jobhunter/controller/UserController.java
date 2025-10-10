package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import vn.hoidanit.jobhunter.config.SecurityConfiguration;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.DTO.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.DTO.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.DTO.ResUserDTO;
import vn.hoidanit.jobhunter.domain.DTO.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;




@RestController
@RequestMapping("/api/v1")

public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    
    

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    public ResponseEntity<ResCreateUserDTO> createNewUser(@RequestBody User newUser)
     throws IdInvalidException {
      boolean isEmailExist=this.userService.existsByEmail(newUser.getEmail());
      if (isEmailExist) {
        throw new IdInvalidException("Email " +newUser.getEmail()+" đã ton tai");
      }
        String hashPassword =this.passwordEncoder.encode(newUser.getPassword());
        newUser.setPassword(hashPassword);
        User user=this.userService.handleCreateUser(newUser);
        ResCreateUserDTO createUserDTO= this.userService.convertToResCreateUserDTO(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createUserDTO);
    }

    

    @GetMapping("/users")
    public ResponseEntity<ResultPaginationDTO> getAllUser(
      @Filter Specification<User> spec,
      Pageable pageable){

      ResultPaginationDTO curList=this.userService.getAllUser(spec,pageable);

      return ResponseEntity.ok().body(curList);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable ("id") Long id) throws IdInvalidException {
      User cur=this.userService.getUserById(id);
      if (cur==null) {
        throw new IdInvalidException("Id ko ton tai");
        
      }
      ResUserDTO res=this.userService.convertToResUserDTO(cur);
        return ResponseEntity.ok().body(res);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable ("id") Long id) throws IdInvalidException{
        User cur=this.userService.getUserById(id);
        if (cur==null) {
          throw new IdInvalidException("Id ko ton tai");
          
        }
        this.userService.handleDeleteUser(id);
        return ResponseEntity.ok().body("Delete success");
    }

    @PutMapping("/users")
    public ResponseEntity<ResUpdateUserDTO> UpdateUser(@RequestBody User user)
     throws IdInvalidException{ 
        User cur =this.userService.updateUserById(user);
        ResUpdateUserDTO res=this.userService.convertToResUpdateUserDTO(cur);
        return ResponseEntity.ok().body(res);
    }
 
    
}
