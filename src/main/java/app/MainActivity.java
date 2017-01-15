package app;

import javax.inject.Inject;

import app.fragments.BaseFragment;
import app.fragments.ChatListFragment;
import app.fragments.ChatRoomFragment;
import app.presenters.MainNavigator;
import di.components.DaggerAppComponent;
import di.components.MainComponent;

/**
 * @author kitttn
 */
public class MainActivity extends Activity {
    private MainComponent component;
    @Inject MainNavigator navigator;
    private BaseFragment selected;

    public MainActivity() {
        component = DaggerAppComponent.create().add();
        component.inject(this);
        System.out.println("Activity created!");
        navigator.printInfo();
        new ChatListFragment(this);
        new ChatRoomFragment(this);
    }

    public MainComponent getMainComponent() {
        return component;
    }
}