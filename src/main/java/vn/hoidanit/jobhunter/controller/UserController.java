package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.config.SecurityConfiguration;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.anotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import java.util.List;
import java.util.Optional;

import javax.naming.spi.DirStateFactory.Result;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;





@RestController
@RequestMapping("/api/v1")

public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }


    @GetMapping("/users")
    @ApiMessage("fetch all users")
    public ResponseEntity<ResultPaginationDTO> getAllUsers(
            @Filter Specification<User> spec, Pageable pageable) {

                ResultPaginationDTO resultPaginationDTO = this.userService.handleGetAllUsersWithPaginate(spec, pageable);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resultPaginationDTO);
    }

    @GetMapping("/users/{id}")
    @ApiMessage("fetch user by id")
    public ResponseEntity<ResUserDTO> getUserByID(@PathVariable("id") long id) throws IdInvalidException {
        User user = this.userService.handleFetchUserByID(id);
        if (user == null) {
            throw new IdInvalidException("Cannot find user with id " + id);
        }
        this.userService.convertResGetUserDTO(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertResGetUserDTO(user));
    }

    @PostMapping("/users")
    @ApiMessage("Create a new user")
    public ResponseEntity<ResCreateUserDTO> createNewUser(@Valid @RequestBody User user)
            throws IdInvalidException {

        boolean isExist = this.userService.isEmailExist(user.getEmail());
        if (isExist) {
            throw new IdInvalidException("Email " + user.getEmail() + " is already exist.");
        }

        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        User userCreated = this.userService.handleCreateUser(user);

        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertToResCreatedUserDTO(userCreated));
    }

    @PutMapping("/users")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User user) throws IdInvalidException {

        User userUpdated = this.userService.handleUpdateUser(user);

        if (userUpdated == null) {
            throw new IdInvalidException("Cannot update user with id " + user.getId());
        }

        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertResUpdatedUserDTO(userUpdated));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUserByID(@PathVariable("id") long id) throws IdInvalidException {

        boolean isExist = this.userService.handleFetchUserByID(id) != null;
        if (!isExist) {
            throw new IdInvalidException("Cannot delete user with id " + id);
        }
        this.userService.handleDeleteUser(id);
        return ResponseEntity.ok().body(null);
    }
}
