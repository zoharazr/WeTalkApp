package com.example.wetalk;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.transition.Fade;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Button updateProfileBtn;
    private EditText mUserName, mUserStatus;
    private TextView mUserPhoneNumber;
    private CircleImageView mUserProfileImage;
    private ImageView mUserProfileImage2;

    private User user;
    private String currentUserId;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getWindow().setEnterTransition(null);
        getWindow().setExitTransition(null);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();

        mUserProfileImage = findViewById(R.id.profile_image);
        mUserProfileImage2 = findViewById(R.id.profile_image2);
        mUserName = findViewById(R.id.set_user_name);
        mUserStatus = findViewById(R.id.set_user_status);
        mUserPhoneNumber = findViewById(R.id.profile_phone_number);

        user = new User();
        retrieveUserInfo();

        updateProfileBtn = findViewById(R.id.update_profile_btn);

        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mToolbar.setNavigationOnClickListener(v -> sendUserToMainActivity());

        updateProfileBtn.setOnClickListener(v -> updateSettings());

        Fade fade = new Fade();
        View decor = getWindow().getDecorView();
        fade.excludeTarget(decor.findViewById(R.id.main_page_toolbar), true);
        fade.excludeTarget(decor.findViewById(R.id.AppBarLayout), true);
        fade.excludeTarget(decor.findViewById(R.id.shared_toolbar), true);
        fade.excludeTarget(decor.findViewById(R.id.main_tabs),true);
        fade.excludeTarget(android.R.id.statusBarBackground,true);
        fade.excludeTarget(android.R.id.navigationBarBackground,true);

        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);

        mUserProfileImage.setOnClickListener(v -> {
            Intent profileIntent = new Intent(SettingsActivity.this, ProfileImageActivity.class);
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(SettingsActivity.this, mUserProfileImage, ViewCompat.getTransitionName(mUserProfileImage));
            mUserProfileImage.setVisibility(View.INVISIBLE);
            mUserProfileImage2.setVisibility(View.VISIBLE);
            startActivity(profileIntent, optionsCompat.toBundle());
            mUserProfileImage.setVisibility(View.VISIBLE);
            mUserProfileImage2.setVisibility(View.INVISIBLE);
        });
    }

    private void retrieveUserInfo() {
        rootRef.child("Users").child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.hasChild("image")) {
                                user.setImage(dataSnapshot.child("image").getValue().toString());
                                Glide.with(getApplicationContext()).asBitmap().load(user.getImage()).into(new CustomTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        mUserProfileImage.setImageBitmap(resource);
                                        mUserProfileImage2.setImageBitmap(resource);
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                        Glide.with(getApplicationContext()).load(placeholder).into(mUserProfileImage);
                                        Glide.with(getApplicationContext()).load(placeholder).into(mUserProfileImage2);
                                    }
                                });
                            }

                            if (dataSnapshot.hasChild("name") && dataSnapshot.hasChild("status")) {
                                user.setName(dataSnapshot.child("name").getValue().toString());
                                user.setStatus(dataSnapshot.child("status").getValue().toString());

                                mUserStatus.setText(user.getStatus());
                                mUserName.setText(user.getName());
                            }

                            if (dataSnapshot.hasChild("phone")) {
                                user.setPhone(dataSnapshot.child("phone").getValue().toString());

                                mUserPhoneNumber.setVisibility(View.VISIBLE);
                                mUserPhoneNumber.setText(user.getPhone());
                            }

                        }
                        else
                            Toast.makeText(SettingsActivity.this, "Please set and update profile information...", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
    }

    private void updateSettings() {
        String userName = mUserName.getText().toString();
        String status = mUserStatus.getText().toString();

        if ((userName.isEmpty()) || (userName.equals(""))) {
            Toast.makeText(this, "User name is missing...", Toast.LENGTH_SHORT).show();
        }
        else if ((status.isEmpty()) || (status.equals(""))) {
            Toast.makeText(this, "Status is missing...", Toast.LENGTH_SHORT).show();
        }
        else {
            HashMap<String, Object> profileMap = new HashMap<>();
            profileMap.put("name", userName);
            profileMap.put("status", status);
            rootRef.child("Users").child(currentUserId).updateChildren(profileMap)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(SettingsActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                            if (getIntent().getBooleanExtra("FLAG", false))
                                sendUserToMainActivity();
                        }
                        else {
                            String message = task.getException().toString();
                            Toast.makeText(SettingsActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        if (getIntent().getBooleanExtra("FLAG", false))
            overridePendingTransition(R.anim.slide_up, R.anim.slide_up);
        else
            overridePendingTransition(R.anim.slide_down, R.anim.slide_down);
        finish();
    }
}
