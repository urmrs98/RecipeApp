package com.velasco.recipeapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.velasco.recipeapp.Pojo.User;
import com.velasco.recipeapp.CRUD_Recipe.DetailsFragment;
import com.velasco.recipeapp.LoginAndRegister.SharedPrefManager;
import com.velasco.recipeapp.WebServices.Singleton.RequestHandler;
import com.velasco.recipeapp.WebServices.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddRecipeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddRecipeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddRecipeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddRecipeFragment newInstance(String param1, String param2) {
        AddRecipeFragment fragment = new AddRecipeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    /********************** START OF CODE *********************/

    // views of fragment
    View view;
    private Button cancelBtn;
    private Button doneBtn;
    private EditText recipeNameEt, descriptionEt;

    // spinner
    private Spinner spinner;
    private String[] categories = {
            "Starters",
            "Main Courses",
            "Desserts"
    };
    public ArrayList<String> spinnerList = new ArrayList<>(Arrays.asList(categories));

    // get texts from views
    private String recipeNameTxt, descriptionTxt;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_recipe, container, false);

        //getting the current user
        User user = SharedPrefManager.getInstance(getContext()).getUser();

        // spinner
        spinner = view.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, spinnerList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // views
        cancelBtn = view.findViewById(R.id.btn_cancelRecipe);
        recipeNameEt = view.findViewById(R.id.et_recipeName);
        descriptionEt = view.findViewById(R.id.et_recipeDescription);
        doneBtn = view.findViewById(R.id.btn_doneRecipe);

        // cancel
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CategoriesFragment categoriesFragment = new CategoriesFragment();
                getParentFragmentManager().beginTransaction().replace(R.id.addFrag, categoriesFragment).commit();
            }
        });

        // done btn listener - send POST request ( insert to database recipe )
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get texts
                recipeNameTxt = recipeNameEt.getText().toString();
                descriptionTxt = descriptionEt.getText().toString();
                int category = spinner.getSelectedItemPosition() + 1;

                // FIXME: check if it is not empty - create a new recipe
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_ADD_RECIPE, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // get json response
                            JSONObject jsonObject = new JSONObject(response);
                            //Toast.makeText(AddActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            if (jsonObject.getString("success").equals("true")) {
                                // get the last inserted id recipe and create a bundle to send the id to the detailsFragment
                                int recipe_id = Integer.parseInt(jsonObject.getString("inserted_id"));
                                Bundle bundle = new Bundle();
                                bundle.putInt("recipeID", recipe_id);

                                DetailsFragment detailsFragment = new DetailsFragment();
                                detailsFragment.setArguments(bundle);
                                getParentFragmentManager().beginTransaction().replace(R.id.addFrag, detailsFragment).addToBackStack(null).commit();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(AddActivity.this, "Failed to Add Data",Toast.LENGTH_SHORT).show();
                    }
                }) {
                    protected Map<String, String> getParams() throws AuthFailureError {
                        // POST parameters
                        Map<String, String> params = new HashMap<>();
                        params.put("name", recipeNameTxt);
                        params.put("description", descriptionTxt);
                        params.put("category", Integer.toString(category));
                        params.put("userid", Integer.toString(user.getId()));
                        return params;
                    }
                };
                RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);
            }
        });
        return view;
    }
}