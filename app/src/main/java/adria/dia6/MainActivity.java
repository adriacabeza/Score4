package adria.dia6;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;


/**
 * Created by inlab on 04/07/2017.
 */

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_WRITE_PERMISSION = 288;
    private FirebaseUser user;
    private static final int REQUEST_PERMISSIONS_CODE = 1;
    private FirebaseAuth mAuth;
    private ListView listView;
    private ListAdapter listAdapter;
    FloatingActionButton mfloatingbutton;



    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.grups);
        requestPermission();
        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
        }

        else if(!isNetworkAvailable()) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("No Connection Found ");
            alertDialog.setMessage("Your device needs to be connected to run this app.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }

        else {
            DatabaseReference mPersonaRef = FirebaseDatabase.getInstance().getReference("persona");
            listView = (ListView) findViewById(R.id.listView);
            listAdapter = new ListAdapter(mPersonaRef, Persona.class, R.layout.my_list_item, this);
            listView.setAdapter(listAdapter);



            mfloatingbutton = (FloatingActionButton) findViewById(R.id.floatingActionButton3);
            mfloatingbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent2 = new Intent(MainActivity.this, CreateActivity.class);
                    MainActivity.this.startActivity(intent2);
                }
            });


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public void onBackPressed(){
        moveTaskToBack(true);}


    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        } else {

        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(listView.getChildAt(0) != null) {
            FancyShowCaseView fancyShowCaseView3 = new FancyShowCaseView.Builder(this)
                    .focusOn(listView.getChildAt(0).findViewById(R.id.score))
                    .focusShape(FocusShape.ROUNDED_RECTANGLE)
                    .title("Here you will find \n the global score of the user")
                    .build();

            FancyShowCaseView fancyShowCaseView1 = new FancyShowCaseView.Builder(this)
                    .focusOn(listView.getChildAt(0).findViewById(R.id.imageView))
                    .title("Press the image \n to score")
                    .build();

            FancyShowCaseView fancyShowCaseView2 = new FancyShowCaseView.Builder(this)
                    .focusOn(mfloatingbutton)
                    .title("Press the button to \n create a user")
                    .build();


            new FancyShowCaseQueue()
                    .add(fancyShowCaseView3)
                    .add(fancyShowCaseView1)
                    .add(fancyShowCaseView2)
                    .show();
        }

        else{   new FancyShowCaseView.Builder(this)
                .focusOn(mfloatingbutton)
                .title("Press the button to \n create a user")
                .build()
                .show();
               } return true;
    }

}








