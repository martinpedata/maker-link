package com.example.makerlink.navigation_pages.profile;

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

import com.example.makerlink.R;
import com.example.makerlink.navigation_pages.exchange.InfoPage;
import com.example.makerlink.navigation_pages.exchange.User;
import com.example.makerlink.navigation_pages.exchange.UserAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class LenderAdapter extends RecyclerView.Adapter<LenderAdapter.LenderViewHolder>{

    private List<Lender_info> lenderList;
    public static class LenderViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textName, textAddress, textRent;
        Button goButton;

        public LenderViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textName = itemView.findViewById(R.id.textName);
            textAddress = itemView.findViewById(R.id.textAddress);
            textRent = itemView.findViewById(R.id.textRent);
            goButton = itemView.findViewById(R.id.button);
        }
    }

    public LenderAdapter(List<Lender_info> users) {
        this.lenderList = users;
    }

    @NonNull
    @Override
    public LenderAdapter.LenderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_1, parent, false);
        return new LenderAdapter.LenderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LenderAdapter.LenderViewHolder holder, int position) {
        Lender_info user = lenderList.get(position);
        holder.textName.setText(user.getLenderTool());
        holder.textAddress.setText(user.getLenderAddress());
        holder.textRent.setText(user.getLenderRent() + "€/hour");
        holder.imageView.setImageBitmap(user.getLenderImage());
        holder.goButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Lender_change_info_page.class);
                intent.putExtra("name_of_user", user.getLenderName());
                intent.putExtra("address_of_user", user.getLenderAddress());
                intent.putExtra("rent_of_user", user.getLenderRent());
                intent.putExtra("tool_of_user", user.getLenderTool());
                intent.putExtra("description_of_tool", user.getLenderDescription());
                intent.putExtra("start_of_user", user.getLenderStartday());
                intent.putExtra("end_of_user", user.getLenderEndday());
                Bitmap bitmap = user.getLenderImage();
                String imagePath = saveImageToInternalStorage(v.getContext(), bitmap);
                intent.putExtra("imagePath", imagePath);
                intent.putExtra("location_id", user.getLocationID());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lenderList.size();
    }

    public void updateList(List<Lender_info> newList) {
        lenderList = newList;
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
