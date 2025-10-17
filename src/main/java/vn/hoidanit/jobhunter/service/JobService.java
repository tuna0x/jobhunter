package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.hoidanit.jobhunter.domain.response.job.ResUpdateJobDTO;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.SkillRepository;

@Service
@AllArgsConstructor
public class JobService {
    private final JobRepository jobRepository;
    private final SkillRepository SkillRepository;


    public ResCreateJobDTO handleCreateJob(Job newJob){
        if (newJob.getSkills()!=null) {
            List<Long> reqSkills=newJob.getSkills().stream().map(skill->skill.getId())
            .collect(Collectors.toList());

            List<Skill> dbSkills=this.SkillRepository.findByIdIn(reqSkills);
            newJob.setSkills(dbSkills);
            
        }
        Job job= this.jobRepository.save(newJob);
        ResCreateJobDTO resCreateJobDTO=new ResCreateJobDTO();
        resCreateJobDTO.setId(job.getId());
        resCreateJobDTO.setName(job.getName());
        resCreateJobDTO.setLocation(job.getLocation());
        resCreateJobDTO.setSalary(job.getSalary());
        resCreateJobDTO.setQuantity(job.getQuantity());
        resCreateJobDTO.setLevel(job.getLevel());
        resCreateJobDTO.setDescription(job.getDescription());
        resCreateJobDTO.setStartDate(job.getStartDate());
        resCreateJobDTO.setEndDate(job.getEndDate());
        resCreateJobDTO.setActive(job.isActive());
        resCreateJobDTO.setCreatedAt(job.getCreatedAt());
        resCreateJobDTO.setUpdatedAt(job.getUpdatedAt());
        resCreateJobDTO.setCreatedBy(job.getCreatedBy());
        resCreateJobDTO.setUpdatedBy(job.getUpdatedBy());

        if (job.getSkills()!=null) {
            List<String> skills=job.getSkills().stream().map(skill->skill.getName())
            .collect(Collectors.toList());
            resCreateJobDTO.setSkills(skills);
        }

        return resCreateJobDTO;
        
    }

    public ResUpdateJobDTO handleUpdateJob(Job job){
          if (job.getSkills()!=null) {
            List<Long> reqSkills=job.getSkills().stream().map(skill->skill.getId())
            .collect(Collectors.toList());

            List<Skill> dbSkills=this.SkillRepository.findByIdIn(reqSkills);
            job.setSkills(dbSkills);
            
        }
        Job curJob= this.jobRepository.save(job);
        ResUpdateJobDTO resUpdateJobDTO=new ResUpdateJobDTO();
        resUpdateJobDTO.setId(curJob.getId());
        resUpdateJobDTO.setName(curJob.getName());
        resUpdateJobDTO.setLocation(curJob.getLocation());
        resUpdateJobDTO.setSalary(curJob.getSalary());
        resUpdateJobDTO.setQuantity(curJob.getQuantity());
        resUpdateJobDTO.setLevel(curJob.getLevel());
        resUpdateJobDTO.setDescription(curJob.getDescription());
        resUpdateJobDTO.setStartDate(curJob.getStartDate());
        resUpdateJobDTO.setEndDate(curJob.getEndDate());
        resUpdateJobDTO.setActive(curJob.isActive());
        resUpdateJobDTO.setCreatedAt(curJob.getCreatedAt());
        resUpdateJobDTO.setUpdatedAt(curJob.getUpdatedAt());
        resUpdateJobDTO.setCreatedBy(curJob.getCreatedBy());
        resUpdateJobDTO.setUpdatedBy(curJob.getUpdatedBy());

        if (curJob.getSkills()!=null) {
            List<String> skills=curJob.getSkills().stream().map(skill->skill.getName())
            .collect(Collectors.toList());
            resUpdateJobDTO.setSkills(skills);
        }

        return resUpdateJobDTO;
        }

        public Job handleGetJobById(Long id){
            Optional<Job> jobOptional=this.jobRepository.findById(id);
            if (jobOptional.isPresent()) {
                return jobOptional.get();
            }
            return null;
        }

         public ResultPaginationDTO handleGetAllJobWithPaginate(Specification<Job> spec, Pageable pageable) {
        Page<Job> pageCompany = this.jobRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta metaData = new ResultPaginationDTO.Meta();

        metaData.setPage(pageable.getPageNumber() + 1);
        metaData.setPageSize(pageable.getPageSize());

        metaData.setPages(pageCompany.getTotalPages());
        metaData.setTotal(pageCompany.getTotalElements());

        rs.setMeta(metaData);
        rs.setData(pageCompany.getContent());

        return rs;
    }

        public void handleDeleteJob(Long id){
            this.jobRepository.deleteById(id);
        }

 
}
