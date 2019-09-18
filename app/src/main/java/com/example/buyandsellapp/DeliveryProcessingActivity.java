package com.example.buyandsellapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.buyandsellapp.Models.ListProduct;
import com.example.buyandsellapp.Models.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class DeliveryProcessingActivity extends AppCompatActivity implements View.OnClickListener {


    private Button buttonReceived;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliveryprocessing);
        Toolbar toolbar = findViewById(R.id.tool);
        setSupportActionBar(toolbar);

        db = FirebaseFirestore.getInstance();
        buttonReceived = findViewById(R.id.buttonReceived);
        buttonReceived.setOnClickListener(this);
        final String uid = FirebaseAuth.getInstance().getUid();
        db.collection("Users").document(uid).collection("Cart").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("document", document.getId() + " => " + document.getData());
                                ListProduct product=document.toObject(ListProduct.class);
                                final String productId=product.getProductID();
                                final int soldQty=product.getQty();
                                //Update quantity
                                db.collection("Products").document(productId).get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        Product product=document.toObject(Product.class);
                                                        int prevQty=product.getQty();
                                                        int newQty=prevQty-soldQty;
                                                        db.collection("Products").document(productId)
                                                                .update("qty",newQty);
                                                        db.collection("Users").document(uid).collection("Cart")
                                                                .document(productId).delete();
                                                        Log.d("", "DocumentSnapshot data: " + document.getData());
                                                    } else {
                                                        Log.d("", "No such document");
                                                    }
                                                } else {
                                                    Log.d("", "get failed with ", task.getException());
                                                }
                                            }
                                        });
                            }
                        } else {
                            Log.d("", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }


    public void onStart() {
        super.onStart();
        // Check auth on Activity start
    }


    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.buttonReceived) {

            Intent intent = new Intent();
            intent.setClass(DeliveryProcessingActivity.this, ConfirmationActivity.class);
            intent.putExtra("message", "Product Has Been Delivered");
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(DeliveryProcessingActivity.this, WelcomeScreenActivity.class);
        startActivity(intent);
    }
}
