package com.example.buyandsellapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class CartListActivity extends BaseActivity implements View.OnClickListener {
    private Button buttonCheckout;

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

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        db = FirebaseFirestore.getInstance();
        buttonCheckout=findViewById(R.id.buttonCheckout);
        buttonCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        recyclerView = findViewById(R.id.cartListView);
        linearLayoutManager = new LinearLayoutManager(CartListActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        fetch();
    }
    private void fetch() {
        Query query =db.collection("Users")
                .document(FirebaseAuth.getInstance().getUid()).collection("Cart");

        Log.d("fetch", "fetch");

        FirestoreRecyclerOptions<String> options =
                new FirestoreRecyclerOptions.Builder<String>()
                        .setQuery(query, String.class)
                        .build();
        Log.d("beforeAdapter", "beforeAdapter");
        adapter = new FirestoreRecyclerAdapter<String, CartListActivity.ViewHolder>(options) {

            @Override
            public CartListActivity.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item, parent, false);
                return new CartListActivity.ViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(final CartListActivity.ViewHolder holder, final int position, final String model) {
                // holder.setTxtTitle(model.getProductID());
                // holder.setTxtDesc(model.getTimestamp().toString());
                db.collection("Products").document(model).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                  @Override
                                                  public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                      Product prod = documentSnapshot.toObject(Product.class);
                                                      holder.setTxtTitle(prod.getProductName());
                                                      holder.setTxtDesc(Double.toString(prod.getPrice()));
                                                      Log.d("title set","title set");
                                                  }
                                              }

                        );

          /*     FirebaseDatabase.getInstance().getReference().child("Product").
                       child(adapter.getRef(holder.getAdapterPosition()).getKey())
                        .addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                try {
                                    if (snapshot.getValue() != null) {
                                        try {
                                            Log.d("productName", "" + snapshot.getValue()); // your name values you will get here
                                            Product prod = snapshot.getValue(Product.class);
                                            holder.setTxtTitle(prod.getProductName());
                                            holder.setTxtDesc(Double.toString(prod.getPrice()));
                                            Log.d("title set","title set");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        Log.e("TAG", " it's null.");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError firebaseError) {
                                Log.e("onCancelled", " cancelled");
                            }
                        });

*/


                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //  Toast.makeText(ProductListActivity.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
                        //+adapter.getRef(1)
                        Intent intent = new Intent();
                        intent.setClass(CartListActivity.this, ProductViewCartWishlistActivity.class);
                        //String cartRef=adapter.getRef(holder.getAdapterPosition()).toString();
                        //String[] refs=cartRef.split("/");
                        //Log.d("prodRef",refs[refs.length-1]);
                        // DatabaseReference prodRef=FirebaseDatabase.getInstance().getReference()
                        //       .child("Product").child(refs[refs.length-1]);
                        intent.putExtra("productID",model);
                        intent.putExtra("category","null");
                        intent.putExtra("prevIntent","WishListActivity");
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
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        return true;
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(CartListActivity.this, WelcomeScreenActivity.class);
        startActivity(intent);
    }
}
