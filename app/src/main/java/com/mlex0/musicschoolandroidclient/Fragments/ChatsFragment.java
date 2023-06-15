package com.mlex0.musicschoolandroidclient.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import okhttp3.OkHttpClient;

public class ChatsFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;
    DatabaseReference reference;
    private List<Chatlist> userList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_chats, container, false);

        recyclerView=view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        userList=new ArrayList<>();

        reference=FirebaseDatabase
                .getInstance("https://musicschoolandroidclient-default-rtdb.firebaseio.com")
                .getReference("Chatlist")
                .child(Constants.UserId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Chatlist chatlist=snapshot.getValue(Chatlist.class);
                    userList.add(chatlist);
                }
                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void chatList() {
        mUsers=new ArrayList<>();

        String url = Constants.ApiUrl + "user/";

        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for(int i = 0; i < jsonArray.length(); i++){

                    JSONObject OneResponse = new JSONObject(jsonArray.get(i).toString());
                    User user = new User();
                    user.ID = OneResponse.get("ID").toString();
                    user.Email = OneResponse.get("Email").toString();
                    user.Password = OneResponse.get("Password").toString();
                    user.IDRole = OneResponse.get("IDRole").toString();
                    user.UserImage = OneResponse.get("UserImagePath").toString();

                    for (Chatlist chatlist:userList){
                        if(user.ID.equals(chatlist.getId())){
                            mUsers.add(user);
                        }
                    }
                }
                userAdapter=new UserAdapter(getContext(),mUsers,true);
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
