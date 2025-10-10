package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.DTO.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.DTO.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.DTO.ResUserDTO;
import vn.hoidanit.jobhunter.domain.DTO.Meta;
import vn.hoidanit.jobhunter.domain.DTO.ResultPaginationDTO;
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
    public ResCreateUserDTO convertToResCreateUserDTO(User user){
        ResCreateUserDTO res=new ResCreateUserDTO();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAddress(user.getAddress());
        res.setAge(user.getAge());
        res.setCreatedAt(user.getCreatedAt());
        return res;
    }

    public void handleDeleteUser(Long id){
         this.userRepository.deleteById(id);
    }

    public User handleGetUserByUsername(String username){
       return this.userRepository.findByEmail(username);
    }

    public ResultPaginationDTO getAllUser(Specification<User> spec,Pageable page){
        Page<User> pageUser = this.userRepository.findAll(spec,page);
        ResultPaginationDTO resultPaginationDTO=new ResultPaginationDTO();
        Meta meta=new Meta();
        meta.setPage(page.getPageNumber() +1);
        meta.setPageSize(page.getPageSize());

        meta.setTotal(pageUser.getTotalElements());
        meta.setPage(pageUser.getTotalPages());
        
        resultPaginationDTO.setMeta(meta);

        List<ResUserDTO> listUser=pageUser.getContent().stream()
        .map(user->new ResUserDTO(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getAge(),
            user.getAddress(),
            user.getGender(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        )).collect(Collectors.toList());

        resultPaginationDTO.setData(listUser);
       return resultPaginationDTO;
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
            currentUser.setName(user.getName());
            currentUser.setAddress(user.getAddress());
            currentUser.setAge(user.getAge());
            currentUser.setGender(user.getGender());
            
            currentUser= this.userRepository.save(currentUser);
        }
        return currentUser;
    }

    public ResUpdateUserDTO convertToResUpdateUserDTO(User user){
        ResUpdateUserDTO res=new ResUpdateUserDTO();
        res.setId(user.getId());
        res.setName(user.getName());
        res.setAddress(user.getAddress());
        res.setAge(user.getAge());
        res.setGender(user.getGender());
        res.setUpdatedAt(user.getUpdatedAt());
        return res;
    }
    public ResUserDTO convertToResUserDTO(User user){
        ResUserDTO res=new ResUserDTO();
        res.setId(user.getId());
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setAge(user.getAge());
        res.setAddress(user.getAddress());
        res.setGender(user.getGender());
        res.setCreatedAt(user.getCreatedAt());
        res.setUpdatedAt(user.getUpdatedAt());
        return res;
    }

    public Boolean existsByEmail(String email){
        return this.userRepository.existsByEmail(email);
    }

    public void updateUserToken(String token,String email){
        User curUser=this.handleGetUserByUsername(email);
        if (curUser!=null) {
           curUser.setRefreshToken(token);
              this.userRepository.save(curUser);
    }
}
    public User getUserByRefreshTokenAndEmail(String token,String email){
        return    this.userRepository.findByRefreshTokenAndEmail(token, email);
    }

}  
