package com.velasco.recipeapp;

import static com.velasco.recipeapp.WebServices.Constants.URL_UPLOAD;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.velasco.recipeapp.LoginAndRegister.LoginActivity;
import com.velasco.recipeapp.LoginAndRegister.SharedPrefManager;
import com.velasco.recipeapp.Pojo.User;
import com.velasco.recipeapp.WebServices.Singleton.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {


    private Bitmap bitmap; // to encode image
    User user;

    // STEP 7: Sundesh me xml
    BottomNavigationView bottomNavigationView;  // menu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //if the user is not logged in
        //starting the login activity
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }


        // get current logged in user
        user = SharedPrefManager.getInstance(this).getUser();

        // STEP 9: Να ξεκινάει από εδώ
        replaceFragment(new CategoriesFragment()); // set which fragment to show up at the start of activity

        // STEP 7.1: sundesh
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // STEP 8: on click navigate
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    replaceFragment(new CategoriesFragment());
                    break;
                case R.id.add:
                    replaceFragment(new AddRecipeFragment());
                    break;
                case R.id.profile:
                    replaceFragment(new ProfileFragment());
                    break;
            }
            return true;
        });
    }

    // STEP 9: Βοηθητική συνάρτηση
    // the main activity is responsible for the switches on fragments
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager(); // manager
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); // transaction
        fragmentTransaction.replace(R.id.frameLayout, fragment); // where the transaction will take place
        fragmentTransaction.commit(); // commit
    }

    // upload image ( profile fragment ) - The activity who's responsible for the fragment should manage this
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri filepath = data.getData();
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        UploadPicture(Integer.toString(8), getStringImage(bitmap));
    }


    // StringRequest to upload the image
    private void UploadPicture(final String id, final String photo) {
        //Log.i("OK", photo);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        //getting the current user
        User user = SharedPrefManager.getInstance(this).getUser();
        Log.i("OK", "8");

        //if everything is fine
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_UPLOAD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        //Log.i("TEST", response.toString());

                        try {

                            // Get json object from response
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            // Log.i("OK", jsonObject.getString("success"));
                            if (success.equals("1")) {
                                Toast.makeText(getApplicationContext()
                                        , "Success!", Toast.LENGTH_SHORT).show();
                                // Log.i("OK", jsonObject.getString("photo_path"));
                                user.setPhoto(jsonObject.getString("photo_path"));
                                // Log.i("OK", user.getPhoto());
                                SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                                // Log.i("TEST", SharedPrefManager.getInstance(getApplicationContext()).getUser().getPhoto());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext()
                                    , "Try again!", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext()
                                , "Try again! " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                // send parameters to POST request
                Map<String, String> params = new HashMap<>();
                params.put("id", Integer.toString(user.getId()));
                params.put("photo", photo);
                //  Log.i("TEST", "NEW: " + photo);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    // encode image
    private String getStringImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] imageByteArray = byteArrayOutputStream.toByteArray();
        String encodedImage = android.util.Base64.encodeToString(imageByteArray, Base64.DEFAULT);
        return encodedImage;
    }
}