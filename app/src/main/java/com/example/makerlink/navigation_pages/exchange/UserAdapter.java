package com.example.makerlink.navigation_pages.exchange;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import com.example.makerlink.R;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> userList;


    public static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textName, textAddress, textRent, texttooltype;
        Button goButton;

        public UserViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textName = itemView.findViewById(R.id.textName);
            textAddress = itemView.findViewById(R.id.textAddress);
            textRent = itemView.findViewById(R.id.textRent);
            goButton = itemView.findViewById(R.id.button);
            texttooltype = itemView.findViewById(R.id.texttooltype);
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
        holder.textRent.setText(user.getRent() + "€/hour");
        holder.imageView.setImageBitmap(user.getUserImage());
        holder.texttooltype.setText(user.getTool());
        holder.goButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), InfoPage.class);
                intent.putExtra("name_of_user", user.getName());
                intent.putExtra("address_of_user", user.getAddress());
                intent.putExtra("rent_of_user", user.getRent());
                intent.putExtra("tool_of_user", user.getTool());
                intent.putExtra("description_of_tool", user.getDescription());
                intent.putExtra("start_of_user", user.getStartday());
                intent.putExtra("end_of_user", user.getEndday());
                Bitmap bitmap = user.getUserImage();
                String imagePath = saveImageToInternalStorage(v.getContext(), bitmap);
                intent.putExtra("imagePath", imagePath);
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
    private String saveImageToInternalStorage(Context context, Bitmap bitmap) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("images", Context.MODE_PRIVATE);
        File imagePath = new File(directory, "profileImage_" + System.currentTimeMillis() + ".jpg"); // unique filename

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imagePath.getAbsolutePath();
    }
}
