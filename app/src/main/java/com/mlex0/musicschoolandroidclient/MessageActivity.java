package com.mlex0.musicschoolandroidclient;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mlex0.musicschoolandroidclient.Adapter.MessageAdapter;
import com.mlex0.musicschoolandroidclient.Classes.Constants;
import com.mlex0.musicschoolandroidclient.Model.Chat;
import com.mlex0.musicschoolandroidclient.Model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {
    CircleImageView profile_image;
    TextView username;
    ImageButton btn_send;
    EditText text_send;

    String userid;

    MessageAdapter messageAdapter;
    List<Chat> mChat;

    RecyclerView recyclerView;

    DatabaseReference reference;

    Intent intent;

    ValueEventListener seenListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profile_image=findViewById(R.id.profile_image);
        username=findViewById(R.id.profile_username);
        btn_send=findViewById(R.id.btn_send);
        text_send=findViewById(R.id.text_send);

        intent=getIntent();
        userid=intent.getStringExtra("userid");

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg=text_send.getText().toString();
                if(!msg.equals("")){
                    sendMessage(Constants.UserId,userid,msg);
                }
                else {
                    Toast.makeText(MessageActivity.this,"Нельзя отправить пустое сообщение!",Toast.LENGTH_SHORT);
                }
                text_send.setText("");
            }
        });


        String url = Constants.ApiUrl + "user/" + userid;
        RequestQueue queue = Volley.newRequestQueue(MessageActivity.this);

        StringRequest request = new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject respObj = new JSONObject(response);
                    User user = new User();
                    try {

                        user.ID = respObj.get("ID").toString();
                        user.Email = respObj.get("Email").toString();
                        user.Password = respObj.get("Password").toString();
                        user.IDRole = respObj.get("IDRole").toString();
                        user.UserImage = respObj.get("UserImagePath").toString();

                        username.setText(user.Email);
                        if(user.getImageURL().equals("default")){
                            profile_image.setImageResource(R.drawable.bottom_navigation_profile_icon);
                        }
                        else {
                            GlideUrl glideUrl = new GlideUrl(user.getImageURL(), new LazyHeaders.Builder()
                                    .addHeader("Authorization", "Bearer " + Constants.UserToken)
                                    .build());

                            Glide.with(getApplicationContext()).load(glideUrl).into(profile_image);
                        }
                        readMessages(Constants.UserId,userid,user.getImageURL());
                    }
                    catch (Throwable te){

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // method to handle errors.
                Toast.makeText(MessageActivity.this, "Отсутствует подключение к серверу" + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>(super.getHeaders());
                if(params==null)params = new HashMap<>();
                params.put("Authorization","Bearer " + Constants.UserToken);
                return params;
            }
        };
        queue.add(request);

        seenMessage(userid);
    }

    private void seenMessage(final String userid){
        reference=FirebaseDatabase.getInstance("https://musicschoolandroidclient-default-rtdb.firebaseio.com").getReference("Chats");
        seenListener=reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Chat chat=snapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(Constants.UserId)&& chat.getSender().equals(userid)){
                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("isseen",true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String Sender,String Receiver,String Message){

        DatabaseReference reference=FirebaseDatabase.getInstance("https://musicschoolandroidclient-default-rtdb.firebaseio.com")
                .getReference();
        HashMap<String,Object> hashMap=new HashMap<>();

        hashMap.put("sender",Sender);
        hashMap.put("receiver",Receiver);
        hashMap.put("message",Message);
        hashMap.put("asseen",false);

        reference.child("Chats").push().setValue(hashMap);

        final DatabaseReference chatRef=FirebaseDatabase.getInstance("https://musicschoolandroidclient-default-rtdb.firebaseio.com")
                .getReference("Chatlist")
                .child(Constants.UserId)
                .child(userid);
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    chatRef.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //###
        final DatabaseReference chatRef1 = FirebaseDatabase.getInstance("https://musicschoolandroidclient-default-rtdb.firebaseio.com")
                .getReference("Chatlist")
                .child(Receiver).child(Constants.UserId);
        chatRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists())
                {
                    chatRef1.child("id").setValue(Constants.UserId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMessages(final String myid, final String userid, final String imageurl){
        mChat=new ArrayList<>();
        reference=FirebaseDatabase.getInstance("https://musicschoolandroidclient-default-rtdb.firebaseio.com").getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat=snapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(myid)&&chat.getSender().equals(userid)||
                            chat.getReceiver().equals(userid)&&chat.getSender().equals(myid)){
                        mChat.add(chat);
                    }
                    messageAdapter=new MessageAdapter(MessageActivity.this,mChat,imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
