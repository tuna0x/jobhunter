package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import vn.hoidanit.jobhunter.service.ResumeService;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import java.lang.StackWalker.Option;
import java.util.Optional;

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
    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping("/resumes")
    public ResponseEntity<ResCreateResumeDTO> createNewResume(@Valid @RequestBody Resume resume) throws IdInvalidException{

        boolean isIdExist=this.resumeService.checkResumeExistByUserAndJob(resume);
        if (!isIdExist) {
            throw new IdInvalidException("User and jov is not existed resume");
        }
        ResCreateResumeDTO resCreateResumeDTO=this.resumeService.createNewResume(resume);
        return ResponseEntity.status(HttpStatus.CREATED).body(resCreateResumeDTO);
    }

    @PutMapping("resumes")
    public ResponseEntity<ResUpdateResumeDTO> updateResume(@RequestBody Resume resume) throws IdInvalidException{
        //check id exist
        Optional<Resume> reOptional=this.resumeService.getResumeById(resume.getId());
        if (!reOptional.isPresent()) {
            throw new IdInvalidException("Resume id is not exist");
        }
        Resume resumeToUpdate=reOptional.get();
        resumeToUpdate.setStatus(resume.getStatus());

        return ResponseEntity.ok().body(this.resumeService.updateResume(resumeToUpdate));
    }

    @DeleteMapping("/resumes/{id}")
    public ResponseEntity<Void> deleteResume(@PathVariable("id") Long id) throws IdInvalidException{
        //check id exist
        Optional<Resume> reOptional=this.resumeService.getResumeById(id);
        if (!reOptional.isPresent()) {
            throw new IdInvalidException("Resume id is not exist");
        }
        this.resumeService.deleteResume(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/resumes/{id}")
    public ResponseEntity<ResResumeDTO> getResumeById(@PathVariable("id") Long id) throws IdInvalidException{
        Optional<Resume> reOptional=this.resumeService.getResumeById(id);
        if (!reOptional.isPresent()) {
            throw new IdInvalidException("Resume id is not exist");
        }
        return ResponseEntity.ok().body(this.resumeService.getResumeById(reOptional.get()));
    }

    @GetMapping("/resumes")
    public ResponseEntity<ResultPaginationDTO> getAllResume(@Filter Specification<Resume> spec, Pageable pageable){
        return ResponseEntity.ok().body(this.resumeService.getAllResume(spec, pageable));

    }


    @PostMapping("/resumes/by-user")
    public ResponseEntity<ResultPaginationDTO> getResumeByUser(Pageable pageable){
        return ResponseEntity.ok().body(this.resumeService.getResumeByUser(pageable));
    }
}
