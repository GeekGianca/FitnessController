package com.example.rafaelmadrid.fitnesscontroller;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import Data.Rutina;

public class cardviewAdapter extends RecyclerView.Adapter<cardviewAdapter.cardviewholder> {
    private List<Rutina> items;
    private View dialogView;
    private AlertDialog builder;
    ViewGroup group;

    public static class cardviewholder extends RecyclerView.ViewHolder {

        private TextView Nombre;
        private TextView rep;
        private TextView ser;
        private Button editar;
        private Button eliminar;

        public cardviewholder(View v) {
            super(v);
            Nombre = v.findViewById(R.id.Nombre_card);
            rep = v.findViewById(R.id.Rep_card);
            ser = v.findViewById(R.id.Ser_card);
            editar = v.findViewById(R.id.editar_card);
            eliminar = v.findViewById(R.id.eliminar_card);
        }
    }


    public cardviewAdapter(List<Rutina> items) {
        this.items = items;
    }

    public void setItems(List<Rutina> items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }



    @Override
    public cardviewholder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_rutina, viewGroup, false);
        group=viewGroup;
        return new cardviewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final cardviewholder viewHolder, final int i) {
        viewHolder.Nombre.setText(items.get(i).getNombre());
        viewHolder.ser.setText("Series:"+String.valueOf(items.get(i).getSeries()));
        viewHolder.rep.setText("Repeticiones:"+String.valueOf(items.get(i).getRepeticiones()));
        viewHolder.eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                items.remove(items.get(i));
                cardviewAdapter.this.notifyItemRemoved(i);
            }
        });
        viewHolder.editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formulario(group,i);
                alertdialog(group,i);
            }
        });
    }

    public void formulario(ViewGroup viewGroup,int i) {
            dialogView=View.inflate(viewGroup.getContext(),R.layout.agregar_rutina,null);
            EditText a=dialogView.findViewById(R.id.agg_Nombre_rut);
            a.setText(items.get(i).getNombre());
            EditText b=dialogView.findViewById(R.id.agg_Numero_rep);
            b.setText(String.valueOf(items.get(i).getRepeticiones()));
            EditText c=dialogView.findViewById(R.id.agg_Series_rut);
            c.setText(String.valueOf(items.get(i).getSeries()));
    }

    public void alertdialog(ViewGroup viewGroup, final int i){
        String titulo="Editar Rutina";
        final AlertDialog.Builder Alertdialog = new AlertDialog.Builder(viewGroup.getContext());
        Alertdialog.setView(dialogView)
                .setTitle(titulo)
                .setPositiveButton("Aceptar",null)
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder=Alertdialog.create();
        builder.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button b=builder.getButton(DialogInterface.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        check_out(items.get(i),i);
                    }
                });
            }
        });
        builder.show();
    }

    public void check_out(Rutina Rutina, int pos) {
        int i = 0;
        String Nombre = Rutina.getNombre();
        int series = Rutina.getSeries();
        int rep = Rutina.getRepeticiones();
        EditText campo = null;
        do {
            switch (i) {
                case 0:
                    campo = dialogView.findViewById(R.id.agg_Nombre_rut);
                    break;
                case 1:
                    campo = dialogView.findViewById(R.id.agg_Numero_rep);
                    break;
                case 2:
                    campo = dialogView.findViewById(R.id.agg_Series_rut);
                    break;
            }
            if (!campo.getText().toString().isEmpty()) {
                switch (i) {
                    case 0:
                        Nombre = campo.getText().toString();
                        break;
                    case 1:
                        rep = Integer.parseInt(campo.getText().toString());
                        break;
                    case 2:
                        series = Integer.parseInt(campo.getText().toString());
                        break;
                }
            }
            i++;
        }while (i < 3) ;
        Rutina.setNombre(Nombre);
        Rutina.setSeries(series);
        Rutina.setRepeticiones(rep);
        items.set(pos, Rutina);
        builder.dismiss();
        notifyDataSetChanged();
    }

}




