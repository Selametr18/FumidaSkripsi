package com.example.aditmail.fumida.Settings;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class RequestHandler {


    /*
    Metode Untuk mengirim httpPostRequest
    Metode ini mengambil 2 Argumen
    Metode Pertama adalah URL dari Skrip yang digunakan untuk mengirimkan permintaan
    Yang lainnya adalah HashMap dengan nilai pasangan nama yang berisi data yang akan dikirim dengan permintaan
*/
    public String sendPostRequest(String requestURL,
                                  HashMap<String, String> postDataParams) {
        //Membuat URL
        URL url;

        //Objek StringBuilder untuk menyimpan pesan diambil dari server
        StringBuilder sb = new StringBuilder();
        Log.e("INI CUKKKK PARAM", String.valueOf(postDataParams));

        try {
            //Inisialisasi URL
            url = new URL(requestURL);

            //Membuat Koneksi HttpURLConnection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //Konfigurasi koneksi
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);

            //Dikirim dengan post
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            //Membuat Keluaran Stream
            OutputStream os = conn.getOutputStream();

            //Menulis Parameter Untuk Permintaan
            //Kita menggunakan metode getPostDataString yang didefinisikan di bawah ini
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, StandardCharsets.UTF_8));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                sb = new StringBuilder();
                String response;
                //Reading server response
                while ((response = br.readLine()) != null){
                    sb.append(response);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("tag", String.valueOf(e));
        }
        return sb.toString();
    }

    public String sendGetRequest(String requestURL){
        StringBuilder sb =new StringBuilder();
        try {
            URL url = new URL(requestURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String s;
            while((s=bufferedReader.readLine())!=null){
                sb.append(s).append("\n");
            }
        }catch(Exception e){
            Log.e("tag", String.valueOf(e));
        }
        return sb.toString();
    }

    public String sendGetRequestParam(String requestURL, String id_pegawai){
        StringBuilder sb =new StringBuilder();
        try {
            URL url = new URL(requestURL + id_pegawai);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String s;
            while((s=bufferedReader.readLine())!=null){
                sb.append(s).append("\n");
            }
        }catch(Exception e){
            Log.e("tag", String.valueOf(e));
        }
        return sb.toString();
    }

    public String sendGetRequestParam_CariDataPest(String requestURL, String id_pegawai, String cariDataPest, String kategoriCariPest){
        StringBuilder sb =new StringBuilder();
        try {
            URL url = new URL(requestURL + id_pegawai + "&kategoriCariPest=" + kategoriCariPest + "&cariDataPest=" + cariDataPest);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String s;
            while((s=bufferedReader.readLine())!=null){
                sb.append(s+"\n");
            }
        }catch(Exception e){
            Log.e("tag", String.valueOf(e));
        }
        return sb.toString();
    }

    public String sendGetRequestParam_CariDataTermite(String requestURL, String id_pegawai, String cariDataTermite, String kategoriCariTermite){
        StringBuilder sb =new StringBuilder();
        try {
            URL url = new URL(requestURL + id_pegawai + "&kategoriCariTermite=" + kategoriCariTermite + "&cariDataTermite=" + cariDataTermite);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String s;
            while((s=bufferedReader.readLine())!=null){
                sb.append(s+"\n");
            }
        }catch(Exception e){
            Log.e("tag", String.valueOf(e));
        }
        return sb.toString();
    }

    public String sendGetRequestParam_CariDataFumigasi(String requestURL, String id_pegawai, String cariDataFumigasi, String kategoriCariFumigasi){
        StringBuilder sb =new StringBuilder();
        try {
            URL url = new URL(requestURL + id_pegawai + "&kategoriCariFumigasi=" + kategoriCariFumigasi + "&cariDataFumigasi=" + cariDataFumigasi);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String s;
            while((s=bufferedReader.readLine())!=null){
                sb.append(s+"\n");
            }
        }catch(Exception e){
            Log.e("tag", String.valueOf(e));
        }
        return sb.toString();
    }

    public String sendGetRequestParam_CariDataWorkReport(String requestURL, String id_pegawai, String cariDataWorkReport, String kategoriCariWorkReport){
        StringBuilder sb =new StringBuilder();
        try {
            URL url = new URL(requestURL + id_pegawai + "&kategoriCariWorkReport=" + kategoriCariWorkReport + "&cariDataWorkReport=" + cariDataWorkReport);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String s;
            while((s=bufferedReader.readLine())!=null){
                sb.append(s+"\n");
            }
        }catch(Exception e){
            Log.e("tag", String.valueOf(e));
        }
        return sb.toString();
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}
