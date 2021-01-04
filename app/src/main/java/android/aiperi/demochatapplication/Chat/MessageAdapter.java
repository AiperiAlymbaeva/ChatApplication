package android.aiperi.demochatapplication.Chat;

import android.aiperi.demochatapplication.Models.Message;
import android.aiperi.demochatapplication.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<Message> list;
    private LayoutInflater inflater;


    public MessageAdapter(Context context, List<Message> list) {
        inflater = LayoutInflater.from(context);
        this.list = list;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.chat_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private View message_left;
        private View message_right;
        private TextView date_tv;
        private TextView message_left_tv;
        private TextView message_right_tv;
        private TextView message_left_time_tv;
        private TextView message_right_time_tv;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            message_left = itemView.findViewById(R.id.message_left);
            message_right = itemView.findViewById(R.id.message_right);
            date_tv = itemView.findViewById(R.id.date_tv);
            message_left_time_tv = itemView.findViewById(R.id.message_left_time);
            message_right_time_tv = itemView.findViewById(R.id.message_right_time);
            message_left_tv = itemView.findViewById(R.id.message_left_tv);
            message_right_tv = itemView.findViewById(R.id.message_right_tv);

        }


        public void bind(Message message) {
            Date date =  message.getTimestamp().toDate();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm" );
            String time = simpleDateFormat.format(date);

            if (message.getSenderId().equals(FirebaseAuth.getInstance().getUid())) {
                message_right_time_tv.setText(time);
                message_right.setVisibility(View.VISIBLE);
                message_left.setVisibility(View.GONE);
                message_right_tv.setText(message.getText());
            } else {
                message_left_time_tv.setText(time);
                message_left.setVisibility(View.VISIBLE);
                message_right.setVisibility(View.GONE);
                message_left_tv.setText(message.getText());
            }


        }


    }
}
