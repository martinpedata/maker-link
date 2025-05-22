package com.example.makerlink.navigation_pages.settings;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.makerlink.access.LoginActivity;
import com.example.makerlink.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {
    private CardView changeuserinfo;
    private CardView changelenderinfo;
    private CardView addtool;
    private CardView changelanguage;
    private FragmentSettingsBinding binding;
    private Button logout;
    private SharedPreferences sharedPref;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        changeuserinfo = binding.changeinfouser;
        changelenderinfo = binding.changeinfolender;
        addtool = binding.addtool;
        changelanguage = binding.chooselanguage;
        logout = binding.logoutButton;
        sharedPref = requireContext().getSharedPreferences("myPref", MODE_PRIVATE);
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
        changelenderinfo.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), Change_info_lender.class);
            startActivity(i);
        });
        logout.setOnClickListener(v -> {
            sharedPref.edit().clear().apply();
            Intent i = new Intent(getContext(), LoginActivity.class);
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