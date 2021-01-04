package android.aiperi.demochatapplication.Chat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.aiperi.demochatapplication.Contacts.ContactsActivity;
import android.aiperi.demochatapplication.Contacts.ContactsAdapter;
import android.aiperi.demochatapplication.Fcm.MyFirebaseMessagingService;
import android.aiperi.demochatapplication.Interfaces.OnItemClickListener;
import android.aiperi.demochatapplication.Models.Chat;
import android.aiperi.demochatapplication.Models.Message;
import android.aiperi.demochatapplication.Models.User;
import android.aiperi.demochatapplication.R;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Message> list = new ArrayList<>();
    private EditText editText;
    private User user;
    private Chat chat;
    private Message message;
    MyFirebaseMessagingService myFirebaseMessagingService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        editText = findViewById(R.id.inputMessage);
        recyclerView = findViewById(R.id.recyclerView);
        myFirebaseMessagingService = new MyFirebaseMessagingService();
        message = (Message) getIntent().getSerializableExtra("message");
        user = (User) getIntent().getSerializableExtra("user");
        chat = (Chat) getIntent().getSerializableExtra("chat");
        if (chat == null) {
            chat = new Chat();
            ArrayList<String> userIds = new ArrayList<>();
            userIds.add(user.getId());
            userIds.add(FirebaseAuth.getInstance().getUid());
            chat.setUserIds(userIds);
        } else if (user == null) {
            getMessages();
            initList();
            FirebaseFirestore.getInstance().collection("users")
                    .document(chat.getUserIds().get(0))
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot snapshot) {
                            user = snapshot.toObject(User.class);
                            assert user != null;
                            setTitle(user.getName());
                        }
                    });
        }


    }


    private void initList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new MessageAdapter(this, list);
        recyclerView.setAdapter(adapter);

    }

    private void getMessages() {
        FirebaseFirestore.getInstance().collection("chats")
                .document(chat.getId())
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                        for (DocumentChange change : snapshots.getDocumentChanges()) {
                            switch (change.getType()) {
                                case ADDED:
                                    Message message = change.getDocument().toObject(Message.class);
                                    message.setId(change.getDocument().getId());
                                    message.setTimestamp(change.getDocument().getTimestamp("timestamp"));
                                    list.add(message);
                                    adapter.notifyDataSetChanged();
                                    break;
                                case MODIFIED:
                                    break;
                                case REMOVED:
                                    break;
                            }
                        }
//                        adapter.notifyDataSetChanged();
                    }
                });
    }

    public void onClickSend(View view) {
        String text = editText.getText().toString().trim();
        if (chat.getId() != null) {
            sendMessage(text);
        } else {
            createChat(text);
        }
    }

    private void createChat(final String text) {
        Map<String, Object> map = new HashMap<>();
        map.put("userIds", chat.getUserIds());
        FirebaseFirestore.getInstance().collection("chats")
                .add(chat)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        chat.setId(documentReference.getId());
                        sendMessage(text);
                        getMessages();
                        initList();
                    }
                });
    }

    private void sendMessage(String text) {
        Map<String, Object> map = new HashMap<>();
        map.put("text", text);
//        map.put("receiver", user.getId());
        map.put("senderId", FirebaseAuth.getInstance().getUid());
        map.put("timestamp", new Timestamp(new Date()));

        DocumentReference document = FirebaseFirestore.getInstance().collection("chats").document(chat.getId());
        document.update("timestamp", new Timestamp(new Date()));
        document.collection("messages").add(map);

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        editText.setText("");
    }
}