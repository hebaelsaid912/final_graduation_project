package com.example.android.final_graduation_project.ui.home.fragments.rooms.get_room_info;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android.final_graduation_project.databinding.SpeakersListItemsBinding;
import com.example.android.final_graduation_project.pojo.Rooms.getRoomInfo.SpeakersList;

import java.util.ArrayList;

public class RoomSpeakersAdabter extends RecyclerView.Adapter<RoomSpeakersAdabter.RoomSpeakersHolder> {
    ArrayList<SpeakersList> data ;
   // SpeakersViewModel data;
    Context context;
    LayoutInflater inflater;

    public RoomSpeakersAdabter(ArrayList<SpeakersList> data , Context context) {
        this.data = data;
        this.context = context;
    }

    public void setSpeakersList(ArrayList<SpeakersList> speakersList){
        this.data = speakersList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public RoomSpeakersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }
        SpeakersListItemsBinding speakersListItemsBinding = SpeakersListItemsBinding.inflate(inflater, parent, false);
        return new RoomSpeakersHolder(speakersListItemsBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomSpeakersHolder holder, int position) {
        holder.bind(data.get(position));
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    class RoomSpeakersHolder extends RecyclerView.ViewHolder {

        private SpeakersListItemsBinding speakersListItemsBinding;

        public RoomSpeakersHolder(@NonNull SpeakersListItemsBinding speakersListItemsBinding) {
            super(speakersListItemsBinding.getRoot());
            this.speakersListItemsBinding = speakersListItemsBinding;
            //this.speakersListItemsBinding.setViewModel(data);
        }

        void bind(SpeakersList speakersList) {
            Glide.with(context).load(speakersList.getUser_image()).into(speakersListItemsBinding.imageView);
           /* Uri image_uri = Uri.parse(speakersList.getUser_image());
            speakersListItemsBinding.imageView.setImageURI(image_uri);*/
            Log.i("Speaker Link image : " , speakersList.getUser_image() );
            Log.i("Speaker Link image : " , speakersList.getUser_name());
            speakersListItemsBinding.textView7.setText(speakersList.getUser_name());
        }
    }
}


