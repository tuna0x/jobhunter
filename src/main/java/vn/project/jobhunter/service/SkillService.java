package vn.project.jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.project.jobhunter.domain.Skill;
import vn.project.jobhunter.domain.response.ResultPaginationDTO;
import vn.project.jobhunter.repository.SkillRepository;

@Service
public class SkillService {
       private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

   public Skill handleCreateSkill(Skill skill) {
        return this.skillRepository.save(skill);
    }

    public boolean isNameExist(String name) {
        return this.skillRepository.existsByName(name);
    }

    public Skill fetchSkillById(Long id) {
        Optional<Skill> skillOptional = this.skillRepository.findById(id);
        if (skillOptional.isPresent()) {
            return skillOptional.get();
        }
        return null;
    }

    public Skill handleUpdateSkill(Skill skill) {
        return this.skillRepository.save(skill);
    }

    public ResultPaginationDTO handleGetAllSkill(Specification<Skill> spec, Pageable pageable) {
        Page<Skill> pageSkill = this.skillRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageSkill.getNumber() + 1);
        meta.setPageSize(pageSkill.getSize());
        meta.setPages(pageSkill.getTotalPages());
        meta.setTotal(pageSkill.getTotalElements());

        resultPaginationDTO.setMeta(meta);

        resultPaginationDTO.setResult(pageSkill.getContent());

        return resultPaginationDTO;
    }

    public void deleteSkill(Long id) {
        // delete job
        Optional<Skill> optSkill = this.skillRepository.findById(id);
        Skill currentSkill = optSkill.get();
        currentSkill.getJobs().forEach(job -> job.getSkills().remove(currentSkill));
        // delete skill
        this.skillRepository.delete(currentSkill);
    }
}
