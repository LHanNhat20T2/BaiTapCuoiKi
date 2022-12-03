package com.example.quanlyquanao.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.bumptech.glide.Glide;
import com.example.quanlyquanao.Activity.MainActivity;
import com.example.quanlyquanao.Class.Product;
import com.example.quanlyquanao.Fragment.CartFragment;
import com.example.quanlyquanao.Activity.MainActivity;
import com.example.quanlyquanao.R;

import java.text.DecimalFormat;
import java.util.List;

public class ProductCartAdapter extends RecyclerView.Adapter<ProductCartAdapter.ProductCartViewHolder> {

    private DecimalFormat formatPrice = new DecimalFormat("###,###,###");
    private List<Product> listProductCart;
    private int countProduct;
    private MainActivity mainActivity;
    private CartFragment cartFragment;

    public void setData(List<Product> listProductCart, MainActivity mainActivity,CartFragment cartFragment) {
        this.listProductCart = listProductCart;
        this.mainActivity = mainActivity;
        this.cartFragment = cartFragment;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductCartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart,parent,false);
        return new ProductCartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductCartViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Product product = listProductCart.get(position);
        if (product == null){
            return;
        }
        else {
            Glide.with(holder.imgPhotoCart.getContext()).load(product.getUrlImg()).into(holder.imgPhotoCart);
            holder.tvNameProductCart.setText(product.getProductName());
            holder.tvPriceProductCart.setText("₫ " + formatPrice.format(product.getProductPrice()));

            int count = product.getNumProduct();
            if(count != 0){
                holder.tvCountCart.setText(String.valueOf(count));
            }else {
                holder.tvCountCart.setText(String.valueOf(1));
            }

            holder.imgMinusCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    countProduct = Integer.parseInt(holder.tvCountCart.getText().toString());
                    if (countProduct > 1){
                        countProduct--;
                        mainActivity.setCountProductInCart(mainActivity.getCountProduct() - 1);
                        mainActivity.setCountForProduct(position,countProduct);
                        cartFragment.setCountForProduct(position,countProduct);
                        holder.tvCountCart.setText(String.valueOf(countProduct));
                        cartFragment.setTotalPrice(0,1,product.getProductPrice());
                        notifyDataSetChanged();
                    }
                }
            });

            holder.imgPlusCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    countProduct = Integer.parseInt(holder.tvCountCart.getText().toString());
                    if (countProduct < 10){
                        countProduct++;
                        mainActivity.setCountProductInCart(mainActivity.getCountProduct() + 1);
                        mainActivity.setCountForProduct(position,countProduct);
                        cartFragment.setCountForProduct(position,countProduct);
                        holder.tvCountCart.setText(String.valueOf(countProduct));
                        cartFragment.setTotalPrice(1,1,product.getProductPrice());
                        notifyDataSetChanged();
                    }
                }
            });

            holder.imgRemoveCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    countProduct = Integer.parseInt(holder.tvCountCart.getText().toString());
                    mainActivity.setCountProductInCart(mainActivity.getCountProduct() - countProduct);

                    cartFragment.setTotalPrice(0,countProduct,product.getProductPrice());

                    listProductCart.remove(position);
                    if (listProductCart.size() == 0){
                        cartFragment.setVisibilityEmptyCart();
                    }
                    notifyDataSetChanged();
                }
            });
        }
    }

    public class ProductCartViewHolder extends ViewHolder{

        ImageView imgPhotoCart;
        TextView tvNameProductCart, tvPriceProductCart,tvCountCart ;
        ImageView imgMinusCart,imgPlusCart,imgRemoveCart;

        public ProductCartViewHolder(@NonNull View itemView) {
            super(itemView);

            imgPhotoCart = itemView.findViewById(R.id.img_photo_cart);
            tvNameProductCart = itemView.findViewById(R.id.tv_name_product_cart);
            tvPriceProductCart = itemView.findViewById(R.id.tv_price_product_cart);
            imgMinusCart = itemView.findViewById(R.id.img_minus_cart);
            tvCountCart = itemView.findViewById(R.id.tv_count_cart);
            imgPlusCart = itemView.findViewById(R.id.img_plus_cart);
            imgRemoveCart = itemView.findViewById(R.id.img_remove_cart);
        }
    }

    @Override
    public int getItemCount() {
        if(listProductCart != null){
            return listProductCart.size();
        }
        return 0;
    }
}
