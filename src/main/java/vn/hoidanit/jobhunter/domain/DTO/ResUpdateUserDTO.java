package vn.hoidanit.jobhunter.domain.DTO;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.constant.GenderEnum;

@Getter
@Setter
public class ResUpdateUserDTO {
    private Long id;
    private String name;
    private int age;
    private String address;
    private GenderEnum gender;
    private Instant updatedAt;
}
