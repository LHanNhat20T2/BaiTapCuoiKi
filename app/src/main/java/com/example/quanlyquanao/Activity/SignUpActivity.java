package com.example.quanlyquanao.Activity;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.quanlyquanao.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {
    Activity activity = SignUpActivity.this;
    TextInputEditText userNameET;
    EditText passET, confirmET;
    Button signUpBT, logInBT;
    String userString, passString, confirmString;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        AnhXa();
        mAuth = FirebaseAuth.getInstance();
        signUpBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.postDelayed(runnable,3000);
            }
        });
        logInBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConvertFromSignUpToLogIn();
            }
        });
    }
    public void AnhXa(){
        userNameET = findViewById(R.id.usernameSU);
        passET = findViewById(R.id.passwordSU);
        confirmET = findViewById(R.id.confirmPasswordSU);
        signUpBT = findViewById(R.id.buttonSignUp);
        logInBT = findViewById(R.id.buttonLogin);
    }
    public void GetData(){
        userString = userNameET.getText().toString();
        passString = passET.getText().toString();
        confirmString = confirmET.getText().toString();
    }
    public void ConvertFromSignUpToLogIn(){
        Intent intent = new Intent(activity, LogInActivity.class);
        startActivity(intent);
    }
    public void SignUp(){
        GetData();
        if(userString.isEmpty()){
            userNameET.setError("Trống");
            userNameET.requestFocus();
        }
        else
            if(passString.isEmpty()){
                passET.setError("Trống");
                passET.requestFocus();
            }
            else
                if(passString.equals(confirmString))
                    mAuth.createUserWithEmailAndPassword(userString, passString).addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(activity, "SignUp Successful", Toast.LENGTH_LONG).show();
                                ConvertFromSignUpToLogIn();
                            }
                            else{
                                System.out.println(task.getException());
                                Toast.makeText(activity, "Đăng ký không thành công", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                else{
                    confirmET.setError("Mật khẩu không trùng khớp");
                    confirmET.requestFocus();
                }
    }
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            SignUp();
        }
    };
}