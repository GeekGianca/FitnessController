package com.example.rafaelmadrid.fitnesscontroller.State;

public class Repeticion {
    private Estado estado;
    public boolean repeticion;
    public int waveView;

    public Repeticion() {
        this.estado = null;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public void antesDeEjecutarse(){
        repeticion = estado.enEjecucion();
        waveView = estado.arrojarMensaje();
    }
}
