package com.example.conexao;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.os.Handler;
public class MainActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener {
    private Handler handler;

    TextView txtcomunicacao,txtIp;
    EditText edtIp;
    Button BtnHandler,BtnClient,BtnGetIp,BTNcima,BTNesquerda,BTNdireita,BTNbaixo,BTNSalvar;
    Button BtnGetGestTest,BtnGetGestTreino,BtnCalcDTW;
    TextView txtResultDTW;

    Button BTN_comparar_banco;
    TextView txt_resultado_banco;

    Server server;
    Client client;
    int contador = 0;
    String auxIp;
    String ip;
    int ipAddress;

    List<String> dados = new ArrayList<>();
    List<List<String>> ListTreino = new ArrayList<List<String>>();
    List<List<String>> ListTeste = new ArrayList<List<String>>();
    List<List<String>> ListRepouso = new ArrayList<List<String>>();

    MDDTW mddtw;
    SensorManager sensorManager;
    Sensor sensor;
    Boolean boolteste = false, boolTreino =false;
    BancoDeDados bancoDeDados;
    SQLiteDatabase db;
    Boolean gesto_para_repuso = true;
    List<String> lista_de_gestos = Arrays.asList("GESTO1", "GESTO2","GESTO3","GESTO4");

    Boolean ipencontrado = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BtnHandler = findViewById(R.id.btnHandler);
        BtnClient = findViewById(R.id.btnclient);
        BtnGetIp = findViewById(R.id.btnGetIp);
        txtcomunicacao = findViewById(R.id.txtMensagem);
        txtIp = findViewById(R.id.txtenvio);
        edtIp = findViewById(R.id.EDTIp);
        BtnClient.setOnClickListener(this);
        BtnHandler.setOnClickListener(this);
        txtcomunicacao.setOnClickListener(this);
        BtnGetIp.setOnClickListener(this);


        BTNcima = findViewById(R.id.btncima);
        BTNbaixo = findViewById(R.id.btnbaixo);
        BTNesquerda = findViewById(R.id.btnesquerda);
        BTNdireita = findViewById(R.id.btndireita);
        BTNdireita.setOnClickListener(this);
        BTNesquerda.setOnClickListener(this);
        BTNcima.setOnClickListener(this);
        BTNbaixo.setOnClickListener(this);

        BtnCalcDTW = findViewById(R.id.btnCalcDTW);
        BtnGetGestTest = findViewById(R.id.btnGetTest);
        BtnGetGestTreino = findViewById(R.id.btnGetTreino);
        txtResultDTW = findViewById(R.id.txtResultadoDTW);
        BtnCalcDTW.setOnClickListener(this);
        BtnGetGestTest.setOnClickListener(this);
        BtnGetGestTreino.setOnClickListener(this);

        BTNSalvar = findViewById(R.id.salvarSerie);
        BTNSalvar.setOnClickListener(this);


        BTN_comparar_banco = findViewById(R.id.btnCompBanco);
        BTN_comparar_banco.setOnClickListener(this);
        txt_resultado_banco = findViewById(R.id.txtResultadoCOmpBanco);




        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        handler = new Handler();


       // checkPermission();

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(this.WIFI_SERVICE);
        ipAddress = wifiManager.getConnectionInfo().getIpAddress();
        Log.d("teste", "onCreate: " + ipAddress);
        // Converta o endereço IP inteiro em um formato legível por humanos (ex: 192.168.1.1)
         ip = Formatter.formatIpAddress(ipAddress);
        Log.d("teste", "onCreate: " + ip);
        txtIp.setText(ip);

        Log.d("Banco de dados", "criar banco de dados");
        bancoDeDados = new BancoDeDados(this);
        db = bancoDeDados.getReadableDatabase();
       // db.close();
        Log.d("Banco de dados", "fexhar banco de dados");


    }
    public boolean checkPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_DENIED){
            requestPermission();
            return false;
        }
        return true;
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_WIFI_STATE},1);

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

    @Override
    public void onClick(View view) {
        int click = view.getId();
        if(BtnHandler.getId() == click){
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
                            new Thread(new MainActivity.SocketHandler(socket)).start();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("teste", "run: erro ao criar serversocket");
                    }
                }
            }).start();
        } else if ( R.id.btnclient == click){

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Cria um novo Socket para se conectar ao ServerSocket
                        Log.d("teste", "criado conexão");
                        Socket socket = new Socket(auxIp, 12345);
                        Log.d("teste", "run: criado conexão");

                        // Envia dados para o ServerSocket
                        OutputStream outputStream = socket.getOutputStream();
                        PrintWriter writer = new PrintWriter(outputStream);

                        Log.d("teste", "run: criado conexão");
                        writer.println("Hello, Server!"+ contador);
                        contador = contador+1;
                        writer.flush();

                        // Fecha a conexão
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("teste", "run: erro criar socket");
                    }
                }
            }).start();
        }else if (R.id.btnGetIp == click){
            encontraIp();
        }else if (R.id.btncima == click){
            enviarMensagem("2");
        }else if(R.id.btnbaixo == click) {
            enviarMensagem("3");
        } else if (R.id.btndireita == click) {
            enviarMensagem("0");
        } else if (R.id.btnesquerda == click) {
            enviarMensagem("1");
        } else if (R.id.btnGetTreino == click) {
            Log.d("btntreino", " treino clicado");
            if (boolTreino){
                Log.d("btntreino", "treino clicado para falso");
                boolTreino = false;

            }else{
                Log.d("btntreino", "treino clicado para verdadeiro");
                boolTreino = true;
                ListTreino.clear();
            }

        } else if (R.id.btnGetTest == click) {
            Log.d("btnteste", "teste clicado");
            if (boolteste){
                boolteste = false;
                Log.d("btnteste", "teste clicado para falso");
            }else{
                Log.d("btnteste", "teste clicado para verdadeiro");
                boolteste = true;
                ListTeste.clear();
            }

        } else if (R.id.btnCalcDTW == click) {
            Log.d("DTW", "antes calc DTW");
            SensorData sensorData1 = new SensorData(ListTeste);
            SensorData sensorData2 = new SensorData(ListTreino);
            mddtw = new MDDTW(sensorData1,sensorData2);
            txtResultDTW.setText( "R: " + mddtw.getDistancia() );
            Log.d("DTW", "print" + mddtw.getDistancia() );

        } else if (R.id.salvarSerie == click) {

            Log.d("banco de dados", "antes de salvar ");
            SensorData sensorData1 = new SensorData(ListTeste);
           long idGesto = bancoDeDados.inserirGesto("GESTO4",db);
           bancoDeDados.inserir_dados_gesto(db,idGesto,sensorData1,"GESTO4");

        } else if (R.id.btnCompBanco == click) {

            iniciarCronometro();
            gesto_para_repuso = false;

        }
    }

    private void comparar_mddtw_com_banco() {
        MDDTW mddtw_banco;
        //Log.d("resultado do getGest", "get gest" +bancoDeDados.getListaGesto("GESTO1",ids,db));
        SensorData sensorData1 = new SensorData(ListRepouso);
        double menorDIstancia = Double.POSITIVE_INFINITY;;
        String gesto_mais_proximo = "não encontrado";

        for (int i = 0; i < lista_de_gestos.size();i++){
            List<Long> ids = new ArrayList<>();
            Log.d("TAG", "gesto : " + lista_de_gestos.get(i));
            ids = bancoDeDados.getListaIDGestos(db,lista_de_gestos.get(i));

            List<List<List<String>>> result = bancoDeDados.getListaGesto(lista_de_gestos.get(i),ids,db);
            Log.d("TAG", "i" + i);
            Log.d("TAG", "size result" + result.size());

            for (int j = 0 ; j < result.size();j++){
                Log.d("TAG", "j" + j);
                SensorData sensorData2 = new SensorData(result.get(j));
                mddtw_banco = new MDDTW(sensorData1,sensorData2);
                Log.d("TAG", "distancia : " +mddtw_banco.getDistancia());
                if (mddtw_banco.getDistancia() < menorDIstancia){
                    Log.d("TAG", "gesto menor encontrado" + mddtw_banco.getDistancia());
                    menorDIstancia = mddtw_banco.getDistancia();
                    gesto_mais_proximo = lista_de_gestos.get(i);
                }
            }
        }
        txt_resultado_banco.setText(gesto_mais_proximo);

        if (ipencontrado) {
            if (gesto_mais_proximo == "GESTO1") {
                enviarMensagem("0");
                //direita
            } else if (gesto_mais_proximo == "GESTO2") {
                enviarMensagem("1");
                //esquerda
            } else if (gesto_mais_proximo == "GESTO3") {
                enviarMensagem("2");
                //cima
            } else if (gesto_mais_proximo == "GESTO4") {
                enviarMensagem("3");
                //baixo
            }
        }

    }

    public void enviarMensagem(String mensagem){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Cria um novo Socket para se conectar ao ServerSocket
                    Log.d("teste", "criado conexão");
                    Socket socket = new Socket(auxIp, 12345);
                    Log.d("teste", "run: criado conexão");

                    // Envia dados para o ServerSocket
                    OutputStream outputStream = socket.getOutputStream();
                    PrintWriter writer = new PrintWriter(outputStream);

                    Log.d("teste", "run: criado conexão");
                    writer.println(mensagem);
                    writer.flush();
                    // Fecha a conexão
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("teste", "run: erro criar socket");
                }
            }
        }).start();
    }

    public void encontraIp(){
        Log.d("teste", "encontraIp: ");
        Handler handler = new Handler(Looper.getMainLooper());

        // Poste uma mensagem na fila de mensagens do Handler
        handler.post(new Runnable() {
            @Override
            public void run() {
                // Código que atualiza a interface do usuário
                auxIp = edtIp.getText().toString();
            }
        });
        ipencontrado = true;
        Log.d("teste", "encontraIp: " + auxIp);
    }
    public void mudarCorBTN_comparar(int cor){
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {
            @Override
            public void run() {
                BTN_comparar_banco.setBackgroundColor(cor);
            }
        });
    }
    private void iniciarCronometro() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (gesto_para_repuso){
                    Log.d("TAG", "run: repouso true");
                    mudarCorBTN_comparar(0xFF00FF00);
                    comparar_mddtw_com_banco();
                    ListRepouso.clear();
                    gesto_para_repuso = false;
                }else {
                    Log.d("TAG", "run: repouso false");
                    mudarCorBTN_comparar(0xFFFF0000);
                    gesto_para_repuso = true;
                }


                handler.postDelayed(this, 3000); // Executar novamente após 1 segundo
            }
        }, 3000); // Iniciar a contagem após 1 segundo
    }
    public void mudarTexto(String texto){
        Log.d("teste", "mudar texto " );
        //txtcomunicacao.setText(texto +"");
        //Log.d("teste", "apos mudar texto " );
        Handler handler = new Handler(Looper.getMainLooper());

        // Poste uma mensagem na fila de mensagens do Handler
        handler.post(new Runnable() {
            @Override
            public void run() {
                // Código que atualiza a interface do usuário
                txtcomunicacao.setText(texto);
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        String eixox = String.valueOf(sensorEvent.values[0]);
        String eixoy = String.valueOf(sensorEvent.values[1]);
        String eixoz = String.valueOf(sensorEvent.values[2]);

        List<String> dado = new ArrayList<String>();
        dado.add(eixox);
        dado.add(eixoy);
        dado.add(eixoz);

        if (boolTreino){
            ListTreino.add(dado);
        }
        if (boolteste){
            ListTeste.add(dado);
        }
        if (!gesto_para_repuso){
            Log.d("TAG", "onSensorChanged: inserindo banco");
            ListRepouso.add(dado);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);

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
                    Log.d("teste", "run: resultado " + line);
                    MainActivity.this.mudarTexto(line + "");
                    Log.d("teste", "run: teste resultado " );
                }

                // Fecha a conexão
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}