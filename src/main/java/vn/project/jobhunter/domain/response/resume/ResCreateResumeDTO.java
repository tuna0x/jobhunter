package vn.hoidanit.jobhunter.domain.response.resume;

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
import vn.hoidanit.jobhunter.util.SecurityUtil;
import vn.hoidanit.jobhunter.util.constant.ResumeEnum;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResCreateResumeDTO {
    private Long id;
    private Instant createdAt;
    private String createdBy;



}
