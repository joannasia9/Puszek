package com.puszek.jm.puszek.helpers;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.puszek.jm.puszek.R;
import com.puszek.jm.puszek.models.RequestedBarcodeData;
import com.puszek.jm.puszek.models.ShortCodes;
import com.puszek.jm.puszek.models.WasteType;

import java.util.ArrayList;


public class DialogManager {
    private Context context;
    private int bgResource;
    private String date;
    private Dialog dialog;
    private String stringTitle = "";
    private String[] wasteTypesString;
    private WasteType[] wasteTypes;

    private TextView title;
    private ImageView boxColour;
    private TextView depDate;

    private String[] wasteTypeDateSet;

    private View.OnClickListener okButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialog.cancel();
        }
    };

    public DialogManager(Context context) {
        this.context = context;
        this.wasteTypesString = context.getResources().getStringArray(R.array.waste_types);
        createBaseDialog();
    }

    public void showBarcodeDetectedDialog(RequestedBarcodeData barcode) {
        wasteTypeDateSet = barcode.getProduct().getWasteType().getScheduledDisposals();
        ShortCodes code = barcode.getProduct().getWasteType().getShortCode();
        if (code != null) {
            setDialogFields(code, wasteTypeDateSet);
            dialog.show();
        }
    }

    public Dialog getDialog() {
        return dialog;
    }

    public void showBoxDetectedDialog(String result, WasteType[] wastes) {
        this.wasteTypes = wastes;

        setDialogFields(result);
        if (result.equals(wasteTypesString[0])
                || result.equals(wasteTypesString[1])
                || result.equals(wasteTypesString[2])
                || result.equals(wasteTypesString[3])
                || result.equals(wasteTypesString[4])
                || result.equals(wasteTypesString[5])) dialog.show();
    }

    private void setDialogFields(String result) {
        setDataToShow(result);
        depDate.setText(date);
        boxColour.setBackground(context.getDrawable(bgResource));
        title.setText(stringTitle);
    }

    private void setDialogFields(ShortCodes type, String[] dateSet) {
        setDataToShow(type);

        if (dateSet.length != 0) {
            StringBuilder stringBuilder = new StringBuilder("");
            for (String item : dateSet) {
                stringBuilder.append(parseSingleDate(item)).append("\n");
            }
            date = stringBuilder.toString();
        }

        depDate.setText(date);
        boxColour.setBackground(context.getDrawable(bgResource));
        title.setText(stringTitle);
    }

    private void createBaseDialog() {
        Button okButton;

        if (dialog == null) {
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_classified);
            title = dialog.findViewById(R.id.wasteType);
            boxColour = dialog.findViewById(R.id.boxColour);
            depDate = dialog.findViewById(R.id.dTitle);
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
        StringBuilder tempDates = new StringBuilder();
        ArrayList<WasteType> wasteArray = getWasteTypeFromLabel(label);
        if (wasteArray.size() != 0) {
            for (WasteType item : wasteArray) {
                String[] scheduledDisposals = item.getScheduledDisposals();
                for (String disposal : scheduledDisposals) {
                    tempDates.append(parseSingleDate(disposal)).append("\n");
                }
            }
        } else tempDates.append("");
        return tempDates.toString();
    }

    private ArrayList<WasteType> getWasteTypeFromLabel(String label) {
        ArrayList<WasteType> wasteTypesTemp = new ArrayList<>();
        if (wasteTypes!=null)
        if (wasteTypes.length!=0) {
            switch (label) {
                case "paper":
                    for (WasteType item : wasteTypes) {
                        if (item.getShortCode() == ShortCodes.DRY || item.getShortCode() == ShortCodes.PAP) {
                            wasteTypesTemp.add(item);
                        }
                    }
                    break;
                case "metal":
                    for (WasteType item : wasteTypes) {
                        if (item.getShortCode() == ShortCodes.MET) {
                            wasteTypesTemp.add(item);
                        }
                    }
                    break;
                case "cardboard":
                    for (WasteType item : wasteTypes) {
                        if (item.getShortCode() == ShortCodes.DRY || item.getShortCode() == ShortCodes.PAP) {
                            wasteTypesTemp.add(item);
                        }
                    }
                    break;
                case "glass":
                    for (WasteType item : wasteTypes) {
                        if (item.getShortCode() == ShortCodes.GLA) {
                            wasteTypesTemp.add(item);
                        }
                    }
                    break;
                case "plastic":
                    for (WasteType item : wasteTypes) {
                        if (item.getShortCode() == ShortCodes.PLA) {
                            wasteTypesTemp.add(item);
                        }
                    }
                    break;
                case "trash":
                    for (WasteType item : wasteTypes) {
                        if (item.getShortCode() == ShortCodes.MIX) {
                            wasteTypesTemp.add(item);
                        }
                    }
                    break;
                default:
                    for (WasteType item : wasteTypes) {
                        if (item.getShortCode() == ShortCodes.MIX) {
                            wasteTypesTemp.add(item);
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
                bgResource = R.drawable.green_box;
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
            case MET:
                bgResource = R.drawable.red_box;
                stringTitle = context.getString(R.string.met);
                date = "";
                break;
            case PAP:
                bgResource = R.drawable.blue_box;
                stringTitle = context.getString(R.string.pap);
                date = "";
                break;
            case PLA:
                bgResource = R.drawable.yellow_box;
                stringTitle = context.getString(R.string.pla);
                date = "";
                break;
            default:
                bgResource = R.drawable.black_box;
                stringTitle = context.getString(R.string.mix);
                date = "";
                break;
        }
    }

    private String parseSingleDate(String item) {
        String[] dateParts = item.split("-");
        return dateParts[2] + "." + dateParts[1] + "." + dateParts[0];
    }

}
