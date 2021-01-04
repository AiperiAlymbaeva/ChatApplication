package android.aiperi.demochatapplication.Chat;

import android.aiperi.demochatapplication.Interfaces.OnItemClickListener;
import android.aiperi.demochatapplication.Models.Chat;
import android.aiperi.demochatapplication.Models.User;
import android.aiperi.demochatapplication.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {

    private List<Chat> list;
    private LayoutInflater inflater;

    private OnItemClickListener onItemClickListener;

    public ChatsAdapter(Context context, List<Chat> list) {
        inflater = LayoutInflater.from(context);
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name_tv;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name_tv = itemView.findViewById(R.id.name_tv);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(getAdapterPosition());
                }
            });


        }


        public void bind(Chat chat) {
            FirebaseFirestore.getInstance().collection("chats")
                    .document(chat.getId())
                    .collection("messages")
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot snapshots) {
                }
            });

            FirebaseFirestore.getInstance().collection("users")
                    .document(chat.getUserIds().get(0))
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User user = documentSnapshot.toObject(User.class);
                            name_tv.setText(user.getName());

                        }
                    });
        }
    }
}


