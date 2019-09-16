package com.example.buyandsellapp.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.buyandsellapp.Models.Product;
import com.example.buyandsellapp.R;

import java.util.List;

public class ProductListAdapter extends ArrayAdapter<Product> {
    public ProductListAdapter(Context context, List<Product> object) {
        super(context, 0, object);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_products, parent, false);
        }

        TextView productName = (TextView) convertView.findViewById(R.id.productName);
        TextView productPrice = (TextView) convertView.findViewById(R.id.productPrice);
        TextView productCategory = (TextView) convertView.findViewById(R.id.productCategory);
        TextView productCondition = (TextView) convertView.findViewById(R.id.productCondition);

        Product product = getItem(position);

        productName.setText(product.getProductName());
        productPrice.setText(Double.toString(product.getPrice()));
        productCategory.setText(product.getCategory());
        productCondition.setText(product.getCondition());


        return convertView;
    }

}