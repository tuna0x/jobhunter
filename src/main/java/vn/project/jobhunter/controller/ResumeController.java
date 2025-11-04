package vn.project.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;

import jakarta.validation.Valid;
import vn.project.jobhunter.domain.Company;
import vn.project.jobhunter.domain.Job;
import vn.project.jobhunter.domain.Resume;
import vn.project.jobhunter.domain.User;
import vn.project.jobhunter.domain.response.ResultPaginationDTO;
import vn.project.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.project.jobhunter.domain.response.resume.ResResumeDTO;
import vn.project.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import vn.project.jobhunter.service.ResumeService;
import vn.project.jobhunter.service.UserService;
import vn.project.jobhunter.util.SecurityUtil;
import vn.project.jobhunter.util.anotation.ApiMessage;
import vn.project.jobhunter.util.error.IdInvalidException;

import java.lang.StackWalker.Option;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/api/v1")
public class ResumeController {

    private final ResumeService resumeService;
    private final UserService userService;
    private FilterSpecificationConverter filterSpecificationConverter;
    private FilterBuilder filterBuilder;
    public ResumeController(ResumeService resumeService,UserService userService) {
        this.resumeService = resumeService;
        this.userService=userService;
    }

   @PostMapping("/resumes")
    @ApiMessage("Create a new resume")
    public ResponseEntity<ResCreateResumeDTO> create(@Valid @RequestBody Resume resume) throws IdInvalidException {
        // check user and job
        boolean isExistID = this.resumeService.checkResumeExistByUserAndJob(resume);
        if (!isExistID) {
            throw new IdInvalidException("User ID/Job ID is not exist");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(this.resumeService.create(resume));
    }

    @PutMapping("/resumes")
    @ApiMessage("Update a resume")
    public ResponseEntity<ResUpdateResumeDTO> update(@RequestBody Resume resume) throws IdInvalidException {

        Optional<Resume> resumeOpt = this.resumeService.fetchByID(resume.getId());
        if (resumeOpt.isEmpty()) {
            throw new IdInvalidException("Resume with id :" + resume.getId() + " is not exist!");
        }
        Resume currentResume = resumeOpt.get();
        currentResume.setStatus(resume.getStatus());
        return ResponseEntity.ok().body(this.resumeService.update(currentResume));
    }

    @DeleteMapping("/resumes/{id}")
    @ApiMessage("Delete a resume")
    public ResponseEntity<Void> deleteSkill(@PathVariable("id") long id) throws IdInvalidException {
        // Check id
        Optional<Resume> resumeOptional = this.resumeService.fetchByID(id);
        if (resumeOptional == null) {
            throw new IdInvalidException("Resume with Id: " + id + " is not exist !");
        }

        this.resumeService.delete(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/resumes/{id}")
    @ApiMessage("Fetch resume by id")
    public ResponseEntity<ResResumeDTO> fetchById(@PathVariable("id") long id) throws IdInvalidException {

        // Check id
        Optional<Resume> resumeOptional = this.resumeService.fetchByID(id);
        if (resumeOptional == null) {
            throw new IdInvalidException("Resume with Id: " + id + " is not exist !");
        }
        return ResponseEntity.ok().body(this.resumeService.getResume(resumeOptional.get()));
    }

    @GetMapping("/resumes")
    @ApiMessage("Fetch all resume with paginate")
    public ResponseEntity<ResultPaginationDTO> fetchAll(
            @Filter Specification<Resume> spec,
            Pageable pageable) {

                List<Long> arrJobIds =null;
                String email=SecurityUtil.getCurrentUserLogin().isPresent()==true ? SecurityUtil.getCurrentUserLogin().get() :"";
                User curUser = this.userService.handleGetUserByUserName(email);
                if (curUser != null) {
                    Company company = curUser.getCompany();
                    if (company != null) {
                        List<Job> comJobs = company.getJobs();
                        if (comJobs != null && comJobs.size() > 0) {
                            arrJobIds = comJobs.stream().map(x-> x.getId()).collect(Collectors.toList());
                        }
                    }
                }
                Specification<Resume> jobInSpec=filterSpecificationConverter.convert(filterBuilder.field("job").in(filterBuilder.input(arrJobIds)).get());

                Specification<Resume> finalSpec = jobInSpec.and(jobInSpec);
        return ResponseEntity.ok().body(this.resumeService.fetchAll(finalSpec, pageable));
    }

    @PostMapping("/resumes/by-user")
    @ApiMessage("Get list resumes by user")
    public ResponseEntity<ResultPaginationDTO> fetchResumeByUser(
            Pageable pageable) {
        return ResponseEntity.ok().body(this.resumeService.fetchResumeByUser(pageable));
    }

}
