package com.example.unityplugin;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

public class PluginInstance extends Activity {
    public static Activity unityActivity;
    String ip;
    int ipAddress;
    List<String> lista = new ArrayList<String>();
    List<String> listateste = new ArrayList<String>();

    public boolean checkPermission(){
        if(ContextCompat.checkSelfPermission(unityActivity.getApplicationContext(), Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_DENIED){
            requestPermission();
            return false;
        }
        return true;
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(unityActivity,new String[]{Manifest.permission.ACCESS_WIFI_STATE},1);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                }else{
                    checkPermission();
                }
                return;
        }
    }
    public List<String> getLista() {
        int TamAux = lista.size();
        List<String> listaAux = new ArrayList<String>();
        for(int i = 0;i < TamAux;i++){
            listaAux.add(lista.remove(0));
        }
        return listaAux;
    }
    public List<String> getListateste() {
        listateste.add("0");
        listateste.add("0");
        return listateste;
    }

    public static void receiveUnityActivity(Activity tactivity){
        unityActivity = tactivity;
    }

    public String getIp(){
        WifiManager wifiManager = (WifiManager) unityActivity.getApplicationContext().getSystemService(unityActivity.getApplicationContext().WIFI_SERVICE);
        ipAddress = wifiManager.getConnectionInfo().getIpAddress();
        // Converta o endereço IP inteiro em um formato legível por humanos (ex: 192.168.1.1)
        ip = Formatter.formatIpAddress(ipAddress);
        return ip;
    }
    public void criar_Server(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Cria um novo ServerSocket na porta desejada
                    Log.d("teste", "run: server socket");
                    ServerSocket serverSocket = new ServerSocket(12345);
                    Log.d("teste", "run: criado server socket");
                    // Fica em um loop para esperar novas conexões
                    while (true) {
                        // Aguarda uma nova conexão
                        Socket socket = serverSocket.accept();

                        // Processa a conexão em uma nova thread
                        new Thread(new PluginInstance.SocketHandler(socket)).start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("teste", "run: erro ao criar serversocket");
                }
            }
        }).start();
    }

    private class SocketHandler implements Runnable {
        private final Socket socket;

        public SocketHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                // Lê os dados da conexão
                InputStream inputStream = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    // Processa os dados recebidos
                    // ...
                    /*Log.d("teste", "run: resultado " + line);
                    PluginInstance.this.mudarTexto(line + "");
                    Log.d("teste", "run: teste resultado " );*/
                    lista.add(line + "");

                }

                // Fecha a conexão
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
