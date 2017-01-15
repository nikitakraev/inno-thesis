package app.fragments;

import javax.inject.Inject;

import app.Activity;
import app.MainActivity;
import app.fragments.BaseFragment;
import app.presenters.ChatRoomPresenter;

/**
 * @author kitttn
 */
public class ChatRoomFragment extends BaseFragment {
    @Inject ChatRoomPresenter presenter;

    public ChatRoomFragment(Activity activity) {
        super(activity);
        System.out.println("ChatRoomFragment created!");
        ((MainActivity) getActivity()).getMainComponent().inject(this);
        System.out.println("presenter: " + presenter);
        presenter.printInfo();
    }
}
