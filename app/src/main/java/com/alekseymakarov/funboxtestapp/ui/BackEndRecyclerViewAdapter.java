package com.alekseymakarov.funboxtestapp.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.alekseymakarov.funboxtestapp.R;
import com.alekseymakarov.funboxtestapp.model.Product;
import java.util.Collections;
import java.util.List;

public class BackEndRecyclerViewAdapter extends
        RecyclerView.Adapter<BackEndRecyclerViewAdapter.ViewHolder> {
    public static final String PRODUCT_ID = "productID";
    private Context context;
    private List<Product> products = Collections.EMPTY_LIST;
    private LayoutInflater layoutInflater;

    public BackEndRecyclerViewAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.recycler_view_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name = products.get(position).getName();
        int quantity = products.get(position).getQuantity();
        holder.recyclerviewItemName.setText(name);
        holder.recyclerviewItemQuantity.setText(quantity + " " + context.getString(R.string.piece));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BackendActivityEditProduct.class);
                intent.putExtra(PRODUCT_ID, products.get(position).getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void setProducts (List<Product> products){
        this.products = products;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

       private TextView recyclerviewItemName;
       private TextView recyclerviewItemQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerviewItemName = itemView.findViewById(R.id.recyclerviewItemName);
            recyclerviewItemQuantity = itemView.findViewById(R.id.recyclerviewItemQuantity);
        }
    }
}
