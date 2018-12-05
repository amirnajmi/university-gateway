package ir.co.sadad.service.dto;

import ir.co.sadad.domain.Authority;
import ir.co.sadad.domain.User;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Column;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

import static ir.co.sadad.config.Constants.*;
import static java.util.stream.Collectors.toSet;

/**
 * A DTO representing a user, with his authorities.
 */
public class UserDTO implements Serializable {

    private Long id;

    @NotBlank
    @Pattern(regexp = LOGIN_REGEX)
    @Size(min = 1, max = 50)
    private String login;

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    @Email(regexp = EMAIL_REGEX, message = EMAIL_REGEX_MESSAGE)
    @Size(min = 5, max = 100)
    private String email;

    private boolean activated = false;

    @Size(min = 2, max = 5)
    private String langKey;

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    private Set<String> authorities;

    @JsonbTransient
    @NotNull
    @Size(min = 32, max = 32)
    @Column(name = "password_hash", length = 32)
    private String password;

    public UserDTO() {
    }

    public UserDTO(User user) {
        this(user.getId(), user.getLogin(), user.getFirstName(), user.getLastName(),
                user.getEmail(), user.getActivated(), user.getLangKey(),
                user.getCreatedBy(), user.getCreatedDate(), user.getLastModifiedBy(), user.getLastModifiedDate(),
                user.getAuthorities().stream().map(Authority::getName).collect(toSet()),user
        .getPassword());
    }

    public UserDTO(Long id, String login, String firstName, String lastName,
            String email, boolean activated, String langKey,
            String createdBy, Instant createdDate, String lastModifiedBy, Instant lastModifiedDate,
            Set<String> authorities,String password) {
        this.id = id;
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.activated = activated;
        this.langKey = langKey;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.lastModifiedBy = lastModifiedBy;
        this.lastModifiedDate = lastModifiedDate;
        this.authorities = authorities;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public String getLangKey() {
        return langKey;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Set<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<String> authorities) {
        this.authorities = authorities;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserDTO{"
                + "login='" + login + '\''
                + ", firstName='" + firstName + '\''
                + ", lastName='" + lastName + '\''
                + ", email='" + email + '\''
                + ", activated=" + activated
                + ", langKey='" + langKey + '\''
                + ", createdBy=" + createdBy
                + ", createdDate=" + createdDate
                + ", lastModifiedBy='" + lastModifiedBy + '\''
                + ", lastModifiedDate=" + lastModifiedDate
                + ", authorities=" + authorities
                + "}";
    }

}
