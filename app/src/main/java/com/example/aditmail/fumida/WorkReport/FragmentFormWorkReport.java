package com.example.aditmail.fumida.WorkReport;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.aditmail.fumida.Activities.OpenMaps;
import com.example.aditmail.fumida.R;
import com.example.aditmail.fumida.Activities.SignatureActivity;
import com.example.aditmail.fumida.Settings.TrackGPS;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class FragmentFormWorkReport extends Fragment implements View.OnClickListener {

    public final static String path_fumida = "com.example.aditmail.fumida";
    public final static String Pesan_Extra = path_fumida + ".Pesan";

    public final static String WorkReport_Rev_JenisPengerjaan = path_fumida + ".JenisPengerjaan_WorkReport";
    public final static String WorkReport_Rev_ID_Client = path_fumida + ".ClientID_WorkReport";
    public final static String WorkReport_Rev_NamaClient = path_fumida + ".NamaClient_WorkReport";
    public final static String WorkReport_Rev_Alamat = path_fumida + ".Alamat_WorkReport";
    public final static String WorkReport_Rev_Pekerjaan = path_fumida + ".Pekerjaan_WorkReport";

    public final static String WorkReport_Rev_JenisHamaPest = path_fumida + ".JenisHamaPest_WorkReport";
    public final static String WorkReport_Rev_HamaLainnyaPest = path_fumida + ".HamaLain_WorkReport";
    public final static String WorkReport_Rev_KategoriPenangananPest = path_fumida + ".PenangananPest_WorkReport";

    //Kuantitas Pest Penanganan
    public final static String WorkReport_Rev_QtyLemTikus = path_fumida + ".QtyLemTikus_WorkReport";
    public final static String WorkReport_Rev_QtyPerangkapTikus = path_fumida + ".QtyPerangkapTikus_WorkReport";
    public final static String WorkReport_Rev_QtyUmpanTikusOutdoor = path_fumida + ".QtyUmpanTikusOutdoor_WorkReport";
    public final static String WorkReport_Rev_QtyUmpanTikusIndoor = path_fumida + ".QtyUmpanTikusIndoor_WorkReport";
    public final static String WorkReport_Rev_QtyPohonLalat = path_fumida + ".QtyPohonLalat_WorkReport";
    public final static String WorkReport_Rev_QtyBlackHole = path_fumida + ".QtyBlackHole_WorkReport";
    public final static String WorkReport_Rev_JenisFumigasi = path_fumida + ".JenisFumigasi_WorkReport";
    public final static String WorkReport_Rev_QtyFlyCathcer = path_fumida + ".QtyFlyCatcher_WorkReport";
    public final static String WorkReport_Rev_MetodeLain = path_fumida + ".MetodeLain_WorkReport";

    public final static String WorkReport_Rev_JenisRayapTermite = path_fumida + ".JenisRayapTermite_WorkReport";
    public final static String WorkReport_Rev_MetodeKendaliRayap = path_fumida + ".MetodeKendaliRayap_WorkReport";
    public final static String WorkReport_Rev_ChemicalTermite = path_fumida + ".ChemicalTermite_WorkReport";

    //Kuantitas Termite Penanganan
    public final static String WorkReport_Rev_QtyFipronil = path_fumida + ".QtyFipronil_WorkReport";
    public final static String WorkReport_Rev_QtyImidaclporid = path_fumida + ".QtyImidaclporid_WorkReport";
    public final static String WorkReport_Rev_QtyCypermethrin = path_fumida + ".QtyCypermethrin_WorkReport";
    public final static String WorkReport_Rev_QtyDichlorphos = path_fumida + ".QtyDichlorphos_WorkReport";
    public final static String WorkReport_Rev_QtyBaitingAG = path_fumida + ".QtyBaitingAG_WorkReport";
    public final static String WorkReport_Rev_QtyBaitingIG = path_fumida + ".QtyBaitingIG_WorkReport";

    public final static String WorkReport_Rev_GasFumigasi = path_fumida + ".GasFumigasi_WorkReport";
    public final static String WorkReport_Rev_Pekerja = path_fumida + ".Pekerja_WorkReport";
    public final static String WorkReport_Rev_WaktuMulai = path_fumida + ".WaktuMulai_WorkReport";
    public final static String WorkReport_Rev_WaktuSelesai = path_fumida + ".WaktuSelesai_WorkReport";
    public final static String WorkReport_Rev_Keterangan = path_fumida + ".Keterangan_WorkReport";

    public final static String WorkReport_Rev_LatLong_Consultant = path_fumida + ".LatLong_Consultant";
    public final static String WorkReport_Rev_LatLong_Alamat_Pelanggan = path_fumida + ".LatLong_AlamatPelanggan";

    protected TimePickerDialog timePickerDialog;

    //-------------- Bagian I : Jenis Pekerjaan --------------
    private RadioGroup rdg_JenisKerja;
    private EditText edt_Client, edt_ClientID, edt_AlamatClient;

    private ImageView imgPinCircle;
    protected Double lat, lng;

    public static String namaPelanggan = "";

    /*FOR GPS*/
    protected String latlong_Consultant = "0,0";
    protected String latlong_AlamatPelanggan = "0,0";
    protected TrackGPS gps;
    double longitude;
    double latitude;
    //-------------- Bagian I : Jenis Pekerjaan --------------

    //-------------- Bagian II : Pekerjaan --------------
    private RadioGroup rdg_Pekerjaan;
    private RadioButton rdb_JenisKerja, rdb_PilihanKerja;
    private String pengerjaan = "", pilihan_kerja = "";

    //-------------- Bagian III : Klasifikasi --------------
    private TableLayout tbl_KategoriPest, tbl_KategoriTermite, tbl_KategoriFumigasi;
    //-------------- Bagian III : Klasifikasi --------------

    //-------------- Bagian IV : Pest Control --------------
    protected TextView img_JenisHama, img_MetodePengendalianHama_Pest;
    protected TableLayout tbl_PestControl;
    private CheckBox chk_Kecoa, chk_Kutu, chk_Lalat, chk_Cicak, chk_Semut, chk_Tikus, chk_Tawon, chk_Nyamuk, chk_HamaLainnya;
    private EditText edt_HamaLainnya;

    protected TableLayout tbl_TreatmentPestControl;
    private EditText edt_QtyPerangkapTikus, edt_QtyLemTikus, edt_QtyUmpanIndoor, edt_QtyUmpanOutdoor, edt_QtyPohonLalat,
            edt_QtyBlackHole, edt_QtyFlyCatcher, edt_MetodeLainnya;
    private Spinner spr_Fumigasi;
    private CheckBox chk_Penyemprotan, chk_Pengembunan, chk_PengasapanThermal, chk_PengasapanMini, chk_PerangkapTikus, chk_LemTikus, chk_UmpanTikusIndoor,
            chk_UmpanTikusOutdoor, chk_UmpanSemut, chk_GelKecoa, chk_LemKecoa, chk_BlackHole, chk_FlyCatcher, chk_PohonLalat, chk_UmpanLalat, chk_MetodeLainnya, chk_Fumigasi;
    //-------------- Bagian IV : Pest Control --------------

    //-------------- Bagian V : Termite Control --------------
    protected TableLayout tbl_TreatmentTermiteControl;
    private Spinner spr_JenisRayap;
    protected CheckBox chk_Spraying, chk_Drill, chk_BaitingAG, chk_BaitingIG, chk_FumigasiTermiteControl;

    protected TextView img_Chemical;
    protected TableLayout tbl_ChemicalTermite;
    private CheckBox chk_Fipronil, chk_Imidaclporid, chk_Cypermethrin, chk_Dichlorphos, chk_ChemicalBaitingAG, chk_ChemicalBaitingIG;
    private EditText edt_QtyFipronil, edt_QtyImidaclporid, edt_QtyCypermethrin, edt_QtyDichlorphos, edt_QtyChemicalBaitingAG, edt_QtyChemicalBaitingIG, edt_nominal, edt_ketbayar;
    //-------------- Bagian V : Termite Control --------------

    //-------------- Bagian VI : Fumigasi Control --------------
    private Spinner spr_GasFumigasi;
    protected TextView img_GasFumigasi;
    //-------------- Bagian VI : Fumigasi Control --------------

    //-------------- Bagian VII : Waktu Mulai --------------
    private EditText edt_Pekerja;
    private EditText edt_WaktuMulai;
    //-------------- Bagian VII : Waktu Mulai --------------

    //-------------- Bagian VIII : Upload Foto --------------
    private Button btn_PilihFotoSatu, btn_PilihFotoDua, btn_PilihFotoTiga, btn_PilihFotoEmpat;

    @SuppressLint("StaticFieldLeak")
    public static ImageView img_FotoSatu, img_FotoDua, img_FotoTiga, img_FotoEmpat;

    private Space spc_Satu, spc_Dua, spc_Tiga;
    //-------------- Bagian VIII : Upload Foto --------------

    //-------------- Bagian IX : TTD --------------
    private Button btn_DialogTTD_Pelanggan, btn_DialogTTD_Consultant;

    @SuppressLint("StaticFieldLeak")
    public static ImageView img_ttdPelanggan, img_ttdConsultant;

    private CheckBox chk_PernyataanPelanggan, chk_PernyataanConsultant;
    //-------------- Bagian IX : TTD --------------

    //Button Simpan
    private Button btn_SimpanWorkReport;

    //Untuk Create View Fragment
    private View view;

    LocationManager locationManager;

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
        view = inflater.inflate(R.layout.fragment_form_work_report, container, false);

        InitData();
        OpenTTD();
        getCurrentLocationRoute();
        ShowTimeAndWork();
        ForHamaClicked();

        ChemicalTermite();
        return view;
    }

    @Override
    public void onStart() {
        OpenPhoto();
        OpenMaps();
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        if (v == btn_SimpanWorkReport) {
            simpan_workReport();
        }
    }

    private void InitData() {
        rdg_JenisKerja = view.findViewById(R.id.RadioGroup_JenisKerja);
        edt_Client = view.findViewById(R.id.editText_Client);
        edt_ClientID = view.findViewById(R.id.editText_ClientID);
        edt_AlamatClient = view.findViewById(R.id.editText_AlamatClient);
        imgPinCircle = view.findViewById(R.id.imgPin);

        //Get Nama Pelanggan
        namaPelanggan = edt_Client.getText().toString();

        rdg_Pekerjaan = view.findViewById(R.id.RadioGroup_Pekerjaan);
        rdb_PilihanKerja = view.findViewById(R.id.radioButton_PestControl);
        rdb_JenisKerja = view.findViewById(R.id.radioButton_Treatment);

        //Jenis Pekerjaan...
        tbl_KategoriPest = view.findViewById(R.id.TableLayout_KategoriPestControl);
        tbl_KategoriTermite = view.findViewById(R.id.TableLayout_KategoriTermite);
        tbl_KategoriFumigasi = view.findViewById(R.id.TableLayout_KategoriFumigasi);

        img_JenisHama = view.findViewById(R.id.imageView_JenisHama);
        img_MetodePengendalianHama_Pest = view.findViewById(R.id.imageView_MetodeTreatment_Pest);

        img_GasFumigasi = view.findViewById(R.id.imageView_GasFumigasi);

        spr_GasFumigasi = view.findViewById(R.id.spinner_GasFumigasi);
        //Bagian Jenis Hama Termite
        spr_JenisRayap = view.findViewById(R.id.spinner_JenisRayap);

        //Bagian Jenis Hama Pest
        tbl_PestControl = view.findViewById(R.id.TableLayout_PestControl);
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

        //Bagian Metode Pengendalian Rayap
        tbl_TreatmentTermiteControl = view.findViewById(R.id.TableLayout_TreatmentTermiteControl);
        chk_Spraying = view.findViewById(R.id.checkBox_Spraying);
        chk_Drill = view.findViewById(R.id.checkBox_InjectDrill);
        chk_BaitingAG = view.findViewById(R.id.checkBox_BaitingAG);
        chk_BaitingIG = view.findViewById(R.id.checkBox_BaitingIG);
        chk_FumigasiTermiteControl = view.findViewById(R.id.checkBox_FumigasiTermite);

        //Bagian Chemical yg Digunakan
        img_Chemical = view.findViewById(R.id.imageView_ChemicalTermite);
        tbl_ChemicalTermite = view.findViewById(R.id.TableLayout_ChemicalTermite);
        chk_Fipronil = view.findViewById(R.id.checkBox_Fipronil);
        chk_Imidaclporid = view.findViewById(R.id.checkBox_Imidaclporid);
        chk_Cypermethrin = view.findViewById(R.id.checkBox_Cypermethrin);
        chk_Dichlorphos = view.findViewById(R.id.checkBox_Dichlorphos);
        chk_ChemicalBaitingAG = view.findViewById(R.id.checkBox_BaitingAG_Chemical);
        chk_ChemicalBaitingIG = view.findViewById(R.id.checkBox_BaitingIG_Chemical);

        edt_QtyFipronil = view.findViewById(R.id.editText_QtyFipronil);
        edt_QtyImidaclporid = view.findViewById(R.id.editText_QtyImidaclporid);
        edt_QtyCypermethrin = view.findViewById(R.id.editText_QtyCypermethrin);
        edt_QtyDichlorphos = view.findViewById(R.id.editText_QtyDichlorphos);
        edt_QtyChemicalBaitingAG = view.findViewById(R.id.editText_QtyBaitingAG);
        edt_QtyChemicalBaitingIG = view.findViewById(R.id.editText_QtyBaitingIG);

        //Bagian Metode Pengendalian
        tbl_TreatmentPestControl = view.findViewById(R.id.TableLayout_TreatmentPestControl);
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

//        edt_nominal = view.findViewById(R.id.konfirmasi_bayar);
//        edt_ketbayar = view.findViewById(R.id.konfirmasi_ketbayar);


        edt_QtyPerangkapTikus = view.findViewById(R.id.editText_QtyPerangkapTikus);
        edt_QtyLemTikus = view.findViewById(R.id.editText_QtyLemTikus);
        edt_QtyUmpanIndoor = view.findViewById(R.id.editText_QtyUmpanIndoor);
        edt_QtyUmpanOutdoor = view.findViewById(R.id.editText_QtyUmpanOutdoor);
        edt_QtyPohonLalat = view.findViewById(R.id.editText_QtyPohonLalat);
        edt_QtyBlackHole = view.findViewById(R.id.editText_QtyBlackHole);
        edt_QtyFlyCatcher = view.findViewById(R.id.editText_QtyFlyCatcher);
        edt_MetodeLainnya = view.findViewById(R.id.editText_MetodeLainnya);
        spr_Fumigasi = view.findViewById(R.id.spinner_Fumigasi);

        edt_Pekerja = view.findViewById(R.id.editText_PersonWorker);
        edt_WaktuMulai = view.findViewById(R.id.editText_WaktuMulai);

        //Button Foto
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

        btn_DialogTTD_Pelanggan = view.findViewById(R.id.button_GetTTD_Pelanggan);
        btn_DialogTTD_Consultant = view.findViewById(R.id.button_GetTTD_Consultant);
        img_ttdPelanggan = view.findViewById(R.id.imageView_TTD_Pelanggan);
        img_ttdConsultant = view.findViewById(R.id.imageView_TTD_Consultant);
        chk_PernyataanPelanggan = view.findViewById(R.id.checkBox_PernyataanCustomer_WorkReport);
        chk_PernyataanConsultant = view.findViewById(R.id.checkBox_PernyataanPestConsultant_WorkReport);

        //Button Simpan
        btn_SimpanWorkReport = view.findViewById(R.id.button_SimpanWorkReport);
        btn_SimpanWorkReport.setOnClickListener(this);

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

    private void OpenTTD() {
        btn_DialogTTD_Pelanggan.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), SignatureActivity.class);
                i.putExtra(Pesan_Extra, "TTD_PelangganWorksReport");
                startActivityForResult(i, REQ_TTD_PELANGGAN);
            }
        });

        btn_DialogTTD_Consultant.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(getContext(), SignatureActivity.class);
                a.putExtra(Pesan_Extra, "TTD_ConsultantWorksReport");
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

    private void OpenMaps() {
        imgPinCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenPlacePicker();
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

            Log.e("tag", latlong_Consultant);
        } else {
            gps.showSettingsAlert();
        }
    }

    private void ShowTimeAndWork() {
        edt_WaktuMulai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog_mulai();
            }
        });

        /*edt_WaktuSelesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog_selesai();
            }
        });*/

        rdg_JenisKerja.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButton_Treatment:
                        pengerjaan = "Treatment";
                        break;
                    case R.id.radioButton_Complaint:
                        pengerjaan = "Complaint";
                        break;
                    case R.id.radioButton_Supervisi:
                        pengerjaan = "Supervisi";
                        break;
                }
            }
        });

        rdg_Pekerjaan.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButton_PestControl:
                        tbl_KategoriPest.setVisibility(View.VISIBLE);
                        tbl_KategoriFumigasi.setVisibility(View.GONE);
                        tbl_KategoriTermite.setVisibility(View.GONE);
                        pilihan_kerja = "Pest Control";
                        break;

                    case R.id.radioButton_TermiteControl:
                        tbl_KategoriPest.setVisibility(View.GONE);
                        tbl_KategoriFumigasi.setVisibility(View.GONE);
                        tbl_KategoriTermite.setVisibility(View.VISIBLE);
                        pilihan_kerja = "Termite Control";
                        break;

                    case R.id.radioButton_Fumigasi:
                        tbl_KategoriPest.setVisibility(View.GONE);
                        tbl_KategoriFumigasi.setVisibility(View.VISIBLE);
                        tbl_KategoriTermite.setVisibility(View.GONE);
                        pilihan_kerja = "Fumigasi";
                        break;
                }
            }
        });
    }

    private void ChemicalTermite() {
        chk_Fipronil.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_QtyFipronil.setVisibility(View.VISIBLE);
                    edt_QtyFipronil.requestFocus();

                } else {
                    edt_QtyFipronil.setVisibility(View.GONE);
                    edt_QtyFipronil.setText("");
                }
            }
        });

        chk_Imidaclporid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_QtyImidaclporid.setVisibility(View.VISIBLE);
                    edt_QtyImidaclporid.requestFocus();
                } else {
                    edt_QtyImidaclporid.setVisibility(View.GONE);
                    edt_QtyImidaclporid.setText("");
                }
            }
        });

        chk_Cypermethrin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_QtyCypermethrin.setVisibility(View.VISIBLE);
                    edt_QtyCypermethrin.requestFocus();
                } else {
                    edt_QtyCypermethrin.setVisibility(View.GONE);
                    edt_QtyCypermethrin.setText("");
                }
            }
        });

        chk_Dichlorphos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_QtyDichlorphos.setVisibility(View.VISIBLE);
                    edt_QtyDichlorphos.requestFocus();
                } else {
                    edt_QtyDichlorphos.setVisibility(View.GONE);
                    edt_QtyDichlorphos.setText("");
                }
            }
        });

        chk_ChemicalBaitingAG.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_QtyChemicalBaitingAG.setVisibility(View.VISIBLE);
                    edt_QtyChemicalBaitingAG.requestFocus();
                } else {
                    edt_QtyChemicalBaitingAG.setVisibility(View.GONE);
                    edt_QtyChemicalBaitingAG.setText("");
                }
            }
        });

        chk_ChemicalBaitingIG.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_QtyChemicalBaitingIG.setVisibility(View.VISIBLE);
                    edt_QtyChemicalBaitingIG.requestFocus();
                } else {
                    edt_QtyChemicalBaitingIG.setVisibility(View.GONE);
                    edt_QtyChemicalBaitingIG.setText("");
                }
            }
        });
    }

    private void ForHamaClicked() {
        chk_Tikus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    img_MetodePengendalianHama_Pest.setVisibility(View.VISIBLE);
                    tbl_TreatmentPestControl.setVisibility(View.VISIBLE);
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
                    img_MetodePengendalianHama_Pest.setVisibility(View.VISIBLE);
                    tbl_TreatmentPestControl.setVisibility(View.VISIBLE);
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
                    img_MetodePengendalianHama_Pest.setVisibility(View.VISIBLE);
                    tbl_TreatmentPestControl.setVisibility(View.VISIBLE);
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
                    img_MetodePengendalianHama_Pest.setVisibility(View.VISIBLE);
                    tbl_TreatmentPestControl.setVisibility(View.VISIBLE);
                    show_Nyamuk();
                } else if (chk_Lalat.isChecked()) {
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
                    img_MetodePengendalianHama_Pest.setVisibility(View.VISIBLE);
                    tbl_TreatmentPestControl.setVisibility(View.VISIBLE);
                    show_Lalat();
                } else if (chk_Nyamuk.isChecked()) {
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
                    img_MetodePengendalianHama_Pest.setVisibility(View.VISIBLE);
                    tbl_TreatmentPestControl.setVisibility(View.VISIBLE);
                    show_Kutu();
                } else if (chk_Semut.isChecked() || chk_Kecoa.isChecked() || chk_Tawon.isChecked()) {
                    chk_Penyemprotan.setVisibility(View.VISIBLE);
                } else {
                    hide_Kutu();
                }
            }
        });

        chk_Tawon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    img_MetodePengendalianHama_Pest.setVisibility(View.VISIBLE);
                    tbl_TreatmentPestControl.setVisibility(View.VISIBLE);
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
                    img_MetodePengendalianHama_Pest.setVisibility(View.VISIBLE);
                    tbl_TreatmentPestControl.setVisibility(View.VISIBLE);
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
                    img_MetodePengendalianHama_Pest.setVisibility(View.VISIBLE);
                    tbl_TreatmentPestControl.setVisibility(View.VISIBLE);
                    edt_HamaLainnya.setVisibility(View.VISIBLE);
                    show_Lainnya();
                } else {
                    edt_HamaLainnya.setVisibility(View.GONE);
                    //hide_Lainnya();
                }
            }
        });

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

    private void showPictureDialog_Satu() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
        pictureDialog.setTitle("Pilih Aksi");

        String[] pictureDialogItems = {
                "Pilih Foto dari Galeri", "Ambil Foto dari Kamera"};

        pictureDialog.setItems(pictureDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        if (Build.VERSION.SDK_INT >= 23) {
                            isStoragePermissionGranted();
                        } else {
                            openGaleri();
                        }
                        break;

                    case 1:
                        if (Build.VERSION.SDK_INT >= 23) {
                            if (Objects.requireNonNull(getActivity()).checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                openKamera();
                            } else {
                                String[] permissionRequest = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                                requestPermissions(permissionRequest, 99);
                            }
                        } else {
                            openKamera();
                        }
                        break;
                }
            }
        });
        pictureDialog.show();
    }

    private void showPictureDialog_Dua() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
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
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
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
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
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

    public void isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (Objects.requireNonNull(getActivity()).checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    Objects.requireNonNull(getContext()).checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                openGaleri();
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 4);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissons, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissons, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGaleri();
        } else {
            Toast.makeText(getContext(), "Aplikasi tidak diberikan izin untuk menyimpan data kedalam penyimpanan. Sehingga aplikasi tidak dapat berjalan normal." +
                    "Mohon pertimbangkan untuk memberikan Izin akses.", Toast.LENGTH_LONG).show();
        }

        if (requestCode == 3) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                openKamera();
            } else {
                Toast.makeText(getContext(), "Aplikasi tidak diberikan izin untuk menyimpan data kedalam penyimpanan. Sehingga aplikasi tidak dapat berjalan normal." +
                        "Mohon pertimbangkan untuk memberikan Izin akses.", Toast.LENGTH_LONG).show();
            }
        }
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

        //Untuk ttd Pelanggan dan Consultant
        if (requestCode == REQ_TTD_PELANGGAN) {
            if (resultCode == Activity.RESULT_OK) {
                //Nampilin hasil ttd untuk Pest Pelanggan
                String image_path = data.getStringExtra("imagePath_WorkReportPelanggan");
                Bitmap bitmap = BitmapFactory.decodeFile(image_path);
                img_ttdPelanggan.setImageBitmap(bitmap);
            }
        }

        if (requestCode == REQ_TTD_CONSULTANT) {
            if (resultCode == Activity.RESULT_OK) {
                String imagepath_PestConsultant = data.getStringExtra("imagePath_WorkReportConsultant");
                Bitmap bitmap_PestConsultant = BitmapFactory.decodeFile(imagepath_PestConsultant);
                img_ttdConsultant.setImageBitmap(bitmap_PestConsultant);
            }
        }

        //Untuk Picture atau Kamera
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
            if(resultCode == Activity.RESULT_OK) {
                String lokasi = data.getStringExtra("Alamat_Lokasi");
                edt_AlamatClient.setText(lokasi);

                latlong_AlamatPelanggan = data.getStringExtra("LatLong_Lokasi");
                Log.e("tag", latlong_AlamatPelanggan);
            }
        }
    }

    private void showTimeDialog_mulai() {

        Calendar calendar = Calendar.getInstance();
        timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                edt_WaktuMulai.setText(String.format("%02d:%02d", hourOfDay, minute));
            }
        },
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(getContext()));

        timePickerDialog.show();
    }

    private void simpan_workReport() {
        //Bagian I - Jenis Pengerjaan
        final String jenis_pengerjaan = pengerjaan;
        final String clientID_WorkReport = edt_ClientID.getText().toString().toUpperCase();
        final String namaClient_WorkReport = edt_Client.getText().toString();
        final String alamat_WorkReport = edt_AlamatClient.getText().toString();

        if (jenis_pengerjaan.isEmpty()) {
            rdb_JenisKerja.setError("Harap Memilih Jenis Pengerjaan pada Work Report");
            rdb_JenisKerja.requestFocusFromTouch();
            return;
        }

        if (clientID_WorkReport.isEmpty()) {
            edt_ClientID.setError("Mohon Memasukkan ID Client");
            edt_ClientID.requestFocus();
            return;
        }

        if (namaClient_WorkReport.isEmpty()) {
            edt_Client.setError("Mohon Memasukkan Nama Client");
            edt_Client.requestFocus();
            return;
        }

        if (alamat_WorkReport.isEmpty()) {
            edt_AlamatClient.setError("Harap Memasukkan Alamat");
            edt_AlamatClient.requestFocus();
            return;
        }

        //Bagian II - Pekerjaan
        final String pekerjaan_workReport = pilihan_kerja;

        if (pekerjaan_workReport.isEmpty()) {
            rdb_PilihanKerja.setError("Harap Memilih Jenis Pekerjaan pada Work Report");
            rdb_PilihanKerja.requestFocusFromTouch();
            return;
        }

        //<-- Bagian Pest Control -->
        //Temporary before DB
        String jenis_hama_tmp = "";
        String hama_lainnya_tmp = "";
        String metode_kendali_tmp = "";

        //For Quantity
        String qty_lemTikus_tmp = "";
        String qty_perangkapTikus_tmp = "";
        String qty_umpanTikus_tmp = "";
        String qty_umpanTikusIndoor_tmp = "";
        String qty_pohonLalat_tmp = "";
        String qty_blackHole_tmp = "";
        String jenis_fumigasi_tmp = "";
        String qty_FlyCatcher_tmp = "";
        String metodeLain_tmp = "";

        if (pekerjaan_workReport.equals("Pest Control")) {
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
                    edt_HamaLainnya.setError(("Harap Masukkan Hama Lainnya"));
                    edt_HamaLainnya.requestFocus();
                    return;
                }

                jenis_hama_tmp += "Lainnya,";
            }

            if (jenis_hama_tmp.isEmpty()) {
                chk_Kecoa.setError("Harap Memilih Minimal Satu Jenis Hama yang Dikendalikan");
                chk_Kecoa.requestFocusFromTouch();
                return;
            }

            //Untuk Metode Kendali Hama
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
                qty_lemTikus_tmp = edt_QtyLemTikus.getText().toString();

                if (TextUtils.isEmpty(qty_lemTikus_tmp)) {
                    edt_QtyLemTikus.setError(("Harap Masukkan Banyak Perangkap yang Dibutuhkan"));
                    edt_QtyLemTikus.requestFocus();
                    return;
                }
                metode_kendali_tmp += "Lem Tikus (Rodent Glue Trap),";
            }

            if (chk_PerangkapTikus.isChecked()) {
                qty_perangkapTikus_tmp = edt_QtyPerangkapTikus.getText().toString();

                if (TextUtils.isEmpty(qty_perangkapTikus_tmp)) {
                    edt_QtyPerangkapTikus.setError(("Harap Masukkan Banyak Perangkap yang Dibutuhkan"));
                    edt_QtyPerangkapTikus.requestFocus();
                    return;
                }
                metode_kendali_tmp += "Perangkap Tikus Massal (Rodent Trap),";
            }

            if (chk_UmpanTikusOutdoor.isChecked()) {
                qty_umpanTikus_tmp = edt_QtyUmpanOutdoor.getText().toString();

                if (TextUtils.isEmpty(qty_umpanTikus_tmp)) {
                    edt_QtyUmpanOutdoor.setError(("Harap Masukkan Banyak Perangkap yang Dibutuhkan"));
                    edt_QtyUmpanOutdoor.requestFocus();
                    return;
                }
                metode_kendali_tmp += "Umpan Tikus (Rodent Baiting Outdoor),";
            }

            if (chk_UmpanTikusIndoor.isChecked()) {
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
        }

        //Simpan Ke DB
        final String jenis_hama_final = jenis_hama_tmp;
        final String hama_lain_final = hama_lainnya_tmp;
        final String metode_kendali_hama_final = metode_kendali_tmp;

        final String qty_lemTikus_final = qty_lemTikus_tmp;
        final String qty_perangkapTikus_final = qty_perangkapTikus_tmp;
        final String qty_umpanTikus_final = qty_umpanTikus_tmp;
        final String qty_umpanTikusIndoor_final = qty_umpanTikusIndoor_tmp;
        final String qty_pohonLalat_final = qty_pohonLalat_tmp;
        final String qty_blackhole_final = qty_blackHole_tmp;
        final String jenis_fumigasi_final = jenis_fumigasi_tmp;
        final String qty_flyCatcher_final = qty_FlyCatcher_tmp;
        final String metodeLain_final = metodeLain_tmp;

        //<-- Bagian Pest Control -->


        //<-- Bagian Termite Control -->
        String jenis_rayap_tmp = "";
        String metode_kendali_rayap_tmp = "";
        String jenis_chemical_rayap_tmp = "";

        String qty_fipronil_tmp = "";
        String qty_imidaclporid_tmp = "";
        String qty_cypermethrin_tmp = "";
        String qty_dichlorphos_tmp = "";
        String qty_baitingAG_tmp = "";
        String qty_baitingIG_tmp = "";

        if (pilihan_kerja.equals("Termite Control")) {
            //Untuk pilih Rayap
            //Save to DB
            jenis_rayap_tmp = spr_JenisRayap.getSelectedItem().toString();

            //Untuk pilih metode kendali Rayap
            if (chk_Spraying.isChecked()) {
                metode_kendali_rayap_tmp += "Spraying,";
            }

            if (chk_Drill.isChecked()) {
                metode_kendali_rayap_tmp += "Drill Inject,";
            }

            if (chk_BaitingAG.isChecked()) {
                metode_kendali_rayap_tmp += "Termite Baiting AG,";
            }

            if (chk_BaitingIG.isChecked()) {
                metode_kendali_rayap_tmp += "Termite Baiting IG,";
            }

            if (chk_FumigasiTermiteControl.isChecked()) {
                metode_kendali_rayap_tmp += "Fumigasi,";
            }

            if (metode_kendali_rayap_tmp.isEmpty()) {
                chk_Spraying.setError("Harap Memilih Minimal Satu Jenis Metode Kendali Hama Rayap");
                chk_Spraying.requestFocusFromTouch();
                return;
            }

            //Untuk Chemical
            if (chk_Fipronil.isChecked()) {
                qty_fipronil_tmp = edt_QtyFipronil.getText().toString();

                if (TextUtils.isEmpty(qty_fipronil_tmp)) {
                    edt_QtyFipronil.setError("Harap Masukkan Banyak Botol yang Dibutuhkan");
                    edt_QtyFipronil.requestFocus();
                    return;
                }
                jenis_chemical_rayap_tmp += "Fipronil 50 g/l,";
            }

            if (chk_Imidaclporid.isChecked()) {
                qty_imidaclporid_tmp = edt_QtyImidaclporid.getText().toString();

                if (TextUtils.isEmpty(qty_imidaclporid_tmp)) {
                    edt_QtyImidaclporid.setError("Harap Masukkan Banyak Botol yang Dibutuhkan");
                    edt_QtyImidaclporid.requestFocus();
                    return;
                }
                jenis_chemical_rayap_tmp += "Imidacloprid 200 g/l,";
            }

            if (chk_Cypermethrin.isChecked()) {
                qty_cypermethrin_tmp = edt_QtyCypermethrin.getText().toString();

                if (TextUtils.isEmpty(qty_cypermethrin_tmp)) {
                    edt_QtyCypermethrin.setError("Harap Masukkan Banyak Botol yang Dibutuhkan");
                    edt_QtyCypermethrin.requestFocus();
                    return;
                }
                jenis_chemical_rayap_tmp += "Cypermethrin 100 g/l,";
            }

            if (chk_Dichlorphos.isChecked()) {
                qty_dichlorphos_tmp = edt_QtyDichlorphos.getText().toString();

                if (TextUtils.isEmpty(qty_dichlorphos_tmp)) {
                    edt_QtyDichlorphos.setError("Harap Masukkan Banyak Botol yang Dibutuhkan");
                    edt_QtyDichlorphos.requestFocus();
                    return;
                }
                jenis_chemical_rayap_tmp += "Dichlorphos 200 g/l,";
            }

            if (chk_ChemicalBaitingAG.isChecked()) {
                qty_baitingAG_tmp = edt_QtyChemicalBaitingAG.getText().toString();

                if (TextUtils.isEmpty(qty_baitingAG_tmp)) {
                    edt_QtyChemicalBaitingAG.setError("Harap Masukkan Banyak Unit yang Dibutuhkan");
                    edt_QtyChemicalBaitingAG.requestFocus();
                    return;
                }
                jenis_chemical_rayap_tmp += "Baiting AG,";
            }

            if (chk_ChemicalBaitingIG.isChecked()) {
                qty_baitingIG_tmp = edt_QtyChemicalBaitingIG.getText().toString();

                if (TextUtils.isEmpty(qty_baitingIG_tmp)) {
                    edt_QtyChemicalBaitingIG.setError("Harap Masukkan Banyak Unit yang Dibutuhkan");
                    edt_QtyChemicalBaitingIG.requestFocus();
                    return;
                }
                jenis_chemical_rayap_tmp += "Baiting IG,";
            }

            if (jenis_chemical_rayap_tmp.isEmpty()) {
                chk_Fipronil.setError("Harap Memilih Minimal Satu Jenis Chemical");
                chk_Fipronil.requestFocusFromTouch();
                return;
            }
        }

        //Untuk simpan Nilai Jenis Rayap, Metode Kendali dan Chemical - Termite Control

        //Save to DB
        final String jenis_rayap_final = jenis_rayap_tmp;
        final String metode_kendali_rayap_final = metode_kendali_rayap_tmp;
        final String jenis_chemical_final = jenis_chemical_rayap_tmp;

        final String qty_fipronil_final = qty_fipronil_tmp;
        final String qty_imidaclporid_final = qty_imidaclporid_tmp;
        final String qty_cypermethrin_final = qty_cypermethrin_tmp;
        final String qty_dichlorphos_final = qty_dichlorphos_tmp;
        final String qty_baitingAG_final = qty_baitingAG_tmp;
        final String qty_baitingIG_final = qty_baitingIG_tmp;

        //<-- Bagian Termite Control -->

        //<-- Bagian Fumigasi -->
        String fumigasi_tmp = "";
        if (pilihan_kerja.equals("Fumigasi")) {
            fumigasi_tmp = spr_GasFumigasi.getSelectedItem().toString();
        }

        //Untuk simpan Nilai Fumigasi - Fumigasi
        final String final_fumigasi = fumigasi_tmp;

        //untuk Nama Pekerja
        final String pekerja = edt_Pekerja.getText().toString();
        if (pekerja.isEmpty()) {
            edt_Pekerja.setError("Harap Masukkan Nama Pekerja");
            edt_Pekerja.requestFocus();
            return;
        }

        //Untuk Bagian Waktu Pengerjaan
        String str_WaktuSelesai = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        final String waktu_mulai = edt_WaktuMulai.getText().toString();
        final String waktu_selesai = str_WaktuSelesai;

        if (waktu_mulai.isEmpty()) {
            edt_WaktuMulai.setError("Harap Memasukkan Waktu Mulai");
            edt_WaktuMulai.requestFocus();
            return;
        }

        /*if (waktu_selesai.isEmpty()){
            edt_WaktuSelesai.setError("Harap Memasukkan Waktu Selesai");
            edt_WaktuSelesai.requestFocus();
            return;
        }*/

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

        //Untuk Tanda Tangan
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

        String dateKeterangan = new SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault()).format(new Date());
        final String keterangan = "*Berkas ini Dibuat Secara Otomatis oleh Sistem Mobile Apps Fumida pada Tanggal " + dateKeterangan;


        //Menampilkan dialog Alert
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());

        builder.setCancelable(true);
        builder.setTitle("Apakah Anda Yakin Data Yang Dimasukkan Telah Benar dan Tepat?");
        builder.setMessage("Apabila Yakin Tekan 'OK'!");

        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Menjalankan Fungsi Intent
                Intent Review_WorkReport_Intent = new Intent(getContext(), ReviewWorkReport.class);
                Bundle Extra_Rev_WorkReport = new Bundle();
                Extra_Rev_WorkReport.putString(WorkReport_Rev_JenisPengerjaan, jenis_pengerjaan);
                Extra_Rev_WorkReport.putString(WorkReport_Rev_ID_Client, clientID_WorkReport);
                Extra_Rev_WorkReport.putString(WorkReport_Rev_NamaClient, namaClient_WorkReport);
                Extra_Rev_WorkReport.putString(WorkReport_Rev_Alamat, alamat_WorkReport);
                Extra_Rev_WorkReport.putString(WorkReport_Rev_Pekerjaan, pekerjaan_workReport);

                //Pest Control
                Extra_Rev_WorkReport.putString(WorkReport_Rev_JenisHamaPest, jenis_hama_final);
                Extra_Rev_WorkReport.putString(WorkReport_Rev_HamaLainnyaPest, hama_lain_final);
                Extra_Rev_WorkReport.putString(WorkReport_Rev_KategoriPenangananPest, metode_kendali_hama_final);

                Extra_Rev_WorkReport.putString(WorkReport_Rev_QtyLemTikus, qty_lemTikus_final);
                Extra_Rev_WorkReport.putString(WorkReport_Rev_QtyPerangkapTikus, qty_perangkapTikus_final);
                Extra_Rev_WorkReport.putString(WorkReport_Rev_QtyUmpanTikusOutdoor, qty_umpanTikus_final);
                Extra_Rev_WorkReport.putString(WorkReport_Rev_QtyUmpanTikusIndoor, qty_umpanTikusIndoor_final);
                Extra_Rev_WorkReport.putString(WorkReport_Rev_QtyPohonLalat, qty_pohonLalat_final);
                Extra_Rev_WorkReport.putString(WorkReport_Rev_QtyBlackHole, qty_blackhole_final);
                Extra_Rev_WorkReport.putString(WorkReport_Rev_JenisFumigasi, jenis_fumigasi_final);
                Extra_Rev_WorkReport.putString(WorkReport_Rev_QtyFlyCathcer, qty_flyCatcher_final);
                Extra_Rev_WorkReport.putString(WorkReport_Rev_MetodeLain, metodeLain_final);
                //Pest Control

                //Termite Control
                Extra_Rev_WorkReport.putString(WorkReport_Rev_JenisRayapTermite, jenis_rayap_final);
                Extra_Rev_WorkReport.putString(WorkReport_Rev_MetodeKendaliRayap, metode_kendali_rayap_final);
                Extra_Rev_WorkReport.putString(WorkReport_Rev_ChemicalTermite, jenis_chemical_final);

                Extra_Rev_WorkReport.putString(WorkReport_Rev_QtyFipronil, qty_fipronil_final);
                Extra_Rev_WorkReport.putString(WorkReport_Rev_QtyImidaclporid, qty_imidaclporid_final);
                Extra_Rev_WorkReport.putString(WorkReport_Rev_QtyCypermethrin, qty_cypermethrin_final);
                Extra_Rev_WorkReport.putString(WorkReport_Rev_QtyDichlorphos, qty_dichlorphos_final);
                Extra_Rev_WorkReport.putString(WorkReport_Rev_QtyBaitingAG, qty_baitingAG_final);
                Extra_Rev_WorkReport.putString(WorkReport_Rev_QtyBaitingIG, qty_baitingIG_final);
                //Termite Control

                Extra_Rev_WorkReport.putString(WorkReport_Rev_GasFumigasi, final_fumigasi);
                Extra_Rev_WorkReport.putString(WorkReport_Rev_Pekerja, pekerja);
                Extra_Rev_WorkReport.putString(WorkReport_Rev_WaktuMulai, waktu_mulai);
                Extra_Rev_WorkReport.putString(WorkReport_Rev_WaktuSelesai, waktu_selesai);
                Extra_Rev_WorkReport.putString(WorkReport_Rev_Keterangan, keterangan);

                //Bagian IX: Latlong...
                Extra_Rev_WorkReport.putString(WorkReport_Rev_LatLong_Alamat_Pelanggan, latlong_AlamatPelanggan);
                Extra_Rev_WorkReport.putString(WorkReport_Rev_LatLong_Consultant, latlong_Consultant);

                Review_WorkReport_Intent.putExtras(Extra_Rev_WorkReport);
                startActivity(Review_WorkReport_Intent);
            }
        });
        builder.show();
    }


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
        chk_MetodeLainnya.setChecked(false);
        chk_MetodeLainnya.setVisibility(View.GONE);

        chk_PerangkapTikus.setChecked(false);
        chk_LemTikus.setChecked(false);
        chk_UmpanTikusIndoor.setChecked(false);
        chk_UmpanTikusOutdoor.setChecked(false);
        chk_Fumigasi.setChecked(false);
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
        chk_GelKecoa.setChecked(false);
        chk_LemKecoa.setChecked(false);
        chk_Fumigasi.setChecked(false);
        chk_MetodeLainnya.setChecked(false);
        chk_MetodeLainnya.setVisibility(View.GONE);
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
        chk_MetodeLainnya.setChecked(false);
        chk_MetodeLainnya.setVisibility(View.GONE);

        chk_UmpanSemut.setChecked(false);
        chk_Fumigasi.setChecked(false);
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
        chk_MetodeLainnya.setChecked(false);
        chk_MetodeLainnya.setVisibility(View.GONE);

        chk_BlackHole.setChecked(false);
        chk_Fumigasi.setChecked(false);

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
        chk_MetodeLainnya.setChecked(false);
        chk_MetodeLainnya.setVisibility(View.GONE);

        chk_Fumigasi.setChecked(false);
        chk_PohonLalat.setChecked(false);
        chk_FlyCatcher.setChecked(false);
        chk_UmpanLalat.setChecked(false);
    }

    protected void show_Kutu() {
        chk_Penyemprotan.setVisibility(View.VISIBLE);
        chk_Fumigasi.setVisibility(View.VISIBLE);
        chk_MetodeLainnya.setVisibility(View.VISIBLE);
    }

    protected void hide_Kutu() {
        chk_Penyemprotan.setVisibility(View.GONE);
        chk_Fumigasi.setVisibility(View.GONE);
        chk_MetodeLainnya.setChecked(false);
        chk_MetodeLainnya.setVisibility(View.GONE);
        chk_Fumigasi.setChecked(false);
    }

    protected void show_Tawon() {
        chk_Penyemprotan.setVisibility(View.VISIBLE);
        chk_Fumigasi.setVisibility(View.VISIBLE);
        chk_MetodeLainnya.setVisibility(View.VISIBLE);
    }

    protected void hide_Tawon() {
        chk_Penyemprotan.setVisibility(View.GONE);
        chk_Fumigasi.setVisibility(View.GONE);
        chk_MetodeLainnya.setChecked(false);
        chk_MetodeLainnya.setVisibility(View.GONE);

        chk_Fumigasi.setChecked(false);
    }

    protected void show_Cicak() {
        chk_Fumigasi.setVisibility(View.VISIBLE);
        chk_MetodeLainnya.setVisibility(View.VISIBLE);
    }

    protected void hide_Cicak() {
        chk_Fumigasi.setVisibility(View.GONE);
        chk_MetodeLainnya.setChecked(false);
        chk_MetodeLainnya.setVisibility(View.GONE);
        chk_Fumigasi.setChecked(false);
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


}

