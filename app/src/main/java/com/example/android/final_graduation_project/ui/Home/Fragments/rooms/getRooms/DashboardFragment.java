package com.example.android.final_graduation_project.ui.home.fragments.rooms.getRooms;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.final_graduation_project.R;
import com.example.android.final_graduation_project.SERVER.refresh_token.Refresh;
import com.example.android.final_graduation_project.SessionManager;
import com.example.android.final_graduation_project.databinding.FragmentDashboardBinding;
import com.example.android.final_graduation_project.pojo.Rooms.getRooms.GetRooms;
import com.example.android.final_graduation_project.pojo.Rooms.getRooms.RoomsList;
import com.example.android.final_graduation_project.pojo.Rooms.joinRoom.JoinRoom;
import com.example.android.final_graduation_project.ui.home.fragments.rooms.createRooms.CreateRoomFragment;
import com.example.android.final_graduation_project.ui.home.fragments.rooms.get_room_info.ActiveRoomActivity;

import java.util.HashMap;

import retrofit2.Retrofit;


public class DashboardFragment extends Fragment {
    private String ARG_ROOM_ID = "room_id";
    private String TOAST_TAG = "GetRoom";
    private static final String ARG_ACCESS_TOKEN = "accessToken";
    private String mAccessToken;
    private String accessToken = "";
    CreateRoomFragment createRoomFragment;
    Retrofit retrofit;
    FragmentDashboardBinding dashboardBinding;
    DashboardViewModel dashboardViewModel;
    ShowAllRoomsAdapter showAllRoomsAdapter;
    OnItemClickListener onItemClickListener;

    public DashboardFragment() {
    }

    public static DashboardFragment newInstance(String mAccessToken) {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ACCESS_TOKEN, mAccessToken);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accessToken = "Bearer " + SessionManager.getAccessToken();
        Log.i(TOAST_TAG, accessToken + "");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dashboardBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard, container, false);
        dashboardBinding.setLifecycleOwner(this);
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        dashboardViewModel.getRoom(accessToken);
        dashboardBinding.executePendingBindings();

        dashboardBinding.createNewRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createRoomFragment = CreateRoomFragment.newInstance(accessToken);
                createRoomFragment.show(requireActivity().getSupportFragmentManager(), null);

            }
        });

        dashboardViewModel.getRoomsMutableLiveData.observe(this, new Observer<GetRooms>() {
            @Override
            public void onChanged(GetRooms getRooms) {
                if(dashboardViewModel.getRoomsMutableLiveData.getValue().getCount() >0) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dashboardBinding.shimmerEffectRooms.stopShimmer();
                            dashboardBinding.shimmerEffectRooms.setVisibility(View.GONE);
                            dashboardBinding.getRoomsRv.setVisibility(View.VISIBLE);
                            showAllRoomsAdapter = new ShowAllRoomsAdapter(
                                    dashboardViewModel.getRoomsMutableLiveData.getValue().getRooms()
                                    , getContext()
                                    , accessToken
                                    , new OnItemClickListener() {
                                @Override
                                public void onItemClick(RoomsList getRooms) {
                                   String roomID = getRooms.getRoom_id();
                                    HashMap<String , Object> data = new HashMap<>();
                                    data.put(ARG_ROOM_ID , roomID);
                                    Log.i(TOAST_TAG , accessToken + "");
                                    Log.i(TOAST_TAG , roomID + "");
                                    Intent intent = new Intent(getContext() , ActiveRoomActivity.class);
                                    intent.putExtra(ARG_ACCESS_TOKEN , accessToken);
                                    intent.putExtra(ARG_ROOM_ID , roomID);
                                    dashboardViewModel.join_Room(accessToken,data);
                                    startActivity(intent);

                                }
                            });

                            showAllRoomsAdapter.notifyDataSetChanged();
                            Log.i(TOAST_TAG , "rooms size  : "+dashboardViewModel.getRoomsMutableLiveData.getValue().getRooms().size() + "" );
                            dashboardBinding.getRoomsRv.setAdapter(showAllRoomsAdapter);
                            LinearLayoutManager layoutManager
                                    = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                            dashboardBinding.getRoomsRv.setLayoutManager(layoutManager);
                        /*    dashboardBinding.refreshGetRooms.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                @Override
                                public void onRefresh() {
                                   showAllRoomsAdapter.updateList(dashboardViewModel.getRoomsMutableLiveData.getValue().getRooms());
                                   showAllRoomsAdapter.notifyDataSetChanged();

                                }
                            });*/


                        }
                    }, 1500);

               }else{}

            }
        });

        dashboardViewModel.joinRoomMutableLiveData.observe(this, new Observer<JoinRoom>() {
            @Override
            public void onChanged(JoinRoom joinRoom) {
                if(dashboardViewModel.joinRoomMutableLiveData.getValue().getCode() != 403 ) {
                    Log.i(TOAST_TAG, "Joined : " + dashboardViewModel.joinRoomMutableLiveData.getValue().isJoined() + "");
                    Log.i(TOAST_TAG, "Message : " + dashboardViewModel.joinRoomMutableLiveData.getValue().getMessage() + "");
                    Toast.makeText(getContext(),
                            dashboardViewModel.joinRoomMutableLiveData.getValue().getMessage() + "", Toast.LENGTH_LONG).show();
                    //  dashboardBinding.getRoomsRv.notifyAll();
                }else{
                    dashboardViewModel.refredh_token(accessToken);
                    Toast.makeText(getContext() , "something went wrong. please retry ",Toast.LENGTH_LONG).show();

                }
            }
        });
        dashboardViewModel.refreshMutableLiveData.observe(this, new Observer<Refresh>() {
            @Override
            public void onChanged(Refresh refresh) {
                Log.i(TOAST_TAG , dashboardViewModel.refreshMutableLiveData.getValue().getToken()+"");
                SessionManager.setAccessToken(dashboardViewModel.refreshMutableLiveData.getValue().getToken());
            }
        });

        return dashboardBinding.getRoot();
    }
}