package Data;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.ArrayAdapter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class Archivo_Rutina {

    SharedPreferences mPrefs;

    public Archivo_Rutina(Context context) {
        mPrefs = context.getSharedPreferences("Rutinas", Context.MODE_PRIVATE);
    }

    public void Guardar(List<Rutina> items) {
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        int i = 0;
        if (items.size() != 0) {
            String json = gson.toJson(items);
            prefsEditor.putString("Rutinas", json);
        }else{
            prefsEditor.clear();
        }
        prefsEditor.commit();
    }

    public List<Rutina> leer() {
        Gson gson = new Gson();
        String json = mPrefs.getString("Rutinas", "");
        List<Rutina> Rutinas = gson.fromJson(json, new TypeToken<List<Rutina>>(){}.getType());
        return Rutinas;
    }
}
