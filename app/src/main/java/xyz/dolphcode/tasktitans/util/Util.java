package xyz.dolphcode.tasktitans.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.icu.util.Calendar;
import android.net.ConnectivityManager;
import android.net.Network;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import xyz.dolphcode.tasktitans.DetailsActivity;
import xyz.dolphcode.tasktitans.MainActivity;
import xyz.dolphcode.tasktitans.database.User;
import xyz.dolphcode.tasktitans.database.tasks.Task;
import xyz.dolphcode.tasktitans.resources.FrequencyType;
import xyz.dolphcode.tasktitans.resources.TaskType;

// The Util class contains all universal utility functions that are invoked throughout the program
// It is marked as final so that it can't be extended because it does not need to be extended
public final class Util {

    // A list of every possible character that can be used in an ID
    public static String CHAR_LIST = "abcdefghijklmnopqrstuvwxyz1234567890";

    // A list of every possible number that can be used in an ID
    public static String NUM_LIST = "1234567890";

    // Private constructor so that the class can't be instantiated because it does not need to be
    private Util() {}

    // Automatically adds an OnClickListener to a button to make it switch screens
    public static void addSwitchScreenAction(Button btn, final Class switchTo, final AppCompatActivity switchFrom) {
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switchFrom.startActivity(new Intent(switchFrom, switchTo));
            }
        });
    }

    // Automatically adds an OnClickListener to a button to make it switch screens and carry necessary user info
    public static void addSwitchWithUser(Button btn, final Class switchTo, final AppCompatActivity switchFrom, final String id) {
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(switchFrom, switchTo);
                intent.putExtra("ID", id);
                switchFrom.startActivity(intent);
            }
        });
    }

    public static void setupConnectionChangedHandler(final AppCompatActivity context) {
        ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).registerDefaultNetworkCallback(
                new ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onAvailable(Network network) {}

                    @Override
                    public void onLost(Network network) {
                        new AlertDialog.Builder(context)
                                .setTitle("Internet Connection Lost")
                                .setMessage("A constant internet connection is required to interact with the app. Please restart the app when a connection is established")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(context, MainActivity.class);
                                        context.startActivity(intent);
                                    }
                                })
                                .show();
                    }
                });
    }

    // Transfers all extras passed over from the previous screen to the new intent
    // For context an Intent is used to switch screens, data can also be stored in Intents to carry on to the next screen when switching
    public static void transferData(Intent prev, Intent current) {
        for (String key:prev.getExtras().keySet()) { // Each key is used to get each piece of data from the previous Intent
            Object obj = prev.getExtras().get(key);
            // Convert to the appropriate datatype
            if (obj instanceof String) {
                current.putExtra(key, (String) obj);
            } else {
                current.putExtra(key, ((Integer) obj).intValue()); // Converting from the wrapper class instance to its primitive version
            }
        }
    }

    // Generates a random string of "len" length using the character list at the top of the Util class
    // Primarily used for generating IDs
    public static String generateRandomString(int len) {
        String random = "";
        for (int i = 0; i < len; i++) {
            char character = CHAR_LIST.charAt((int) Math.round(Math.random() * (CHAR_LIST.length() - 1))); // Picks out a random character
            character = (int) Math.round(Math.random()) == 1 ? Character.toUpperCase(character) : character; // Randomly decides if the character should be uppercase or not
            random += character; // Add the selected character to the rest of the string
        }
        return random;
    }

    // Generates a random string of "len" length using the character list at the top of the Util class
    // Primarily used for generating Join Codes
    public static String generateRandomNumString(int len) {
        String random = "";
        for (int i = 0; i < len; i++) {
            char character = NUM_LIST.charAt((int) Math.round(Math.random() * (NUM_LIST.length() - 1))); // Picks out a random character
            character = (int) Math.round(Math.random()) == 1 ? Character.toUpperCase(character) : character; // Randomly decides if the character should be uppercase or not
            random += character; // Add the selected character to the rest of the string
        }
        return random;
    }

    // Splits separated by a specific character or sequence of characters
    public static String joinList(ArrayList<String> list, String separator) {
        String txt = "";
        // This code takes care of empty lists and lists with only one item that would normally cause an error with the code below
        if (list.size() == 0) {
            return txt; // Return empty string if there is nothing in the list
        } else if (list.size() == 1) {
            return list.get(0); // Return the only item in the list if the list only has one item
        }

        for (int i = 0; i < list.size() - 1; i++) { // Leave out final item in list
            if (!list.get(i).isEmpty()) // Primarily used for chat which always started with a blank string for some reason, this fixed that
                txt = txt + list.get(i) + separator;
        }
        txt = txt + list.get(list.size() - 1); // Add final item in list without separator
        return txt;
    }

    // Split string into list by some separator
    public static List<String> toList(String str, String separator) {
        List<String> list = new ArrayList<String>();
        for (String item:str.split(separator)) { // Split function converts to an array so we want to convert that to a list object
            list.add(item);
        }
        return list;
    }

    // Formats time given hour and minute
    public static String formatTime(int hour, int min) {
        // Determine whether AM or PM and convert military to standard time
        String meridiem = "AM";
        int formatHour = hour;
        if (formatHour >= 12 && formatHour < 24) {
            meridiem = "PM";
            formatHour -= 12;
        }
        if (formatHour == 0 || formatHour == 24) {
            formatHour = 12;
        }
        formatHour = Math.abs(formatHour);

        // Format time to two digits
        String formatMin = String.format("%02d", min);

        return formatHour + ":" + formatMin + meridiem;
    }

    // Formats date given day, month and year
    public static String formatDate(int day, int month, int year){
        String formatDate = String.format("%02d", day);
        return month + "/" + day + "/" + year;
    }

    // Formats date and time for storage in database
    public static String formatDateTimeDB(int day, int month, int year, int hour, int minute) {
        return month + "/" + day + "/" + year + " " + hour + ":" + minute;
    }

    // Formats date and time for storage in database with Calendar object as input
    public static String formatDateTimeDB(Calendar cal) {
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int year = cal.get(Calendar.YEAR);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        return month + "/" + day + "/" + year + " " + hour + ":" + minute;
    }

    public static Calendar dateToCal(String date) {
        Calendar calendar = Calendar.getInstance();
        String[] dateSplit = date.split("/");
        calendar.set(
                safeParseInt(dateSplit[2], calendar.get(Calendar.YEAR)),
                safeParseInt(dateSplit[0], calendar.get(Calendar.MONTH)),
                safeParseInt(dateSplit[1], calendar.get(Calendar.DATE))
        );
        return calendar;
    }

    // Gets current time in hours and minutes
    // Hour is at index 0 (in military time)
    // Minutes are at index 1
    public static int[] currentTime() {
        final Calendar c = Calendar.getInstance();
        int[] time = {c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE)};
        return time;
    }

    // Gets today's date
    // Day is at index 0
    // Month is at index 1
    // Year is at index 2
    public static int[] currentDate() {
        final Calendar c = Calendar.getInstance();
        int[] date = {c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH), c.get(Calendar.YEAR)};
        return date;
    }

    // Converts formatted date time String to a Calendar object
    public static Calendar toDate(String formatted) {
        final Calendar setDay = Calendar.getInstance();
        String[] dateTime = formatted.split(" ");
        String[] date = formatted.split("/");
        String[] time = formatted.split(":");
        setDay.set(Integer.parseInt(date[2]),
                Integer.parseInt(date[0]),
                Integer.parseInt(date[1]),
                Integer.parseInt(time[0]),
                Integer.parseInt(time[1]));

        return setDay;
    }

    // Checks to see if a frequency task is active
    public static boolean activeRepeatTask(Task task) {
        if (task.getTaskType() == TaskType.REPEAT_TASK) {
            Calendar calendar = Calendar.getInstance();

            switch (task.getFreqType()) {
                case FrequencyType.MONTHS:
                    int monthIndex = calendar.get(Calendar.MONTH) - 1; // Gets an index based on the current month of the year

                    // Used to determine if the last time a repeat task was finished was this month
                    boolean monthTest = false;
                    try {
                        int lastFinishedMonthIndex = Integer.parseInt(task.getLastFinished()); // If the frequency type is monthly the last finished will be the month number - 1
                        monthTest = monthIndex != lastFinishedMonthIndex; // Check if the month we are in doesn't match the last time the task was finished
                    } catch (NumberFormatException e) {
                        monthTest = true; // Since the task has never been finished just set monthTest to true meaning the task would be active if we are in the correct month
                    }

                    if (task.getFreqData().charAt(monthIndex) == '1' && monthTest) // If the task hasn't been finished this month and should be finished this month
                        return true;
                    break;
                case FrequencyType.WEEKS:
                    if (task.getLastFinished().isEmpty()) // If the task has never been finished return true
                        return true;
                    int timeBetween = Integer.parseInt(task.getFreqData());
                    Calendar nextWeek = Calendar.getInstance();
                    nextWeek.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(task.getLastFinished())); // If frequency type is weekly the last finished will be a week of the year
                    nextWeek.add(Calendar.WEEK_OF_YEAR, timeBetween); // Adds number of weeks between each time a task should be active to the last time this task was finished to determine the next deadline
                    if (calendar.get(Calendar.WEEK_OF_YEAR) == nextWeek.get(Calendar.WEEK_OF_YEAR)) // Checks if this week matches the week it should be completed
                        return true;
                    break;
                case FrequencyType.DAYS:
                    int lastFinishedDayIndex = safeParseInt(task.getLastFinished(), -1); // Default is -1 so that it is guaranteed that that the task will be active if today is the appropriate day
                    int dayIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1; // Index used to get the character in the binary flags

                    if (task.getFreqData().charAt(dayIndex) == '1' && dayIndex != lastFinishedDayIndex) // If the task hasn't been finished today and should be finished on the current day
                        return true;
                    break;
                case FrequencyType.DATE:
                    String[] date = task.getFreqData().split("-"); // If frequency type is on a certain day the frequency data will be a date

                    Calendar dayDue = Calendar.getInstance();
                    dayDue.set(Integer.parseInt(date[2]), Integer.parseInt(date[1]), Integer.parseInt(date[0]));

                    int lastYear = Integer.parseInt(task.getLastFinished()); // If frequency type is on a certain day the last finished data will be the year the task was last finished

                    if (calendar.get(Calendar.YEAR) != lastYear && calendar.get(Calendar.DAY_OF_YEAR) == dayDue.get(Calendar.DAY_OF_YEAR)) // Check if the date today is the due date of the task and check if it hasn't already been finished this year
                        return true;
                    break;
            }
        }
        return false;
    }

    // This method safely parses an int so that a NumberFormatException is caught
    // when a string is parsed for an integer and it does not contain an integer
    // Instead returns a default value if a NumberFormatException is caught
    public static int safeParseInt(String str, int defaultValue) {
        int value;
        try {
            value = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            value = defaultValue;
        }
        return value;
    }

    // This method returns the id of a profile picture resource to be displayed as the user's profile image
    public static int getProfileImage(AppCompatActivity activity, User user) {
        Canvas canvas = new Canvas();

        String race;
        if (user.getRaceID() == DetailsActivity.HUMAN) {
            race = "human";
        } else if (user.getRaceID() == DetailsActivity.ORC) {
            race = "orc";
        } else {
            race = "elf";
        }
        int skin = user.getColorID() + 1;
        String gender = user.getGender() == DetailsActivity.MALE ? "male" : "female";
        String name = race + "_" + gender + "_" + skin;
        Log.v("FILE", name);
        return activity.getResources().getIdentifier(name, "drawable", activity.getPackageName());
    }
}
