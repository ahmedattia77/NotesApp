package com.example.notes.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.notes.Listeners.NoteListener;
import com.example.notes.R;
import com.example.notes.adapters.NoteAdapter;
import com.example.notes.database.NoteDatabase;
import com.example.notes.databinding.ActivityMainBinding;
import com.example.notes.entities.Note;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteListener {

    public static int ADD_REQUEST = 1;
    public static int UPDATE_REQUEST = 2;
    public static int SHOW_REQUEST = 3;
    private ActivityMainBinding binding;
    private NoteAdapter noteAdapter;
    private List<Note> noteList;
    private int noteClickPosition = -1;
    private  AlertDialog aboutUsalertDialog;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private boolean  nightMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        binding.mainAdd.setOnClickListener ((v)-> {
            startActivityForResult(new Intent(getApplicationContext() , createNoteActivity.class),ADD_REQUEST);
        });

        binding.mainRecycleView.setLayoutManager(new StaggeredGridLayoutManager(
                2, StaggeredGridLayoutManager.VERTICAL));

        noteList = new ArrayList<>();
        noteAdapter = new NoteAdapter(noteList , this);
        binding.mainRecycleView.setAdapter(noteAdapter);
        displayNotes(SHOW_REQUEST , false);

        binding.mainAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext() , createNoteActivity.class),ADD_REQUEST);
            }
        });

        binding.mainAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext() , createNoteActivity.class);
                intent.putExtra("addImage" , true);
                startActivityForResult(intent , UPDATE_REQUEST);
            }
        });
        binding.mainAddWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext() , createNoteActivity.class);
                intent.putExtra("addUri" , true);
                startActivityForResult(intent , UPDATE_REQUEST);
            }
        });

        binding.mainSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                noteAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {

                noteAdapter.filter(s.toString());
            }
        });

        binding.mainAbout.setOnClickListener((v) -> aboutUsNoteAlertDialog());

        initialDarkLightMode();
        binding.mainSwitch.setOnClickListener((v) -> darkLightMode());

    }

    private void initialDarkLightMode (){
        preferences = getSharedPreferences("mode" , Context.MODE_PRIVATE);
        nightMode = preferences.getBoolean("night" , false);

        if (nightMode)
            binding.mainSwitch.setChecked(true);
    }

    private void darkLightMode(){
        Toast.makeText(this, "switched", Toast.LENGTH_SHORT).show();
        if (nightMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            editor = preferences.edit();
            editor.putBoolean("night" , false);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            editor = preferences.edit();
            editor.putBoolean("night" , true);
        }
        editor.apply();
    }

    private void aboutUsNoteAlertDialog(){

        if (aboutUsalertDialog == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.about_us
                    ,(ViewGroup) findViewById(R.id.about)
            );

            builder.setView(view);
            aboutUsalertDialog = builder.create();

            if (aboutUsalertDialog.getWindow() != null){
                aboutUsalertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }


            aboutUsalertDialog.show();
            aboutUsalertDialog = null;

    }
    }

    @Override
    public void onClickListener(Note note, int position) {
        noteClickPosition = position;
        Intent intent = new Intent(getApplicationContext() , createNoteActivity.class);
        intent.putExtra("EDIT/VIEW_REQUEST" , true);
        intent.putExtra("note" , note);
        startActivityForResult(intent , UPDATE_REQUEST);

    }

    private void displayNotes (final int requestCode , final boolean isNoteDeleted){

        @SuppressLint("staticFieldLeak")
        class getAllNotesAsyncTask extends AsyncTask<Void,Void, List<Note>>  {

            @Override
            protected List<Note> doInBackground(Void... voids) {

                return NoteDatabase.getInstance(getApplicationContext()).noteDao().getAllNotes();
            }

            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);
                if (requestCode == SHOW_REQUEST){
                    noteList.addAll(notes);
                    noteAdapter.notifyDataSetChanged();
                }else if (requestCode == ADD_REQUEST){
                    noteList.add(0 , notes.get(0));
                    noteAdapter.notifyItemInserted(0);
                }else if (requestCode == UPDATE_REQUEST){
                    noteList.remove(noteClickPosition);

                    if (isNoteDeleted){
                        noteAdapter.notifyItemRemoved(noteClickPosition);
                    }else{
                        noteList.add(noteClickPosition , notes.get(noteClickPosition));
                        noteAdapter.notifyItemChanged(noteClickPosition);
                    }
                }
            }
        }
        new getAllNotesAsyncTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_REQUEST && resultCode == RESULT_OK){
            displayNotes(ADD_REQUEST , false);
        }else if (requestCode == UPDATE_REQUEST && resultCode == RESULT_OK){
            if (data != null)
                displayNotes(UPDATE_REQUEST , data.getBooleanExtra("isDeletedNote" , false));
        }
    }
}