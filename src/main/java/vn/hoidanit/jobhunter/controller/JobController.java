package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.hoidanit.jobhunter.domain.response.job.ResUpdateJobDTO;
import vn.hoidanit.jobhunter.service.JobService;
import vn.hoidanit.jobhunter.util.anotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;





@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class JobController {
    private final JobService jobService;

    @PostMapping("/jobs")
    @ApiMessage( "Create new job")
    public ResponseEntity<ResCreateJobDTO> createNewJob(@Valid @RequestBody Job newJob){ {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.jobService.handleCreateJob(newJob));

    }
}

    @PutMapping("/jobs")
    @ApiMessage( "Update job by id")
    public ResponseEntity<ResUpdateJobDTO> updateJob(@Valid @RequestBody Job job) throws IdInvalidException{
        Job curJob=this.jobService.handleGetJobById(job.getId());
        if (curJob==null) {
            throw new IdInvalidException("Job not found");
        }
        
        return ResponseEntity.ok().body(this.jobService.handleUpdateJob(job));
    }

    @GetMapping("/jobs")
    public ResponseEntity<ResultPaginationDTO> getAllJob(@Filter Specification<Job> spec, Pageable pageable){
        return ResponseEntity.ok().body(this.jobService.handleGetAllJobWithPaginate(spec, pageable));
 
}

    @GetMapping("/jobs/{id}")
    public ResponseEntity<Job> getJobByID(@PathVariable("id") Long id) throws IdInvalidException{
        Job job=this.jobService.handleGetJobById(id);
        if (job==null) {
            throw new IdInvalidException("Job not found");
        }
        
        return ResponseEntity.ok().body(job);
    }

    @DeleteMapping("/jobs/{id}")
    @ApiMessage( "Delete job by id")
    public ResponseEntity<Void> deleteJobByID(@PathVariable Long id) throws IdInvalidException{
        Job job=this.jobService.handleGetJobById(id);
        if (job==null) {
            throw new IdInvalidException("Job not found");
        }
        this.jobService.handleDeleteJob(id);
        return ResponseEntity.ok().body(null);
    }
}
