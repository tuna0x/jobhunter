package vn.project.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;

import jakarta.validation.Valid;
import vn.project.jobhunter.domain.Job;
import vn.project.jobhunter.domain.Resume;
import vn.project.jobhunter.domain.User;
import vn.project.jobhunter.domain.response.ResultPaginationDTO;
import vn.project.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.project.jobhunter.domain.response.resume.ResResumeDTO;
import vn.project.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import vn.project.jobhunter.repository.JobRepository;
import vn.project.jobhunter.repository.ResumeRepository;
import vn.project.jobhunter.repository.UserRepository;
import vn.project.jobhunter.util.SecurityUtil;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    @Autowired
    private FilterParser filterParser;
    @Autowired
    private FilterSpecificationConverter filterSpecificationConverter;

    public ResumeService(ResumeRepository resumeRepository, UserRepository userRepository, JobRepository jobRepository) {
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

   public boolean checkResumeExistByUserAndJob(Resume resume) {

        // check user by id
        if (resume.getUser() == null) {
            return false;
        }
        Optional<User> userOpt = this.userRepository.findById(resume.getUser().getId());
        if (userOpt.isEmpty()) {
            return false;
        }

        // Check job
        if (resume.getJob() == null) {
            return false;
        }
        Optional<Job> jobOpt = this.jobRepository.findById(resume.getJob().getId());
        if (jobOpt.isEmpty()) {
            return false;
        }

        return true;
    }

    public ResCreateResumeDTO create(Resume resume) {
        resume = this.resumeRepository.save(resume);

        ResCreateResumeDTO res = new ResCreateResumeDTO();
        res.setId(resume.getId());
        res.setCreatedAt(resume.getCreatedAt());
        res.setCreatedBy(resume.getCreatedBy());

        return res;
    }

    public Optional<Resume> fetchByID(Long id) {
        return this.resumeRepository.findById(id);
    }

    public ResUpdateResumeDTO update(Resume resume) {
        resume = this.resumeRepository.save(resume);
        ResUpdateResumeDTO res = new ResUpdateResumeDTO();
        res.setUpdatedAt(resume.getUpdatedAt());
        res.setUpdatedBy(resume.getUpdatedBy());
        return res;
    }

    public void delete(long id) {
        this.resumeRepository.deleteById(id);
    }

    public ResResumeDTO getResume(Resume resume) {
        ResResumeDTO res = new ResResumeDTO();
        res.setId(resume.getId());
        res.setEmail(resume.getEmail());
        res.setUrl(resume.getUrl());
        res.setStatus(resume.getStatus());
        res.setCreatedAt(resume.getCreatedAt());
        res.setUpdatedAt(resume.getUpdatedAt());
        res.setCreatedBy(resume.getCreatedBy());
        res.setUpdatedBy(resume.getUpdatedBy());

        if (resume.getJob() != null) {
            res.setCompanyName(resume.getJob().getCompany().getName());
        }
        res.setUser(new ResResumeDTO.UserResume(resume.getUser().getId(), resume.getUser().getName()));
        res.setJob(new ResResumeDTO.JobResume(resume.getJob().getId(), resume.getJob().getName()));
        return res;
    }

    public ResultPaginationDTO fetchAll(Specification<Resume> spec, Pageable pageable) {
        Page<Resume> pageSkill = this.resumeRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageSkill.getNumber() + 1);
        meta.setPageSize(pageSkill.getSize());
        meta.setPages(pageSkill.getTotalPages());
        meta.setTotal(pageSkill.getTotalElements());

        resultPaginationDTO.setMeta(meta);

        List<ResResumeDTO> listResume = pageSkill.getContent()
                .stream().map(item -> this.getResume(item))
                .collect(Collectors.toList());

        resultPaginationDTO.setResult(listResume);

        return resultPaginationDTO;
    }

    public ResultPaginationDTO fetchResumeByUser(Pageable pageable) {
        // query builder
        // Get email
        String email = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        FilterNode node = filterParser.parse("email='" + email + "'");
        FilterSpecification<Resume> spec = filterSpecificationConverter.convert(node);

        Page<Resume> pageSkill = this.resumeRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageSkill.getNumber() + 1);
        meta.setPageSize(pageSkill.getSize());
        meta.setPages(pageSkill.getTotalPages());
        meta.setTotal(pageSkill.getTotalElements());

        resultPaginationDTO.setMeta(meta);

        List<ResResumeDTO> listResume = pageSkill.getContent()
                .stream().map(item -> this.getResume(item))
                .collect(Collectors.toList());

        resultPaginationDTO.setResult(listResume);

        return resultPaginationDTO;
    }

}
