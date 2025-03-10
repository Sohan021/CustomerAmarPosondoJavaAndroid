package com.amarposondo.windows10.customeramarposondo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amarposondo.windows10.customeramarposondo.Prevalent.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView profileImageView;
    private EditText fullNameEditText, userPhoneEditText, addressEditText;
    private TextView profileChangeTextBtn, closeTextBtn, saveTextBtn;

    private Uri imageUri;
    private String myUrl;
    private StorageTask uploadTask;
    private StorageReference storageProfilePictureRef;
    private String checker = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        storageProfilePictureRef = FirebaseStorage.getInstance().getReference().child("Profile Pictures");

        profileImageView = findViewById(R.id.setting_profile_image);
        fullNameEditText = findViewById(R.id.settings_fullname);
        userPhoneEditText = findViewById(R.id.settings_phone_number);
        addressEditText = findViewById(R.id.settings_address);

        profileChangeTextBtn = findViewById(R.id.profile_image_change_btn);
        closeTextBtn = findViewById(R.id.close_settings);
        saveTextBtn = findViewById(R.id.update_account_settings);

        userInfoDisplay(profileImageView, fullNameEditText, userPhoneEditText, addressEditText);



        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checker.equals("clicked"))
                {
                    userInfoSaved();
                }
                else
                {
                    updateOnlyUserInfoButton();
                }
            }
        });

        profileChangeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker = "clicked";

                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(SettingsActivity.this);


            }
        });


    }

    private void updateOnlyUserInfoButton()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("name", fullNameEditText.getText().toString());
        userMap.put("address", addressEditText.getText().toString());
        userMap.put("phone", userPhoneEditText.getText().toString());
        ref.child(Prevalent.currentOnlineUsers.getPhone()).updateChildren(userMap);


        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
        Toast.makeText(SettingsActivity.this,"Profile Updated Succesfully",Toast.LENGTH_SHORT).show();
        finish();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            profileImageView.setImageURI(imageUri);
        }
        else
        {
            Toast.makeText(this, "Error, Try Again.", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
            finish();
        }
    }




    private void userInfoSaved()
    {
        if(TextUtils.isEmpty(fullNameEditText.getText().toString()))
        {
            Toast.makeText(SettingsActivity.this,"Name is Mandatory",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(addressEditText.getText().toString()))
        {
            Toast.makeText(SettingsActivity.this,"Name is Mandatory",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(userPhoneEditText.getText().toString()))
        {
            Toast.makeText(SettingsActivity.this,"Name is Mandatory",Toast.LENGTH_SHORT).show();
        }
        else if(checker.equals("clicked"))
        {
            uploadImage();
        }

    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Upload Profile");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if(imageUri!=null)
        {
            final StorageReference fileRef = storageProfilePictureRef
                    .child(Prevalent.currentOnlineUsers.getPhone()+ ".jpg");

            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception
                {
                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful())
                            {
                                Uri downloadUri = task.getResult();
                                myUrl = downloadUri.toString();

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

                                HashMap<String, Object> userMap = new HashMap<>();
                                userMap.put("name", fullNameEditText.getText().toString());
                                userMap.put("address", addressEditText.getText().toString());
                                userMap.put("phone", userPhoneEditText.getText().toString());
                                userMap.put("image", myUrl);

                                ref.child(Prevalent.currentOnlineUsers.getPhone()).updateChildren(userMap);

                                progressDialog.dismiss();

                                startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                                Toast.makeText(SettingsActivity.this,"Profile Updated Succesfully",Toast.LENGTH_SHORT).show();
                                finish();

                            }
                            else
                            {
                                progressDialog.dismiss();
                                Toast.makeText(SettingsActivity.this,"Error",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        else
        {
            Toast.makeText(SettingsActivity.this,"Image is not Selected",Toast.LENGTH_SHORT).show();
        }
    }

    private void userInfoDisplay(final CircleImageView profileImageView, final EditText fullNameEditText, final EditText userPhoneEditText, final EditText addressEditText)
    {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUsers.getPhone());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.child("image").exists())
                    {
                        String image = dataSnapshot.child("image").getValue().toString();
                        String name = dataSnapshot.child("name").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(profileImageView);
                        fullNameEditText.setText(name);
                        userPhoneEditText.setText(phone);
                        addressEditText.setText(address);


                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
