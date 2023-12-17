package com.aac.envision;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;

public class UsersManagementActivity extends AppCompatActivity {

    private RecyclerView userRecyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;
    private ArrayList<String> selectedUsers;

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.users_management_activity);

        userRecyclerView = findViewById(R.id.userRecyclerView);
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList, this);
        userRecyclerView.setAdapter(userAdapter);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        selectedUsers = new ArrayList<>();

        fetchUsers();

        // Spinner setup
        Spinner actionSpinner = findViewById(R.id.spinnerAction);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.actions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        actionSpinner.setAdapter(adapter);

        actionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String action = parent.getItemAtPosition(position).toString();
                if (!selectedUsers.isEmpty()) {
                    if (action.equals(getString(R.string.make_user_admin))) {
                        changeRole();
                    } else if (action.equals(getString(R.string.delete_user))) {
                        deleteUser();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing here
            }
        });
    }

    private void toggleSelection(User user) {
        user.setSelected(!user.isSelected());
        if (user.isSelected()) {
            selectedUsers.add(user.getGlobalUserID());
        } else {
            selectedUsers.remove(user.getGlobalUserID());
        }
        userAdapter.notifyDataSetChanged();
    }

    private void fetchUsers() {
        firestore.collection("users")
                .whereEqualTo("role", "User")
                .orderBy("GlobalUserID", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    userList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        User user = document.toObject(User.class);
                        if (user != null) {
                            userList.add(user);
                        }
                    }
                    userAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch users", Toast.LENGTH_SHORT).show();
                });
    }

    private void changeRole() {
        for (String selectedUser : selectedUsers) {
            firestore.collection("users")
                    .document(selectedUser)
                    .update("role", "Admin")
                    .addOnSuccessListener(aVoid -> {
                        // Role changed successfully
                    })
                    .addOnFailureListener(e -> {
                        // Failed to change role
                        Toast.makeText(this, "Failed to change roles", Toast.LENGTH_SHORT).show();
                    });
        }

        Toast.makeText(this, "Roles changed successfully", Toast.LENGTH_SHORT).show();
        selectedUsers.clear();
        fetchUsers(); // Refresh the user list after changing roles
    }

    private void deleteUser() {
        for (String selectedUser : selectedUsers) {
            firestore.collection("users")
                    .document(selectedUser)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        // User deleted successfully
                    })
                    .addOnFailureListener(e -> {
                        // Failed to delete user
                        Toast.makeText(this, "Failed to delete users", Toast.LENGTH_SHORT).show();
                    });
        }

        Toast.makeText(this, "Users deleted successfully", Toast.LENGTH_SHORT).show();
        selectedUsers.clear();
        fetchUsers(); // Refresh the user list after deletion
    }
}