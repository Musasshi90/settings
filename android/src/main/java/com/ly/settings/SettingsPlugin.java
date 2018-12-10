package com.ly.settings;

import android.app.Activity;
import android.content.Intent;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.provider.Settings;
import android.text.TextUtils;

import java.util.ArrayList;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import static android.content.Context.LOCATION_SERVICE;

public class SettingsPlugin implements MethodCallHandler, PluginRegistry.ActivityResultListener {
    private Registrar registrar;
    private static int REQUEST_LOCATION_SETTING = 12;
    private static int REQUEST_WIFI_SETTING = 13;
    private Result pendingResult;

    private SettingsPlugin(Registrar registrar) {
        this.registrar = registrar;
    }

    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "plugins.ly.com/settings");
        SettingsPlugin instance = new SettingsPlugin(registrar);
        registrar.addActivityResultListener(instance);
        channel.setMethodCallHandler(instance);
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        pendingResult = result;
        switch (call.method) {
            case "wifi":
                openWiFiSettings();
                break;
            case "gps":
                openGPSSettings();
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    private void openWiFiSettings() {
        registrar.activity().startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
    }

    private void openGPSSettings() {
        registrar.activity().startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_LOCATION_SETTING);
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (pendingResult != null) {
            if (requestCode == REQUEST_LOCATION_SETTING) {
                pendingResult.success(true);
                return true;
            } else if (requestCode == REQUEST_WIFI_SETTING) {
                pendingResult.success(true);
                return true;
            }
            pendingResult = null;
        }
        return false;
    }
}
