package vn.project.jobhunter.domain.response;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import vn.project.jobhunter.util.constant.GenderEnum;

@Getter
@Setter
public class ResCreateUserDTO {
    private Long id;
    private String name;
    private String email;
    private int age;
    private String address;
    private GenderEnum gender;
    private Instant createdAt;
    private CompanyUser company;


    @Getter
    @Setter
    public static class CompanyUser{
        private Long id;
        private String name;
    }
}
