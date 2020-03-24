package com.protal.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.firestore.auth.User;
import com.protal.R;
import com.protal.fragments.PersonalBoardsFragment;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.HashMap;
import java.util.Map;

public class MainHomePageActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Creating a dialog to enter Dog information
                LayoutInflater li = LayoutInflater.from(MainHomePageActivity.this);
                View dialogView = li.inflate(R.layout.dialog_get_board_info, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainHomePageActivity.this);
                alertDialogBuilder.setView(dialogView);
                // Get dialog views objects
                final EditText etAddBoardDialogName = (EditText) dialogView.findViewById(R.id.etAddBoardDialogName);
                final Spinner spBoardType = (Spinner) dialogView.findViewById(R.id.spBoardType);

                String[] arraySpinner = {"Personal", "Team"};
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainHomePageActivity.this,
                        android.R.layout.simple_list_item_1, arraySpinner);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spBoardType.setAdapter(adapter);


                // Add functionality
                alertDialogBuilder
                        .setCancelable(true)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Get user input and set it to result
                                if(!etAddBoardDialogName.getText().toString().trim().isEmpty())
                                    insertBoard(etAddBoardDialogName.getText().toString(),
                                            spBoardType.getSelectedItem().toString());

                            }
                        }).create()
                        .show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_personal, R.id.nav_team)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        UserInfo user = FirebaseAuth.getInstance().getCurrentUser();
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.tvNavBarDisplayName)).setText(user.getDisplayName());
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.tvNavBarEMail)).setText(user.getEmail());
    }



    private void insertBoard(final String name, String type) {

        Map<String, Object> boardsMembers = new HashMap<>();
        boardsMembers.put(FirebaseAuth.getInstance().getCurrentUser().getUid(), null);
        Map<String, Object> board = new HashMap<>();
        board.put("Name", name);
        board.put("Type", type);
        board.put("Members", boardsMembers);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        WriteBatch batch = db.batch();

        final DocumentReference boardFieldsRef = db.collection("Boards").document();
        batch.set(boardFieldsRef, board);

        DocumentReference todoRef = db.collection("Boards").document(
                boardFieldsRef.getId()).collection("Lists").document("To-Do");
        batch.set(todoRef, new HashMap<>());

        DocumentReference doingRef = db.collection("Boards").document(
                boardFieldsRef.getId()).collection("Lists").document("Doing");
        batch.set(doingRef, new HashMap<>());

        DocumentReference doneRef = db.collection("Boards").document(
                boardFieldsRef.getId()).collection("Lists").document("Done");
        batch.set(doneRef, new HashMap<>());

        DocumentReference userRef = db.collection("Users").document(FirebaseAuth
                .getInstance().getCurrentUser().getUid()).collection("Boards")
                .document(boardFieldsRef.getId());
        batch.set(userRef, new HashMap<String, Object>(){{put("Archived", false);}});

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainHomePageActivity.this, "Success", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_home_page, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        // customize navigation bar

        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void signout(MenuItem item) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainHomePageActivity.this, HomePageActivity.class));
    }
}
