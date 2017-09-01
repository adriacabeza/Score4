package adria.dia6;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


/**
 * Created by usuario on 09/08/2017.
 */

public class ListAdapter extends FirebaseListAdapter<Persona> {
    private final Context context;
    int score = 0;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://score-game-52da4.appspot.com/images/");
    private FirebaseUser user;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference personaRef = database.getReference("persona");



    public ListAdapter(Query mRef, Class<Persona> mPersonaClass, int mLayout, Activity activity) {
        super(mRef, mPersonaClass, mLayout, activity);
        this.context = activity;
    }

        @Override
        protected void populateView ( final View v, final Persona model){
            final String name = model.name;

            TextView textView = (TextView) v.findViewById(R.id.textView);
            textView.setText(name);

            TextView textView1 = (TextView) v.findViewById(R.id.score);
            textView1.setText("Score: " + String.valueOf(model.puntuation));
            final TextView textView2 = (TextView) v.findViewById(R.id.scorestars);


            final TextView puntuation = (TextView) v.findViewById(R.id.score);
            final RatingBar mratingBar = (RatingBar) v.findViewById(R.id.ratingBar);

            mratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    textView2.setText(String.valueOf(Math.round((rating)*25)));
                    score = Math.round((rating)*25);
                }
            });

         ImageView imageView = (ImageView) v.findViewById(R.id.imageView);

                    StorageReference imageRef = storageRef.child(name);

                    Glide.with(context).using(new FirebaseImageLoader()).load(imageRef).asBitmap().skipMemoryCache(false).into(imageView);


            imageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(final View v) {

                    user = FirebaseAuth.getInstance().getCurrentUser();
                    String a = user.getDisplayName();


                    if(!model.searchVotadors(a)) {
                        puntuation.setText(String.valueOf(Math.round(score)));


                        if (score != -1) {
                            personaRef.child(name).child("puntuation").setValue(Math.round((model.puntuation + score)));
                            personaRef.child(name).child("votadors").child(String.valueOf(model.votadors.size())).setValue(a);
                            personaRef.child(name).child("votadors").child(String.valueOf(model.votadors.size()+1)).setValue(a);

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("CONGRATULATIONS! You've just scored " + name).setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //do things
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                            textView2.setText(String.valueOf(0));
                            mratingBar.setRating(0);
                            score = -1;
                        }
                    }

                    else{
                        Toast toast = Toast.makeText(context, "ERROR. You've already scored " + name, Toast.LENGTH_SHORT);
                        toast.show();
                        textView2.setText(String.valueOf(0));
                        mratingBar.setRating(0);

                    }

                }
//
            });
        }

}
