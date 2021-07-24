package com.example.aditmail.fumida.TermiteControl;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TableLayout;
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

public class FragmentFormTermite extends Fragment implements View.OnClickListener {

    private static final String TAG = "TabFormTermite";

    public final static String path_fumida = "com.example.aditmail.fumida";
    public final static String Pesan_Extra = path_fumida + ".Pesan" ;

    //Bagian 1: Data Diri
    public final static String TermiteRev_ID_Client_Termite = path_fumida + ".ClientID_Termite";
    public final static String TermiteRev_Nama_Client_Termite = path_fumida + ".NamaClient_Termite";
    public final static String TermiteRev_Kategori_Client_Termite = path_fumida + ".Kategori_Termite";
    public final static String TermiteRev_Alamat_Client_Termite = path_fumida + ".Alamat_Termite";
    public final static String TermiteRev_Hp_Client_Termite = path_fumida + ".Hp_Termite";
    public final static String TermiteRev_Email_DB = path_fumida + ".Email_DB";

    //Bagian 2: Jenis Rayap dan Kategori Penanganan
    public final static String TermiteRev_Rayap_Client = path_fumida + ".Rayap";
    public final static String TermiteRev_KategoriPenanganan_Termite = path_fumida + ".KategoriPenanganan_Termite";

    //Bagian 3: Metode Kendali dan Chemical
    public final static String TermiteRev_MetodeKendali_DB = path_fumida + ".Metode_Kendali_DB";
    public final static String TermiteRev_Chemical_DB = path_fumida + ".Chemical_DB";

    public final static String TermiteRev_Qty_Fipronil_DB = path_fumida + ".Qty_Fipronil_DB";
    public final static String TermiteRev_Qty_Imidaclporid_DB = path_fumida + ".Qty_Imidaclporid_DB";
    public final static String TermiteRev_Qty_Cypermethrin_DB = path_fumida + ".Qty_Cypermethrin_DB";
    public final static String TermiteRev_Qty_Dichlorphos_DB = path_fumida + ".Qty_Dichlorphos_DB";
    public final static String TermiteRev_Qty_BaitingAG_DB = path_fumida + ".Qty_BaitingAG_DB";
    public final static String TermiteRev_Qty_BaitingIG_DB = path_fumida + ".Qty_BaitingIG_DB";

    //Bagian 4: Luas Indoor Outdoor
    public final static String TermiteRev_Indoor_DB = path_fumida + ".Indoor_DB";
    public final static String TermiteRev_Outdoor_DB = path_fumida + ".Outdoor_DB";

    //Bagian 5: Lantai Bangunan (Pasca Konstruksi)
    public final static String TermiteRev_LantaiBangunan_DB = path_fumida + ".LantaiBangunan_DB";

    public final static String TermiteRev_Qty_Keramik_DB = path_fumida + ".Qty_Keramik_DB";
    public final static String TermiteRev_Keramik_Lain_DB = path_fumida + ".Keramik_Lain_DB";

    public final static String TermiteRev_Qty_Granito_DB = path_fumida + ".Qty_Granito_DB";
    public final static String TermiteRev_Granito_Lain_DB = path_fumida + ".Granito_Lain_DB";

    public final static String TermiteRev_Qty_Marmer_DB = path_fumida + ".Qty_Marmer_DB";
    public final static String TermiteRev_Marmer_Lain_DB = path_fumida + ".Marmer_Lain_DB";

    public final static String TermiteRev_Qty_Teraso_DB = path_fumida + ".Qty_Teraso_DB";
    public final static String TermiteRev_Teraso_Lain_DB = path_fumida + ".Teraso_Lain_DB";

    public final static String TermiteRev_Qty_Granit_DB = path_fumida + ".Qty_Granit_DB";
    public final static String TermiteRev_Granit_Lain_DB = path_fumida + ".Granit_Lain_DB";

    //Bagian 6: Harga, Status dan Catatan
    public final static String TermiteRev_PenawaranHarga_DB = path_fumida + ".PenawaranHarga_DB";
    public final static String TermiteRev_TotalHarga_DB = path_fumida + ".TotalHarga_DB";
    public final static String TermiteRev_Status_Termite = path_fumida + ".Status_Termite";
    public final static String TermiteRev_Catatan_DB = path_fumida + ".Catatan_DB";

    //Bagian 7: Keterangan PDF
    public final static String TermiteRev_Keterangan_Termite = path_fumida + ".Keterangan_Termite";

    public final static String TermiteRev_LatLong_Consultant = path_fumida + ".LatLong_Consultant";
    public final static String TermiteRev_LatLong_Alamat_Pelanggan = path_fumida + ".LatLong_AlamatPelanggan";

    //Untuk Create View Fragment
    private View view;

    //--------------- Bagian I: Identitas Pelanggan ---------------
    private EditText edt_ClientID_Termite, edt_NamaPelanggan_Termite, edt_Alamat_Termite, edt_NoHP_Termite, edt_Email_Termite;
    private Spinner spr_Kategori_Termite;

    private ImageView imgPinCircle;
    protected Double lat,lng;

    /*FOR GPS*/
    protected String latlong_Consultant = "0,0";
    protected String latlong_AlamatPelanggan = "0,0";
    protected TrackGPS gps;
    double longitude;
    double latitude;

    //--------------- Bagian I: Identitas Pelanggan ---------------

    //--------------- Bagian II: Jenis Rayap dan Penanganan---------------
    private Spinner spr_JenisRayap;

    private RadioGroup rdg_KategoriPenanganan;
    protected RadioButton rdb_PraKonstruksi, rdb_PascaKonstruksi;
    //--------------- Bagian II: Jenis Rayap dan Penanganan---------------

    //--------------- Bagian III: Metode Pengendalian Rayap dan Chemical---------------
    private CheckBox chk_Spraying, chk_Drill, chk_BaitingAG, chk_BaitingIG, chk_Fumigasi;
    private CheckBox chk_Fipronil, chk_Imidaclporid, chk_Cypermethrin, chk_Dichlorphos, chk_ChemicalBaitingAG, chk_ChemicalBaitingIG;
    private EditText edt_QtyFipronil, edt_QtyImidaclporid, edt_QtyCypermethrin, edt_QtyDichlorphos, edt_QtyChemicalBaitingAG, edt_QtyChemicalBaitingIG;
    //--------------- Bagian III: Metode Pengendalian Rayap dan Chemical---------------

    //--------------- Bagian IV: Area Lokasi dan Lantai---------------
    private EditText edt_LuasBangunan, edt_LuasOutdoor;

    //Bagian Lantai Bangunan
    private TableLayout tbl_LantaiBangunan;
    private CheckBox chk_Keramik, chk_Granito, chk_Marmer, chk_Granit, chk_Teraso, chk_SolidParquet,
            chk_VinylParquet,chk_LaminatedParquet, chk_LantaiBeton;

    //Untuk Keramik
    private TableLayout tbl_Keramik;
    private CheckBox chk_Keramik30, chk_Keramik40, chk_Keramik60, chk_KeramikLainnya;
    private EditText edt_KeramikLainnya;

    //Untuk Granito
    private TableLayout tbl_Granito;
    private CheckBox chk_Granito30, chk_Granito40, chk_Granito60, chk_GranitoLainnya;
    private EditText edt_GranitoLainnya;

    //Untuk Marmer
    private TableLayout tbl_Marmer;
    private CheckBox chk_Marmer30, chk_Marmer40, chk_Marmer60, chk_MarmerLainnya;
    private EditText edt_MarmerLainnya;

    //Untuk Granit
    private TableLayout tbl_Granit;
    private CheckBox chk_Granit30, chk_Granit40, chk_Granit60, chk_GranitLainnya;
    private EditText edt_GranitLainnya;

    //Untuk Teraso
    private TableLayout tbl_Teraso;
    private CheckBox chk_Teraso30, chk_Teraso40, chk_Teraso60, chk_TerasoLainnya;
    private EditText edt_TerasoLainnya;
    //--------------- Bagian IV: Area Lokasi dan Lantai---------------

    //--------------- Bagian V: Kontrak, Kerjasama dan Catatan ---------------
    private EditText edt_PenawaranHarga, edt_GrandTotal_Termite;
    private RadioGroup rdg_StatusKerjaSama;
    private EditText edt_CatatanTambahan;
    private RadioButton rdb_Berhasil;
    private String penanganan="", status_kerjasama="";
    //--------------- Bagian V: Kontrak, Kerjasama dan Catatan ---------------

    //--------------- Bagian VI: Tanda Tangan ---------------
    private Button btn_DialogTTD_Pelanggan, btn_DialogTTD_Consultant;
    private CheckBox chk_PernyataanPelanggan, chk_PernyataanConsultant;

    @SuppressLint("StaticFieldLeak")
    public static ImageView img_ttdPelanggan, img_ttdConsultant;

    protected Bitmap bitmap_TermiteConsultant, bitmap_TermitePelanggan;

    //--------------- Bagian VI: Tanda Tangan ---------------

    //--------------- Bagian VII: Upload Image---------------
    private Button btn_PilihFotoSatu, btn_PilihFotoDua, btn_PilihFotoTiga, btn_PilihFotoEmpat;

    @SuppressLint("StaticFieldLeak")
    public static ImageView img_FotoSatu, img_FotoDua, img_FotoTiga, img_FotoEmpat;

    private Space spc_Satu, spc_Dua, spc_Tiga;
    //--------------- Bagian VII: Upload Image---------------

    //Button Simpan
    private Button btn_SimpanSurvey;

    //Send Data To Intent -------------------------------------------->
    public static String namaPelanggan;
    private String IDClient, kategoriTempatPelanggan, alamatPelanggan, hpPelanggan, email_DB, jenis_rayap,
            kategori_penanganan, metode_kendali_DB, total_chemical_DB, qty_fipronil_DB, qty_imidaclporid_DB,
            qty_cypermethrin_DB, qty_dichlorphos_DB, qty_baitingAG_DB, qty_baitingIG_DB, indoor_DB, outdoor_DB,
            lantai_bangunan_DB, qty_keramik_DB, keramik_lain_DB, qty_granito_DB, granito_lain_DB,
            qty_marmer_DB, marmer_lain_DB, qty_teraso_DB, teraso_lain_DB, qty_granit_DB, granit_lain_DB,
            penawaran_harga_DB, total_harga_penawaran_DB, status, catatan_tambahan_DB, keterangan;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_form_termite, container, false);

        InitData();

        JenisChemical();
        JenisLantaiBangunan();
        CekRadioGroup();
        TextWatcher();
        CekTandaTangan();
        getCurrentLocationRoute();
        return view;
    }

    @Override
    public void onStart(){
        OpenMaps();
        OpenPhoto();
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        if(v == btn_SimpanSurvey){
           simpan_pelanggan();
        }
    }

    private void InitData(){
        //Bagian Identitas Pelanggan
        edt_ClientID_Termite = view.findViewById(R.id.editText_ClientID_Termite);
        edt_NamaPelanggan_Termite = view.findViewById(R.id.editText_NamaPelanggan_Termite);    edt_Alamat_Termite = view.findViewById(R.id.editText_Alamat_Termite);
        edt_NoHP_Termite = view.findViewById(R.id.editText_NoHP_Termite);                      edt_Email_Termite = view.findViewById(R.id.editText_Email_Termite);
        spr_Kategori_Termite = view.findViewById(R.id.spinner_Kategori_Termite);               imgPinCircle = view.findViewById(R.id.imgPin_Termite);

        //Get Nama Pelanggan
        namaPelanggan = edt_NamaPelanggan_Termite.getText().toString();

        //Bagian Jenis Rayap
        spr_JenisRayap = view.findViewById(R.id.spinner_JenisRayap);

        //Bagian Kategori Penanganan Rayap
        rdg_KategoriPenanganan = view.findViewById(R.id.RadioGroup_PenangananRayap);
        rdb_PraKonstruksi = view.findViewById(R.id.radioButton_PraKonstruksi);  rdb_PascaKonstruksi = view.findViewById(R.id.radioButton_PascaKonstruksi);

        //Bagian Metode Pengendalian Rayap
        chk_Spraying = view.findViewById(R.id.checkBox_Spraying);      chk_Drill = view.findViewById(R.id.checkBox_InjectDrill);
        chk_BaitingAG = view.findViewById(R.id.checkBox_BaitingAG);    chk_BaitingIG = view.findViewById(R.id.checkBox_BaitingIG);
        chk_Fumigasi = view.findViewById(R.id.checkBox_Fumigasi);

        //Bagian Chemical yg Digunakan
        chk_Fipronil = view.findViewById(R.id.checkBox_Fipronil);                      chk_Imidaclporid = view.findViewById(R.id.checkBox_Imidaclporid);
        chk_Cypermethrin = view.findViewById(R.id.checkBox_Cypermethrin);              chk_Dichlorphos = view.findViewById(R.id.checkBox_Dichlorphos);
        chk_ChemicalBaitingAG = view.findViewById(R.id.checkBox_BaitingAG_Chemical);   chk_ChemicalBaitingIG = view.findViewById(R.id.checkBox_BaitingIG_Chemical);

        edt_QtyFipronil = view.findViewById(R.id.editText_QtyFipronil);            edt_QtyImidaclporid = view.findViewById(R.id.editText_QtyImidaclporid);
        edt_QtyCypermethrin = view.findViewById(R.id.editText_QtyCypermethrin);    edt_QtyDichlorphos = view.findViewById(R.id.editText_QtyDichlorphos);
        edt_QtyChemicalBaitingAG = view.findViewById(R.id.editText_QtyBaitingAG);  edt_QtyChemicalBaitingIG = view.findViewById(R.id.editText_QtyBaitingIG);

        //Bagian Area Lokasi
        edt_LuasBangunan = view.findViewById(R.id.editText_LuasBangunan_Termite);  edt_LuasOutdoor = view.findViewById(R.id.editText_LuasOutdoor_Termite);

        //Bagian Lantai Bangunan
        tbl_LantaiBangunan = view.findViewById(R.id.TableLayout_LantaiBangunan);
        chk_Keramik = view.findViewById(R.id.checkBox_Keramik);    chk_Granito = view.findViewById(R.id.checkBox_Granito);
        chk_Marmer = view.findViewById(R.id.checkBox_Marmer);      chk_Granit = view.findViewById(R.id.checkBox_Granit);
        chk_Teraso = view.findViewById(R.id.checkBox_Teraso);      chk_SolidParquet = view.findViewById(R.id.checkBox_SolidParquet);
        chk_VinylParquet = view.findViewById(R.id.checkBox_VinylParquet); chk_LaminatedParquet = view.findViewById(R.id.checkBox_LaminatedParquet);
        chk_LantaiBeton = view.findViewById(R.id.checkBox_LantaiBeton);

        //Untuk Keramik
        tbl_Keramik = view.findViewById(R.id.TableLayout_Keramik);
        chk_Keramik30 = view.findViewById(R.id.checkBox_Keramik30);  chk_Keramik40 = view.findViewById(R.id.checkBox_Keramik40);
        chk_Keramik60 = view.findViewById(R.id.checkBox_Keramik60);  chk_KeramikLainnya = view.findViewById(R.id.checkBox_KeramikLainnya);
        edt_KeramikLainnya = view.findViewById(R.id.editText_KeramikLainnya);

        //Untuk Granito
        tbl_Granito = view.findViewById(R.id.TableLayout_Granito);
        chk_Granito30 = view.findViewById(R.id.checkBox_Granito30);  chk_Granito40 = view.findViewById(R.id.checkBox_Granito40);
        chk_Granito60 = view.findViewById(R.id.checkBox_Granito60);  chk_GranitoLainnya = view.findViewById(R.id.checkBox_GranitoLainnya);
        edt_GranitoLainnya = view.findViewById(R.id.EditText_GranitoLainnya);

        //Untuk Marmer
        tbl_Marmer = view.findViewById(R.id.TableLayout_Marmer);
        chk_Marmer30 = view.findViewById(R.id.checkBox_Marmer30);  chk_Marmer40 = view.findViewById(R.id.checkBox_Marmer40);
        chk_Marmer60 = view.findViewById(R.id.checkBox_Marmer60);  chk_MarmerLainnya = view.findViewById(R.id.checkBox_MarmerLainnya);
        edt_MarmerLainnya = view.findViewById(R.id.EditText_MarmerLainnya);

        //Untuk Granit
        tbl_Granit = view.findViewById(R.id.TableLayout_Granit);
        chk_Granit30 = view.findViewById(R.id.checkBox_Granit30);  chk_Granit40 = view.findViewById(R.id.checkBox_Granit40);
        chk_Granit60 = view.findViewById(R.id.checkBox_Granit60);  chk_GranitLainnya = view.findViewById(R.id.checkBox_GranitLainnya);
        edt_GranitLainnya = view.findViewById(R.id.EditText_GranitLainnya);

        //Untuk Teraso
        tbl_Teraso = view.findViewById(R.id.TableLayout_Teraso);
        chk_Teraso30 = view.findViewById(R.id.checkBox_Teraso30);  chk_Teraso40 = view.findViewById(R.id.checkBox_Teraso40);
        chk_Teraso60 = view.findViewById(R.id.checkBox_Teraso60);  chk_TerasoLainnya = view.findViewById(R.id.checkBox_TerasoLainnya);
        edt_TerasoLainnya = view.findViewById(R.id.EditText_TerasoLainnya);

        //Kontrak dan Penawaran Harga
        edt_PenawaranHarga = view.findViewById(R.id.editText_PenawaranHarga_Termite);
        edt_GrandTotal_Termite = view.findViewById(R.id.editText_GrandTotal_Termite);
        rdg_StatusKerjaSama = view.findViewById(R.id.RadioGroup_Status);
        rdb_Berhasil = view.findViewById(R.id.radioButton_Berhasil);

        //Bagian Catatan Tambahan
        edt_CatatanTambahan = view.findViewById(R.id.editText_CatatanTambahan_Termite);

        //Button Simpan
        btn_SimpanSurvey = view.findViewById(R.id.button_SimpanSurvey_Termite);
        btn_SimpanSurvey.setOnClickListener(this);

        //Bagian TTD - Signature
        btn_DialogTTD_Pelanggan = view.findViewById(R.id.button_GetTTD_Pelanggan_Termite); btn_DialogTTD_Consultant = view.findViewById(R.id.button_GetTTD_Consultant_Termite);
        img_ttdPelanggan = view.findViewById(R.id.imageView_TTD_Pelanggan_Termite);  img_ttdConsultant = view.findViewById(R.id.imageView_TTD_Consultant_Termite);
        chk_PernyataanPelanggan = view.findViewById(R.id.checkBox_PernyataanCustomer_Termite);  chk_PernyataanConsultant = view.findViewById(R.id.checkBox_PernyataanPestConsultant_Termite);

        //Bagian upload Foto
        btn_PilihFotoSatu = view.findViewById(R.id.button_FotoSatu);    img_FotoSatu = view.findViewById(R.id.imageView_FotoSatu);
        btn_PilihFotoDua = view.findViewById(R.id.button_FotoDua);    img_FotoDua = view.findViewById(R.id.imageView_FotoDua);
        btn_PilihFotoTiga = view.findViewById(R.id.button_FotoTiga);    img_FotoTiga = view.findViewById(R.id.imageView_FotoTiga);
        btn_PilihFotoEmpat = view.findViewById(R.id.button_FotoEmpat);    img_FotoEmpat = view.findViewById(R.id.imageView_FotoEmpat);

        spc_Satu = view.findViewById(R.id.Space_Satu);    spc_Dua = view.findViewById(R.id.Space_Dua);
        spc_Tiga = view.findViewById(R.id.Space_Tiga);

        if(getActivity()!= null)
            locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    private void OpenMaps(){
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
    private void OpenPlacePicker(){
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

    private void getCurrentLocationRoute(){
        gps = new TrackGPS(getContext());
        if(gps.canGetLocation()){
            longitude = gps.getLongitude();
            latitude = gps .getLatitude();

            latlong_Consultant = latitude + "," + longitude;
            latlong_AlamatPelanggan = latitude + "," + longitude;

            Log.e("tag", latlong_Consultant);
        }else{
            gps.showSettingsAlert();
        }
    }

    private void OpenPhoto(){
        //Open galeri
        btn_PilihFotoSatu.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_FotoSatu.setVisibility(View.VISIBLE);   spc_Satu.setVisibility(View.VISIBLE);
                img_FotoDua.setVisibility(View.VISIBLE);    btn_PilihFotoDua.setVisibility(View.VISIBLE);
                showPictureDialog_Satu();
            }
        });

        btn_PilihFotoDua.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_FotoTiga.setVisibility(View.VISIBLE);   spc_Dua.setVisibility(View.VISIBLE);
                btn_PilihFotoTiga.setVisibility(View.VISIBLE);
                showPictureDialog_Dua();
            }
        });

        btn_PilihFotoTiga.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_FotoEmpat.setVisibility(View.VISIBLE); spc_Tiga.setVisibility(View.VISIBLE);
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

    private void showPictureDialog_Satu(){
        android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(getContext());
        pictureDialog.setTitle("Pilih Aksi");

        String[]pictureDialogItems = {
                "Pilih Foto dari Galeri", "Ambil Foto dari Kamera"};

        pictureDialog.setItems(pictureDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
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

    private void showPictureDialog_Dua(){
        android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(getContext());
        pictureDialog.setTitle("Pilih Aksi");

        String[]pictureDialogItems = {
                "Pilih Foto dari Galeri", "Ambil Foto dari Kamera"};

        pictureDialog.setItems(pictureDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_GALERI_DUA);
                        break;

                    case 1:
                        Intent intent_cameraDua = new Intent( MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent_cameraDua, REQ_KAMERA_DUA);
                        break;
                }
            }
        });
        pictureDialog.show();
    }

    private void showPictureDialog_Tiga(){
        android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(getContext());
        pictureDialog.setTitle("Pilih Aksi");

        String[]pictureDialogItems = {
                "Pilih Foto dari Galeri", "Ambil Foto dari Kamera"};

        pictureDialog.setItems(pictureDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_GALERI_TIGA);
                        break;

                    case 1:
                        Intent intent_cameraDua = new Intent( MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent_cameraDua, REQ_KAMERA_TIGA);
                        break;
                }
            }
        });
        pictureDialog.show();
    }

    private void showPictureDialog_Empat(){
        android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(getContext());
        pictureDialog.setTitle("Pilih Aksi");

        String[]pictureDialogItems = {
                "Pilih Foto dari Galeri", "Ambil Foto dari Kamera"};

        pictureDialog.setItems(pictureDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_GALERI_EMPAT);
                        break;

                    case 1:
                        Intent intent_cameraDua = new Intent( MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent_cameraDua, REQ_KAMERA_EMPAT);
                        break;
                }
            }
        });
        pictureDialog.show();
    }

    //Buka Foto Galeri
    private void openGaleri(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        intent.putExtra(Pesan_Extra, "Foto_Satu");

        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_GALERI_SATU);
    }

    private void openKamera(){
        Intent intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQ_KAMERA_SATU);
    }

    //Request Location, External and Camera..
    public void isStoragePermissionGranted(){
        if (Build.VERSION.SDK_INT >=23){
            if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()),android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                OpenPlacePicker();
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},99);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[]permissons, @NonNull int[]grantResults){
        super.onRequestPermissionsResult(requestCode,permissons,grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            OpenPlacePicker();
        } else{
            Toast.makeText(getContext(), "Aplikasi tidak diberikan izin untuk menyimpan data kedalam penyimpanan. Sehingga aplikasi tidak dapat berjalan normal." +
                    "Mohon pertimbangkan untuk memberikan Izin akses.", Toast.LENGTH_LONG).show();
        }
    }

    private void JenisChemical(){
        chk_Fipronil.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    edt_QtyFipronil.setVisibility(View.VISIBLE);
                    edt_QtyFipronil.requestFocus();
                }else{
                    edt_QtyFipronil.setVisibility(View.GONE); edt_QtyFipronil.setText("");
                }
            }
        });

        chk_Imidaclporid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    edt_QtyImidaclporid.setVisibility(View.VISIBLE);
                    edt_QtyImidaclporid.requestFocus();
                }else{
                    edt_QtyImidaclporid.setVisibility(View.GONE); edt_QtyImidaclporid.setText("");
                }
            }
        });

        chk_Cypermethrin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    edt_QtyCypermethrin.setVisibility(View.VISIBLE);
                    edt_QtyCypermethrin.requestFocus();
                }else{
                    edt_QtyCypermethrin.setVisibility(View.GONE); edt_QtyCypermethrin.setText("");
                }
            }
        });

        chk_Dichlorphos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    edt_QtyDichlorphos.setVisibility(View.VISIBLE);
                    edt_QtyDichlorphos.requestFocus();
                }else{
                    edt_QtyDichlorphos.setVisibility(View.GONE); edt_QtyDichlorphos.setText("");
                }
            }
        });

        chk_ChemicalBaitingAG.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    edt_QtyChemicalBaitingAG.setVisibility(View.VISIBLE);
                    edt_QtyChemicalBaitingAG.requestFocus();
                }else{
                    edt_QtyChemicalBaitingAG.setVisibility(View.GONE); edt_QtyChemicalBaitingAG.setText("");
                }
            }
        });

        chk_ChemicalBaitingIG.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    edt_QtyChemicalBaitingIG.setVisibility(View.VISIBLE);
                    edt_QtyChemicalBaitingIG.requestFocus();
                }else{
                    edt_QtyChemicalBaitingIG.setVisibility(View.GONE); edt_QtyChemicalBaitingIG.setText("");
                }
            }
        });
    }

    private void JenisLantaiBangunan(){
        chk_Keramik.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    tbl_Keramik.setVisibility(View.VISIBLE);
                }else{
                    tbl_Keramik.setVisibility(View.GONE);
                    chk_Keramik30.setChecked(false); chk_Keramik40.setChecked(false); chk_Keramik60.setChecked(false);
                    chk_Keramik60.setChecked(false); chk_KeramikLainnya.setChecked(false);
                }
            }
        });

        chk_KeramikLainnya.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    edt_KeramikLainnya.setVisibility(View.VISIBLE);
                    edt_KeramikLainnya.requestFocus();
                }else{
                    edt_KeramikLainnya.setVisibility(View.GONE); edt_KeramikLainnya.setText("");
                }
            }
        });

        chk_Granito.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    tbl_Granito.setVisibility(View.VISIBLE);
                }else{
                    tbl_Granito.setVisibility(View.GONE);
                    chk_Granito30.setChecked(false); chk_Granito40.setChecked(false); chk_Granito60.setChecked(false);
                    chk_Granito60.setChecked(false); chk_GranitoLainnya.setChecked(false);
                }
            }
        });

        chk_GranitoLainnya.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    edt_GranitoLainnya.setVisibility(View.VISIBLE);
                    edt_GranitoLainnya.requestFocus();
                }else{
                    edt_GranitoLainnya.setVisibility(View.GONE); edt_GranitoLainnya.setText("");
                }
            }
        });

        chk_Marmer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    tbl_Marmer.setVisibility(View.VISIBLE);
                }else{
                    tbl_Marmer.setVisibility(View.GONE);
                    chk_Marmer30.setChecked(false); chk_Marmer40.setChecked(false); chk_Marmer60.setChecked(false);
                    chk_Marmer60.setChecked(false); chk_MarmerLainnya.setChecked(false);
                }
            }
        });

        chk_MarmerLainnya.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    edt_MarmerLainnya.setVisibility(View.VISIBLE);
                    edt_MarmerLainnya.requestFocus();
                }else{
                    edt_MarmerLainnya.setVisibility(View.GONE); edt_MarmerLainnya.setText("");
                }
            }
        });

        chk_Granit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    tbl_Granit.setVisibility(View.VISIBLE);
                }else{
                    tbl_Granit.setVisibility(View.GONE);
                    chk_Granit30.setChecked(false); chk_Granit40.setChecked(false); chk_Granit60.setChecked(false);
                    chk_Granit60.setChecked(false); chk_GranitLainnya.setChecked(false);
                }
            }
        });

        chk_GranitLainnya.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    edt_GranitLainnya.setVisibility(View.VISIBLE);
                    edt_GranitLainnya.requestFocus();
                }else{
                    edt_GranitLainnya.setVisibility(View.GONE); edt_GranitLainnya.setText("");
                }
            }
        });

        chk_Teraso.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    tbl_Teraso.setVisibility(View.VISIBLE);
                }else{
                    tbl_Teraso.setVisibility(View.GONE);
                    chk_Teraso30.setChecked(false); chk_Teraso40.setChecked(false); chk_Teraso60.setChecked(false);
                    chk_Teraso60.setChecked(false); chk_TerasoLainnya.setChecked(false);
                }
            }
        });

        chk_TerasoLainnya.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    edt_TerasoLainnya.setVisibility(View.VISIBLE);
                    edt_TerasoLainnya.requestFocus();
                }else{
                    edt_TerasoLainnya.setVisibility(View.GONE); edt_TerasoLainnya.setText("");
                }
            }
        });
    }

    private void CekRadioGroup(){
        rdg_StatusKerjaSama.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
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

        rdg_KategoriPenanganan.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.radioButton_PraKonstruksi:
                        tbl_LantaiBangunan.setVisibility(View.GONE);
                        chk_Keramik.setChecked(false);  chk_Granito.setChecked(false);
                        chk_Granit.setChecked(false);   chk_Teraso.setChecked(false);
                        chk_Marmer.setChecked(false);   chk_LantaiBeton.setChecked(false);
                        chk_SolidParquet.setChecked(false);   chk_VinylParquet.setChecked(false);
                        chk_LaminatedParquet.setChecked(false);
                        penanganan = "Anti Rayap Pra-Konstruksi GARANSI 5 TAHUN";
                        break;
                    case R.id.radioButton_PascaKonstruksi:
                        tbl_LantaiBangunan.setVisibility(View.VISIBLE);
                        penanganan = "Anti Rayap Pasca-Konstruksi GARANSI 3 TAHUN";
                }
            }
        });
    }

    private String kalkulasi_total(){
        double total, a;

        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

        if (!edt_PenawaranHarga.getText().toString().equals("") && edt_PenawaranHarga.getText().length() > 0){
            String penawaran = edt_PenawaranHarga.getText().toString();
            a = Double.parseDouble(penawaran);
            formatRupiah.format(a);
        } else {
            a = 0;
        }

        total = a;

        return (formatRupiah.format(total));
    }

    private void TextWatcher(){
        edt_PenawaranHarga.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edt_GrandTotal_Termite.setText(kalkulasi_total());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edt_NoHP_Termite.addTextChangedListener(new TextWatcher() {

            int keyDel;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edt_NoHP_Termite.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                        if (keyCode == KeyEvent.KEYCODE_DEL) {
                            keyDel = 1;
                        }
                        return false;
                    }
                });

                String currentString = edt_NoHP_Termite.getText().toString();
                int currentLength = edt_NoHP_Termite.getText().length();

                if (currentLength == 5 || currentLength == 10) {
                    keyDel = 1;
                }

                if (keyDel == 0) {
                    if (currentLength == 4 || currentLength == 9) {
                        edt_NoHP_Termite.setText(edt_NoHP_Termite.getText() + "-");
                        edt_NoHP_Termite.setSelection(edt_NoHP_Termite.getText().length());
                    }
                } else {
                    if (currentLength != 5 && currentLength != 10) {
                        keyDel = 0;
                    } else if ((currentLength == 5 || currentLength == 10)
                            && !"-".equals(currentString.substring(currentLength - 1, currentLength))) {
                        edt_NoHP_Termite.setText(currentString.substring(0, currentLength - 1) + "-"
                                + currentString.substring(currentLength - 1, currentLength));
                        edt_NoHP_Termite.setSelection(edt_NoHP_Termite.getText().length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

    }

    public void CekTandaTangan(){
        btn_DialogTTD_Pelanggan.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), SignatureActivity.class);
                i.putExtra(Pesan_Extra, "TTD_PelangganTermiteControl");
                startActivityForResult(i,REQ_TTD_PELANGGAN);
            }
        });

        btn_DialogTTD_Consultant.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(getContext(), SignatureActivity.class);
                a.putExtra(Pesan_Extra, "TTD_ConsultantTermiteControl");
                startActivityForResult(a,REQ_TTD_CONSULTANT);
            }
        });


        chk_PernyataanPelanggan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    btn_DialogTTD_Pelanggan.setEnabled(true);
                }else{
                    btn_DialogTTD_Pelanggan.setEnabled(false);
                }
            }
        });

        chk_PernyataanConsultant.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    btn_DialogTTD_Consultant.setEnabled(true);
                }else{
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_TTD_PELANGGAN) {
            if (resultCode == Activity.RESULT_OK) {
                String image_path = data.getStringExtra("imagePath_TermitePelanggan");
                bitmap_TermitePelanggan = BitmapFactory.decodeFile(image_path);
                img_ttdPelanggan.setImageBitmap(bitmap_TermitePelanggan);
            }
        }
        if (requestCode == REQ_TTD_CONSULTANT) {
            if (resultCode == Activity.RESULT_OK) {
                String image_path_termite = data.getStringExtra("imagePath_TermiteConsultant");
                bitmap_TermiteConsultant = BitmapFactory.decodeFile(image_path_termite);
                img_ttdConsultant.setImageBitmap(bitmap_TermiteConsultant);
            }
        }

        //Untuk Picture atau Kamera
        if (requestCode == REQ_KAMERA_SATU){
            if (resultCode == Activity.RESULT_OK){
                Bitmap bitmap_PickFoto = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();

                if(bitmap_PickFoto!=null) {
                    bitmap_PickFoto.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
                    img_FotoSatu.setImageBitmap(bitmap_PickFoto);
                }
            }
        }

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

        if (requestCode == REQ_KAMERA_DUA){
            if (resultCode == Activity.RESULT_OK){
                Bitmap bitmap_PickFoto = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                if(bitmap_PickFoto!=null) {
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
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap_Foto1, 300, 400, false);
                    scaled = getResizedBitmap(scaled, 300);
                    img_FotoDua.setImageBitmap(scaled);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (requestCode == REQ_KAMERA_TIGA){
            if (resultCode == Activity.RESULT_OK){
                Bitmap bitmap_PickFoto = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                if(bitmap_PickFoto!=null) {
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
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap_Foto1, 300, 400, false);
                    scaled = getResizedBitmap(scaled, 300);
                    img_FotoTiga.setImageBitmap(scaled);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (requestCode == REQ_KAMERA_EMPAT){
            if (resultCode == Activity.RESULT_OK){
                Bitmap bitmap_PickFoto = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                if(bitmap_PickFoto!=null) {
                    bitmap_PickFoto.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    img_FotoEmpat.setImageBitmap(bitmap_PickFoto);
                }
            }
        }

        if (requestCode == REQ_GALERI_EMPAT) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                try {
                    Bitmap bitmap_Foto1 = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(), imageUri);
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
            String lokasi = data.getStringExtra("Alamat_Lokasi");
            edt_Alamat_Termite.setText(lokasi);

            latlong_AlamatPelanggan = data.getStringExtra("LatLong_Lokasi");
            Log.e("tag", latlong_AlamatPelanggan);
        }
    }

    private void simpan_pelanggan() {
        //Untuk Bagian I - Identitas Diri
        IDClient = edt_ClientID_Termite.getText().toString().toUpperCase();
        namaPelanggan = edt_NamaPelanggan_Termite.getText().toString();
        kategoriTempatPelanggan = spr_Kategori_Termite.getSelectedItem().toString();
        alamatPelanggan = edt_Alamat_Termite.getText().toString();
        hpPelanggan = edt_NoHP_Termite.getText().toString();
        email_DB = edt_Email_Termite.getText().toString();

        if (TextUtils.isEmpty(IDClient)) {
            edt_ClientID_Termite.setError("Harap Masukkan ID Client");
            edt_ClientID_Termite.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(namaPelanggan)) {
            edt_NamaPelanggan_Termite.setError("Harap Masukkan Nama Pelanggan/Perusahaan");
            edt_NamaPelanggan_Termite.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(alamatPelanggan)) {
            edt_Alamat_Termite.setError("Harap Masukkan Alamat Pelanggan/Perusahaan");
            edt_Alamat_Termite.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(hpPelanggan)) {
            edt_NoHP_Termite.setError("Harap Masukkan No.HP atau Telepon Pelanggan/Perusahaan");
            edt_NoHP_Termite.requestFocus();
            return;
        }

        if (hpPelanggan.trim().length() < 5) {
            edt_NoHP_Termite.setError("Harap Masukkan Nomor Handphone/Telepon yang Valid");
            edt_NoHP_Termite.requestFocus();
            return;
        }

        //Untuk Bagian II - Rayap
        //Ikut disimpan ke DB
        jenis_rayap = spr_JenisRayap.getSelectedItem().toString();
        kategori_penanganan = penanganan;

        if (kategori_penanganan.isEmpty()) {
            rdb_PraKonstruksi.setError("Harap Memilih Kategori Penanganan");
            rdb_PraKonstruksi.requestFocusFromTouch();
            return;
        }

        //Disimpan ke temporary DB
        String kendali_tmp = "";
        if (chk_Spraying.isChecked()) {
            kendali_tmp += "Spraying,";
        }

        if (chk_Drill.isChecked()) {
            kendali_tmp += "Drill Inject,";
        }

        if (chk_BaitingAG.isChecked()) {
            kendali_tmp += "Termite Baiting AG,";
        }

        if (chk_BaitingIG.isChecked()) {
            kendali_tmp += "Termite Baiting IG,";
        }

        if (chk_Fumigasi.isChecked()) {
            kendali_tmp += "Fumigasi,";
        }

        //Simpan ke DB
        metode_kendali_DB = kendali_tmp;

        if (TextUtils.isEmpty(metode_kendali_DB)) {
            chk_Spraying.setError("Harap Memilih Minimal Satu Metode Kendali Rayap");
            chk_Spraying.requestFocusFromTouch();
            return;
        }

        //Bagian 3 : Chemical
        //Tmp ke DB
        String jenis_chemical_tmp = "";
        String qty_fipronil_tmp = "";
        String qty_imidaclporid_tmp = "";
        String qty_cypermethrin_tmp = "";
        String qty_dichlorphos_tmp = "";
        String qty_baitingAG_tmp = "";
        String qty_baitingIG_tmp = "";

        if (chk_Fipronil.isChecked()) {
            qty_fipronil_tmp = edt_QtyFipronil.getText().toString();

            if (TextUtils.isEmpty(qty_fipronil_tmp)) {
                edt_QtyFipronil.setError("Harap Masukkan Banyak Botol yang Dibutuhkan");
                edt_QtyFipronil.requestFocus();
                return;
            }

            //Simpan ke tmp
            jenis_chemical_tmp += "Fipronil 50 g/l,";
        }

        if (chk_Imidaclporid.isChecked()) {
            qty_imidaclporid_tmp = edt_QtyImidaclporid.getText().toString();

            if (TextUtils.isEmpty(qty_imidaclporid_tmp)) {
                edt_QtyImidaclporid.setError("Harap Masukkan Banyak Botol yang Dibutuhkan");
                edt_QtyImidaclporid.requestFocus();
                return;
            }
            //Simpan ke tmp
            jenis_chemical_tmp += "Imidacloprid 200 g/l,";
        }

        if (chk_Cypermethrin.isChecked()) {
            qty_cypermethrin_tmp = edt_QtyCypermethrin.getText().toString();

            if (TextUtils.isEmpty(qty_cypermethrin_tmp)) {
                edt_QtyCypermethrin.setError("Harap Masukkan Banyak Botol yang Dibutuhkan");
                edt_QtyCypermethrin.requestFocus();
                return;
            }
            //Simpan ke tmp
            jenis_chemical_tmp += "Cypermethrin 100 g/l,";
        }

        if (chk_Dichlorphos.isChecked()) {
            qty_dichlorphos_tmp = edt_QtyDichlorphos.getText().toString();

            if (TextUtils.isEmpty(qty_dichlorphos_tmp)) {
                edt_QtyDichlorphos.setError("Harap Masukkan Banyak Botol yang Dibutuhkan");
                edt_QtyDichlorphos.requestFocus();
                return;
            }
            //Simpan ke tmp
            jenis_chemical_tmp += "Dichlorphos 200 g/l,";
        }

        if (chk_ChemicalBaitingAG.isChecked()) {
            qty_baitingAG_tmp = edt_QtyChemicalBaitingAG.getText().toString();

            if (TextUtils.isEmpty(qty_baitingAG_tmp)) {
                edt_QtyChemicalBaitingAG.setError("Harap Masukkan Banyak Unit yang Dibutuhkan");
                edt_QtyChemicalBaitingAG.requestFocus();
                return;
            }
            //Simpan ke tmp
            jenis_chemical_tmp += "Baiting AG,";
        }

        if (chk_ChemicalBaitingIG.isChecked()) {
            qty_baitingIG_tmp = edt_QtyChemicalBaitingIG.getText().toString();

            if (TextUtils.isEmpty(qty_baitingIG_tmp)) {
                edt_QtyChemicalBaitingIG.setError("Harap Masukkan Banyak Unit yang Dibutuhkan");
                edt_QtyChemicalBaitingIG.requestFocus();
                return;
            }
            //Simpan ke tmp
            jenis_chemical_tmp += "Baiting IG,";
        }

        //Simpan ke DB
        total_chemical_DB = jenis_chemical_tmp;
        qty_fipronil_DB = qty_fipronil_tmp;
        qty_imidaclporid_DB = qty_imidaclporid_tmp;
        qty_cypermethrin_DB = qty_cypermethrin_tmp;
        qty_dichlorphos_DB = qty_dichlorphos_tmp;
        qty_baitingAG_DB = qty_baitingAG_tmp;
        qty_baitingIG_DB = qty_baitingIG_tmp;

        if (TextUtils.isEmpty(total_chemical_DB)) {
            chk_Fipronil.setError("Harap Memilih Minimal Satu Jenis Chemical yang Digunakan");
            chk_Fipronil.requestFocusFromTouch();
            return;
        }

        //Bagian 4: Luas Outdoor Indoor
        indoor_DB = edt_LuasBangunan.getText().toString();
        outdoor_DB = edt_LuasOutdoor.getText().toString();

        if (TextUtils.isEmpty(indoor_DB)) {
            edt_LuasBangunan.setError("Harap Masukkan Luas Bangunan");
            edt_LuasBangunan.requestFocus();
            return;
        }


        //Bagian 5 : Lantai Bangunan - Pasca Konstruksi
        //tmp ke DB
        String bangunan_lantai_tmp = "";

        String qty_keramik_tmp = "";
        String ukuran_keramik_lain_tmp = "";

        String qty_granito_tmp = "";
        String ukuran_granito_lain_tmp = "";

        String qty_marmer_tmp = "";
        String ukuran_marmer_lain_tmp = "";

        String qty_teraso_tmp = "";
        String ukuran_teraso_lain_tmp = "";

        String qty_granit_tmp = "";
        String ukuran_granit_lain_tmp = "";

        if (penanganan.equals("Anti Rayap Pasca-Konstruksi GARANSI 3 TAHUN")) {
            //Untuk Keramik
            if (chk_Keramik.isChecked()) {
                //tmp ke DB
                bangunan_lantai_tmp += "Keramik,";

                if (!chk_Keramik30.isChecked() && !chk_Keramik40.isChecked() && !chk_Keramik60.isChecked() &&
                        !chk_KeramikLainnya.isChecked()) {
                    chk_Keramik30.setError("Harap Memilih Minimal Satu Jenis Ukuran Lantai Bangunan");
                    chk_Keramik30.requestFocusFromTouch();
                    return;
                }

                if (chk_Keramik30.isChecked()) {
                    //tmp ke DB
                    qty_keramik_tmp += "30x30,";
                }
                if (chk_Keramik40.isChecked()) {
                    //tmp ke DB
                    qty_keramik_tmp += "40x40,";
                }
                if (chk_Keramik60.isChecked()) {
                    //tmp ke DB
                    qty_keramik_tmp += "60x60,";
                }
                if (chk_KeramikLainnya.isChecked()) {
                    ukuran_keramik_lain_tmp += edt_KeramikLainnya.getText().toString();

                    if (TextUtils.isEmpty(ukuran_keramik_lain_tmp)) {
                        edt_KeramikLainnya.setError("Harap Memasukkan Ukuran Lantai");
                        edt_KeramikLainnya.requestFocus();
                        return;
                    }
                }
            }

            //Untuk Granito
            if (chk_Granito.isChecked()) {
                //tmp ke DB
                bangunan_lantai_tmp += "Granit Tile,";

                if (!chk_Granito30.isChecked() && !chk_Granito40.isChecked() && !chk_Granito60.isChecked() &&
                        !chk_GranitoLainnya.isChecked()) {
                    chk_Granito30.setError("Harap Memilih Minimal Satu Jenis Ukuran Lantai Bangunan");
                    chk_Granito30.requestFocusFromTouch();
                    return;
                }

                if (chk_Granito30.isChecked()) {
                    //tmp ke DB
                    qty_granito_tmp += "30x30,";
                }
                if (chk_Granito40.isChecked()) {
                    //tmp ke DB
                    qty_granito_tmp += "40x40,";
                }
                if (chk_Granito60.isChecked()) {
                    //tmp ke DB
                    qty_granito_tmp += "60x60,";
                }
                if (chk_GranitoLainnya.isChecked()) {
                    ukuran_granito_lain_tmp += edt_GranitoLainnya.getText().toString();

                    if (TextUtils.isEmpty(ukuran_granito_lain_tmp)) {
                        edt_GranitoLainnya.setError("Harap Memasukkan Ukuran Lantai");
                        edt_GranitoLainnya.requestFocus();
                        return;
                    }
                }
            }

            //Untuk Marmer
            if (chk_Marmer.isChecked()) {
                //tmp ke DB
                bangunan_lantai_tmp += "Marmer,";

                if (!chk_Marmer30.isChecked() && !chk_Marmer40.isChecked() && !chk_Marmer60.isChecked() &&
                        !chk_MarmerLainnya.isChecked()) {
                    chk_Marmer30.setError("Harap Memilih Minimal Satu Jenis Ukuran Lantai Bangunan");
                    chk_Marmer30.requestFocusFromTouch();
                    return;
                }

                if (chk_Marmer30.isChecked()) {
                    //tmp ke DB
                    qty_marmer_tmp += "30x30,";
                }
                if (chk_Marmer40.isChecked()) {
                    //tmp ke DB
                    qty_marmer_tmp += "40x40,";
                }
                if (chk_Marmer60.isChecked()) {
                    //tmp ke DB
                    qty_marmer_tmp += "60x60,";
                }
                if (chk_MarmerLainnya.isChecked()) {
                    ukuran_marmer_lain_tmp += edt_MarmerLainnya.getText().toString();

                    if (TextUtils.isEmpty(ukuran_marmer_lain_tmp)) {
                        edt_MarmerLainnya.setError("Harap Memasukkan Ukuran Lantai");
                        edt_MarmerLainnya.requestFocus();
                        return;
                    }
                }
            }

            //Untuk Teraso
            if (chk_Teraso.isChecked()) {
                //tmp ke DB
                bangunan_lantai_tmp += "Teraso,";

                if (!chk_Teraso30.isChecked() && !chk_Teraso40.isChecked() && !chk_Teraso60.isChecked() &&
                        !chk_TerasoLainnya.isChecked()) {
                    chk_Teraso30.setError("Harap Memilih Minimal Satu Jenis Ukuran Lantai Bangunan");
                    chk_Teraso30.requestFocusFromTouch();
                    return;
                }

                if (chk_Teraso30.isChecked()) {
                    //tmp ke DB
                    qty_teraso_tmp += "30x30,";
                }
                if (chk_Teraso40.isChecked()) {
                    //tmp ke DB
                    qty_teraso_tmp += "40x40,";
                }
                if (chk_Teraso60.isChecked()) {
                    //tmp ke DB
                    qty_teraso_tmp += "60x60,";
                }
                if (chk_TerasoLainnya.isChecked()) {
                    ukuran_teraso_lain_tmp += edt_TerasoLainnya.getText().toString();

                    if (TextUtils.isEmpty(ukuran_teraso_lain_tmp)) {
                        edt_TerasoLainnya.setError("Harap Memasukkan Ukuran Lantai");
                        edt_TerasoLainnya.requestFocus();
                        return;
                    }
                }
            }

            //Untuk Granit
            if (chk_Granit.isChecked()) {
                //tmp ke DB
                bangunan_lantai_tmp += "Granit,";

                if (!chk_Granit30.isChecked() && !chk_Granit40.isChecked() && !chk_Granit60.isChecked() &&
                        !chk_GranitLainnya.isChecked()) {
                    chk_Granit30.setError("Harap Memilih Minimal Satu Jenis Ukuran Lantai Bangunan");
                    chk_Granit30.requestFocusFromTouch();
                    return;
                }

                if (chk_Granit30.isChecked()) {
                    //tmp ke DB
                    qty_granit_tmp += "30x30,";
                }
                if (chk_Granit40.isChecked()) {
                    //tmp ke DB
                    qty_granit_tmp += "40x40,";
                }
                if (chk_Granit60.isChecked()) {
                    //tmp ke DB
                    qty_granit_tmp += "60x60,";
                }
                if (chk_GranitLainnya.isChecked()) {
                    ukuran_granit_lain_tmp += edt_GranitLainnya.getText().toString();

                    if (TextUtils.isEmpty(ukuran_granit_lain_tmp)) {
                        edt_GranitLainnya.setError("Harap Memasukkan Ukuran Lantai");
                        edt_GranitLainnya.requestFocus();
                        return;
                    }
                }
            }

            if (chk_LantaiBeton.isChecked()) {
                //tmp ke DB
                bangunan_lantai_tmp += "Lantai Beton,";
            }

            if (chk_SolidParquet.isChecked()) {
                //tmp ke DB
                bangunan_lantai_tmp += "Solid Parquet,";
            }

            if (chk_VinylParquet.isChecked()) {
                //tmp ke DB
                bangunan_lantai_tmp += "Vinyl Parquet,";
            }

            if (chk_LaminatedParquet.isChecked()) {
                //tmp ke DB
                bangunan_lantai_tmp += "Laminated Parquet,";
            }

            if (bangunan_lantai_tmp.isEmpty()) {
                chk_Keramik.setError("Harap Memilih Minimal Satu Jenis Lantai Bangunan");
                chk_Keramik.requestFocusFromTouch();
                return;
            }
        }

        //Simpan ke DB
        lantai_bangunan_DB = bangunan_lantai_tmp;

        qty_keramik_DB = qty_keramik_tmp;
        keramik_lain_DB = ukuran_keramik_lain_tmp;

        qty_granito_DB = qty_granito_tmp;
        granito_lain_DB = ukuran_granito_lain_tmp;

        qty_marmer_DB = qty_marmer_tmp;
        marmer_lain_DB = ukuran_marmer_lain_tmp;

        qty_teraso_DB = qty_teraso_tmp;
        teraso_lain_DB = ukuran_teraso_lain_tmp;

        qty_granit_DB = qty_granit_tmp;
        granit_lain_DB = ukuran_granit_lain_tmp;


        //Bagian 6: Kontrak dan Harga Penawaran dan Catatan
        //Untuk kontrak dan Penawaran Harga
        penawaran_harga_DB = edt_PenawaranHarga.getText().toString();
        total_harga_penawaran_DB = edt_GrandTotal_Termite.getText().toString();
        status = status_kerjasama;
        catatan_tambahan_DB = edt_CatatanTambahan.getText().toString();

        if (penawaran_harga_DB.isEmpty()) {
            edt_PenawaranHarga.setError("Harap Masukkan Penawaran Harga");
            edt_PenawaranHarga.requestFocus();
        }

        if (status.isEmpty()) {
            rdb_Berhasil.setError("Harap Memilih Status Kerjasama");
            rdb_Berhasil.requestFocusFromTouch();
            return;
        }

        // Bagian 7 : TTD dan Keterangan
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

        //Bagian Foto Validasi
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

    private void doIntent(){
            //Menjalankan Fungsi Intent
            Intent Review_Termite_Intent = new Intent(getContext(), ReviewSurveiTermiteControl.class);
            Bundle Extra_Rev_Termite = new Bundle();

            //Bagian 1: Data Diri
            Extra_Rev_Termite.putString(TermiteRev_ID_Client_Termite, IDClient);
            Extra_Rev_Termite.putString(TermiteRev_Nama_Client_Termite, namaPelanggan );
            Extra_Rev_Termite.putString(TermiteRev_Kategori_Client_Termite, kategoriTempatPelanggan);
            Extra_Rev_Termite.putString(TermiteRev_Alamat_Client_Termite, alamatPelanggan);
            Extra_Rev_Termite.putString(TermiteRev_Hp_Client_Termite, hpPelanggan);
            Extra_Rev_Termite.putString(TermiteRev_Email_DB, email_DB);

            //Bagian 2: Jenis Rayap dan Kategori Penanganan
            Extra_Rev_Termite.putString(TermiteRev_Rayap_Client, jenis_rayap);
            Extra_Rev_Termite.putString(TermiteRev_KategoriPenanganan_Termite, kategori_penanganan);

            //Bagian 3: Metode Kendali dan Chemical
            Extra_Rev_Termite.putString(TermiteRev_MetodeKendali_DB, metode_kendali_DB);
            Extra_Rev_Termite.putString(TermiteRev_Chemical_DB, total_chemical_DB);
            Extra_Rev_Termite.putString(TermiteRev_Qty_Fipronil_DB, qty_fipronil_DB);
            Extra_Rev_Termite.putString(TermiteRev_Qty_Imidaclporid_DB, qty_imidaclporid_DB);
            Extra_Rev_Termite.putString(TermiteRev_Qty_Cypermethrin_DB, qty_cypermethrin_DB);
            Extra_Rev_Termite.putString(TermiteRev_Qty_Dichlorphos_DB, qty_dichlorphos_DB);
            Extra_Rev_Termite.putString(TermiteRev_Qty_BaitingAG_DB, qty_baitingAG_DB);
            Extra_Rev_Termite.putString(TermiteRev_Qty_BaitingIG_DB, qty_baitingIG_DB);

            //Bagian 4: Luas Indoor Outdoor
            Extra_Rev_Termite.putString(TermiteRev_Indoor_DB, indoor_DB);
            Extra_Rev_Termite.putString(TermiteRev_Outdoor_DB, outdoor_DB);

            //Bagian 5: Lantai Bangunan (Pasca Konstruksi)
            Extra_Rev_Termite.putString(TermiteRev_LantaiBangunan_DB, lantai_bangunan_DB);

            Extra_Rev_Termite.putString(TermiteRev_Qty_Keramik_DB, qty_keramik_DB);
            Log.e("tag", qty_keramik_DB);
            Extra_Rev_Termite.putString(TermiteRev_Keramik_Lain_DB, keramik_lain_DB);

            Extra_Rev_Termite.putString(TermiteRev_Qty_Granito_DB, qty_granito_DB);
            Extra_Rev_Termite.putString(TermiteRev_Granito_Lain_DB, granito_lain_DB);

            Extra_Rev_Termite.putString(TermiteRev_Qty_Marmer_DB, qty_marmer_DB);
            Extra_Rev_Termite.putString(TermiteRev_Marmer_Lain_DB, marmer_lain_DB);
            Log.e("tag", marmer_lain_DB);

            Extra_Rev_Termite.putString(TermiteRev_Qty_Teraso_DB, qty_teraso_DB);
            Extra_Rev_Termite.putString(TermiteRev_Teraso_Lain_DB, teraso_lain_DB);

            Extra_Rev_Termite.putString(TermiteRev_Qty_Granit_DB, qty_granit_DB);
            Extra_Rev_Termite.putString(TermiteRev_Granit_Lain_DB, granit_lain_DB);

            //Bagian 6: Harga, Status dan Catatan
            Extra_Rev_Termite.putString(TermiteRev_PenawaranHarga_DB, penawaran_harga_DB);
            Extra_Rev_Termite.putString(TermiteRev_TotalHarga_DB, total_harga_penawaran_DB);
            Extra_Rev_Termite.putString(TermiteRev_Status_Termite, status);
            Extra_Rev_Termite.putString(TermiteRev_Catatan_DB, catatan_tambahan_DB);

            //Bagian 7: Keterangan PDF
            Extra_Rev_Termite.putString(TermiteRev_Keterangan_Termite, keterangan);

            //Bagian 8 : LatLong
            Extra_Rev_Termite.putString(TermiteRev_LatLong_Alamat_Pelanggan, latlong_AlamatPelanggan);
            Extra_Rev_Termite.putString(TermiteRev_LatLong_Consultant, latlong_Consultant);

            Review_Termite_Intent.putExtras(Extra_Rev_Termite);
            startActivity(Review_Termite_Intent);
    }
}
