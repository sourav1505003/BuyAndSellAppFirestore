package com.example.buyandsellapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class ConfirmationActivity extends AppCompatActivity implements View.OnClickListener {


    private TextView confirmation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        confirmation = (TextView) findViewById(R.id.confirmation);
        Intent intent=getIntent();
        String  message=intent.getStringExtra("message");
        confirmation.setText(message);

    }


    public void onStart() {
        super.onStart();
        // Check auth on Activity start
    }


    @Override
    public void onClick(View view) {
        //Intent intent=new Intent();
        //intent.setClass(this,ThirdScreenActivity.class);
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        Log.d("gotAuthData", mAuth.getUid());
        // startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(ConfirmationActivity.this, WelcomeScreenActivity.class);
        startActivity(intent);
    }
}
