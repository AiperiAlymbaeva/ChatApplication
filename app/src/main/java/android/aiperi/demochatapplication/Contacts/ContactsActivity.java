package android.aiperi.demochatapplication.Contacts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.aiperi.demochatapplication.Chat.ChatActivity;
import android.aiperi.demochatapplication.Interfaces.OnItemClickListener;
import android.aiperi.demochatapplication.Models.User;
import android.aiperi.demochatapplication.R;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ContactsAdapter adapter;
    private List<User> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        recyclerView = findViewById(R.id.recyclerView);
        setTitle(getString(R.string.contacts));
        initList();
        getContacts();
    }
    private void initList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new ContactsAdapter(this, list);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(ContactsActivity.this, ChatActivity.class);
                intent.putExtra("user", list.get(position));
                startActivity(intent);
            }
        });
    }
    private void getContacts() {
        FirebaseFirestore.getInstance().collection("users")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot snapshots) {
                        for (DocumentSnapshot snapshot:snapshots) {
                            User user = snapshot.toObject(User.class);
                            if (user != null) {
                                user.setId(snapshot.getId());
                            }
                            list.add(user);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }


}