package xyz.whatson.android.activities.login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import xyz.whatson.android.R;
import xyz.whatson.android.activities.EventsFeedActivity;

public class SignupVerificationActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_verification);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textView = (TextView) findViewById(R.id.textViewVerified);

        mAuth = FirebaseAuth.getInstance();

        loadUserInformation();


    }

    @Override
    protected void onResume() {
        super.onResume();
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        } else if (user.isEmailVerified()) {
            finish();
            Intent intent = new Intent(this, EventsFeedActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    private void loadUserInformation() {
        textView.setText("Email Not Verified (Click to Verify)");
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(SignupVerificationActivity.this, "Verification Email Sent", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void OnClickLogOut (View view) {
            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

}
