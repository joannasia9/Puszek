package com.puszek.jm.puszek.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class SendUserDetails extends AsyncTask<String, Integer, String> {

    @Override
    protected String doInBackground(String... params) {

        StringBuilder data = new StringBuilder();

        try {
            URL url = new URL(params[0]);

            URLConnection conn = url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(params[1]);  //<-- sending data

            wr.flush();

            // read any answer from server.
            BufferedReader serverAnswer = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = serverAnswer.readLine()) != null) {
                System.out.println("LINE: " + line); //<--If any response from server
                data.append(line);
            }
            wr.close();
            serverAnswer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return data.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.e("TAG POSTEXECUTE RESULT", result); // this is expecting a response code to be sent from your server upon receiving the POST data

    }

}
