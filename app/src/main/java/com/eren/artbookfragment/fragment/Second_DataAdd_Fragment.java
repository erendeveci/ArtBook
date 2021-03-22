package com.eren.artbookfragment.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.eren.artbookfragment.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class Second_DataAdd_Fragment extends Fragment {
    Bitmap bitmap;
    ImageView imageViewUpload;
    EditText editTextName, editTextPainterName, editTextYear;
    SQLiteDatabase database;
    String info = "";


    public Second_DataAdd_Fragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_data_add, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnSave = view.findViewById(R.id.btnSave);
        editTextName = view.findViewById(R.id.editTextName);
        editTextPainterName = view.findViewById(R.id.editTextPainterName);
        editTextYear = view.findViewById(R.id.editTextYear);
        imageViewUpload = view.findViewById(R.id.imageViewUplaod);

        imageViewUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewUploadOn(view);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSaveOn(view);
            }
        });


        if (getArguments() != null) {
            info = Second_DataAdd_FragmentArgs.fromBundle(getArguments()).getAboutSecond();


        } else {
            info = "new";

        }


        if (info.matches("new")) {
            editTextName.setText("");
            editTextPainterName.setText("");
            editTextYear.setText("");


        } else {
            int artId = Second_DataAdd_FragmentArgs.fromBundle(getArguments()).getNumberSecond();

            btnSave.setVisibility(View.INVISIBLE);


            try {
                database = getActivity().openOrCreateDatabase("Arts",Context.MODE_PRIVATE,null);

                Cursor cursor = database.rawQuery("SELECT * FROM arts WHERE id=?",new String[] {String.valueOf(artId)});
                int nameIX = cursor.getColumnIndex("artname");
                int painterNameIX=cursor.getColumnIndex("paintername");
                int yearIX = cursor.getColumnIndex("year");
                int imageIX= cursor.getColumnIndex("image");

                while(cursor.moveToNext()) {

                    editTextName.setText(cursor.getString(nameIX));
                    editTextPainterName.setText(cursor.getString(painterNameIX));
                    editTextYear.setText(cursor.getString(yearIX));

                    byte[] bytes =cursor.getBlob(imageIX);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    imageViewUpload.setImageBitmap(bitmap);
                }



                cursor.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }

    }


    public void imageViewUploadOn(View view) {

        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        } else {
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentToGallery, 2);

        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentToGallery, 2);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && data != null && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                imageViewUpload.setImageBitmap(bitmap);


            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }


    public void btnSaveOn(View view) {

        String paintName = editTextName.getText().toString();
        String painterName = editTextPainterName.getText().toString();
        String year = editTextYear.getText().toString();

        Bitmap smallImage = makeSmallerImage(bitmap, 300);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        smallImage.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
        byte[] byteArray = outputStream.toByteArray();

        try {


            database = getActivity().getApplicationContext().openOrCreateDatabase("Arts", Context.MODE_PRIVATE, null);
            database.execSQL("CREATE TABLE IF NOT EXISTS arts(id INTEGER PRIMARY KEY,artname VARCHAR , paintername VARCHAR , year VARCHAR,image BLOB)");

            String sqlString = "INSERT INTO arts(artname,paintername,year,image) VALUES (?,?,?,?)";
            SQLiteStatement statement = database.compileStatement(sqlString);
            statement.bindString(1, paintName);
            statement.bindString(2, painterName);
            statement.bindString(3, year);
            statement.bindBlob(4, byteArray);


            statement.execute();


        } catch (Exception e) {
            e.printStackTrace();
        }

        // finish

        NavDirections action = Second_DataAdd_FragmentDirections.actionDataAddToDataListMain();
        Navigation.findNavController(view).navigate(action);


        editTextName.setText("");
        editTextPainterName.setText("");
        editTextYear.setText("");

        Bitmap selectImageBitmap =BitmapFactory.decodeResource(getContext().getResources(),R.drawable.select_image);
        imageViewUpload.setImageBitmap(selectImageBitmap);

    }

    public Bitmap makeSmallerImage(Bitmap image, int maximumSize) {

        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;

        if (bitmapRatio > 1) {
            width = maximumSize;
            height = (int) (width / bitmapRatio);

        } else {
            height = maximumSize;
            width = (int) (height * bitmapRatio);

        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }

}