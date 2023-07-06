package pers.ervinse.ddmall.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.Gson;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pers.ervinse.ddmall.domain.Photo;
import pers.ervinse.ddmall.domain.Result;

public class OkhttpUtils {
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json;charset=utf-8");
    private static final OkHttpClient okhttpclient = new OkHttpClient();

    public static OkHttpClient genericClient(String value) {
        OkHttpClient httpClient = okhttpclient.newBuilder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request()
                                .newBuilder()
                                .addHeader("token", value)
                                .build();
                        return chain.proceed(request);
                    }
                })
                .build();
        return httpClient;
    }

    public static String doGet(String url, String que) throws IOException {

        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = okhttpclient.newCall(request);
        Response resp = call.execute();

        return resp.body().string();
    }


    public static String doGet(String url) throws IOException {

        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = okhttpclient.newCall(request);
        Response resp = call.execute();

        return resp.body().string();
    }

    public static String doGetByToken(String url, String token) throws IOException {

        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = genericClient(token).newCall(request);
        Response resp = call.execute();
        return resp.body().string();
    }

    public static String doDeleteByToken(String url, String token) throws IOException {

        Request request = new Request.Builder()
                .delete()
                .url(url)
                .build();
        Call call = genericClient(token).newCall(request);
        Response resp = call.execute();
        return resp.body().string();
    }

    public static void setImage(ImageView view, String pictureName, Context context) throws IOException {
        String TargetPath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString();
        Bitmap bitmap = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(TargetPath + "/" + pictureName + ".png");
            bitmap = BitmapFactory.decodeStream(fis);
            view.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) fis.close();
        }

    }

    public static void saveImage(String url, String pictureName, Context context) throws IOException {

        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = okhttpclient.newCall(request);
        Response resp = call.execute();
        Log.i("当前图片code获取", url);
        Result<Photo> medicineResponse = JSONObject.parseObject(resp.body().string(), new TypeReference<Result<Photo>>() {
        });
        Log.i("当前图片code获取", medicineResponse.getCode().toString());

        if (medicineResponse.getCode().equals(500)) return;

        byte[] data = medicineResponse.getData().getPhotoBytes().getBytes();
        data = Base64.getDecoder().decode(data);
        Log.i("", medicineResponse.getData().toString());

        //获取位图文件
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

        //获取目标目录
        String TargetPath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString();
        FileOutputStream fos = null;
        try {
            // 本地目录
            fos = new FileOutputStream(TargetPath + "/" + pictureName + ".png");

            bitmap.compress(Bitmap.CompressFormat.PNG, 80, fos);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                fos.flush();
                fos.close();
            }
        }
    }

    public static String doPost(String url, String json) throws IOException {
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, json);
        Request request = new Request.Builder().post(requestBody).url(url).build();
        Call call = okhttpclient.newCall(request);
        Response response = call.execute();
        return response.body().string();
    }

    public static String doPostByToken(String url, String json, String token) throws IOException {
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, json);
        Request request = new Request.Builder().post(requestBody).url(url).build();
        Call call = genericClient(token).newCall(request);
        Response response = call.execute();
        return response.body().string();
    }

    public static String doPutByToken(String url, String json, String token) throws IOException {
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, json);
        Request request = new Request.Builder().put(requestBody).url(url).build();
        Call call = genericClient(token).newCall(request);
        Response response = call.execute();
        return response.body().string();
    }

    public static void uploadImage(String userName, File file, String url) throws IOException {

        //2.创建RequestBody
        RequestBody fileBody = RequestBody.create(MEDIA_TYPE_PNG, file);
        String filename = file.getName();
        //3.构建MultipartBody
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", filename, fileBody)
                .addFormDataPart("userName", userName)
                .build();

        //4.构建请求
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        //5.发送请求
        Response response = okhttpclient.newCall(request).execute();
    }

    public static void downloadImage(String url, String picturename) {
        // 创建一个GET方式的请求结构
        Request request = new Request.Builder().url(url).build();
        Call call = okhttpclient.newCall(request); // 根据请求结构创建调用对象
        // 加入HTTP请求队列。异步调用，并设置接口应答的回调方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { // 请求失败
                Log.i("PICTURE ERROR", "网络出错");
            }

            @Override
            public void onResponse(Call call, final Response response) { // 请求成功
                InputStream is = response.body().byteStream();
                // 从返回的输入流中解码获得位图数据
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                String mediaType = response.body().contentType().toString();
                long length = response.body().contentLength();
                String desc = String.format("文件类型为%s，文件大小为%d", mediaType, length);

                Log.i("SAVE BITMAP", "Ready to save picture");
                //指定我们想要存储文件的地址
                String TargetPath = "/drawable/";
                Log.i("SAVE BITMAP", "Save Path=" + TargetPath);
                //判断指定文件夹的路径是否存在
                File saveFile = new File(TargetPath, picturename);
                try {
                    FileOutputStream saveImgOut = new FileOutputStream(saveFile);
                    // compress - 压缩的意思
                    bitmap.compress(Bitmap.CompressFormat.PNG, 80, saveImgOut);
                    //存储完成后需要清除相关的进程
                    saveImgOut.flush();
                    saveImgOut.close();
                    Log.d("SAVE BITMAP", "The picture is save to your phone!");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

}
