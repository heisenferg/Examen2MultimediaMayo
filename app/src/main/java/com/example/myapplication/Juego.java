package com.example.myapplication;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.provider.Settings.System.getString;
import static androidx.core.content.ContextCompat.getSystemService;
import static androidx.core.content.ContextCompat.startActivities;
import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.core.app.NotificationCompat;

import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.Random;

public class Juego extends SurfaceView  implements SurfaceHolder.Callback, View.OnTouchListener  {

    private SurfaceHolder holder;
    private BucleJuego bucle;
    private int madrigueraX, madrigueraY;
    Random aleatorio = new Random();
    Activity activity;
    private int AnchoPantalla,AltoPantalla;
   // int touchX, touchY, index;

    public int conejoX, conejoY;
    Bitmap conejo;
    int punteroConejo=0;
    float velocidad;
    boolean hayToque=false;
    private ArrayList<Toque> toques = new ArrayList<Toque>();
    private Control[] controles = new Control[4];
    private final int IZQUIERDA =0;
    private final int DERECHA =1;
    private final int ARRIBA = 2;
    private final int ABAJO = 3;
    int touchX, touchY, index;
    float punteroYConejo =0;
    float estadoConejoY;
    public static int contadorFrames =0;
    int estadoConejo=0;
    int estad_altura_conejo=0;
    boolean fin;

    private static final String TAG = Juego.class.getSimpleName();

    public Juego(Activity context) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);
        activity = context;
        fin = false;
        //Velocidad
        velocidad = 4;
        //Calculamos dimesiones de pantalla.
        dimesionesPantalla();
        //Calculamos posición de madriguera.
        madrigueraX = aleatorio.nextInt(AnchoPantalla) + 50;
        madrigueraY = aleatorio.nextInt(AltoPantalla) + 50;
        //Cargamos los controles
        CargaControles();
        //Cargamos conejo
        conejo = BitmapFactory.decodeResource(getResources(), R.drawable.rabbit);
        conejo.createScaledBitmap(conejo, 70, 110, true);
        // Situamos al conejo:
        conejoX = AnchoPantalla/2-conejo.getWidth()/5;
        conejoY = AltoPantalla/4*5;

        //Listener del onTouch
        setOnTouchListener(this);

    }

    public void dimesionesPantalla(){
        if(Build.VERSION.SDK_INT > 13) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            AnchoPantalla = size.x;
            AltoPantalla = size.y;
        }
        else{
            Display display = activity.getWindowManager().getDefaultDisplay();
            AnchoPantalla = display.getWidth();  // deprecated
            AltoPantalla = display.getHeight();  // deprecated
        }
        Log.i(Juego.class.getSimpleName(), "alto:" + AltoPantalla + "," + "ancho:" + AnchoPantalla);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // se crea la superficie, creamos el game loop

        // Para interceptar los eventos de la SurfaceView
        getHolder().addCallback(this);

        // creamos el game loop
        bucle = new BucleJuego(getHolder(), this);

        // Hacer la Vista focusable para que pueda capturar eventos
        setFocusable(true);

        //comenzar el bucle
        bucle.start();

    }

    /**
     * Este método actualiza el estado del juego. Contiene la lógica del videojuego
     * generando los nuevos estados y dejando listo el sistema para un repintado.
     */
    public void actualizar() {

    contadorFrames++;

        for (int i=0; i<4; i++){
            if (controles[i].pulsado){
                Log.d("Control: ", "Se ha pulsado " + controles[i].nombre);
            }
        }

        /**
         * CONTROLES
         */
        if (controles[IZQUIERDA].pulsado){
            if (conejoX >=0) {
                conejoX = (int) (conejoX  - 2*velocidad);
                //punteroConejo++;
                // Estado conejo es la fila
                estad_altura_conejo=1;
                punteroYConejo = conejo.getHeight() / 4 * estadoConejoY;
                actualizarSpriteConejo();
            }

        }
        if (controles[DERECHA].pulsado){
            //Controlamos que no se salga por la derecha.
            if (conejoX <AnchoPantalla-conejo.getWidth()/4)
                estad_altura_conejo=3;

            conejoX = (int) (conejoX + 2*velocidad);
            actualizarSpriteConejo();
        }

        if (controles[ARRIBA].pulsado){
            if (conejoY>0){
                estad_altura_conejo=2;

                conejoY = (int) (conejoY - 2*velocidad);
                actualizarSpriteConejo();
            }
        }
        if (controles[ABAJO].pulsado){
            if (conejoY/4<AltoPantalla){
                estad_altura_conejo=2;

                conejoY = (int) (conejoY + 2*velocidad);
                actualizarSpriteConejo();
            }
        }


        // Llegada madriguera. 100 es el radio.
        Log.d("Conejo: ", " ConejoX=" + (conejoX+conejo.getWidth()/4) + " madrigueraX=" + madrigueraX);
        Log.d("Conejo: ", " ConejoY=" + (conejoY) + " madrigueraY=" + madrigueraY);

/*
        if (conejoX+conejo.getWidth()/4 >= madrigueraX-50 && conejoX+conejo.getWidth()/4<=madrigueraX+100){
            Log.d("FIN", " Se acabó");
            if (//conejoY-(conejo.getHeight()/4)*3>madrigueraY && conejoY-(conejo.getHeight()/4)*3<madrigueraY+100) {
            (conejoY >= madrigueraY-50 && conejoY<=madrigueraY+100)){
                Log.d("FIN2", " Se acabó");
                fin = true;
            } else {
                fin = false;
            }
        }
*/


    }

    public boolean colisionCirculo(){
        int alto_mayor= Math.max(100, conejo.getHeight()/4);
        int ancho_mayor= Math.max(100, conejo.getWidth()/4);
        float diferenciaX=Math.abs(madrigueraX-conejoX);
        float diferenciaY=Math.abs(madrigueraY-conejoY);
        return diferenciaX<ancho_mayor && diferenciaY<alto_mayor;
    }

    /**
     * Este método dibuja el siguiente paso de la animación correspondiente
     */
    public void renderizar(Canvas canvas) {

        canvas.drawColor(Color.BLACK);

        //pintar mensajes que nos ayudan
        Paint p=new Paint();
        p.setStyle(Paint.Style.FILL_AND_STROKE);
        p.setColor(Color.RED);
        p.setTextSize(50);
        canvas.drawText("Frame "+bucle.iteraciones+";"+"Tiempo "+bucle.tiempoTotal,50,150,p);

        canvas.drawCircle(madrigueraX,madrigueraY,100,p);

        //Dibujar controles
        p.setAlpha(400);
        for (int i = 0; i<4; i++){
            controles[i].Dibujar(canvas, p);
        }

        //Dibujar conejo:
     /*   canvas.drawBitmap(conejo, new Rect((int) punteroConejo, (int) punteroYConejo, (int) (punteroConejo + conejo.getWidth()/4), conejo.getHeight()/4),
                new Rect((int)conejoX, (int) conejoY-conejo.getHeight(), (int)conejoX+conejo.getWidth()/4, (int) (conejo.getHeight()/4+conejoY)-conejo.getHeight()),
                null);
*/
        canvas.drawBitmap(conejo,
                new Rect(punteroConejo,0+(conejo.getHeight()/4)*estad_altura_conejo,punteroConejo+conejo.getWidth()/4,(conejo.getHeight()*1/4)+(conejo.getHeight()/4)*estad_altura_conejo),
                new Rect((int)conejoX,(int) conejoY-conejo.getHeight()*1/4,(int)conejoX+conejo.getWidth()/4,conejoY),
                null);


        if (colisionCirculo()){
victoriaFindeJuego(p, canvas);
            Log.d("Fin: ", " 1");

abrirParaNotificar(this);

        }
Log.d("Fin: ", " está: " + fin);

    }


public void abrirParaNotificar (View v){
    Intent i = new Intent(getContext(), MainActivity.class);
    v.getContext().startActivity(i);
}


    public void victoriaFindeJuego(Paint myPaint, Canvas canvas){
        myPaint.setAlpha(0);

            //Bandera Nazi Victoria
          //  canvas.drawBitmap(banderaNazi, AnchoPantalla/2-banderaNazi.getWidth()/2, AltoPantalla-banderaNazi.getHeight()*2, null);
            myPaint.setColor(Color.GREEN);
            myPaint.setTextSize(AnchoPantalla/10);
            canvas.drawText("¡Llegaste!", AnchoPantalla/4, AltoPantalla/2-100, myPaint);
            myPaint.setTextSize(AnchoPantalla/20);
            canvas.drawText("El conejo se resguardó", AnchoPantalla/4, AltoPantalla/2, myPaint);
            myPaint.setColor(Color.MAGENTA);
            canvas.drawText("Acabaste em " + contadorFrames + " frames", AnchoPantalla/5, AltoPantalla/2+100, myPaint);
            fin();
        }

    public void actualizarSpriteConejo(){
        if (contadorFrames%3==0) {
            punteroConejo = conejo.getWidth() / 4 * estadoConejo;
            estadoConejo++;
            if (estadoConejo >= 4) {
                estadoConejo = 0;
            }
        }
    }

    // Para liberar recursos
    public void fin(){
        bucle.JuegoEnEjecucion=false;
        try{
            conejo.recycle();
        } catch (Exception e){
            Log.d("Excepción: ", "reproductores");
        }

    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "Juego destruido!");
        // cerrar el thread y esperar que acabe
        boolean retry = true;
        while (retry) {
            try {
                bucle.join();
                fin();
                retry = false;
            } catch (InterruptedException e) {

            }
        }
    }



    // Controles
    public void CargaControles(){
        float aux;

        //Izquierda
        controles[IZQUIERDA]=new Control(getContext(),0,AltoPantalla/5*4);
        controles[IZQUIERDA].Cargar(R.drawable.izquierda);
        controles[IZQUIERDA].nombre="Izquieda";

        //Derecha
        controles[DERECHA]=new Control(getContext(),
                controles[0].xCoordenada+controles[0].Ancho(), controles[0].yCoordenada);
        controles[DERECHA].Cargar(R.drawable.derecha);
        controles[DERECHA].nombre="Derecha";

        //Arriba
        //aux=6.0f/7.0f*maxX; //en los 6/7 del ancho
        controles[ARRIBA]=new Control(getContext(),controles[0].xCoordenada,controles[0].yCoordenada-controles[0].Alto());
        controles[ARRIBA].Cargar(R.drawable.arriba);
        controles[ARRIBA].nombre="Arriba";

        //Poner música
        controles[ABAJO] = new Control(getContext(), controles[0].xCoordenada+controles[0].Ancho(), controles[0].yCoordenada-controles[0].Alto());
        controles[ABAJO].Cargar(R.drawable.abajo);
        controles[ABAJO].nombre="Abajo";
    }






    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // Obtener el pointer asociado con la acción
        index = event.getActionIndex();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                hayToque = true;
                touchX = (int) event.getX(index);
                touchY = (int) event.getY(index);
                synchronized (this) {
                    toques.add(index, new Toque(index, touchX, touchY));
                }
                // Se comprueba si se ha pulsado.
                for(int i=0;i<4;i++)
                    controles[i].comprueba_Pulsado(touchX,touchY);
                Log.i(Juego.class.getSimpleName(), "Pulsado dedo " + index + ".");
                break;
            case MotionEvent.ACTION_POINTER_UP:
                synchronized (this) {
                    toques.remove(index);
                }
                // Se comprueba si se ha soltado.
                for(int i=0;i<4;i++)
                    controles[i].compruebaSoltado(toques);
                Log.i(Juego.class.getSimpleName(), "Soltado dedo " + index + ".");
                break;
            case MotionEvent.ACTION_UP:
                synchronized (this) {
                    toques.remove(index);
                }
                // Se comprueba si se ha soltado.
                for(int i=0;i<4;i++)
                    controles[i].compruebaSoltado(toques);
                Log.i(Juego.class.getSimpleName(), "Soltado dedo " + index + ".ultimo.");
                hayToque = false;
                break;
        }
        return true;
    }

}
