package com.example.aditmail.fumida.PestControl;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aditmail.fumida.Activities.OpenMaps;
import com.example.aditmail.fumida.R;
import com.example.aditmail.fumida.Activities.SignatureActivity;
import com.example.aditmail.fumida.Settings.TrackGPS;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class FragmentFormPest extends Fragment implements View.OnClickListener {

    private static final String TAG = "TabFormPest";

    // <-------------------------- INTENT EXTRA SEND TO REVIEW ACTIVITIES -------------------------->
    private final static String path_fumida = "com.example.aditmail.fumida";
    public final static String Pesan_Extra = path_fumida + ".Pesan";

    public final static String PestRev_ID_Client = path_fumida + ".ClientID";
    public final static String PestRev_Nama_Client = path_fumida + ".NamaClient";
    public final static String PestRev_Kategori_Client = path_fumida + ".Kategori";
    public final static String PestRev_Alamat_Client = path_fumida + ".Alamat";
    public final static String PestRev_Hp_Client = path_fumida + ".Hp";
    public final static String PestRev_Email_DB = path_fumida + ".Email_DB";

    public final static String PestRev_Penanganan_DB = path_fumida + ".Penanganan_DB";
    public final static String PestRev_QtyPenanganan_DB = path_fumida + ".Qty_Penanganan_DB";
    public final static String PestRev_Hama_DB = path_fumida + ".Hama_DB";
    public final static String PestRev_HamaLainnya_DB = path_fumida + ".HamaLainnya_DB";

    public final static String PestRev_Chemical_DB = path_fumida + ".Chemical_DB";

    public final static String PestRev_MetodeKendali_DB = path_fumida + ".MetodeKendali_DB";
    public final static String PestRev_QtyLemTikus_DB = path_fumida + ".QtyLemTikus_DB";
    public final static String PestRev_QtyPerangkapTikus_DB = path_fumida + ".QtyPerangkapTikus_DB";
    public final static String PestRev_QtyUmpanTikus_DB = path_fumida + ".QtyUmpanTikus_DB";
    public final static String PestRev_QtyUmpanTikusIndoor_DB = path_fumida + ".QtyUmpanTikusIndoor_DB";
    public final static String PestRev_QtyPohonLalat_DB = path_fumida + ".QtyPohonLalat_DB";
    public final static String PestRev_QtyBlackHole_DB = path_fumida + ".QtyBlackHole_DB";
    public final static String PestRev_JenisFumigasi_DB = path_fumida + ".JenisFumigasi_DB";
    public final static String PestRev_QtyFlyCatcher_DB = path_fumida + ".QtyFlyCatcher_DB";
    public final static String PestRev_MetodeLain_DB = path_fumida + ".MetodeLain_DB";

    public final static String PestRev_LuasIndoor_DB = path_fumida + ".LuasIndoor_DB";
    public final static String PestRev_LuasOutdoor_DB = path_fumida + ".LuasOutdoor_DB";

    public final static String PestRev_JenisKerja_Client = path_fumida + ".JenisKerja";
    public final static String PestRev_DurasiKontrak_DB = path_fumida + ".DurasiKontrak_DB";
    public final static String PestRev_PenawaranHarga_DB = path_fumida + ".PenawaranHarga_DB";
    public final static String PestRev_GrandTotal_Client = path_fumida + ".GrandTotal";

    public final static String PestRev_Status_Client = path_fumida + ".Status";
    public final static String PestRev_CatatanTambahan_DB = path_fumida + ".CatatanTambahan_DB";

    public final static String PestRev_Keterangan_Client = path_fumida + ".Keterangan";

    public final static String PestRev_LatLong_Consultant = path_fumida + ".LatLong_Consultant";
    public final static String PestRev_LatLong_Alamat_Pelanggan = path_fumida + ".LatLong_AlamatPelanggan";

    // <-------------------------- INTENT EXTRA SEND TO REVIEW ACTIVITIES -------------------------->

    //<---------------------------- Bagian I : Identitas Pelanggan ---------------------------->
    private EditText edt_ClientID_Pest, edt_NamaPelanggan, edt_Alamat, edt_NoHP, edt_Email;
    private Spinner spr_Kategori;

    private ImageView imgPinCircle;
    protected Double lat, lng;

    /*FOR GPS*/
    protected String latlong_Consultant = "0,0";
    protected String latlong_AlamatPelanggan = "0,0";
    protected TrackGPS gps;
    double longitude;
    double latitude;
    //<---------------------------- Bagian I : Identitas Pelanggan ---------------------------->

    //Untuk Simpan Nama TTD
    public static String namaPelanggan_get = "";

    //<---------------------------- Bagian II : Jenis Hama ---------------------------->
    private CheckBox chk_Kecoa, chk_Kutu, chk_Lalat, chk_Cicak, chk_Semut, chk_Tikus, chk_Tawon,
            chk_Nyamuk, chk_HamaLainnya;
    private EditText edt_HamaLainnya;
    //<---------------------------- Bagian II : Jenis Hama ---------------------------->

    //<---------------------------- Bagian III : Metode Penanganan Pest Control ---------------------------->
    private Spinner spr_KategoriPenanganan;

    private EditText edt_QtyPenanganan;
    private TextView txt_Penanganan;
    //<---------------------------- Bagian III : Metode Penanganan Pest Control ---------------------------->

    //<---------------------------- Bagian IV : Jenis Chemical ---------------------------->
    private CheckBox chk_BoricAcid, chk_Cypermethrin, chk_ZetaCypermethrin, chk_Dichlorvos,
            chk_Fipronil, chk_Thiamethoxam;
    //<---------------------------- Bagian IV : Jenis Chemical ---------------------------->

    //<---------------------------- Bagian V : Metode Pengendalian Hama ---------------------------->
    private EditText edt_QtyPerangkapTikus, edt_QtyLemTikus, edt_QtyUmpanIndoor, edt_QtyUmpanOutdoor,
            edt_QtyPohonLalat, edt_QtyBlackHole, edt_QtyFlyCatcher, edt_MetodeLainnya;
    private Spinner spr_Fumigasi;
    private CheckBox chk_Penyemprotan, chk_Pengembunan, chk_PengasapanThermal, chk_PengasapanMini,
            chk_PerangkapTikus, chk_LemTikus, chk_UmpanTikusIndoor, chk_UmpanTikusOutdoor,
            chk_UmpanSemut, chk_GelKecoa, chk_LemKecoa, chk_BlackHole, chk_FlyCatcher,
            chk_PohonLalat, chk_UmpanLalat, chk_MetodeLainnya, chk_Fumigasi;
    //<---------------------------- Bagian V : Metode Pengendalian Hama ---------------------------->

    //<---------------------------- Bagian VI : Area Lokasi dan Kontrak ---------------------------->
    private EditText edt_LuasBangunan, edt_LuasOutdoor;

    private EditText edt_DurasiKontrak, edt_PenawaranHarga, edt_GrandTotal;
    private TextView txt_DurasiKontrak, txt_PenawaranHarga;

    private RadioGroup rdg_Kontrak, rdg_Status;
    private RadioButton rdb_Kontrak, rdb_Berhasil;

    protected String kontrak = "";
    protected String status_kerjasama = "";

    protected double total_harga;
    //<---------------------------- Bagian VI : Area Lokasi dan Kontrak ---------------------------->

    //<---------------------------- Bagian VII : Catatan Tambahan ---------------------------->
    private EditText edt_CatatanTambahan;
    //<---------------------------- Bagian VII : Catatan Tambahan ---------------------------->

    //<---------------------------- Bagian VIII : Tanda Tangan Digital ---------------------------->
    private Button btn_DialogTTD_Pelanggan, btn_DialogTTD_Consultant;
    private CheckBox chk_PernyataanPelanggan, chk_PernyataanConsultant;

    @SuppressLint("StaticFieldLeak")
    public static ImageView img_ttdPelanggan, img_ttdConsultant;
    Bitmap bitmap_PestConsultant, bitmap_PestPelanggan;
    //<---------------------------- Bagian VIII : Tanda Tangan Digital ---------------------------->

    //<---------------------------- Bagian IX : Gambar Upload ---------------------------->
    Button btn_PilihFotoSatu, btn_PilihFotoDua, btn_PilihFotoTiga, btn_PilihFotoEmpat;

    @SuppressLint("StaticFieldLeak")
    public static ImageView img_FotoSatu, img_FotoDua, img_FotoTiga, img_FotoEmpat;
    Space spc_Satu, spc_Dua, spc_Tiga;
    //<---------------------------- Bagian IX : Gambar Upload ---------------------------->

    //Save for PutExtra ------------------------------------------------------------------->
    public static String namaPelanggan;
    private String IDClient, kategoriTempatPelanggan, alamatPelanggan, hpPelanggan, email_DB, Penanganan, qty_penanganan_DB, jenis_hama_DB, hama_lainnya_DB,
            jenis_chemical_DB, metode_kendali_DB, qty_lemTikus_DB, qty_perangkapTikus_DB, qty_umpanTikus_DB, qty_umpanTikusIndoor_DB, qty_pohonLalat_DB,
            qty_blackHole_DB, jenis_fumigasi_DB, qty_FlyCatcher_DB, metodeLain_DB, indoor_DB, outdoor_DB, jenis_kontrak, durasi_kontrak_DB, penawaran_harga_DB,
            total_harga_penawaran, status_kerjasama_kontrak, catatan_tambahan_DB, keterangan;

    //Button Simpan
    private Button btn_SimpanSurvey;
    protected ConnectivityManager conMgr;
    LocationManager locationManager;

    //Request Code..
    private final static int REQ_TTD_PELANGGAN = 1;
    private final static int REQ_TTD_CONSULTANT = 2;

    private final static int REQ_KAMERA_SATU = 3;
    private final static int REQ_KAMERA_DUA = 4;
    private final static int REQ_KAMERA_TIGA = 5;
    private final static int REQ_KAMERA_EMPAT = 6;

    private final static int REQ_GALERI_SATU = 7;
    private final static int REQ_GALERI_DUA = 8;
    private final static int REQ_GALERI_TIGA = 9;
    private final static int REQ_GALERI_EMPAT = 10;

    private final static int REQ_PLACE_PICKER = 11;

    //Untuk Create View Fragment
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_form_pest, container, false);

        InitData();
        CheckButtonHama();
        getCurrentLocationRoute();
        CheckButtonMetodeKendali();
        CekRadioGroup();
        CekSpinner();
        TextWatcher();
        CekTandaTangan();

        return view;
    }

    @Override
    public void onStart() {
        OpenMaps();
        OpenPhoto();
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        if (v == btn_SimpanSurvey) {
            ValidateAndShow();
        }
    }

    private void InitData() {
        // Bagian I : Identitas Pelanggan --------------------------------------------------------------------------------------------->
        edt_ClientID_Pest = view.findViewById(R.id.editText_ClientID_Pest);
        edt_NamaPelanggan = view.findViewById(R.id.editText_NamaPelanggan);
        edt_Alamat = view.findViewById(R.id.editText_Alamat);
        edt_NoHP = view.findViewById(R.id.editText_NoHP);
        edt_Email = view.findViewById(R.id.editText_Email);
        spr_Kategori = view.findViewById(R.id.spinner_Kategori);
        imgPinCircle = view.findViewById(R.id.imgPin);

        //Get Nama Pelanggan
        namaPelanggan_get = edt_NamaPelanggan.getText().toString();
        // Bagian I : Identitas Pelanggan --------------------------------------------------------------------------------------------->

        //Bagian II : Jenis Hama --------------------------------------------------------------------------------------------->
        chk_Semut = view.findViewById(R.id.checkBox_Semut);
        chk_Tikus = view.findViewById(R.id.checkBox_Tikus);
        chk_Kecoa = view.findViewById(R.id.checkBox_Kecoa);
        chk_Nyamuk = view.findViewById(R.id.checkBox_Nyamuk);
        chk_Lalat = view.findViewById(R.id.checkBox_Lalat);
        chk_Kutu = view.findViewById(R.id.checkBox_Kutu);
        chk_Tawon = view.findViewById(R.id.checkBox_Tawon);
        chk_Cicak = view.findViewById(R.id.checkBox_Cicak);
        chk_Fumigasi = view.findViewById(R.id.checkBox_Fumigasi);
        chk_HamaLainnya = view.findViewById(R.id.checkBox_HamaLainnya);
        edt_HamaLainnya = view.findViewById(R.id.editText_HamaLainnya);
        //Bagian II : Jenis Hama --------------------------------------------------------------------------------------------->

        //Bagian III : Jenis Penanganan Pest Control ------------------------------------------------------------------------->
        spr_KategoriPenanganan = view.findViewById(R.id.spinner_KategoriPenanganan);
        edt_QtyPenanganan = view.findViewById(R.id.editText_QtyPenanganan);
        txt_Penanganan = view.findViewById(R.id.textView_Penanganan);
        //Bagian III : Jenis Penanganan Pest Control ------------------------------------------------------------------------->

        //Bagian IV : Jenis Chemical ---------------------------------------------------------------------------------------------------------------->
        chk_BoricAcid = view.findViewById(R.id.checkBox_BoricAcid);
        chk_Cypermethrin = view.findViewById(R.id.checkBox_Cypermethrin);
        chk_ZetaCypermethrin = view.findViewById(R.id.checkBox_ZetaCypermethrin);
        chk_Dichlorvos = view.findViewById(R.id.checkBox_Dichlorvos);
        chk_Fipronil = view.findViewById(R.id.checkBox_Fipronil);
        chk_Thiamethoxam = view.findViewById(R.id.checkBox_Thiamethoxam);
        //Bagian IV : Jenis Chemical ---------------------------------------------------------------------------------------------------------------->

        //Bagian V : Metode Pengendalian Hama --------------------------------------------------------------------------------------------------------->
        chk_Penyemprotan = view.findViewById(R.id.checkBox_Penyemprotan);
        chk_Pengembunan = view.findViewById(R.id.checkBox_Pengembunan);
        chk_PengasapanThermal = view.findViewById(R.id.checkBox_PengasapanThermal);
        chk_PengasapanMini = view.findViewById(R.id.checkBox_PengasapanMini);
        chk_PerangkapTikus = view.findViewById(R.id.checkBox_PerangkapTikus);
        chk_LemTikus = view.findViewById(R.id.checkBox_LemTikus);
        chk_UmpanTikusIndoor = view.findViewById(R.id.checkBox_UmpanTikusIndoor);
        chk_UmpanTikusOutdoor = view.findViewById(R.id.checkBox_UmpanTikusOutdoor);
        chk_UmpanSemut = view.findViewById(R.id.checkBox_UmpanSemut);
        chk_GelKecoa = view.findViewById(R.id.checkBox_UmpanGelKecoa);
        chk_LemKecoa = view.findViewById(R.id.checkBox_PerangkapLemKecoa);
        chk_BlackHole = view.findViewById(R.id.checkBox_InstalasiBlackHole);
        chk_FlyCatcher = view.findViewById(R.id.checkBox_InstalasiFlyCatcher);
        chk_PohonLalat = view.findViewById(R.id.checkBox_PohonLalat);
        chk_UmpanLalat = view.findViewById(R.id.checkBox_UmpanLalat);
        chk_Fumigasi = view.findViewById(R.id.checkBox_Fumigasi);
        chk_MetodeLainnya = view.findViewById(R.id.checkBox_MetodeLainnya);

        edt_QtyPerangkapTikus = view.findViewById(R.id.editText_QtyPerangkapTikus);
        edt_QtyLemTikus = view.findViewById(R.id.editText_QtyLemTikus);
        edt_QtyUmpanIndoor = view.findViewById(R.id.editText_QtyUmpanIndoor);
        edt_QtyUmpanOutdoor = view.findViewById(R.id.editText_QtyUmpanOutdoor);
        edt_QtyPohonLalat = view.findViewById(R.id.editText_QtyPohonLalat);
        edt_QtyBlackHole = view.findViewById(R.id.editText_QtyBlackHole);
        edt_QtyFlyCatcher = view.findViewById(R.id.editText_QtyFlyCatcher);
        edt_MetodeLainnya = view.findViewById(R.id.editText_MetodeLainnya);
        spr_Fumigasi = view.findViewById(R.id.spinner_Fumigasi);
        //Bagian V : Metode Pengendalian Hama --------------------------------------------------------------------------------------------------------->

        //Bagian VI : Area Lokasi dan Jenis Kontrak --------------------------------------------------------------------------------------------------->
        edt_LuasBangunan = view.findViewById(R.id.editText_LuasBangunan);
        edt_LuasOutdoor = view.findViewById(R.id.editText_LuasOutdoor);
        rdg_Kontrak = view.findViewById(R.id.RadioGroup_Kontrak);
        rdg_Status = view.findViewById(R.id.RadioGroup_Status);
        edt_DurasiKontrak = view.findViewById(R.id.editText_DurasiKontrak);
        edt_PenawaranHarga = view.findViewById(R.id.editText_PenawaranHarga);
        edt_GrandTotal = view.findViewById(R.id.editText_GrandTotal);
        txt_DurasiKontrak = view.findViewById(R.id.textView_DurasiKontrak);
        txt_PenawaranHarga = view.findViewById(R.id.textView_PenawaranHarga);

        rdb_Kontrak = view.findViewById(R.id.radioButton_Kontrak);
        rdb_Berhasil = view.findViewById(R.id.radioButton_Berhasil);
        //Bagian VI : Area Lokasi dan Jenis Kontrak --------------------------------------------------------------------------------------------------->

        //Bagian VII : Catatan Tambahan -------------------------------------->
        edt_CatatanTambahan = view.findViewById(R.id.editText_CatatanTambahan);
        //Bagian VII : Catatan Tambahan -------------------------------------->

        //Bagian VIII : Tanda Tangan Digital ------------------------------------------>
        btn_DialogTTD_Pelanggan = view.findViewById(R.id.button_GetTTD_Pelanggan);
        btn_DialogTTD_Consultant = view.findViewById(R.id.button_GetTTD_Consultant);

        //Check Pernyataan
        chk_PernyataanPelanggan = view.findViewById(R.id.checkBox_PernyataanCustomer);
        chk_PernyataanConsultant = view.findViewById(R.id.checkBox_PernyataanPestConsultant);

        //Image TTD
        img_ttdPelanggan = view.findViewById(R.id.imageView_TTD_Pelanggan);
        img_ttdConsultant = view.findViewById(R.id.imageView_TTD_Consultant);
        //Bagian VIII : Tanda Tangan Digital ------------------------------------------>

        //Bagian IX : Upload Foto ------------------------------------------>
        btn_PilihFotoSatu = view.findViewById(R.id.button_FotoSatu);
        img_FotoSatu = view.findViewById(R.id.imageView_FotoSatu);
        btn_PilihFotoDua = view.findViewById(R.id.button_FotoDua);
        img_FotoDua = view.findViewById(R.id.imageView_FotoDua);
        btn_PilihFotoTiga = view.findViewById(R.id.button_FotoTiga);
        img_FotoTiga = view.findViewById(R.id.imageView_FotoTiga);
        btn_PilihFotoEmpat = view.findViewById(R.id.button_FotoEmpat);
        img_FotoEmpat = view.findViewById(R.id.imageView_FotoEmpat);

        spc_Satu = view.findViewById(R.id.Space_Satu);
        spc_Dua = view.findViewById(R.id.Space_Dua);
        spc_Tiga = view.findViewById(R.id.Space_Tiga);
        //Bagian IX : Upload Foto ------------------------------------------>

        //Button Simpan Data ------------------------------------------->
        btn_SimpanSurvey = view.findViewById(R.id.button_SimpanSurvey);
        btn_SimpanSurvey.setOnClickListener(this);
        //Button Simpan Data ------------------------------------------->

        if(getActivity()!= null)
            locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    private void OpenPhoto() {
        //Open galeri
        btn_PilihFotoSatu.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_FotoSatu.setVisibility(View.VISIBLE);
                spc_Satu.setVisibility(View.VISIBLE);
                img_FotoDua.setVisibility(View.VISIBLE);
                btn_PilihFotoDua.setVisibility(View.VISIBLE);
                showPictureDialog_Satu();
            }
        });

        btn_PilihFotoDua.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_FotoTiga.setVisibility(View.VISIBLE);
                spc_Dua.setVisibility(View.VISIBLE);
                btn_PilihFotoTiga.setVisibility(View.VISIBLE);
                showPictureDialog_Dua();
            }
        });

        btn_PilihFotoTiga.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_FotoEmpat.setVisibility(View.VISIBLE);
                spc_Tiga.setVisibility(View.VISIBLE);
                btn_PilihFotoEmpat.setVisibility(View.VISIBLE);
                showPictureDialog_Tiga();
            }
        });

        btn_PilihFotoEmpat.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog_Empat();
            }
        });
    }

    // <------------------------------------------- MAPS ACTIVITIES <------------------------------------------->
    private void OpenMaps() {
        imgPinCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23) {
                    isStoragePermissionGranted();
                } else {
                    OpenPlacePicker();
                }
            }
        });
    }

    //Opening Maps...
    private void OpenPlacePicker() {
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Intent openMaps = new Intent(getContext(), OpenMaps.class);
            startActivityForResult(openMaps, REQ_PLACE_PICKER);
        }else{
            showGPSDisabledAlertToUser();
        }
    }

    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void getCurrentLocationRoute() {
        gps = new TrackGPS(getContext());
        if (gps.canGetLocation()) {
            longitude = gps.getLongitude();
            latitude = gps.getLatitude();

            latlong_Consultant = latitude + "," + longitude;
            latlong_AlamatPelanggan = latitude + "," + longitude;

            Log.e("tag1", latlong_Consultant);
        } else {
            gps.showSettingsAlert();
        }
    }

    private void showPictureDialog_Satu() {
        android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(getContext());
        pictureDialog.setTitle("Pilih Aksi");

        String[] pictureDialogItems = {
                "Pilih Foto dari Galeri", "Ambil Foto dari Kamera"};

        pictureDialog.setItems(pictureDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        openGaleri();
                        break;

                    case 1:
                        openKamera();
                        break;
                }
            }
        });
        pictureDialog.show();
    }

    private void showPictureDialog_Dua() {
        android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(getContext());
        pictureDialog.setTitle("Pilih Aksi");

        String[] pictureDialogItems = {
                "Pilih Foto dari Galeri", "Ambil Foto dari Kamera"};

        pictureDialog.setItems(pictureDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_GALERI_DUA);
                        break;

                    case 1:
                        Intent intent_cameraDua = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent_cameraDua, REQ_KAMERA_DUA);
                        break;
                }
            }
        });
        pictureDialog.show();
    }

    private void showPictureDialog_Tiga() {
        android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(getContext());
        pictureDialog.setTitle("Pilih Aksi");

        String[] pictureDialogItems = {
                "Pilih Foto dari Galeri", "Ambil Foto dari Kamera"};

        pictureDialog.setItems(pictureDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_GALERI_TIGA);
                        break;

                    case 1:
                        Intent intent_cameraDua = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent_cameraDua, REQ_KAMERA_TIGA);
                        break;
                }
            }
        });
        pictureDialog.show();
    }

    private void showPictureDialog_Empat() {
        android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(getContext());
        pictureDialog.setTitle("Pilih Aksi");

        String[] pictureDialogItems = {
                "Pilih Foto dari Galeri", "Ambil Foto dari Kamera"};

        pictureDialog.setItems(pictureDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_GALERI_EMPAT);
                        break;

                    case 1:
                        Intent intent_cameraDua = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent_cameraDua, REQ_KAMERA_EMPAT);
                        break;
                }
            }
        });
        pictureDialog.show();
    }

    //Buka Foto Galeri
    private void openGaleri() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Pesan_Extra, "Foto_Satu");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_GALERI_SATU);
    }

    private void openKamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQ_KAMERA_SATU);
    }


    //Request Location, External and Camera..
    public void isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                OpenPlacePicker();
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 99);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissons, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissons, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            OpenPlacePicker();
        } else {
            Toast.makeText(getContext(), "Aplikasi tidak diberikan izin untuk menyimpan data kedalam penyimpanan. Sehingga aplikasi tidak dapat berjalan normal." +
                    "Mohon pertimbangkan untuk memberikan Izin akses.", Toast.LENGTH_LONG).show();
        }
    }
    // <------------------------------------------- MAPS ACTIVITIES <------------------------------------------->

    private void CheckButtonHama() {
        chk_Tikus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    show_Tikus();
                } else {
                    hide_Tikus();
                }
            }
        });

        chk_Kecoa.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    show_Kecoa();
                } else if (chk_Semut.isChecked() || chk_Kutu.isChecked() || chk_Tawon.isChecked()) {
                    chk_LemKecoa.setVisibility(View.GONE);
                    chk_LemKecoa.setChecked(false);
                    chk_GelKecoa.setVisibility(View.GONE);
                    chk_GelKecoa.setChecked(false);
                } else {
                    hide_Kecoa();
                }
            }
        });

        chk_Semut.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    show_Semut();
                } else if (chk_Kecoa.isChecked() || chk_Kutu.isChecked() || chk_Tawon.isChecked()) {
                    chk_UmpanSemut.setVisibility(View.GONE);
                    chk_UmpanSemut.setChecked(false);
                } else {
                    hide_Semut();
                }
            }
        });

        chk_Nyamuk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    show_Nyamuk();
                } else if (chk_Lalat.isChecked() || chk_Kutu.isChecked()) {
                    chk_BlackHole.setVisibility(View.GONE);
                    chk_BlackHole.setChecked(false);
                } else {
                    hide_Nyamuk();
                }
            }
        });

        chk_Lalat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    show_Lalat();
                } else if (chk_Nyamuk.isChecked() || chk_Kutu.isChecked()) {
                    chk_FlyCatcher.setVisibility(View.GONE);
                    chk_FlyCatcher.setChecked(false);
                    chk_PohonLalat.setVisibility(View.GONE);
                    chk_PohonLalat.setChecked(false);
                    chk_UmpanLalat.setVisibility(View.GONE);
                    chk_UmpanLalat.setChecked(false);
                } else {
                    hide_Lalat();
                }
            }
        });

        chk_Kutu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    show_Kutu();
                } else if (chk_Semut.isChecked() || chk_Kecoa.isChecked() || chk_Tawon.isChecked()) {
                    chk_Penyemprotan.setVisibility(View.VISIBLE);
                    chk_Pengembunan.setVisibility(View.GONE);
                } else if (chk_Nyamuk.isChecked() || chk_Lalat.isChecked()) {
                    chk_Penyemprotan.setVisibility(View.GONE);
                    chk_Pengembunan.setVisibility(View.VISIBLE);
                } else {
                    hide_Kutu();
                }
            }
        });

        chk_Tawon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    show_Tawon();
                } else if (chk_Semut.isChecked() || chk_Kecoa.isChecked() || chk_Kutu.isChecked()) {
                    chk_Penyemprotan.setVisibility(View.VISIBLE);
                } else {
                    hide_Tawon();
                }
            }
        });

        chk_Cicak.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    show_Cicak();
                } else {
                    hide_Cicak();
                }
            }
        });

        chk_HamaLainnya.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_HamaLainnya.setVisibility(View.VISIBLE);
                    show_Lainnya();
                } else {
                    edt_HamaLainnya.setVisibility(View.GONE);
                    //hide_Lainnya();
                }
            }
        });
    }

    private void CheckButtonMetodeKendali() {
        chk_PerangkapTikus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_QtyPerangkapTikus.setVisibility(View.VISIBLE);
                    edt_QtyPerangkapTikus.requestFocus();
                } else {
                    edt_QtyPerangkapTikus.setVisibility(View.GONE);
                }
            }
        });

        chk_LemTikus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_QtyLemTikus.setVisibility(View.VISIBLE);
                    edt_QtyLemTikus.requestFocus();
                } else {
                    edt_QtyLemTikus.setVisibility(View.GONE);
                }
            }
        });
        chk_UmpanTikusIndoor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_QtyUmpanIndoor.setVisibility(View.VISIBLE);
                    edt_QtyUmpanIndoor.requestFocus();
                } else {
                    edt_QtyUmpanIndoor.setVisibility(View.GONE);
                }
            }
        });
        chk_UmpanTikusOutdoor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_QtyUmpanOutdoor.setVisibility(View.VISIBLE);
                    edt_QtyUmpanOutdoor.requestFocus();
                } else {
                    edt_QtyUmpanOutdoor.setVisibility(View.GONE);
                }
            }
        });
        chk_BlackHole.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_QtyBlackHole.setVisibility(View.VISIBLE);
                    edt_QtyBlackHole.requestFocus();
                } else {
                    edt_QtyBlackHole.setVisibility(View.GONE);
                }
            }
        });
        chk_FlyCatcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_QtyFlyCatcher.setVisibility(View.VISIBLE);
                    edt_QtyFlyCatcher.requestFocus();
                } else {
                    edt_QtyFlyCatcher.setVisibility(View.GONE);
                }
            }
        });
        chk_PohonLalat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_QtyPohonLalat.setVisibility(View.VISIBLE);
                    edt_QtyPohonLalat.requestFocus();
                } else {
                    edt_QtyPohonLalat.setVisibility(View.GONE);
                }
            }
        });
        chk_Fumigasi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    spr_Fumigasi.setVisibility(View.VISIBLE);
                } else {
                    spr_Fumigasi.setVisibility(View.GONE);
                }
            }
        });
        chk_MetodeLainnya.setOnCheckedChangeListener((new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_MetodeLainnya.setVisibility(View.VISIBLE);
                    edt_MetodeLainnya.requestFocus();
                } else {
                    edt_MetodeLainnya.setVisibility(View.GONE);
                }
            }
        }));
    }

    private void CekRadioGroup() {
        rdg_Kontrak.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButton_Kontrak:
                        txt_PenawaranHarga.setVisibility(View.VISIBLE);
                        txt_PenawaranHarga.setText("Penawaran Harga/Bulan");
                        txt_DurasiKontrak.setVisibility(View.VISIBLE);

                        edt_PenawaranHarga.setVisibility(View.VISIBLE);
                        edt_DurasiKontrak.setVisibility(View.VISIBLE);
                        edt_DurasiKontrak.requestFocus();
                        kontrak = "Kontrak";
                        break;
                    case R.id.radioButton_NonKontrak:
                        txt_PenawaranHarga.setVisibility(View.VISIBLE);
                        txt_PenawaranHarga.setText("Penawaran Harga");
                        txt_DurasiKontrak.setVisibility(View.GONE);

                        edt_DurasiKontrak.setVisibility(View.GONE);
                        edt_DurasiKontrak.setText("");
                        edt_PenawaranHarga.setVisibility(View.VISIBLE);
                        edt_PenawaranHarga.requestFocus();
                        kontrak = "Non-Kontrak";
                        break;
                }
            }
        });

        rdg_Status.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButton_Berhasil:
                        status_kerjasama = "Berhasil";
                        break;
                    case R.id.radioButton_Gagal:
                        status_kerjasama = "Gagal";
                        break;
                    case R.id.radioButton_Menunggu:
                        status_kerjasama = "Menunggu";
                        Toast.makeText(getContext(), "Catatan:\n" +
                                "Pastikan untuk Melakukan Konfirmasi Kembali Dalam Beberapa Waktu Kedepan Kepada Pelanggan/Perusahaan", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
    }

    private void CekSpinner() {
        spr_KategoriPenanganan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spr_KategoriPenanganan.getSelectedItem().equals("Sistem Mobile")) {
                    edt_QtyPenanganan.setVisibility(View.VISIBLE);
                    edt_QtyPenanganan.setText("");
                    //edt_QtyPenanganan.requestFocus();

                    txt_Penanganan.setText("x Kunjungan/Bulan");
                    txt_Penanganan.setVisibility(View.VISIBLE);
                } else if (spr_KategoriPenanganan.getSelectedItem().equals("Standby/Station")) {
                    edt_QtyPenanganan.setVisibility(View.VISIBLE);
                    edt_QtyPenanganan.setText("");
                    //edt_QtyPenanganan.requestFocus();

                    txt_Penanganan.setText("Orang");
                    txt_Penanganan.setVisibility(View.VISIBLE);
                } else {
                    edt_QtyPenanganan.setVisibility(View.GONE);
                    edt_QtyPenanganan.setText("");

                    txt_Penanganan.setText("");
                    txt_Penanganan.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // <------------------------------------------- KALKULASI TOTAL HARGA PENAWARAN <------------------------------------------->
    private String kalkulasi_total() {
        double bulan, a;

        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

        if (!edt_DurasiKontrak.getText().toString().equals("") && edt_DurasiKontrak.getText().length() > 0) {
            bulan = Double.valueOf(edt_DurasiKontrak.getText().toString());
        } else {
            bulan = 1;
        }
        if (!edt_PenawaranHarga.getText().toString().equals("") && edt_PenawaranHarga.getText().length() > 0) {
            String penawaran = edt_PenawaranHarga.getText().toString();
            a = Double.parseDouble(penawaran);
            formatRupiah.format(a);

            //String nilai_penawaran = penawaran.replace("Rp","");
            //penawaran_harga = Double.valueOf(nilai_penawaran);

            //String nilai_penawaran = penawaran.replace(",","");
            //penawaran_harga = Double.valueOf(nilai_penawaran);
        } else {
            a = 0;
        }

        total_harga = bulan * a;
        return (formatRupiah.format(total_harga));
    }

    private void TextWatcher() {
        edt_PenawaranHarga.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edt_GrandTotal.setText(kalkulasi_total());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        edt_DurasiKontrak.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edt_GrandTotal.setText(kalkulasi_total());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        edt_NoHP.addTextChangedListener(new TextWatcher() {

            int keyDel;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edt_NoHP.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                        if (keyCode == KeyEvent.KEYCODE_DEL) {
                            keyDel = 1;
                        }
                        return false;
                    }
                });

                String currentString = edt_NoHP.getText().toString();
                int currentLength = edt_NoHP.getText().length();

                if (currentLength == 5 || currentLength == 10) {
                    keyDel = 1;
                }

                if (keyDel == 0) {
                    if (currentLength == 4 || currentLength == 9) {
                        edt_NoHP.setText(edt_NoHP.getText() + "-");
                        edt_NoHP.setSelection(edt_NoHP.getText().length());
                    }
                } else {
                    if (currentLength != 5 && currentLength != 10) {
                        keyDel = 0;
                    } else if ((currentLength == 5 || currentLength == 10)
                            && !"-".equals(currentString.substring(currentLength - 1, currentLength))) {
                        edt_NoHP.setText(currentString.substring(0, currentLength - 1) + "-"
                                + currentString.substring(currentLength - 1, currentLength));
                        edt_NoHP.setSelection(edt_NoHP.getText().length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    // <------------------------------------------- KALKULASI TOTAL HARGA PENAWARAN ------------------------------------------->

    public void CekTandaTangan() {
        //Button for Signature Pelanggan
        btn_DialogTTD_Pelanggan.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), SignatureActivity.class);
                i.putExtra(Pesan_Extra, "TTD_PelangganPestControl");
                startActivityForResult(i, REQ_TTD_PELANGGAN);
            }
        });

        //Button for Signature Consultant
        btn_DialogTTD_Consultant.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(getContext(), SignatureActivity.class);
                //startActivityForResult(a,2);
                a.putExtra(Pesan_Extra, "TTD_ConsultantPestControl");
                startActivityForResult(a, REQ_TTD_CONSULTANT);
            }
        });

        chk_PernyataanPelanggan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    btn_DialogTTD_Pelanggan.setEnabled(true);
                } else {
                    btn_DialogTTD_Pelanggan.setEnabled(false);
                }
            }
        });

        chk_PernyataanConsultant.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    btn_DialogTTD_Consultant.setEnabled(true);
                } else {
                    btn_DialogTTD_Consultant.setEnabled(false);
                }
            }
        });
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    // <------------------------------------------- HASIL AKTIVITAS ------------------------------------------->
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //REQUEST TTD PELANGGAN
        if (requestCode == REQ_TTD_PELANGGAN) {
            if (resultCode == Activity.RESULT_OK) {
                //Nampilin hasil ttd untuk Pest Pelanggan
                String image_path = data.getStringExtra("imagePath_PestPelanggan");
                bitmap_PestPelanggan = BitmapFactory.decodeFile(image_path);
                img_ttdPelanggan.setImageBitmap(bitmap_PestPelanggan);
            }
        }

        //REQUEST TTD CONSULTANT
        if (requestCode == REQ_TTD_CONSULTANT) {
            if (resultCode == Activity.RESULT_OK) {
                String imagepath_PestConsultant = data.getStringExtra("imagePath_PestConsultant");
                bitmap_PestConsultant = BitmapFactory.decodeFile(imagepath_PestConsultant);
                img_ttdConsultant.setImageBitmap(bitmap_PestConsultant);
            }
        }

        //Untuk Picture atau Kamera
        //take Photo #1
        if (requestCode == REQ_KAMERA_SATU) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap bitmap_PickFoto = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();

                if (bitmap_PickFoto != null) {
                    bitmap_PickFoto.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
                    img_FotoSatu.setImageBitmap(bitmap_PickFoto);
                }
            }
        }

        //gallery Photo #1
        if (requestCode == REQ_GALERI_SATU) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                try {
                    Bitmap bitmap_Foto1 = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(), imageUri);
                    //untuk decompress size foto
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap_Foto1, 300, 400, false);
                    scaled = getResizedBitmap(scaled, 300);
                    img_FotoSatu.setImageBitmap(scaled);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (requestCode == REQ_KAMERA_DUA) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap bitmap_PickFoto = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                if (bitmap_PickFoto != null) {
                    bitmap_PickFoto.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
                    img_FotoDua.setImageBitmap(bitmap_PickFoto);
                }
            }
        }

        if (requestCode == REQ_GALERI_DUA) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                try {
                    Bitmap bitmap_Foto1 = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(), imageUri);
                    //untuk decompress size foto
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap_Foto1, 300, 400, false);
                    scaled = getResizedBitmap(scaled, 300);
                    img_FotoDua.setImageBitmap(scaled);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (requestCode == REQ_KAMERA_TIGA) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap bitmap_PickFoto = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();

                if (bitmap_PickFoto != null) {
                    bitmap_PickFoto.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
                    img_FotoTiga.setImageBitmap(bitmap_PickFoto);
                }
            }
        }

        if (requestCode == REQ_GALERI_TIGA) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                try {
                    Bitmap bitmap_Foto1 = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(), imageUri);
                    //untuk decompress size foto
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap_Foto1, 300, 400, false);
                    scaled = getResizedBitmap(scaled, 300);
                    img_FotoTiga.setImageBitmap(scaled);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (requestCode == REQ_KAMERA_EMPAT) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap bitmap_PickFoto = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                if (bitmap_PickFoto != null) {
                    bitmap_PickFoto.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
                    img_FotoEmpat.setImageBitmap(bitmap_PickFoto);
                }
            }
        }

        if (requestCode == REQ_GALERI_EMPAT) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                try {
                    Bitmap bitmap_Foto1 = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(), imageUri);
                    //untuk decompress size foto
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap_Foto1, 300, 400, false);
                    scaled = getResizedBitmap(scaled, 300);
                    img_FotoEmpat.setImageBitmap(scaled);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //REQUEST MAPS
        if (requestCode == REQ_PLACE_PICKER) {
            if (resultCode == Activity.RESULT_OK) {
                String lokasi = data.getStringExtra("Alamat_Lokasi");
                edt_Alamat.setText(lokasi);

                latlong_AlamatPelanggan = data.getStringExtra("LatLong_Lokasi");
                Log.e("tag2", latlong_AlamatPelanggan);
            }
        }
    }
    // <------------------------------------------- HASIL AKTIVITAS ------------------------------------------->

    // <------------------------------------------- HIDE/SHOW CLICKED DATA ------------------------------------------->
    protected void show_Tikus() {
        chk_PerangkapTikus.setVisibility(View.VISIBLE);
        chk_LemTikus.setVisibility(View.VISIBLE);
        chk_UmpanTikusIndoor.setVisibility(View.VISIBLE);
        chk_UmpanTikusOutdoor.setVisibility(View.VISIBLE);
        chk_Fumigasi.setVisibility(View.VISIBLE);
        chk_MetodeLainnya.setVisibility(View.VISIBLE);
    }

    protected void hide_Tikus() {
        chk_PerangkapTikus.setVisibility(View.GONE);
        chk_LemTikus.setVisibility(View.GONE);
        chk_UmpanTikusIndoor.setVisibility(View.GONE);
        chk_UmpanTikusOutdoor.setVisibility(View.GONE);
        chk_Fumigasi.setVisibility(View.GONE);
        chk_MetodeLainnya.setVisibility(View.GONE);

        chk_PerangkapTikus.setChecked(false);
        chk_LemTikus.setChecked(false);
        chk_UmpanTikusIndoor.setChecked(false);
        chk_UmpanTikusOutdoor.setChecked(false);
        chk_Fumigasi.setChecked(false);
        chk_MetodeLainnya.setChecked(false);
    }

    protected void show_Kecoa() {
        chk_Penyemprotan.setVisibility(View.VISIBLE);
        chk_LemKecoa.setVisibility(View.VISIBLE);
        chk_GelKecoa.setVisibility(View.VISIBLE);
        chk_Fumigasi.setVisibility(View.VISIBLE);
        chk_MetodeLainnya.setVisibility(View.VISIBLE);
    }

    protected void hide_Kecoa() {
        chk_Penyemprotan.setVisibility(View.GONE);
        chk_LemKecoa.setVisibility(View.GONE);
        chk_GelKecoa.setVisibility(View.GONE);
        chk_Fumigasi.setVisibility(View.GONE);
        chk_MetodeLainnya.setVisibility(View.GONE);
        chk_GelKecoa.setChecked(false);
        chk_LemKecoa.setChecked(false);
        chk_MetodeLainnya.setChecked(false);
        chk_Fumigasi.setChecked(false);
    }

    protected void show_Semut() {
        chk_Penyemprotan.setVisibility(View.VISIBLE);
        chk_UmpanSemut.setVisibility(View.VISIBLE);
        chk_Fumigasi.setVisibility(View.VISIBLE);
        chk_MetodeLainnya.setVisibility(View.VISIBLE);
    }

    protected void hide_Semut() {
        chk_Penyemprotan.setVisibility(View.GONE);
        chk_UmpanSemut.setVisibility(View.GONE);
        chk_Fumigasi.setVisibility(View.GONE);
        chk_MetodeLainnya.setVisibility(View.GONE);

        chk_UmpanSemut.setChecked(false);
        chk_Fumigasi.setChecked(false);
        chk_MetodeLainnya.setChecked(false);
    }

    protected void show_Nyamuk() {
        chk_Pengembunan.setVisibility(View.VISIBLE);
        chk_PengasapanThermal.setVisibility(View.VISIBLE);
        chk_PengasapanMini.setVisibility(View.VISIBLE);
        chk_BlackHole.setVisibility(View.VISIBLE);
        chk_Fumigasi.setVisibility(View.VISIBLE);
        chk_MetodeLainnya.setVisibility(View.VISIBLE);
    }

    protected void hide_Nyamuk() {
        chk_Pengembunan.setVisibility(View.GONE);
        chk_PengasapanThermal.setVisibility(View.GONE);
        chk_PengasapanMini.setVisibility(View.GONE);
        chk_BlackHole.setVisibility(View.GONE);
        chk_Fumigasi.setVisibility(View.GONE);
        chk_MetodeLainnya.setVisibility(View.GONE);

        chk_BlackHole.setChecked(false);
        chk_Fumigasi.setChecked(false);
        chk_MetodeLainnya.setChecked(false);

    }

    protected void show_Lalat() {
        chk_Pengembunan.setVisibility(View.VISIBLE);
        chk_PengasapanThermal.setVisibility(View.VISIBLE);
        chk_PengasapanMini.setVisibility(View.VISIBLE);
        chk_FlyCatcher.setVisibility(View.VISIBLE);
        chk_PohonLalat.setVisibility(View.VISIBLE);
        chk_UmpanLalat.setVisibility(View.VISIBLE);
        chk_Fumigasi.setVisibility(View.VISIBLE);
        chk_MetodeLainnya.setVisibility(View.VISIBLE);
    }

    protected void hide_Lalat() {
        chk_Pengembunan.setVisibility(View.GONE);
        chk_PengasapanThermal.setVisibility(View.GONE);
        chk_PengasapanMini.setVisibility(View.GONE);
        chk_FlyCatcher.setVisibility(View.GONE);
        chk_PohonLalat.setVisibility(View.GONE);
        chk_UmpanLalat.setVisibility(View.GONE);
        chk_Fumigasi.setVisibility(View.GONE);
        chk_MetodeLainnya.setVisibility(View.GONE);

        chk_Fumigasi.setChecked(false);
        chk_PohonLalat.setChecked(false);
        chk_FlyCatcher.setChecked(false);
        chk_UmpanLalat.setChecked(false);
        chk_MetodeLainnya.setChecked(false);
    }

    protected void show_Kutu() {
        chk_Penyemprotan.setVisibility(View.VISIBLE);
        chk_Fumigasi.setVisibility(View.VISIBLE);
        chk_MetodeLainnya.setVisibility(View.VISIBLE);
        chk_Pengembunan.setVisibility(View.VISIBLE);
    }

    protected void hide_Kutu() {
        chk_Penyemprotan.setVisibility(View.GONE);
        chk_Fumigasi.setVisibility(View.GONE);
        chk_MetodeLainnya.setVisibility(View.GONE);
        chk_Pengembunan.setVisibility(View.GONE);

        chk_Fumigasi.setChecked(false);
        chk_MetodeLainnya.setChecked(false);
    }

    protected void show_Tawon() {
        chk_Penyemprotan.setVisibility(View.VISIBLE);
        chk_Fumigasi.setVisibility(View.VISIBLE);
        chk_MetodeLainnya.setVisibility(View.VISIBLE);
    }

    protected void hide_Tawon() {
        chk_Penyemprotan.setVisibility(View.GONE);
        chk_Fumigasi.setVisibility(View.GONE);
        chk_MetodeLainnya.setVisibility(View.GONE);

        chk_Fumigasi.setChecked(false);
        chk_MetodeLainnya.setChecked(false);
    }

    protected void show_Cicak() {
        chk_Fumigasi.setVisibility(View.VISIBLE);
        chk_MetodeLainnya.setVisibility(View.VISIBLE);
    }

    protected void hide_Cicak() {
        chk_Fumigasi.setVisibility(View.GONE);
        chk_MetodeLainnya.setVisibility(View.GONE);
        chk_Fumigasi.setChecked(false);
        chk_MetodeLainnya.setChecked(false);
    }

    protected void show_Lainnya() {
        chk_Penyemprotan.setVisibility(View.VISIBLE);
        chk_Pengembunan.setVisibility(View.VISIBLE);
        chk_PengasapanThermal.setVisibility(View.VISIBLE);
        chk_PengasapanMini.setVisibility(View.VISIBLE);
        chk_PerangkapTikus.setVisibility(View.VISIBLE);
        chk_LemTikus.setVisibility(View.VISIBLE);
        chk_UmpanTikusIndoor.setVisibility(View.VISIBLE);
        chk_UmpanTikusOutdoor.setVisibility(View.VISIBLE);
        chk_UmpanSemut.setVisibility(View.VISIBLE);
        chk_GelKecoa.setVisibility(View.VISIBLE);
        chk_LemKecoa.setVisibility(View.VISIBLE);
        chk_BlackHole.setVisibility(View.VISIBLE);
        chk_FlyCatcher.setVisibility(View.VISIBLE);
        chk_PohonLalat.setVisibility(View.VISIBLE);
        chk_UmpanLalat.setVisibility(View.VISIBLE);
        chk_Fumigasi.setVisibility(View.VISIBLE);
        chk_MetodeLainnya.setVisibility(View.VISIBLE);
    }

    /*protected void hide_Lainnya(){
        chk_Penyemprotan.setVisibility(View.GONE);          chk_Pengembunan.setVisibility(View.GONE);
        chk_PengasapanThermal.setVisibility(View.GONE);     chk_PengasapanMini.setVisibility(View.GONE);
        chk_PerangkapTikus.setVisibility(View.GONE);        chk_LemTikus.setVisibility(View.GONE);
        chk_UmpanTikusIndoor.setVisibility(View.GONE);      chk_UmpanTikusOutdoor.setVisibility(View.GONE);
        chk_UmpanSemut.setVisibility(View.GONE);            chk_GelKecoa.setVisibility(View.GONE);
        chk_LemKecoa.setVisibility(View.GONE);              chk_BlackHole.setVisibility(View.GONE);
        chk_FlyCatcher.setVisibility(View.GONE);            chk_PohonLalat.setVisibility(View.GONE);
        chk_UmpanLalat.setVisibility(View.GONE);            chk_Fumigasi.setVisibility(View.GONE);
        chk_MetodeLainnya.setVisibility(View.GONE);

        chk_Penyemprotan.setChecked(false);          chk_Pengembunan.setChecked(false);
        chk_PengasapanThermal.setChecked(false);     chk_PengasapanMini.setChecked(false);
        chk_PerangkapTikus.setChecked(false);        chk_LemTikus.setChecked(false);
        chk_UmpanTikusIndoor.setChecked(false);      chk_UmpanTikusOutdoor.setChecked(false);
        chk_UmpanSemut.setChecked(false);            chk_GelKecoa.setChecked(false);
        chk_LemKecoa.setChecked(false);              chk_BlackHole.setChecked(false);
        chk_FlyCatcher.setChecked(false);            chk_PohonLalat.setChecked(false);
        chk_UmpanLalat.setChecked(false);            chk_Fumigasi.setChecked(false);
        chk_MetodeLainnya.setChecked(false);
    }*/
    // <------------------------------------------- HIDE/SHOW CLICKED DATA ------------------------------------------->

    // <------------------------------------------- VALIDATE BEFORE SAVE TO DB ------------------------------------------->
    private void ValidateAndShow() {
        // <---- Untuk Bagian 1 - Data Diri Pelanggan ---->
        IDClient = edt_ClientID_Pest.getText().toString().toUpperCase();
        namaPelanggan = edt_NamaPelanggan.getText().toString();
        kategoriTempatPelanggan = spr_Kategori.getSelectedItem().toString();
        alamatPelanggan = edt_Alamat.getText().toString();
        hpPelanggan = edt_NoHP.getText().toString();
        email_DB = edt_Email.getText().toString();

        if (TextUtils.isEmpty(IDClient)) {
            edt_ClientID_Pest.setError("Harap Masukkan ID Client");
            edt_ClientID_Pest.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(namaPelanggan)) {
            edt_NamaPelanggan.setError("Harap Masukkan Nama Pelanggan/Perusahaan");
            edt_NamaPelanggan.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(alamatPelanggan)) {
            edt_Alamat.setError("Harap Masukkan Alamat Pelanggan/Perusahaan");
            edt_Alamat.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(hpPelanggan)) {
            edt_NoHP.setError("Harap Masukkan No.HP atau Telepon Pelanggan/Perusahaan");
            edt_NoHP.requestFocus();
            return;
        }

        if (hpPelanggan.trim().length() < 5) {
            edt_NoHP.setError("Harap Masukkan Nomor Handphone/Telepon yang Valid");
            edt_NoHP.requestFocus();
            return;
        }

        //Untuk Kategori Penanganan tmp ke DB
        String qty_penanganan_tmp = "";

        Penanganan = spr_KategoriPenanganan.getSelectedItem().toString();

        if (Penanganan.equals("Sistem Mobile")) {
            qty_penanganan_tmp = edt_QtyPenanganan.getText().toString();
        } else if (Penanganan.equals("Standby/Station")) {
            qty_penanganan_tmp = edt_QtyPenanganan.getText().toString();
        }

        //Disimpan ke DB
        qty_penanganan_DB = qty_penanganan_tmp;

        if (edt_QtyPenanganan.getVisibility() == View.VISIBLE && TextUtils.isEmpty(qty_penanganan_DB)) {
            edt_QtyPenanganan.setError("Harap Masukkan Jumlah Kuantitas yang Disepakati");
            edt_QtyPenanganan.requestFocus();
            return;
        }

        //Disimpan di Temporary DB
        String jenis_hama_tmp = "";
        String hama_lainnya_tmp = "";

        if (!chk_Kecoa.isChecked() && !chk_Kutu.isChecked() && !chk_Lalat.isChecked() && !chk_Cicak.isChecked() && !chk_Semut.isChecked() && !chk_Tikus.isChecked()
                && !chk_Tawon.isChecked() && !chk_Nyamuk.isChecked() && !chk_HamaLainnya.isChecked()) {
            chk_Kecoa.setError("Harap Memilih Minimal Satu Jenis Hama yang Akan Dikendalikan");
            chk_Kecoa.requestFocusFromTouch();
            return;
        }

        if (chk_Kecoa.isChecked()) {
            jenis_hama_tmp += "Kecoa,";
        }

        if (chk_Kutu.isChecked()) {
            jenis_hama_tmp += "Kutu,";
        }

        if (chk_Lalat.isChecked()) {
            jenis_hama_tmp += "Lalat,";
        }

        if (chk_Cicak.isChecked()) {
            jenis_hama_tmp += "Cicak,";
        }

        if (chk_Semut.isChecked()) {
            jenis_hama_tmp += "Semut,";
        }

        if (chk_Tikus.isChecked()) {
            jenis_hama_tmp += "Tikus,";
        }

        if (chk_Tawon.isChecked()) {
            jenis_hama_tmp += "Tawon,";
        }

        if (chk_Nyamuk.isChecked()) {
            jenis_hama_tmp += "Nyamuk,";
        }

        if (chk_HamaLainnya.isChecked()) {
            hama_lainnya_tmp = edt_HamaLainnya.getText().toString();

            if (TextUtils.isEmpty(hama_lainnya_tmp)) {
                edt_HamaLainnya.setError(("Harap Masukkan Jenis Hama Lainnya"));
                edt_HamaLainnya.requestFocus();
                return;
            }
            jenis_hama_tmp += "Lainnya,";

        }

        //Disimpan di temporary DB
        String jenis_chemical_tmp = "";

        if (!chk_BoricAcid.isChecked() && !chk_Cypermethrin.isChecked() && !chk_ZetaCypermethrin.isChecked() && !chk_Dichlorvos.isChecked()
                && !chk_Fipronil.isChecked() && !chk_Thiamethoxam.isChecked()) {
            chk_BoricAcid.setError("Harap Memilih Minimal Satu Jenis Chemical yang Digunakan");
            chk_BoricAcid.requestFocusFromTouch();
            return;
        }

        if (chk_BoricAcid.isChecked()) {
            jenis_chemical_tmp += "Boric Acid,";
        }

        if (chk_Cypermethrin.isChecked()) {
            jenis_chemical_tmp += "Cypermethrin,";
        }

        if (chk_ZetaCypermethrin.isChecked()) {
            jenis_chemical_tmp += "Zeta-Cypermethrin,";
        }

        if (chk_Dichlorvos.isChecked()) {
            jenis_chemical_tmp += "Dichlorvos,";
        }

        if (chk_Fipronil.isChecked()) {
            jenis_chemical_tmp += "Fipronil,";
        }

        if (chk_Thiamethoxam.isChecked()) {
            jenis_chemical_tmp += "Thiamethoxam,";
        }

        // <---- Untuk Bagian 2 - Jenis Hama ---->
        jenis_hama_DB = jenis_hama_tmp;
        hama_lainnya_DB = hama_lainnya_tmp;
        jenis_chemical_DB = jenis_chemical_tmp;

        // <---- Untuk Bagian 3 - Kendali Hama ---->
        //Disimpan di temporary DB
        String metode_kendali_tmp = "";
        String qty_lemTikus_tmp = "";
        String qty_perangkapTikus_tmp = "";
        String qty_umpanTikus_tmp = "";
        String qty_umpanTikusIndoor_tmp = "";
        String qty_pohonLalat_tmp = "";
        String qty_blackHole_tmp = "";
        String jenis_fumigasi_tmp = "";
        String qty_FlyCatcher_tmp = "";
        String metodeLain_tmp = "";

        if (chk_Penyemprotan.isChecked()) {
            metode_kendali_tmp += "Penyemprotan (Spraying),";
        }

        if (chk_Pengembunan.isChecked()) {
            metode_kendali_tmp += "Pengembunan (Cold Fogging),";
        }

        if (chk_PengasapanMini.isChecked()) {
            metode_kendali_tmp += "Pengasapan (Mini Fogging),";
        }

        if (chk_PengasapanThermal.isChecked()) {
            metode_kendali_tmp += "Pengasapan (Thermal Fogging),";
        }

        if (chk_LemTikus.isChecked()) {
            //Untuk disimpan ke DB
            qty_lemTikus_tmp = edt_QtyLemTikus.getText().toString();

            if (TextUtils.isEmpty(qty_lemTikus_tmp)) {
                edt_QtyLemTikus.setError(("Harap Masukkan Banyak Perangkap yang Dibutuhkan"));
                edt_QtyLemTikus.requestFocus();
                return;
            }
            metode_kendali_tmp += "Lem Tikus (Rodent Glue Trap),";
        }

        if (chk_PerangkapTikus.isChecked()) {
            //Untuk disimpan ke DB
            qty_perangkapTikus_tmp = edt_QtyPerangkapTikus.getText().toString();

            if (TextUtils.isEmpty(qty_perangkapTikus_tmp)) {
                edt_QtyPerangkapTikus.setError(("Harap Masukkan Banyak Perangkap yang Dibutuhkan"));
                edt_QtyPerangkapTikus.requestFocus();
                return;
            }

            metode_kendali_tmp += "Perangkap Tikus Massal (Rodent Trap),";
        }

        if (chk_UmpanTikusOutdoor.isChecked()) {
            //untuk disimpan ke DB
            qty_umpanTikus_tmp = edt_QtyUmpanOutdoor.getText().toString();

            if (TextUtils.isEmpty(qty_umpanTikus_tmp)) {
                edt_QtyUmpanOutdoor.setError(("Harap Masukkan Banyak Perangkap yang Dibutuhkan"));
                edt_QtyUmpanOutdoor.requestFocus();
                return;
            }

            metode_kendali_tmp += "Umpan Tikus (Rodent Baiting Outdoor),";
        }

        if (chk_UmpanTikusIndoor.isChecked()) {
            //untuk disimpan ke DB
            qty_umpanTikusIndoor_tmp = edt_QtyUmpanIndoor.getText().toString();

            if (TextUtils.isEmpty(qty_umpanTikusIndoor_tmp)) {
                edt_QtyUmpanIndoor.setError(("Harap Masukkan Banyak Perangkap yang Dibutuhkan"));
                edt_QtyUmpanIndoor.requestFocus();
                return;
            }

            metode_kendali_tmp += "Umpan Tikus (Rodent Baiting Indoor),";
        }

        if (chk_GelKecoa.isChecked()) {
            metode_kendali_tmp += "Umpan Gel Kecoa (Roach Gelling),";
        }

        if (chk_UmpanSemut.isChecked()) {
            metode_kendali_tmp += "Umpan Semut (Ant Baiting),";
        }

        if (chk_LemKecoa.isChecked()) {
            metode_kendali_tmp += "Perangkap Lem Kecoa (Hoy-hoy),";
        }

        if (chk_UmpanLalat.isChecked()) {
            metode_kendali_tmp += "Umpan Lalat (Fly Baiting),";
        }

        if (chk_PohonLalat.isChecked()) {
            qty_pohonLalat_tmp = edt_QtyPohonLalat.getText().toString();

            if (TextUtils.isEmpty(qty_pohonLalat_tmp)) {
                edt_QtyPohonLalat.setError(("Harap Masukkan Banyak Perangkap yang Dibutuhkan"));
                edt_QtyPohonLalat.requestFocus();
                return;
            }

            metode_kendali_tmp += "Pohon Lalat,";
        }

        if (chk_BlackHole.isChecked()) {
            qty_blackHole_tmp = edt_QtyBlackHole.getText().toString();

            if (TextUtils.isEmpty(qty_blackHole_tmp)) {
                edt_QtyBlackHole.setError(("Harap Masukkan Banyak Perangkap yang Dibutuhkan"));
                edt_QtyBlackHole.requestFocus();
                return;
            }

            metode_kendali_tmp += "Instalasi Black Hole,";
        }

        if (chk_Fumigasi.isChecked()) {
            jenis_fumigasi_tmp = spr_Fumigasi.getSelectedItem().toString();
            metode_kendali_tmp += "Fumigasi,";
        }

        if (chk_FlyCatcher.isChecked()) {
            qty_FlyCatcher_tmp = edt_QtyFlyCatcher.getText().toString();

            if (TextUtils.isEmpty(qty_FlyCatcher_tmp)) {
                edt_QtyFlyCatcher.setError(("Harap Masukkan Banyak Perangkap yang Dibutuhkan"));
                edt_QtyFlyCatcher.requestFocus();
                return;
            }

            metode_kendali_tmp += "Instalasi Fly Catcher,";
        }

        if (chk_MetodeLainnya.isChecked()) {
            metodeLain_tmp = edt_MetodeLainnya.getText().toString();

            if (TextUtils.isEmpty(metodeLain_tmp)) {
                edt_MetodeLainnya.setError(("Harap Masukkan Metode Lainnya"));
                edt_MetodeLainnya.requestFocus();
                return;
            }
            metode_kendali_tmp += "Metode Lainnya,";
        }

        //Disimpan Ke Database
        metode_kendali_DB = metode_kendali_tmp;

        qty_lemTikus_DB = qty_lemTikus_tmp;
        qty_perangkapTikus_DB = qty_perangkapTikus_tmp;
        qty_umpanTikus_DB = qty_umpanTikus_tmp;
        qty_umpanTikusIndoor_DB = qty_umpanTikusIndoor_tmp;
        qty_pohonLalat_DB = qty_pohonLalat_tmp;
        qty_blackHole_DB = qty_blackHole_tmp;
        jenis_fumigasi_DB = jenis_fumigasi_tmp;
        qty_FlyCatcher_DB = qty_FlyCatcher_tmp;
        metodeLain_DB = metodeLain_tmp;
        //Disimpan ke Database

        //Disimpan ke DB
        indoor_DB = edt_LuasBangunan.getText().toString();
        outdoor_DB = edt_LuasOutdoor.getText().toString();

        if (TextUtils.isEmpty(indoor_DB)) {
            edt_LuasBangunan.setError(("Harap Masukkan Luas Bangunan"));
            edt_LuasBangunan.requestFocus();
            return;
        }

        //<---- Untuk Bagian 4 - Kontrak, Status dan Harga---->
        jenis_kontrak = kontrak;

        //Tmp ke DB
        String durasi_kontrak_tmp = "";
        String penawaran_harga_tmp;

        if (jenis_kontrak.equals("Kontrak")) {
            //untuk tmp DB
            durasi_kontrak_tmp = edt_DurasiKontrak.getText().toString();
            penawaran_harga_tmp = edt_PenawaranHarga.getText().toString();

            if (TextUtils.isEmpty(penawaran_harga_tmp)) {
                edt_PenawaranHarga.setError(("Harap Masukkan Penawaran Harga"));
                edt_PenawaranHarga.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(durasi_kontrak_tmp)) {
                edt_DurasiKontrak.setError(("Harap Masukkan Durasi Kontrak"));
                edt_DurasiKontrak.requestFocus();
                return;
            }

        } else if (jenis_kontrak.equals("Non-Kontrak")) {
            //untuk tmp DB
            penawaran_harga_tmp = edt_PenawaranHarga.getText().toString();

            if (TextUtils.isEmpty(penawaran_harga_tmp)) {
                edt_PenawaranHarga.setError(("Harap Masukkan Penawaran Harga"));
                edt_PenawaranHarga.requestFocus();
                return;
            }
        }

        durasi_kontrak_DB = durasi_kontrak_tmp;
        penawaran_harga_DB = edt_PenawaranHarga.getText().toString();
        total_harga_penawaran = String.valueOf(total_harga);

        status_kerjasama_kontrak = status_kerjasama;

        if (jenis_kontrak.isEmpty()) {
            rdb_Kontrak.setError("Harap Memilih Jenis Kontrak");
            rdb_Kontrak.requestFocusFromTouch();
            return;
        }

        if (status_kerjasama_kontrak.isEmpty()) {
            rdb_Berhasil.setError("Harap Memilih Status Kerjasama");
            rdb_Berhasil.requestFocusFromTouch();
            return;
        }

        //<-- Bagian 5 - Catatan dan TTD serta Keterangan PDF -->
        catatan_tambahan_DB = edt_CatatanTambahan.getText().toString();

        if (!chk_PernyataanPelanggan.isChecked()) {
            chk_PernyataanPelanggan.setError("Harap Mencentang Pernyataan Berikut dan Menandatanganinya!");
            chk_PernyataanPelanggan.requestFocusFromTouch();
            return;
        }

        if (!chk_PernyataanConsultant.isChecked()) {
            chk_PernyataanConsultant.setError("Harap Mencentang Pernyataan Berikut dan Menandatanganinya!");
            chk_PernyataanConsultant.requestFocusFromTouch();
            return;
        }

        if (img_ttdConsultant.getDrawable() == null) {
            btn_DialogTTD_Consultant.setError("Harap Melakukan Proses Tanda Tangan Digital!");
            btn_DialogTTD_Consultant.requestFocusFromTouch();
            return;
        }

        if (img_ttdPelanggan.getDrawable() == null) {
            btn_DialogTTD_Pelanggan.setError("Harap Melakukan Proses Tanda Tangan Digital!");
            btn_DialogTTD_Pelanggan.requestFocusFromTouch();
            return;
        }

        //Foto Validasi
        //Untuk Upload Foto-Foto Pekerjaan
        if (img_FotoSatu.getVisibility() == View.GONE) {
            btn_PilihFotoSatu.setError("Harap Mengupload Foto Pekerjaan #1!");
            btn_PilihFotoSatu.requestFocusFromTouch();
            return;
        }

        if (img_FotoSatu.getDrawable() == null) {
            btn_PilihFotoSatu.setError("Harap Mengupload Foto Pekerjaan #1!");
            btn_PilihFotoSatu.requestFocusFromTouch();
            return;
        }

        if (img_FotoDua.getDrawable() == null) {
            btn_PilihFotoDua.setError("Harap Mengupload Foto Pekerjaan #2!");
            btn_PilihFotoDua.requestFocusFromTouch();
            return;
        }

        if (img_FotoTiga.getDrawable() == null) {
            btn_PilihFotoTiga.setError("Harap Mengupload Foto Pekerjaan #3!");
            btn_PilihFotoTiga.requestFocusFromTouch();
            return;
        }

        if (img_FotoEmpat.getDrawable() == null) {
            btn_PilihFotoEmpat.setError("Harap Mengupload Foto Pekerjaan #4!");
            btn_PilihFotoEmpat.requestFocusFromTouch();
            return;
        }

        String dateKeterangan = new SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault()).format(new Date());
        keterangan = "*Berkas ini Dibuat Secara Otomatis oleh Sistem Mobile Apps Fumida pada Tanggal " + dateKeterangan;

        doIntent();

    }

    private void doIntent() {
        //Menjalankan Fungsi Intent
        Intent Review_Pest_Intent = new Intent(getContext(), ReviewSurveiPestControl.class);
        Bundle Extra_Rev_Pest = new Bundle();

        //Bagian I : Data Kostumer
        Extra_Rev_Pest.putString(PestRev_ID_Client, IDClient);
        Extra_Rev_Pest.putString(PestRev_Nama_Client, namaPelanggan);
        Extra_Rev_Pest.putString(PestRev_Kategori_Client, kategoriTempatPelanggan);
        Extra_Rev_Pest.putString(PestRev_Alamat_Client, alamatPelanggan);
        Extra_Rev_Pest.putString(PestRev_Hp_Client, hpPelanggan);
        Extra_Rev_Pest.putString(PestRev_Email_DB, email_DB);

        //Bagian II : Kendali Hama
        Extra_Rev_Pest.putString(PestRev_Penanganan_DB, Penanganan);
        Extra_Rev_Pest.putString(PestRev_QtyPenanganan_DB, qty_penanganan_DB);
        Extra_Rev_Pest.putString(PestRev_Hama_DB, jenis_hama_DB);
        Extra_Rev_Pest.putString(PestRev_HamaLainnya_DB, hama_lainnya_DB);

        //Bagian III : Chemical dan Metode Kendali
        Extra_Rev_Pest.putString(PestRev_Chemical_DB, jenis_chemical_DB);
        Extra_Rev_Pest.putString(PestRev_MetodeKendali_DB, metode_kendali_DB);

        Extra_Rev_Pest.putString(PestRev_QtyLemTikus_DB, qty_lemTikus_DB);
        Extra_Rev_Pest.putString(PestRev_QtyPerangkapTikus_DB, qty_perangkapTikus_DB);
        Extra_Rev_Pest.putString(PestRev_QtyUmpanTikus_DB, qty_umpanTikus_DB);
        Extra_Rev_Pest.putString(PestRev_QtyUmpanTikusIndoor_DB, qty_umpanTikusIndoor_DB);
        Extra_Rev_Pest.putString(PestRev_QtyPohonLalat_DB, qty_pohonLalat_DB);
        Extra_Rev_Pest.putString(PestRev_QtyBlackHole_DB, qty_blackHole_DB);
        Extra_Rev_Pest.putString(PestRev_JenisFumigasi_DB, jenis_fumigasi_DB);
        Extra_Rev_Pest.putString(PestRev_QtyFlyCatcher_DB, qty_FlyCatcher_DB);
        Extra_Rev_Pest.putString(PestRev_MetodeLain_DB, metodeLain_DB);

        //Bagian IV : Luas Outdoor Indoor
        Extra_Rev_Pest.putString(PestRev_LuasIndoor_DB, indoor_DB);
        Extra_Rev_Pest.putString(PestRev_LuasOutdoor_DB, outdoor_DB);

        //Bagian V: Kontrak dan Harga
        Extra_Rev_Pest.putString(PestRev_JenisKerja_Client, jenis_kontrak);
        Extra_Rev_Pest.putString(PestRev_DurasiKontrak_DB, durasi_kontrak_DB);
        Extra_Rev_Pest.putString(PestRev_PenawaranHarga_DB, penawaran_harga_DB);
        Extra_Rev_Pest.putString(PestRev_GrandTotal_Client, total_harga_penawaran);

        //Bagian VI: Status Kerjasama, Catatan dan TTD
        Extra_Rev_Pest.putString(PestRev_Status_Client, status_kerjasama_kontrak);
        Extra_Rev_Pest.putString(PestRev_CatatanTambahan_DB, catatan_tambahan_DB);

        //Bagian VII: Keterangan PDF
        Extra_Rev_Pest.putString(PestRev_Keterangan_Client, keterangan);

        //Bagian IX: Latlong...
        Extra_Rev_Pest.putString(PestRev_LatLong_Alamat_Pelanggan, latlong_AlamatPelanggan);
        Extra_Rev_Pest.putString(PestRev_LatLong_Consultant, latlong_Consultant);

        Review_Pest_Intent.putExtras(Extra_Rev_Pest);
        startActivity(Review_Pest_Intent);
    }
}

