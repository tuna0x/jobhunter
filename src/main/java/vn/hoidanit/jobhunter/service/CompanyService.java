package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.boot.autoconfigure.rsocket.RSocketProperties.Server.Spec;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.DTO.Meta;
import vn.hoidanit.jobhunter.domain.DTO.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company handleCreateCompany(Company company) {
    return this.companyRepository.save(company);
    }

    public ResultPaginationDTO handleGetAllCompanies(Specification<Company> spec, Pageable page) {
        Page<Company> pageUser = this.companyRepository.findAll(spec,page);
        ResultPaginationDTO resultPaginationDTO=new ResultPaginationDTO();
        Meta meta=new Meta();

        meta.setPage(page.getPageNumber() +1);
        meta.setPageSize(page.getPageSize());
           
        meta.setTotal(pageUser.getTotalElements());
        meta.setPages(pageUser.getTotalPages());                   

        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setData(pageUser.getContent());

        return resultPaginationDTO;
    }

    public Company handleGetCompanyById(Long id) {
        Optional<Company> companyOptional = this.companyRepository.findById(id);
        if (companyOptional.isPresent()) {
            return companyOptional.get();
        }
        return null;
    }

    public Company handleUpdateCompany( Company c) {
        Company existingCompany = this.handleGetCompanyById(c.getId());
        if (existingCompany != null) {
            existingCompany.setName(c.getName());
            existingCompany.setAddress(c.getAddress());
            existingCompany.setLogo(c.getLogo());
            existingCompany.setUpdatedAt(null);
            existingCompany.setUpdatedBy(null);
            existingCompany.setDescription(c.getDescription());
            // Cập nhật các trường khác nếu cần
            
            existingCompany=this.companyRepository.save(existingCompany);
        }
        return existingCompany;
    }

    public void handleDeleteCompany(Long id) {
        this.companyRepository.deleteById(id);
    }
    
}
