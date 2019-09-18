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
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buyandsellapp.Models.ListProduct;
import com.example.buyandsellapp.Models.Product;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ReceiptActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseFirestore db;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    public FirestoreRecyclerAdapter adapter;
    private Button buttonCheckout;
    FirebaseAuth mAuth;
    Query query;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);
        Toolbar toolbar = findViewById(R.id.tool);
        setSupportActionBar(toolbar);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid();
        recyclerView = findViewById(R.id.orderList);
        linearLayoutManager = new LinearLayoutManager(ReceiptActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        Intent intent = getIntent();
        buttonCheckout = findViewById(R.id.buttonCheckout);
        buttonCheckout.setOnClickListener(this);
        fetch();

    }

    private void fetch() {

        Log.d("fetch", "fetch");
        query = db.collection("Users").document(userId).collection("Cart");
        FirestoreRecyclerOptions<ListProduct> options =
                new FirestoreRecyclerOptions.Builder<ListProduct>()
                        .setQuery(query, ListProduct.class)
                        .build();
        Log.d("beforeAdapter", "beforeAdapter");
        adapter = new FirestoreRecyclerAdapter<ListProduct, ViewHolder>(options) {

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cart_item, parent, false);
                return new ViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(final ViewHolder holder, final int position, final ListProduct model) {

                final String productId = model.getProductID();
                Log.d("insideAdapter", model.getProductID());
                final int qty=model.getQty();
                holder.setTxtTitle(productId);

                db.collection("Products").document(productId).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                  @Override
                                                  public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                      Product prod = documentSnapshot.toObject(Product.class);
                                                      holder.setTxtTitle(prod.getProductName());
                                                      holder.setTxtPrice(Double.toString(prod.getPrice()*qty));
                                                      Log.d("title set", "title set");
                                                  }
                                              }

                        );
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
        int i = view.getId();
        if (i == R.id.buttonCheckout) {
            Intent intent = new Intent();
            intent.setClass(ReceiptActivity.this, DeliveryProcessingActivity.class);
            startActivity(intent);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout root;
        public TextView txtTitle;
        public TextView txtQty;
        public TextView txtPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.list_root);
            txtTitle = itemView.findViewById(R.id.list_title);
            txtQty = itemView.findViewById(R.id.list_qty);
            txtPrice = itemView.findViewById(R.id.list_price);
        }

        public void setTxtTitle(String string) {
            txtTitle.setText(string);
        }

        public void setTxtPrice(String string) {
            txtPrice.setText(string);
        }

        public void setTxtQty(String string) {
            txtQty.setText(string);
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

    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(ReceiptActivity.this, CartListActivity.class);
        startActivity(intent);
    }
}
