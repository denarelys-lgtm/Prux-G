package com.example.detectcamera;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Formatter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private TextView tvIpAddress;
    private Button btnStartServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        tvIpAddress = findViewById(R.id.tvIpAddress);
        btnStartServer = findViewById(R.id.btnStartServer);

        tvIpAddress.setText("IP: http://" + obtenerIpLocal() + ":8080");

        // Otorga permisos en tiempo de ejecución silenciosamente vía Device Owner
        AdminUtils.otorgarPermisosSilenciosamente(this);

        btnStartServer.setOnClickListener(v -> iniciarServidorService());
    }

    private String obtenerIpLocal() {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wm != null) return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        return "Desconocida";
    }

    private void iniciarServidorService() {
        Intent serviceIntent = new Intent(this, CameraService.class);
        serviceIntent.putExtra("USER_PARAM", etUsername.getText().toString());
        serviceIntent.putExtra("PASS_PARAM", etPassword.getText().toString());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
        Toast.makeText(this, "Servidor Iniciado", Toast.LENGTH_SHORT).show();
    }
}
