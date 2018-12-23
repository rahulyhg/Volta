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
import com.jmjsolution.solarup.interfaces.DeletePack;
import com.jmjsolution.solarup.model.Pack;

import java.util.ArrayList;

public class PackAdapter extends RecyclerView.Adapter<PackAdapter.PackViewHolder> {


    Context mContext;
    ArrayList<Pack> mPacks;
    DeletePack mDeletePack;

    public PackAdapter(Context context, ArrayList<Pack> packs, DeletePack deletePack){
        mContext = context;
        mPacks = packs;
        mDeletePack = deletePack;
    }

    @NonNull
    @Override
    public PackViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pack_item, viewGroup, false);
        return new PackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PackViewHolder packViewHolder, int i) {
        packViewHolder.mDeletePackIv.setTag(i);
        packViewHolder.bindPack(i);
    }

    @Override
    public int getItemCount() {
        return mPacks.size();
    }

    public class PackViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mNamePack, mDescriptifPack, mPricePack;
        private ImageView mDeletePackIv;

        public PackViewHolder(@NonNull View itemView) {
            super(itemView);

            mNamePack = itemView.findViewById(R.id.namePackTv);
            mDescriptifPack = itemView.findViewById(R.id.descriptifPackTv);
            mPricePack = itemView.findViewById(R.id.pricePackTv);
            mDeletePackIv = itemView.findViewById(R.id.deletePackIv);

            mDeletePackIv.setOnClickListener(this);
        }

        public void bindPack(int i) {
            mNamePack.setText(mPacks.get(i).getNom());
            StringBuilder stringBuilder = new StringBuilder();
            for(int y = 0; y < mPacks.get(i).getDescriptif().size(); y++){
                if(y + 1 != mPacks.get(i).getDescriptif().size()){
                    stringBuilder.append(mPacks.get(i).getDescriptif().get(y)).append(", ");
                } else {
                    stringBuilder.append(mPacks.get(i).getDescriptif().get(y)).append(".");
                }

            }
            mDescriptifPack.setText(stringBuilder.toString());
            mPricePack.setText(String.valueOf(mPacks.get(i).getPrix() + "€"));
        }

        @Override
        public void onClick(View view) {
            if(view == mDeletePackIv) {
                showDeleteDialog((Integer) view.getTag());
            }
        }

        private void showDeleteDialog(final int position){
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Supprimer");
            builder.setMessage("Etes vous sur de voulour supprimer ce pack ?");
            builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mDeletePack.onDeletePack(position);
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
