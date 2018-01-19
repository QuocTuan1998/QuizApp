package com.example.admin.quizapp;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.admin.quizapp.model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    MaterialEditText edit_new_user,edit_new_password,edit_new_email; // Sign Up

    MaterialEditText edit_username, edit_password; // Sign In

    Button button_sign_in, button_sign_up;

    FirebaseDatabase database;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFirebase();
        addControls();

        button_sign_in.setOnClickListener(this);
        button_sign_up.setOnClickListener(this);
    }

    private void addControls() {
        edit_username = findViewById(R.id.edit_username);
        edit_password = findViewById(R.id.edit_password);

        button_sign_in = findViewById(R.id.button_sign_in);
        button_sign_up = findViewById(R.id.button_sign_up);
    }

    private void initFirebase() {
        database = FirebaseDatabase.getInstance();
        users = database.getReference("users");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_sign_up :
                showSignUpDialog();
                break;
            case R.id.button_sign_in :
                signin(edit_username.getText().toString(),edit_password.getText().toString());
                break;
        }
    }

    private void signin(final String name, final String pass) {
        Log.d("text","Sigin !!!!!");
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(name).exists()){
                    if ((!name.isEmpty())){
                        Users login = dataSnapshot.child(name).getValue(Users.class);
                        if (login.getPassword().equals(pass)){
                            Toast.makeText(MainActivity.this, "Login Success !", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(MainActivity.this, "Wrong password, please try again", Toast.LENGTH_SHORT).show();
                            edit_password.setText("");
                        }
                    }else {
                        Toast.makeText(MainActivity.this, "Please enter your user name", Toast.LENGTH_SHORT).show();   
                    }
                }else {
                    Toast.makeText(MainActivity.this, "User is not exist !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showSignUpDialog() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("Sign Up");
        alert.setMessage("please fill full information");

        LayoutInflater inflater = this.getLayoutInflater();
        View sign_up_layout = inflater.inflate(R.layout.sign_up_layout,null);

        edit_new_user = sign_up_layout.findViewById(R.id.edit_new_username);
        edit_new_password = sign_up_layout.findViewById(R.id.edit_new_password);
        edit_new_email = sign_up_layout.findViewById(R.id.edit_new_email);

        alert.setView(sign_up_layout);
        alert.setIcon(R.drawable.ic_account_circle_black_24dp);

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final Users user = new Users(edit_new_user.getText().toString(),
                        edit_new_password.getText().toString(),
                        edit_new_email.getText().toString());

                users.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(user.getUserName()).exists()){
                            Toast.makeText(MainActivity.this, "User already exsit !", Toast.LENGTH_SHORT).show();
                        }else {
                            users.child(user.getUserName()).setValue(user);
                            Toast.makeText(MainActivity.this, "User Registration success !", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                dialogInterface.dismiss();
            }
        });
        alert.show();
    }
}
