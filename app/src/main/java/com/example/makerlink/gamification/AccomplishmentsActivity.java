package com.example.makerlink.gamification;

import android.animation.ValueAnimator;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.makerlink.R;

public class AccomplishmentsActivity extends AppCompatActivity {

    private TextView pointsTextView;
    private int actualPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_accomplishments);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        pointsTextView = findViewById(R.id.pointsNum);
        TextView level1text = findViewById(R.id.level1text);
        TextView level2text = findViewById(R.id.level2text);
        TextView level3text = findViewById(R.id.level3text);
        TextView level4text = findViewById(R.id.level4text);

        CardView level1line = findViewById(R.id.level1line);
        CardView level2line = findViewById(R.id.level2line);
        CardView level3line = findViewById(R.id.level3line);
        CardView level4line = findViewById(R.id.level4line);

        ImageView level1image = findViewById(R.id.level1image);
        ImageView level2image = findViewById(R.id.level2image);
        ImageView level3image = findViewById(R.id.level3image);
        ImageView level4image = findViewById(R.id.level4image);

        SharedPreferences sharedPreferences = getSharedPreferences("myPref", MODE_PRIVATE);
        actualPoints = sharedPreferences.getInt("points", -1);
        animatePoints(0, actualPoints, 1500); // animate from 0 to 1000 in 2000 ms

        if (actualPoints < 3000) {
            level4image.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
            level4line.setBackgroundColor(Color.GRAY);
            level4text.setTextColor(Color.GRAY);
            if (actualPoints < 2000) {
                level3image.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
                level3line.setBackgroundColor(Color.GRAY);
                level3text.setTextColor(Color.GRAY);
                if (actualPoints < 1000) {
                    level2image.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
                    level2line.setBackgroundColor(Color.GRAY);
                    level2text.setTextColor(Color.GRAY);
                    if (actualPoints < 500) {
                        level1image.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
                        level1line.setBackgroundColor(Color.GRAY);
                        level1text.setTextColor(Color.GRAY);
                    }
                }
            }
        }
    }



    private void animatePoints(int start, int end, long duration) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.setDuration(duration); // Duration in milliseconds
        animator.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            pointsTextView.setText(String.valueOf(animatedValue));
        });
        animator.start();
    }
}