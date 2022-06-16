package com.example.domestrackv3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.esotericsoftware.kryo.serializers.DefaultSerializers;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterInfo extends AppCompatActivity {
    private EditText firstName;
    private EditText lastName;
    private Button saveToRealtimeDatabase;
    private ImageView profileImage;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private CircleImageView ProfileImage;
    private static final int PICK_IMAGE = 1;
    Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_info);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        saveToRealtimeDatabase = findViewById(R.id.saveToRealtimeDatabase);

        saveToRealtimeDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getFirstName = firstName.getText().toString();
                String getLastName = lastName.getText().toString();

                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("first name", getFirstName);
                hashMap.put("last name", getLastName);

                databaseReference.child("Users")
                        .child(getFirstName)
                        .setValue(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(RegisterInfo.this, "Profile made successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener((new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RegisterInfo.this, "+e.getMessage", Toast.LENGTH_SHORT).show();
                            }
                        }));
                startActivity(new Intent(RegisterInfo.this, MainActivity.class));
            }
        });

        ProfileImage = (CircleImageView) findViewById(R.id.profile_picture);
        ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallery, "select picture"), PICK_IMAGE);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if(requestCode== PICK_IMAGE && resultCode == RESULT_OK) {
                imageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
                    ProfileImage.setImageBitmap(bitmap);
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }
}