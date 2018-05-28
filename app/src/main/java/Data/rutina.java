package Data;

public class rutina {
    private int series;
    private int repeticiones;
    private String Nombre;


    public rutina() {
        series=0;
        repeticiones=0;
        Nombre="";

    }

    public int getSeries() {
        return series;
    }

    public void setSeries(int series) {
        this.series = series;
    }

    public int getRepeticiones() {
        return repeticiones;
    }

    public void setRepeticiones(int repeticiones) {
        this.repeticiones = repeticiones;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

}
