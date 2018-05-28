package com.example.rafaelmadrid.fitnesscontroller.State;

public class IRepeticionCompleta implements Estado {
    @Override
    public boolean enEjecucion() {
        return true;
    }

    @Override
    public int arrojarMensaje() {
        return 1;
    }
}
