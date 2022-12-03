package com.example.quanlyquanao.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.example.quanlyquanao.Activity.MainActivity;
import com.example.quanlyquanao.Adapter.ProductAdapter;
import com.example.quanlyquanao.Adapter.ProductSearchAdapter;
import com.example.quanlyquanao.Adapter.SlidePhotoAdapter;
import com.example.quanlyquanao.Class.Product;
import com.example.quanlyquanao.Class.SlidePhoto;
import com.example.quanlyquanao.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;


public class ProductFragment extends Fragment {

    private MainActivity mainActivity;
    private Timer mTimer;
    private List<SlidePhoto> listSlidePhoto;
    private List<Product> listAllProduct;

    private View mView;
    private RecyclerView rcvProduct;
    private ViewPager viewPagerSlidePhoto;
    private CircleIndicator circleIndicator;
    private AutoCompleteTextView atcProductSearch;

    private ProductAdapter productAdapter;
    private SlidePhotoAdapter slidePhotoAdapter;

    // endregion Variable

    public ProductFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_product, container, false);

        // Khởi tạo các item
        initItem();

        // Set Adapter cho viewPagerSlidePhoto
        setDataSlidePhotoAdapter();

        // Set Adapter cho rcvProduct
        setDataProductAdapter();

        return mView;
    }

    // region Private menthod

    // Khởi tạo các item
    private void initItem(){
        rcvProduct = mView.findViewById(R.id.rcv_product);
        viewPagerSlidePhoto = mView.findViewById(R.id.vp_slide_photo);
        circleIndicator = mView.findViewById(R.id.circle_indicator);
        atcProductSearch = mView.findViewById(R.id.atc_product_search);

        listSlidePhoto = getListSlidePhoto();
        listAllProduct = getDataProduct();

        mainActivity = (MainActivity) getActivity();
    }
    // Set Adapter cho viewPagerSlidePhoto
    private void setDataSlidePhotoAdapter(){
        slidePhotoAdapter = new SlidePhotoAdapter(listSlidePhoto, this);
        viewPagerSlidePhoto.setAdapter(slidePhotoAdapter);
        circleIndicator.setViewPager(viewPagerSlidePhoto);
        slidePhotoAdapter.registerDataSetObserver(circleIndicator.getDataSetObserver());

        // Auto chuyển các slide photo
        autoSildeImage();
    }

    // Auto chuyển các slide photo
    private void autoSildeImage(){
        if(listSlidePhoto == null || listSlidePhoto.isEmpty() || viewPagerSlidePhoto == null){
            return;
        }
        if (mTimer == null){
            mTimer = new Timer();
        }
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        int currentItem = viewPagerSlidePhoto.getCurrentItem();
                        int totalItem = listSlidePhoto.size() - 1;

                        // Nếu item hiện tại chưa phải cuối cùng
                        if(currentItem < totalItem){
                            currentItem++;
                            viewPagerSlidePhoto.setCurrentItem(currentItem);
                        }else {
                            viewPagerSlidePhoto.setCurrentItem(0);
                        }
                    }
                });
            }

            // xử lý thêm để set time
        },500,3000 );
    }

    // Set Adapter cho rcvProduct
    private void setDataProductAdapter(){

        GridLayoutManager gridLayoutManager = new GridLayoutManager(mainActivity, 2);
        rcvProduct.setLayoutManager(gridLayoutManager);

        productAdapter = new ProductAdapter();
        productAdapter.setData(listAllProduct,mainActivity);

        rcvProduct.setAdapter(productAdapter);
    }

    // Set Adapter cho atcProductSearch
    private void setProductSearchAdapter(List<Product> listProduct ){
        ProductSearchAdapter productSearchAdapter = new ProductSearchAdapter(mainActivity,R.layout.item_search, listProduct);
        atcProductSearch.setAdapter(productSearchAdapter);

        // Sau khi chọn item search sẽ chuyển sang fragment detail
        atcProductSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mainActivity.toDetailProductFragment(listProduct.get(position));
            }
        });
    }
    // Lấy Product để vào slide
    private List<SlidePhoto> getListSlidePhoto(){
        List<SlidePhoto> listSlidePhoto = new ArrayList<>();
        listSlidePhoto.add(new SlidePhoto(R.drawable.slide1));
        listSlidePhoto.add(new SlidePhoto(R.drawable.slide2));
        listSlidePhoto.add(new SlidePhoto(R.drawable.slide3));
        listSlidePhoto.add(new SlidePhoto(R.drawable.slide4));
        listSlidePhoto.add(new SlidePhoto(R.drawable.slide5));
        return listSlidePhoto;
    }

    // Lấy dữ liệu Product từ FireBase
    private List<Product> getDataProduct(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("DBProduct");

        List<Product> mListProduct = new ArrayList<>();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productAdapter.notifyDataSetChanged();

                for (DataSnapshot data : snapshot.getChildren()){
                    Product product = data.getValue(Product.class);
                    product.setId(data.getKey());
                    mListProduct.add(product);
                }
                setProductSearchAdapter(mListProduct);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(),"Không tải được dữ liệu từ firebase"
                        +error.toString(),Toast.LENGTH_LONG).show();
                Log.d("MYTAG","onCancelled"+ error.toString());
            }
        });
        return mListProduct;
    }
}