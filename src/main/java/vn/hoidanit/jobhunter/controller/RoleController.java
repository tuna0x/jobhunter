package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

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
    @ApiMessage("Create new role")
    public ResponseEntity<Role> createnewRole(@RequestBody Role role) throws IdInvalidException {
        //check name
        // boolean check=this.roleService.existByName(role.getName());
        // if (check) {
        //     throw new IdInvalidException("role is exist");
        // }

        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.create(role));
    }

    @PutMapping("/roles")
    @ApiMessage("Update role")
    public ResponseEntity<Role> updateRoles(@RequestBody Role role) throws IdInvalidException {

        if (this.roleService.getById(role.getId())==null) {
            throw new IdInvalidException("role is exist");
        }
        // if (this.roleService.existByName(role.getName())) {
        //     throw new IdInvalidException("role name is exist");
        // }
        return ResponseEntity.ok().body(this.roleService.update(role));
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("Delete role by id")
    public ResponseEntity<Void> deleteRoles(@PathVariable("id") Long id) throws IdInvalidException {
        if (this.roleService.getById(id)==null) {
            throw new IdInvalidException("role is not exist");
        }
        this.roleService.delete(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/roles")
    @ApiMessage("Get all roles")
    public ResponseEntity<ResultPaginationDTO> getAllRoles(@Filter Specification<Role> spec,Pageable pageable) {
        return ResponseEntity.ok().body(this.roleService.getAllRoles(spec, pageable));
    }

    @GetMapping("/role{id}")
    @ApiMessage("Get by Id")
    public ResponseEntity<Role> getById(@PathVariable ("id") Role role) throws IdInvalidException {
        Role cur=this.roleService.getById(role.getId());
        if (cur!=null) {
            throw new IdInvalidException("role is not exist");
        }
        return ResponseEntity.ok().body(cur);
    }

}
