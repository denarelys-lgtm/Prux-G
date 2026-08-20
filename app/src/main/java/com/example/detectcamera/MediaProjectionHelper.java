package com.example.detectcamera;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import rikka.shizuku.Shizuku;
import rikka.shizuku.ShizukuExec;

public class MediaProjectionHelper {

    private static final String TAG = "MediaProjectionHelper";

    public enum ModoProyeccion {
        DEVICE_OWNER,
        SHIZUKU,
        STANDARD_DIALOG
    }

    public static ModoProyeccion obtenerModoDisponible(Context context) {
        if (esDeviceOwner(context)) {
            Log.d(TAG, "Modo seleccionado: DEVICE OWNER");
            return ModoProyeccion.DEVICE_OWNER;
        }

        if (esShizukuDisponible()) {
            Log.d(TAG, "Modo seleccionado: SHIZUKU");
            return ModoProyeccion.SHIZUKU;
        }

        Log.d(TAG, "Modo seleccionado: DIÁLOGO ESTÁNDAR");
        return ModoProyeccion.STANDARD_DIALOG;
    }

    public static boolean esDeviceOwner(Context context) {
        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        return dpm != null && dpm.isDeviceOwnerApp(context.getPackageName());
    }

    public static boolean esShizukuDisponible() {
        try {
            return Shizuku.pingBinder() && Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean otorgarConsentimientoShizuku(String packageName) {
        try {
            String command = "cmd media_projection grant-consent " + packageName;
            Process process = ShizukuExec.exec(new String[]{"sh", "-c", command}, null, null);
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                Log.d(TAG, "Shizuku output: " + line);
            }
            
            return process.waitFor() == 0;
        } catch (Exception e) {
            Log.e(TAG, "Error otorgando consentimiento vía Shizuku", e);
            return false;
        }
    }
}
