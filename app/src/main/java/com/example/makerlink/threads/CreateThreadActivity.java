package com.example.makerlink.threads;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.makerlink.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CreateThreadActivity extends AppCompatActivity {

    private FloatingActionButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_thread);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }



    //TODO: THE CODE BELOW ENSURES THE USER CAN ADD THEIR OWN PHOTOS AND VIDEOS FROM THEIR GALLERY.
//    public void onBtnPickClicked(View caller)
//    {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_PICK);
//
//        //this line will start the new activity and will automatically run the callback method below when the user has picked an image
//        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
//    }
//
//    /**
//     * Processes the image picked by the user. For now, the bitmap is simply stored in an attribute.
//     */
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            Uri filePath = data.getData();
//
//            try {
//                //getting image from gallery
//                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
//                //Rescale the bitmap to 400px wide (avoid storing large images!)
//                bitmap = getResizedBitmap( bitmap, 400 );
//
//                //Setting image to ImageView
//                image.setImageBitmap(bitmap);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

}