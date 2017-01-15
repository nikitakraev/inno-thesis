package app.presenters;

import app.interactors.MainInteractor;

/**
 * @author kitttn
 */

public class MainNavigator {
    private MainInteractor interactor;

    public MainNavigator(MainInteractor interactor) {
        this.interactor = interactor;
    }

    public void printInfo() {
        System.out.println(this + ": interactor: " + interactor);
    }
}
