package top.goup.update;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;

public class DownloadReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()){
            case DownloadManager.ACTION_DOWNLOAD_COMPLETE:
                long id = context.getSharedPreferences("ninja",0).getLong("downloadId",-2);
                if(id==intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1)){
                    installApk(context);
                    abortBroadcast();
                }else {
                    Log.i("Down","ERROR");
                }
                break;

        }
    }


    public static void installApk(Context context) {


        File file = new File(context.getSharedPreferences("ninja",0).getString("apkPath","null"));
        Intent intent = new Intent(Intent.ACTION_VIEW);

        //判读版本是否在7.0以上
        if (Build.VERSION.SDK_INT >= 24) {
            Log.v("Down","7.0以上，正在安装apk...");
            //provider authorities
            Uri apkUri = FileProvider.getUriForFile(context, "top.goup.update.fileProvider", file);
            //Granting Temporary Permissions to a URI
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            Log.v("Down","7.0以下，正在安装apk...");
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }


        context.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());


    }
}