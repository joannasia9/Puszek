package com.puszek.jm.puszek.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.puszek.jm.puszek.LoginActivity;
import com.puszek.jm.puszek.MainMenuActivity;
import com.puszek.jm.puszek.R;
import com.puszek.jm.puszek.helpers.DialogManager;
import com.puszek.jm.puszek.models.APIClient;
import com.puszek.jm.puszek.models.ApiInterface;
import com.puszek.jm.puszek.models.WasteType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Receiver extends BroadcastReceiver{
    public static final int NOTIFICATION_REQUEST = 1234;

    private Context context;
    DialogManager dialogManager;
    String[] datesForThisWeek;

    @Override
    public void onReceive(Context context, Intent intent) {
        //getDates
        this.context = context;
        String dateString = intent.getStringExtra("current_date");


        if(intent.getAction().equals("com.puszek.jm.puszek.GET_NOTIFICATION")) {
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", new Locale("pl"));
            try {
                currentDate = format.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(currentDate!=null){
                dialogManager = new DialogManager();
                requestWasteTypes(context, currentDate);
            }

            Log.e("NOTIFICATION", "onReceive: RECEIVED ");
        }

    }


    protected void createBigNotification(String[] msgPositions, final Context context) {
        Intent intent = new Intent(context, MainMenuActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(context.getString(R.string.welcome));
        for (int i=0; i < msgPositions.length; i++) {
            inboxStyle.addLine(msgPositions[i]);
        }

        Notification noti = new NotificationCompat.Builder(context, "Channel")
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.current_termins))
                .setTicker(context.getString(R.string.app_name))
                .setNumber(msgPositions.length)
                .setAutoCancel(true)
                .setStyle(inboxStyle)
                .setSmallIcon(R.drawable.notif_icon)
                .setContentIntent(pIntent)
                .build();

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        assert notificationManager != null;
        notificationManager.notify(1, noti);
    }

    Date currentDate;
    public String[] getNearestDates(WasteType[] wasteTypes, final Date currentDate) {

        ArrayList<String> dates = new ArrayList<>();
        String[] wasteTypesString = context.getResources().getStringArray(R.array.waste_types);
        String[] wasteTypesPl = context.getResources().getStringArray(R.array.waste_types_pl);

        for (int i = 0; i<wasteTypesString.length; i++) {
            String value = dialogManager.getSingleDateFromWasteTypes(wasteTypes, wasteTypesString[i], currentDate);
            String singleDate = wasteTypesPl[i] + " " + value;
            dates.add(singleDate);
        }

        if(dates.size()!=0) {
            String[] datesForThisWeek = new String[dates.size()];
            for (int i = 0; i < dates.size(); i++) {
                datesForThisWeek[i] = dates.get(i);
            }
            return datesForThisWeek;
        } else return new String[1];

    }

    private void requestWasteTypes(final Context context, final Date currentDate){
        SharedPreferences puszekPrefs = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        final String accessToken = "Bearer "+ puszekPrefs.getString("access_token","");

        final Thread newThread = new Thread(){
            @Override
            public void run() {
                final ApiInterface apiInterface = APIClient.getClient().create(ApiInterface.class);
                final Call<WasteType[]> requestWasteType = apiInterface.getWasteTypes(accessToken);

                requestWasteType.enqueue(new Callback<WasteType[]>() {
                    @Override
                    public void onResponse(Call<WasteType[]> call, Response<WasteType[]> response) {
                        assert response.body() != null;
                        datesForThisWeek = getNearestDates(response.body(), currentDate);
                        if(datesForThisWeek.length != 0) {
                            createBigNotification(datesForThisWeek,context);
                        }
                    }

                    @Override
                    public void onFailure(Call<WasteType[]> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        };

        newThread.start();
    }



}
