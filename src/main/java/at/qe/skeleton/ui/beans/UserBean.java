package at.qe.skeleton.ui.beans;

import at.qe.skeleton.model.User;
import at.qe.skeleton.model.UserRole;
import at.qe.skeleton.ui.controllers.AdminController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Component
@ViewScoped
public class UserBean implements Serializable {
    
    private User user;
    @Autowired
    private transient AdminController adminController;
    public UserBean() {
        this.user = new User();
        this.user.setRoles(new HashSet<>());
    }
    
    public boolean isAdmin() {
        return this.user.getRoles().contains(UserRole.ADMIN);
    }
    
    public void setAdmin(boolean admin) {
        Set<UserRole> roles = this.user.getRoles();
        if (admin) {
            roles.add(UserRole.ADMIN);
            this.user.setRoles(roles);
            return;
        }
        roles.remove(UserRole.ADMIN);
        this.user.setRoles(roles);
    }
    
    public boolean isLearner() {
        return this.user.getRoles().contains(UserRole.LEARNER);
    }
    
    public void setLearner(boolean learner) {
        Set<UserRole> roles = this.user.getRoles();
        if (learner) {
            roles.add(UserRole.LEARNER);
            this.user.setRoles(roles);
            return;
        }
        roles.remove(UserRole.LEARNER);
        this.user.setRoles(roles);
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public void doCreateUser() {
        this.adminController.createNewUser(this.user);
    }
}
