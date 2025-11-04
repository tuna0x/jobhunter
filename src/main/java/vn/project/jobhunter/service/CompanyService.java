package vn.project.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.boot.autoconfigure.rsocket.RSocketProperties.Server.Spec;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.project.jobhunter.domain.Company;
import vn.project.jobhunter.domain.User;
import vn.project.jobhunter.domain.response.ResultPaginationDTO;
import vn.project.jobhunter.repository.CompanyRepository;
import vn.project.jobhunter.repository.UserRepository;

@Service
public class CompanyService {
   private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyService(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public List<Company> handleGetAllCompanies() {
        return this.companyRepository.findAll();
    }

   public Company handleCreateCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public ResultPaginationDTO handleGetAllCompanies(Specification<Company> spec, Pageable pageable) {
        Page<Company> pageCompany = this.companyRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageCompany.getNumber() + 1);
        meta.setPageSize(pageCompany.getSize());
        meta.setPages(pageCompany.getTotalPages());
        meta.setTotal(pageCompany.getTotalElements());

        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(pageCompany.getContent());
        return resultPaginationDTO;
    }

    public Company fetchCompanyById(Long id) {
        Optional<Company> companyOptional = this.companyRepository.findById(id);
        if (companyOptional.isPresent()) {
            return companyOptional.get();
        }
        return null;
    }

    public Optional<Company> findCompanyById(Long id) {
        return this.companyRepository.findById(id);

    }

    public Company handleUpdateCompany(Company companyInput) {
        Optional<Company> companyOptional = this.companyRepository.findById(companyInput.getId());
        if (companyOptional.isPresent()) {
            Company currentCompany = companyOptional.get();
            currentCompany.setName(companyInput.getName());
            currentCompany.setDescription(companyInput.getDescription());
            currentCompany.setAddress(companyInput.getAddress());
            currentCompany.setLogo(companyInput.getLogo());
            return this.companyRepository.save(currentCompany);
        }
        return null;
    }

    public void handleDeleteCompany(Long id) {
        Optional<Company> optCompany = this.companyRepository.findById(id);
        if (optCompany.isPresent()) {
            Company com = optCompany.get();
            // fetch all user by company
            List<User> users = this.userRepository.findByCompany(com);
            this.userRepository.deleteAll(users);
        }
        this.companyRepository.deleteById(id);
    }

    public Optional<Company> fetchById(long id) {
        return this.companyRepository.findById(id);
    }


}
