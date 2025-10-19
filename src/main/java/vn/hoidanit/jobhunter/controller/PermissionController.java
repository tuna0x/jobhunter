package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.PermissionSerVice;
import vn.hoidanit.jobhunter.util.anotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

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
    private final PermissionSerVice permissionSerVice;
    public PermissionController(PermissionSerVice permissionSerVice) {
        this.permissionSerVice = permissionSerVice;
    }

    @PostMapping("/permissions")
    @ApiMessage("Create new permission")
    public ResponseEntity<Permission> createNewPermission(@Valid @RequestBody Permission permission) throws IdInvalidException{
        //check exist
        if (this.permissionSerVice.isPermissionExist(permission)) {
            throw new IdInvalidException("Permission is exist");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.permissionSerVice.create(permission));
    }

    @PutMapping("/permissions")
    @ApiMessage("Update permission by id")
    public ResponseEntity<Permission> updatePermission(@RequestBody Permission permission) throws IdInvalidException{
        //check exist by id
        if (this.permissionSerVice.getById(permission.getId())==null) {
            throw new IdInvalidException("Permission id is not exist");
        }

        //check exist by module, apiPath, method
        if (this.permissionSerVice.isPermissionExist(permission)) {
            //check name
            if (this.permissionSerVice.isSameName(permission)) {
                throw new IdInvalidException("Permission is exist");
            }
        }

        return ResponseEntity.ok().body(this.permissionSerVice.update(permission));
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Delete permission by id")
    public ResponseEntity<Void> deletePermission(@PathVariable("id") Long id) throws IdInvalidException{
        //check exist by id
        Permission permissionDB=this.permissionSerVice.getById(id);
        if (permissionDB==null) {
            throw new IdInvalidException("Permission id is not exist");
        }
        this.permissionSerVice.delete(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/permissions")
    public ResponseEntity<ResultPaginationDTO> getPermission(@Filter Specification<Permission> spec,Pageable pageable){ {
        return ResponseEntity.ok().body(this.permissionSerVice.getAllPermission(spec,pageable));
    }
}

    @GetMapping("/permissions/{id}")
    public ResponseEntity<Permission> getPermissionById(@PathVariable("id") Long id) throws IdInvalidException{
        Permission permissionDB=this.permissionSerVice.getById(id);
        if (permissionDB==null) {
            throw new IdInvalidException("Permission id is not exist");
        }
        return ResponseEntity.ok().body(permissionDB);
    }


}
