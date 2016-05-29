package com.univ.lorraine.cmi;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.univ.lorraine.cmi.database.model.Utilisateur;
import com.univ.lorraine.cmi.retrofit.CallMeIshmaelServiceProvider;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final int REQUEST_SIGNUP = 0;

    EditText emailText;
    EditText passwordText;
    Button loginButton;
    TextView signupLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailText = (EditText) this.findViewById(R.id.input_email);
        passwordText = (EditText) this.findViewById(R.id.input_password);
        loginButton = (Button) this.findViewById(R.id.btn_login);
        signupLink = (TextView) this.findViewById(R.id.link_signup);
        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
                //on cache le clavier
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(loginButton.getWindowToken(), 0);
            }
        });

        signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
            }
        });
    }

    public void login() {

        if (!validate()) {
            Toast.makeText(getApplicationContext(), "Champ(s) invalide(s)", Toast.LENGTH_LONG).show();
            return;
        }

        // Si on dispose d'une connexion internet
        if (Utilities.checkNetworkAvailable(this)) {

            final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.login_progress_dialog));
            progressDialog.show();

            String email = emailText.getText().toString();
            String password = passwordText.getText().toString();

            Utilisateur user = new Utilisateur();
            user.setPassword(password);
            user.setEmail(email);

            CallMeIshmaelServiceProvider
                    .getService()
                    .login(user)
                    .enqueue(new Callback<Utilisateur>() {
                        @Override
                        public void onResponse(Call<Utilisateur> call, Response<Utilisateur> response) {
                            progressDialog.dismiss();
                            if (response.code() == 401) onLoginFailed();
                            else onLoginSuccess(response.body());
                        }

                        @Override
                        public void onFailure(Call<Utilisateur> call, Throwable t) {
                            Log.e("THROWABLE", t.toString());
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Erreur connexion serveur", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                this.finish();
            }
        }
    }

    public void onLoginSuccess(Utilisateur newUser) {
        Toast.makeText(LoginActivity.this, "Connexion réussie !", Toast.LENGTH_SHORT).show();
        CredentialsUtilities.setCurrentUser(getApplicationContext(), newUser);
        CallMeIshmaelServiceProvider.setHeaderAuth(CredentialsUtilities.getCurrentToken(getApplicationContext()));
        this.setResult(RESULT_OK);
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Informations invalides", Toast.LENGTH_LONG).show();
    }

    public boolean validate() {
        boolean valid = true;

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError(getString(R.string.email_non_valide));
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4) {
            passwordText.setError(getString(R.string.password_court));
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }
}
