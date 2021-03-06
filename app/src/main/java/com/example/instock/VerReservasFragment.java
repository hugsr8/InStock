package com.example.instock;

import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.instock.BD.ReservasManagerDB;
import com.example.instock.models.ModalDialogValues;
import com.example.instock.models.Reserva;
import com.example.instock.Adapter.ReservasAdaptador;
import com.example.instock.utils.CreateDialog;
import com.example.instock.utils.Utils;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class VerReservasFragment extends Fragment {

    private View vista;

    TextInputLayout tilBuscarCorreo;
    EditText edtBuscarCorreo;

    RecyclerView recyclerReservas;
    ReservasAdaptador reservasAdaptador;
    ArrayList<Reserva> ReservaList;
    Reserva reserva;

    //Variable que almacena la posición del item al que se le hizo "swipe"
    private int  recyclerPositionItem;

    //Objeto de MyDialog
    CreateDialog createDialog = new CreateDialog();
    private ModalDialogValues modalDialogValues = ModalDialogValues.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.fragment_ver_reservas, container, false);
        cargarDatosLike("");
        tilBuscarCorreo = (TextInputLayout)vista.findViewById(R.id.tilBuscarCorreo);
        edtBuscarCorreo = (EditText)vista.findViewById(R.id.edtBuscarCorreo);
        edtChangedListener();
        return vista;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        edtBuscarCorreo.setText(null);
        cargarDatosLike("");
        super.onViewStateRestored(savedInstanceState);
    }

    private void cargarDatosLike(String correo) {

        ReservasManagerDB reservasManagerDB = new ReservasManagerDB(getContext());

        ReservaList = reservasManagerDB.obtenerReservasLike(correo);

        recyclerReservas = vista.findViewById(R.id.recyclerReservas);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerReservas.setLayoutManager(layoutManager);

        //Enlazamos el simpleItemTouchCallback con el recyclerProducto
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerReservas);

        reservasAdaptador = new ReservasAdaptador (ReservaList, getActivity());

        recyclerReservas.setAdapter(reservasAdaptador);
    }

    //Método para enlazar los editText con el ChangedListener
    private void edtChangedListener(){

        edtBuscarCorreo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 1){
                    cargarDatosLike(s.toString());
                }
                else{
                    cargarDatosLike("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    //Objeto de tipo ItemTouchHelper que permite realizar el swipe
    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

        //Método para cambiar el color del Background del Item según la dirección del "swipe"
        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                // Get RecyclerView item from the ViewHolder
                View itemView = viewHolder.itemView;

                Paint p = new Paint();
                if (dX > 0) {
                    /* Set your color for positive displacement */
                    p.setColor(getResources().getColor(R.color.azul_marino));

                    // Draw Rect with varying right side, equal to displacement dX
                    c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX,
                            (float) itemView.getBottom(), p);
                } else {
                    /* Set your color for negative displacement */
                    p.setColor(getResources().getColor(R.color.rojo));

                    // Draw Rect with varying left side, equal to the item's right side plus negative displacement dX
                    c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                            (float) itemView.getRight(), (float) itemView.getBottom(), p);
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }

        //Método obligatorio para implementar el ItemTouchHelper.SimpleCallback()
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            Toast.makeText(getContext(), "on Move", Toast.LENGTH_SHORT).show();
            return false;
        }

        //Método que realiza acciones cuando se hace el swipe
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

            //swipeDir = 4 Indica swipe hacia la izquierda
            //swipeDir = 8 Indica swipe hacia la derecha
            if(swipeDir == 4){
                //Cancelar reserva
                recyclerPositionItem = viewHolder.getAdapterPosition();
                cancelarReserva(recyclerPositionItem);
            }
            else if(swipeDir == 8){
                //Convertir venta en reserva
                recyclerPositionItem = viewHolder.getAdapterPosition();
                convertirAVenta(recyclerPositionItem);
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void cancelarReserva(int recyclerPositionItem){
        //Asignamos los valores para mostrar el Dialog
        modalDialogValues.modalDialogValues("Cancelar reserva",
                "¿Estás seguro que deseas cancelar esta reserva?\n\nEl producto de la reserva" +
                        " aparecerá como disponible.");

        //Invocamos el dialog() y sobreescribimos sus metodos setPositiveButton y setNegativeButton
        createDialog.dialog(getContext()).setPositiveButton(null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                ReservasManagerDB reservasManagerDB = new ReservasManagerDB(getContext());
                int idReserva = Integer.parseInt(ReservaList.get(recyclerPositionItem).getIdReserva());
                int resultado = reservasManagerDB.cancelarReserva(idReserva);

                if(resultado > 0){
                    ReservaList.remove(recyclerPositionItem);//Removemos el item segun la posición
                    reservasAdaptador.notifyDataSetChanged();//Notoficamos el cambio al Adaptador del RecyclerView
                    Toast.makeText(getContext(), "Reserva cancelada", Toast.LENGTH_SHORT).show();
                } else{
                    reservasAdaptador.notifyDataSetChanged();//Notoficamos el cambio al Adaptador del RecyclerView
                    Toast.makeText(getContext(), "No se pudo cancelar la reserva", Toast.LENGTH_SHORT).show();
                }

            }
        }).setNegativeButton(null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                reservasAdaptador.notifyDataSetChanged();

            }
        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                reservasAdaptador.notifyDataSetChanged();
            }
        }).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void convertirAVenta(int recyclerPositionItem){
        modalDialogValues.modalDialogValues("Reserva a Venta",
                "¿Estás seguro que deseas convertir esta reserva en venta?");

        //Invocamos el dialog() y sobreescribimos sus metodos setPositiveButton y setNegativeButton
        createDialog.dialog(getContext()).setPositiveButton(null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                ReservasManagerDB reservasManagerDB = new ReservasManagerDB(getContext());
                int idReserva = Integer.parseInt(ReservaList.get(recyclerPositionItem).getIdReserva());
                String fechaActual = new Utils().getFechaActual();//Obtenemos la fecha actual

                //Convertimos la Reserva en Venta
                int resultado = reservasManagerDB.convertirReservaEnVenta(idReserva, fechaActual);

                if(resultado > 0){
                    ReservaList.remove(recyclerPositionItem);//Removemos el item segun la posición
                    reservasAdaptador.notifyDataSetChanged();//Notoficamos el cambio al Adaptador del RecyclerView
                    Toast.makeText(getContext(), "La Reserva se convirtió en Venta", Toast.LENGTH_SHORT).show();
                } else {
                    reservasAdaptador.notifyDataSetChanged();//Notoficamos el cambio al Adaptador del RecyclerView
                    Toast.makeText(getContext(), "No se pudo convertir en Venta", Toast.LENGTH_SHORT).show();
                }

            }
        }).setNegativeButton(null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                reservasAdaptador.notifyDataSetChanged();

            }
        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                reservasAdaptador.notifyDataSetChanged();
            }
        }).show();
    }
}