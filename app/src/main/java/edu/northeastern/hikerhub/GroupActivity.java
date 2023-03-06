package edu.northeastern.hikerhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import edu.northeastern.hikerhub.stickerService.models.User;
import edu.northeastern.hikerhub.user.UserActivity;
import edu.northeastern.hikerhub.utils.ViewUtils;

public class GroupActivity extends AppCompatActivity {


    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String USER_TABLE = "Users";
    private String deviceFcmToken = "";
    public static DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(
                new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        // Get new FCM registration token
                        deviceFcmToken = task.getResult();
                        Log.i(TAG, "Fetched token is: " + deviceFcmToken);
                    }
                });

        EditText etName = findViewById(R.id.inputUserName);
        Button btnRegister = findViewById(R.id.btnRegister);
        etName.addTextChangedListener(new ViewUtils.JumpTextWatcher(etName,btnRegister));
        btnRegister.setOnClickListener(view -> {
            login(etName.getText().toString());
        });

    }

    private void login(String username) {
        User user = new User(username, deviceFcmToken);
        Task<Void> loginTask = mDatabase.child(USER_TABLE).child(user.username).setValue(user);
        loginTask.addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.i(TAG,
                        String.format("Login user %s, success: %s ", user.username, loginTask.isSuccessful()));
                onLoginSuccess(username);
            }
        });
    }

    private void onLoginSuccess(String username) {
        Toast.makeText(GroupActivity.this, "Login successfully!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getBaseContext(), UserActivity.class);
        intent.putExtra("login_username", username);
        startActivity(intent);
    }
}
