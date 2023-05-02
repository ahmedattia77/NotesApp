package com.example.notes.adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.R;
import com.example.notes.entities.Note;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.MyViewHolder>{
    private List<Note> noteList;

    public NoteAdapter(List<Note> noteList) {
        this.noteList = noteList;
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
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.container_title);
            subtitle = itemView.findViewById(R.id.container_subtitle);
            dateTime = itemView.findViewById(R.id.container_dateTime);
            layout = itemView.findViewById(R.id.container_layout);

        }

        void setNote (Note note){

            title.setText(note.getTitle());
            subtitle.setText(note.getSubtitle());
            dateTime.setText(note.getDateTime());

//            if (!note.getColor().trim().isEmpty()){
//            GradientDrawable gradientDrawable = (GradientDrawable) layout.getBackground();
//            gradientDrawable.setColor(Color.parseColor(note.getColor()));
//            }

        }
    }
}
