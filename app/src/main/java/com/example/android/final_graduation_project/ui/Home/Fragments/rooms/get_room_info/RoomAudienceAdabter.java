package com.example.android.final_graduation_project.ui.home.fragments.rooms.get_room_info;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android.final_graduation_project.databinding.AudienceListItemsBinding;
import com.example.android.final_graduation_project.pojo.Rooms.getRoomInfo.AudienceList;

import java.net.URL;
import java.util.ArrayList;

public class RoomAudienceAdabter extends RecyclerView.Adapter<RoomAudienceAdabter.RoomAudienceHolder> {
    ArrayList<AudienceList> data = new ArrayList<>();
   // AudienceViewModel data;
    Context context;
    LayoutInflater inflater;



    public RoomAudienceAdabter(ArrayList<AudienceList> data, Context context) {
        this.data = data;
        this.context = context;
    }

    public void setAudienceList(ArrayList<AudienceList> audienceList){
        this.data = audienceList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RoomAudienceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }
        AudienceListItemsBinding audienceListItemsBinding = AudienceListItemsBinding.inflate(inflater, parent, false);
        return new RoomAudienceHolder(audienceListItemsBinding);
    }


    @Override
    public void onBindViewHolder(@NonNull RoomAudienceHolder holder, int position) {
        holder.bind(data.get(position));
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    class RoomAudienceHolder extends RecyclerView.ViewHolder {

        private AudienceListItemsBinding audienceListItemsBinding;

        public RoomAudienceHolder(@NonNull AudienceListItemsBinding audienceListItemsBinding) {
            super(audienceListItemsBinding.getRoot());
            this.audienceListItemsBinding = audienceListItemsBinding;
           // this.audienceListItemsBinding.setViewModel(data);
        }

        void bind(AudienceList audienceList) {
            Glide.with(context).load(audienceList.getUser_image()).into(audienceListItemsBinding.memberImageView);
          /*  Uri imageUrl = Uri.parse(audienceList.getUser_image());
            audienceListItemsBinding.memberImageView.setImageURI(imageUrl);*/
            Log.i("Audience : " ,   audienceList.getUser_image() + "" );
            Log.i("Audience : " ,  audienceList.getUser_name() + "" );
            audienceListItemsBinding.memberName.setText(audienceList.getUser_name());
        }

    }
}


