package com.amarposondo.windows10.customeramarposondo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amarposondo.windows10.customeramarposondo.Model.Cart;
import com.amarposondo.windows10.customeramarposondo.Prevalent.Prevalent;
import com.amarposondo.windows10.customeramarposondo.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CartActivityUser extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button NextProcessBtn;
    private TextView txtTotalBtn, txtmsg1;
    private Button ordrdprdct;

    private int overTotalPrice = 0, a;
    private int oneTypeproductTotalPrice;
    private String userId = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_user);

        userId = getIntent().getStringExtra("uid");

        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        NextProcessBtn = findViewById(R.id.next_process_btn);
        txtTotalBtn = findViewById(R.id.total_price);
        txtmsg1 = findViewById(R.id.msg1);

        ordrdprdct = findViewById(R.id.show_all_orderd_product);



        NextProcessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                txtTotalBtn.setText("Total Price = TK"+String.valueOf(overTotalPrice));

                Intent intent = new Intent(CartActivityUser.this, ConfirmFinalProductActivity.class);
                intent.putExtra("Total Price", String.valueOf(overTotalPrice));
                startActivity(intent);
                finish();
            }
        });

        ordrdprdct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uID = Prevalent.currentOnlineUsers.getPhone();
                Intent intent = new Intent(CartActivityUser.this, ShowAllOrderdProductActivity.class);
                intent.putExtra("uid", uID);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        CheckOrderState();

        ordrdprdct.setVisibility(View.INVISIBLE);

        final DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        final FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(cartRef.child("Admin View")
                                .child(Prevalent.currentOnlineUsers.getPhone())
                                .child("Products"),Cart.class)
                        .build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model)
                    {
                        holder.txtProductQuantity.setText("পরিমাণ: "+model.getQuantity());
                        holder.txtProductPrice.setText("মূল্য "+model.getPrice());
                        holder.txtProductName.setText(model.getName());


                        a = Integer.parseInt(model.getPrice());
                        overTotalPrice = overTotalPrice + a;

                        txtTotalBtn.setText("মোট মূল্য ="+String.valueOf(overTotalPrice)+" টাকা");


                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                CharSequence options[] = new CharSequence[]
                                        {
                                                "ইডিট",
                                                "আপনি কি এই পন্যের অর্ডারটি মুছে ফেলতে চাচ্ছেন???"
                                        };
                                AlertDialog.Builder builder = new AlertDialog.Builder(CartActivityUser.this);
                                builder.setTitle("\uD83D\uDE25\uD83D\uDE25\uD83D\uDE25");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i)
                                    {
                                        if(i == 0)
                                        {
                                            Intent intent = new Intent(CartActivityUser.this, ProductDetailsActivity.class);
                                            intent.putExtra("pid", model.getPid());
                                            startActivity(intent);
                                        }
                                        if(i == 1 )
                                        {
                                            cartRef.child("Admin View")
                                                    .child(Prevalent.currentOnlineUsers.getPhone())
                                                    .child("Products")
                                                    .child(model.getPid())
                                                    .removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task)
                                                        {
                                                            if(task.isSuccessful())
                                                            {
                                                                Toast.makeText(CartActivityUser.this,"এই অর্ডারটি সম্পুর্নভাবে মুছে গেছে।",Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(CartActivityUser.this, HomeActivity.class);
                                                                startActivity(intent);
                                                            }

                                                        }
                                                    });
                                        }

                                    }
                                });


                                builder.show();
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout,parent,false);
                        CartViewHolder holder = new CartViewHolder(view);
                        return holder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();





    }
    private void CheckOrderState()
    {
        DatabaseReference orderRef;
        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUsers.getPhone());

        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    String shippingState = dataSnapshot.child("state").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();

                    if(shippingState.equals("shipped"))
                    {
                        txtTotalBtn.setText("Dear "+userName+"\n order is shipped Successfully" );
                        recyclerView.setVisibility(View.GONE);
                        ordrdprdct.setVisibility(View.INVISIBLE);
                        txtmsg1.setVisibility(View.VISIBLE);
                        txtmsg1.setText("Successfully Shipped, Soon you will receive your order");
                        NextProcessBtn.setVisibility(View.GONE);

                    }
                    else if (shippingState.equals("not shipped"))
                    {
                        ordrdprdct.setVisibility(View.INVISIBLE);
                        txtTotalBtn.setText("Not Shipped" );
                        recyclerView.setVisibility(View.GONE);
                        txtmsg1.setVisibility(View.VISIBLE);
                        NextProcessBtn.setVisibility(View.GONE);
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
