package vn.project.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import vn.project.jobhunter.domain.Company;
import vn.project.jobhunter.domain.Job;
import vn.project.jobhunter.domain.Skill;
import vn.project.jobhunter.domain.response.ResultPaginationDTO;
import vn.project.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.project.jobhunter.domain.response.job.ResUpdateJobDTO;
import vn.project.jobhunter.repository.CompanyRepository;
import vn.project.jobhunter.repository.JobRepository;
import vn.project.jobhunter.repository.SkillRepository;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    private final CompanyRepository companyRepository;

     public JobService(JobRepository jobRepository, SkillRepository skillRepository,
            CompanyRepository companyRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
        this.companyRepository = companyRepository;
    }

    public ResCreateJobDTO handleCreateJob(Job job) {
        // check skills
        if (job.getSkills() != null) {
            // Get list skill id - Long
            List<Long> reqSkills = job.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());
            // Get list skill by Id
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            job.setSkills(dbSkills);
        }

        // check company
        if (job.getCompany() != null) {
            Optional<Company> companyOptional = this.companyRepository.findById(job.getCompany().getId());
            if (companyOptional.isPresent()) {
                job.setCompany(companyOptional.get());
            }
        }
        // Create job
        Job currentJob = this.jobRepository.save(job);

        // Convert response
        ResCreateJobDTO dto = new ResCreateJobDTO();
        dto.setId(currentJob.getId());
        dto.setName(currentJob.getName());
        dto.setLocation(currentJob.getLocation());
        dto.setSalary(currentJob.getSalary());
        dto.setQuantity(currentJob.getQuantity());
        dto.setLevel(currentJob.getLevel());
        dto.setStartDate(currentJob.getStartDate());
        dto.setEndDate(currentJob.getEndDate());
        dto.setActive(currentJob.isActive());
        dto.setCreatedAt(currentJob.getCreatedAt());
        dto.setCreatedBy(currentJob.getCreatedBy());

        // Set skills for current job
        if (currentJob.getSkills() != null) {
            List<String> skills = currentJob.getSkills()
                    .stream().map(s -> s.getName())
                    .collect(Collectors.toList());
            dto.setSkills(skills);

        }
        return dto;
    }

    public Optional<Job> fetchJobById(Long id) {
        return this.jobRepository.findById(id);
    }

    public ResUpdateJobDTO handleUpdateJob(Job job, Job jobInDB) {

        // check skills
        if (job.getSkills() != null) {
            // Get list skill id - Long
            List<Long> reqSkills = job.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());
            // Get list skill by Id
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            jobInDB.setSkills(dbSkills);
        }

        // check company
        if (job.getCompany() != null) {
            Optional<Company> companyOpt = this.companyRepository.findById(job.getCompany().getId());
            if (companyOpt.isPresent()) {
                jobInDB.setCompany(companyOpt.get());
            }
        }
        jobInDB.setName(job.getName());
        jobInDB.setLocation(job.getLocation());
        jobInDB.setSalary(job.getSalary());
        jobInDB.setQuantity(job.getQuantity());
        jobInDB.setLevel(job.getLevel());
        jobInDB.setStartDate(job.getStartDate());
        jobInDB.setEndDate(job.getEndDate());
        jobInDB.setActive(job.isActive());

        // Update job
        Job currentJob = this.jobRepository.save(jobInDB);

        // Convert response
        ResUpdateJobDTO dto = new ResUpdateJobDTO();
        dto.setId(currentJob.getId());
        dto.setName(currentJob.getName());
        dto.setLocation(currentJob.getLocation());
        dto.setSalary(currentJob.getSalary());
        dto.setQuantity(currentJob.getQuantity());
        dto.setLevel(currentJob.getLevel());
        dto.setStartDate(currentJob.getStartDate());
        dto.setEndDate(currentJob.getEndDate());
        dto.setActive(currentJob.isActive());
        dto.setUpdatedAt(currentJob.getUpdatedAt());
        dto.setUpdatedBy(currentJob.getUpdatedBy());

        // Set skills for current job
        if (currentJob.getSkills() != null) {
            List<String> skills = currentJob.getSkills()
                    .stream().map(s -> s.getName())
                    .collect(Collectors.toList());
            dto.setSkills(skills);

        }
        return dto;
    }

    public ResultPaginationDTO handleGetAllJob(Specification<Job> spec, Pageable pageable) {
        Page<Job> pageJob = this.jobRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageJob.getNumber() + 1);
        meta.setPageSize(pageJob.getSize());
        meta.setPages(pageJob.getTotalPages());
        meta.setTotal(pageJob.getTotalElements());

        resultPaginationDTO.setMeta(meta);

        resultPaginationDTO.setResult(pageJob.getContent());

        return resultPaginationDTO;
    }

    public void deleteJob(Long id) {
        this.jobRepository.deleteById(id);
    }

   
}
