package com.example.notes.activities;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class createNoteActivity extends AppCompatActivity {

    private static final int IMAGE_REQUEST_PERMISSION_CODE = 1;
    private static final int SELECT_IMAGE_REQUEST_CODE = 2;
    private String selectedImagePath;
    private ActivityCreateNoteBinding binding;
    private String chosenColor = "#333333";
    private AlertDialog dialogAddWebsite;
    private AlertDialog dialogDelete;
    Note sentNote;

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

        selectedImagePath = "";
        createMiscellaneous();
        setSubtitleViewColor();

        if (getIntent().getBooleanExtra("EDIT/VIEW_REQUEST" , false)){
            binding.createLayoutAddUri.setVisibility(View.VISIBLE);
            sentNote = (Note) getIntent().getSerializableExtra("note");
            editViewNote();
        }

    }

    private void editViewNote() {
        binding.createTitle.setText(sentNote.getTitle());
        binding.createSubtitle.setText(sentNote.getSubtitle());
        binding.createDescription.setText(sentNote.getDescription());
        binding.createDateTime.setText(sentNote.getDateTime());

        if(sentNote.getImagePath() != null && !sentNote.getImagePath().trim().isEmpty()){
            binding.createPhoto.setImageBitmap(BitmapFactory.decodeFile(sentNote.getImagePath()));
            binding.createPhoto.setVisibility(View.VISIBLE);
            selectedImagePath = sentNote.getImagePath();
        }

        if(sentNote.getWebLink() != null && !sentNote.getWebLink().trim().isEmpty()){
            binding.createUri.setText(sentNote.getWebLink());
            binding.createUri.setVisibility(View.VISIBLE);
        }

    }

    private void addNote (){

        Note note = new Note();

        if(!checkFields()){
            Toast.makeText(this, "you can't leave all fields empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!binding.createTitle.getText().toString().trim().isEmpty())
            note.setTitle(binding.createTitle.getText().toString());

        if (!binding.createSubtitle.getText().toString().trim().isEmpty())
            note.setSubtitle(binding.createSubtitle.getText().toString());

        if (!binding.createDescription.getText().toString().trim().isEmpty())
            note.setDescription(binding.createDescription.getText().toString());

        if (!binding.createDateTime.getText().toString().trim().isEmpty())
            note.setDateTime(binding.createDateTime.getText().toString());

        note.setColor(chosenColor);

        if (!selectedImagePath.isEmpty())
            note.setImagePath(selectedImagePath);

        if(!binding.createUri.getText().toString().trim().isEmpty()){
            note.setWebLink(binding.createUri.getText().toString());
        }

        if (sentNote != null){
            note.setId(sentNote.getId());
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
                    bottomSheetBehavior.setState(STATE_COLLAPSED);
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

        if (sentNote != null && sentNote.getColor() != null &&!sentNote.getColor().trim().isEmpty()){
            switch (sentNote.getColor()){
                case "673AB7":
                    miscellaneous.findViewById(R.id.viewColor2).performClick();
                    break;

                case "#FF4842":
                    miscellaneous.findViewById(R.id.viewColor3).performClick();
                    break;

                case "#3A52Fc":
                    miscellaneous.findViewById(R.id.viewColor4).performClick();
                    break;

                case "#000000":
                    miscellaneous.findViewById(R.id.viewColor5).performClick();
                    break;
            }
        }

        miscellaneous.findViewById(R.id.create_addPhotoLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(STATE_COLLAPSED);
                if(ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
                )!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(
                            createNoteActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            IMAGE_REQUEST_PERMISSION_CODE
                    );
                }else
                    selectImage();

            }
        });

        miscellaneous.findViewById(R.id.create_addUriLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView input = findViewById(R.id.miscellaneous_inputLink_et);
                input.requestFocus();
                bottomSheetBehavior.setState(STATE_COLLAPSED);
                addWebsiteNoteAlertDialog();
            }

        });

//        if (sentNote != null) {
//            miscellaneous.findViewById(R.id.miscellaneous_deleteLayout).setVisibility(View.VISIBLE);
            miscellaneous.findViewById(R.id.miscellaneous_deleteLayout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetBehavior.setState(STATE_COLLAPSED);
                    showDeleteNoteAlertDialog();
                }
            });
//        }
    }

    private void showDeleteNoteAlertDialog (){
        if (dialogDelete == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(createNoteActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_delete_note,
                    (ViewGroup) findViewById(R.id.layout_deleteNoteContainer)
            );

            builder.setView(view);
            dialogDelete = builder.create();

            if (dialogDelete.getWindow() != null){
                dialogDelete.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            view.findViewById(R.id.deleteNote_done_tv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    @SuppressLint("StaticFieldLeak")
                    class DeleteNoteTask extends AsyncTask<Void,Void,Void>{
                        @Override
                        protected Void doInBackground(Void... voids) {
                            NoteDatabase.getInstance(getApplicationContext())
                                    .noteDao().deleteNote(sentNote);
                            return null;
                        }
                        @Override
                        protected void onPostExecute(Void unused) {
                            super.onPostExecute(unused);
                            Intent intent = new Intent();
                            intent.putExtra("isDeletedNote" , true);
                            setResult(RESULT_OK , intent);
                            finish();
                        }
                    }
                    new DeleteNoteTask().execute();
                }
            });

            view.findViewById(R.id.deleteNote_cancel_tv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogDelete.dismiss();
                }
            });
        }
        dialogDelete.show();
    }
    private void addWebsiteNoteAlertDialog(){

        if (dialogAddWebsite == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(createNoteActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_add_uri
                    ,(ViewGroup) findViewById(R.id.addUri_layoutContainer)
            );

            builder.setView(view);
            dialogAddWebsite = builder.create();

            if (dialogAddWebsite.getWindow() != null){
                dialogAddWebsite.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }


            EditText input = view.findViewById(R.id.addUri_input_et);
            input.requestFocus();
            view.findViewById(R.id.addUri_add_tv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(input.getText().toString().trim().isEmpty()){
                        Toast.makeText(createNoteActivity.this, "Add uri", Toast.LENGTH_SHORT).show();
                    }else if (!Patterns.WEB_URL.matcher(input.getText().toString()).matches()){
                        Toast.makeText(createNoteActivity.this, "un valid uri", Toast.LENGTH_SHORT).show();
                    }else {
                        binding.createLayoutAddUri.setVisibility(View.VISIBLE);
                        binding.createUri.setText(input.getText().toString());
                        dialogAddWebsite.dismiss();
                    }
                }
            });

            view.findViewById(R.id.addUri_cancel_tv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogAddWebsite.dismiss();
                }
            });
        }
        dialogAddWebsite.show();
    }


    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent ,SELECT_IMAGE_REQUEST_CODE);
    }

    private void setSubtitleViewColor(){
        GradientDrawable gradientDrawable = (GradientDrawable) binding.createView.getBackground();
        gradientDrawable.setColor(Color.parseColor(chosenColor));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == IMAGE_REQUEST_PERMISSION_CODE && grantResults.length > 0){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                selectImage();
            }else {
                Toast.makeText(this, "Permissions Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SELECT_IMAGE_REQUEST_CODE && resultCode == RESULT_OK){
            if(data != null){
                Uri imageUri = data.getData();
                if (imageUri != null){
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        Bitmap imageBitmap = BitmapFactory.decodeStream(inputStream);
                        binding.createPhoto.setImageBitmap(imageBitmap);
                        binding.createPhoto.setVisibility(View.VISIBLE);
                        selectedImagePath = getImagePath(imageUri);

                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private String getImagePath (Uri uri){
        String path;
        Cursor cursor = getContentResolver().
                query(uri , null ,null ,null ,null);
        if (cursor == null){
            path = uri.getPath();
        }else{
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            path = cursor.getString(index);
            cursor.close();
        }

        return path;
    }

}