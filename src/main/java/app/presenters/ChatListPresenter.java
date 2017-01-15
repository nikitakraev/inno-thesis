package app.presenters;

import app.interactors.ChatListInteractor;

/**
 * @author kitttn
 */
public class ChatListPresenter {
    private ChatListInteractor interactor;
    private MainNavigator navigator;

    public ChatListPresenter(ChatListInteractor interactor, MainNavigator navigator) {
        this.interactor = interactor;
        this.navigator = navigator;
    }

    public void printInfo() {
        System.out.println(this + ": interactor: " + interactor);
    }
}
