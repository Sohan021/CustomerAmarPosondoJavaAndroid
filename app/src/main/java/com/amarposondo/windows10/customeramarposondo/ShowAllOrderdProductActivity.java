package com.amarposondo.windows10.customeramarposondo;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amarposondo.windows10.customeramarposondo.Model.UserOrder;
import com.amarposondo.windows10.customeramarposondo.Prevalent.Prevalent;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ShowAllOrderdProductActivity extends AppCompatActivity {


    private RecyclerView orderList;
    private DatabaseReference orderRef, a;
    private TextView totalprice;
    private int overTotalPrice = 0;
    private String userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_orderd_product);

            userId = getIntent().getStringExtra("uid");
            orderRef = FirebaseDatabase.getInstance().getReference().child("Orders");
            orderList = findViewById(R.id.order_list);
            orderList.setLayoutManager(new LinearLayoutManager(this));
            totalprice = findViewById(R.id.order_total_price);
        }

        @Override
        protected void onStart() {
            super.onStart();


            FirebaseRecyclerOptions<UserOrder> options =
                    new FirebaseRecyclerOptions.Builder<UserOrder>()
                            .setQuery(orderRef, UserOrder.class)
                            .build();

            FirebaseRecyclerAdapter<UserOrder, AdminOrdersViewHolder> adapter =
                    new FirebaseRecyclerAdapter<UserOrder, AdminOrdersViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull AdminOrdersViewHolder holder, final int position, @NonNull final UserOrder model)
                        {
                            holder.userName.setText("Name: "+model.getName());
                            holder.userPhoneNumber.setText("Phone: "+model.getPhone());
                            holder.userTotalPrice.setText("Total Price: "+model.getTotalAmount());
                            holder.userDateTime.setText("Order At: "+model.getDate()+" "+ model.getTime());
                            holder.userShippingAddress.setText("Address: "+model.getAddress()+", "+model.getCity());

                            holder.showOrdersBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    String uID = getRef(position).getKey();

                                    Intent intent = new Intent(ShowAllOrderdProductActivity.this, AllOrderdProductActivity.class);
                                    intent.putExtra("uid", uID);
                                    startActivity(intent);
                                }
                            });





                        }

                        @NonNull
                        @Override
                        public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
                        {
                            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.orders_layout,viewGroup,false);
                            return new AdminOrdersViewHolder(view);
                        }
                    };

            orderList.setAdapter(adapter);
            adapter.startListening();
        }



        public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder
        {

            public TextView userName, userPhoneNumber, userTotalPrice, userDateTime, userShippingAddress;
            public Button showOrdersBtn;

            public AdminOrdersViewHolder(@NonNull View itemView)
            {
                super(itemView);

                userName = itemView.findViewById(R.id.order_user_name);
                userPhoneNumber = itemView.findViewById(R.id.order_phone_number);
                userTotalPrice = itemView.findViewById(R.id.order_total_price);
                userDateTime = itemView.findViewById(R.id.order_date_time);
                userShippingAddress = itemView.findViewById(R.id.order_addresss_city);
                showOrdersBtn = itemView.findViewById(R.id.show_all_product_btn);

            }
        }

        private void RemoveOrder(String uID)
        {
            orderRef.child(uID).removeValue();
        }
    }
