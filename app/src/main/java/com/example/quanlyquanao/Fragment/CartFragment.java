package com.example.quanlyquanao.Fragment;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quanlyquanao.Activity.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.quanlyquanao.Adapter.ProductCartAdapter;
import com.example.quanlyquanao.Class.DetailOrder;
import com.example.quanlyquanao.Class.Product;
import com.example.quanlyquanao.R;

import java.sql.Date;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartFragment extends Fragment {

    // region Variable

    private int totalPrice;
    private View mView;
    private MainActivity mainActivity;
    private DecimalFormat format;

    // Item empty cart
    private RelativeLayout rlCartEmpty,rlCart;

    // Item product cart
    private List<Product> listCartProduct;
    private RecyclerView rcvCartProduct;
    private TextView tvCartTotalPrice;
    private EditText edtCartCustName,edtCartCustAddress,edtCartCustPhone;
    private Button btnCartOrder;

    private ProductCartAdapter productCartAdapter;

    // endregion Variable

    public CartFragment(List<Product> listCartProduct) {
        this.listCartProduct = listCartProduct;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_cart, container, false);

        // Kh???i t???o c??c item
        initItem();

        // Set hi???n th??? c??c view
        setVisibilityView();

        return mView;
    }

    // region Private menthod

    // Kh???i t???o c??c item
    private void initItem(){
        productCartAdapter = new ProductCartAdapter();
        rlCartEmpty = mView.findViewById(R.id.rl_cart_empty);
        rlCart = mView.findViewById(R.id.rl_cart);
        rcvCartProduct = mView.findViewById(R.id.rcv_cart_product);
        tvCartTotalPrice = mView.findViewById(R.id.tv_cart_total_price);
        edtCartCustName = mView.findViewById(R.id.edt_cart_cust_name);
        edtCartCustAddress = mView.findViewById(R.id.edt_cart_cust_address);
        edtCartCustPhone = mView.findViewById(R.id.edt_cart_cust_phone);
        btnCartOrder = mView.findViewById(R.id.btn_cart_order);
        btnCartOrder.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                // Th??m d??? li???u c??c th??ng tin ???? order
                addDataOrder();
            }
        });

        mainActivity = (MainActivity) getActivity();
        format = new DecimalFormat("###,###,###");
    }

    // Set tr???ng th??i hi???n th??? c??c view
    private void setVisibilityView(){
        if (listCartProduct.size() == 0){

            // Hi???n th??? gi??? h??ng r???ng
            setVisibilityEmptyCart();
        }else {

            // Hi???n th??? gi??? h??ng
            setVisibilityCart();
            setDataProductCartAdapter();
        }
    }

    // Hi???n th??? gi??? h??ng
    private void setVisibilityCart(){
        rlCartEmpty.setVisibility(View.GONE);
        rlCart.setVisibility(View.VISIBLE);
        String total = format.format(getTotalPrice());
        tvCartTotalPrice.setText("??? "+ total);
    }

    // set data ProductCartAdapter
    private void setDataProductCartAdapter(){
        productCartAdapter.setData(listCartProduct,mainActivity,this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mainActivity,RecyclerView.VERTICAL,false);
        rcvCartProduct.setLayoutManager(linearLayoutManager);
        rcvCartProduct.setAdapter(productCartAdapter);
    }

    // l???y gi?? tr??? t???ng ti???n t???t c??? s???n ph???m trong gi??? h??ng
    private int getTotalPrice(){
        for (Product product : listCartProduct){
            int priceProduct = product.getProductPrice() ;
            totalPrice = totalPrice +  priceProduct * product.getNumProduct();
        }
        return totalPrice;
    }

    // Th??m d??? li???u c??c th??ng tin ???? order
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addDataOrder(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("DbOrder");

        Map<String,Object> map = new HashMap<>();

        // L???y ng??y order = now
        Date date = new Date(System.currentTimeMillis());
        map.put("dateOrder", date.toString());
        map.put("custName",edtCartCustName.getText().toString());
        map.put("custAddress",edtCartCustAddress.getText().toString());
        map.put("custPhone",edtCartCustPhone.getText().toString());

        int num = 0;
        for (Product product : listCartProduct){
            num = num + product.getNumProduct();
        }
        map.put("numProduct",num);
        map.put("totalPrice",totalPrice);
        map.put("status","??ang ch??? x??c nh???n");

        // Add th??ng tin order
        String odrKey = myRef.push().getKey();
        myRef.child(odrKey).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                List<DetailOrder> listDetailOrder = makeDetailOrder(odrKey);

                // Add th??ng tin detail order
                for (DetailOrder detailOrder : listDetailOrder){

                    myRef.child(odrKey).child("detail").child(myRef.push().getKey())
                            .setValue(detailOrder).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getContext(),"???? ????ng k?? ????n h??ng",Toast.LENGTH_SHORT).show();
                                    listCartProduct.clear();
                                    setVisibilityEmptyCart();
                                    mainActivity.setCountProductInCart(0);
                                }
                            });

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"????ng k?? ????n h??ng th???t b???i",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<DetailOrder> makeDetailOrder( String odrNo){
        List<DetailOrder> listDetailOrder = new ArrayList<>();
        for (Product product : mainActivity.getListCartProduct()){
            DetailOrder detailOrder = new DetailOrder();
            detailOrder.setOrderNo(odrNo);
            detailOrder.setProductName(product.getProductName());
            detailOrder.setProductPrice(product.getProductPrice());
            detailOrder.setUrlImg(product.getUrlImg());
            detailOrder.setNumProduct(product.getNumProduct());
            detailOrder.setStatus("??ang ch??? x??c nh???n");
            listDetailOrder.add(detailOrder);
        }
        return listDetailOrder;
    }

    // endregion Private menthod

    // region Public menthod

    // Hi???n th??? gi??? h??ng r???ng
    public void setVisibilityEmptyCart(){
        rlCartEmpty.setVisibility(View.VISIBLE);
        rlCart.setVisibility(View.GONE);
    }

    // Set gi?? tr??? hi???n th??? t???ng ti???n khi t??ng gi???m s??? l?????ng
    // mode = 0 : gi???m
    // mode = 1 : t??ng
    public void setTotalPrice(int mode,int count, int priceProduct ){
        if( mode == 0){
            totalPrice = totalPrice - priceProduct * count;
        }else if (mode == 1){
            totalPrice = totalPrice + priceProduct * count;
        }

        tvCartTotalPrice.setText("??? "+format.format(totalPrice));
    }

    // Set s?? l?????ng s???n ph???m sau nh???n n??t t??ng gi???m
    public void setCountForProduct(int possion,int countProduct){
        listCartProduct.get(possion).setNumProduct(countProduct);
    }

    // endregion Public menthod

}