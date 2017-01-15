package app.fragments;

import javax.inject.Inject;

import app.Activity;
import app.MainActivity;
import app.presenters.ChatListPresenter;
import di.components.DaggerAppComponent;
import di.modules.InteractorsModule;
import di.modules.PresentersModule;

/**
 * @author kitttn
 */
public class ChatListFragment extends BaseFragment {
    @Inject ChatListPresenter presenter;

    public ChatListFragment(Activity activity) {
        super(activity);
        System.out.println("Fragment created!");
        ((MainActivity) getActivity()).getMainComponent().inject(this);
        System.out.println("Presenter: " + presenter);
        presenter.printInfo();
    }
}
