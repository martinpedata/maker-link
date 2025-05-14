package com.example.makerlink.threads.post;


//TODO: ADD COMMENTS AND POINTS
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.makerlink.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class CreateThreadActivity extends AppCompatActivity {
    private SharedPreferences sharedPref;

    private String weblink;
    private CardView thumbnail;
    private ImageView thumbnailImage;
    private ImageView placeholderThumbnail;
    private TextView clickMe;
    private String nameThread;
    private String domain;
    private int domainID;
    private int authorID;
    private Bitmap bitmap;
    private String base64;
    private RequestQueue requestQueue;


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
        thumbnailImage = findViewById(R.id.thumbnailImage);
        placeholderThumbnail = findViewById(R.id.placeholderThumbnail);
        clickMe = findViewById(R.id.clickMeThumbnail);
        thumbnail = findViewById(R.id.threadThumbnail);

        sharedPref = getSharedPreferences("myPref", MODE_PRIVATE);

        authorID = sharedPref.getInt("user_ID", -1);
        domainID = -1;

    }

    ///When thumbnail card is clicked and an image chosen, the following 3 methods are executed.
    public void onBtnPickClicked(View caller)
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);

        //this line will start the new activity and will automatically run the callback method below when the user has picked an image
        startActivityForResult(Intent.createChooser(intent, "Select Image"), 111);
    }

    /// Converts chosen image into a bitmap
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 111 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();

            try {
                //getting image from gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Rescale the bitmap to 300px wide (avoid storing large images!)
                bitmap = getResizedBitmap( bitmap, 450 );

                //Setting background image of thumbnail
                thumbnailImage.setImageBitmap(bitmap);
                clickMe.setVisibility(View.INVISIBLE);
                placeholderThumbnail.setVisibility(View.INVISIBLE);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /// Helper method
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scale = ((float) newWidth) / width;

        // We create a matrix to transform the image
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // Create the new bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
    public void createThread(View v) {
        /// Retrieve content of Edit Texts
        EditText nameInput = findViewById(R.id.editTextThreadName);
        EditText domainInput = findViewById(R.id.editTextDomain);
        EditText weblinkInput = findViewById(R.id.weblinkText);

        nameThread = nameInput.getText().toString();
        domain = domainInput.getText().toString();
        weblink = weblinkInput.getText().toString();

        /// Convert bitmap into base64.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        base64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        /// Retrieve domainID
        retrieveDomainID("https://studev.groept.be/api/a24pt215/RetrieveDomainID/" + domain);
    }

    public void retrieveDomainID(String requestURL) {
        requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET,requestURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject o = response.getJSONObject(0);
                            domainID = o.getInt("id");

                            /// Now that all needed data is retrieved, do post request.
                            postThread("https://studev.groept.be/api/a24pt215/InsertNewThread");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CreateThreadActivity.this, "Network error! Please try again.", Toast.LENGTH_SHORT).show();
                        Log.e("ErrorWithLudo", error.getLocalizedMessage());
                    }
                }
        );
        requestQueue.add(submitRequest);
    }

    public void postThread(String requestURL) {
        //Start an animating progress widget
        ProgressDialog progressDialog = new ProgressDialog(CreateThreadActivity.this);
        progressDialog.setMessage("Uploading, please wait...");
        progressDialog.show();

        //Execute the Volley call. Note that we are not appending the image string to the URL, that happens further below
        StringRequest submitRequest = new StringRequest (Request.Method.POST, requestURL,  new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Turn the progress widget off
                progressDialog.dismiss();
                Toast.makeText(CreateThreadActivity.this, "New Thread Posted !", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CreateThreadActivity.this, "Failed to post new thread ", Toast.LENGTH_LONG).show();
            }
        }) { //NOTE THIS PART: here we are passing the parameter to the webservice, NOT in the URL!
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", nameThread);
                params.put("authorid", Integer.toString(authorID));
                params.put("domainid", Integer.toString(domainID));
                params.put("imagestring", base64);
                params.put("document", weblink);
                return params;
            }
        };

        requestQueue.add(submitRequest);
    }
}