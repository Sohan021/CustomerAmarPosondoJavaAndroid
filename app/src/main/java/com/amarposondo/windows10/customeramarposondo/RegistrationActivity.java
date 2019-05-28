package com.amarposondo.windows10.customeramarposondo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amarposondo.windows10.customeramarposondo.Model.phonenumber;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class RegistrationActivity extends AppCompatActivity {


    EditText txtphone, code, txtpassword, txtconfirmpassword;

    FirebaseAuth firebaseAuth;

    String Codesent;

    DatabaseReference databaseReference;

    private RelativeLayout rlayout;
    private Animation animation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        firebaseAuth = FirebaseAuth.getInstance();


        databaseReference = FirebaseDatabase.getInstance().getReference("UserInfo");

        txtphone = findViewById(R.id.editTextPhone);
        txtpassword = findViewById(R.id.editpassword);
        txtconfirmpassword = findViewById(R.id.editconfirmpassword);
        code = findViewById(R.id.editCode);

        rlayout = findViewById(R.id.rlayout);
        animation = AnimationUtils.loadAnimation(this,R.anim.uptodowndiagonal);
        rlayout.setAnimation(animation);

        findViewById(R.id.buttonverificationcode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(RegistrationActivity.this,"দয়া করে কিছুক্ষণ অপেক্ষা করুন",Toast.LENGTH_SHORT).show();

                String p = txtphone.getText().toString().trim();

                checkForPhoneNumber(p);
                sendVerificationCode();
            }
        });

        findViewById(R.id.Sign_Up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String pass = txtpassword.getText().toString().trim();
                String comfpass = txtconfirmpassword.getText().toString().trim();
                String phn = txtphone.getText().toString().trim();
                String c = code.getText().toString().trim();



                if(phn.isEmpty())
                {
                    Toast.makeText(RegistrationActivity.this,"দয়া করে ফোন নম্বরটি দিন",Toast.LENGTH_SHORT).show();
                }

                if(c.isEmpty())
                {
                    Toast.makeText(RegistrationActivity.this,"ভেরিফাইড কোডটি বসান",Toast.LENGTH_SHORT).show();
                }

                else if(pass.isEmpty())
                {
                    Toast.makeText(RegistrationActivity.this,"দয়া করে পাসওয়ার্ড দিন",Toast.LENGTH_SHORT).show();
                }
                else if(comfpass.isEmpty())
                {
                    Toast.makeText(RegistrationActivity.this,"পুনরায় পাসওয়ার্ড দিন",Toast.LENGTH_SHORT).show();
                }
                else if(pass.length() <4)
                {
                    Toast.makeText(RegistrationActivity.this,"পাসওয়ার্ড সর্বনিম্ন ৪ টি অক্ষর হতে হবে",Toast.LENGTH_SHORT).show();
                }



                else
                {
                    String codea = code.getText().toString();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(Codesent, codea);

                    if(pass.equals(comfpass))
                    {
                        signInWithPhoneAuthCredential(credential);

                    }



                }


            }
        });

       /* findViewById(R.id.Sign_In).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
            }
        });*/


    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {

                            String pho  = txtphone.getText().toString().trim();
                            String pass = txtpassword.getText().toString().trim();

                            phonenumber info = new phonenumber(

                                    pho,
                                    pass

                            );
                            FirebaseDatabase.getInstance().getReference("UserInfo")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                                    .setValue(info).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(RegistrationActivity.this,"কাজটি সম্পন্য হয়েছে।☺",Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                                }
                            });
                        }
                        else
                        {

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(getApplicationContext(), "ভেরিফিকেশন কোডটি ভুল টাইপ করেছেন", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                });
    }

    boolean checkForPhoneNumber(String number){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("UserInfo");

        ref.orderByChild("phone").equalTo(number).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    Toast.makeText(RegistrationActivity.this,"এই নম্বরটি অলরেডি ব্যবহার হচ্ছে...",Toast.LENGTH_SHORT).show();
                }

                else
                    sendVerificationCode();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return true;
    }
    private void sendVerificationCode() {

        final String phoneno  = txtphone.getText().toString().trim();
        final String password  = txtphone.getText().toString().trim();


        if(phoneno.isEmpty())
        {
            txtphone.setError("দয়া করে ফোন নম্বরটি দিন...");
            txtphone.requestFocus();
            return;
        }
        else if(txtphone.length() < 11)
        {
            txtphone.setError("আপনি ভুল নম্বর টাইপ করেছেন...");
            txtphone.requestFocus();
            return;
        }
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+880" + phoneno,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks); // OnVerificationStateChangedCallbacks
    }
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            Codesent = s;
        }
    };
}
