package com.amarposondo.windows10.customeramarposondo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amarposondo.windows10.customeramarposondo.Model.phonenumber;
import com.amarposondo.windows10.customeramarposondo.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private EditText txtphn, txtpass;
    private Button lgn;
    private CheckBox checkBox;
    private Animation animation;
    private RelativeLayout rlayout;
    private TextView forgottenPassword;

    private String parentDbName = "UserInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtphn = findViewById(R.id.editTextPhonelogin);
        txtpass = findViewById(R.id.editpasswordlogin);
        checkBox = findViewById(R.id.rememberme);
        forgottenPassword = findViewById(R.id.forgetpassword);


        rlayout = findViewById(R.id.rlayout);
        animation = AnimationUtils.loadAnimation(this,R.anim.uptodowndiagonal);
        rlayout.setAnimation(animation);

        lgn = findViewById(R.id.loginid);

        Paper.init(this);

        lgn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginUser();
            }
        });

        forgottenPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgottenPassword();
            }
        });
    }

    private void ForgottenPassword() {

        Intent intent = new Intent(LoginActivity.this,ForgotPasswordActivity.class);
        startActivity(intent);

    }


    private void loginUser() {

        String phoneno = txtphn.getText().toString().trim();
        String password = txtpass.getText().toString().trim();

        if(TextUtils.isEmpty(phoneno) )
        {
            Toast.makeText(LoginActivity.this,"দয়া করে ফোন নম্বরটি দিন",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password) )
        {
            Toast.makeText(LoginActivity.this,"দয়া করে পাসওয়ার্ড দিন",Toast.LENGTH_SHORT).show();
            return;
        }

        else
        {
            AllowAccessToAccount(phoneno, password);
        }

    }

    private void AllowAccessToAccount(final String phone, final String password) {

        if(checkBox.isChecked())
        {
            Paper.book().write(Prevalent.UserPhonekey, phone);
            Paper.book().write(Prevalent.UserPasswordkey, password);
        }

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

                            if(parentDbName.equals("UserInfo")) {
                                Toast.makeText(LoginActivity.this, "লগইন", Toast.LENGTH_SHORT).show();
                                //loadingBar.dismiss();

                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                Prevalent.currentOnlineUsers = userData;
                                startActivity(intent);
                            }
                        }

                        else if(phone.length()<11 || userData.getPassword() != password)
                        {
                            //loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this,"নম্বর অথবা পাসওয়ার্ড ভুল টাইপ করেছেন। পুনরায় চেস্টা করুন।",Toast.LENGTH_SHORT).show();

                        }


                    }

                    /*else if(phone.length()<11)
                    {
                        //loadingBar.dismiss();
                        Toast.makeText(LoginActivity.this,"আপনি ভুল নম্বর টাইপ করেছেন...",Toast.LENGTH_SHORT).show();

                    }

                    else if(userData.getPassword() != password)
                    {
                        //loadingBar.dismiss();
                        Toast.makeText(LoginActivity.this,"আআপনি ভুল পাসওয়ার্ড টাইপ করেছেন",Toast.LENGTH_SHORT).show();

                    }*/

                    else if(phone.length()<11 && userData.getPassword() != password)
                    {
                        //loadingBar.dismiss();
                        Toast.makeText(LoginActivity.this,"নম্বর অথবা পাসওয়ার্ড ভুল টাইপ করেছেন। পুনরায় চেস্টা করুন।",Toast.LENGTH_SHORT).show();

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
