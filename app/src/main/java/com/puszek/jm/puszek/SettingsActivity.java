package com.puszek.jm.puszek;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.puszek.jm.puszek.adapters.SettingsListAdapter;
import com.puszek.jm.puszek.helpers.FieldsValidator;
import com.puszek.jm.puszek.models.APIClient;
import com.puszek.jm.puszek.models.ApiInterface;
import com.puszek.jm.puszek.models.Password;
import com.puszek.jm.puszek.models.RegisteredUser;
import com.puszek.jm.puszek.models.User;

import java.util.ArrayList;

import javax.xml.validation.Validator;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends MyBaseActivity{
    ListView settingsListView;
    SettingsListAdapter settingsListAdapter;
    private ArrayList<String> registeredUsersData;
    private User userToUpdate;
    private Password passwordToUpdate;
    private String[] settingsTitles;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settingsTitles = new String[]{getString(R.string.login),getString(R.string.e_mail),getString(R.string.password),
                getString(R.string.district), getString(R.string.street),
                getString(R.string.house_number)};

        registeredUsersData = new ArrayList<>();
        getCurrentUser();
        settingsListView = findViewById(R.id.settingsListView);


    }

    private void setupListView(ListView listView, final ArrayList<String> registeredUsersData){
        if(registeredUsersData.size() != 0){
            settingsListAdapter = new SettingsListAdapter(this,registeredUsersData,settingsTitles);
            listView.setAdapter(settingsListAdapter);

            final Animation itemAnimation = AnimationUtils.loadAnimation(this, R.anim.settings_item_animation);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    view.startAnimation(itemAnimation);

                    if(position == 2){
                        showModifyPassDialog();
                    } else if(position != 0) showModifyDialog(settingsTitles[position],registeredUsersData.get(position),position);
                    else Toasty.info(getApplicationContext(),getString(R.string.login_unmodif)).show();
                }
            });
        }
    }

    private void showModifyDialog(String title, String current, final int position){
        TextView dialogTitle;
        TextView currentValue;
        final Button saveButton;
        Button cancelButton;
        final EditText newValue;

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_modify_data);

        dialogTitle = dialog.findViewById(R.id.dialogTitle);
        dialogTitle.setText(title);

        currentValue = dialog.findViewById(R.id.currentValue);
        currentValue.setText(current);

        cancelButton = dialog.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        newValue = dialog.findViewById(R.id.newPassword);

        saveButton = dialog.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = newValue.getText().toString().trim();
                if(temp.equals("")){
                    dialog.cancel();
                } else {
                    updateUserObject(position,temp);
                    if(userToUpdate != null)updateUsersData(userToUpdate,position,temp);
                    dialog.cancel();
                }
            }
        });

        dialog.show();
    }

    private void updateUserObject(int position, String value){
        if (userToUpdate!=null)
        switch(position){
            case 0:
                userToUpdate.setLogin(value);
                break;
            case 1:
                userToUpdate.setEmail(value);
                break;
            case 3:
                userToUpdate.setDistrict(value);
                break;
            case 4:
                userToUpdate.setStreet(value);
                break;
            case 5:
                userToUpdate.setHouseNumber(value);
                break;
        }
    }

    private void updateUsersData(final User user, final int selection, final String newValue){

        final Thread newThread = new Thread(){
            @Override
            public void run() {
                final ApiInterface apiInterface = APIClient.getClient().create(ApiInterface.class);
                SharedPreferences puszekPrefs = getApplicationContext().getSharedPreferences(
                        getString(R.string.preference_file_key), Context.MODE_PRIVATE);

                String accessToken = "Bearer "+ puszekPrefs.getString("access_token","");


                final Call<RegisteredUser> updateUser = apiInterface.updateUser(accessToken,user);

                updateUser.enqueue(new Callback<RegisteredUser>() {
                    @Override
                    public void onResponse(Call<RegisteredUser> call, Response<RegisteredUser> response) {
                        if(response.body()!=null) {
                            runOnUiThreadToast(getString(R.string.saved_succ),0);
                            registeredUsersData.set(selection,newValue);
                            settingsListAdapter.updateAdapter(registeredUsersData);
                        }
                        else runOnUiThreadToast(getString(R.string.saving_error), 1);

                    }

                    @Override
                    public void onFailure(Call<RegisteredUser> call, Throwable t) {
                        runOnUiThreadToast(getString(R.string.saving_error), 1);
                    }
                });
            }
        };

        newThread.start();
    }

    private void showModifyPassDialog(){
        final EditText newPasswordValue;
        final EditText repeatedPassword;
        Button saveButton;
        Button cancelButton;

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_password_change);

        newPasswordValue = dialog.findViewById(R.id.newPassword);
        repeatedPassword = dialog.findViewById(R.id.repeatedPassword);

        cancelButton = dialog.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        saveButton = dialog.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FieldsValidator validator = new FieldsValidator(getApplicationContext());
                if(validator.isValidField(newPasswordValue)
                        && validator.isValidField(repeatedPassword)
                        && validator.isValidPassword(newPasswordValue,repeatedPassword)){
                    passwordToUpdate = new Password();
                    passwordToUpdate.setPassword(newPasswordValue.getText().toString());
                    updateUsersPassword(passwordToUpdate);
                    dialog.cancel();
                } else {
                    dialog.cancel();
                }
            }
        });

        dialog.show();
    }

    private void getCurrentUser(){
        registeredUsersData = new ArrayList<>();

        final Thread newThread = new Thread(){
            @Override
            public void run() {
                final ApiInterface apiInterface = APIClient.getClient().create(ApiInterface.class);
                SharedPreferences puszekPrefs = getApplicationContext().getSharedPreferences(
                        getString(R.string.preference_file_key), Context.MODE_PRIVATE);

                String accessToken = "Bearer "+ puszekPrefs.getString("access_token","");


                final Call<RegisteredUser> requestCurrentUser = apiInterface.getCurrentUser(accessToken);

                requestCurrentUser.enqueue(new Callback<RegisteredUser>() {
                    @Override
                    public void onResponse(Call<RegisteredUser> call, Response<RegisteredUser> response) {
                        if(response.body()!=null){
                            RegisteredUser registeredUser = response.body();
                            String login = registeredUser.getLogin();
                            String password = "";
                            String district = registeredUser.getDistrict();
                            String street = registeredUser.getStreet();
                            String houseNum = registeredUser.getHouseNumber();
                            String email = registeredUser.getEmail();

                            registeredUsersData.add(0, login);
                            registeredUsersData.add(1, email);
                            registeredUsersData.add(2, password);
                            registeredUsersData.add(3, district);
                            registeredUsersData.add(4, street);
                            registeredUsersData.add(5, houseNum);

                            setupListView(settingsListView,registeredUsersData);

                            userToUpdate = new User(login,email,password,district,street,houseNum);

                        }
                    }

                    @Override
                    public void onFailure(Call<RegisteredUser> call, Throwable t) {
                            t.printStackTrace();
                    }
                });
            }
        };

        newThread.start();
    }

    private void updateUsersPassword(final Password password){

        final Thread newThread = new Thread() {
            @Override
            public void run() {
                final ApiInterface apiInterface = APIClient.getClient().create(ApiInterface.class);
                SharedPreferences puszekPrefs = getApplicationContext().getSharedPreferences(
                        getString(R.string.preference_file_key), Context.MODE_PRIVATE);

                String accessToken = "Bearer " + puszekPrefs.getString("access_token", "");


                final Call<RegisteredUser> updateUser = apiInterface.updatePassword(accessToken, password);

                updateUser.enqueue(new Callback<RegisteredUser>() {
                    @Override
                    public void onResponse(Call<RegisteredUser> call, Response<RegisteredUser> response) {
                        if(response.body()!=null) runOnUiThreadToast(getString(R.string.saved_succ),0);
                        else runOnUiThreadToast(getString(R.string.saving_error), 1);
                    }

                    @Override
                    public void onFailure(Call<RegisteredUser> call, Throwable t) {
                        runOnUiThreadToast(getString(R.string.saving_error), 1);
                    }
                });
            }
        };
        newThread.start();


    }

    private void runOnUiThreadToast(final String message, final int selection){
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                   if(selection == 0) Toasty.success(getApplicationContext(),message).show();
                   if(selection == 1) Toasty.error(getApplicationContext(),message).show();
                }
            });
        }
}
