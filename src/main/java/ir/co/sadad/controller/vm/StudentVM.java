package ir.co.sadad.controller.vm;

import java.time.Instant;
import java.util.Set;

public class StudentVM extends ManagedUserVM {


    private String nationalCode;

    private String mobile;

    public StudentVM(){

    }


    public StudentVM(
            Long id,
            String login,
            String password,
            String firstName,
            String lastName,
            String email,
            boolean activated,
            String langKey,
            String createdBy,
            Instant createdDate,
            String lastModifiedBy,
            Instant lastModifiedDate,
            Set<String> authorities,
            String nationalCode,
            String mobile
    ) {
        super(
                id,
                login,
                password,
                firstName,
                lastName,
                email,
                activated,
                langKey,
                createdBy,
                createdDate,
                lastModifiedBy,
                lastModifiedDate,
                authorities
        );
        this.nationalCode = nationalCode;
        this.mobile = mobile;
    }

    public String getNationalCode() {
        return nationalCode;
    }

    public void setNationalCode(String nationalCode) {
        this.nationalCode = nationalCode;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public String toString() {
        return "StudentVM{" +
                "nationalCode='" + nationalCode + '\'' +
                ", mobile='" + mobile + '\'' +
                '}';
    }
}
