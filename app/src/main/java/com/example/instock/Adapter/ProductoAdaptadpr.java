package com.example.instock.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instock.ModificarProductosFragment;
import com.example.instock.R;

import java.util.List;

public class ProductoAdaptadpr extends  RecyclerView.Adapter<ProductoAdaptadpr.ViewHolder>{


    private List<Producto> ProductoList;
    private Context context;

    public ProductoAdaptadpr(List<Producto> productoList, Context context) {
        ProductoList = productoList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tarjeta_producto,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvProducto.setText(ProductoList.get(position).getNomProducto());
        holder.tvCategoria.setText(ProductoList.get(position).getCategoria());
        holder.tvCantidad.setText(ProductoList.get(position).getCantidad());
        holder.tvPrecio.setText(ProductoList.get(position).getPrecio());
    }
    @Override
    public int getItemCount() {
        return ProductoList.size();
    }

    public  static class ViewHolder extends RecyclerView.ViewHolder{


        private ImageView imgFoto;
        private TextView tvProducto;
        private TextView tvCategoria;
        private TextView tvCantidad;
        private TextView tvPrecio;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFoto = itemView.findViewById(R.id.imgProd);
            tvProducto = itemView.findViewById(R.id.tvNombreProductoVal);
            tvCategoria = itemView.findViewById(R.id.tvCategoriaVal);
            tvCantidad = itemView.findViewById(R.id.tvCantidadVal);
            tvPrecio = itemView.findViewById(R.id.tvPrecioVal);
        }
    }
}
