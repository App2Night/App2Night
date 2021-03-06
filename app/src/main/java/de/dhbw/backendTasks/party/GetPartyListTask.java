package de.dhbw.backendTasks.party;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.IOException;

import de.dhbw.BackEndCommunication.RestBackendCommunication;
import de.dhbw.exceptions.BackendCommunicationException;
import de.dhbw.exceptions.IllegalKeyException;
import de.dhbw.exceptions.NetworkUnavailableException;
import de.dhbw.exceptions.NoTokenFoundException;
import de.dhbw.exceptions.RefreshTokenFailedException;
import de.dhbw.model.Party;
import de.dhbw.utils.GetPartyListSave;
import de.dhbw.utils.PropertyUtil;
import de.dhbw.utils.SettingsUtil;

/**
 * Created by Tobias Berner on 21.10.2016.
 */


/**
 * Dieser AsyncTask dient dazu eine Liste in Abhängigkeit von bestimmten Koordinaten abzufragen.
 * Der Task wird mit dem Konstruktor erzeugt und dann automatisch gestartet.
 */
public class GetPartyListTask extends AsyncTask<Void, Void, String> implements ApiPartyTask {

    private static boolean allreadyRunning = false;
    //Initialisert von PropertyUtil
    private String url;
    private final GetPartyList fragment;
    private final double latitude, longtitude;
    private float radius;


    public void setUrl(String urlParm) {
        url = urlParm;
    }

    public GetPartyListTask(GetPartyList fragment, double latitude, double longtitude) {
        this.fragment = fragment;
        this.latitude = latitude;
        this.longtitude = longtitude;
        try {
            this.radius =  Float.parseFloat(SettingsUtil.getInstance().getSetting("radius"));
        } catch (IllegalKeyException e) {
            this.radius = 100;
        }
        //Wenn bereits ein getPartyTask läuft, starte keinen neuen
        if (allreadyRunning)
            return;
        allreadyRunning=true;
        prepare();
    }

    private void prepare() {
        PropertyUtil.getInstance().init(this);
        this.execute();
    }

    private String buildUrl(){
        return url + "?lat=" + latitude + "&lon=" + longtitude + "&radius=" + radius;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
                return RestBackendCommunication.getInstance().getRequest(buildUrl());
        } catch (Exception e ){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        //Anzeigen, dass Task nicht mehr läuft
        allreadyRunning = false;
        if (result != null) {
            Party[] parties = new Gson().fromJson(result, Party[].class);
            //Speichern der aktuellsten Liste
            GetPartyListSave.getInstance().storeList(parties);
            fragment.onSuccessGetPartyList(parties);
        }else
            fragment.onFailGetPartyList();
    }
}

