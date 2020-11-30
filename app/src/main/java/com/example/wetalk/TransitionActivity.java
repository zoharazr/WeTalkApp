package com.example.wetalk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Fade;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TransitionActivity extends AppCompatActivity {

    private static int WELCOME_TIMEOUT = 1000;
    private final static String USERS = "Users";
    private final static String NAME = "name";
    private final static String STATUS = "status";
    private static final String MyPREFERENCES = "MyPrefs";
    private static final String Login_State = "loginState";
    private static final String Profile_State = "profileState";


    private SharedPreferences mSharedPreferences;

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    private boolean mLoginState;
    private boolean mProfileState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition);

        mSharedPreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        getSharedPreferences();

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();

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
    }

    private void getSharedPreferences() {
        mLoginState = mSharedPreferences.getBoolean(Login_State, false);
        mProfileState = mSharedPreferences.getBoolean(Profile_State, false);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mLoginState)
            sendUserToLoginActivity();
        else if (mProfileState)
            sendUserToProfileActivity();
        else if(currentUser != null)
            checkIfUserExist();
        else
            sendUserToHelloActivity();
    }

    private void checkIfUserExist() {
        String currentUserId = currentUser.getUid();

        rootRef.child(USERS).child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.hasChild(NAME) && dataSnapshot.hasChild(STATUS))
                                sendUserToMainActivity();
                            else
                                sendUserToHelloActivity();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
    }

    private void sendUserToProfileActivity() {
        new Handler().postDelayed(() -> {
            Intent helloIntent = new Intent(TransitionActivity.this, ProfileActivity.class);
            helloIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(helloIntent);
            overridePendingTransition(new Fade().getMode(), new Fade().getMode());
            finish();
        },WELCOME_TIMEOUT);
    }

    private void sendUserToHelloActivity() {
        new Handler().postDelayed(() -> {
            Intent helloIntent = new Intent(TransitionActivity.this, HelloActivity.class);
            helloIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(helloIntent);
            overridePendingTransition(new Fade().getMode(), new Fade().getMode());
            finish();
        },WELCOME_TIMEOUT);
    }

    private void sendUserToLoginActivity() {
        new Handler().postDelayed(() -> {
            Intent loginIntent = new Intent(TransitionActivity.this, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            overridePendingTransition(new Fade().getMode(), new Fade().getMode());
            finish();
        },WELCOME_TIMEOUT);
    }

    private void sendUserToMainActivity() {
        new Handler().postDelayed(() -> {
            Intent mainIntent = new Intent(TransitionActivity.this, MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
            overridePendingTransition(new Fade().getMode(), new Fade().getMode());
            finish();
        },WELCOME_TIMEOUT);
    }
}
