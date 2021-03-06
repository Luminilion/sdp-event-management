package ch.epfl.sweng.eventmanager.ui.event.interaction.fragments.schedule;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.core.content.FileProvider;
import androidx.lifecycle.LiveData;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ch.epfl.sweng.eventmanager.R;
import ch.epfl.sweng.eventmanager.repository.data.ScheduledItem;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

public class MyScheduleFragment extends AbstractScheduleFragment {
    private static final String TAG = "MyScheduleFragment";
    private static final String CALENDAR_FILE_NAME = "myschedule.ics";

    @BindView(R.id.addToCalendar)
    MaterialButton addToCalendarButton;

    @Override
    protected void setEmptyListTextView() {
        super.emptyListTextView.setText(R.string.my_schedule_empty);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_my_schedule;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, v);
        return v;
    }

    @OnClick(R.id.addToCalendar)
    void setAddToCalendarButton() {
        this.writeEventsToCalendar(this.getScheduledItems().getValue());
        this.openCalendar();
    }

    @Override
    protected void onItemsUpdate(List<ScheduledItem> items) {
        if (addToCalendarButton == null) return;

        if (items != null && items.size() > 0) addToCalendarButton.setVisibility(Button.VISIBLE);
        else addToCalendarButton.setVisibility(Button.GONE);
    }

    @Override
    protected LiveData<List<ScheduledItem>> getScheduledItems() {
        return this.model.getJoinedScheduleItems();
    }

    private void writeEventsToCalendar(List<ScheduledItem> mySchedule) {
        FileOutputStream outputStream;

        try {
            outputStream = getContext().openFileOutput(CALENDAR_FILE_NAME, Context.MODE_PRIVATE);

            writeCalendar(mySchedule, outputStream);

            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void writeCalendar(List<ScheduledItem> events, OutputStream stream) {
        PrintStream printer = new PrintStream(stream);

        printer.println("BEGIN:VCALENDAR\n" + "VERSION:2.0\n" + "PRODID:-//EventManager/MySchedule//Event " +
                "Schedule//EN");

        for (ScheduledItem item : events) {
            item.printAsIcalendar(printer);
        }

        printer.println("END:VCALENDAR");

        printer.close();
    }

    private void openCalendar() {
        Intent openFile = new Intent(Intent.ACTION_VIEW);
        // TODO handle null pointer exception from getContext and getFilesDir
        Uri uri = FileProvider.getUriForFile(getContext(), "ch.epfl.sweng.eventmanager.fileprovider",
                new File(getContext().getFilesDir(), CALENDAR_FILE_NAME));
        openFile.setDataAndType(uri, "text/calendar");

        // https://developer.android.com/reference/android/support/v4/content/FileProvider#GetUri
        openFile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        openFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


        try {
            getContext().startActivity(openFile);
        } catch (ActivityNotFoundException e) {
            Log.i(TAG, "Cannot open file.");
        }
    }
}
