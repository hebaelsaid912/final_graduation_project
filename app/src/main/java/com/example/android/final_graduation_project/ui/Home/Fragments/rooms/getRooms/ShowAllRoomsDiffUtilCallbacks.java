package com.example.android.final_graduation_project.ui.home.fragments.rooms.getRooms;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.example.android.final_graduation_project.pojo.Rooms.getRooms.GetRooms;
import com.example.android.final_graduation_project.pojo.Rooms.getRooms.RoomsList;

import java.util.ArrayList;

public class ShowAllRoomsDiffUtilCallbacks extends DiffUtil.Callback {
    ArrayList<RoomsList> oldGetRooms = new ArrayList<>();
    ArrayList<RoomsList> newGetRooms = new ArrayList<>();

    public ShowAllRoomsDiffUtilCallbacks(ArrayList<RoomsList> oldGetRooms, ArrayList<RoomsList> newGetRooms) {
        this.oldGetRooms = oldGetRooms;
        this.newGetRooms = newGetRooms;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }

    @Override
    public int getOldListSize() {
        return oldGetRooms != null ? oldGetRooms.size() : 0 ;
    }

    @Override
    public int getNewListSize() {
        return newGetRooms != null ? newGetRooms.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return true;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
       // int result = newGetRooms.get(newItemPosition)
        if(newGetRooms.get(newItemPosition) == oldGetRooms.get(oldItemPosition)){
            return true;
        }
        return false;
    }
}
