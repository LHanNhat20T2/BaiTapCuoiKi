package com.example.quanlyquanao.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.example.quanlyquanao.Class.Order;
import com.example.quanlyquanao.Class.Product;
import com.example.quanlyquanao.Fragment.CartFragment;
import com.example.quanlyquanao.Fragment.DetailProductFragment;
import com.example.quanlyquanao.Fragment.HistoryFragment;
import com.example.quanlyquanao.Fragment.OrderInfoFragment;
import com.example.quanlyquanao.Fragment.ProductFragment;
import com.example.quanlyquanao.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private AHBottomNavigation ahBotNavHome;

    private List<Product> listCartProduct;
    private int countProduct;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Khởi tạo các item
        initItem();

        // Set data cho ahBotNavHome
        setDataBotNavHome();
    }
    // region  Private Menthod
    // khởi tạo các item
    private void initItem() {
        ahBotNavHome = findViewById(R.id.ahbotnav_home);
        if(listCartProduct == null){
            listCartProduct = new ArrayList<>();
        }
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.contet_frame, new ProductFragment());

        fragmentTransaction.commit();
    }

    private void setDataBotNavHome() {

        // Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.tab_product, R.drawable.ic_baseline_home_24, R.color.teal_200);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.tab_cart, R.drawable.ic_baseline_add_shopping_cart_24, R.color.gray);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.tab_history, R.drawable.ic_baseline_history_24, R.color.yellow);

        // Add items
        ahBotNavHome.addItem(item1);
        ahBotNavHome.addItem(item2);
        ahBotNavHome.addItem(item3);

        ahBotNavHome.setColored(false);

        // Set màu nav
        ahBotNavHome.setDefaultBackgroundColor(getResources().getColor(R.color.white));

        // Khi click vào các icon trên nav
        ahBotNavHome.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {

                switch (position){
                    case 0:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.contet_frame, new ProductFragment());
                        fragmentTransaction.commit();
                        break;

                    case 1:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.contet_frame, new CartFragment(listCartProduct));
                        fragmentTransaction.commit();
                        break;
                    case 2:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.contet_frame, new HistoryFragment());
                        fragmentTransaction.commit();
                        break;
                }
                return true;
            }
        });
      }

    // Mở Fragment DetailProduct
    public void toDetailProductFragment(Product product){
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.contet_frame, new DetailProductFragment(product,listCartProduct));
        fragmentTransaction.commit();
    }
    // Thêm sản phẩm đã chọn vào giỏ hàng
    public void addToListCartProdct(Product product){
        listCartProduct.add(product);
    }

    // Set số lượng các sản phẩm trong giỏ hàng
    public void setCountProductInCart(int count){
        countProduct = count;
        AHNotification notification = new AHNotification.Builder()
                .setText(String.valueOf(count))
                .setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.red))
                .setTextColor(ContextCompat.getColor(MainActivity.this, R.color.white))
                .build();
        ahBotNavHome.setNotification(notification, 1);
    }
    // Mở Fragment OrderInfo
    public void toOrderInfoFragment(Order orderInfo){
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.contet_frame, new OrderInfoFragment(orderInfo));
        fragmentTransaction.addToBackStack(OrderInfoFragment.TAG);
        fragmentTransaction.commit();
    }


    // Set lại số lượng của sản phẩm khi mua nhiều
    public void setCountForProduct(int possion, int countProduct){
        listCartProduct.get(possion).setNumProduct(countProduct);
    }

    // Lấy ra các sản phẩm đã thêm vào giỏ hàng
    public List<Product> getListCartProduct() {
        return listCartProduct;
    }
    // Lấy ra số lượng các sản phẩm đã thêm vào giỏ hàng
    public int getCountProduct() {
        return countProduct;
    }
}