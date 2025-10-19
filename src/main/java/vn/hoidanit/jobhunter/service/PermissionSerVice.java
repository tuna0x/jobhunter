package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResResumeDTO;
import vn.hoidanit.jobhunter.repository.PermissionRepository;

@Service
public class PermissionSerVice {
    private final PermissionRepository permissionRepository;

    public PermissionSerVice(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public boolean isPermissionExist(Permission permission) {
        return permissionRepository.existsByModuleAndApiPathAndMethod(permission.getModule(), permission.getApiPath(), permission.getMethod());
    }

    public Permission create(Permission permission) {
        return this.permissionRepository.save(permission);
    }

    public Permission update(Permission permission) {
        Permission permissionDB=this.getById(permission.getId());
        if (permissionDB!=null) {
            permissionDB.setName(permission.getName());
            permissionDB.setModule(permission.getModule());
            permissionDB.setApiPath(permission.getApiPath());
            permissionDB.setMethod(permission.getMethod());
            permissionDB=this.permissionRepository.save(permissionDB);
            return permissionDB;
        }
        return null;
    }

    public Permission getById(Long id) {
        Optional<Permission> permission = this.permissionRepository.findById(id);
        if (permission.isPresent()) {
            return permission.get();
        }
        return null;
    }

    public void delete(Long id) {
        // delete permission_role
        Optional<Permission> permissioOptional=this.permissionRepository.findById(null);
        Permission cuPermission=permissioOptional.get();
        cuPermission.getRoles().forEach(role->role.getPermission().remove(cuPermission.getRoles()));

        //delete permission
        this.permissionRepository.delete(cuPermission);
    }

    public ResultPaginationDTO getAllPermission(Specification<Permission> spec, Pageable pageable) {
         Page<Permission> pagePer = this.permissionRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta metaData = new ResultPaginationDTO.Meta();

        metaData.setPage(pageable.getPageNumber() + 1);
        metaData.setPageSize(pageable.getPageSize());

        metaData.setPages(pagePer.getTotalPages());
        metaData.setTotal(pagePer.getTotalElements());

        rs.setMeta(metaData);
        rs.setData(pagePer.getContent());
        return rs;
    }

    public boolean isSameName(Permission permission) {
        Permission perInDB=this.getById(permission.getId());
        if (perInDB!=null) {
            if (perInDB.getName().equals(permission.getName())) {
                return true;
            }
        }
        return false;
    }
}
