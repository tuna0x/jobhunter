package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.ResumeRepository;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    public ResumeService(ResumeRepository resumeRepository, UserRepository userRepository, JobRepository jobRepository) {
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    public ResCreateResumeDTO createNewResume(Resume resume) {
        resume=this.resumeRepository.save(resume);
        ResCreateResumeDTO resCreateResumeDTO=new ResCreateResumeDTO();
        resCreateResumeDTO.setId(resume.getId());
        resCreateResumeDTO.setCreatedAt(resume.getCreatedAt());
        resCreateResumeDTO.setCreatedBy(resume.getCreatedBy());

        return resCreateResumeDTO;
    }

    public Optional<Resume> getResumeById(Long id) {
        Optional<Resume> resume= this.resumeRepository.findById(id);
        return resume;
    }

    public ResUpdateResumeDTO updateResume(Resume resume) {
       Optional <Resume> reOptional=this.getResumeById(resume.getId());
        if (reOptional.isPresent()) {
            resume=this.resumeRepository.save(resume);
            ResUpdateResumeDTO resUpdateResumeDTO=new ResUpdateResumeDTO();
            resUpdateResumeDTO.setUpdatedAt(resume.getUpdatedAt());
            resUpdateResumeDTO.setUpdatedBy(resume.getUpdatedBy());
            return resUpdateResumeDTO;
        }
        return null;
    }

    public void deleteResume(Long id) {
        this.resumeRepository.deleteById(id);
    }

    public boolean checkResumeExistByUserAndJob(Resume resume) {
        if (resume.getUser()==null) {
            return false;
        }
        Optional<User> userOptional=this.userRepository.findById(resume.getUser().getId());
        if (userOptional.isEmpty()) {
            return false;
        }

        if (resume.getJob()==null) {
            return false;
        }
        Optional<Job> jobOptional=this.jobRepository.findById(resume.getJob().getId());
        if (jobOptional.isEmpty()) {
            return false;
        }
        return true;
    }

    public ResResumeDTO getResumeById(Resume resume) {
        ResResumeDTO resResumeDTO=new ResResumeDTO();
        resResumeDTO.setId(resume.getId());
        resResumeDTO.setEmail(resume.getEmail());
        resResumeDTO.setStatus(resume.getStatus());
        resResumeDTO.setCreatedAt(resume.getCreatedAt());
        resResumeDTO.setCreatedBy(resume.getCreatedBy());
        resResumeDTO.setUpdatedAt(resume.getUpdatedAt());
        resResumeDTO.setUpdatedBy(resume.getUpdatedBy());

        resResumeDTO.setUser(new ResResumeDTO.UserResume(resume.getUser().getId(), resume.getUser().getName()));
        resResumeDTO.setJob(new ResResumeDTO.JobResume(resume.getJob().getId(), resume.getJob().getName()));
        return resResumeDTO;
    }

    public ResultPaginationDTO getAllResume(Specification <Resume> spec, Pageable pageable) {
        Page<Resume> pageResume = this.resumeRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta metaData = new ResultPaginationDTO.Meta();

        metaData.setPage(pageable.getPageNumber() + 1);
        metaData.setPageSize(pageable.getPageSize());

        metaData.setPages(pageResume.getTotalPages());
        metaData.setTotal(pageResume.getTotalElements());

        rs.setMeta(metaData);

        // remove sensitive data

        List<ResResumeDTO> listResume=pageResume.getContent().stream()
        .map(item->this.getResumeById(item))
        .collect(Collectors.toList());

        rs.setData(listResume);
        return rs;
    }

}
