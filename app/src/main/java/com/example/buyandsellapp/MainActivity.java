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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import customfonts.MyTextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private EditText mEmailField;
    private EditText mPasswordField;
    private MyTextView mSignInButton;
    private TextView mSignUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference();
        mAuth = FirebaseAuth.getInstance();
        mSignInButton = (MyTextView) findViewById(R.id.mSignInButton);
        mSignUpButton = (TextView) findViewById(R.id.mSignUpButton);
        mSignInButton.setOnClickListener(this);
        mSignUpButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.mSignInButton) {
            if (mAuth.getCurrentUser() != null) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, WelcomeScreenActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, SignInActivity.class);
                startActivity(intent);
            }

        } else if (i == R.id.mSignUpButton) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
        }
    }


    public void onStart() {
        super.onStart();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
