package de.dhbw.app2night;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.w3c.dom.Text;

import java.util.Calendar;

import de.dhbw.backendTasks.party.AdressValidate;
import de.dhbw.backendTasks.party.AdressValidateTask;
import de.dhbw.backendTasks.party.PostParty;
import de.dhbw.backendTasks.party.PostPartyTask;
import de.dhbw.model.Party;
import de.dhbw.model.PartyDisplay;

/**
 * Created by Flo on 31.10.2016.
 */
public class AddEventFragment extends Fragment implements View.OnTouchListener, View.OnClickListener,
       TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener, PostParty, AdressValidate{
    EditText editTextPartyName, editTextStreetName, editTextHouseNumber, editTextZipCode, editTextCityName, editTextCountryName, editTextDescription;
    TextInputLayout tilPartyName, tilStreetName, tilHouseNumber, tilZipcode, tilCityName, tilCountryName, tilDescription;
    TextView textDate, textTime;
    ScrollView scrollViewAddEvent;
    Spinner spinnerPartyType, spinnerMusicGenre;
    View rootView;
    String partyDate, partyTime, partyDateTime;
    DatePickerDialog dpd;
    TimePickerDialog tpd;
    Button okButton;


    public AddEventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_addevent, container, false);

        initializeViews();

        // Inflate the layout for this fragment
        return rootView;
    }

    private void initializeViews() {
        editTextDescription = (EditText) rootView.findViewById(R.id.input_description);
        editTextDescription.setOnTouchListener(this);
        textDate = (TextView) rootView.findViewById(R.id.input_party_date);
        textDate.setOnClickListener(this);
        textTime = (TextView) rootView.findViewById(R.id.input_party_time);
        textTime.setOnClickListener(this);

        scrollViewAddEvent = (ScrollView) rootView.findViewById(R.id.addevent_scrollview_main);

        spinnerPartyType = (Spinner) rootView.findViewById(R.id.spinner_party_type);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterPartyType = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.party_type, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterPartyType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerPartyType.setAdapter(adapterPartyType);

        spinnerMusicGenre = (Spinner) rootView.findViewById(R.id.spinner_music_genre);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterMusicGenre = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.music_genre, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterMusicGenre.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerMusicGenre.setAdapter(adapterMusicGenre);

        Calendar now = Calendar.getInstance();
        dpd = DatePickerDialog.newInstance(
                AddEventFragment.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        tpd = TimePickerDialog.newInstance(
                AddEventFragment.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );

        okButton = (Button) rootView.findViewById(R.id.addevent_button_ok);
        okButton.setOnClickListener(this);

        editTextPartyName = (EditText) rootView.findViewById(R.id.input_party_name);
        editTextStreetName = (EditText) rootView.findViewById(R.id.input_street_name);
        editTextHouseNumber = (EditText) rootView.findViewById(R.id.input_house_number);
        editTextZipCode = (EditText) rootView.findViewById(R.id.input_zipcode);
        editTextCityName = (EditText) rootView.findViewById(R.id.input_city_name);
        editTextCountryName = (EditText) rootView.findViewById(R.id.input_country_name);

        tilPartyName = (TextInputLayout) rootView.findViewById(R.id.input_layout_party_name);
        tilStreetName = (TextInputLayout) rootView.findViewById(R.id.input_layout_street_name);
        tilHouseNumber = (TextInputLayout) rootView.findViewById(R.id.input_layout_house_number);
        tilZipcode = (TextInputLayout) rootView.findViewById(R.id.input_layout_zipcode);
        tilCityName = (TextInputLayout) rootView.findViewById(R.id.input_layout_city_name);
        tilCountryName = (TextInputLayout) rootView.findViewById(R.id.input_layout_country_name);
        tilDescription = (TextInputLayout) rootView.findViewById(R.id.input_layout_description);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        scrollViewAddEvent.requestDisallowInterceptTouchEvent(true);

        int action = motionEvent.getActionMasked();

        switch (action) {
            case MotionEvent.ACTION_UP:
                scrollViewAddEvent.requestDisallowInterceptTouchEvent(false);
                break;
        }

        return false;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch(id){
            case R.id.input_party_date:
                dpd.show(getFragmentManager(), "DatePickerDialog");
                break;
            case R.id.input_party_time:
                tpd.show(getFragmentManager(), "TimePickerDialog");
                break;
            case R.id.addevent_button_ok:
                runPartyCheck();
        }
    }

    /**
     * Methode ueberprueft die Eingabefelder ->Party versenden oder Nutzer zu Korrekturen auffordern
     */
    private void runPartyCheck() {
        PartyDisplay partyDisplay = new PartyDisplay();
        Boolean correctInput = true;

        if(!editTextPartyName.getText().toString().trim().equals("")) {
            editTextPartyName.setError(null);
            tilPartyName.setError(null);
            partyDisplay.setPartyName(editTextPartyName.getText().toString());
        }else{
            editTextPartyName.setError("\"" + getString(R.string.party_name) + "\"" + " darf nicht leer sein");
            tilPartyName.setError(getString(R.string.party_name));
            correctInput = false;
        }

        if(!editTextStreetName.getText().toString().trim().equals("")) {
            editTextStreetName.setError(null);
            tilStreetName.setError(null);
            partyDisplay.setStreetName(editTextStreetName.getText().toString());
        }else{
            editTextStreetName.setError("\"" + getString(R.string.street_name) + "\"" + " darf nicht leer sein");
            tilStreetName.setError(getString(R.string.street_name));
            correctInput = false;
        }

        if(!editTextHouseNumber.getText().toString().trim().equals("")) {
            editTextHouseNumber.setError(null);
            tilHouseNumber.setError(null);
            partyDisplay.setHouseNumber(editTextHouseNumber.getText().toString());
        }else{
            editTextHouseNumber.setError("\"" + getString(R.string.house_number) + "\"" + " darf nicht leer sein");
            tilHouseNumber.setError(getString(R.string.house_number));
            correctInput = false;
        }

        if(!editTextZipCode.getText().toString().trim().equals("")) {
            editTextZipCode.setError(null);
            tilZipcode.setError(null);
            partyDisplay.setZipcode(editTextZipCode.getText().toString());
        }else{
            editTextZipCode.setError("\"" + getString(R.string.zipcode) + "\"" + " darf nicht leer sein");
            tilZipcode.setError(getString(R.string.zipcode));
            correctInput = false;
        }

        if(!editTextCityName.getText().toString().trim().equals("")) {
            editTextCityName.setError(null);
            tilCityName.setError(null);
            partyDisplay.setCityName(editTextCityName.getText().toString());
        }else{
            editTextCityName.setError("\"" + getString(R.string.city_name) + "\"" + " darf nicht leer sein");
            tilCityName.setError(getString(R.string.city_name));
            correctInput = false;
        }

        if(!editTextCountryName.getText().toString().trim().equals("")) {
            editTextCountryName.setError(null);
            tilCountryName.setError(null);
            partyDisplay.setCountryName(editTextCountryName.getText().toString());
        }else{
            editTextCountryName.setError("\"" + getString(R.string.country_name) + "\"" + " darf nicht leer sein");
            tilCountryName.setError(getString(R.string.country_name));
            correctInput = false;
        }

        if(!textDate.getText().equals("")) {
            textDate.setError(null);
            textTime.setError(null);
            if(!textTime.getText().equals("")) {
                partyDateTime = partyDate + partyTime;
                partyDisplay.setPartyDate(partyDateTime);
            }else{
                textTime.setError("\"" + getString(R.string.party_time) + "\"" + " darf nicht leer sein");
                correctInput = false;
            }
        }else{
            textDate.setError("\"" + getString(R.string.party_date) + "\"" + " darf nicht leer sein");
            if(textTime.getText().equals("")){
                textTime.setError("\"" + getString(R.string.party_time) + "\"" + " darf nicht leer sein");
            }
            correctInput = false;
        }

        //TODO: Verbesserung durchfuehren, nur zum Testen
        if(spinnerPartyType.getSelectedItemPosition()!= 0) {
            ((TextView)spinnerPartyType.getChildAt(0)).setError(null);
            partyDisplay.setPartyType(spinnerPartyType.getSelectedItemPosition() - 1);
        }else{
            ((TextView)spinnerPartyType.getChildAt(0)).setError("\"" + getString(R.string.party_type) + "\"" + " darf nicht leer sein");
            correctInput = false;
        }

        if(spinnerMusicGenre.getSelectedItemPosition()!= 0) {
            ((TextView)spinnerMusicGenre.getChildAt(0)).setError(null);
            partyDisplay.setMusicGenre(spinnerMusicGenre.getSelectedItemPosition() - 1);
        }else{
            ((TextView)spinnerMusicGenre.getChildAt(0)).setError("\"" + getString(R.string.music_genre) + "\"" + " darf nicht leer sein");
            correctInput = false;
        }

        if(!editTextDescription.getText().toString().trim().equals("")) {
            editTextDescription.setError(null);
            tilDescription.setError(null);
            partyDisplay.setDescription(editTextDescription.getText().toString());
        }else{
            editTextDescription.setError("\"" + getString(R.string.description) + "\"" + " darf nicht leer sein");
            tilDescription.setError(getString(R.string.description));
            correctInput = false;
        }

        if(correctInput) {
            new AdressValidateTask(partyDisplay,this);
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        //Speichern des erfassten Datums fuer Party und setzen in Eingabefeld
        String month = Integer.toString(monthOfYear+1);
        String day = Integer.toString(dayOfMonth);

        if(monthOfYear+1<10) month = "0" + (monthOfYear+1);
        if(dayOfMonth<10) day = "0" + dayOfMonth;
        partyDate = year + "-" + month + "-" + day;
        textDate.setText(day + "." + month + "." + year);
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minuteInt, int second) {
        //Speichern der erfassten Zeit fuer Party und setzen in Eingabefeld
        String hour = Integer.toString(hourOfDay);
        String minute = Integer.toString(minuteInt);
        if(hourOfDay <10) hour ="0"+ hourOfDay;
        if(minuteInt<10) minute="0"+minuteInt;
        partyTime = "T" + hour + ":" + minute + ":" + "00.000Z";
        textTime.setText(hour + ":" + minute);
    }

    @Override
    public void onSuccessPostParty(Party party) {

    }

    @Override
    public void onFailPostParty(PartyDisplay party) {

    }

    @Override
    public void onSuccessAdressValidate(PartyDisplay partyDisplay) {
        new PostPartyTask(this, partyDisplay);
    }


    @Override
    public void onFailAdressValidate(PartyDisplay partyDisplay) {

    }
}
