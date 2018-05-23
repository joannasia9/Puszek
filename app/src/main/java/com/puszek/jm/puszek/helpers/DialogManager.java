package com.puszek.jm.puszek.helpers;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.puszek.jm.puszek.BarcodeReadingActivity;
import com.puszek.jm.puszek.BarcodeReadingFragment;
import com.puszek.jm.puszek.ObjectVerificationActivity;
import com.puszek.jm.puszek.R;
import com.puszek.jm.puszek.models.RequestedBarcodeData;
import com.puszek.jm.puszek.models.ShortCodes;
import com.puszek.jm.puszek.models.WasteType;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;


public class DialogManager {
    private Context context;

    private int bgResource;
    private String date ="";
    private Dialog dialog;
    private String stringTitle = "";
    private String[] wasteTypesString;
    private WasteType[] wasteTypes;

    private TextView title;
    private ImageView boxColour;
    private TextView depDate;

    private String[] wasteTypeDateSet;
    private Date currentDate;
    private OnActivityStatusChangedListener activityStatusChangedListener = new OnActivityStatusChangedListener() {
        @Override
        public void OnActivityStatusChanged(boolean isActive) {
            isActivityActive = isActive;
        }
    };
    private View.OnClickListener okButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialog.cancel();
        }
    };
    private boolean isActivityActive = false;

    public DialogManager(BarcodeReadingFragment context) {
        this.context = context.getContext();
        this.wasteTypesString = context.getResources().getStringArray(R.array.waste_types);
        context.setOnActivityStatusChangedListener(activityStatusChangedListener);
        createBaseDialog();
    }

    public DialogManager(ObjectVerificationActivity context) {
        this.context = context;
        this.wasteTypesString = context.getResources().getStringArray(R.array.waste_types);

        context.setOnActivityStatusChangedListener(activityStatusChangedListener);
        createBaseDialog();
    }

    public DialogManager(){
    }

    public void showBarcodeDetectedDialog(RequestedBarcodeData barcode, Date current) {
        this.currentDate = current;
        wasteTypeDateSet = barcode.getProduct().getWasteType().getScheduledDisposals();
        ShortCodes code = barcode.getProduct().getWasteType().getShortCode();
        if (code != null) {
            setDialogFields(code, wasteTypeDateSet);
           if (isActivityActive) dialog.show();
        }
    }

    public Dialog getDialog() {
        return dialog;
    }

    public void showBoxDetectedDialog(String result, WasteType[] wastes, Date currentDate) {
        this.wasteTypes = wastes;
        this.currentDate = currentDate;

        setDialogFields(result);
        if ((result.equals(wasteTypesString[0])
                || result.equals(wasteTypesString[1])
                || result.equals(wasteTypesString[2])
                || result.equals(wasteTypesString[3])
                || result.equals(wasteTypesString[4])
                || result.equals(wasteTypesString[5]))&&isActivityActive) dialog.show();
    }

    private void setDialogFields(String result) {
        setDataToShow(result);
        depDate.setText(date);
        boxColour.setBackground(context.getDrawable(bgResource));
        title.setText(stringTitle);
    }

    private void setDialogFields(ShortCodes type, String[] dateSet) {
        setDataToShow(type);

        ArrayList<String> filteredDateSet = getFilteredFiveDates(dateSet);

        if (filteredDateSet.size() != 0) {
            StringBuilder stringBuilder = new StringBuilder("");
            for (String item : filteredDateSet) {
                stringBuilder.append(item).append("\n");
            }
            date = stringBuilder.toString();
        }

        depDate.setText(date);
        boxColour.setBackground(context.getDrawable(bgResource));
        title.setText(stringTitle);
    }

    private ArrayList<String> getFilteredFiveDates(String[] dateSet) {
        ArrayList<String> results = new ArrayList<>();

        if (dateSet.length > 0) {
            ArrayList<Date> sortedDates = new ArrayList<>();
            for (String item : dateSet) {
                Date temp = stringToDate(item);
                if (temp.after(currentDate)) sortedDates.add(temp);
            }
            Collections.sort(sortedDates);


            if (sortedDates.size() != 0) {
                if (sortedDates.size() < 5) {
                    for (Date item : sortedDates) {
                        results.add(dateToString(item));
                    }
                } else {
                    for (int i = 0; i < 4; i++) results.add(dateToString(sortedDates.get(i)));
                }
            }
        }

        return results;

    }

    private Date stringToDate(String date){
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd",new Locale("pl"));
        try {
            return format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    private String dateToString(Date date){
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy", new Locale("pl"));
        return format.format(date);
    }

    private void createBaseDialog() {
        Button okButton;

        if (dialog == null) {
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_classified);
            title = dialog.findViewById(R.id.wasteType);
            boxColour = dialog.findViewById(R.id.boxColour);
            depDate = dialog.findViewById(R.id.departureDate);
            okButton = dialog.findViewById(R.id.okButton);
            okButton.setOnClickListener(okButtonListener);
        }


    }

    private void setDataToShow(String result) {
        switch (result) {
            case "paper":
                bgResource = R.drawable.blue_box;
                stringTitle = context.getString(R.string.pap);
                date = getDateFromWasteTypes(result);
                break;
            case "metal":
                bgResource = R.drawable.red_box;
                stringTitle = context.getString(R.string.met);
                date = getDateFromWasteTypes(result);
                break;
            case "cardboard":
                bgResource = R.drawable.red_box;
                stringTitle = context.getString(R.string.dry);
                date = getDateFromWasteTypes(result);
                break;
            case "glass":
                bgResource = R.drawable.green_box;
                stringTitle = context.getString(R.string.gla);
                date = getDateFromWasteTypes(result);
                break;
            case "plastic":
                bgResource = R.drawable.yellow_box;
                stringTitle = context.getString(R.string.pla);
                date = getDateFromWasteTypes(result);
                break;
            case "trash":
                bgResource = R.drawable.black_box;
                stringTitle = context.getString(R.string.mix);
                date = getDateFromWasteTypes(result);
                break;
            default:
                break;
        }
    }

    private String getDateFromWasteTypes(String label) {
        StringBuilder tempDates = new StringBuilder("");
        ArrayList<WasteType> wasteArray = getWasteTypeFromLabel(label);

        if (wasteArray.size() != 0) {
            ArrayList<Date> allDates = new ArrayList<>();
            for (WasteType item : wasteArray) {
                String[] scheduledDisposals = item.getScheduledDisposals();

                for (String disposal : scheduledDisposals) {
                    Date temp = stringToDate(disposal);
                    if(temp.after(currentDate)) allDates.add(temp);
                }
            }

            if(allDates.size() != 0){
                Collections.sort(allDates);
                if(allDates.size()<5){
                    for (Date item : allDates) {
                        tempDates.append(dateToString(item)).append("\n").append("\n");
                    }
                } else {
                    for (int i = 0; i<4; i++) tempDates.append(dateToString(allDates.get(i))).append("\n");
                }
            }
        }

        return tempDates.toString();
    }

    private ArrayList<WasteType> getWasteTypeFromLabel(String label) {
        ArrayList<WasteType> wasteTypesTemp = new ArrayList<>();

        if (wasteTypes!=null)
        if (wasteTypes.length!=0) {
            switch (label) {
                case "paper":
                    for (WasteType item : wasteTypes) {
                        if (item.getShortCode() == ShortCodes.DRY) {
                            if(item.getScheduledDisposals().length != 0) wasteTypesTemp.add(item);
                        }
                    }
                    break;
                case "metal":
                    for (WasteType item : wasteTypes) {
                        if (item.getShortCode() == ShortCodes.DRY) {
                            if(item.getScheduledDisposals().length != 0) wasteTypesTemp.add(item);
                        }
                    }
                    break;
                case "cardboard":
                    for (WasteType item : wasteTypes) {
                        if (item.getShortCode() == ShortCodes.DRY) {
                            if(item.getScheduledDisposals().length != 0) wasteTypesTemp.add(item);
                        }
                    }
                    break;
                case "glass":
                    for (WasteType item : wasteTypes) {
                        if (item.getShortCode() == ShortCodes.GLA) {
                            if(item.getScheduledDisposals().length != 0) wasteTypesTemp.add(item);
                        }
                    }
                    break;
                case "plastic":
                    for (WasteType item : wasteTypes) {
                        if (item.getShortCode() == ShortCodes.DRY) {
                            if(item.getScheduledDisposals().length != 0) wasteTypesTemp.add(item);
                        }
                    }
                    break;
                case "trash":
                    for (WasteType item : wasteTypes) {
                        if (item.getShortCode() == ShortCodes.MIX) {
                            if(item.getScheduledDisposals().length != 0) wasteTypesTemp.add(item);
                        }
                    }
                    break;
                default:
                    for (WasteType item : wasteTypes) {
                        if (item.getShortCode() == ShortCodes.MIX) {
                            if(item.getScheduledDisposals().length != 0) wasteTypesTemp.add(item);
                        }
                    }
                    break;
            }
        }
        return wasteTypesTemp;
    }

    private void setDataToShow(ShortCodes type) {
        switch (type) {
            case DRY:
                bgResource = R.drawable.red_box;
                stringTitle = context.getString(R.string.dry);
                date = "";
                break;
            case GLA:
                bgResource = R.drawable.yellow_box;
                stringTitle = context.getString(R.string.gla);
                date = "";
                break;
            case MIX:
                bgResource = R.drawable.black_box;
                stringTitle = context.getString(R.string.mix);
                date = "";
                break;
            case BIG:
                bgResource = R.drawable.blue_box;
                stringTitle = context.getString(R.string.big);
                date = "";
                break;
            case BIO:
                bgResource = R.drawable.brown_box;
                stringTitle = context.getString(R.string.bio);
                date = "";
                break;
            case GRE:
                bgResource = R.drawable.green_box;
                stringTitle = context.getString(R.string.gre);
                date = "";
                break;
            default:
                bgResource = R.drawable.black_box;
                stringTitle = context.getString(R.string.mix);
                date = "";
                break;
        }
    }


    public String getSingleDateFromWasteTypes(WasteType[] types, String label, Date currentDate) {
        this.currentDate = currentDate;
        this.wasteTypes = types;
        ArrayList<WasteType> wasteArray = getWasteTypeFromLabel(label);

        if (wasteArray.size() != 0) {
            ArrayList<Date> allDates = new ArrayList<>();
            for (WasteType item : wasteArray) {
                String[] scheduledDisposals = item.getScheduledDisposals();

                for (String disposal : scheduledDisposals) {
                    Date temp = stringToDate(disposal);
                    if(temp.after(currentDate)) allDates.add(temp);
                }
            }

            if(allDates.size() != 0){
                Collections.sort(allDates);
                return dateToString(allDates.get(0));
            } else return "";
        } else return "";

    }

    public void setWasteTypes(WasteType[] wasteTypes) {
        this.wasteTypes = wasteTypes;
    }

}
