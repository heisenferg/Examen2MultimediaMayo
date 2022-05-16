package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        enviarNotificacion();
    }


    public static int id;

    public void enviarNotificacion(){
        Log.d("NOTIFICACIÓN", "Comienza");
        crearCanalNotificaciones();
        id = 1;
        // Abre el listado de los cumpleaños guardados
        Intent intent = new Intent(getApplicationContext(), ActividadJuego.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(),0,intent,0);
        NotificationManager notificationManager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"noti").
                setSmallIcon(R.drawable.icon)
                .setContentTitle("Ganaste!")
                .setContentText("Consumiste " + Juego.contadorFrames + " frames.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        Log.d("NOTIFICACIÓN", "después de builder");


        notificationManager.notify(id, builder.build());

    }


    private void crearCanalNotificaciones(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel canal2 = new NotificationChannel("noti", "nombre", NotificationManager.IMPORTANCE_DEFAULT);
            canal2.setDescription("Felicitacion cumpleaños");
            NotificationManager notificationManager = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(canal2);
        }

        Log.d("NOTIFICACIÓN", "FIN canal");

    }

    public void prueba(){
        crearCanalNotificaciones();
        Context context = this;
        Intent intent = new Intent(context,Juego.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);
        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this.getApplicationContext())//Habrá una línea media aquí, que no afecta la operación, esto esandroidProblema de versión del sistema
                .setContentTitle("título")  //Mostrar el título de la notificación
                .setContentText("El contenido de la notificación se muestra aquí.~")//Mostrar el contenido de la notificación del mensaje
                .setWhen(System.currentTimeMillis())//Mostrar la hora específica de la notificación
                .setSmallIcon(R.mipmap.ic_launcher)//La configuración que se muestra aquí son los íconos de aplicaciones para notificaciones del sistema en la parte superior del teléfono
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))//Aquí está configurado para mostrar el icono del sistema que se muestra después de que la barra de notificaciones se despliega
                .setContentIntent(pendingIntent)
                //.setAutoCancel(true)//Puede usar este método aquí, después de hacer clic en la notificación, el contenido de la notificación se cancela automáticamente,Puede también serNotificationActivity.javaEstablecer el método para cancelar el contenido de la notificación
                .setVibrate(new long[] {0,1000,1000,1000})//Establezca la vibración durante un segundo después de la notificación, pare durante un segundo y luego vibre durante un segundo.manifest.xmlEstablecer permisos en
                .build();
        manager.notify(1,notification);
    }
}