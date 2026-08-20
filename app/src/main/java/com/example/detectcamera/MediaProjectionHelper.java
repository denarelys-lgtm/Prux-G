package com.example.detectcamera;

import android.content.Context;
import android.content.pm.PackageManager;
import java.io.OutputStream;

// Importación correcta de Shizuku
import rikka.shizuku.Shizuku;

public class MediaProjectionHelper {

    public static boolean isShizukuAvailable() {
        try {
            return Shizuku.pingBinder() && Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean ejecutarComandoShell(String command) {
        if (!isShizukuAvailable()) {
            return false;
        }

        try {
            // Se usa Shizuku.newProcess en lugar de ShizukuExec.exec
            Process process = Shizuku.newProcess(new String[]{"sh"}, null, null);
            
            OutputStream os = process.getOutputStream();
            os.write((command + "\n").getBytes());
            os.write("exit\n".getBytes());
            os.flush();
            os.close();

            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
