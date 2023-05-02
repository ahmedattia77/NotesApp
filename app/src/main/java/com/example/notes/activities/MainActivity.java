package com.example.notes.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.notes.R;
import com.example.notes.adapters.NoteAdapter;
import com.example.notes.database.NoteDatabase;
import com.example.notes.databinding.ActivityMainBinding;
import com.example.notes.entities.Note;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static int ADD_REQUEST = 1;
    private ActivityMainBinding binding;
    private NoteAdapter noteAdapter;
    private List<Note> noteList;
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
        noteAdapter = new NoteAdapter(noteList);
        binding.mainRecycleView.setAdapter(noteAdapter);

        displayNotes();

    }

    private void displayNotes (){

        class getAllNotesAsyncTask extends AsyncTask<Void,Void, List<Note>>  {

            @Override
            protected List<Note> doInBackground(Void... voids) {

                return NoteDatabase.getInstance(getApplicationContext()).noteDao().getAllNotes();
            }

            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);
                if (noteList.isEmpty()){
                    noteList.addAll(notes);
                    noteAdapter.notifyDataSetChanged();
                }else{
                    noteList.add(0 ,notes.get(0));
                    noteAdapter.notifyItemInserted(0);
                }
                binding.mainRecycleView.smoothScrollToPosition(0);
            }
        }
        new getAllNotesAsyncTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_REQUEST && resultCode == resultCode){
            displayNotes();
        }
    }
}