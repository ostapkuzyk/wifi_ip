package com.example.wifiip;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.HashMap;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.embedding.engine.plugins.FlutterPlugin;

import static android.content.Context.WIFI_SERVICE;

/** WifiIpPlugin */
public class WifiIpPlugin implements FlutterPlugin, MethodCallHandler {
  final String TAG= "WifiIpPlugin";
  final Context context;

  
  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "com.lulu.plugin/get_wifi_ip");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    if (channel != null) {
      channel.setMethodCallHandler(null);
      channel = null;
    }
  }


  WifiIpPlugin(Context ctx)
  {
    context = ctx;
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    if (call.method.equals("getWifiIp")) {
      result.success(getWifiIP());
    } else {
      result.notImplemented();
    }
  }

  public HashMap<String, String> getWifiIP() {
    HashMap<String, String> result;
    try {
      WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
      DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();

      result = new HashMap<>();
      result.put("ip", intToIp(dhcpInfo.ipAddress));
      result.put("netmask", intToIp(dhcpInfo.netmask));
      result.put("broadcastIP", intToIp((dhcpInfo.ipAddress & dhcpInfo.netmask) | ~dhcpInfo.netmask));
      return result;
    } catch (Exception e) {
      Log.e(TAG, e.getMessage());
    }
    return null;
  }

  String intToIp(int i) {
    return (i & 0xFF) + "." +
            ((i >> 8) & 0xFF) + "." +
            ((i >> 16) & 0xFF) + "." +
            ((i >> 24) & 0xFF);
  }
}
