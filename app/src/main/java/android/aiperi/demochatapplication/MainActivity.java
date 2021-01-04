package android.aiperi.demochatapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.aiperi.demochatapplication.Chat.ChatActivity;
import android.aiperi.demochatapplication.Chat.ChatsAdapter;
import android.aiperi.demochatapplication.Contacts.ContactsActivity;
import android.aiperi.demochatapplication.Contacts.ContactsAdapter;
import android.aiperi.demochatapplication.Interfaces.OnItemClickListener;
import android.aiperi.demochatapplication.Models.Chat;

import android.aiperi.demochatapplication.Models.User;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatsAdapter adapter;
    private List<Chat> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.chat));
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, PhoneActivity.class));
            finish();
            return;
        }
        recyclerView = findViewById(R.id.recyclerView);
        getChats();

        initList();
    }


    public void onClickContacts(View view) {
        startActivity(new Intent(MainActivity.this, ContactsActivity.class));
        finish();
    }

    private void initList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new ChatsAdapter(this, list);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                intent.putExtra("chat", list.get(position));
                startActivity(intent);
            }
        });
    }


    private void getChats() {
        String myUserId = FirebaseAuth.getInstance().getUid();
        FirebaseFirestore.getInstance().collection("chats")
                .whereArrayContains("userIds", myUserId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                        for (DocumentChange change : snapshots.getDocumentChanges()) {
                            switch (change.getType()) {
                                case ADDED:
                                    Chat chat = change.getDocument().toObject(Chat.class);
                                    chat.setId(change.getDocument().getId());
                                    list.add(chat);
                                    break;

                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}

