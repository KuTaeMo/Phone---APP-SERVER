package com.cos.contactapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhoneAdapter extends RecyclerView.Adapter<PhoneAdapter.MyViewHolder> {

    private final List<Phone> phones;
    private static final String TAG = "PhoneAdapter";

    public PhoneAdapter(List<Phone> phones) {
        this.phones = phones;
    }

    public  void removeItem(int position){
        phones.remove(position);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.phone_item, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setItem(phones.get(position));

    }

    @Override
    public int getItemCount() {
        return phones.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView tvName, tvTel;
        private PhoneService phoneService;
        private Phone phone;
        private EditText etName,etTel;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.name);
            tvTel = itemView.findViewById(R.id.tel);
            phoneService = PhoneService.retrofit.create(PhoneService.class);

            itemView.setOnClickListener(v -> {
                View dialogView= LayoutInflater.from(v.getContext()).inflate(R.layout.layout_add_contact,null);
                int position = getAdapterPosition();
                phone = phones.get(position);

                etName = dialogView.findViewById(R.id.et_name);
                etTel = dialogView.findViewById(R.id.et_phone);

                etName.setText(tvName.getText());
                etTel.setText(tvTel.getText());

                AlertDialog.Builder dlg = new AlertDialog.Builder(v.getContext());
                dlg.setTitle("????????? ??????");
                dlg.setView(dialogView);
                dlg.setPositiveButton("??????",(dialogInterface, i) -> {
                    phone.setName(etName.getText().toString());
                    phone.setTel(etTel.getText().toString());
                    Call<CMRespDto<Phone>> call = phoneService.update(phone.getId(),phone);

                    call.enqueue(new Callback<CMRespDto<Phone>>() {
                        @Override
                        public void onResponse(Call<CMRespDto<Phone>> call, Response<CMRespDto<Phone>> response) {
                            Log.d(TAG, "onResponse: ?????? ?????? : ");
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(Call<CMRespDto<Phone>> call, Throwable t) {
                            Log.d(TAG, "onFailure: ?????? ??????");
                        }
                    });
                });
                dlg.setNegativeButton("??????", ((dialogInterface, i) -> {
                    Call<CMRespDto<String>> call = phoneService.delete(phone.getId());
                    call.enqueue(new Callback<CMRespDto<String>>() {
                        @Override
                        public void onResponse(Call<CMRespDto<String>> call, Response<CMRespDto<String>> response) {
                            Log.d(TAG, "onResponse: ?????? ?????? : ");
                            removeItem(position);
                        }

                        @Override
                        public void onFailure(Call<CMRespDto<String>> call, Throwable t) {
                            Log.d(TAG, "onResponse: ?????? ?????? : ");
                        }
                    });
                }));
                dlg.show();
            });

        }

        public void setItem(Phone phone){
            tvName.setText(phone.getName());
            tvTel.setText(phone.getTel());

        }
    }
}