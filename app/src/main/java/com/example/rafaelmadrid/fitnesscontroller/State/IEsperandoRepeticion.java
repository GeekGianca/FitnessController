package com.example.rafaelmadrid.fitnesscontroller.State;

public class IEsperandoRepeticion implements Estado {

    @Override
    public boolean enEjecucion() {
        return false;
    }

    @Override
    public int arrojarMensaje() {
        return 1;
    }
}
