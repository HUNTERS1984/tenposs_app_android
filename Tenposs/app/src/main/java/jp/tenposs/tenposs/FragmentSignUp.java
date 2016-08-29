package jp.tenposs.tenposs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import jp.tenposs.communicator.SignUpCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;

/**
 * Created by ambient on 8/15/16.
 */
public class FragmentSignUp extends AbstractFragment implements View.OnClickListener {

    EditText emailText;
    EditText nameText;
    EditText passwordText;
    EditText passwordConfirmText;

    Button signUpButton;

    @Override
    protected void customClose() {

    }

    @Override
    protected void customToolbarInit() {
        toolbarSettings.toolbarTitle = getString(R.string.signup);
        toolbarSettings.toolbarIcon = "ti-arrow-left";
        toolbarSettings.toolbarType = ToolbarSettings.LEFT_BACK_BUTTON;
    }

    @Override
    protected void reloadScreenData() {

    }

    @Override
    protected void previewScreenData() {

    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mRoot = inflater.inflate(R.layout.fragment_signup, null);
        emailText = (EditText) mRoot.findViewById(R.id.email_text);
        nameText = (EditText) mRoot.findViewById(R.id.name_text);
        passwordText = (EditText) mRoot.findViewById(R.id.password_text);
        passwordConfirmText = (EditText) mRoot.findViewById(R.id.password_confirm_text);

        signUpButton = (Button) mRoot.findViewById(R.id.signup_button);
        signUpButton.setOnClickListener(this);
        return mRoot;
    }

    @Override
    protected void customResume() {
        previewScreenData();
    }

    @Override
    void loadSavedInstanceState(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(SCREEN_DATA)) {
            //this.screenData = (TopInfo.Response.ResponseData) savedInstanceState.getSerializable(SCREEN_DATA);
        }
    }

    @Override
    void setRefreshing(boolean refreshing) {

    }

    @Override
    public void onClick(View v) {
        if (v == signUpButton) {
            Bundle params = new Bundle();

            SignUpCommunicator communicator = new SignUpCommunicator(new TenpossCommunicator.TenpossCommunicatorListener() {
                @Override
                public void completed(TenpossCommunicator request, Bundle responseParams) {

                }
            });
            communicator.execute(params);
        }
    }
}
