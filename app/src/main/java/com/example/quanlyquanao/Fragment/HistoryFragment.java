package com.example.quanlyquanao.Fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.quanlyquanao.Activity.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.quanlyquanao.Adapter.HistoryProductAdapter;
import com.example.quanlyquanao.Class.DetailOrder;
import com.example.quanlyquanao.Class.Order;
import com.example.quanlyquanao.R;
import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    // region Variable

    private MainActivity mainActivity;
    private List<Order> listOrder;
    private List<DetailOrder> listDetailOrder;

    private View mView;
    private EditText edtHistoryPhone;
    private Button btnHistorySearch;
    private RecyclerView rcvHitorySearch;

    private HistoryProductAdapter historyProductAdapter;

    // endregion Variable

    public HistoryFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();

        // Khi quay lại từ fragment OrderInfo sẽ thực hiện tìm kiếm lại
        if (!edtHistoryPhone.getText().toString().trim().isEmpty()){
            findOrder();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView =  inflater.inflate(R.layout.fragment_history, container, false);

        initItem();

        return mView;
    }

    // region Private menthod

    // Khởi tạo các item
    private void initItem(){
        listOrder = new ArrayList<>();
        listDetailOrder = new ArrayList<>();

        mainActivity = (MainActivity) getActivity();

        historyProductAdapter = new HistoryProductAdapter();

        edtHistoryPhone = mView.findViewById(R.id.edt_history_phone);

        rcvHitorySearch = mView.findViewById(R.id.rcv_hitory_search);
        btnHistorySearch = mView.findViewById(R.id.btn_history_search);
        btnHistorySearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findOrder();
            }
        });
    }

    // set data cho HistoryProductAdapter
    private void setDataHistoryProductAdapter(){
        historyProductAdapter.setData(listDetailOrder,listOrder,mainActivity);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mainActivity,RecyclerView.VERTICAL,false);
        rcvHitorySearch.setLayoutManager(linearLayoutManager);
        rcvHitorySearch.setAdapter(historyProductAdapter);
    }

    // Lấy thông tin order
    private void findOrder(){

        // Clear các list dữ liệu khi tìm kiếm
        listOrder.clear();
        listDetailOrder.clear();

        // Kết nối tới data base
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("DbOrder");

        // Lấy thông tin order
        myRef.orderByChild("custPhone").equalTo(edtHistoryPhone.getText().toString())
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        historyProductAdapter.notifyDataSetChanged();
                        for (DataSnapshot dataOrder : snapshot.getChildren()){
                            Order order = dataOrder.getValue(Order.class);
                            order.setOrderNo(dataOrder.getKey());
                            listOrder.add(order);
                        }

                        if (listOrder.size() > 0){
                            // Lấy thông tin detail order
                            findDetailOrder(myRef);
                        }
                        else {
                            Toast.makeText(getContext(),"Không tìm thấy lịch sử đặt hàng",Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(),"Không lấy được thông tin đơn hàng từ firebase",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Lấy thông tin detail order
    private void findDetailOrder( DatabaseReference myRef){
        if (listOrder.size() > 0){
            for (int i = 0; i<listOrder.size(); i++){
                Order order = listOrder.get(i);
                myRef.child(order.getOrderNo()).child("detail").addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataDetail : snapshot.getChildren()){
                            historyProductAdapter.notifyDataSetChanged();
                            DetailOrder detailOrder = dataDetail.getValue(DetailOrder.class);
                            listDetailOrder.add(detailOrder);
                        }

                        // set data HistoryProductAdapter
                        if (listDetailOrder.size() > 0){
                            setDataHistoryProductAdapter();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(),"Không lấy được chi tiết đơn hàng từ firebase",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    // endregion Private menthod

}