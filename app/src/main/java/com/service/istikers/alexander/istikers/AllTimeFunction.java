package com.service.istikers.alexander.istikers;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import static com.service.istikers.alexander.istikers.RedactKeep.periodRepeatEventEditText;
import static com.service.istikers.alexander.istikers.RedactKeep.setRepeatTimeOnlyDays;
import static com.service.istikers.alexander.istikers.RedactKeep.setRepeatTimeOnlyTimes;

public class AllTimeFunction {

    public static void setTypeRepeatTimeTextView (int progress) {
        /*switch (progress) {
            case 0:
                setRepeatTimeOnlyDays.setVisibility(View.GONE);
                setRepeatTimeOnlyTimes.setVisibility(View.VISIBLE);
                break;
            case 1:
                setRepeatTimeOnlyDays.setVisibility(View.GONE);
                setRepeatTimeOnlyTimes.setVisibility(View.VISIBLE);
                break;
            case 2:
                setRepeatTimeOnlyDays.setVisibility(View.VISIBLE);
                setRepeatTimeOnlyTimes.setVisibility(View.GONE);
                break;
            case 3:
                setRepeatTimeOnlyDays.setVisibility(View.VISIBLE);
                setRepeatTimeOnlyTimes.setVisibility(View.GONE);
                break;
            case 4:
                setRepeatTimeOnlyDays.setVisibility(View.GONE);
                setRepeatTimeOnlyTimes.setVisibility(View.GONE);
                break;
            case 5:
                setRepeatTimeOnlyDays.setVisibility(View.GONE);
                setRepeatTimeOnlyTimes.setVisibility(View.GONE);
                break;
        }*/
        int point = 0;
        if (periodRepeatEventEditText.getText().toString().equals("") || periodRepeatEventEditText.getText().toString().equals("0")) {
            point = 1;
        } else {
            point = Integer.parseInt(periodRepeatEventEditText.getText().toString());
        }
        RedactKeep.typeRepeatTimeTextView.setText(AllTimeFunction.ToRusNameOfTypeTimeRepeat(progress, point));
    }

    public static void SetTextRepeat(View RepeatedViewText) {
        if (RepeatedViewText.getId() == R.id.repeat_event) {
            String whenRepeatText = "";
            if (RedactKeep.eventIsRepeated) {
                whenRepeatText = "Период повтора: " + RedactKeep.eventRereatedPoint + " " +
                        ToRusNameOfTypeTimeRepeat(RedactKeep.eventRereatedScoreVariantNumber, RedactKeep.eventRereatedPoint).toLowerCase();
                String repeatTimeOnly = "";
                /*if (RedactKeep.eventRereatedScoreVariantNumber == 2 || RedactKeep.eventRereatedScoreVariantNumber == 3) {
                    for (int i = 0; i < 7; i++) {
                        if (RedactKeep.eventRereatedDayOfWeak[i]) {
                            if (!repeatTimeOnly.equals("")) repeatTimeOnly += ", ";
                            repeatTimeOnly += RedactKeep.DayOfWeakText[i];
                        }
                    }
                    if (!repeatTimeOnly.equals("")) repeatTimeOnly = " (" + repeatTimeOnly + ")";
                } else if (RedactKeep.eventRereatedScoreVariantNumber == 0 || RedactKeep.eventRereatedScoreVariantNumber == 1) {
                    repeatTimeOnly = " (с " + RedactKeep.eventRereatedTimeInterval[0] + ":" + String.format("%02d", RedactKeep.eventRereatedTimeInterval[1])
                            + " по " + RedactKeep.eventRereatedTimeInterval[2] + ":" + String.format("%02d", RedactKeep.eventRereatedTimeInterval[3]) + ")";
                }*/
                whenRepeatText += repeatTimeOnly;
                switch (RedactKeep.eventRereatedEndVariantNumber) {
                    case 1:
                        whenRepeatText += " до " + String.format("%02d", RedactKeep.eventRereatedEnd[0]) + "." +
                                String.format("%02d", RedactKeep.eventRereatedEnd[1] + 1) + "." + RedactKeep.eventRereatedEnd[2];
                        break;
                    case 2:
                        whenRepeatText += " " + RedactKeep.eventRereatedEnd[3] + " раз";
                        break;
                }
            } else whenRepeatText = "Не повторяется";
            ((TextView) RepeatedViewText).setText(whenRepeatText);
        } else {
            String whenRepeatText = "";
            if (RedactKeep.notificationIsRepeated[RedactKeep.SISTEM_NUMBER]) {
                whenRepeatText = "Период повтора: " + RedactKeep.notificationRereatedPoint[RedactKeep.SISTEM_NUMBER] + " " +
                        ToRusNameOfTypeTimeRepeat(RedactKeep.notificationRereatedScoreVariantNumber[RedactKeep.SISTEM_NUMBER], RedactKeep.notificationRereatedPoint[RedactKeep.SISTEM_NUMBER]).toLowerCase();
                String repeatTimeOnly = "";
                /*if (RedactKeep.notificationRereatedScoreVariantNumber[RedactKeep.SISTEM_NUMBER] == 2 || RedactKeep.notificationRereatedScoreVariantNumber[RedactKeep.SISTEM_NUMBER] == 3) {
                    for (int i = 0; i < 7; i++) {
                        if (RedactKeep.notificationRereatedDayOfWeak[RedactKeep.SISTEM_NUMBER][i]) {
                            if (!repeatTimeOnly.equals("")) repeatTimeOnly += ", ";
                            repeatTimeOnly += RedactKeep.DayOfWeakText[i];
                        }
                    }
                    if (!repeatTimeOnly.equals("")) repeatTimeOnly = " (" + repeatTimeOnly + ")";
                } else if (RedactKeep.notificationRereatedScoreVariantNumber[RedactKeep.SISTEM_NUMBER] == 0 || RedactKeep.notificationRereatedScoreVariantNumber[RedactKeep.SISTEM_NUMBER] == 1) {
                    repeatTimeOnly = " (с " + RedactKeep.notificationRereatedTimeInterval[RedactKeep.SISTEM_NUMBER][0] + ":" + String.format("%02d", RedactKeep.notificationRereatedTimeInterval[RedactKeep.SISTEM_NUMBER][1])
                            + " по " + RedactKeep.notificationRereatedTimeInterval[RedactKeep.SISTEM_NUMBER][2] + ":" + String.format("%02d", RedactKeep.notificationRereatedTimeInterval[RedactKeep.SISTEM_NUMBER][3]) + ")";
                }*/
                whenRepeatText += repeatTimeOnly;
                switch (RedactKeep.notificationRereatedEndVariantNumber[RedactKeep.SISTEM_NUMBER]) {
                    case 1:
                        whenRepeatText += " до " + String.format("%02d", RedactKeep.notificationRereatedEnd[RedactKeep.SISTEM_NUMBER][0]) + "." +
                                String.format("%02d", RedactKeep.notificationRereatedEnd[RedactKeep.SISTEM_NUMBER][1] + 1) + "." + RedactKeep.notificationRereatedEnd[RedactKeep.SISTEM_NUMBER][2];
                        break;
                    case 2:
                        whenRepeatText += " " + RedactKeep.notificationRereatedEnd[RedactKeep.SISTEM_NUMBER][3] + " раз";
                        break;
                }
            } else whenRepeatText = "Не повторяется";
            ((TextView) RepeatedViewText).setText(whenRepeatText);
        }
    }

    public static String ToRusNameOfTypeTimeRepeat (int numberTypeTime, int pointTypeTime) {
        switch (numberTypeTime) {
            case 0:
                if (pointTypeTime % 10 == 1 && pointTypeTime % 100 != 11) return "Минута";
                else if (pointTypeTime % 10 >= 5 || pointTypeTime % 10 == 0 || (pointTypeTime % 100 >= 11 && pointTypeTime <= 19)) return "Минут";
                else if (pointTypeTime % 10 >= 2 && pointTypeTime % 10 <= 4 && !(pointTypeTime % 100 >= 11 && pointTypeTime <= 19)) return "Минуты";
                break;
            case 1:
                if (pointTypeTime % 10 == 1 && pointTypeTime % 100 != 11) return "Час";
                else if (pointTypeTime % 10 >= 5 || pointTypeTime % 10 == 0 || (pointTypeTime % 100 >= 11 && pointTypeTime <= 19)) return "Часов";
                else if (pointTypeTime % 10 >= 2 && pointTypeTime % 10 <= 4 && !(pointTypeTime % 100 >= 11 && pointTypeTime <= 19)) return "Часа";
                break;
            case 2:
                if (pointTypeTime % 10 == 1 && pointTypeTime % 100 != 11) return "День";
                else if (pointTypeTime % 10 >= 5 || pointTypeTime % 10 == 0 || (pointTypeTime % 100 >= 11 && pointTypeTime <= 19)) return "Дней";
                else if (pointTypeTime % 10 >= 2 && pointTypeTime % 10 <= 4 && !(pointTypeTime % 100 >= 11 && pointTypeTime <= 19)) return "Дня";
                break;
            case 3:
                if (pointTypeTime % 10 == 1 && pointTypeTime % 100 != 11) return "Неделя";
                else if (pointTypeTime % 10 >= 5 || pointTypeTime % 10 == 0 || (pointTypeTime % 100 >= 11 && pointTypeTime <= 19)) return "Недель";
                else if (pointTypeTime % 10 >= 2 && pointTypeTime % 10 <= 4 && !(pointTypeTime % 100 >= 11 && pointTypeTime <= 19)) return "Недели";
                break;
            case 4:
                if (pointTypeTime % 10 == 1 && pointTypeTime % 100 != 11) return "Месяц";
                else if (pointTypeTime % 10 >= 5 || pointTypeTime % 10 == 0 || (pointTypeTime % 100 >= 11 && pointTypeTime <= 19)) return "Месяцев";
                else if (pointTypeTime % 10 >= 2 && pointTypeTime % 10 <= 4 && !(pointTypeTime % 100 >= 11 && pointTypeTime <= 19)) return "Месяца";
                break;
            case 5:
                if (pointTypeTime % 10 == 1 && pointTypeTime % 100 != 11) return "Год";
                else if (pointTypeTime % 10 >= 5 || pointTypeTime % 10 == 0 || (pointTypeTime % 100 >= 11 && pointTypeTime <= 19)) return "Лет";
                else if (pointTypeTime % 10 >= 2 && pointTypeTime % 10 <= 4 && !(pointTypeTime % 100 >= 11 && pointTypeTime <= 19)) return "Года";
                break;
        }
        return null;
    }
}
