package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.*;
import at.qe.skeleton.services.DeckService;
import at.qe.skeleton.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.annotation.Transient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

@Controller
@Scope("request")
public class AdminController {

    @Autowired
    @Transient
    private UserService userService;
    @Autowired
    @Transient
    private DeckService deckService;

    public enum DoDeleteUserErrors {
        USER_WAS_NULL,
        USER_IS_LAST_ADMIN,
        SUCCESS
    }

    /**
     * Action to delete the referenced user.
     * If the user is the last admin in the system the program throws an Exception
     *
     * @param user The user to be deleted
     * @throws IllegalStateException If the user is the last admin
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public DoDeleteUserErrors doDeleteUser(User user) {
        if (user == null) {
            return DoDeleteUserErrors.USER_WAS_NULL;
        }
        /*
            In case of user being an admin get the number of Admins the system holds at the moment.
            In case there is only 1 admin an exception will be thrown.
            We assume the system needs at least one admin.
         */
        if (user.getRoles().contains(UserRole.ADMIN) && userService.getAllAdmins().size() <= 1) {
            return DoDeleteUserErrors.USER_IS_LAST_ADMIN;
        }
        this.userService.deleteUser(user);
        return DoDeleteUserErrors.SUCCESS;
    }

    public enum LockUnlockDeckErrors {
        DECK_WAS_NULL,
        DECK_NOT_LOCKED,
        DECK_NOT_PUBLIC,
        SUCCESS
    }

    /**
     * @param deck The deck to lock
     * @return enum values of type {@link LockUnlockDeckErrors} <br>
     * DECK_WAS_NULL if deck was null <br>
     * DECK_ALREADY_LOCKED if deck already locked <br>
     * DECK_NOT_PUBLIC if deck not public (only public decks can be locked) <br>
     * NO_ERROR
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public LockUnlockDeckErrors lockDeck(Deck deck) {
        // Todo: Add email notification
        if (deck == null) {
            return LockUnlockDeckErrors.DECK_WAS_NULL;
        }
        if (deck.getStatus() != DeckStatus.PUBLIC) {
            return LockUnlockDeckErrors.DECK_NOT_PUBLIC;
        }
        deckService.lockDeck(deck);
        return LockUnlockDeckErrors.SUCCESS;
    }

    /**
     * @param deck The deck to lock
     * @return enum values of type {@link LockUnlockDeckErrors} <br>
     * DECK_WAS_NULL if deck was null <br>
     * DECK_ALREADY_LOCKED if deck already locked <br>
     * DECK_NOT_PUBLIC if deck not public (only public decks can be locked) <br>
     * NO_ERROR
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public LockUnlockDeckErrors unlockDeck(Deck deck) {
        if (deck == null) {
            return LockUnlockDeckErrors.DECK_WAS_NULL;
        }
        if (deck.getStatus() != DeckStatus.LOCKED) {
            return LockUnlockDeckErrors.DECK_NOT_LOCKED;
        }
        deckService.unlockDeck(deck);
        return LockUnlockDeckErrors.SUCCESS;
    }

    public enum AssignAdminRightsErrors {
        USER_WAS_NULL,
        USER_ALREADY_ADMIN,
        SUCCESS
    }

    /**
     * @param user The user to assign admin rights to
     * @return False if the user already has admin rights, true if they were assigned
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public AssignAdminRightsErrors assignAdminRights(User user) {
        if (user == null) {
            return AssignAdminRightsErrors.USER_WAS_NULL; // Somehow make output on website
        }
        if (user.getRoles().contains(UserRole.ADMIN)) {
            return AssignAdminRightsErrors.USER_ALREADY_ADMIN; // Somehow make output on website
        }
        user.getRoles().add(UserRole.ADMIN);
        return AssignAdminRightsErrors.SUCCESS;
    }

    public enum UserCreationErrors {
        USERNAME_NULL,
        PASSWORD_NULL,
        FIRST_NAME_NULL,
        LAST_NAME_NULL,
        EMAIL_NULL,
        ROLES_NULL,
        USERNAME_TO_SHORT,
        USERNAME_TO_LONG,
        USERNAME_ALREADY_EXISTS,
        EMAIL_ALREADY_EXISTS,
        EMAIL_WRONG_FORMAT
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public Set<UserCreationErrors> createNewUser(User user) {
        return createNewUser(
                user.getUsername(),
                user.getPassword(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRoles()
        );
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public Set<UserCreationErrors> createNewUser(String username, String password, String firstName, String lastName, String
            email, Collection<UserRole> roles) {
        Set<UserCreationErrors> userCreationErrors = new TreeSet<>();
        if (username == null) {
            userCreationErrors.add(UserCreationErrors.USERNAME_NULL);
        } else {
            if (username.length() < 3) {
                userCreationErrors.add(UserCreationErrors.USERNAME_TO_SHORT);
            } else if (username.length() > 100) {
                userCreationErrors.add(UserCreationErrors.USERNAME_TO_LONG);
            }
            // Check if username exists already
            if (userService.loadUser(username) != null) {
                userCreationErrors.add(UserCreationErrors.USERNAME_ALREADY_EXISTS);
            }
        }
        if (password == null) {
            userCreationErrors.add(UserCreationErrors.PASSWORD_NULL);
        }
        if (firstName == null) {
            userCreationErrors.add(UserCreationErrors.FIRST_NAME_NULL);
        }
        if (lastName == null) {
            userCreationErrors.add(UserCreationErrors.LAST_NAME_NULL);
        }
        if (email == null) {
            userCreationErrors.add(UserCreationErrors.EMAIL_NULL);
        } else {
            if (!checkEmailIsValid(email)) {
                userCreationErrors.add(UserCreationErrors.EMAIL_WRONG_FORMAT);
            }
            if (userService.loadUserByMail(email).size() > 0) {
                userCreationErrors.add(UserCreationErrors.EMAIL_ALREADY_EXISTS);
            }
        }
        if (roles == null) {
            userCreationErrors.add(UserCreationErrors.ROLES_NULL);
        }
        // In case of errors userCreationErrors will hold them, and we can return them.
        if (userCreationErrors.size() > 0) {
            return userCreationErrors;
        }
        userService.createUser(username, password, firstName, lastName, email, true, roles);
        return null;
    }

    public static boolean checkEmailIsValid(String email) {
        try {
            InternetAddress internetAddress = new InternetAddress(email);
            internetAddress.validate();
            return true;
        } catch (AddressException e) {
            return false;
        }
    }
}
