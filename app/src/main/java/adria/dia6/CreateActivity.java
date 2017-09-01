package adria.dia6;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import de.hdodenhof.circleimageview.CircleImageView;


public class CreateActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    CircleImageView mImageView;
    String mCurrentPhotoPath;
    Button button;
    EditText name;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private  StorageReference storageRef = storage.getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create);


        button = (Button) findViewById(R.id.button);
        mImageView = (CircleImageView) findViewById(R.id.image_view);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = (EditText) findViewById(R.id.editText2);
                String mname = name.getText().toString();
                ArrayList<String> votadors = new ArrayList<String>();
                votadors.add("0");
                Persona persona = new Persona(mname,0, votadors);
                int length = name.getText().toString().length();

                if (length == 0){
                    Toast toast = Toast.makeText (getApplicationContext(), "Insert a name", Toast.LENGTH_SHORT);
                    toast.show();
                }

                else if(length > 13){
                    Toast toast = Toast.makeText(getApplicationContext(), "Insert a shorter name", Toast.LENGTH_SHORT);
                    toast.show();
                }

                else if (mCurrentPhotoPath == null) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Insert a pic", Toast.LENGTH_SHORT);
                    toast.show();

                } else {
                    FirebaseDatabase.getInstance().getReference("persona").child(mname).setValue(persona);
                    FirebaseDatabase.getInstance().getReference("persona").child(mname).child("votadors/0").setValue("0");

                    Uri file = Uri.fromFile(new File(mCurrentPhotoPath));
                    StorageReference personesRef = storageRef.child("images/"+mname);
                    UploadTask uploadTask = personesRef.putFile(file);

                    // Register observers to listen for when the download is done or if it fails
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            Toast toast = Toast.makeText(getApplicationContext(), "Something went wrong. Try again.", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        }
                    });


                    Intent intent3 = new Intent(CreateActivity.this, MainActivity.class);
                    CreateActivity.this.startActivity(intent3);
                }
            }
        });

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDialog();
            }

        });
    }
    private void startDialog() {
        final CharSequence[] items = { "Take Photo", "Choose from Gallery",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                        dispatchTakePictureIntent();

                } else if (items[item].equals("Choose from Gallery")) {
                        loadGallery();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();

    }


    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;

    }

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_PICK_IMAGE = 2 ;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
             return; }
            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(this, "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void loadGallery(){
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");
        Intent pickIntent= new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
        startActivityForResult(pickIntent, REQUEST_PICK_IMAGE);
    }




    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data) {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Glide.with(this).load(new File(mCurrentPhotoPath)).into(mImageView);
        }
        else if(requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK ){
           try {
               InputStream inputStream = getContentResolver().openInputStream(data.getData());
               Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
               mImageView.setImageBitmap(bitmap);

               mCurrentPhotoPath =getRealPathFromURI(data.getData());
           } catch (FileNotFoundException e) {
               e.printStackTrace();
           }
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
}


