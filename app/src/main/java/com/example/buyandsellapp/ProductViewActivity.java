package com.example.buyandsellapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.example.buyandsellapp.Models.Product;
import com.example.buyandsellapp.Models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class ProductViewActivity extends BaseActivity implements View.OnClickListener {

    private FirebaseFirestore db;
    private TextView productName;
    private TextView productPrice;
    private TextView productSeller;

    private ImageView carticon;
    private String productSellerID;
    String productSellerName;
    String productID;
    FirebaseAuth mAuth;
    private ImageView productImage;
    StorageReference mstorageRef;
    File localFile;
    Bitmap myBitmap;
    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productviewonly);
        Toolbar toolbar = findViewById(R.id.tool);
        setSupportActionBar(toolbar);
        db = FirebaseFirestore.getInstance();
        mstorageRef = FirebaseStorage.getInstance().getReference();
        Intent intent = getIntent();
        category = intent.getStringExtra("category");
        productID = intent.getStringExtra("productID");

        Log.d("productID", productID);

        productName = (TextView) findViewById(R.id.productdescName);
        productPrice = (TextView) findViewById(R.id.productdescPrice);
        productSeller = (TextView) findViewById(R.id.productdescSeller);
        productImage = (ImageView) findViewById(R.id.imageView);

        final ImagePopup imagePopup = new ImagePopup(this);
        imagePopup.setWindowHeight(800); // Optional
        imagePopup.setWindowWidth(800); // Optional
        imagePopup.setHideCloseIcon(true);  // Optional
        imagePopup.setImageOnClickClose(true);  // Optional

        db.collection("Products").document(productID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Log.d("DocumentSnapshot", documentSnapshot.getData().toString());
                            Product product = documentSnapshot.toObject(Product.class);
                            String prodName = "";
                            prodName = product.getProductName();
                            productName.setText(prodName);
                            productPrice.setText(Double.toString(product.getPrice()));
                            productSellerName = "";
                            //retrieve seller name
                            db.collection("Users").document(product.getUid()).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.exists()) {
                                                User user = documentSnapshot.toObject(User.class);
                                                productSellerName = user.getFullName();
                                                //productSellerName = documentSnapshot.getData().get("fullName").toString();
                                                Log.d("productSellerName", productSellerName); // your name values you will get here
                                                productSeller.setText(productSellerName);
                                            } else {
                                                Log.d("DocumentSnapshotUser", "No such document");
                                            }
                                        }
                                    });

                            try {
                                localFile = File.createTempFile("images", "jpg");
                                mstorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(product.getImageuri());
                                mstorageRef.getFile(localFile)
                                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                // Successfully downloaded data to local file
                                                // ...
                                                myBitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                                productImage.setImageBitmap(myBitmap);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle failed download
                                        // ...
                                    }
                                });


                                imagePopup.initiatePopupWithGlide(product.getImageuri());


                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Log.d("DocumentSnapshot", "No such document");
                        }
                    }
                });


        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /** Initiate Popup view **/
                imagePopup.viewPopup();

            }
        });

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
        String prevIntent = getIntent().getStringExtra("prevIntent");
        Intent intent = new Intent();
        if (prevIntent.equals("ProductListActivity"))
            intent.setClass(ProductViewActivity.this, ProductListActivity.class);
        else if (prevIntent.equals("WishListActivity"))
            intent.setClass(ProductViewActivity.this, WishListActivity.class);
        else if (prevIntent.equals("UploadedProductListActivity"))
            intent.setClass(ProductViewActivity.this, UploadedProductListActivity.class);

        intent.putExtra("category", category);
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
