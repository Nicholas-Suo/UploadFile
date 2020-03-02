package tech.bb.com.uploadfile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.CipherSuite;
import com.squareup.okhttp.ConnectionSpec;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.TlsVersion;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity {

    File testADir = new File(Environment.getExternalStorageDirectory(),"testA");
    File testBDir = new File(Environment.getExternalStorageDirectory(),"testB");
    private static final String TAG = "MainActivity";

    private static final String URL ="https://helix-flexdev-device-file-drop.s3.dualstack.us-west-2.amazonaws.com/WGFLXD-EDMDDLZE/diagpkg_20200228_084725_354115100004231_user.tar.gz?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=ASIATVJ5DCCLZVZP7PMV%2F20200228%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20200228T084803Z&X-Amz-Expires=604800&X-Amz-Security-Token=IQoJb3JpZ2luX2VjEAkaCXVzLXdlc3QtMiJGMEQCIA2Mi5N16hxNKWPQyNS3xMpAAGCSJHwqo1KpTMkPRryPAiAVvVDegFaSWElAoTuw%2FJOrYFGrR7YrD5RhruFEcTQTOSrpAQjS%2F%2F%2F%2F%2F%2F%2F%2F%2F%2F8BEAEaDDI1MTkyMDU4NDg1NSIMc000e5cBh2dk2T5uKr0Bz9eUu6sm8me0cJ7P46gZ4MvfybL%2Fj%2Bth9ROWfEuFMi10j5q4wTq9VZuVlKNdXWdZndyNUjCRXL%2BltJUSHsa8YRvtwa%2BTVgvKCk3NNEhBCE%2FMD6RWwYf1nrF6X4UbGgDr1tFVnUmkedo3t%2BCBXS4bT%2Fx0cgXgiD2wlPSduzPZcFmUuC%2BzkH3YLsPdkWa%2FTlKo%2Fq702kXRslw6%2BD%2BeQLTaGeloLmrcYp0hzDTpmPnZl5Ft09tVEforNfXDZufWMMGn4%2FIFOuEBVUPTBsjHmA1fKx5PgEBvm2G4vi6DxHFckP0nUFCv5dy1nH5QtJdgH9f%2B%2FdJLo5TFbGiMliUtLGbbK0EdUReUXuSo4EK%2BoYKDO5%2Fn2PAW0vOHtJc16Xf%2FfUlMZ3WpGmqggUnIqR%2FCtS90NQoiek9QzjcMS5DUpu5JWVc5xe1QvWntzeAhVj3cTp9NlTPu191tIt7jSFolTR3DCRxfUBvtWcL9DdkwbgdikjmZc47XS6nWp1MlenBkvHRXUrf0LlO9xlVcMbjxEypQjI5FzsntGOUDrbaTGzz8HdTyx44Z7agu&X-Amz-Signature=51e3cda87894720d41852a852ad9f32ca1e372c07f298a59e61501693ec0e036&X-Amz-SignedHeaders=host%3Bx-amz-acl&x-amz-acl=private";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button upload = findViewById(R.id.upload_button);

        View popView = LayoutInflater.from(this).inflate(R.layout.popupwindow,null);
        PopupWindow popupWindow = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        upload.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
              //  requestPermissions(new String[]{""},1);
                //popupWindow.showAsDropDown(upload);
                if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                   requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"},1);
                }else{
                      uploadLogToServer(URL);
                }

               // uploadLogToServer("https://www.baidu.com");
            }
        });

        Button copyDirFilesButton = findViewById(R.id.copy_dir_files);
        copyDirFilesButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"},2);
                }else{
                    copyDirTest(testADir,testBDir);
                }
            }
        });
    }
 /*   String
    one of MEDIA_UNKNOWN, MEDIA_REMOVED, MEDIA_UNMOUNTED, MEDIA_CHECKING, MEDIA_NOFS, MEDIA_MOUNTED, MEDIA_MOUNTED_READ_ONLY, MEDIA_SHARED, MEDIA_BAD_REMOVAL, or MEDIA_UNMOUNTABLE.*/
    private void uploadLogToServer(String url){
      //  url = "https://helix-flexdev-device-file-drop.s3.dualstack.us-west-2.amazonaws.com/WGFLXD-EDMDDLZE/diagpkg_20200227_042334_354115100004231_user.tar.gz?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=ASIATVJ5DCCL34NWCQBZ%2F20200227%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20200227T035511Z&X-Amz-Expires=604800&X-Amz-Security-Token=IQoJb3JpZ2luX2VjEOr%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FwEaCXVzLXdlc3QtMiJIMEYCIQCAnTxGA5lu1O8Z%2FFUndHFEUNJ%2FGwvLYnHCfXUf28dkCgIhANPXqLXZeUfoZ%2Ffls%2Fu1LoTtJ5n8GONC%2BskG%2Bn2JQRZFKuABCLP%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FwEQARoMMjUxOTIwNTg0ODU1IgwQQ4y%2F%2B30pmMixmesqtAF%2B7WXK6ZA3R6Lzfo9h5vgE%2BB7AbC5FfnbioBLOnWMurd1D0ybpK31%2FlRZO6%2FBtQ%2BpdcL2IOgqZlDE%2BZO0SaLxtA63L7pkGp7yrVMTV4iEpp7rLHhcWqRg%2BT30GIGPutaOQSn%2FLHStEhUpKivBNyvgV9GVkGxcCokqeThCRsc9S%2FGxvosif6iojgdnRYR%2FD4UfyCEsUhA7i2ta0vzVpy6jRxHE%2B41qrdZFJuR8Mt7li24p0%2F8kw5cnc8gU63wHyhm6lf99nVyCq5vguOu8wWhnLr2VvE%2FQ96LKhF9NkZaI8wZf6VrTzeyBFQXLZdG9uKG4yJN9TY4mQXrwROIPjHsOeU4sr%2Fyk0GV5gfzD8CRzxB%2BG9aGQtU30PgN%2F0xpaGIUREv9rl%2Bjx3TUT3oc3FWh9nFzOAByID9no2axEPgq1mmP57soz1jjYa%2FUg9N3Cpr6CbM0iGc3wnhfQZeL3uJUO8V9SBiorsEH7svExMjo9UtHZWZ9E3p8GjBHNciEa9xPVurCNjjVzrY60rSn1nOIed4gZvV5xU9G9kUyt7&X-Amz-Signature=d0f65bb339e69d3fad84182ace3ecfc7d54705b32d2397d2a3096d26ce1b7ad6&X-Amz-SignedHeaders=host%3Bx-amz-acl&x-amz-acl=private";

        Log.d(TAG,"the url is " + url);
        String storageState = Environment.getExternalStorageState();
        Log.d(TAG,"the storageState is " + storageState);
        File storageDir = Environment.getExternalStorageDirectory();
        String path = storageDir.toString();
        String absolutePath = storageDir.getAbsolutePath();
        Log.d(TAG," the storage path is: " + path + "  absolutePath: " + absolutePath);
        File logFile = new File(storageDir,"logcat2.rar");
        if(logFile.exists()){
            Log.d(TAG," the logcat.rar is exist,name is: " + logFile.getName() + " size is: " + logFile.length());
            uploadFile(logFile,url);
        }else{
            Log.d(TAG," read logcat file fail");
        }

    }
//           https://helix-flexdev-device-file-drop.s3.dualstack.us-west-2.amazonaws.com/WGFLXD-EDMDDLZE/diagpkg_20200227_034152_354115100004231_user.tar.gz?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Cred
    //       https://helix-flexdev-device-file-drop.s3.dualstack.us-west-2.amazonaws.com/WGFLXD-EDMDDLZE/diagpkg_20200227_035439_354115100004231_user.tar.gz?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Am
// UPLOAD_URL=https://helix-flexdev-device-file-drop.s3.dualstack.us-west-2.amazonaws.com/WGFLXD-EDMDDLZE/diagpkg_20200227_042334_354115100004231_user.tar.gz?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=ASIATVJ5DCCLWJM5RHGU%2F20200227%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20200227T043220Z&X-Amz-Expires=604800&X-Amz-Security-Token=IQoJb3JpZ2luX2VjEOz%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FwEaCXVzLXdlc3QtMiJIMEYCIQCHX4V1lUcl8SvGncWYXjSsu2Asz9hjcJ3tiVERsz8cJwIhAKICG7bHAnc98r2aJbu%2FP1Te9WGiwjUHgCVOm2JIAe26KuABCLX%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FwEQARoMMjUxOTIwNTg0ODU1Igyd%2FzyuJeNg3Y2VDugqtAF8Z6NbEUE3sOqOoyginElv1yXH%2B0ZU7DPC0nQ3r4Zot7zbn%2BPhM7p8EffSmWBg%2BR0EnISsDomUWsy9nMoAwGfHsDJgWl0Kimk2%2F96JkOAiGEtE%2FCp7wN9k3hyUx4bot%2B3uaTXGV9b%2BZynakz6hKlM9X%2FyQXctafBmCvSh4W%2BCPLexQFv3%2FbtPDxgzbb4GPS4CuYdR8YWL3tENPxcA%2FNFGEAZW6vnzqeY9uKqkUzeMXJItKLsEw5u7c8gU63wEtz%2Bw3MKdT8ekc01ljzKRPjKeiEvWsBSu%2Bdd5tHXFjr9PATALehbqWUaol6GmtMoowPcVg90hOhr%2FYjlgCec9Tu5ZARfcgHKpAjOBdzFFLuE8vuRXkM6tCTyWBzdjPRLfgJIhLW%2FMYm64wJfxeKHJaAHjpdGbbsXxW0XXB%2F5SlHSxbIJrbUZTOxckk7izPD4t3QG3QkTTIcCil9t%2B5nushigvAKT5KFsnS61GjSeHbzArJ8hbKuP1xSgOKa7Bs2suuYrSD%2BsOOCsVG0vWve6SilXsqVZjwFpHD1SjKcEQ5&X-Amz-Signature=77938ab13ed982eab6fbbd5e6de129c621164cefb5338d8e5b9e69fa96f77d48&X-Amz-SignedHeaders=host%3Bx-amz-acl&x-amz-acl=private
    private void uploadFile(File file,String url){

        if(file == null || !file.exists() || url == null){
            Log.d(TAG," uploadFile, params is null,return");
            return;
        }

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(60, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(60,TimeUnit.SECONDS);

      // okHttpClient.setConnectionSpecs(Collections.singletonList(spec));
       // okHttpClient.setSslSocketFactory(new SSLSocketFactoryCompat(trustAllCert));
        //OkHttpClient okHttpClient = new OkHttpClient();
        //MediaType fileType = MediaType.parse("File/*");
        MediaType fileType = MediaType.parse("application/octet-stream");
        RequestBody requestBody = RequestBody.create(fileType,file);

        Request request = new Request.Builder().url(url).put(requestBody).build();
        okHttpClient.newCall(request).enqueue(new Callback(){

            public void onFailure(Request request, IOException e) {
                Log.d(TAG," upload fail e: " + e.getMessage());
                e.printStackTrace();

            }

            public void onResponse(Response response) throws IOException {

                if(response != null){
                    Log.d(TAG,"  responese msg : " + response.message() + " code is: " + response.code() + " body is: " + response.body().string());
                }else{
                    Log.d(TAG,"  responese is null : ");
                }


            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG,"  permission: " + permissions[0] + "  grant result;  " + grantResults[0]);
        if(requestCode == 1){
            if(permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                uploadLogToServer(URL);
            }
        }else if(requestCode == 2){
            if(permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                copyDirTest(testADir,testBDir);
            }
        }
    }


    final X509TrustManager trustAllCert = new X509TrustManager() {
        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[]{};
        }
    };
   //copy dir testA to testB

    private void copyDirTest(File soruceDirFile,File targetDirFile){
        //sdcard
/*        if(dirFile.isFile()){
            File targetFile = new File(testBDir,dirFile.getName());
            copyfile(dirFile,targetFile);
            return;
        }*/
/*        if(!soruceDirFile.isDirectory() || !targetDirFile.isDirectory()){
            Log.d(TAG,"  the params is not dir ,return");
            return;
        }*/
        Log.d(TAG,"  soruceDirFile: " + soruceDirFile.getAbsolutePath());
        Log.d(TAG,"  targetDirFile: " + targetDirFile.getAbsolutePath());
        boolean result = false;
        String dirName = soruceDirFile.getName();
        File targetDir = new File(targetDirFile,dirName);
        if(!targetDir.exists()){
             result = targetDir.mkdirs();
            Log.d(TAG,"  create dir: " + targetDir.getAbsolutePath());
            if(result == false){
                Log.d(TAG,"  create dir fail,dir: " + targetDir.getAbsolutePath());
                return;
            }
        }else {
            Log.d(TAG, "the dir: " + targetDir.getAbsolutePath() + " is exist ,do not need create it");
        }
       // boolean result = targetDir.mkdir();


        File files[] = soruceDirFile.listFiles();
        for(File file : files){
           if(file.isFile()){//copy file
                File targetFile = new File(targetDir,file.getName());
                if(targetFile.exists()){
                    targetFile.delete();
                }
                try {
                    result = targetFile.createNewFile();
                    if(result == false){
                        Log.d(TAG," create new file fail. retrun " + targetFile.getAbsolutePath());
                        return;
                    }
                    Log.d(TAG,"  copy file sorce file: " + file.getAbsolutePath());
                    Log.d(TAG,"  copy file target file: " + targetFile.getAbsolutePath());
                    copyfile(file,targetFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if(file.isDirectory()){//copy dir
               copyDirTest(file,targetDir);
           }
        }
    }

    private void copyfile(File sourceFile,File targetFile){
        try {
             if(!sourceFile.isFile() || !targetFile.isFile()){
                 Log.d(TAG," the source or targe is not file,return ");
                 return;
             }

            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            FileOutputStream fileOutputStream = new FileOutputStream(targetFile);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = fileInputStream.read(buffer)) != -1){
                fileOutputStream.write(buffer,0,len);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void copyFileDir(){

    }
}
