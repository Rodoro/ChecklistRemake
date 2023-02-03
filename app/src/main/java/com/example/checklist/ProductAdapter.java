package com.example.checklist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder>{
    interface OnProductClickListener{
        void onProductClick(Product product, int position);
    }

    private final OnProductClickListener onClickListener;

    private final LayoutInflater inflater;
    private final List<Product> products;

    ProductAdapter(Context context, List<Product> products, OnProductClickListener onClickListener) {
        this.onClickListener = onClickListener;
        this.products = products;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public  ProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Product product = products.get(position);
        holder.nameView.setText(product.getName());
        holder.quantityView.setText(product.getQuantity());

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                onClickListener.onProductClick(product, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nameView, quantityView;
        ViewHolder(View view){
            super(view);
            nameView = view.findViewById(R.id.productName);
            quantityView = view.findViewById(R.id.productQuantity);
        }
    }
}