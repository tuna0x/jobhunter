package vn.project.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.project.jobhunter.domain.Permission;
import vn.project.jobhunter.domain.response.ResultPaginationDTO;
import vn.project.jobhunter.service.PermissionSerVice;
import vn.project.jobhunter.util.anotation.ApiMessage;
import vn.project.jobhunter.util.error.IdInvalidException;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;





@RestController
@RequestMapping("/api/v1")

public class PermissionController {
    private final PermissionSerVice permissionService;
    public PermissionController(PermissionSerVice permissionSerVice) {
        this.permissionService = permissionSerVice;
    }

  @PostMapping("/permissions")
    @ApiMessage("Create a permission")
    public ResponseEntity<Permission> create(@Valid @RequestBody Permission p) throws IdInvalidException {
        // check exist
        if (this.permissionService.isPermissionExist(p)) {
            throw new IdInvalidException("Permission is exists already");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.permissionService.create(p));
    }

    @PutMapping("/permissions")
    @ApiMessage("Update a permission")
    public ResponseEntity<Permission> update(@Valid @RequestBody Permission p) throws IdInvalidException {
        if (this.permissionService.fetchById(p.getId()) == null) {
            throw new IdInvalidException("Permission with id = " + p.getId() + " is not exist");
        }
        // check exist module, apiPath, method
        if (this.permissionService.isPermissionExist(p)) {
            if (this.permissionService.isSameName(p)) {
                throw new IdInvalidException("Permission is exists already (module/apiPath/method)");
            }
        }

        return ResponseEntity.ok().body(this.permissionService.update(p));
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Delete a permission")
    public ResponseEntity<Void> update(@PathVariable("id") long id) throws IdInvalidException {
        // check exist
        if (this.permissionService.fetchById(id) == null) {
            throw new IdInvalidException("Permission with id = " + id + " is not exist");
        }
        this.permissionService.delete(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/permissions")
    @ApiMessage("Fetch all permission")
    public ResponseEntity<ResultPaginationDTO> getAll(
            @Filter Specification<Permission> spec,
            Pageable pageable) {
        return ResponseEntity.ok().body(this.permissionService.getAll(spec, pageable));
    }

}
