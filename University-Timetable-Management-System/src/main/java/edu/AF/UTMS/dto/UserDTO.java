package edu.AF.UTMS.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import edu.AF.UTMS.models.consts.UserRoles;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
public class UserDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private UserRoles userRole;

    @JsonInclude(JsonInclude.Include.NON_NULL) // Include only if Faculty is not null.
    private String faculty;

    // (Optional) Set Faculty if only a Student
    /*public void setFacultyNameIfApplicable(String faculty) {
        this.faculty = faculty;
    }*/
    /*public String getFaculty() {
        return faculty;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public UserRoles getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRoles userRole) {
        this.userRole = userRole;
    }
*/

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(userRole.name()));
    }

    public String getUsername() {
        return email;
    }

}
