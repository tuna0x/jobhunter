package vn.hoidanit.jobhunter.domain.response.resume;

import java.time.Instant;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.constant.ResumeEnum;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResResumeDTO {

    private Long id;
    private String email;
    private String url;

    @Enumerated(EnumType.STRING)
    private ResumeEnum status;

    private Instant createdAt;
    private Instant updatedAt;

    private String createdBy;
    private String updatedBy;

    private UserResume user;
    private JobResume job;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
    public static class UserResume {
    private Long id;
    private String name;
    }

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
    public static class JobResume {
    private Long id;
    private String name;

    }
}
