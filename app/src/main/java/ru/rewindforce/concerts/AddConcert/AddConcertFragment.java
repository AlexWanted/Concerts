package ru.rewindforce.concerts.AddConcert;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TimePicker;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.textfield.TextInputEditText;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import androidx.fragment.app.Fragment;
import ru.rewindforce.concerts.Authorization.AuthorizationActivity;
import ru.rewindforce.concerts.R;

public class AddConcertFragment extends Fragment {

    private static final String TAG = AddConcertFragment.class.getSimpleName();

    private final static String BUNDLE_BITMAP = "chosen_image_bitmap",
                                BUNDLE_DATE = "chosen_date";

    private static final int GET_IMAGE_RESPONSE = 1;
    private ImageView thumbnail;
    private Bitmap currentBitmap;
    private AddConcertPresenter presenter;
    private TextInputEditText editTitle, editClub, editDate, editTime;
    private Button buttonAccept;
    private int year, month, day, hour, minutes;
    private String currentBand, currentClub;
    private byte[] imageByteArray;
    public AddConcertFragment() {}

    public static AddConcertFragment newInstance() {
        return new AddConcertFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new AddConcertPresenter();
        year = month = day = hour = minutes = -1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_concert, container, false);
        thumbnail = view.findViewById(R.id.concert_thumbnail);
        editTitle = view.findViewById(R.id.edit_title);
        editClub = view.findViewById(R.id.edit_club);
        editDate = view.findViewById(R.id.edit_date);
        editTime = view.findViewById(R.id.edit_time);
        buttonAccept = view.findViewById(R.id.accept);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.attachFragment(this);
        if(savedInstanceState != null && getContext() != null) {
            int[] date = savedInstanceState.getIntArray(BUNDLE_DATE);
            if(date != null && (date[0] > -1 && date[1] > -1 && date[2] > -1)) {
                year = date[0];
                month = date[1];
                day = date[2];
                editDate.setText(new DateTime(year, month, day, 0, 0)
                        .toString(DateTimeFormat.forPattern("dd.MM.yyyy")));
            }

            if(date != null && (date[3] > -1 && date[4] > -1)) {
                hour = date[3];
                minutes = date[4];
                editTime.setText(new DateTime(2000, 1, 1, hour, minutes)
                        .toString(DateTimeFormat.forPattern("HH:mm")));
            }

            imageByteArray = savedInstanceState.getByteArray(BUNDLE_BITMAP);
            if (imageByteArray != null) {
                currentBitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
                Glide.with(getContext()).load(currentBitmap).thumbnail(0.1f)
                        .apply(new RequestOptions().circleCrop()).into(thumbnail);
            }
        }
        thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(presenter.getImageActivityIntent(), GET_IMAGE_RESPONSE);
            }
        });

        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        AddConcertFragment.this.year = year;
                        AddConcertFragment.this.month = monthOfYear+1;
                        AddConcertFragment.this.day = dayOfMonth;
                        editDate.setText(new DateTime(year, monthOfYear+1, dayOfMonth, 0, 0)
                                .toString(DateTimeFormat.forPattern("dd.MM.yyyy")));
                    }
                };

                int dialogYear, dialogMonth, dialogDay;
                if(year > -1 && month > -1 && day > -1) {
                    dialogYear = year;
                    dialogMonth = month;
                    dialogDay = day;
                } else {
                    DateTime currentDT = new DateTime();
                    dialogYear = currentDT.getYear();
                    dialogMonth = currentDT.getMonthOfYear();
                    dialogDay = currentDT.getDayOfMonth();
                }

                if (getContext() != null) {
                    DatePickerDialog datePicker = new DatePickerDialog(getContext(),
                            R.style.Theme_MaterialComponents_Light_Dialog_Alert, myCallBack,
                            dialogYear, dialogMonth - 1, dialogDay);
                    datePicker.show();
                }
            }
        });

        editTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog.OnTimeSetListener myCallBack = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        AddConcertFragment.this.hour = hourOfDay;
                        AddConcertFragment.this.minutes = minute;
                        editTime.setText(new DateTime(2000, 1, 1, hourOfDay, minute)
                                .toString(DateTimeFormat.forPattern("HH:mm")));
                    }
                };


                int dialogHour, dialogMinute;
                if(hour > -1 && minutes > -1) {
                    dialogHour = hour;
                    dialogMinute = minutes;
                } else {
                    DateTime currentDT = new DateTime();
                    dialogHour = currentDT.getHourOfDay();
                    dialogMinute = currentDT.getMinuteOfHour();
                }

                if (getContext() != null) {
                    TimePickerDialog timePicker = new TimePickerDialog(getContext(),
                            R.style.Theme_MaterialComponents_Light_Dialog_Alert, myCallBack,
                            dialogHour, dialogMinute, true);
                    timePicker.show();
                }
            }
        });

        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTitle.getText() != null && editClub.getText() != null
                        && editDate.getText() != null && editTime.getText() != null
                        && currentBitmap != null) {
                    currentBand = editTitle.getText().toString();
                    currentClub = editClub.getText().toString();
                    String token = getContext().getSharedPreferences(AuthorizationActivity.PREF_NAME, Context.MODE_PRIVATE)
                            .getString(AuthorizationActivity.PREF_TOKEN, "");
                    String uid = getContext().getSharedPreferences(AuthorizationActivity.PREF_NAME, Context.MODE_PRIVATE)
                            .getString(AuthorizationActivity.PREF_UID, "");
                    long currentDatetime = new DateTime(year, month, day, hour, minutes).getMillis();
                    presenter.addConcert(token, uid, currentBand, currentClub, currentDatetime, imageByteArray);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_IMAGE_RESPONSE) {
            if (data != null && data.getData() != null) {
                try {
                    if (getContext() != null) {
                        InputStream inputStream = getContext().getContentResolver().openInputStream(data.getData());
                        currentBitmap = BitmapFactory.decodeStream(inputStream);
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        currentBitmap.compress(Bitmap.CompressFormat.WEBP, 75, out);
                        imageByteArray = out.toByteArray();

                        Glide.with(getContext()).load(currentBitmap).thumbnail(0.1f)
                                .apply(new RequestOptions().circleCrop()).into(thumbnail);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (presenter != null) presenter.detachFragment();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (currentBitmap != null) outState.putByteArray(BUNDLE_BITMAP, imageByteArray);
        outState.putIntArray(BUNDLE_DATE, new int[]{year, month, day, hour, minutes});
    }
}
