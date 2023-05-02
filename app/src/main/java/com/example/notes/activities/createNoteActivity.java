package com.example.notes.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notes.R;
import com.example.notes.adapters.NoteAdapter;
import com.example.notes.database.NoteDatabase;
import com.example.notes.databinding.ActivityCreateNoteBinding;
import com.example.notes.databinding.ActivityMainBinding;
import com.example.notes.entities.Note;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class createNoteActivity extends AppCompatActivity {

    private ActivityCreateNoteBinding binding;
    private String chosenColor = "#333333";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateNoteBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.createBack.setOnClickListener((v) -> onBackPressed() );

        binding.createDateTime.setText(new SimpleDateFormat("EEE, dd MMMM yyyy HH:mm a",
                Locale.getDefault())
                .format(new Date()));

        binding.createDone.setOnClickListener((v) -> { addNote(); });

        createMiscellaneous();
//        setSubtitleViewColor();


    }

    private void addNote (){

        Note note  = new Note();

        if (!binding.createTitle.getText().toString().trim().isEmpty())
            note.setTitle(binding.createTitle.getText().toString());

        if (!binding.createSubtitle.getText().toString().trim().isEmpty())
            note.setSubtitle(binding.createSubtitle.getText().toString());

        if (!binding.createDescription.getText().toString().trim().isEmpty())
            note.setDescription(binding.createDescription.getText().toString());

        if (!binding.createDateTime.getText().toString().trim().isEmpty())
            note.setDateTime(binding.createDateTime.getText().toString());

        note.setColor(chosenColor);

        if(!checkFields()){
            Toast.makeText(this, "you can't leave all fields empty", Toast.LENGTH_SHORT).show();
            return;
        }



        class saveNotesAsyncTask extends AsyncTask<Void,Void,Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                NoteDatabase.getInstance(getApplicationContext()).noteDao().insertNote(note);
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                Intent intent = new Intent();
                setResult(RESULT_OK,intent);
                finish();
            }
        }
        new saveNotesAsyncTask().execute();

    }
    private boolean checkFields(){
        if (binding.createTitle.getText().toString().trim().isEmpty()&&
                binding.createSubtitle.getText().toString().trim().isEmpty()&&
                binding.createDescription.getText().toString().trim().isEmpty())
            return false;
        return true;
    }

    private void autoFillEmptyFields (){
        if (!binding.createDescription.getText().toString().trim().isEmpty()&&
                binding.createTitle.getText().toString().trim().isEmpty()) {
            String copyDescription = binding.createDescription.getText().toString();
            String titleFromDescription = copyDescription.substring(0,copyDescription.indexOf(' '));
            binding.createTitle.setText(titleFromDescription);
        }
        else if(binding.createDescription.getText().toString().trim().isEmpty()&&
                !binding.createTitle.getText().toString().trim().isEmpty())
            binding.createDescription.setText(binding.createTitle.getText());

    }
    private void createMiscellaneous (){
        LinearLayout miscellaneous = findViewById(R.id.miscellaneous);
        BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(miscellaneous);
        miscellaneous.findViewById(R.id.miscellaneous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState() != bottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.setState(bottomSheetBehavior.STATE_EXPANDED);
                }else{
                    bottomSheetBehavior.setState(bottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        final ImageView imageViewColor1 = miscellaneous.findViewById(R.id.miscellaneous_color1);
        final ImageView imageViewColor2 = miscellaneous.findViewById(R.id.miscellaneous_color2);
        final ImageView imageViewColor3 = miscellaneous.findViewById(R.id.miscellaneous_color3);
        final ImageView imageViewColor4 = miscellaneous.findViewById(R.id.miscellaneous_color4);
        final ImageView imageViewColor5 = miscellaneous.findViewById(R.id.miscellaneous_color5);

        miscellaneous.findViewById(R.id.viewColor1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosenColor = "#333333";
                imageViewColor1.setImageResource(R.drawable.ic_done);
                imageViewColor2.setImageResource(0);
                imageViewColor3.setImageResource(0);
                imageViewColor4.setImageResource(0);
                imageViewColor5.setImageResource(0);
                setSubtitleViewColor();
            }
        });

        miscellaneous.findViewById(R.id.viewColor2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosenColor = "#673AB7";
                imageViewColor1.setImageResource(0);
                imageViewColor2.setImageResource(R.drawable.ic_done);
                imageViewColor3.setImageResource(0);
                imageViewColor4.setImageResource(0);
                imageViewColor5.setImageResource(0);
                setSubtitleViewColor();
            }
        });

        miscellaneous.findViewById(R.id.viewColor3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosenColor = "#FF4842";
                imageViewColor1.setImageResource(0);
                imageViewColor2.setImageResource(0);
                imageViewColor3.setImageResource(R.drawable.ic_done);
                imageViewColor4.setImageResource(0);
                imageViewColor5.setImageResource(0);
                setSubtitleViewColor();
            }
        });

        miscellaneous.findViewById(R.id.viewColor4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosenColor = "#3A52Fc";
                imageViewColor1.setImageResource(0);
                imageViewColor2.setImageResource(0);
                imageViewColor3.setImageResource(0);
                imageViewColor4.setImageResource(R.drawable.ic_done);
                imageViewColor5.setImageResource(0);
                setSubtitleViewColor();

            }
        });

        miscellaneous.findViewById(R.id.viewColor5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosenColor = "#000000";
                imageViewColor1.setImageResource(0);
                imageViewColor2.setImageResource(0);
                imageViewColor3.setImageResource(0);
                imageViewColor4.setImageResource(0);
                imageViewColor5.setImageResource(R.drawable.ic_done);
                setSubtitleViewColor();
            }
        });

    }

    private void setSubtitleViewColor(){
        GradientDrawable gradientDrawable = (GradientDrawable) binding.createView.getBackground();
        gradientDrawable.setColor(Color.parseColor(chosenColor));
    }
}