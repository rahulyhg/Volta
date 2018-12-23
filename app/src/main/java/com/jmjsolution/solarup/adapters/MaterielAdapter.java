package com.jmjsolution.solarup.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jmjsolution.solarup.R;
import com.jmjsolution.solarup.interfaces.DeleteMaterial;
import com.jmjsolution.solarup.model.Materiel;

import java.util.ArrayList;

public class MaterielAdapter extends RecyclerView.Adapter<MaterielAdapter.MaterielViewHolder> {

    private Context mContext;
    private ArrayList<Materiel> mMateriels;
    private DeleteMaterial mDeleteMaterial;

    public MaterielAdapter(Context context, ArrayList<Materiel> materiels, DeleteMaterial deleteMaterial){
        mContext = context;
        mMateriels = materiels;
        mDeleteMaterial = deleteMaterial;
    }

    @NonNull
    @Override
    public MaterielViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.materiel_item, viewGroup, false);
        return new MaterielViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MaterielViewHolder materielViewHolder, int i) {
        materielViewHolder.bindMaterial(i);
        materielViewHolder.mDeleteMaterialIv.setTag(i);
    }

    @Override
    public int getItemCount() {
        return mMateriels.size();
    }

    public class MaterielViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mPriceMaterialTv, mNameMaterialTv;
        ImageView mDeleteMaterialIv;

        public MaterielViewHolder(@NonNull View itemView) {
            super(itemView);

            mPriceMaterialTv = itemView.findViewById(R.id.priceMaterialTv);
            mNameMaterialTv = itemView.findViewById(R.id.nameMaterialTv);
            mDeleteMaterialIv = itemView.findViewById(R.id.deleteMaterialIv);

            mDeleteMaterialIv.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view == mDeleteMaterialIv){
                showDeleteDialog((Integer) view.getTag());
            }
        }

        public void bindMaterial(int i) {
            mPriceMaterialTv.setText(String.valueOf(mMateriels.get(i).getPrix() + "€"));
            mNameMaterialTv.setText(mMateriels.get(i).getNom() + " : ");
        }

        private void showDeleteDialog(final int position){
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Supprimer");
            builder.setMessage("Etes vous sur de voulour supprimer ce pack ?");
            builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mDeleteMaterial.onDeleteMaterial(position);
                }
            });
            builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
    }
}
