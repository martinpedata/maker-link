package com.example.makerlink.navigation_pages.home;

import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.makerlink.databinding.FragmentHomeBinding;
import com.example.makerlink.gamification.LeaderBoardActivity;
import com.example.makerlink.playlists.PlaylistRecyclerActivity;
import com.example.makerlink.threads.list.FilteredThreadRecyclerActivity;
import com.example.makerlink.threads.list.ThreadRecyclerActivity;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sharedPref = getActivity().getSharedPreferences("storeFilteredSearch", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        /// To display Threads

        ImageView discoveryImage = binding.DIYimage;
        ImageView mechanicsImage = binding.mechanicsPic;
        ImageView electronicsImage = binding.electronicsPic;
        ImageView cookingImage = binding.cookingPic;
        ImageView carpentryImage = binding.carpentryPic;
        CardView playlistFolder = binding.playlistFolder;
        CardView rankingFolder = binding.leaderboardButton;

        playlistFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt("isFiltered", 5).apply();
                editor.putString("nameDomain", "Your Playlists");
                ProgressDialog progressDialog = new ProgressDialog(getActivity()); // or requireActivity()
                progressDialog.setMessage("Opening page, please wait...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    Intent intent = new Intent(getActivity(), PlaylistRecyclerActivity.class);
                    startActivity(intent);
                    progressDialog.dismiss();
                }, 300);
            }
        });

        rankingFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog = new ProgressDialog(getActivity()); // or requireActivity()
                progressDialog.setMessage("Opening page, please wait...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    Intent i = new Intent(getActivity(), LeaderBoardActivity.class);
                    startActivity(i);
                    progressDialog.dismiss();
                }, 300);
            }
        });

        discoveryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("nameDomain", "See What’s General").apply();
                ProgressDialog progressDialog = new ProgressDialog(getActivity()); // or requireActivity()
                progressDialog.setMessage("Opening page, please wait...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    Intent intent = new Intent(getActivity(), FilteredThreadRecyclerActivity.class);
                    startActivity(intent);
                    progressDialog.dismiss();
                }, 300); // slight delay to let dialog show
            }
        });

        mechanicsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt("isFiltered", 1).apply();
                editor.putString("nameDomain", "Mechanics \uD83D\uDD27").apply();
                ProgressDialog progressDialog = new ProgressDialog(getActivity()); // or requireActivity()
                progressDialog.setMessage("Opening page, please wait...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    Intent intent = new Intent(getActivity(), ThreadRecyclerActivity.class);
                    startActivity(intent);
                    progressDialog.dismiss();
                }, 300);
            }
        });


        electronicsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt("isFiltered", 2).apply();
                editor.putString("nameDomain", "Electronics \uD83E\uDD16").apply();
                ProgressDialog progressDialog = new ProgressDialog(getActivity()); // or requireActivity()
                progressDialog.setMessage("Opening page, please wait...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    Intent intent = new Intent(getActivity(), ThreadRecyclerActivity.class);
                    startActivity(intent);
                    progressDialog.dismiss();
                }, 300);
            }
        });
        cookingImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt("isFiltered", 3).apply();
                editor.putString("nameDomain", "Cooking \uD83C\uDF73").apply();
                ProgressDialog progressDialog = new ProgressDialog(getActivity()); // or requireActivity()
                progressDialog.setMessage("Opening page, please wait...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    Intent intent = new Intent(getActivity(), ThreadRecyclerActivity.class);
                    startActivity(intent);
                    progressDialog.dismiss();
                }, 300);
            }
        });
        carpentryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt("isFiltered", 4).apply();
                editor.putString("nameDomain", "Carpentry \uD83E\uDE9A").apply();
                ProgressDialog progressDialog = new ProgressDialog(getActivity()); // or requireActivity()
                progressDialog.setMessage("Opening page, please wait...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    Intent intent = new Intent(getActivity(), ThreadRecyclerActivity.class);
                    startActivity(intent);
                    progressDialog.dismiss();
                }, 300);
            }
        });

    }

}