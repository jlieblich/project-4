package com.lieblich.jon.playme.event_detail_components.message_components;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.lieblich.jon.playme.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lieblich.jon.playme.main_menu.MainMenuView;

import java.util.ArrayList;
import java.util.List;

public class ChatGroupActivity extends AppCompatActivity {
    private RecyclerView mMessageRecycler;
    private EditText mMessageText;
    private Button mSendMessage;
    private MessageGroup mGroup;
    private MessageRecyclerAdapter mAdapter;
    private DatabaseReference mReference;
    private ChildEventListener mListener;
    private List<SelfMessageObject> mContent;
    private String mChatTitle, mSenderName;
    private static final String TYPE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_group);

        SharedPreferences pref = getSharedPreferences("account", MODE_PRIVATE);
        mSenderName = pref.getString("firstName", "ERROR");

        Intent intent = getIntent();
        String chatKey = intent.getStringExtra("chatKey");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mReference = database.getReference("chats").child(chatKey);

        mMessageRecycler = (RecyclerView)findViewById(R.id.chat_message_recycler);
        mMessageText = (EditText)findViewById(R.id.message_edit);
        mSendMessage = (Button)findViewById(R.id.send_message);

        mGroup = new MessageGroup();
        mContent = new ArrayList<>();
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new MessageRecyclerAdapter(mContent, mSenderName);
        mMessageRecycler.setAdapter(mAdapter);

        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mGroup = dataSnapshot.getValue(MessageGroup.class);
                if(mGroup.getUsers().get(0).getUser().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    mChatTitle = mGroup.getUsers().get(1).getFirstName();
                } else {
                    mChatTitle = mGroup.getUsers().get(0).getFirstName();
                }
                setTitle(mChatTitle);
                if(mGroup.getMessages()==null||mGroup.getMessages().size()==0) {
                    mReference.child("messages").push().setValue(new SelfMessageObject("started a new chat", mGroup.getCreator()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mMessageText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 0) {
                    mSendMessage.setBackgroundTintList(ColorStateList.valueOf(Color.argb(255,75,175,80)));
                    mSendMessage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String message = mMessageText.getText().toString();
                            SelfMessageObject sending = new SelfMessageObject(message, mSenderName, TYPE);
                            mReference.child("messages").push().setValue(sending);
                            mMessageText.setText("");
                        }
                    });
                } else {
                    mSendMessage.setBackgroundTintList(ColorStateList.valueOf(Color.argb(100,75,175,80)));
                    mSendMessage.setOnClickListener(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mMessageRecycler.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if(bottom < oldBottom) {
                    mMessageRecycler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mMessageRecycler.scrollToPosition(mContent.size()-1);
                        }
                    }, 100);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        setListener();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mReference.child("messages").removeEventListener(mListener);
        mContent.clear();
        mAdapter.notifyDataSetChanged();
        super.onStop();
    }

    private void setListener() {
        mListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mContent.add(dataSnapshot.getValue(SelfMessageObject.class));
                mAdapter.notifyItemInserted(mContent.size() - 1);
                mMessageRecycler.scrollToPosition(mContent.size() - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ChatGroupActivity.this, "Chat Error", Toast.LENGTH_SHORT).show();
            }
        };
        mReference.child("messages").addChildEventListener(mListener);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainMenuView.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
