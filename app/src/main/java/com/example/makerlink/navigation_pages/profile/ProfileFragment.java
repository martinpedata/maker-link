package com.example.makerlink.navigation_pages.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.makerlink.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {
    private CardView changeuserinfo;
    private CardView addtool;
    private CardView changelanguage;
    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        changeuserinfo = binding.changeinfouser;
        addtool = binding.addtool;
        changelanguage = binding.chooselanguage;
        changeuserinfo.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), Change_info_of_user.class);
            startActivity(i);
        });
        addtool.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), AddTool.class);
            startActivity(i);
        });
        changelanguage.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), ChangeLanguage.class);
            startActivity(i);
        });
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}