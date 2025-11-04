package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Role;
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
    private final RoleService roleService;

    public UserService(UserRepository userRepository, CompanyService companyService,RoleService roleService) {
        this.userRepository = userRepository;
        this.companyService = companyService;
        this.roleService=roleService;
    }

     public User handleCreateUser(User user) {
        // Check exist company
        if (user.getCompany() != null) {
            Optional<Company> optCompany = this.companyService.fetchById(
                    user.getCompany().getId());
            user.setCompany(optCompany.isPresent() ? optCompany.get() : null);
        }

        // Check exist role
        if (user.getRole() != null) {
            Role r = this.roleService.fetchById(user.getRole().getId());
            user.setRole(r);
        }

        return this.userRepository.save(user);
    }

    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public ResCreateUserDTO ConvertToResCreateDTO(User user) {
        ResCreateUserDTO resCreateUserDTO = new ResCreateUserDTO();
        ResCreateUserDTO.CompanyUser resCompany = new ResCreateUserDTO.CompanyUser();
        resCreateUserDTO.setId(user.getId());
        resCreateUserDTO.setName(user.getName());
        resCreateUserDTO.setEmail(user.getEmail());
        resCreateUserDTO.setAge(user.getAge());
        resCreateUserDTO.setGender(user.getGender());
        resCreateUserDTO.setAddress(user.getAddress());
        resCreateUserDTO.setCreatedAt(user.getCreatedAt());
        // check company
        if (user.getCompany() != null) {
            resCompany.setId(user.getCompany().getId());
            resCompany.setName(user.getCompany().getName());
            resCreateUserDTO.setCompany(resCompany);
        }

        return resCreateUserDTO;
    }

    public ResUserDTO ConvertToResUserDTO(User user) {
        ResUserDTO resUserDTO = new ResUserDTO();
        ResUserDTO.CompanyUser resCompany = new ResUserDTO.CompanyUser();
        ResUserDTO.RoleUser resUser = new ResUserDTO.RoleUser();

        resUserDTO.setId(user.getId());
        resUserDTO.setName(user.getName());
        resUserDTO.setEmail(user.getEmail());
        resUserDTO.setAge(user.getAge());
        resUserDTO.setGender(user.getGender());
        resUserDTO.setAddress(user.getAddress());
        resUserDTO.setCreatedAt(user.getCreatedAt());
        resUserDTO.setUpdatedAt(user.getUpdatedAt());

        // check company
        if (user.getCompany() != null) {
            resCompany.setId(user.getCompany().getId());
            resCompany.setName(user.getCompany().getName());
            resUserDTO.setCompany(resCompany);
        }

        // check role
        if (user.getRole() != null) {
            resUser.setId(user.getRole().getId());
            resUser.setName(user.getRole().getName());
            resUserDTO.setRole(resUser);
        }

        return resUserDTO;
    }

    public ResultPaginationDTO handleGetAllUser(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageUser.getNumber() + 1);
        meta.setPageSize(pageUser.getSize());
        meta.setPages(pageUser.getTotalPages());
        meta.setTotal(pageUser.getTotalElements());

        resultPaginationDTO.setMeta(meta);

        // Remove sensitive data
        List<ResUserDTO> listUser = pageUser.getContent()
                .stream().map(item -> this.ConvertToResUserDTO(item))
                .collect(Collectors.toList());

        resultPaginationDTO.setResult(listUser);

        return resultPaginationDTO;
    }

    public User fetchUserById(Long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        return null;
    }

    public User handleUpdateUser(User userInput) {
        User currentUser = this.fetchUserById(userInput.getId());

        if (currentUser != null) {
            currentUser.setName(userInput.getName());
            currentUser.setAddress(userInput.getAddress());
            currentUser.setGender(userInput.getGender());
            currentUser.setAge(userInput.getAge());

            // Check company
            if (userInput.getCompany() != null) {
                Optional<Company> optCompany = this.companyService.findCompanyById(
                        userInput.getCompany().getId());
                currentUser.setCompany(optCompany.isPresent() ? optCompany.get() : null);
            }
            // Check exist role
            if (userInput.getRole() != null) {
                Role r = this.roleService.fetchById(userInput.getRole().getId());
                currentUser.setRole(r);
            }
            currentUser = this.userRepository.save(currentUser);
            return currentUser;
        }
        return null;
    }

    public ResUpdateUserDTO ConvertToResUpdateUserDTO(User user) {
        ResUpdateUserDTO resUserDTO = new ResUpdateUserDTO();
        ResUpdateUserDTO.CompanyUser resCompany = new ResUpdateUserDTO.CompanyUser();
        ResUpdateUserDTO.RoleUser resUser = new ResUpdateUserDTO.RoleUser();

        resUserDTO.setId(user.getId());
        resUserDTO.setName(user.getName());
        resUserDTO.setAge(user.getAge());
        resUserDTO.setGender(user.getGender());
        resUserDTO.setAddress(user.getAddress());
        resUserDTO.setUpdatedAt(user.getUpdatedAt());

        // check company
        if (user.getCompany() != null) {
            resCompany.setId(user.getCompany().getId());
            resCompany.setName(user.getCompany().getName());
            resUserDTO.setCompany(resCompany);
        }

        // check role
        if (user.getRole() != null) {
            resUser.setId(user.getRole().getId());
            resUser.setName(user.getRole().getName());
            resUserDTO.setRole(resUser);
        }

        return resUserDTO;
    }

    public void handleDeleteUser(Long id) {
        this.userRepository.deleteById(id);
    }

    public User handleGetUserByUserName(String username) {
        return this.userRepository.findByEmail(username);
    }

    public void updateUserToken(String token, String email) {
        User currentUser = this.handleGetUserByUserName(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }
}
