package com.example.notes.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

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
        if (requestCode == ADD_REQUEST && resultCode == resultCode){
            displayNotes(ADD_REQUEST , false);
        }else if (requestCode == UPDATE_REQUEST && resultCode == RESULT_OK){
            if (data != null)
                displayNotes(UPDATE_REQUEST , data.getBooleanExtra("isDeletedNote" , false));
        }
    }
}