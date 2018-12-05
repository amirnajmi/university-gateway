package ir.co.sadad.restinterfaces.restclientmodels;


import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @author ammac
 */
public class Student implements Serializable {

    public Student() {
    }

    public Student(String name, String lastName, String nationalCode, String mobile, String email, String accountId, String studentNo) {
        this.name = name;
        this.lastName = lastName;
        this.nationalCode = nationalCode;
        this.mobile = mobile;
        this.email = email;
        this.accountId = accountId;
        this.studentNo = studentNo;
    }

    @JsonProperty
    private String name;

    @JsonProperty
    private String lastName;

    @JsonProperty
    private String nationalCode;

    @JsonProperty
    private String mobile;

    @JsonProperty
    private String email;

    @JsonProperty
    private String accountId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }


    @JsonProperty
    private String studentNo;

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

}