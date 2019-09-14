package com.example.buyandsellapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

import customfonts.MyEditText;

public class ProductListActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseFirestore db;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    public FirestoreRecyclerAdapter adapter;
    private Spinner spinner1;
    private Button buttonFilter;
    private Button buttonSearch;
    private MyEditText searchBox;
    private String category;
    String uid;
    private TextView noresult;
    int count;
    private Query query;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productlist);
        addListenerOnButton();
        addListenerOnSpinnerItemSelection();
        buttonFilter = findViewById(R.id.buttonFilter);
        buttonSearch = findViewById(R.id.buttonSearch);
        searchBox = (MyEditText) findViewById(R.id.textboxSearch);
        noresult = (TextView) findViewById(R.id.noresult);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.productListView);
        linearLayoutManager = new LinearLayoutManager(ProductListActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        Intent intent = getIntent();
        category = intent.getStringExtra("category");
        count = 0;

        try {
            query = db.collection("Products").whereEqualTo("category", category);
            Log.d("query", category);
            buttonFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    query = db.collection("Products")
                            .whereEqualTo("category", String.valueOf(spinner1.getSelectedItem()));
                    fetch();
                }
            });
            buttonSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    query = db.collection("Products")
                            .whereEqualTo("productName", searchBox.getText().toString());
                    query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w("Tag", "Listen failed.", e);
                                return;
                            }
                            if (queryDocumentSnapshots != null && queryDocumentSnapshots.isEmpty() == false) {
                                Log.d("size", queryDocumentSnapshots.size() + "Current data: " + queryDocumentSnapshots.toString());
                                count = queryDocumentSnapshots.size();
                                if (count > 0) {
                                    recyclerView.setVisibility(View.VISIBLE);
                                    noresult.setVisibility(View.INVISIBLE);
                                } else {
                                    recyclerView.setVisibility(View.INVISIBLE);
                                    noresult.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Log.d("Tag", "Current data: null");
                            }
                        }
                    });
                    fetch();
                }
            });
            fetch();
        } catch (Exception e) {
            Log.e("null", e.toString());
        }
    }

    private void fetch() {

        Log.d("fetch", "fetch");
        FirestoreRecyclerOptions<Product> options =
                new FirestoreRecyclerOptions.Builder<Product>()
                        .setQuery(query, Product.class)
                        .build();
        Log.d("beforeAdapter", "beforeAdapter");
        adapter = new FirestoreRecyclerAdapter<Product, ViewHolder>(options) {

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item, parent, false);
                return new ViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(final ViewHolder holder, final int position, final Product model) {

                Log.d("insideAdapter", model.getProductName());

                holder.setTxtTitle(model.getProductName());
                holder.setTxtDesc(model.getCategory());
                uid = model.getUid();
                final String documentId = getSnapshots().getSnapshot(position).getId();
                Log.d("documentId", documentId);
                if (model.getUid().equals(mAuth.getUid()) == true)
                    holder.setTxtDesc(model.getCategory() + "\nThis Product is uploaded by you");
                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Log.d("ClickedOn", model.getUid() + "    " + mAuth.getUid());
                        Intent intent = new Intent();
                        intent.setClass(ProductListActivity.this, ProductViewCartWishlistActivity.class);
                        intent.putExtra("prevIntent", "ProductListActivity");
                        Log.d("documentId",documentId);
                        intent.putExtra("productID", documentId);
                        intent.putExtra("category", category);
                        if (model.getUid().equals(mAuth.getUid()) == true)
                            intent.putExtra("productUID", "own");
                        else
                            intent.putExtra("productUID", "other");

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
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        Log.d("gotAuthData", mAuth.getUid());
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

    public void addListenerOnSpinnerItemSelection() {
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    public void addListenerOnButton() {
        spinner1 = (Spinner) findViewById(R.id.spinner1);
    }

    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(ProductListActivity.this, CategoryActivity.class);
        startActivity(intent);
    }
}
