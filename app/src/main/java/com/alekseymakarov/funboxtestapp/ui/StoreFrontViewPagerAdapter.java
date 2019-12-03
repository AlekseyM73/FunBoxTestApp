package com.alekseymakarov.funboxtestapp.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.alekseymakarov.funboxtestapp.R;
import com.alekseymakarov.funboxtestapp.model.Product;

import java.util.Collections;
import java.util.List;

public class StoreFrontViewPagerAdapter
        extends RecyclerView.Adapter<StoreFrontViewPagerAdapter.ViewHolder> {

    private List<Product> products = Collections.EMPTY_LIST;
    private Context context;
    private LayoutInflater layoutInflater;

    public StoreFrontViewPagerAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.view_pager2_item,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            int quantity = products.get(position).getQuantity();
            String name = products.get(position).getName();
            double price = products.get(position).getPrice();
            holder.nameTextView.setText(name);
            //Знаю, что так слова не соединяют)
            holder.priceTextView.setText(context
                    .getString(R.string.price) + " " + price +
                    " " + context.getString(R.string.rub));
            holder.quantityTextView.setText(context
                    .getString(R.string.quantity)+ " " + quantity +
                    " " + context.getString(R.string.piece));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void setProducts (List<Product> products){
        this.products = products;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTextView;
        private TextView priceTextView;
        private TextView quantityTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            setViews(itemView);
        }

        private void setViews(View itemView){
            nameTextView = itemView.findViewById(R.id.viewPagerNameTextView);
            priceTextView = itemView.findViewById(R.id.viewPagerPriceTextView);
            quantityTextView = itemView.findViewById(R.id.viewPagerQuantityTextView);
        }

    }
    public Product getProductFromAdapter (int position){
        return products.get(position);
    }
}
