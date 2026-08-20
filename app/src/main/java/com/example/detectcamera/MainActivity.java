package com.example.detectcamera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        verificarYSolicitarPermisos();
    }

    private void verificarYSolicitarPermisos() {
        String[] permisos;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permisos = new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.POST_NOTIFICATIONS
            };
        } else {
            permisos = new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
            };
        }

        boolean todosConcedidos = true;
        for (String perm : permisos) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                todosConcedidos = false;
                break;
            }
        }

        if (!todosConcedidos) {
            ActivityCompat.requestPermissions(this, permisos, PERMISSION_REQUEST_CODE);
        } else {
            iniciarServicioCamara();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean todosConcedidos = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    todosConcedidos = false;
                    break;
                }
            }
            if (todosConcedidos) {
                iniciarServicioCamara();
            } else {
                Toast.makeText(this, "Se requieren permisos para iniciar el servicio", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void iniciarServicioCamara() {
        Intent intent = new Intent(this, CameraService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }
}
