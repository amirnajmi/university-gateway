package ir.co.sadad.restinterfaces.restclientmodels;


import javax.persistence.Basic;
import javax.persistence.Entity;

/**
 * @author ammac
 */
public class Teacher extends User {

    private String teacherNo;

    public String getTeacherNo() {
        return teacherNo;
    }

    public void setTeacherNo(String teacherNo) {
        this.teacherNo = teacherNo;
    }

}