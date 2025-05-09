package com.example.makerlink.navigation_pages.exchange;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.example.makerlink.R;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> userList;


    public static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textName, textAddress, textRent;
        Button goButton;

        public UserViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textName = itemView.findViewById(R.id.textName);
            textAddress = itemView.findViewById(R.id.textAddress);
            textRent = itemView.findViewById(R.id.textRent);
            goButton = itemView.findViewById(R.id.button);
        }
    }

    public UserAdapter(List<User> users) {
        this.userList = users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.textName.setText(user.getName());
        holder.textAddress.setText(user.getAddress());
        holder.textRent.setText("Rent: " + user.getRent() + "€/hour");
        holder.imageView.setImageResource(R.drawable.ic_launcher_foreground);
        holder.goButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), InfoPage.class);
                intent.putExtra("name_of_user", user.getName());
                intent.putExtra("address_of_user", user.getAddress());
                intent.putExtra("rent_of_user", user.getRent());
                intent.putExtra("tool_of_user", user.getTool());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void updateList(List<User> newList) {
        userList = newList;
        notifyDataSetChanged();
    }
}
