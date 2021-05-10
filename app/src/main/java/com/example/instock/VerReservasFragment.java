package com.example.instock;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.instock.models.Reserva;
import com.example.instock.Adapter.ReservasAdaptador;

import java.util.ArrayList;

public class VerReservasFragment extends Fragment {
    RecyclerView recyclerReservas;
    ReservasAdaptador reservasAdaptador;
    ArrayList<Reserva> ReservaList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private void cargarDatos() {

        ReservaList.add(new Reserva("Camisa verde","$13.50", "Maria Martinez"));
        ReservaList.add(new Reserva("Short Azul","$8.50", "Mario Gonzáles"));
        ReservaList.add(new Reserva("Zapatos de vestir","$25.75", "Rosa Rivera"));
        ReservaList.add(new Reserva("Collar","$2.50", "CR7 \"El Bicho\""));
        ReservaList.add(new Reserva("Audifonos","$7.00", "Juan Pérez"));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_ver_reservas, container, false);

        //Agregado
        ReservaList = new ArrayList<>();

        RecyclerView recyclerReservas = vista.findViewById(R.id.recyclerReservas);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerReservas.setLayoutManager(layoutManager);

        cargarDatos();
        reservasAdaptador = new ReservasAdaptador (ReservaList, getActivity());

        recyclerReservas.setAdapter(reservasAdaptador);

        cargarDatos();

        //Enlazamos el simpleItemTouchCallback con el recyclerProducto
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerReservas);

        return vista;
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
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

            //swipeDir = 4 Indica swipe hacia la izquierda
            //swipeDir = 8 Indica swipe hacia la derecha
            if(swipeDir == 4){
                //Eliminar item
                //Remove swiped item from list and notify the RecyclerView
                int position = viewHolder.getAdapterPosition();//Obtenemos la posición del item
                ReservaList.remove(position);//Removemos el item segun la posición
                reservasAdaptador.notifyDataSetChanged();//Notoficamos el cambio al Adaptador del RecyclerView

                Toast.makeText(getContext(), "¿Desea eliminar la reserva?", Toast.LENGTH_SHORT).show();

            }
            else if(swipeDir == 8){
                //Modificar item
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Fragment fModificarProductos = new ModificarProductosFragment();
                //Lo enviamos al Fragment de ModificarProductos
                transaction.replace(R.id.fragment_container_view, fModificarProductos);
                transaction.addToBackStack(null);
                transaction.commit();
            }

        }

    };
}