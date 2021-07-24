package com.example.aditmail.fumida.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Build;
import android.os.Environment;

import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.aditmail.fumida.Fumigasi.FragmentFormFumigasi;
import com.example.aditmail.fumida.PestControl.FragmentFormPest;
import com.example.aditmail.fumida.R;
import com.example.aditmail.fumida.TermiteControl.FragmentFormTermite;
import com.example.aditmail.fumida.WorkReport.FragmentFormWorkReport;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SignatureActivity extends AppCompatActivity {

    String Pesan;

    Button mClear_ConsultantPest, mGetSign_ConsultantPest;
    File file;
    LinearLayout mContentConsultant;
    View view_ttdPestConsultant;
    signature PestConsultant_Signature;
    Bitmap bitmap;

    String DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/Fumida_TandaTangan/";
    public static String pic_name = new SimpleDateFormat("dd-MM-yy_hh.mm.ss", Locale.getDefault()).format(new Date());
  //  String StoredPath = DIRECTORY + pic_name + ".png";
  String StoredPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);

        Intent getIntent = getIntent();
        Pesan = getIntent.getStringExtra(FragmentFormTermite.Pesan_Extra);

        switch (Pesan) {
            case "TTD_PelangganPestControl":
                setTitle("Tanda Tangan Pelanggan Pest Control Disini");
                StoredPath = DIRECTORY + "PelangganPest_" + FragmentFormPest.namaPelanggan + "_" + pic_name + ".png";
                break;
            case "TTD_ConsultantPestControl":
                setTitle("Tanda Tangan Consultant Pest Control Disini");
                StoredPath = DIRECTORY + "ConsultantPest_" + TampilanMenuUtama.username + "_" + pic_name + ".png";
                break;
            case "TTD_PelangganFumigasi":
                setTitle("Tanda Tangan Pelanggan Fumigasi Disini");
                StoredPath = DIRECTORY + "PelangganFumigasi_" + FragmentFormFumigasi.namaPelanggan + "_" + pic_name + ".png";
                break;
            case "TTD_ConsultantFumigasi":
                setTitle("Tanda Tangan Consultant Fumigasi Disini");
                StoredPath = DIRECTORY + "ConsultantFumigasi_" + TampilanMenuUtama.username + "_" + pic_name + ".png";
                break;
            case "TTD_PelangganTermiteControl":
                setTitle("Tanda Tangan Pelanggan Termite Control Disini");
                StoredPath = DIRECTORY + "PelangganTermite_" + FragmentFormTermite.namaPelanggan + "_" + pic_name + ".png";
                break;
            case "TTD_ConsultantTermiteControl":
                setTitle("Tanda Tangan Consultant Termite Control Disini");
                StoredPath = DIRECTORY + "ConsultantTermite_" + TampilanMenuUtama.username + "_" + pic_name + ".png";
                break;
            case "TTD_PelangganWorksReport":
                setTitle("Tanda Tangan Pelanggan Disini");
                StoredPath = DIRECTORY + "PelangganWorkReport_" + FragmentFormWorkReport.namaPelanggan + "_" + pic_name + ".png";
                break;
            case "TTD_ConsultantWorksReport":
                setTitle("Tanda Tangan Consultant Disini");
                StoredPath = DIRECTORY + "ConsultantWorkReport_" + TampilanMenuUtama.username + "_" + pic_name + ".png";
                break;
        }

        mContentConsultant = findViewById(R.id.canvasTTD_LayoutConsultant);
        PestConsultant_Signature = new signature(getApplicationContext(), null);
        PestConsultant_Signature.setBackgroundColor(Color.WHITE);

//        mContentConsultant.addView(PestConsultant_Signature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mContentConsultant.addView(PestConsultant_Signature, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mClear_ConsultantPest = findViewById(R.id.clear_ttd);
        mGetSign_ConsultantPest = findViewById(R.id.getSign_ttd);
        mGetSign_ConsultantPest.setEnabled(false);
        view_ttdPestConsultant = mContentConsultant;

        mGetSign_ConsultantPest.setOnClickListener(onButtonClick);
        mClear_ConsultantPest.setOnClickListener(onButtonClick);

        file = new File(DIRECTORY);
        if (!file.exists()){
            file.mkdir();
        }
    }

    Button.OnClickListener onButtonClick = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            if (v == mClear_ConsultantPest) {
                Log.v("log_tag", "Panel Cleared");
                PestConsultant_Signature.clear();
                mGetSign_ConsultantPest.setEnabled(false);
            } else if (v == mGetSign_ConsultantPest) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignatureActivity.this);

                builder.setCancelable(true);
                builder.setTitle("Apakah Anda Yakin Menyimpan Tanda Tangan Tersebut?");
                builder.setMessage("Apabila Yakin Tekan 'OK'");

                builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.v("log_tag", "Panel Saved");
                        if (Build.VERSION.SDK_INT >= 23) {
                            isStoragePermissionGranted();
                        } else {
                            view_ttdPestConsultant.setDrawingCacheEnabled(true);
                            PestConsultant_Signature.save(view_ttdPestConsultant, StoredPath);
                            Toast.makeText(getApplicationContext(), "Berhasil Menyimpan Tanda Tangan", Toast.LENGTH_SHORT).show();
                            recreate();
                        }
                    }
                });
                builder.show();
            }
        }
    };

    public void isStoragePermissionGranted(){
        if (Build.VERSION.SDK_INT >=23){
            if (getApplicationContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){

                view_ttdPestConsultant.setDrawingCacheEnabled(true);
                PestConsultant_Signature.save(view_ttdPestConsultant, StoredPath);
                Toast.makeText(getApplicationContext(), "Berhasil Menyimpan Tanda Tangan", Toast.LENGTH_SHORT).show();
                recreate();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},1);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[]permissons, @NonNull int[]grantResults){
        super.onRequestPermissionsResult(requestCode,permissons,grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            view_ttdPestConsultant.setDrawingCacheEnabled(true);
            PestConsultant_Signature.save(view_ttdPestConsultant, StoredPath);
            Toast.makeText(getApplicationContext(), "Berhasil Menyimpan Tanda Tangan", Toast.LENGTH_SHORT).show();
            recreate();
        }else{
            Toast.makeText(this, "Aplikasi tidak diberikan izin untuk menyimpan data kedalam penyimpanan. Sehingga aplikasi tidak dapat berjalan normal." +
                    "Mohon pertimbangkan untuk memberikan Izin akses.", Toast.LENGTH_LONG).show();
        }
    }

    public class signature extends View {

        private static final float STROKE_WIDTH = 4.5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        @SuppressLint("WrongThread")
        public void save(View v, String storedPath) {
            Log.v("log_tag", "width:" + v.getWidth());
            Log.v("log_tag", "Height:" + v.getHeight());
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(mContentConsultant.getWidth(), mContentConsultant.getHeight(), Bitmap.Config.RGB_565);
            }
            Canvas canvas = new Canvas(bitmap);
            try {
                FileOutputStream mFileOutputStream = new FileOutputStream(storedPath);
                v.draw(canvas);
                //Konversi ke PNG
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, mFileOutputStream);

                //SharedPreferences.Editor editor = prefs.edit();
                //editor.putString(Image, StoredPath);
                //editor.apply();

                switch (Pesan) {
                    case "TTD_PelangganPestControl": {
                        Intent gambaran = new Intent();
                        //Intent gambar = new Intent(getApplicationContext(), FormSurveiPestControl.class);
                        gambaran.putExtra("imagePath_PestPelanggan", StoredPath);
                        setResult(RESULT_OK, gambaran);
                        finish();

                        break;
                    }
                    case "TTD_ConsultantPestControl": {
                        Intent gambaran = new Intent();
                        //Intent gambar = new Intent(getApplicationContext(), FormSurveiPestControl.class);
                        gambaran.putExtra("imagePath_PestConsultant", StoredPath);
                        setResult(RESULT_OK, gambaran);
                        finish();

                        break;
                    }
                    case "TTD_PelangganTermiteControl": {
                        Intent gambaran = new Intent();
                        //Intent gambar = new Intent(getApplicationContext(), FormSurveiPestControl.class);
                        gambaran.putExtra("imagePath_TermitePelanggan", StoredPath);
                        setResult(RESULT_OK, gambaran);
                        finish();

                        break;
                    }
                    case "TTD_ConsultantTermiteControl": {
                        Intent gambaran = new Intent();
                        //Intent gambar = new Intent(getApplicationContext(), FormSurveiPestControl.class);
                        gambaran.putExtra("imagePath_TermiteConsultant", StoredPath);
                        setResult(RESULT_OK, gambaran);
                        finish();

                        break;
                    }
                    case "TTD_PelangganFumigasi": {
                        Intent gambaran = new Intent();
                        //Intent gambar = new Intent(getApplicationContext(), FormSurveiPestControl.class);
                        gambaran.putExtra("imagePath_FumigasiPelanggan", StoredPath);
                        setResult(RESULT_OK, gambaran);
                        finish();

                        break;
                    }
                    case "TTD_ConsultantFumigasi": {
                        Intent gambaran = new Intent();
                        //Intent gambar = new Intent(getApplicationContext(), FormSurveiPestControl.class);
                        gambaran.putExtra("imagePath_FumigasiConsultant", StoredPath);
                        setResult(RESULT_OK, gambaran);
                        finish();

                        break;
                    }
                    case "TTD_PelangganWorksReport": {
                        Intent gambaran = new Intent();
                        //Intent gambar = new Intent(getApplicationContext(), FormSurveiPestControl.class);
                        gambaran.putExtra("imagePath_WorkReportPelanggan", StoredPath);
                        setResult(RESULT_OK, gambaran);
                        finish();
                        break;
                    }
                    case "TTD_ConsultantWorksReport": {
                        Intent gambaran = new Intent();
                        //Intent gambar = new Intent(getApplicationContext(), FormSurveiPestControl.class);
                        gambaran.putExtra("imagePath_WorkReportConsultant", StoredPath);
                        setResult(RESULT_OK, gambaran);
                        finish();
                        break;
                    }
                }

                /*
                Intent gambaran = new Intent();
                //Intent gambar = new Intent(getApplicationContext(), FormSurveiPestControl.class);
                gambaran.putExtra("imagePath_PestConsultant", StoredPath);
                setResult(RESULT_OK, gambaran);
                finish();

*/
                mFileOutputStream.flush();
                mFileOutputStream.close();

            } catch (Exception e) {
                Log.v("log_tag", e.toString());
            }
        }

        public void clear() {
            path.reset();
            invalidate();
            mGetSign_ConsultantPest.setEnabled(false);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            mGetSign_ConsultantPest.setEnabled(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:
                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:
                    debug("Ignored touch event:" + event.toString());
                    return false;
            }
            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void debug(String string) {
            Log.v("log_tag", string);
        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }
}
