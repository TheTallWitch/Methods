package com.gearback.methods;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.content.FileProvider;
import android.support.v4.graphics.TypefaceCompatUtil;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TypefaceSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

public class Methods {

    public static final String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATEFORMAT2 = "MM/dd/yyyy HH:mm:ss a";

    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }

    public int CheckConnectionStatus(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi.isConnectedOrConnecting ()) {
            return 1;
        } else if (mobile.isConnectedOrConnecting ()) {
            return 2;
        } else {
            return 0;
        }
    }

    public Bitmap GetBitmapFromAssets(Activity mainActivity, String filepath) {
        AssetManager assetManager = mainActivity.getAssets();
        InputStream istr = null;
        Bitmap bitmap = null;

        try {
            istr = assetManager.open(filepath);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException ioe) {
            // manage exception
        } finally {
            if (istr != null) {
                try {
                    istr.close();
                } catch (IOException e) {
                }
            }
        }

        return bitmap;
    }

    public String RandomNumber(int num) {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        String randomLetters = "0123456789";
        int randomLength = generator.nextInt(randomLetters.length());
        for (int i = 0; i < num; i++){
            randomStringBuilder.append(randomLetters.charAt(randomLength));
        }
        return randomStringBuilder.toString();
    }
    public String UserGenerator() {
        String newtoken = "";
        Calendar c = Calendar.getInstance();
        newtoken = "user" + RandomNumber(1) + (c.get(Calendar.YEAR) - 2000) + RandomNumber(1) + c.get(Calendar.MONTH) + RandomNumber(2);
        return newtoken;
    }

    public String GetTimeString(int hour) {
        String time = "";
        if (hour <= 11) {
            time = hour + " ق.ظ";
        }
        else {
            time = hour + " ب.ظ";
        }
        return time;
    }

    public Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }

    public Bitmap getScreenShot(View view) {
        View temp = view.getRootView();
        View screenView = temp;
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public void shareImage(Bitmap bitmap, Activity activity) {
        try {
            File cachePath = new File(activity.getCacheDir(), "images");
            cachePath.mkdirs();
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png");
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

            File imagePath = new File(activity.getCacheDir(), "images");
            File newFile = new File(imagePath, "image.png");
            Uri contentUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".fileprovider", newFile);

            if (contentUri != null) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.setDataAndType(contentUri, activity.getContentResolver().getType(contentUri));
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);

                try {
                    activity.startActivity(Intent.createChooser(shareIntent, "رکورد زدم!"));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(activity, activity.getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void shareImage(String fileName, Activity activity){
        final String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screenshots";
        File file = new File(dirPath, fileName);
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");

        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        try {
            activity.startActivity(Intent.createChooser(intent, "رکورد زدم!"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, activity.getString(R.string.error), Toast.LENGTH_SHORT).show();
        }
    }

    public String TimeDifference(String date) {
        Date then = StringDateToDate(date, DATEFORMAT);
        Calendar thenCal = Calendar.getInstance();
        thenCal.setTime(then);
        Calendar nowCal = Calendar.getInstance();
        long seconds = (nowCal.getTimeInMillis() - thenCal.getTimeInMillis())/1000;
        long minutes = seconds/60;
        long hours = minutes/60;
        long days = hours/24;
        long weeks = days/7;
        long months = days/30;
        long years = days/365;
        if (seconds == 0) {
            return "الان";
        }
        else if (seconds < 60) {
            return (int) Math.abs(Math.round(seconds)) + " ثانیه پیش";
        }
        else if (minutes >= 1 && minutes < 60) {
            return (int) Math.abs(Math.round(minutes)) + " دقیقه پیش";
        }
        else if (hours >= 1 && hours < 24) {
            return (int) Math.abs(Math.round(hours)) + " ساعت پیش";
        }
        else if (days >= 1 && days < 30) {
            return (int) Math.abs(Math.round(days)) + " روز پیش";
        }
        else if (days >= 7 && days < 30) {
            return (int) Math.abs(Math.round(weeks)) + " هفته پیش";
        }
        else if (days >= 30 && days < 365) {
            return (int) Math.abs(Math.round(months)) + " ماه پیش";
        }
        else {
            return (int) Math.abs(Math.round(years)) + " سال پیش";
        }
    }

    public String GetFreeStorage() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable = (long)stat.getBlockSize() * (long)stat.getAvailableBlocks();
        int count = 0;
        String unit = " KB";
        while (bytesAvailable >= 1024) {
            count++;
            bytesAvailable = bytesAvailable/1024;
        }
        if (count == 0) {
            unit = " B";
        }
        else if (count == 1) {
            unit = " KB";
        }
        else if (count == 2) {
            unit = " MB";
        }
        else if (count == 3) {
            unit = " GB";
        }
        return String.valueOf(bytesAvailable) + unit;
    }

    public String GetFileSize(String path) {
        File file = new File(path);
        long length = file.length();
        int count = 0;
        String unit = " KB";
        while (length >= 1024) {
            count++;
            length = length/1024;
        }
        if (count == 0) {
            unit = " B";
        }
        else if (count == 1) {
            unit = " KB";
        }
        else if (count == 2) {
            unit = " MB";
        }
        else if (count == 3) {
            unit = " GB";
        }
        return String.valueOf(length) + unit;
    }

    public String GetFileListSize(List<String> files) {
        long bytes = 0;
        for (int i = 0; i < files.size(); i++) {
            File file = new File(files.get(i));
            long length = file.length();
            bytes += length;
        }

        int count = 0;
        String unit = " KB";
        while (bytes >= 1024) {
            count++;
            bytes = bytes/1024;
        }
        if (count == 0) {
            unit = " B";
        }
        else if (count == 1) {
            unit = " KB";
        }
        else if (count == 2) {
            unit = " MB";
        }
        else if (count == 3) {
            unit = " GB";
        }
        return String.valueOf(bytes) + unit;
    }

    public String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString, minuteString;
        int hours = (int) (milliseconds / (1000*60*60));
        int minutes = (int) (milliseconds % (1000*60*60)) / (1000*60);
        int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
        if (hours > 0) {
            finalTimerString = hours + ":";
        }
        minuteString = "" + minutes;
        if (seconds < 10) {
            secondsString = "0" + seconds;
        }
        else {
            secondsString = "" + seconds;
        }
        finalTimerString = finalTimerString + minuteString + ":" + secondsString;
        return finalTimerString;
    }

    public int getProgressPercentage(long currentDuration, long totalDuration) {
        Double percentage = (double) 0;
        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        percentage = (((double)currentSeconds)/totalSeconds) * 100;
        return percentage.intValue();
    }

    public int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double)progress) / 100) * totalDuration);

        return currentDuration * 1000;
    }

    public void ShowSpinner(ProgressDialog loader, Context activity) {
        loader = new ProgressDialog(activity, R.style.SpinnerTheme);
        loader.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loader.setCancelable(false);
        loader.show();
    }

    public void ShowCustomSpinner(ProgressDialog loader, Context activity) {
        loader = new ProgressDialog(activity);
        String message = activity.getString(R.string.please_wait);
        SpannableString spannableString =  new SpannableString(message);

        CustomTypefaceSpan customTypefaceSpan = new CustomTypefaceSpan(message, Typeface.createFromAsset(activity.getAssets(), "iranyekanregular.ttf"));
        spannableString.setSpan(customTypefaceSpan, 0, message.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        loader.setMessage(spannableString);
        loader.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loader.setIndeterminate(true);
        loader.setCancelable(false);
        loader.show();
    }

    public void HideSpinner(ProgressDialog loader) {
        if (loader != null) {
            loader.hide();
        }
    }

    public void HideKeyboard(Activity activity, View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public Bitmap getFontBitmap(Context context, String text, int color, int fontSizeSP, String fontName) {

        int fontSizePX = DPtoPX(fontSizeSP);
        int pad = (fontSizePX / 9);
        Paint paint = new Paint();
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), fontName);
        paint.setAntiAlias(true);
        paint.setTypeface(typeface);
        paint.setColor(color);
        paint.setTextSize(fontSizePX);

        int textWidth = (int) (paint.measureText(text) + pad * 2);
        int height = (int) (fontSizePX / 0.75);
        Bitmap bitmap = Bitmap.createBitmap(textWidth, height, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        float xOriginal = pad;
        canvas.drawText(text, xOriginal, fontSizePX, paint);
        return bitmap;
    }

    public Bitmap getCustomFontBitmap(Context context, String text, int color, int fontSizeSP, String fontName) {
        int fontSizePX = DPtoPX(fontSizeSP);
        int pad = (fontSizePX / 9);
        Paint paint = new Paint();
        Typeface typeface;
        if (fontName.equals("")) {
            typeface = Typeface.DEFAULT;
        }
        else {
            typeface = Typeface.createFromAsset(context.getAssets(), fontName);
        }
        paint.setAntiAlias(true);
        paint.setTypeface(typeface);
        paint.setColor(color);
        paint.setTextSize(fontSizePX);

        int textWidth = (int) (paint.measureText(text) + pad * 2);
        int height = (int) (fontSizePX);
        Bitmap bitmap = Bitmap.createBitmap(textWidth, height, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        float xOriginal = pad;
        canvas.drawText(text, xOriginal, fontSizePX - pad, paint);
        return bitmap;
    }

    public Bitmap getFontBitmapBottom(Context context, String text, int color, int fontSizeSP, String fontName) {
        int fontSizePX = DPtoPX(fontSizeSP);
        int pad = (fontSizePX / 9);
        Paint paint = new Paint();
        Typeface typeface;
        if (fontName.equals("")) {
            typeface = Typeface.DEFAULT;
        }
        else {
            typeface = Typeface.createFromAsset(context.getAssets(), fontName);
        }
        paint.setAntiAlias(true);
        paint.setTypeface(typeface);
        paint.setColor(color);
        paint.setTextSize(fontSizePX);

        int textWidth = (int) (paint.measureText(text) + pad * 2);
        int height = (int) (fontSizePX);
        Bitmap bitmap = Bitmap.createBitmap(textWidth, height, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        float xOriginal = pad;
        canvas.drawText(text, xOriginal, fontSizePX + 12, paint);
        return bitmap;
    }

    public int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


    public boolean isInternetAvailable(Context activity) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    public int DPtoPX(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public int PXtoDP(float px){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return Math.round(dp);
    }

    public String BitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public Bitmap StringToBitmap(String image) {
        byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    public String calendarToString(Calendar calendar, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(calendar.getTime());
    }
    public Date getLocalDate(String OurDate, String format)
    {
        try
        {
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date value = formatter.parse(OurDate);

            SimpleDateFormat dateFormatter = new SimpleDateFormat(format); //this format changeable
            dateFormatter.setTimeZone(TimeZone.getDefault());
            OurDate = dateFormatter.format(value);
        }
        catch (Exception e)
        {
            OurDate = "00-00-0000 00:00";
        }
        return StringDateToDate(OurDate, format);
    }

    public String getLocalDateString(String OurDate, String format)
    {
        try
        {
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date value = formatter.parse(OurDate);

            SimpleDateFormat dateFormatter = new SimpleDateFormat(format); //this format changeable
            dateFormatter.setTimeZone(TimeZone.getDefault());
            OurDate = dateFormatter.format(value);
        }
        catch (Exception e)
        {
            OurDate = "00-00-0000 00:00";
        }
        return OurDate;
    }

    public Date GetUTCdatetimeAsDate(Calendar calendar, String format)
    {
        //note: doesn't check for null
        return StringDateToDate(GetUTCdatetimeAsString(calendar, format), format);
    }

    public String GetUTCdatetimeAsString(Calendar calendar, String format)
    {
        final SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(calendar.getTime());

        return utcTime;
    }

    public Date StringDateToDate(String StrDate, String format)
    {
        Date dateToReturn = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);

        try
        {
            dateToReturn = (Date)dateFormat.parse(StrDate);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return dateToReturn;
    }

    public Bitmap DecodeURI(String filePath){

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Only scale if we need to
        // (16384 buffer for img processing)
        Boolean scaleByHeight = Math.abs(options.outHeight - 100) >= Math.abs(options.outWidth - 100);
        if(options.outHeight * options.outWidth * 2 >= 16384){
            // Load, scaling to smallest power of 2 that'll get it <= desired dimensions
            double sampleSize = scaleByHeight
                    ? options.outHeight / 100
                    : options.outWidth / 100;
            options.inSampleSize =
                    (int)Math.pow(2d, Math.floor(
                            Math.log(sampleSize)/Math.log(2d)));
        }

        // Do the actual decoding
        options.inJustDecodeBounds = false;
        options.inTempStorage = new byte[512];
        Bitmap output = BitmapFactory.decodeFile(filePath, options);

        return output;
    }
    public Bitmap AddPaddingBitmap(Bitmap bmp, int borderSize) {
        Bitmap bmpWithBorder = Bitmap.createBitmap(bmp.getWidth() + borderSize * 2, bmp.getHeight() + borderSize * 2, bmp.getConfig());
        Canvas canvas = new Canvas(bmpWithBorder);
        canvas.drawColor(Color.TRANSPARENT);
        canvas.drawBitmap(bmp, borderSize, borderSize, null);
        return bmpWithBorder;
    }

    public static int convertDiptoPix(Context context, float dip) {
        int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
        return value;
    }
    public Bitmap TextAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.0f); // round
        int height = (int) (baseline + paint.descent() + 0.0f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }
    public String ReverseNumber(String str) {
        String string = str;
        string = string.replace("۱", "1");
        string = string.replace("۲", "2");
        string = string.replace("۳", "3");
        string = string.replace("۴", "4");
        string = string.replace("۵", "5");
        string = string.replace("۶", "6");
        string = string.replace("۷", "7");
        string = string.replace("۸", "8");
        string = string.replace("۹", "9");
        string = string.replace("۰", "0");
        return string;
    }
    public String ReplaceNumber(String str) {
        String string = str;
        string = string.replace("1", "۱");
        string = string.replace("2", "۲");
        string = string.replace("3", "۳");
        string = string.replace("4", "۴");
        string = string.replace("5", "۵");
        string = string.replace("6", "۶");
        string = string.replace("7", "۷");
        string = string.replace("8", "۸");
        string = string.replace("9", "۹");
        string = string.replace("0", "۰");
        return string;
    }
    public String ReplaceNumber(int str) {
        String string = String.valueOf(str);
        string = string.replace("1", "۱");
        string = string.replace("2", "۲");
        string = string.replace("3", "۳");
        string = string.replace("4", "۴");
        string = string.replace("5", "۵");
        string = string.replace("6", "۶");
        string = string.replace("7", "۷");
        string = string.replace("8", "۸");
        string = string.replace("9", "۹");
        string = string.replace("0", "۰");
        return string;
    }
    public void deleteFile(String file, Context context) {
        try {
            context.deleteFile(file);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void writeToFile(String file, String data,Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(file, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String readFromFile(String file, Context context) {

        String ret = "";
        try {
            InputStream inputStream = context.openFileInput(file);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            ret = "";
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }

    public String RandomWord(int num) {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        String randomLetters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        int randomLength = generator.nextInt(randomLetters.length());
        for (int i = 0; i < num; i++){
            randomStringBuilder.append(randomLetters.charAt(randomLength));
        }
        return randomStringBuilder.toString();
    }
    public String TokenGenerator() {
        String newtoken = "";
        Calendar c = Calendar.getInstance();
        newtoken = RandomWord(1) + (c.get(Calendar.YEAR) - 2000) + RandomWord(2) + c.get(Calendar.MONTH) + RandomWord(2) + c.get(Calendar.DAY_OF_MONTH) + RandomWord(2) + c.get(Calendar.HOUR) + RandomWord(2) + c.get(Calendar.MINUTE) + RandomWord(2) + c.get(Calendar.SECOND) + RandomWord(2);
        return newtoken;
    }

    public String GetUserCountry(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                return simCountry.toUpperCase(Locale.US);
            }
            else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    return networkCountry.toUpperCase(Locale.US);
                }
            }
        }
        catch (Exception e) { Log.d("Country Code", "exception"); }
        return null;
    }
    public String readFromAssets(Context context, String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(filename)));

        // do reading, usually loop until end of file reading
        StringBuilder sb = new StringBuilder();
        String mLine = reader.readLine();
        while (mLine != null) {
            sb.append(mLine); // process line
            mLine = reader.readLine();
        }
        reader.close();
        return sb.toString();
    }
}