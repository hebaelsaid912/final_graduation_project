package com.example.android.final_graduation_project.ui.home.fragments.rooms.get_room_info;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.android.final_graduation_project.R;
import com.example.android.final_graduation_project.SERVER.refresh_token.Refresh;
import com.example.android.final_graduation_project.SessionManager;
import com.example.android.final_graduation_project.StatusBar;
import com.example.android.final_graduation_project.databinding.ActivityActiveRoomBinding;
import com.example.android.final_graduation_project.pojo.Rooms.endRoom.EndRoom;
import com.example.android.final_graduation_project.pojo.Rooms.getRoomInfo.AudienceList;
import com.example.android.final_graduation_project.pojo.Rooms.getRoomInfo.RoomInfo;
import com.example.android.final_graduation_project.pojo.Rooms.getRoomInfo.SpeakersList;
import com.example.android.final_graduation_project.pojo.Rooms.leaveRoom.LeaveRoom;
import com.example.android.final_graduation_project.pojo.UserInfo.UserInformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActiveRoomActivity extends AppCompatActivity {
    ActivityActiveRoomBinding activityActiveRoomBinding;
    ActiveRoomViewModel roomInfoViewModel;
    private String TOAST_TAG = "GetRoomInfo";
    private static final String ARG_ACCESS_TOKEN = "accessToken";
    private static final String ARG_ROOM_ID = "room_id";
    String accessToken = "";
    String roomId = "";
    HashMap<String, Object> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityActiveRoomBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_active_room);
        activityActiveRoomBinding.setLifecycleOwner(this);
        roomInfoViewModel = new ViewModelProvider(this).get(ActiveRoomViewModel.class);
        new StatusBar(this, R.color.browser_actions_bg_grey);
        accessToken = getIntent().getStringExtra(ARG_ACCESS_TOKEN);
        roomId = getIntent().getStringExtra(ARG_ROOM_ID);
        Log.i("getRoomInfo", ARG_ACCESS_TOKEN + accessToken);
        Log.i("getRoomInfo", ARG_ROOM_ID + roomId + "");
         data = new HashMap<>();
        data.put(ARG_ROOM_ID, roomId);

        activityActiveRoomBinding.roomSpeakersRv.setVisibility(View.GONE);
        activityActiveRoomBinding.roomMemberRv.setVisibility(View.GONE);
        activityActiveRoomBinding.LeaveRoom.setVisibility(View.VISIBLE);
        activityActiveRoomBinding.EndRoom.setVisibility(View.GONE);
        roomInfoViewModel.setRoomInfoData(accessToken, data);
        roomInfoViewModel.getUserInfo(accessToken);

        roomInfoViewModel.roomInfoListMutableLiveData.observe(this, new Observer<RoomInfo>() {
            @Override
            public void onChanged(RoomInfo roomInfo) {
                if(roomInfoViewModel.roomInfoListMutableLiveData.getValue().getCode() != 403) {
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
                                            Log.i(TOAST_TAG, "User Created this room   : " + roomInfoViewModel.roomInfoListMutableLiveData.getValue().getRoom_info().getSpeakers().get(i).getUser_id());

                                            activityActiveRoomBinding.EndRoom.setVisibility(View.VISIBLE);
                                            activityActiveRoomBinding.LeaveRoom.setVisibility(View.GONE);

                                            activityActiveRoomBinding.EndRoom.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    roomInfoViewModel.setEndRoom(accessToken, data);
                                                    finish();
                                                }
                                            });
                                        }

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
                }else{
                    roomInfoViewModel.refredh_token(accessToken);
                    Toast.makeText(getBaseContext() , "something went wrong. please retry ",Toast.LENGTH_LONG).show();
                }

            }
        });

        activityActiveRoomBinding.LeaveRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                roomInfoViewModel.setLeaveRoom(accessToken, data);
                finish();
            }
        });
        roomInfoViewModel.leaveRoomMutableLiveData.observe(this, new Observer<LeaveRoom>() {
            @Override
            public void onChanged(LeaveRoom leaveRoom) {
                if(roomInfoViewModel.leaveRoomMutableLiveData.getValue().getCode() != 403) {
                    Log.i(TOAST_TAG, roomInfoViewModel.leaveRoomMutableLiveData.getValue().getMessage() + "");
                    Log.i(TOAST_TAG, roomInfoViewModel.leaveRoomMutableLiveData.getValue().isLeft() + "");
                    Toast.makeText(getBaseContext(), roomInfoViewModel.leaveRoomMutableLiveData.getValue().getMessage() + "", Toast.LENGTH_LONG).show();
                }else{
                    roomInfoViewModel.refredh_token(accessToken);
                    Toast.makeText(getBaseContext() , "something went wrong. please retry ",Toast.LENGTH_LONG).show();
                }
            }
        });

        roomInfoViewModel.endRoomMutableLiveData.observe(this, new Observer<EndRoom>() {
            @Override
            public void onChanged(EndRoom endRoom) {
                if(roomInfoViewModel.endRoomMutableLiveData.getValue().getCode() != 403) {
                    Log.i(TOAST_TAG, roomInfoViewModel.endRoomMutableLiveData.getValue().getMessage() + "");
                    Toast.makeText(getBaseContext(), roomInfoViewModel.endRoomMutableLiveData.getValue().getMessage() + "", Toast.LENGTH_LONG).show();
                }else{
                    roomInfoViewModel.refredh_token(accessToken);
                    Toast.makeText(getBaseContext() , "something went wrong. please retry ",Toast.LENGTH_LONG).show();
                }
            }
        });

        roomInfoViewModel.userInformationMutableLiveData.observe(this, new Observer<UserInformation>() {
            @Override
            public void onChanged(UserInformation userInformation) {
                if(roomInfoViewModel.userInformationMutableLiveData.getValue().getCode() != 403) {
                    Log.i(TOAST_TAG, roomInfoViewModel.userInformationMutableLiveData.getValue().getUser().get_id() + "");
                }else{
                    roomInfoViewModel.refredh_token(accessToken);
                    Toast.makeText(getBaseContext() , "something went wrong. please retry ",Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
        roomInfoViewModel.refreshMutableLiveData.observe(this, new Observer<Refresh>() {
            @Override
            public void onChanged(Refresh refresh) {
                Log.i(TOAST_TAG , roomInfoViewModel.refreshMutableLiveData.getValue().getToken()+"");
                SessionManager.setAccessToken(roomInfoViewModel.refreshMutableLiveData.getValue().getToken());
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(activityActiveRoomBinding.EndRoom.getVisibility() == View.VISIBLE ){
            roomInfoViewModel.setEndRoom(accessToken , data);
        }else if (activityActiveRoomBinding.LeaveRoom.getVisibility() == View.VISIBLE){
            roomInfoViewModel.setLeaveRoom(accessToken, data);
        }
        finish();
    }
}