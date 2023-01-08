package at.qe.skeleton.tests;


import at.qe.skeleton.model.Deck;
import at.qe.skeleton.model.DeckStatus;
import at.qe.skeleton.model.User;
import at.qe.skeleton.model.UserRole;
import at.qe.skeleton.repositories.DeckRepository;
import at.qe.skeleton.services.UserService;
import at.qe.skeleton.ui.controllers.AdminController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@WebAppConfiguration
public class AdminControllerTest {
    @Autowired
    UserService userService;
    @Autowired
    DeckRepository deckRepository;
    @Autowired
    AdminController adminController;

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void deletionOfUserOnUserNULL() {
        assertEquals(AdminController.DoDeleteUserErrors.USER_WAS_NULL, adminController.doDeleteUser(null));
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void deletionOfLastAdmin() {
        List<User> admins = new LinkedList<>(userService.getAllAdmins());
        while (admins.size() > 1) {
            adminController.doDeleteUser(admins.remove(0));
        }
        assertEquals(AdminController.DoDeleteUserErrors.USER_IS_LAST_ADMIN, adminController.doDeleteUser(admins.remove(0)));
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void deletionOfUserSuccess() {
        User user = userService.getAllUsers().iterator().next();
        assertEquals(AdminController.DoDeleteUserErrors.SUCCESS, adminController.doDeleteUser(user));
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void checkIfDeleteActuallyDeletes() {
        int before = userService.getAllUsers().size();
        assertEquals(AdminController.DoDeleteUserErrors.SUCCESS, adminController.doDeleteUser(userService.getAllUsers().iterator().next()));
        int after = userService.getAllUsers().size();
        assertEquals(before - 1, after);
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void tryLockDeckOnDeckNULL() {
        assertEquals(AdminController.LockUnlockDeckErrors.DECK_WAS_NULL, adminController.lockDeck(null));
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void tryLockNotPublicDeck() {
        Deck deck = deckRepository.findAll().get(0);
        deck.setStatus(DeckStatus.PRIVATE);
        assertEquals(AdminController.LockUnlockDeckErrors.DECK_NOT_PUBLIC, adminController.lockDeck(deck));
        deck.setStatus(DeckStatus.LOCKED);
        assertEquals(AdminController.LockUnlockDeckErrors.DECK_NOT_PUBLIC, adminController.lockDeck(deck));
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void lockPublicDeck() {
        Deck deck = deckRepository.findAll().get(0);
        deck.setStatus(DeckStatus.PUBLIC);
        assertEquals(AdminController.LockUnlockDeckErrors.SUCCESS, adminController.lockDeck(deck));
        assertEquals(DeckStatus.LOCKED, deck.getStatus());
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void tryUnlockDeckOnDeckNULL() {
        assertEquals(AdminController.LockUnlockDeckErrors.DECK_WAS_NULL, adminController.unlockDeck(null));
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void tryUnlockNotLockedDeck() {
        Deck deck = new Deck();
        deck.setStatus(DeckStatus.PUBLIC);
        assertEquals(AdminController.LockUnlockDeckErrors.DECK_NOT_LOCKED, adminController.unlockDeck(deck));
        deck.setStatus(DeckStatus.PRIVATE);
        assertEquals(AdminController.LockUnlockDeckErrors.DECK_NOT_LOCKED, adminController.unlockDeck(deck));
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void unlockLockedDeck() {
        Deck deck = deckRepository.findAll().get(0);
        deck.setStatus(DeckStatus.LOCKED);
        assertEquals(DeckStatus.LOCKED, deck.getStatus());
        assertEquals(AdminController.LockUnlockDeckErrors.SUCCESS, adminController.unlockDeck(deck));
        assertEquals(DeckStatus.PUBLIC, deck.getStatus());
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void tryAssignAdminRightsToNULL() {
        assertEquals(AdminController.AssignAdminRightsErrors.USER_WAS_NULL, adminController.assignAdminRights(null));
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void tryAssignAdminRightsToAdmin() {
        User admin = new User();
        admin.setRoles(new TreeSet<>(List.of(UserRole.ADMIN)));
        assertEquals(AdminController.AssignAdminRightsErrors.USER_ALREADY_ADMIN, adminController.assignAdminRights(admin));
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void assignAdminRightsToUser() {
        User user = new User();
        user.setRoles(new TreeSet<>(List.of(UserRole.LEARNER)));
        assertFalse(user.getRoles().contains(UserRole.ADMIN));
        assertEquals(AdminController.AssignAdminRightsErrors.SUCCESS, adminController.assignAdminRights(user));
        assertTrue(user.getRoles().contains(UserRole.ADMIN));
    }

    private record UserCreationFields() {
        static public String username = "ThisUserNameIsSurelyNotTaken";
        static public String passwd = "pasSWD1254823!?*+";
        static public String fstName = "First";
        static public String lastName = "snd";
        static public String eMail = "user@mail.at";
        static public Set<UserRole> userRoles = new TreeSet<>();
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void tryCreateUserOnUsernameNULL() {
        Set<AdminController.UserCreationErrors> errors = adminController.createNewUser(
                null,
                UserCreationFields.passwd,
                UserCreationFields.fstName,
                UserCreationFields.lastName,
                UserCreationFields.eMail,
                UserCreationFields.userRoles
        );
        assertEquals(1, errors.size());
        assertTrue(errors.contains(AdminController.UserCreationErrors.USERNAME_NULL));
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void tryCreateUserOnPasswordNULL() {
        Set<AdminController.UserCreationErrors> errors = adminController.createNewUser(
                UserCreationFields.username,
                null,
                UserCreationFields.fstName,
                UserCreationFields.lastName,
                UserCreationFields.eMail,
                UserCreationFields.userRoles
        );
        assertEquals(1, errors.size());
        assertTrue(errors.contains(AdminController.UserCreationErrors.PASSWORD_NULL));
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void tryCreateUserOnFirstNameNULL() {
        Set<AdminController.UserCreationErrors> errors = adminController.createNewUser(
                UserCreationFields.username,
                UserCreationFields.passwd,
                null,
                UserCreationFields.lastName,
                UserCreationFields.eMail,
                UserCreationFields.userRoles
        );
        assertEquals(1, errors.size());
        assertTrue(errors.contains(AdminController.UserCreationErrors.FIRST_NAME_NULL));
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void tryCreateUserOnLastNameNULL() {
        Set<AdminController.UserCreationErrors> errors = adminController.createNewUser(
                UserCreationFields.username,
                UserCreationFields.passwd,
                UserCreationFields.fstName,
                null,
                UserCreationFields.eMail,
                UserCreationFields.userRoles
        );
        assertEquals(1, errors.size());
        assertTrue(errors.contains(AdminController.UserCreationErrors.LAST_NAME_NULL));
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void tryCreateUserOnEMailNULL() {
        Set<AdminController.UserCreationErrors> errors = adminController.createNewUser(
                UserCreationFields.username,
                UserCreationFields.passwd,
                UserCreationFields.fstName,
                UserCreationFields.lastName,
                null,
                UserCreationFields.userRoles
        );
        assertEquals(1, errors.size());
        assertTrue(errors.contains(AdminController.UserCreationErrors.EMAIL_NULL));
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void tryCreateUserWithEmptyUserName() {
        Set<AdminController.UserCreationErrors> errors = adminController.createNewUser(
                "",
                UserCreationFields.passwd,
                UserCreationFields.fstName,
                UserCreationFields.lastName,
                UserCreationFields.eMail,
                UserCreationFields.userRoles
        );
        assertEquals(1, errors.size());
        assertTrue(errors.contains(AdminController.UserCreationErrors.USERNAME_TO_SHORT));
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void tryCreateUserMoreThan100Chars() {
        Set<AdminController.UserCreationErrors> errors = adminController.createNewUser(
                "User".repeat(30),
                UserCreationFields.passwd,
                UserCreationFields.fstName,
                UserCreationFields.lastName,
                UserCreationFields.eMail,
                UserCreationFields.userRoles
        );
        assertEquals(1, errors.size());
        assertTrue(errors.contains(AdminController.UserCreationErrors.USERNAME_TO_LONG));
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void tryCreateUserOnUsernameAlreadyExists() {
        Set<AdminController.UserCreationErrors> errors = adminController.createNewUser(
                userService.getAllUsers().iterator().next().getUsername(),
                UserCreationFields.passwd,
                UserCreationFields.fstName,
                UserCreationFields.lastName,
                UserCreationFields.eMail,
                UserCreationFields.userRoles
        );
        assertEquals(1, errors.size());
        assertTrue(errors.contains(AdminController.UserCreationErrors.USERNAME_ALREADY_EXISTS));
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void tryCreateUserOnEmailAlreadyExists() {
        User user1 = new User(
                UserCreationFields.username,
                UserCreationFields.passwd,
                UserCreationFields.fstName,
                UserCreationFields.lastName,
                UserCreationFields.eMail,
                true,
                UserCreationFields.userRoles
        );
        User user2 = new User(
                UserCreationFields.username + "x",
                UserCreationFields.passwd,
                UserCreationFields.fstName,
                UserCreationFields.lastName,
                UserCreationFields.eMail,
                true,
                UserCreationFields.userRoles
        );
        assertNull(adminController.createNewUser(user1));
        Set<AdminController.UserCreationErrors> errors = adminController.createNewUser(user2);
        assertEquals(1, errors.size());
        assertTrue(errors.contains(AdminController.UserCreationErrors.EMAIL_ALREADY_EXISTS));
    }

    @ParameterizedTest
    @Transactional
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @ValueSource(strings = {"@text.com", "text@text.text@", "@@@@@mail"})
    public void tryCreateUserOnEmailWrongFormat(String eMail) {
        Set<AdminController.UserCreationErrors> errors = adminController.createNewUser(
                UserCreationFields.username,
                UserCreationFields.passwd,
                UserCreationFields.fstName,
                UserCreationFields.lastName,
                eMail,
                UserCreationFields.userRoles
        );
        assertEquals(1, errors.size());
        assertTrue(errors.contains(AdminController.UserCreationErrors.EMAIL_WRONG_FORMAT));
    }

    @ParameterizedTest
    @Transactional
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @ValueSource(strings = {"mail@text.com", "text@text.text", "mail@mail"})
    public void createUserCorrectFields(String eMail) {
        Set<AdminController.UserCreationErrors> errors = adminController.createNewUser(
                UserCreationFields.username,
                UserCreationFields.passwd,
                UserCreationFields.fstName,
                UserCreationFields.lastName,
                eMail,
                UserCreationFields.userRoles
        );
        assertNull(errors);
    }


}
