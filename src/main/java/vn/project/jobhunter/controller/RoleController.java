package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.RoleService;
import vn.hoidanit.jobhunter.util.anotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import org.hibernate.query.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/api/v1")
public class RoleController {
    private final RoleService roleService;
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

     @PostMapping("/roles")
    @ApiMessage("Create a role")
    public ResponseEntity<Role> create(@Valid @RequestBody Role r) throws IdInvalidException {
        // check exist
        if (this.roleService.existByName(r.getName())) {
            throw new IdInvalidException("Role with name " + r.getName() + " is exists already");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.create(r));
    }

    @PutMapping("/roles")
    @ApiMessage("Update a role")
    public ResponseEntity<Role> update(@Valid @RequestBody Role r) throws IdInvalidException {
        if (this.roleService.fetchById(r.getId()) == null) {
            throw new IdInvalidException("Role with id = " + r.getId() + " is not exist");
        }
        // check exist
        // if (this.roleService.existByName(r.getName())) {
        // throw new IdInvalidException("Role with name " + r.getName() + " is exists
        // already");
        // }
        return ResponseEntity.ok().body(this.roleService.update(r));
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("Delete a role")
    public ResponseEntity<Void> update(@PathVariable("id") long id) throws IdInvalidException {
        // check exist
        if (this.roleService.fetchById(id) == null) {
            throw new IdInvalidException("Role with id = " + id + " is not exist");
        }
        this.roleService.delete(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/roles")
    @ApiMessage("Fetch all role")
    public ResponseEntity<ResultPaginationDTO> getAll(
            @Filter Specification<Role> spec,
            Pageable pageable) {
        return ResponseEntity.ok().body(this.roleService.getAll(spec, pageable));
    }

    @GetMapping("/roles/{id}")
    @ApiMessage("Get a role by id")
    public ResponseEntity<Role> getRoleById(@PathVariable("id") Long id) throws IdInvalidException {

        Role currentRole = this.roleService.fetchById(id);
        if (currentRole == null) {
            throw new IdInvalidException("Role with id " + id + " not found");
        }
        return ResponseEntity.ok(currentRole);
    }
}
