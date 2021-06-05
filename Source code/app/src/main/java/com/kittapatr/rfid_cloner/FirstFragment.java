package com.kittapatr.rfid_cloner;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.kittapatr.rfid_cloner.databinding.FragmentFirstBinding;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Editing console text
        TextView textview = binding.textView2;
        Context context = textview.getContext();

        // Clear button click listener
        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textview.setText("");
            }
        });

        // Copy button click listener
        binding.button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) context
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData cData = ClipData.newPlainText("Text", textview.getText());
                cm.setPrimaryClip(cData);
                Toast.makeText(v.getContext(), "Copied!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}