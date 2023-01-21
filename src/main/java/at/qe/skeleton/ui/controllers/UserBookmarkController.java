package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.Deck;
import at.qe.skeleton.model.User;
import at.qe.skeleton.services.DeckService;
import at.qe.skeleton.services.UserService;
import at.qe.skeleton.ui.beans.SessionInfoBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.List;

/**
 * Controller to handle and manipulate bookmarks of the current User.
 *
 */
@Controller
@Scope("view")
public class UserBookmarkController implements Serializable {

    @Autowired
    private transient UserService userService;
    @Autowired
    private transient DeckService deckService;
    @Autowired
    private  SessionInfoBean sessionInfoBean;
    private User currentUser;

    @PostConstruct
    public void init(){
        this.currentUser=userService.loadUser(sessionInfoBean.getCurrentUserName());
    }

    /**
     * Action to get bookmarks of current user.
     * @return
     */
    public List<Deck> doGetBookmarks(){
        return currentUser.getBookmarks().stream().toList();
    }

    /**
     * Action to add new bookmark for the current.
     * @param bookmark
     */
    public void doAddBookmark(Deck bookmark){
        userService.addNewBookmark(currentUser, bookmark);
        this.currentUser=userService.saveUser(currentUser);
    }


    /**
     * Action to delete a bookmark for the current user.
     * @param bookmark
     */
    public void doDeleteBookmark(Deck bookmark){
        userService.deleteBookmark(currentUser, bookmark);
        this.currentUser=userService.saveUser(currentUser);
    }
    public boolean isBookmarked(Deck deck){
        return userService.isBookmarked(currentUser, deck);
    }

}
