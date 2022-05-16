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


}