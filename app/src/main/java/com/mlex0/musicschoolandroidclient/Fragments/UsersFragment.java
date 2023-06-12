package com.mlex0.musicschoolandroidclient.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mlex0.musicschoolandroidclient.Adapter.UserAdapter;
import com.mlex0.musicschoolandroidclient.Classes.Constants;
import com.mlex0.musicschoolandroidclient.Model.Chatlist;
import com.mlex0.musicschoolandroidclient.Model.User;
import com.mlex0.musicschoolandroidclient.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsersFragment extends Fragment {
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;

    EditText search_users;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_users,container,false);

        recyclerView=view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUsers=new ArrayList<>();

        readUsers();

        search_users=view.findViewById(R.id.search_users);
        search_users.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //search_users(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }


    private void readUsers() {


        String url = Constants.ApiUrl + "user/";

        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                mUsers.clear();
                for(int i = 0; i < jsonArray.length(); i++){

                    JSONObject OneResponse = new JSONObject(jsonArray.get(i).toString());
                    User user = new User();
                    user.ID = OneResponse.get("ID").toString();
                    user.Email = OneResponse.get("Email").toString();
                    user.Password = OneResponse.get("Password").toString();
                    user.IDRole = OneResponse.get("IDRole").toString();
                    user.UserImage = OneResponse.get("UserImagePath").toString();

                    assert user != null;
                    assert Constants.UserId != null;

                    if (!user.ID.equals(Constants.UserId)) {
                        mUsers.add(user);
                    }
                }
                userAdapter=new UserAdapter(getContext(),mUsers,false);
                recyclerView.setAdapter(userAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // method to handle errors.
            Toast.makeText(getContext(), "Отсутствует подключение к серверу", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>(super.getHeaders());
                params.put("Authorization","Bearer " + Constants.UserToken);
                return params;
            }
        };
        queue.add(request);
    }
}
