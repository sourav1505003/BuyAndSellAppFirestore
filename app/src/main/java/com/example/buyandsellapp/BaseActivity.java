package com.example.buyandsellapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class BaseActivity extends AppCompatActivity {

    // Activity code here
    private ImageView carticon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* carticon = (ImageView) findViewById(R.id.cartIcon);
        carticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("carticon","carticon");
                Intent intent = new Intent();
                intent.setClass(BaseActivity.this,CartListActivity.class);
                startActivity(intent);
            }
        });*/
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_uploaded_products){
            Intent intent = new Intent();
            intent.setClass(BaseActivity.this, UploadedProductListActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.menu_logout){
            Log.d("loggingout","loggingout");
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent();
            intent.setClass(BaseActivity.this,MainActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.action_editProfile){
            Log.d("action_editProfile","action_editProfile");
            Intent intent = new Intent();
            intent.setClass(BaseActivity.this,EditProfileActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}
