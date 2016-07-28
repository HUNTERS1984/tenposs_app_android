package jp.tenposs.tenposs;

import android.support.v4.app.Fragment;

import jp.tenposs.datamodel.AppSettings;

/**
 * Created by ambient on 7/26/16.
 */
public abstract class AbstractFragment extends Fragment {
    public class ToolbarSettings{
        AppSettings.Settings toolbarSettings;
        boolean showToggleMenu;
    }
    public ToolbarSettings  toolbarSettings;
    protected abstract void customClose();
    protected abstract void customToolbarInit();
    protected void close() {
        customClose();
        getActivity().getSupportFragmentManager().popBackStack();
        /*getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .remove(this)
                .commit();*/
    }
}
