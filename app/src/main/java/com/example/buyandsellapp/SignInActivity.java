package com.example.buyandsellapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import customfonts.MyEditText;
import customfonts.MyTextView;

public class SignInActivity extends BaseActivity implements View.OnClickListener{

    FirebaseFirestore db;
    //private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private MyEditText mEmailField;
    private MyEditText mPasswordField;
    private MyTextView mSignInButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        db=  FirebaseFirestore.getInstance();
       // mDatabase = database.getReference();
        mAuth = FirebaseAuth.getInstance();

        mEmailField = (MyEditText) findViewById(R.id.fieldEmail);
        mPasswordField = (MyEditText) findViewById(R.id.fieldPassword);
        mSignInButton = (MyTextView) findViewById(R.id.buttonSignIn);
        mSignInButton.setOnClickListener(this);
    }

    private void signIn() {
        if (!validateForm()) {
            return;
        }

        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(SignInActivity.this, "Sign In Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.buttonSignIn) {
            signIn();
        }
    }

    private void onAuthSuccess(FirebaseUser user) {
        Log.d("OnAuth",user.getEmail());

        String username = usernameFromEmail(user.getEmail());
        // Write new user
        // writeNewUser(user.getUid(), username, user.getEmail());
        // Go to MainActivity
        Intent intent = new Intent();
        intent.setClass(SignInActivity.this, WelcomeScreenActivity.class);
        startActivity(intent);
        finish();
    }

    public void onStart() {
        super.onStart();
        // Check auth on Activity start
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
            Log.d("Start","started");
        }
    }
    private String usernameFromEmail(String email) {

        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    private boolean validateForm() {

        boolean result = true;
        if (TextUtils.isEmpty(mEmailField.getText().toString())) {
            mEmailField.setError("Required");
            result = false;
        } else {
            mEmailField.setError(null);
        }

        if (TextUtils.isEmpty(mPasswordField.getText().toString())) {
            mPasswordField.setError("Required");
            result = false;
        } else {
            mPasswordField.setError(null);
        }
        return result;
    }

    private void writeNewUser(String userId, String name, String email) {
        // User user = new User(name, email,);
        //mDatabase.child("users").child(userId).setValue(user);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(SignInActivity.this, MainActivity.class);
        startActivity(intent);
    }

}
