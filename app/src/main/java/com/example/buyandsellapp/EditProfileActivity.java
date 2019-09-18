package com.example.buyandsellapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.autofill.AutofillValue;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.buyandsellapp.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import customfonts.MyEditText;
import customfonts.MyTextView;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

   // private DatabaseReference mDatabase;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private MyEditText mPasswordField;
    private MyEditText mFirstName;
    private MyEditText mLastName;
    private MyEditText mDistrict;
    private MyEditText mNewPwdField;

    private MyEditText mConfPasswordField;
    private DatePicker mDateofbirth;
    private MyTextView mSignUpButton;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);
        Toolbar toolbar = findViewById(R.id.tool);
        setSupportActionBar(toolbar);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mSignUpButton=findViewById(R.id.buttonSave);
        mPasswordField = (MyEditText) findViewById(R.id.fieldPassword);
        mNewPwdField = (MyEditText) findViewById(R.id.newPassword);
        mConfPasswordField = (MyEditText) findViewById(R.id.confPassword);
        mFirstName = (MyEditText) findViewById(R.id.firstName);
        mLastName = (MyEditText) findViewById(R.id.lastName);
        mDistrict = (MyEditText) findViewById(R.id.district);
        mDateofbirth = (DatePicker) findViewById(R.id.dateofbirth);
        mSignUpButton.setOnClickListener(this);

        DocumentReference docRef = db.collection("Users").document(mAuth.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    User user=document.toObject(User.class);
                    if (document.exists()) {
                        String[] name=user.fullName.split(" ");
                        mFirstName.setText(name[0]);
                        mLastName.setText(name[1]);
                        String[] date=user.dateOfBirth.split("-");
                        mDateofbirth.updateDate(Integer.parseInt(date[0]),Integer.parseInt(date[1]),Integer.parseInt(date[2]));
                        mDistrict.setText(user.district);
                    } else {
                        Log.d("", "No such document");
                    }
                } else {
                    Log.d("", "get failed with ", task.getException());
                }
            }
        });

    }

    @Override
    public void onClick(View view) {

        Log.d("clicked","clicked");
        signUp();
    }

    public void onStart() {
        super.onStart();
        // Check auth on Activity start

    }
    private void signUp() {
        if (!validateForm()) {
            Log.d("Invalid","hhhhhhhhhh");
            return;
        }

        final String password = mPasswordField.getText().toString();
        final String newPwd = mNewPwdField.getText().toString();

        Log.d("emailPwd",password);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(), password);
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPwd).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        try {
                                            onAuthSuccess(user);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        Log.d("Password", "Password updated");
                                    } else {
                                        Log.d("Password", "Error password not updated");
                                    }
                                }
                            });
                        } else {
                            Log.d("Password", "Error auth failed");
                        }
                    }
                });

// Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential);
    }

    private boolean validateForm() {

        boolean result = true;
        if (TextUtils.isEmpty(mNewPwdField.getText().toString())) {
            mNewPwdField.setError("Required");
            result = false;
        } else {
            mNewPwdField.setError(null);
        }

        if (TextUtils.isEmpty(mPasswordField.getText().toString())) {
            mPasswordField.setError("Required");
            result = false;
        } else {
            mPasswordField.setError(null);
        }

        if (TextUtils.isEmpty(mFirstName.getText().toString())) {
            mFirstName.setError("Required");
            result = false;
        } else {
            mFirstName.setError(null);
        }

        if (TextUtils.isEmpty(mLastName.getText().toString())) {
            mLastName.setError("Required");
            result = false;
        } else {
            mLastName.setError(null);
        }

       if (TextUtils.isEmpty(mConfPasswordField.getText().toString())) {
            mConfPasswordField.setError("Required");
            result = false;
        } else {
            mConfPasswordField.setError(null);
        }

        if (!mConfPasswordField.getText().toString().equals(mNewPwdField.getText().toString())) {
            mConfPasswordField.setError("Password not matched");
            result = false;
        } else {
            mConfPasswordField.setError(null);
        }

        if (TextUtils.isEmpty(mDistrict.getText().toString())) {
            mDistrict.setError("Required");
            result = false;
        } else {
            mDistrict.setError(null);
        }

        if (TextUtils.isEmpty(mDateofbirth.toString())) {
            result = false;
        } else {
            long currentTime = System.currentTimeMillis();
            Calendar now = Calendar.getInstance();
            now.setTimeInMillis(currentTime);
            if((now.get(Calendar.YEAR)- mDateofbirth.getYear())<16){
                result=false;
                Toast.makeText(EditProfileActivity.this, "Invalid Date",
                        Toast.LENGTH_SHORT).show();
            }

        }
        return result;
    }

    private void onAuthSuccess(FirebaseUser user) throws ParseException {
        Log.d("OnAuth",user.getEmail());

        String fullName= mFirstName.getText()+" "+mLastName.getText();
        String district=mDistrict.getText().toString();

       try{
           SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
           // Write new user
            String dateOfBirth=mDateofbirth.getYear()+"-"+mDateofbirth.getMonth()+"-"+mDateofbirth.getDayOfMonth();
           Log.d("creating","User Created");
           UpdateUser(mAuth.getUid(),fullName,district,dateOfBirth);
           // Go to MainActivity
           Intent intent = new Intent();
           intent.setClass(EditProfileActivity.this, WelcomeScreenActivity.class);
           startActivity(intent);
       }
       catch (Exception e){
           e.printStackTrace();
       }

        //finish();
    }

    private String usernameFromEmail(String email) {

        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    private void UpdateUser(String userId,  String fullName, String district, String dateOfBirth) {
        //User user = new User(name, email,fullName,district,dateOfBirth);
        //mDatabase.child("User").child(userId).setValue(user);
        db.collection("Users").document(userId).update("district",district,
                "fullName",fullName,"district",district,"dateOfBirth",dateOfBirth);
    }

    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(EditProfileActivity.this, WelcomeScreenActivity.class);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        return true;
    }

}
