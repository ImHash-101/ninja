package top.goup.ninja;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Reciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context,MyService.class);
        context.startService(i);
    }
}
