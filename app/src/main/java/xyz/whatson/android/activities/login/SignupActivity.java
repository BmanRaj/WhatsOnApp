package xyz.whatson.android.activities.login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import xyz.whatson.android.R;
import xyz.whatson.android.model.User;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    ProgressBar progressBar;
    private EditText editTextName, editTextEmail, editTextPassword;
    private CheckBox checkBoxArt, checkBoxCulture, checkBoxSports, checkBoxMusic, checkBoxTech, checkBoxScience, checkBoxRecreation, checkBoxEducation;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editTextName = findViewById(R.id.etName);
        editTextEmail = findViewById(R.id.etEmail);
        editTextPassword = findViewById(R.id.etPassword);
        checkBoxArt = findViewById(R.id.checkBoxArt);
        checkBoxCulture = findViewById(R.id.checkBoxCulture);
        checkBoxSports = findViewById(R.id.checkBoxSports);
        checkBoxMusic = findViewById(R.id.checkBoxMusic);
        checkBoxTech = findViewById(R.id.checkBoxTech);
        checkBoxScience = findViewById(R.id.checkBoxScience);
        checkBoxEducation = findViewById(R.id.checkBoxEducation);
        checkBoxRecreation = findViewById(R.id.checkBoxRecreation);
        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.btnSignUp).setOnClickListener(this);
        findViewById(R.id.textViewLogin).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(this, SignupVerificationActivity.class));
        }
    }

    private void registerUser() {
        final String name = editTextName.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        final String Art;
        final String Culture;
        final String Sports;
        final String Music;
        final String Tech;
        final String Science;
        final String Recreation;
        final String Education;

        String password = editTextPassword.getText().toString().trim();

        if (name.isEmpty()) {
            editTextName.setError(getString(R.string.input_error_name));
            editTextName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            editTextEmail.setError(getString(R.string.input_error_email));
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError(getString(R.string.input_error_email_invalid));
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError(getString(R.string.input_error_password));
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError(getString(R.string.input_error_password_length));
            editTextPassword.requestFocus();
            return;
        }

        if (checkBoxArt.isChecked()) {
            Art = "true";
        }
            else {
                Art = "false";
            }

        if (checkBoxCulture.isChecked()) {
            Culture = "true";
        }
        else {
            Culture = "false";
        }

        if (checkBoxSports.isChecked()) {
            Sports = "true";
        }
        else {
            Sports = "false";
        }

        if (checkBoxMusic.isChecked()) {
            Music = "true";
        }
        else {
            Music = "false";
        }

        if (checkBoxTech.isChecked()) {
            Tech = "true";
        }
        else {
            Tech = "false";
        }

        if (checkBoxScience.isChecked()) {
            Science = "true";
        }
        else {
            Science = "false";
        }

        if (checkBoxEducation.isChecked()) {
            Education = "true";
        }
        else {
            Education = "false";
        }

        if (checkBoxRecreation.isChecked()) {
            Recreation = "true";
        }
        else {
            Recreation = "false";
        }


        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            User user = new User(
                                    name,
                                    email,
                                    Art,
                                    Culture,
                                    Sports,
                                    Music,
                                    Tech,
                                    Science,
                                    Recreation,
                                    Education
                            );

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SignupActivity.this, getString(R.string.registration_success), Toast.LENGTH_LONG).show();

                                    } else {
                                        Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignUp:
                registerUser();
                break;

            case R.id.textViewLogin:
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                break;

        }
    }
}