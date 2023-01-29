package at.qe.skeleton.tests;

import at.qe.skeleton.model.User;
import at.qe.skeleton.services.UserService;
import at.qe.skeleton.ui.controllers.UserListController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.web.WebAppConfiguration;

import java.lang.reflect.Field;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@WebAppConfiguration
public class UserListControllerTest {

    UserListController userListController;

    @Autowired
    UserService userService;

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @DirtiesContext
    void testGetUsersGetsAllUsers(){
        userListController = new UserListController();
        try {
            Field fieldUserService = Class.forName("at.qe.skeleton.ui.controllers.UserListController").getDeclaredField("userService");
            fieldUserService.setAccessible(true);
            fieldUserService.set(userListController, this.userService);
        } catch (NoSuchFieldException | ClassNotFoundException | IllegalAccessException e) {
            e.printStackTrace();
        }
        Collection<User> userCollection = userListController.getUsers();
        Collection<User> userList = userService.getAllUsers();
        assertTrue(userList.containsAll(userCollection));
        assertEquals(userList.size(), userCollection.size());
    }

}
