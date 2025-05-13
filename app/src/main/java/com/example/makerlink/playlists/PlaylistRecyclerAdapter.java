package com.example.makerlink.playlists;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.makerlink.R;
import com.example.makerlink.threads.list.ThreadRecyclerActivity;

import java.util.ArrayList;

public class PlaylistRecyclerAdapter extends RecyclerView.Adapter<PlaylistRecyclerAdapter.MyViewHolder> {

    public Context context;
    ArrayList<PlaylistRecyclerModel> playlists = new ArrayList<>();

    public PlaylistRecyclerAdapter(Context context, ArrayList<PlaylistRecyclerModel> playlists) {
        this.context = context;
        this.playlists = playlists;
    }

    /// This is what makes the recycler view visible
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.playlist_item, parent, false);

        return new PlaylistRecyclerAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.namePlaylist.setText(playlists.get(position).getName());
        holder.privacyText.setText(playlists.get(position).getPrivacyString());
        holder.privacyImage.setImageResource(playlists.get(position).imageResourcePrivacy());
        //holder.imageThread.setImageBitmap(playlists.get(position).getBitmapImage());

        holder.playlistItem.setOnClickListener(e->{
            Intent intent = new Intent(context, ThreadRecyclerActivity.class);
            intent.putExtra("playlistName", playlists.get(position).getName());
            intent.putExtra("playlistID", playlists.get(position).getPlaylistID());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public CardView playlistItem;
        public TextView namePlaylist;
        public TextView privacyText;
        public ImageView privacyImage;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            playlistItem = itemView.findViewById(R.id.playlistItem);
            namePlaylist = itemView.findViewById(R.id.namePlaylist);
            privacyText = itemView.findViewById(R.id.privacyText);
            privacyImage = itemView.findViewById(R.id.privacyImage);

        }
    }
}