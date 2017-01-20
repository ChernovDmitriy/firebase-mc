package ru.dev2dev.firebasetest;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ru.dev2dev.firebasetest.models.User;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText loginEt, passEt;
    private Button signInBtn, signUpBtn;

    private DatabaseReference database;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEt = (EditText) findViewById(R.id.login_et);
        passEt = (EditText) findViewById(R.id.pass_et);
        signInBtn = (Button) findViewById(R.id.sign_in_btn);
        signUpBtn = (Button) findViewById(R.id.sign_up_btn);
        signInBtn.setOnClickListener(this);
        signUpBtn.setOnClickListener(this);

        database = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser()!=null) {
            onAuthSuccess();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.sign_in_btn:
                signIn();
                break;
            case R.id.sign_up_btn:
                signUp();
                break;
        }
    }

    private void signIn() {
        if (!formIsValid()) {
            return;
        }
        showProgressDialog();


        String email = loginEt.getText().toString();
        String password = passEt.getText().toString();

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgressDialog();
                        Log.d(TAG, "onComplete: isSuccessful "+task.isSuccessful());
                        if (task.isSuccessful()) {
                            onAuthSuccess();
                        } else {
                            showToast("Authorization was failed");
                        }
                    }
                }
        );
    }

    private void signUp() {
        if (!formIsValid()) {
            return;
        }
        showProgressDialog();

        String email = loginEt.getText().toString();
        String password = passEt.getText().toString();

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgressDialog();
                        Log.d(TAG, "onComplete: isSuccessful "+task.isSuccessful());
                        if (task.isSuccessful()) {
                            writeNewUser(firebaseAuth.getCurrentUser());
                            onAuthSuccess();
                        } else {
                            showToast("Registration was failed");
                        }
                    }
                });
    }

    private boolean formIsValid() {
        return (loginEt.length()>4 && passEt.length()>4);
    }

    private void onAuthSuccess() {
        startActivity(new Intent(this, JokeListActivity.class));
        finish();
    }

    private void writeNewUser(FirebaseUser firebaseUser) {
        String email = firebaseUser.getEmail();
        User user = new User(usernameFromEmail(email), email);

        database.child("users").child(firebaseUser.getUid()).setValue(user);
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }
}
