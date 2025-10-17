package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service

public class UserService {
  private final UserRepository userRepository;
    private final CompanyService companyService;

    public UserService(UserRepository userRepository, CompanyService companyService) {
        this.userRepository = userRepository;
        this.companyService = companyService;
    }

    public List<User> handleFetchAllUsers() {
        return userRepository.findAll();
    }

    public ResultPaginationDTO handleGetAllUsersWithPaginate(Specification<User> spec, Pageable pageable) {
        // Page<Company> pageCompany = this.companyRepository.findAll(pageable);
        Page<User> pageUsers = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta metaData = new ResultPaginationDTO.Meta();

        metaData.setPage(pageable.getPageNumber() + 1);
        metaData.setPageSize(pageable.getPageSize());

        metaData.setPages(pageUsers.getTotalPages());
        metaData.setTotal(pageUsers.getTotalElements());

        rs.setMeta(metaData);

        List<ResUserDTO> listUserDTOs = pageUsers.getContent().stream().map(
            user-> new ResUserDTO(
                user.getId(),
                user.getName(), 
                user.getEmail(), 
                user.getGender(),
                user.getAddress(),
                user.getAge(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                new ResUserDTO.CompanyUser( 
                user.getCompany()!=null ?   user.getCompany().getId() : 0,
                user.getCompany()!=null ?    user.getCompany().getName() : null
            )))
                .collect(Collectors.toList());
        rs.setData(listUserDTOs);
        return rs;
    }

    public User handleFetchUserByID(long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        return userOptional.isPresent() ? userOptional.get() : null;
    }

    public User handleGetUserByUsername(String email) {
        Optional<User> userOptional = this.userRepository.findByEmail(email);
        return userOptional.isPresent() ? userOptional.get() : null;
    }

    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public User handleCreateUser(User user) {
        boolean isExist = this.userRepository.existsByEmail(user.getEmail());

        if (user.getCompany() != null) {
            Optional<Company> companyOptional = this.companyService.findById(user.getCompany().getId());
            user.setCompany(companyOptional.isPresent() ? companyOptional.get() : null);
        }

        return this.userRepository.save(user);
    }

    public User handleUpdateUser(User user) {
        User userToUpdate = handleFetchUserByID(user.getId());
        if (userToUpdate != null) {
            userToUpdate.setName(user.getName());
            userToUpdate.setAddress(user.getAddress());
            userToUpdate.setAge(user.getAge());
            userToUpdate.setGender(user.getGender());
            if (user.getCompany() != null) {
                Optional<Company> companyOptional = this.companyService.findById(user.getCompany().getId());
                user.setCompany(companyOptional.isPresent() ? companyOptional.get() : null);
            }

            return this.userRepository.save(userToUpdate);
        }

        return null;
    }

    public void handleDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    public ResCreateUserDTO convertToResCreatedUserDTO(User user) {
        ResCreateUserDTO resUserDTO = new ResCreateUserDTO();
        ResCreateUserDTO.CompanyUser resCom = new ResCreateUserDTO.CompanyUser();

        resUserDTO.setId(user.getId());
        resUserDTO.setName(user.getName());
        resUserDTO.setEmail(user.getEmail());
        resUserDTO.setAddress(user.getAddress());
        resUserDTO.setAge(user.getAge());
        resUserDTO.setGender(user.getGender());
        resUserDTO.setCreatedAt(user.getCreatedAt());

        if (user.getCompany() != null) {
            resCom.setId(user.getCompany().getId());
            resCom.setName(user.getCompany().getName());
            resUserDTO.setCompany(resCom);
        }

        return resUserDTO;
    }

    public ResUpdateUserDTO convertResUpdatedUserDTO(User user) {
        ResUpdateUserDTO resUserDTO = new ResUpdateUserDTO();
        ResUpdateUserDTO.CompanyUser resCom = new ResUpdateUserDTO.CompanyUser();

        if (user.getCompany() != null) {
            resCom.setId(user.getCompany().getId());
            resCom.setName(user.getCompany().getName());
            resUserDTO.setCompany(resCom);
        }
        resUserDTO.setId(user.getId());
        resUserDTO.setName(user.getName());
        resUserDTO.setEmail(user.getEmail());
        resUserDTO.setAddress(user.getAddress());
        resUserDTO.setAge(user.getAge());
        resUserDTO.setGender(user.getGender());
        resUserDTO.setUpdatedAt(user.getUpdatedAt());

        return resUserDTO;
    }

    public ResUserDTO convertResGetUserDTO(User user) {
        ResUserDTO resUserDTO = new ResUserDTO();
        ResUserDTO.CompanyUser resCom = new ResUserDTO.CompanyUser();

        if (user.getCompany() != null) {
            resCom.setId(user.getCompany().getId());
            resCom.setName(user.getCompany().getName());
            resUserDTO.setCompany(resCom);
        }
        resUserDTO.setId(user.getId());
        resUserDTO.setName(user.getName());
        resUserDTO.setEmail(user.getEmail());
        resUserDTO.setAddress(user.getAddress());
        resUserDTO.setAge(user.getAge());
        resUserDTO.setGender(user.getGender());
        resUserDTO.setUpdatedAt(user.getUpdatedAt());
        resUserDTO.setCreatedAt(user.getCreatedAt());

        return resUserDTO;
    }

    public void updateUserToken(String token, String email) {
        User currentUser = this.handleGetUserByUsername(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        Optional<User> user = this.userRepository.findByRefreshTokenAndEmail(token, email);
        return user.isPresent() ? user.get() : null;
    }
}  
