package com.example.buyandsellapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.ObservableSnapshotArray;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;
import java.util.Map;

public class WishListActivity extends BaseActivity implements View.OnClickListener {

    FirebaseFirestore db;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirestoreRecyclerAdapter adapter;
    String productName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.cartListView);
        linearLayoutManager = new LinearLayoutManager(WishListActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        fetch();
    }

    private void fetch() {
        Query query = db.collection("Users")
                .document(FirebaseAuth.getInstance().getUid()).collection("Wishlist");

        Log.d("fetch", "fetch");

        FirestoreRecyclerOptions<WishlistProduct> options =
                new FirestoreRecyclerOptions.Builder<WishlistProduct>()
                        .setQuery(query, WishlistProduct.class)
                        .build();
        Log.d("beforeAdapter", "beforeAdapter");
        adapter = new FirestoreRecyclerAdapter<WishlistProduct, ViewHolder>(options) {

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item, parent, false);
                return new ViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(final ViewHolder holder, final int position, final WishlistProduct model) {
                // holder.setTxtTitle(model.getProductID());
                // holder.setTxtDesc(model.getTimestamp().toString());
                final String productID=model.getProductID();

                db.collection("Products").document(productID).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                  @Override
                                                  public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                      Product prod = documentSnapshot.toObject(Product.class);
                                                      holder.setTxtTitle(prod.getProductName());
                                                      holder.setTxtDesc(Double.toString(prod.getPrice()));
                                                      Log.d("title set", "title set");
                                                  }
                                              }

                        );


                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(WishListActivity.this, ProductViewCartWishlistActivity.class);
                        intent.putExtra("productID", productID);
                        intent.putExtra("category", "null");
                        intent.putExtra("prevIntent", "WishListActivity");
                        startActivity(intent);
                    }
                });

            }

        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    public void onStart() {
        super.onStart();
        // Check auth on Activity start
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout root;
        public TextView txtTitle;
        public TextView txtDesc;

        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.list_root);
            txtTitle = itemView.findViewById(R.id.list_title);
            txtDesc = itemView.findViewById(R.id.list_desc);
        }

        public void setTxtTitle(String string) {
            txtTitle.setText(string);
        }


        public void setTxtDesc(String string) {
            txtDesc.setText(string);
        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(WishListActivity.this, WelcomeScreenActivity.class);
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
