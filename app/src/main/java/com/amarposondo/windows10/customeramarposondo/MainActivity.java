package com.amarposondo.windows10.customeramarposondo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amarposondo.windows10.customeramarposondo.Model.phonenumber;
import com.amarposondo.windows10.customeramarposondo.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button regbtn, loginbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        regbtn = findViewById(R.id.registration_bt);
        loginbtn = findViewById(R.id.login_btn);

        Paper.init(this);


        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });


        String UserPhoneKay = Paper.book().read(Prevalent.UserPhonekey);
        String UserPasswordKey = Paper.book().read(Prevalent.UserPasswordkey);


        if(UserPhoneKay != "" && UserPasswordKey != "")
        {
            if(!TextUtils.isEmpty(UserPhoneKay) && !TextUtils.isEmpty(UserPasswordKey))
            {
                AllowAccess(UserPhoneKay, UserPasswordKey);
            }
        }




    }

    private void AllowAccess(final String phone, final String password) {


        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference("UserInfo");

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    phonenumber userData = dataSnapshot1.getValue(phonenumber.class);

                    if(userData.getPhone().equals(phone))
                    {
                        if(userData.getPassword().equals(password))

                        {


                            Toast.makeText(MainActivity.this, "Already Logged In", Toast.LENGTH_SHORT).show();
                            //loadingBar.dismiss();

                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            Prevalent.currentOnlineUsers = userData;
                            startActivity(intent);

                        }

                        else
                        {
                            //loadingBar.dismiss();
                            Toast.makeText(MainActivity.this,"Incorrect " +phone+ " Number",Toast.LENGTH_SHORT).show();

                        }

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
