package app.fragments;

import app.Activity;

/**
 * @author kitttn
 */
public class BaseFragment {
    private Activity activity;

    public BaseFragment(Activity activity) {
        this.activity = activity;
    }

    public Activity getActivity() {
        return activity;
    }
}
