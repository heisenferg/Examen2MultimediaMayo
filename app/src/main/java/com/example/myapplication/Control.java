package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;

public class Control {

    public boolean pulsado=false;
    public float xCoordenada, yCoordenada;
    private Bitmap imagen;
    private Context miContexto;
    public String nombre;


    public Control(Context miContexto, float xCoordenada, float yCoordenada) {
        this.miContexto = miContexto;
        this.xCoordenada = xCoordenada;
        this.yCoordenada = yCoordenada;
    }

    public int Ancho(){
        return imagen.getWidth();
    }

    public int Alto(){
        return imagen.getHeight();
    }

    public void Cargar(int recurso){
        imagen = BitmapFactory.decodeResource(miContexto.getResources(), recurso);
        imagen = imagen.createScaledBitmap(imagen,imagen.getWidth()/1,imagen.getWidth()/1,true);
    }

    public void Dibujar(Canvas c, Paint p){
        c.drawBitmap(imagen,xCoordenada, yCoordenada, p);
    }

    public void comprueba_Pulsado (int x, int y){
        if (x>xCoordenada && x<xCoordenada+Ancho() && y>yCoordenada && y<yCoordenada+Alto()){
            pulsado=true;
        }
    }

    public void compruebaSoltado(ArrayList<Toque> lista){
        boolean aux=false;
        for (Toque t: lista
             ) {
            if(t.x>xCoordenada && t.x<xCoordenada+Ancho() && t.y>yCoordenada && t.y<yCoordenada+Alto()){
                aux=true;
            }
        }
        if (!aux){
            pulsado=false;
        }
    }


}
