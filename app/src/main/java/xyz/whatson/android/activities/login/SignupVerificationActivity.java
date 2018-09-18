package xyz.whatson.android.activities.login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    private void loadUserInformation() {
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user.isEmailVerified()) {
            // Go to the Events Feed
            Intent intent = new Intent(this, EventsFeedActivity.class);
            startActivity(intent);
        } else {
            textView.setText("Email Not Verified (Click to Verify)");
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(SignupVerificationActivity.this, "Verification Email Sent", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }


}