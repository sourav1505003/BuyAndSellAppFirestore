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

import com.example.buyandsellapp.Models.Product;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class UploadedProductListActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseFirestore db;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirestoreRecyclerAdapter adapter;
    private Button buttonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadedproducts);
        Toolbar toolbar = findViewById(R.id.tool);
        setSupportActionBar(toolbar);
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.productListView);
        linearLayoutManager = new LinearLayoutManager(UploadedProductListActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        fetch();
    }

    private void fetch() {
        Query query = db.collection("Products")
                .whereEqualTo("uid", FirebaseAuth.getInstance().getUid());
        Log.d("fetch", "fetch");
        FirestoreRecyclerOptions<Product> options =
                new FirestoreRecyclerOptions.Builder<Product>()
                        .setQuery(query, Product.class)
                        .build();
        Log.d("beforeAdapter", "beforeAdapter");
        adapter = new FirestoreRecyclerAdapter <Product, ViewHolder>(options) {

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item, parent, false);
                return new ViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(final ViewHolder holder, final int position, Product model) {
                Log.d("insideAdapter", "insideAdapter");

                holder.setTxtTitle(model.getProductName());
                holder.setTxtDesc(model.getCategory());
                final String documentId = getSnapshots().getSnapshot(position).getId();
                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //  Toast.makeText(ProductListActivity.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
                        //+adapter.getRef(1)
                        Log.d("ClickedOn", Integer.toString(holder.getAdapterPosition()));
                        Log.d("documentId",documentId);

                        Intent intent = new Intent();
                        intent.setClass(UploadedProductListActivity.this, ProductViewActivity.class);
                        //intent.putExtra("productID", adapter.getRef(holder.getAdapterPosition()).toString());
                        intent.putExtra("productID",documentId);

                        intent.putExtra("category","null");
                        intent.putExtra("prevIntent","UploadedProductListActivity");
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
            txtDesc = itemView.findViewById(R.id.list_price);
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

    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(UploadedProductListActivity.this, WelcomeScreenActivity.class);
        startActivity(intent);
    }
}
