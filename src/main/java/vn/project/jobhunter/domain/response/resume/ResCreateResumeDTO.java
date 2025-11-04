package vn.project.jobhunter.domain.response.resume;

import java.time.Instant;
import java.util.List;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.project.jobhunter.util.SecurityUtil;
import vn.project.jobhunter.util.constant.ResumeEnum;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResCreateResumeDTO {
    private Long id;
    private Instant createdAt;
    private String createdBy;



}
