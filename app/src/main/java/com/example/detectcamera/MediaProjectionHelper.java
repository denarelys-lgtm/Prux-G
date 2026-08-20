package com.example.detectcamera;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.pm.PackageManager;
import java.io.OutputStream;
import rikka.shizuku.Shizuku;

public class MediaProjectionHelper {

    // Enum requerido por CameraService
    public enum ModoProyeccion {
        NONE,
        SHIZUKU,
        DEVICE_OWNER
    }

    // Comprueba qué modo de permisos está disponible
    public static ModoProyeccion obtenerModoDisponible(Context context) {
        if (isDeviceOwner(context)) {
            return ModoProyeccion.DEVICE_OWNER;
        } else if (isShizukuAvailable()) {
            return ModoProyeccion.SHIZUKU;
        }
        return ModoProyeccion.NONE;
    }

    public static boolean isDeviceOwner(Context context) {
        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        return dpm != null && dpm.isDeviceOwnerApp(context.getPackageName());
    }

    public static boolean isShizukuAvailable() {
        try {
            return Shizuku.pingBinder() && Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED;
        } catch (Exception e) {
            return false;
        }
    }

    // Otorga permisos mediante comandos de Shizuku
    public static boolean otorgarConsentimientoShizuku(String packageName) {
        String cmd1 = "appops set " + packageName + " PROJECT_MEDIA allow";
        String cmd2 = "pm grant " + packageName + " android.permission.PROJECT_MEDIA";
        return ejecutarComandoShell(cmd1) && ejecutarComandoShell(cmd2);
    }

    public static boolean ejecutarComandoShell(String command) {
        if (!isShizukuAvailable()) {
            return false;
        }

        try {
            Process process = Shizuku.newProcess(new String[]{"sh"}, null, null);
            OutputStream os = process.getOutputStream();
            os.write((command + "\n").getBytes());
            os.write("exit\n".getBytes());
            os.flush();
            os.close();

            return process.waitFor() == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
