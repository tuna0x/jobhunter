package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service

public class UserService {
    
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public User handleCreateUser(User user){
         return this.userRepository.save(user);
    }

    public void handleDeleteUser(Long id){
         this.userRepository.deleteById(id);
    }

    public List<User> getAllUser(){
       return this.userRepository.findAll();
    }

    public User getUserById(Long id){
        Optional<User> userOptional=this.userRepository.findById(id);
        if (userOptional.isPresent()) {
           return userOptional.get();
        }
        return null;
    }

    public User updateUserById(User user){
        User currentUser=this.getUserById(user.getId());
        if (currentUser!=null) {
            currentUser.setEmail(user.getEmail());
            currentUser.setName(user.getName());
            currentUser.setPassword(user.getPassword());

            currentUser= this.userRepository.save(currentUser);
        }
        return currentUser;
    }
}  
