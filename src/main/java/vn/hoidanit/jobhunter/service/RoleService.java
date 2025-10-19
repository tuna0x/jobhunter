package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.PermissionRepository;
import vn.hoidanit.jobhunter.repository.RoleRepository;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }
    public boolean existByName(String name) {
        return this.roleRepository.existsByName(name);
    }
    public Role create(Role role) {
        // check permissions
        if (role.getPermission()!=null) {
            List<Long> reqPermissions =role.getPermission().stream().map(permissions->permissions.getId()).collect(Collectors.toList());

            List<Permission> permissions=this.permissionRepository.findByIdIn(reqPermissions);
            role.setPermission(permissions);
        }
        return this.roleRepository.save(role);
    }
    public Role getById(Long id) {
        Optional<Role> roleDb= this.roleRepository.findById(id);
        if (roleDb.isPresent()) {
            return roleDb.get();
        }
        return null;
    }
    public Role update(Role role) {
        Role roleDb=this.getById(role.getId());
        //check permissions
        if (role.getPermission()!=null) {
            List<Long> reqPermission=role.getPermission().stream().map(x->x.getId()).collect(Collectors.toList());
            List<Permission> permissions=this.permissionRepository.findByIdIn(reqPermission);
            role.setPermission(permissions);
        }
        roleDb.setName(role.getName());
        roleDb.setDescription(role.getDescription());
        roleDb.setActive(role.getActive());
        roleDb.setPermission(role.getPermission());
        roleDb=this.roleRepository.save(roleDb);
        return roleDb;
    }
    public void delete(Long id) {
        this.roleRepository.deleteById(id);;
    }
    public ResultPaginationDTO getAllRoles(Specification<Role> spec, Pageable pageable) {
        Page<Role> pageRoles = this.roleRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta metaData = new ResultPaginationDTO.Meta();

        metaData.setPage(pageable.getPageNumber() + 1);
        metaData.setPageSize(pageable.getPageSize());

        metaData.setPages(pageRoles.getTotalPages());
        metaData.setTotal(pageRoles.getTotalElements());

        rs.setMeta(metaData);
        rs.setData(pageRoles.getContent());
        return rs;
    }
}
