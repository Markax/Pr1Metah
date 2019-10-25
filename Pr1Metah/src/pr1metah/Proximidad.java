package com.example.sensoresmovil;

public class Proximidad {
    private double valorPromidad;
    private String fecha;

    public Proximidad(){
        setValorPromidad(0.0);
        setFecha("");

    }

    public Proximidad(double x, String f){
        setValorPromidad(x);
        setFecha(f);
    }

    public double getValorPromidad() {
        return valorPromidad;
    }

    public void setValorPromidad(double valorPromidad) {
        this.valorPromidad = valorPromidad;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
