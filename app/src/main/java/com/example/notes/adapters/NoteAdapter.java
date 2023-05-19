package com.example.notes.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.Listeners.NoteListener;
import com.example.notes.R;
import com.example.notes.activities.MainActivity;
import com.example.notes.entities.Note;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.MyViewHolder>{
    private List<Note> noteList;
    private List<Note> copyNoteList;

    Timer timer;
    private NoteListener noteListener;

    public NoteAdapter(List<Note> noteList , NoteListener noteListener) {
        this.noteList = noteList;
        this.noteListener = noteListener;
        copyNoteList = noteList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new MyViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.activity_item_container
                        ,parent
                        ,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Note currentNote = noteList.get(position);
        holder.setNote(currentNote);
        int currentPosition = position;
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteListener.onClickListener(currentNote , currentPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title , subtitle , dateTime ,description;
        LinearLayout layout;
        RoundedImageView imageView;
        ImageView image , uri , voice;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.container_title);
            subtitle = itemView.findViewById(R.id.container_subtitle);
            dateTime = itemView.findViewById(R.id.container_dateTime);
            layout = itemView.findViewById(R.id.container_layout);
            imageView = itemView.findViewById(R.id.container_photo);

            image = itemView.findViewById(R.id.container_image);
            uri = itemView.findViewById(R.id.container_uri);
            voice = itemView.findViewById(R.id.container_voice);

        }

        void setNote (Note note){

            if (note.getColor() != null){
                layout.setBackgroundColor(Color.parseColor(note.getColor()));
            }

            if (note.getTitle() != null){
                title.setVisibility(View.VISIBLE);
                title.setText(note.getTitle());
            }

            if (note.getSubtitle() != null){
                subtitle.setVisibility(View.VISIBLE);
                subtitle.setText(note.getTitle());
            }

            if (note.getSubtitle() != null){
                subtitle.setVisibility(View.VISIBLE);
                subtitle.setText(note.getTitle());
            }
            dateTime.setText(note.getDateTime());

            if (note.getImagePath() != null)
                image.setVisibility(View.VISIBLE);
            if (note.getWebLink() != null)
                uri.setVisibility(View.VISIBLE);

        }
    }

    public void filter (String searchQuery){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (searchQuery.trim().isEmpty())
                    noteList = copyNoteList;
                else{
                    List<Note> temp = new ArrayList<>();
                    for (Note note : copyNoteList){
                        if(note.getTitle().toLowerCase().contains(searchQuery.toLowerCase())){
                            temp.add(note);
                        }
                    }
                    noteList = temp;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        },500);
    }
    public void cancelTimer(){
        if(timer!=null)
            timer.cancel();
    }
}
