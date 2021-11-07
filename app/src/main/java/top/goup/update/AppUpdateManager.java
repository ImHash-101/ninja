package top.goup.update;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class AppUpdateManager {
    private final String DownloadUrl = "http://1.116.164.17/version.xml";
    HashMap<String, String> info;
    HttpClient httpClient = new HttpClient();
    Context mContext;

    final int ERROR = 0;

    final int SERVICE_VERSION_GOT = 1;

    String apkPath;

    int last_version_code;
    int version_code;

    public AppUpdateManager(Context context){
        mContext =context;

    }


    @SuppressWarnings("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SERVICE_VERSION_GOT:
                    HashMap<String, String> map = (HashMap<String, String>) msg.obj;
                    Log.i("Down", "version:" + map.get("version"));
                    Log.i("Down", "url:" + map.get("url"));
                    info = map;
                    version_code = getVersionCode(mContext);
                    last_version_code = Integer.parseInt(map.get("version"));
                    if(last_version_code>version_code)ShowDialog();

                    break;

                case ERROR:
                    String info = (String) msg.obj;
                    Log.i("Down", "Error:" + info);
                    break;
            }
        }
    };

    /**
     * 显示软件更新对话框
     */

    private void ShowDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("有新版本");
        builder.setMessage("更新内容");
        builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downLoadApk();
                dialog.dismiss();

            }
        }); builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


    private void downLoadApk() {
        apkPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + "ninja_"+last_version_code+".apk";

        //创建request对象
        DownloadManager.Request request=new DownloadManager.Request(Uri.parse(info.get("url")));
        //设置什么网络情况下可以下载
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        //设置通知栏的标题
        request.setTitle("下载");
        //设置通知栏的message
        request.setDescription("Ninja正在下载.....");
        //设置漫游状态下是否可以下载
        request.setAllowedOverRoaming(false);
        //设置是否允许数据下载
        request.setAllowedOverMetered(true);
        //设置文件存放目录
        //request.setDestinationInExternalFilesDir(mContext, Environment.DIRECTORY_DOWNLOADS,"update.apk");
        //request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "update.apk");
        request.setDestinationUri(Uri.fromFile(new File(apkPath)));
        //设置是否显示
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        //获取系统服务
        DownloadManager downloadManager= (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        //进行下载
        long id = downloadManager.enqueue(request);

        SharedPreferences preferences = mContext.getSharedPreferences("ninja",0);
        boolean a = preferences.edit().putLong("downloadId", id).commit();
        boolean b = preferences.edit().putString("apkPath",apkPath).commit();

        DownloadReceiver receiver = new DownloadReceiver();
        mContext.registerReceiver(receiver,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    public void check() {
        new GetServiceVersion().start();
    }

    /**
     * 获取软件版本号，对应AndroidManifest.xml下android:versionCode
     * 修改版本在module build.gradle里修改
     *
     * @param context context
     * @return int versionCode
     */
    public static int getVersionCode(Context context) {
        int versionCode = -1;
        try {
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (Exception ignored) {
        }
        return versionCode;
    }

    /**
     * 从服务器获取最新版本信息
     */
    private class GetServiceVersion extends Thread {
        GetMethod getMethod = new GetMethod(DownloadUrl);

        @Override
        public void run() {
            super.run();
            Message msg = new Message();
            try {

                httpClient.executeMethod(getMethod);
                ParseXmlService service = new ParseXmlService();

                HashMap<String, String> map = service.parseXml(getMethod.getResponseBodyAsStream());
                msg.what = SERVICE_VERSION_GOT;
                msg.obj = map;

            } catch (Exception e) {
                msg.obj = e.getMessage();
                msg.what = ERROR;
            }
            handler.sendMessage(msg);

        }
    }


    /**
     * 解析XML基类
     */
    public class ParseXmlService {
        public HashMap<String, String> parseXml(InputStream inStream) throws Exception {
            HashMap<String, String> hashMap = new HashMap<String, String>();

            // 实例化一个文档构建器工厂
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // 通过文档构建器工厂获取一个文档构建器
            DocumentBuilder builder = factory.newDocumentBuilder();
            // 通过文档通过文档构建器构建一个文档实例
            Document document = builder.parse(inStream);
            // 获取XML文件根节点
            Element root = document.getDocumentElement();
            // 获得所有子节点
            NodeList childNodes = root.getChildNodes();
            for (int j = 0; j < childNodes.getLength(); j++) {
                // 遍历子节点
                Node childNode = (Node) childNodes.item(j);
                if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element childElement = (Element) childNode;
                    // 版本号
                    if ("version".equals(childElement.getNodeName())) {
                        hashMap.put("version", childElement.getFirstChild().getNodeValue());
                    }
                    // 软件名称
                    else if (("name".equals(childElement.getNodeName()))) {
                        hashMap.put("name", childElement.getFirstChild().getNodeValue());
                    }
                    // 下载地址
                    else if (("url".equals(childElement.getNodeName()))) {
                        hashMap.put("url", childElement.getFirstChild().getNodeValue());
                    }
                }
            }
            return hashMap;
        }
    }

}