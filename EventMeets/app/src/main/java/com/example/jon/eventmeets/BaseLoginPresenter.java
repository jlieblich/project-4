package com.example.jon.eventmeets;

import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Jon on 12/16/2016.
 */

public class BaseLoginPresenter implements BaseLoginContract.Presenter {
    private BaseLoginContract.View mView;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public BaseLoginPresenter(BaseLoginContract.View view) {
        mView = view;
    }

    @Override
    public void onLoginPressed() {
        mView.displayLoginDialog();
    }

    @Override
    public void notifyLoginSuccess(boolean wasSuccessful) {
        if (wasSuccessful) {
            Toast.makeText((BaseLoginActivity)mView, "Logged In Successfully", Toast.LENGTH_SHORT).show();
            mView.startMainMenuActivity();
        } else {
            Toast.makeText((BaseLoginActivity)mView, "Invalid Credentials", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoginSkipped() {
        mView.skipLogin();
    }

    @Override
    public void checkLoginDetails(String username, String password) {
        if(username.equals("test")&&password.equals("test")) {
            Toast.makeText((BaseLoginActivity) mView, "Login", Toast.LENGTH_SHORT).show();
            notifyLoginSuccess(true);
        }
    }
}
