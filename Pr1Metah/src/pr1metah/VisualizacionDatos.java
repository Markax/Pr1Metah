package com.example.sensoresmovil;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class VisualizacionDatos extends AppCompatActivity {

    //Gráficas de líneas para cada sensor
    private LineChart graficoAcelerometro;
    private LineChart graficoGravedad;
    private LineChart graficoMagnometro;
    private LineChart graficoProximidad;

    private DatabaseReference dbSensores; //Referencia a la base de datos en tiempo real
    private String usuario;
    private ArrayList<Acelerometro> valoresAcelerometro;
    private ArrayList<Gravedad> valoresGravedad;
    private ArrayList<Magnetometro> valoresMagnetometro;
    private ArrayList<Proximidad> valoresProximidad;

    private String tiempo[];
    Float valoresEjeX[];
    Float valoresEjeY[];
    Float valoresEjeZ[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizacion_datos);

        //Asociación con los elementos de la interfaz
        graficoAcelerometro = (LineChart)findViewById(R.id.graficoAcelerometro);
        graficoGravedad = (LineChart)findViewById(R.id.graficoGravedad);
        graficoMagnometro = (LineChart)findViewById(R.id.graficoMagnometro);
        graficoProximidad = (LineChart)findViewById(R.id.graficoProximidad);

        //Inicialización de variables y estructuras
        usuario = getIntent().getStringExtra("usuario");
        dbSensores = FirebaseDatabase.getInstance().getReference("Sensores");
        valoresAcelerometro = new ArrayList<>();
        valoresGravedad = new ArrayList<>();
        valoresMagnetometro = new ArrayList<>();
        valoresProximidad = new ArrayList<>();

        //Si hay conexión a internet consultamos la base de datos
        if(conexionInternet()){
            //Consultamos los datos asociados al acelerómetro
            dbSensores.child(usuario).child("Acelerometro").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    valoresAcelerometro.clear();
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        Acelerometro ace = snapshot.getValue(Acelerometro.class);
                        Double ejeX = ace.getEjeX();
                        Double ejeY = ace.getEjeY();
                        Double ejeZ = ace.getEjeZ();
                        String fecha = ace.getFecha();

                        Acelerometro nuevoAce = new Acelerometro(ejeX,ejeY,ejeZ,fecha);
                        valoresAcelerometro.add(nuevoAce);
                    }

                    //Inicialización de los datos de cada eje
                    obtenerValoresEjesAcelerometro();
                    //Trazado de la gráfica
                    dibujarGraficoAcelerometro();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            //Consultamos los datos asociados al sensor de gravedad
            dbSensores.child(usuario).child("Gravedad").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    valoresGravedad.clear();
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        Gravedad grav = snapshot.getValue(Gravedad.class);
                        Double ejeX = grav.getEjeX();
                        Double ejeY = grav.getEjeY();
                        Double ejeZ = grav.getEjeZ();
                        String fecha = grav.getFecha();

                        Gravedad nuevoGrav = new Gravedad(ejeX,ejeY,ejeZ,fecha);
                        valoresGravedad.add(nuevoGrav);
                    }

                    //Retardo de un segundo para no colapsar con la lectura de otros sensores
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Inicialización de los datos de cada eje
                            obtenerValoresEjesGravedad();
                            //Trazado de la gráfica
                            dibujarGraficoGravedad();
                        }
                    }, 1000);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            //Consultamos los datos asociados al magnetómetro
            dbSensores.child(usuario).child("Magnetometro").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    valoresMagnetometro.clear();
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        Magnetometro mag = snapshot.getValue(Magnetometro.class);
                        Double ejeX = mag.getEjeX();
                        Double ejeY = mag.getEjeY();
                        Double ejeZ = mag.getEjeZ();
                        String fecha = mag.getFecha();

                        Magnetometro nuevoMag = new Magnetometro(ejeX,ejeY,ejeZ,fecha);
                        valoresMagnetometro.add(nuevoMag);
                    }

                    //Retardo de dos segundos para no colapsar con la lectura de otros sensores
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Inicialización de los datos de cada eje
                            obtenerValoresEjesMagnetometro();
                            //Trazado de la gráfica
                            dibujarGraficoMagnetometro();
                        }
                    }, 2000);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            //Consultamos los datos asociados al sensor de proximidad
            dbSensores.child(usuario).child("Proximidad").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    valoresProximidad.clear();
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        Proximidad prox = snapshot.getValue(Proximidad.class);
                        Double v = prox.getValorPromidad();
                        String fecha = prox.getFecha();

                        Proximidad nuevoProx = new Proximidad(v,fecha);
                        valoresProximidad.add(nuevoProx);
                    }

                    //Retardo de tres segundos para no colapsar con la lectura de otros sensores
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Inicialización de los datos de cada eje
                            obtenerValoresEjesProximidad();
                            //Trazado de la gráfica
                            dibujarGraficoProximidad();
                        }
                    }, 3000);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            //No hay conexión a Internet
            Toast.makeText(getApplicationContext(),"No hay conexión a Internet", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * Método que permite trazar la gráfica del sensor de aceleración
     */
    private void dibujarGraficoAcelerometro(){

        Description description = new Description();
        description.setText(" ");
        description.setTextSize(10);
        graficoAcelerometro.setDescription(description);
        graficoAcelerometro.setData(getLineData(valoresEjeX,valoresEjeY, valoresEjeZ,"Eje X","Eje Y","Eje Z"));
        graficoAcelerometro.invalidate();
        graficoAcelerometro.setDrawBorders(true);
        graficoAcelerometro.setBorderColor(Color.BLACK);
        graficoAcelerometro.setBorderWidth(2);
        graficoAcelerometro.setScaleMinima(5f,1f);

        axisX(graficoAcelerometro.getXAxis());
        axisLeft(graficoAcelerometro.getAxisLeft());
        axisRight(graficoAcelerometro.getAxisRight());

    }

    /*
     * Método que permite trazar la gráfica del sensor de gravedad
     */
    private void dibujarGraficoGravedad(){
        Description description = new Description();
        description.setText(" ");
        description.setTextSize(10);
        graficoGravedad.setDescription(description);
        graficoGravedad.setData(getLineData(valoresEjeX,valoresEjeY, valoresEjeZ,"Eje X","Eje Y","Eje Z"));
        graficoGravedad.invalidate();
        graficoGravedad.setDrawBorders(true);
        graficoGravedad.setBorderColor(Color.BLACK);
        graficoGravedad.setBorderWidth(2);
        graficoGravedad.setScaleMinima(5f,1f);

        axisX(graficoGravedad.getXAxis());
        axisLeft(graficoGravedad.getAxisLeft());
        axisRight(graficoGravedad.getAxisRight());

    }

    /*
     * Método que permite trazar la gráfica del sensor magnético
     */
    private void dibujarGraficoMagnetometro(){
        Description description = new Description();
        description.setText(" ");
        description.setTextSize(10);
        graficoMagnometro.setDescription(description);
        graficoMagnometro.setData(getLineData(valoresEjeX,valoresEjeY, valoresEjeZ,"Eje X","Eje Y","Eje Z"));
        graficoMagnometro.invalidate();
        graficoMagnometro.setDrawBorders(true);
        graficoMagnometro.setBorderColor(Color.BLACK);
        graficoMagnometro.setBorderWidth(2);
        graficoMagnometro.setScaleMinima(5f,1f);

        axisX(graficoMagnometro.getXAxis());
        axisLeftMagnetico(graficoMagnometro.getAxisLeft());
        axisRight(graficoMagnometro.getAxisRight());
    }

    /*
     * Método que permite trazar la gráfica del sensor de proximidad
     */
    private void dibujarGraficoProximidad(){
        Description description = new Description();
        description.setText(" ");
        description.setTextSize(10);
        graficoProximidad.setDescription(description);
        graficoProximidad.setData(getLineData(valoresEjeX,"Valor"));
        graficoProximidad.invalidate();
        graficoProximidad.setDrawBorders(true);
        graficoProximidad.setBorderColor(Color.BLACK);
        graficoProximidad.setBorderWidth(2);
        graficoProximidad.setScaleMinima(5f,1f);

        axisX(graficoProximidad.getXAxis());
        axisLeft(graficoProximidad.getAxisLeft());
        axisRight(graficoProximidad.getAxisRight());
    }

    /*
     * Método que devuelve un arraylist con las entradas de la gráfica
     */
    private ArrayList<Entry> getEntries(Float[] parametro){
        ArrayList<Entry> entries = new ArrayList<>();
        for(int i=0;i<parametro.length;i++){
            entries.add(new Entry(i,parametro[i]));
        }
        return entries;
    }

    /*
     * Método que personaliza el eje de coordenadas X
     */
    private void axisX(XAxis axis){
        axis.setGranularityEnabled(true);
        axis.setPosition(XAxis.XAxisPosition.BOTTOM);
        axis.setValueFormatter(new IndexAxisValueFormatter(tiempo));
    }

    /*
     * Método que personaliza el eje de coordenadas Y de la izquierda
     */
    private void axisLeft(YAxis axis){
        axis.setSpaceTop(30);
        axis.setAxisMinimum(0);
    }

    /*
     * Método que personaliza el eje de coordenadas Y del magnómetro
     * para admitir valores negativos
     */
    private void axisLeftMagnetico(YAxis axis){
        axis.setSpaceTop(30);
        axis.setAxisMinimum(-30);
    }

    /*
     * Método que personazaliza el eje de coordenadas Y de la derecha
     */
    private void axisRight(YAxis axis){
        axis.setEnabled(false);
    }

    /*
     * Método para configurar la línea de datos asociada al eje X
     */
    private DataSet getData(DataSet dataSet){
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(10);

        return dataSet;
    }

    /*
     * Método para configurar la línea de datos asociada al eje Y
     */
    private DataSet getData2(DataSet dataSet){
        dataSet.setColor(Color.RED);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(10);

        return dataSet;
    }

    /*
     * Método para configurar la línea de datos asociada al eje Z
     */
    private DataSet getData3(DataSet dataSet){
        dataSet.setColor(Color.GREEN);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(10);

        return dataSet;
    }

    /*
     * Método que genera una estructura tipo LineData simple, es decir, sólo una línea en la gráfica (sensor de proximidad)
     */
    private LineData getLineData(Float[] parametro,String leyenda){
        LineData lineData = new LineData();

        LineDataSet lineDataSet = (LineDataSet)getData(new LineDataSet(getEntries(parametro),leyenda));
        lineDataSet.setLineWidth(3);
        lineDataSet.setCircleRadius(4);
        lineDataSet.setCircleColor(Color.BLACK);
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setDrawHighlightIndicators(false);
        lineDataSet.enableDashedLine(15f,6f,0);
        lineData.addDataSet(lineDataSet);

        return lineData;
    }

    /*
     * Método que genera una estructura tipo LineData triple, es decir, tres líneas en la gráfica con los datos que queremos mostrar
     */
    private LineData getLineData(Float[] parametro1, Float[] parametro2, Float[] parametro3,String leyenda1, String leyenda2, String leyenda3){
        LineData lineData = new LineData();

        LineDataSet lineDataSet = (LineDataSet)getData(new LineDataSet(getEntries(parametro1),leyenda1));
        lineDataSet.setLineWidth(3);
        lineDataSet.setCircleRadius(4);
        lineDataSet.setCircleColor(Color.BLACK);
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setDrawHighlightIndicators(false);
        lineDataSet.enableDashedLine(15f,6f,0);
        lineData.addDataSet(lineDataSet);

        LineDataSet lineDataSet2 = (LineDataSet)getData2(new LineDataSet(getEntries(parametro2),leyenda2));
        lineDataSet2.setLineWidth(3);
        lineDataSet2.setCircleRadius(4);
        lineDataSet2.setCircleColor(Color.BLACK);
        lineDataSet2.setDrawCircleHole(true);
        lineDataSet2.setDrawCircles(true);
        lineDataSet2.setDrawHorizontalHighlightIndicator(false);
        lineDataSet2.setDrawHighlightIndicators(false);
        lineDataSet2.enableDashedLine(15f,6f,0);
        lineData.addDataSet(lineDataSet2);

        LineDataSet lineDataSet3 = (LineDataSet)getData3(new LineDataSet(getEntries(parametro3),leyenda3));
        lineDataSet2.setLineWidth(3);
        lineDataSet2.setCircleRadius(4);
        lineDataSet2.setCircleColor(Color.BLACK);
        lineDataSet2.setDrawCircleHole(true);
        lineDataSet2.setDrawCircles(true);
        lineDataSet2.setDrawHorizontalHighlightIndicator(false);
        lineDataSet2.setDrawHighlightIndicators(false);
        lineDataSet2.enableDashedLine(15f,6f,0);
        lineData.addDataSet(lineDataSet3);

        return lineData;
    }

    /*
     * Método que permite inicializar las esctructuras con los valores del sensor de aceleración
     */
    private void obtenerValoresEjesAcelerometro(){
        tiempo = new String[valoresAcelerometro.size()];
        valoresEjeX = new Float[valoresAcelerometro.size()];
        valoresEjeY = new Float[valoresAcelerometro.size()];
        valoresEjeZ = new Float[valoresAcelerometro.size()];
        for(int i=0;i<valoresAcelerometro.size();i++){
            //tiempo[i]= String.valueOf(i+1);
            tiempo[i] = valoresAcelerometro.get(i).getFecha();
            valoresEjeX[i]=(float)valoresAcelerometro.get(i).getEjeX();
            valoresEjeY[i]=(float)valoresAcelerometro.get(i).getEjeY();
            valoresEjeZ[i]=(float)valoresAcelerometro.get(i).getEjeZ();
        }
    }

    /*
     * Método que permite inicializr las esctructuras con los valores del sensor de gravedad
     */
    private void obtenerValoresEjesGravedad(){
        tiempo = new String[valoresGravedad.size()];
        valoresEjeX = new Float[valoresGravedad.size()];
        valoresEjeY = new Float[valoresGravedad.size()];
        valoresEjeZ = new Float[valoresGravedad.size()];
        for(int i=0;i<valoresGravedad.size();i++){
            //tiempo[i]= String.valueOf(i+1);
            tiempo[i] = valoresGravedad.get(i).getFecha();
            valoresEjeX[i]=(float)valoresGravedad.get(i).getEjeX();
            valoresEjeY[i]=(float)valoresGravedad.get(i).getEjeY();
            valoresEjeZ[i]=(float)valoresGravedad.get(i).getEjeZ();
        }
    }

    /*
     * Método que permite inicializr las esctructuras con los valores del sensor de magnético
     */
    private void obtenerValoresEjesMagnetometro(){
        tiempo = new String[valoresMagnetometro.size()];
        valoresEjeX = new Float[valoresMagnetometro.size()];
        valoresEjeY = new Float[valoresMagnetometro.size()];
        valoresEjeZ = new Float[valoresMagnetometro.size()];
        for(int i=0;i<valoresMagnetometro.size();i++){
            //tiempo[i]= String.valueOf(i+1);
            tiempo[i] = valoresGravedad.get(i).getFecha();
            valoresEjeX[i]=(float)valoresMagnetometro.get(i).getEjeX();
            valoresEjeY[i]=(float)valoresMagnetometro.get(i).getEjeY();
            valoresEjeZ[i]=(float)valoresMagnetometro.get(i).getEjeZ();
        }
    }

    /*
     * Método que permite inicializr las esctructuras con los valores del sensor de proximidad
     */
    private void obtenerValoresEjesProximidad(){
        tiempo = new String[valoresProximidad.size()];
        valoresEjeX = new Float[valoresProximidad.size()];
        for(int i=0;i<valoresProximidad.size();i++){
            //tiempo[i]= String.valueOf(i+1);
            tiempo[i] = valoresProximidad.get(i).getFecha();
            valoresEjeX[i]=(float)valoresProximidad.get(i).getValorPromidad();
        }
    }

    /**
     * Método que permite comprobar si hay conexión a Internet
     */
    private boolean conexionInternet() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            //La conexión a la red está activada
            isAvailable = true;
        }
        return isAvailable;
    }
}
