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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductViewCartWishlistActivity extends ProductViewActivity implements View.OnClickListener {

    private FirebaseFirestore db;
    private TextView productName;
    private TextView productPrice;
    private TextView productSeller;
    private Button buttonAddtoWishlist;
    private SeekBar seekbar;
    private TextView qtyText;
    private TextView availableQty;
    private Button buttonAddtoCart;
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
    int qty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productdesc);
        Toolbar toolbar = findViewById(R.id.tool);
        setSupportActionBar(toolbar);
        db = FirebaseFirestore.getInstance();
        mstorageRef = FirebaseStorage.getInstance().getReference();
        Intent intent = getIntent();
        category = intent.getStringExtra("category");
        productID = intent.getStringExtra("productID");

        productName = (TextView) findViewById(R.id.productdescName);
        productPrice = (TextView) findViewById(R.id.productdescPrice);
        productSeller = (TextView) findViewById(R.id.productdescSeller);
        productImage = (ImageView) findViewById(R.id.imageView);
        qtyText=(TextView) findViewById(R.id.qty);
        availableQty=(TextView) findViewById(R.id.availableqty);
        seekbar = (SeekBar) findViewById(R.id.seekBar);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                qty=progress;
                qtyText.setText("   X "+qty);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

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
                            productPrice.setText("  BDT "+product.getPrice());
                            productSellerName = "";
                            availableQty.setText(product.getQty() + "pieces Available");
                            if(product.getQty()<=0) {
                                availableQty.setText("Sold Out");
                                buttonAddtoCart.setEnabled(false);
                                buttonAddtoCart.setVisibility(View.INVISIBLE);
                                seekbar.setVisibility(View.INVISIBLE);
                            }
                            seekbar.setMax(product.getQty());
                            seekbar.setProgress(product.getQty()/10+1);
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


        buttonAddtoWishlist = findViewById(R.id.buttonAddtoWishlist);
        buttonAddtoWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("Users").document(FirebaseAuth.getInstance().getUid()).collection("Wishlist")
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshot) {
                        if (documentSnapshot.isEmpty() == false) {
                            List<String> map = (List) documentSnapshot.getDocuments();
                            boolean r = map.contains(productID);
                            if (r == true) {        //Already in Wishlist
                                Toast.makeText(ProductViewCartWishlistActivity.this, "Already added to Wishlist",
                                        Toast.LENGTH_SHORT).show();
                            } else {          //Not in wishlist,add to wishlist
                                mAuth = FirebaseAuth.getInstance();
                                Map<String, String> data1 = new HashMap<>();
                                data1.put("productID", productID);
                                db.collection("Users").document(FirebaseAuth.getInstance().getUid()).collection("Wishlist")
                                        .document(productID).set(data1)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(ProductViewCartWishlistActivity.this, "Added To Wishlist",
                                                        Toast.LENGTH_SHORT).show();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ProductViewCartWishlistActivity.this, "Cannot add to Wishlist",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }

                            Log.d("Wishlist", map.contains(productID) + " ");
                        } else {
                            Map<String, String> data1 = new HashMap<>();
                            data1.put("productID", productID);
                            db.collection("Users").document(FirebaseAuth.getInstance().getUid()).collection("Wishlist")
                                    .document(productID).set(data1)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(ProductViewCartWishlistActivity.this, "Added To Wishlist",
                                                    Toast.LENGTH_SHORT).show();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ProductViewCartWishlistActivity.this, "Cannot add to Wishlist",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }

                });
            }
        });

        buttonAddtoCart = findViewById(R.id.buttonAddtoCart);
        buttonAddtoCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("Users").document(FirebaseAuth.getInstance().getUid()).collection("Cart")
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshot) {
                        if (documentSnapshot.isEmpty() == false) {
                            List<String> map = (List) documentSnapshot.getDocuments();
                            boolean r = map.toString().contains(productID);
                            if (r == true) {        //Already in Wishlist
                                Toast.makeText(ProductViewCartWishlistActivity.this, "Already added to Cart",
                                        Toast.LENGTH_SHORT).show();
                            } else {          //Not in wishlist,add to wishlist
                                Map<String, Object> data1 = new HashMap<>();
                                data1.put("productID", productID);
                                Log.d("map"," "+map.toString());
                                data1.put("qty",qty);
                                db.collection("Users").document(FirebaseAuth.getInstance().getUid()).collection("Cart")
                                        .document(productID).set(data1)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void AVoid) {
                                                Toast.makeText(ProductViewCartWishlistActivity.this, "Added To Cart",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        })

                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(ProductViewCartWishlistActivity.this, "Cannot add to Cart",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }

                            Log.d("Cart", map.contains(productID) + " ");
                        } else {
                            Map<String, String> data1 = new HashMap<>();
                            data1.put("productID", productID);
                            db.collection("Users").document(FirebaseAuth.getInstance().getUid()).collection("Cart")
                                    .document(productID).set(data1)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void AVoid) {
                                            Toast.makeText(ProductViewCartWishlistActivity.this, "Added To Cart",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    })

                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ProductViewCartWishlistActivity.this, "Cannot add to Cart",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }
                    }

                });
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
            intent.setClass(ProductViewCartWishlistActivity.this, ProductListActivity.class);
        else if (prevIntent.equals("WishListActivity"))
            intent.setClass(ProductViewCartWishlistActivity.this, WishListActivity.class);
        else if (prevIntent.equals("UploadedProductListActivity"))
            intent.setClass(ProductViewCartWishlistActivity.this, UploadedProductListActivity.class);

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
