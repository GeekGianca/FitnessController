package com.example.rafaelmadrid.fitnesscontroller;

public class DispositivosEmparejados {
    private String mac;
    private String nombre;

    public DispositivosEmparejados(String mac, String nombre) {
        this.mac = mac;
        this.nombre = nombre;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
