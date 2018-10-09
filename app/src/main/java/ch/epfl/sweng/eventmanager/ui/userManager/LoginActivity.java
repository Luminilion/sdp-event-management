package ch.epfl.sweng.eventmanager.ui.userManager;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import ch.epfl.sweng.eventmanager.R;
import ch.epfl.sweng.eventmanager.repository.UserRepository;
import ch.epfl.sweng.eventmanager.repository.data.User;
import ch.epfl.sweng.eventmanager.userManagement.Session;
import dagger.android.AndroidInjection;

import javax.inject.Inject;
import java.util.Optional;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // Login task.
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private Button mLoginButton;
    private ProgressBar mProgressBar;

    @Inject
    UserRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailView = (EditText) findViewById(R.id.email_field);
        mEmailView.setHint(R.string.email_field);
        mPasswordView = (EditText) findViewById(R.id.password_field);
        mPasswordView.setHint(R.string.password_field);

        mLoginButton = (Button) findViewById(R.id.login_button);
        mLoginButton.setText(R.string.login_button);
        mLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mProgressBar = findViewById(R.id.sign_in_progress_bar);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Validate input from login form.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.empty_password_activity_login));
            focusView = mPasswordView;
            cancel = true;
        }
        if (TextUtils.isEmpty(email) || !isEmailValid(email)) {
            mEmailView.setError(getString(R.string.invalid_email_activity_login));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            mAuthTask = new UserLoginTask(email, password, this);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        // FIXME: run a proper regex on the given email.
        return email.contains("@");
    }

    private void showProgress(boolean displayed) {
        mLoginButton.setEnabled(!displayed);
        mProgressBar.setVisibility(displayed ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * Represents an asynchronous login task used to authenticate the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final Context mContext;

        UserLoginTask(String email, String password, Context context) {
            mEmail = email;
            mPassword = password;
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            showProgress(true);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Boolean doInBackground(Void... params) {
            Optional<User> user = repository.getUserByEmail(mEmail);
            if (user.isPresent()) {
                return Session.login(user.get(), mPassword);
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Intent intent = new Intent(mContext, DisplayAccountActivity.class);
                startActivity(intent);
            } else {
                mPasswordView.setError(getString(R.string.invalid_credentials_activity_login));
                mPasswordView.requestFocus();
            }
        }
    }
}
