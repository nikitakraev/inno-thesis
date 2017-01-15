package app.presenters;

import javax.inject.Inject;

import app.interactors.MainInteractor;

/**
 * @author kitttn
 */
public class ChatRoomPresenter {
    MainInteractor interactor;

    public ChatRoomPresenter(MainInteractor interactor) {
        this.interactor = interactor;
    }

    public void printInfo() {
        System.out.println(this + ": interactor: " + interactor);
    }
}
