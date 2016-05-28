package com.univ.lorraine.cmi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.univ.lorraine.cmi.database.model.Utilisateur;
import com.univ.lorraine.cmi.retrofit.CallMeIshmaelServiceProvider;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    private static final int REQUEST_LOGIN = 0;

    EditText pseudoText;
    EditText emailText;
    EditText passwordText;
    EditText passwordConfirmText;
    Button signupButton;
    TextView loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        pseudoText = (EditText) this.findViewById(R.id.input_pseudo);
        emailText = (EditText) this.findViewById(R.id.input_email);
        passwordText = (EditText) this.findViewById(R.id.input_password);
        passwordConfirmText = (EditText) this.findViewById(R.id.input_password_verif);
        signupButton = (Button) this.findViewById(R.id.btn_signup);
        loginLink = (TextView) this.findViewById(R.id.link_login);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivityForResult(intent, REQUEST_LOGIN);
                finish();
            }
        });
    }

    public void signup() {

        if (!validate()) {
            //onSignupFailed();
            return;
        }

        // Si on dispose d'une connexion internet
        if (Utilities.checkNetworkAvailable(this)) {

            signupButton.setEnabled(false);

            final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.signup_progress_dialog));
            progressDialog.show();

            String pseudo = pseudoText.getText().toString();
            String email = emailText.getText().toString();
            String password = passwordText.getText().toString();
            final Utilisateur send = new Utilisateur();

            send.setPseudo(pseudo);
            send.setEmail(email);
            send.setPassword(password);

            CallMeIshmaelServiceProvider.getService()
                    .signup(send)
                    .enqueue(new Callback<Utilisateur>() {
                        @Override
                        public void onResponse(Call<Utilisateur> call, Response<Utilisateur> response) {
                            if (response.body() == null) onSignupFailed();
                            else onSignupSuccess(response.body());
                        }

                        @Override
                        public void onFailure(Call<Utilisateur> call, Throwable t) {
                            Log.e("EXCSIGNUP", "", t);
                            Toast.makeText(getApplicationContext(), "Erreur connexion serveur.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    public void onSignupSuccess(Utilisateur newUser) {
        signupButton.setEnabled(true);

        // Sauvegarde du nouveau currentUser
        CredentialsUtilities.setCurrentUser(getApplicationContext(), newUser);

        setResult(RESULT_OK, null);
        Toast.makeText(SignupActivity.this, "Incription réussie !", Toast.LENGTH_SHORT).show();
    }

    public void onSignupFailed() {
        signupButton.setEnabled(true);
        Toast.makeText(getBaseContext(), "Impossible de finaliser l'inscription", Toast.LENGTH_LONG).show();
    }

    public boolean validate() {
        boolean valid = true;

        String pseudo = pseudoText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        String passwordVerif = passwordConfirmText.getText().toString();

        if (pseudo.isEmpty() || pseudo.length() < 3) {
            pseudoText.setError(getString(R.string.pseudo_court));
            valid = false;
        } else {
            pseudoText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError(getString(R.string.email_non_valide));
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError(getString(R.string.password_court));
            valid = false;
        } else {
            passwordText.setError(null);
        }

        if (!passwordVerif.equals(password)) {
            passwordConfirmText.setError(getString(R.string.verif_password_incorrecte));
            valid = false;
        } else {
            passwordConfirmText.setError(null);
        }

        return valid;
    }
}