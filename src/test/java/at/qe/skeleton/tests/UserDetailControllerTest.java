package at.qe.skeleton.tests;

import at.qe.skeleton.model.User;
import at.qe.skeleton.model.UserRole;
import at.qe.skeleton.services.UserService;
import at.qe.skeleton.ui.controllers.AdminController;
import at.qe.skeleton.ui.controllers.UserDetailController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.web.WebAppConfiguration;

import java.lang.reflect.Field;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@WebAppConfiguration
public class UserDetailControllerTest {

    UserDetailController userDetailController;

    @Autowired
    AdminController adminController;

    @Autowired
    UserService userService;

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @DirtiesContext
    void testSetUserSetsUserAndDoesReloadOnUsername() {
        userDetailController = new UserDetailController();
        try {
            Field fieldUserService = Class.forName("at.qe.skeleton.ui.controllers.UserDetailController").getDeclaredField("userService");
            fieldUserService.setAccessible(true);
            fieldUserService.set(userDetailController, this.userService);
        } catch (NoSuchFieldException | ClassNotFoundException | IllegalAccessException e) {
            e.printStackTrace();
        }
        User persistetUser = userService.getAllUsers().iterator().next();
        User copieWithJustName = new User();
        copieWithJustName.setUsername(persistetUser.getUsername());
        userDetailController.setUser(copieWithJustName);
        assertEquals(persistetUser, userDetailController.getUser());
    }


    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @DirtiesContext
    void testDoSaveUserSavesUser(){
        userDetailController = new UserDetailController();
        User newUser = new User("username!:_hopefullyDoesNotExist",
                "randomPassword",
                "fstName",
                "lastName",
                "mail@mail.mail",
                true,
                null);
        try {
            Field fieldUserService = Class.forName("at.qe.skeleton.ui.controllers.UserDetailController").getDeclaredField("userService");
            Field fieldUser = Class.forName("at.qe.skeleton.ui.controllers.UserDetailController").getDeclaredField("user");
            fieldUserService.setAccessible(true);
            fieldUser.setAccessible(true);
            fieldUserService.set(userDetailController, this.userService);
            fieldUser.set(userDetailController, newUser);
        } catch (NoSuchFieldException | ClassNotFoundException | IllegalAccessException e) {
            e.printStackTrace();
        }
        assertFalse(userService.getAllUsers().contains(newUser));
        userDetailController.doSaveUser();
        assertTrue(userService.getAllUsers().contains(newUser));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @DirtiesContext
    void testDoDeleteUserDeletesUser(){
        userDetailController = new UserDetailController();
        Iterator<User> userIterator = userService.getAllUsers().iterator();
        User userToDelete = null;
        boolean found = false;
        while(userIterator.hasNext() && !found){
            userToDelete = userIterator.next();
            if(!userToDelete.getRoles().contains(UserRole.ADMIN)){
                found = true;
            }
        }
        try {
            Field fieldAdminController = Class.forName("at.qe.skeleton.ui.controllers.UserDetailController").getDeclaredField("adminController");
            Field fieldUser = Class.forName("at.qe.skeleton.ui.controllers.UserDetailController").getDeclaredField("user");
            fieldAdminController.setAccessible(true);
            fieldUser.setAccessible(true);
            fieldAdminController.set(userDetailController, this.adminController);
            fieldUser.set(userDetailController, userToDelete);
        } catch (NoSuchFieldException | ClassNotFoundException | IllegalAccessException e) {
            e.printStackTrace();
        }
        assertTrue(userService.getAllUsers().contains(userToDelete));
        userDetailController.doDeleteUser();
        assertFalse(userService.getAllUsers().contains(userToDelete));
    }
}
