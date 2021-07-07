package com.example.android.final_graduation_project.ui.home.fragments.rooms.get_room_info;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.mbms.MbmsErrors;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.android.final_graduation_project.R;
import com.example.android.final_graduation_project.SERVER.refresh_token.Refresh;
import com.example.android.final_graduation_project.SERVER.share_sounds.CustomPeerConnectionObserver;
import com.example.android.final_graduation_project.SERVER.share_sounds.CustomSdpObserver;
import com.example.android.final_graduation_project.SERVER.socket_connection.ConnectToSocket_IO;
import com.example.android.final_graduation_project.SessionManager;
import com.example.android.final_graduation_project.StatusBar;
import com.example.android.final_graduation_project.databinding.ActivityActiveRoomBinding;
import com.example.android.final_graduation_project.pojo.Rooms.endRoom.EndRoom;
import com.example.android.final_graduation_project.pojo.Rooms.getRoomInfo.AudienceList;
import com.example.android.final_graduation_project.pojo.Rooms.getRoomInfo.RoomInfo;
import com.example.android.final_graduation_project.pojo.Rooms.getRoomInfo.SpeakersList;
import com.example.android.final_graduation_project.pojo.Rooms.leaveRoom.LeaveRoom;
import com.example.android.final_graduation_project.pojo.SocketIOResponses.JoinRoomResponse;
import com.example.android.final_graduation_project.pojo.SocketIOResponses.LeaveRoomResponse;
import com.example.android.final_graduation_project.pojo.SocketIOResponses.WantToSpeakeResponse;
import com.example.android.final_graduation_project.pojo.UserInfo.UserInformation;
import com.example.android.final_graduation_project.ui.home.fragments.rooms.getRooms.OnItemClickListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.ContextUtils;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.internal.Util;

import org.webrtc.MediaConstraints;
import org.webrtc.SessionDescription;

public class ActiveRoomActivity extends AppCompatActivity {
    ActivityActiveRoomBinding activityActiveRoomBinding;
    ActiveRoomViewModel roomInfoViewModel;
    //leave socket io
    private String CHANNEL_ON_REFRESH = "refresh";
    private String CHANNEL_REQUSTE_TO_LEAVE_ROOM = "leave";
    private String CHANNEL_ROOM_LEAVE_ERROR = "error";
    //want_to_speak socket io
    private String CHANNEL_REQUSTE_TO_WANT_TO_SPEAK = "want_to_speak";
    private String CHANNEL_LISTEN_IF_MEMBER_WANT_TO_SPEAK = "listen_if_member_want_to_speak";
    //allow_member_to_speak socket io
    private String CHANNEL_REQUSTE_TO_ALLOW_MEMBER_TO_SPEAK = "allow_member_to_speak";
    private String CHANNEL_LISTEN_IF_MEMBER_CAN_SPEAK = "if_i_can_speak";
    //dis_allow_member_to_speak socket io
    private String CHANNEL_REQUSTE_TO_DISALLOW_MEMBER_TO_SPEAK = "dis_allow_member_to_speak";
    //move_to_audience socket io
    private String CHANNEL_REQUSTE_TO_MOVE_TO_AUDIENCE = "move_to_audience";
    private String CHANNEL_LISTEN_IF_MODERATOR_CANCEL_MEMBER_REQUEST = "listen_if_moderator_cancel_my_request";
    //open or close mic socket io
    private String CHANNEL_MEMBER_OPEN_MIC = "member_open_mic";
    private String CHANNEL_MEMBER_CLOSE_MIC = "member_closed_mic";
    private String CHANNEL_REFRESH_MIC = "refresh_mic";
    //open or close mic socket io
    private String CHANNEL_END_ROOM = "end_room";
    private String CHANNEL_NOTHING_HAPPEND = "no_thing_happend";
    private String CHANNEL_ROOM_ENDED = "room_ended";
    private String CHANNEL_MY_ROOM_ENDED = "my_room_ended";

    //webRtc
    private MediaConstraints sdpConstraints;
    private PeerConnection localPeer;
    private PeerConnection remotePeer;
    private PeerConnectionFactory peerConnectionFactory;
    private AudioTrack localAudioTrack;
    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private int permissionCode = 21;

    private String TOAST_TAG = "GetRoomInfo";
    private static final String ARG_ACCESS_TOKEN = "accessToken";
    private static final String ARG_ROOM_ID = "room_id";
    String accessToken = "";
    String roomId = "";
    HashMap<String, Object> data;
    private List<HashMap<String, Object>> memberswantTospeakList;
    private Socket getRoomInfoSocket;
    private Socket socketIdFromRoomSocket;
    PopupMenu popupMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityActiveRoomBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_active_room);
        activityActiveRoomBinding.setLifecycleOwner(this);
        roomInfoViewModel = new ViewModelProvider(this).get(ActiveRoomViewModel.class);
        new StatusBar(this, R.color.browser_actions_bg_grey);

        //Socket Connection
        Log.i(TOAST_TAG, "Socket Connection : " + ConnectToSocket_IO.isConnect() + "");
        getRoomInfoSocket = ConnectToSocket_IO.getServerSocketConnction();
        Log.i(TOAST_TAG, ConnectToSocket_IO.getServerSocketConnction().toString() + "");
        getRoomInfoSocket.on(CHANNEL_ROOM_LEAVE_ERROR, onRoomLeaveError);
        getRoomInfoSocket.on(CHANNEL_ON_REFRESH, onRefresh);
        getRoomInfoSocket.on(CHANNEL_REFRESH_MIC, onRefreshMic);
        getRoomInfoSocket.on(CHANNEL_ROOM_ENDED, onRoomEnded);
        getRoomInfoSocket.on(CHANNEL_MY_ROOM_ENDED, onMyRoomEnded);
        getRoomInfoSocket.on(CHANNEL_NOTHING_HAPPEND, onNoThingHappend);
        getRoomInfoSocket.on(CHANNEL_LISTEN_IF_MEMBER_CAN_SPEAK, onIfCanSpeak);
        getRoomInfoSocket.on(CHANNEL_LISTEN_IF_MODERATOR_CANCEL_MEMBER_REQUEST, onIfModeratorCancelMemberRequst);


        accessToken = getIntent().getStringExtra(ARG_ACCESS_TOKEN);
        roomId = getIntent().getStringExtra(ARG_ROOM_ID);
        Log.i("getRoomInfo", ARG_ACCESS_TOKEN + accessToken);
        Log.i("getRoomInfo", ARG_ROOM_ID + roomId + "");
        data = new HashMap<>();
        data.put(ARG_ROOM_ID, roomId);
        memberswantTospeakList = new ArrayList<>();
        memberswantTospeakList.add(new HashMap<>());

        activityActiveRoomBinding.askToSpeakBtn.setVisibility(View.GONE);
        activityActiveRoomBinding.acceptMemberAsk.setVisibility(View.GONE);
        activityActiveRoomBinding.micOff.setVisibility(View.GONE);
        activityActiveRoomBinding.micOn.setVisibility(View.GONE);
        activityActiveRoomBinding.LeaveRoom.setVisibility(View.GONE);
        activityActiveRoomBinding.EndRoom.setVisibility(View.GONE);

        roomInfoViewModel.setRoomInfoData(accessToken, data);
        roomInfoViewModel.getUserInfo(accessToken);

        PopupMenu popupMenu = new PopupMenu(getBaseContext(), activityActiveRoomBinding.acceptMemberAsk);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

        roomInfoViewModel.roomInfoListMutableLiveData.observe(this, new Observer<RoomInfo>() {
            @Override
            public void onChanged(RoomInfo roomInfo) {
                if (roomInfoViewModel.roomInfoListMutableLiveData.getValue().getCode() != 403) {
                    if (roomInfoViewModel.roomInfoListMutableLiveData.getValue() != null) {

                        int length = roomInfoViewModel.roomInfoListMutableLiveData.getValue().getRoom_info().getSpeakers().size();
                        Log.i(TOAST_TAG, "length : " + length + "");
                        roomInfoViewModel.userInformationMutableLiveData.observe(activityActiveRoomBinding.getLifecycleOwner(), new Observer<UserInformation>() {
                            @Override
                            public void onChanged(UserInformation userInformation) {
                                for (int i = 0; i < length; i++) {

                                    if (roomInfoViewModel.userInformationMutableLiveData.getValue().getUser().get_id()
                                            .equals(roomInfoViewModel.roomInfoListMutableLiveData.getValue().getRoom_info().getSpeakers().get(i).getUser_id())) {

                                        if (roomInfoViewModel.userInformationMutableLiveData.getValue().getUser().get_id()
                                                .equals(roomInfoViewModel.roomInfoListMutableLiveData.getValue().getRoom_info().getCreated_by())) {
                                            Log.i(TOAST_TAG, "Created By  : " + roomInfoViewModel.roomInfoListMutableLiveData.getValue().getRoom_info().getCreated_by());
                                            String userId = roomInfoViewModel.roomInfoListMutableLiveData.getValue().getRoom_info().getSpeakers().get(i).getUser_id();
                                            Log.i(TOAST_TAG, "User Created this room   : " + roomInfoViewModel.roomInfoListMutableLiveData.getValue().getRoom_info().getSpeakers().get(i).getUser_id());
                                            getRoomInfoSocket.on(CHANNEL_LISTEN_IF_MEMBER_WANT_TO_SPEAK, onSpeakerListenIfMemberWantToSpeak);
                                            registerForContextMenu(activityActiveRoomBinding.acceptMemberAsk);
                                            activityActiveRoomBinding.EndRoom.setVisibility(View.VISIBLE);
                                            activityActiveRoomBinding.micOff.setVisibility(View.VISIBLE);
                                            activityActiveRoomBinding.acceptMemberAsk.setVisibility(View.VISIBLE);
                                            activityActiveRoomBinding.EndRoom.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                    JSONObject jsonObject = new JSONObject();
                                                    try {
                                                        jsonObject.put("roomId", roomId);
                                                        getRoomInfoSocket.emit(CHANNEL_END_ROOM, jsonObject.toString());
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                            activityActiveRoomBinding.acceptMemberAsk.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    //Toast.makeText(getBaseContext() , "no click, Long press!",Toast.LENGTH_LONG).show();

                                                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                                        @Override
                                                        public boolean onMenuItemClick(MenuItem item) {
                                                            JSONObject jsonObject = new JSONObject();
                                                            try {
                                                                jsonObject.put("memberSocketId", memberswantTospeakList.get(item.getItemId()).get("memberSocketId"));
                                                                Log.i(TOAST_TAG, memberswantTospeakList.get(item.getItemId()).get("memberSocketId") + "");
                                                                new AlertDialog.Builder(ActiveRoomActivity.this)
                                                                        .setTitle("request")
                                                                        .setMessage(memberswantTospeakList.get(item.getItemId()).get("userName").toString() + " want to speak ")
                                                                        .setCancelable(false)
                                                                        .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(DialogInterface dialog, int which) {
                                                                                getRoomInfoSocket.emit(CHANNEL_REQUSTE_TO_ALLOW_MEMBER_TO_SPEAK, jsonObject.toString());
                                                                                JSONObject json = new JSONObject();
                                                                                try {
                                                                                    json.put("roomId", roomId);
                                                                                    getRoomInfoSocket.emit(CHANNEL_REQUSTE_TO_MOVE_TO_AUDIENCE, json.toString());
                                                                                } catch (JSONException e) {
                                                                                    e.printStackTrace();
                                                                                }

                                                                            }
                                                                        })
                                                                        .setNegativeButton("Dis Allow", new DialogInterface.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(DialogInterface dialog, int which) {
                                                                                getRoomInfoSocket.emit(CHANNEL_REQUSTE_TO_DISALLOW_MEMBER_TO_SPEAK, jsonObject.toString());

                                                                            }
                                                                        })
                                                                        .show();
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                            return true;
                                                        }
                                                    });
                                                    popupMenu.show();
                                                }
                                            });
                                            activityActiveRoomBinding.micOff.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    activityActiveRoomBinding.micOff.setVisibility(View.GONE);
                                                    activityActiveRoomBinding.micOn.setVisibility(View.VISIBLE);
                                                    //activityActiveRoomBinding.micOff.setImageResource(R.id.mic_on);
                                                    try {
                                                        JSONObject jsonObject = new JSONObject();
                                                        jsonObject.put("roomId", roomId);
                                                        jsonObject.put("userId", userId);
                                                        getRoomInfoSocket.emit(CHANNEL_MEMBER_OPEN_MIC, jsonObject.toString());
                                                    } catch (JSONException ex) {
                                                        ex.printStackTrace();
                                                    }

                                                }
                                            });
                                            activityActiveRoomBinding.micOn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    activityActiveRoomBinding.micOn.setVisibility(View.GONE);
                                                    activityActiveRoomBinding.micOff.setVisibility(View.VISIBLE);
                                                    try {
                                                        JSONObject jsonObject = new JSONObject();
                                                        jsonObject.put("roomId", roomId);
                                                        jsonObject.put("userId", userId);
                                                        getRoomInfoSocket.emit(CHANNEL_MEMBER_CLOSE_MIC, jsonObject.toString());
                                                    } catch (JSONException ex) {
                                                        ex.printStackTrace();
                                                    }

                                                }
                                            });
                                        }

                                    } else { // if audiance
                                        activityActiveRoomBinding.LeaveRoom.setVisibility(View.VISIBLE);
                                        activityActiveRoomBinding.askToSpeakBtn.setVisibility(View.VISIBLE);
                                        activityActiveRoomBinding.askToSpeakBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Toast.makeText(getBaseContext(), "wait until speakers accept your request !", Toast.LENGTH_LONG).show();
                                                JSONObject jsonObject = new JSONObject();
                                                try {
                                                    jsonObject.put("roomId", roomId);
                                                    jsonObject.put("username", userInformation.getUser().getUsername()+"");
                                                    getRoomInfoSocket.emit(CHANNEL_REQUSTE_TO_WANT_TO_SPEAK, jsonObject.toString());
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }


                                            }
                                        });
                                        activityActiveRoomBinding.LeaveRoom.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                JSONObject jsonObject = new JSONObject();
                                                try {
                                                    jsonObject.put("roomId", roomId);
                                                    Log.i(TOAST_TAG, roomId + " to leave");
                                                    getRoomInfoSocket.emit(CHANNEL_REQUSTE_TO_LEAVE_ROOM, jsonObject.toString());
                                                    roomInfoViewModel.setLeaveRoom(accessToken, data);
                                                    finish();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        });

                                    }
                                }

                            }
                        });


                        //Speakers
                        RoomSpeakersAdabter roomSpeakersAdabter = new RoomSpeakersAdabter(
                                roomInfoViewModel.roomInfoListMutableLiveData.getValue().getRoom_info().getSpeakers(),
                                getBaseContext()
                        );
                        activityActiveRoomBinding.roomSpeakersRv.setVisibility(View.VISIBLE);
                        activityActiveRoomBinding.roomSpeakersRv.setAdapter(roomSpeakersAdabter);
                        LinearLayoutManager layoutManager
                                = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false);
                        activityActiveRoomBinding.roomSpeakersRv.setLayoutManager(layoutManager);
                        //Audience
                        RoomAudienceAdabter roomAudienceAdabter = new RoomAudienceAdabter(
                                roomInfoViewModel.roomInfoListMutableLiveData.getValue().getRoom_info().getAudience(),
                                getBaseContext()
                        );
                        LinearLayoutManager layoutManager22 = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false);
                        activityActiveRoomBinding.roomMemberRv.setVisibility(View.VISIBLE);
                        activityActiveRoomBinding.roomMemberRv.setAdapter(roomAudienceAdabter);
                        activityActiveRoomBinding.roomMemberRv.setLayoutManager(layoutManager22);
                    }
                } else {
                    roomInfoViewModel.refredh_token(accessToken);
                    Toast.makeText(getBaseContext(), "something went wrong. please retry ", Toast.LENGTH_LONG).show();
                }

            }
        });

        roomInfoViewModel.leaveRoomMutableLiveData.observe(this, new Observer<LeaveRoom>() {
            @Override
            public void onChanged(LeaveRoom leaveRoom) {
                if (roomInfoViewModel.leaveRoomMutableLiveData.getValue().getCode() != 403) {
                    Log.i(TOAST_TAG, roomInfoViewModel.leaveRoomMutableLiveData.getValue().getMessage() + "");
                    Log.i(TOAST_TAG, roomInfoViewModel.leaveRoomMutableLiveData.getValue().isLeft() + "");
                    Toast.makeText(getBaseContext(), roomInfoViewModel.leaveRoomMutableLiveData.getValue().getMessage() + "", Toast.LENGTH_LONG).show();
                } else {
                    roomInfoViewModel.refredh_token(accessToken);
                    Toast.makeText(getBaseContext(), "something went wrong. please retry ", Toast.LENGTH_LONG).show();
                }
            }
        });

        roomInfoViewModel.endRoomMutableLiveData.observe(this, new Observer<EndRoom>() {
            @Override
            public void onChanged(EndRoom endRoom) {
                if (roomInfoViewModel.endRoomMutableLiveData.getValue().getCode() != 403) {
                    Log.i(TOAST_TAG, roomInfoViewModel.endRoomMutableLiveData.getValue().getMessage() + "");
                    Toast.makeText(getBaseContext(), roomInfoViewModel.endRoomMutableLiveData.getValue().getMessage() + "", Toast.LENGTH_LONG).show();
                } else {
                    roomInfoViewModel.refredh_token(accessToken);
                    Toast.makeText(getBaseContext(), "something went wrong. please retry ", Toast.LENGTH_LONG).show();
                }
            }
        });

        roomInfoViewModel.userInformationMutableLiveData.observe(this, new Observer<UserInformation>() {
            @Override
            public void onChanged(UserInformation userInformation) {
                if (roomInfoViewModel.userInformationMutableLiveData.getValue().getCode() != 403) {
                    Log.i(TOAST_TAG, roomInfoViewModel.userInformationMutableLiveData.getValue().getUser().get_id() + "");
                } else {
                    roomInfoViewModel.refredh_token(accessToken);
                    Toast.makeText(getBaseContext(), "something went wrong. please retry ", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
        roomInfoViewModel.refreshMutableLiveData.observe(this, new Observer<Refresh>() {
            @Override
            public void onChanged(Refresh refresh) {
                Log.i(TOAST_TAG, roomInfoViewModel.refreshMutableLiveData.getValue().getToken() + "");
                SessionManager.setAccessToken(roomInfoViewModel.refreshMutableLiveData.getValue().getToken());
            }
        });

        checkPermission();
        start();
    }
    private boolean checkPermission() {
        if (ActivityCompat.checkSelfPermission(getBaseContext(), recordPermission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{recordPermission}, permissionCode);
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (activityActiveRoomBinding.EndRoom.getVisibility() == View.VISIBLE) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("roomId", roomId);
                getRoomInfoSocket.emit(CHANNEL_END_ROOM, jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (activityActiveRoomBinding.LeaveRoom.getVisibility() == View.VISIBLE) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("roomId", roomId);
                Log.i(TOAST_TAG, roomId + " to leave");
                getRoomInfoSocket.emit(CHANNEL_REQUSTE_TO_LEAVE_ROOM, jsonObject.toString());
                roomInfoViewModel.setLeaveRoom(accessToken, data);
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private Emitter.Listener onRefresh = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TOAST_TAG, CHANNEL_ON_REFRESH + " : " + args[0].toString() + "");
                    JSONObject jsonObject = (JSONObject) args[0];
                    try {
                        if (jsonObject.get("message").equals("there is member left the room so try to refresh the view")) {
                            Log.i(TOAST_TAG, CHANNEL_ON_REFRESH + " : " + jsonObject.get("message") + "");
                            //Toast.makeText(getBaseContext(), response.getMessage() + "", Toast.LENGTH_LONG).show();
                            Log.i(TOAST_TAG, CHANNEL_ON_REFRESH + " : " + jsonObject.get("message") + "");
                            roomInfoViewModel.setRoomInfoData(accessToken, data);
                        }
                        if (jsonObject.get("message").equals("new member joined so try to refresh the view")) {
                            Log.i(TOAST_TAG, CHANNEL_ON_REFRESH + " : " + jsonObject.get("message") + "");
                            roomInfoViewModel.setRoomInfoData(accessToken, data);
                        }
                        if (jsonObject.get("message").equals("there is a member moved to audience")) {
                            roomInfoViewModel.setRoomInfoData(accessToken, data);
                            Log.i(TOAST_TAG, CHANNEL_ON_REFRESH + " : " + jsonObject.get("message") + "");
                        }
                        if (jsonObject.get("message").equals("there is member changed his state from audience to speaker so try to refresh the view")) {
                            roomInfoViewModel.setRoomInfoData(accessToken, data);
                            Log.i(TOAST_TAG, CHANNEL_ON_REFRESH + " : " + jsonObject.get("message") + "");
                        }

                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }

                }
            });
        }
    };
    private Emitter.Listener onRefreshMic = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TOAST_TAG, CHANNEL_ON_REFRESH + " : " + args[0].toString() + "");
                    JSONObject jsonObject = (JSONObject) args[0];
                    try {
                        if (jsonObject.get("message").equals("there is a member opened his mic")) {

                            Log.i(TOAST_TAG, CHANNEL_ON_REFRESH + " message : " + jsonObject.get("message") + "");
                            Log.i(TOAST_TAG, CHANNEL_ON_REFRESH + " user : " + jsonObject.get("userId") + "");
                        }
                        if (jsonObject.get("message").equals("there is a member closed his mic")) {

                            Log.i(TOAST_TAG, CHANNEL_ON_REFRESH + " message : " + jsonObject.get("message") + "");
                            Log.i(TOAST_TAG, CHANNEL_ON_REFRESH + " user : " + jsonObject.get("userId") + "");
                        }
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }
    };
    private Emitter.Listener onRoomLeaveError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TOAST_TAG, CHANNEL_ROOM_LEAVE_ERROR + " : " + args[0].toString() + "");
                    Gson gson = new Gson();
                    LeaveRoomResponse response = gson.fromJson(args[0].toString(), LeaveRoomResponse.class);
                    Log.i(TOAST_TAG, CHANNEL_ROOM_LEAVE_ERROR + " : " + response.getMessage() + "");
                    Toast.makeText(getBaseContext(), response.getMessage() + "", Toast.LENGTH_LONG).show();
                }
            });
        }
    };
    private Emitter.Listener onSpeakerListenIfMemberWantToSpeak = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TOAST_TAG, CHANNEL_LISTEN_IF_MEMBER_WANT_TO_SPEAK + " : " + args[0].toString() + "");
                    try {
                        JSONObject json = (JSONObject) args[0];
                        Log.i(TOAST_TAG, CHANNEL_LISTEN_IF_MEMBER_WANT_TO_SPEAK + " : " + json.getString("username") + "");
                        Log.i(TOAST_TAG, CHANNEL_LISTEN_IF_MEMBER_WANT_TO_SPEAK + " : " + json.getString("socketId").toString() + "");
                        memberswantTospeakList.get(0).put("userName", json.getString("username")+"");
                        memberswantTospeakList.get(0).put("memberSocketId", json.getString("socketId")+"");
                        popupMenu = new PopupMenu(getBaseContext(), activityActiveRoomBinding.acceptMemberAsk);
                        //popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                        popupMenu.getMenu().add(json.getString("username"));
                        Toast.makeText(getBaseContext(), json.getString("username") + " can i speak? ", Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    };
    private Emitter.Listener onRoomEnded = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TOAST_TAG, CHANNEL_ROOM_ENDED + " : " + args[0].toString() + "");
                    new AlertDialog.Builder(ActiveRoomActivity.this)
                            .setTitle("alert")
                            .setMessage("Room ended by it's creator !")
                            .setCancelable(false)
                            .setNegativeButton("Okey", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }).show();
                }
            });
        }
    };
    private Emitter.Listener onNoThingHappend = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TOAST_TAG, args[0].toString() + "");
                }
            });
        }
    };
    private Emitter.Listener onMyRoomEnded = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TOAST_TAG, args[0].toString() + "");
                    roomInfoViewModel.setEndRoom(accessToken, data);
                    finish();
                }
            });
        }
    };
    private Emitter.Listener onIfCanSpeak = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TOAST_TAG,CHANNEL_LISTEN_IF_MEMBER_CAN_SPEAK + " : "+args[0].toString()+"");
                    try {
                        JSONObject jsonObject = (JSONObject) args[0];
                        Toast.makeText(getBaseContext(),jsonObject.getString("message")+"",Toast.LENGTH_LONG) .show();
                    }catch (JSONException e){
                        e.printStackTrace();
                    }

                }
            });
        }
    };
    private Emitter.Listener onIfModeratorCancelMemberRequst = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i(TOAST_TAG,CHANNEL_LISTEN_IF_MODERATOR_CANCEL_MEMBER_REQUEST + " : "+args[0].toString()+"");
            try {
                JSONObject jsonObject = (JSONObject) args[0];
                Toast.makeText(getBaseContext(),jsonObject.getString("message")+"",Toast.LENGTH_LONG) .show();
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    };

    private void start() {
        if (checkPermission()) {
            initializePeerConnectionFactory();

        } else {
            Toast.makeText(this, "Need some permissions", Toast.LENGTH_LONG).show();
        }
    }
    private void initializePeerConnectionFactory() {
        //Initialize PeerConnectionFactory globals.
        //Params are context, initAudio,initVideo and videoCodecHwAcceleration
        PeerConnectionFactory.initializeAndroidGlobals(this, true, false, true);

        //Create a new PeerConnectionFactory instance.
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        PeerConnectionFactory peerConnectionFactory = new PeerConnectionFactory(options);

        //Create MediaConstraints - Will be useful for specifying video and audio constraints. More on this later!
        MediaConstraints constraints = new MediaConstraints();

        //create an AudioSource instance
        AudioSource audioSource = peerConnectionFactory.createAudioSource(constraints);
        localAudioTrack = peerConnectionFactory.createAudioTrack("101", audioSource);

    }
    private void call() {
        //we already have video and audio tracks. Now create peerconnections
        List<PeerConnection.IceServer> iceServers = new ArrayList<>();

        //create sdpConstraints
        sdpConstraints = new MediaConstraints();
        sdpConstraints.mandatory.add(new MediaConstraints.KeyValuePair("offerToReceiveAudio", "true"));
        sdpConstraints.mandatory.add(new MediaConstraints.KeyValuePair("offerToReceiveVideo", "true"));

        //creating localPeer
        localPeer = peerConnectionFactory.createPeerConnection(iceServers, sdpConstraints, new CustomPeerConnectionObserver("localPeerCreation") {
            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                super.onIceCandidate(iceCandidate);
                onIceCandidateReceived(localPeer, iceCandidate);
            }
        });

        //creating remotePeer
        remotePeer = peerConnectionFactory.createPeerConnection(iceServers, sdpConstraints, new CustomPeerConnectionObserver("remotePeerCreation") {

            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                super.onIceCandidate(iceCandidate);
                onIceCandidateReceived(remotePeer, iceCandidate);
            }

            @Override
            public void onAddStream(MediaStream mediaStream) {
                super.onAddStream(mediaStream);
                gotRemoteStream(mediaStream);
            }
        });

        //creating local mediastream
        MediaStream stream = peerConnectionFactory.createLocalMediaStream("102");
        stream.addTrack(localAudioTrack);
        //stream.addTrack(localVideoTrack);
        localPeer.addStream(stream);

        //creating Offer
        localPeer.createOffer(new CustomSdpObserver("localCreateOffer"){
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                //we have localOffer. Set it as local desc for localpeer and remote desc for remote peer.
                //try to create answer from the remote peer.
                super.onCreateSuccess(sessionDescription);
                localPeer.setLocalDescription(new CustomSdpObserver("localSetLocalDesc"), sessionDescription);
                remotePeer.setRemoteDescription(new CustomSdpObserver("remoteSetRemoteDesc"), sessionDescription);
                remotePeer.createAnswer(new CustomSdpObserver("remoteCreateOffer") {

                    @Override
                    public void onCreateSuccess(SessionDescription sessionDescription) {
                        //remote answer generated. Now set it as local desc for remote peer and remote desc for local peer.
                        super.onCreateSuccess(sessionDescription);
                        remotePeer.setLocalDescription(new CustomSdpObserver("remoteSetLocalDesc"), sessionDescription);
                        localPeer.setRemoteDescription(new CustomSdpObserver("localSetRemoteDesc"), sessionDescription);
                    }
                },new MediaConstraints());
            }
        },sdpConstraints);
    }
    public void onIceCandidateReceived(PeerConnection peer, IceCandidate iceCandidate) {
        //we have received ice candidate. We can set it to the other peer.
        if (peer == localPeer) {
            remotePeer.addIceCandidate(iceCandidate);
        } else {
            localPeer.addIceCandidate(iceCandidate);
        }
    }
    private void gotRemoteStream(MediaStream stream) {
        //we have remote video stream. add to the renderer.
        AudioTrack audioTrack = stream.audioTracks.getFirst();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    audioTrack.setEnabled(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void hangup() {
        localPeer.close();
        remotePeer.close();
        localPeer = null;
        remotePeer = null;
    }

    private createPeerConnection(){
        PeerConnection.RTCConfiguration rtcCongig =PeerConnection.RTCConfiguration(remotePeer);
        rtcCongig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy
    }

}