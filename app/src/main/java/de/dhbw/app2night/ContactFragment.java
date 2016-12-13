package de.dhbw.app2night;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by robin on 24.11.2016.
 */

public class ContactFragment extends Fragment {

    //Variablen
    Button einreichen;
    EditText name, mail, message;
    FloatingActionButton fab_plus, fab_facebook, fab_twitter, fab_googleplus, fab_vimeo, fab_instagram;
    Animation FabOpen,FabClose,FabRClockwise, FabRAntiClockwise;
    boolean isOpen = false;

    /**
     * Wird aufgerufen sobald die View erstellt wird
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contact, container, false);

        //Zuweisungen der EditTexts
        name = (EditText) v.findViewById(R.id.contact_name);
        mail = (EditText) v.findViewById(R.id.contact_mail);
        message = (EditText) v.findViewById(R.id.contact_message);


        // Button zum verschicken der Mail mit OnClickListener
        einreichen = (Button) v.findViewById(R.id.contact_send);
        einreichen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "app2night@email.adress" });
                intent.putExtra(Intent.EXTRA_SUBJECT, "Kontaktformular");
                intent.putExtra(Intent.EXTRA_TEXT, "Von: " + name.getText() + "\n" + "Mail: " + mail.getText() + "\n" + "Nachricht: " + message.getText());
                startActivity(Intent.createChooser(intent, "E-Mail client wählen: "));
            }
        });


        //Floating Action Buttons
        fab_plus = (FloatingActionButton) v.findViewById(R.id.fab_plus);
        fab_facebook = (FloatingActionButton) v.findViewById(R.id.fab_facebook);
        fab_twitter = (FloatingActionButton) v.findViewById(R.id.fab_twitter);
        fab_googleplus = (FloatingActionButton) v.findViewById(R.id.fab_googleplus);
        fab_vimeo = (FloatingActionButton) v.findViewById(R.id.fab_vimeo);
        fab_instagram = (FloatingActionButton) v.findViewById(R.id.fab_instagram);

        //Animationen laden
        FabOpen = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_open);
        FabClose = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_close);
        FabRClockwise = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_clockwise);
        FabRAntiClockwise = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_anticlockwise);

        //OnClickListener, sodass die Buttons richtig animiert werden und anklickbar bzw. nicht anklickbar sind.
        fab_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOpen)
                {
                    fab_facebook.startAnimation(FabClose);
                    fab_twitter.startAnimation(FabClose);
                    fab_googleplus.startAnimation(FabClose);
                    fab_vimeo.startAnimation(FabClose);
                    fab_instagram.startAnimation(FabClose);
                    fab_plus.startAnimation(FabRAntiClockwise);
                    fab_facebook.setClickable(false);
                    fab_twitter.setClickable(false);
                    fab_googleplus.setClickable(false);
                    fab_vimeo.setClickable(false);
                    fab_instagram.setClickable(false);
                    isOpen = false;

                }else
                {
                    fab_facebook.startAnimation(FabOpen);
                    fab_twitter.startAnimation(FabOpen);
                    fab_googleplus.startAnimation(FabOpen);
                    fab_vimeo.startAnimation(FabOpen);
                    fab_instagram.startAnimation(FabOpen);
                    fab_plus.startAnimation(FabRClockwise);
                    fab_facebook.setClickable(true);
                    fab_twitter.setClickable(true);
                    fab_googleplus.setClickable(true);
                    fab_vimeo.setClickable(true);
                    fab_instagram.setClickable(true);
                    isOpen = true;
                }
            }
        });

        return v;
    }
}
