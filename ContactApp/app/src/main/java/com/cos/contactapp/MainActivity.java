package com.cos.contactapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.POST;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity2";
    private RecyclerView rvPhone;
    private FloatingActionButton fabSave;
    private PhoneAdapter phoneAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvPhone = findViewById(R.id.rv_phone);
        fabSave = findViewById(R.id.fab_save);

        findAllPerson();
        fabSave.setOnClickListener(v -> {
            addPerson();
        });

    }
    private void findAllPerson(){
        PhoneService phoneService = PhoneService.retrofit.create(PhoneService.class);
        Call<CMRespDto<List<Phone>>> call = phoneService.findAll();

        call.enqueue(new Callback<CMRespDto<List<Phone>>>() {
            @Override
            public void onResponse(Call<CMRespDto<List<Phone>>> call, Response<CMRespDto<List<Phone>>> response) {
                CMRespDto<List<Phone>> cmRespDto = response.body();
                List<Phone> phones = cmRespDto.getData();

                //어댑터 넘기기
                LinearLayoutManager manager=new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL,false);

                rvPhone=findViewById(R.id.rv_phone);
                rvPhone.setLayoutManager(manager);

                phoneAdapter=new PhoneAdapter(phones);

                rvPhone.setAdapter(phoneAdapter);
                Log.d(TAG, "onResponse: phones : " + phones.toString());
            }

            @Override
            public void onFailure(Call<CMRespDto<List<Phone>>> call, Throwable t) {
                Log.d(TAG, "onFailure: findAll() 실패");
            }
        });
    }
    private void addAction(String name,String tel){
        PhoneService phoneService = PhoneService.retrofit.create(PhoneService.class);
        Phone phone=new Phone();
        phone.setName(name);
        phone.setTel(tel);

        Call<CMRespDto<Phone>> call=phoneService.save(phone);

        call.enqueue(new Callback<CMRespDto<Phone>>() {
            @Override
            public void onResponse(Call<CMRespDto<Phone>> call, Response<CMRespDto<Phone>> response) {
                Log.d(TAG, "onResponse: 저장 성공");
                findAllPerson();
            }

            @Override
            public void onFailure(Call<CMRespDto<Phone>> call, Throwable t) {
                Log.d(TAG, "onFailure: 저장 실패");
            }
        });
    }
    private void addPerson(){
        View dialogView= LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_add_contact,null);
        final EditText etName=dialogView.findViewById(R.id.et_name);
        final EditText etPhone=dialogView.findViewById(R.id.et_phone);

        AlertDialog.Builder dlg=new AlertDialog.Builder(MainActivity.this);
        dlg.setTitle("연락처 등록");
        dlg.setView(dialogView);
        dlg.setPositiveButton("등록", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name=etName.getText().toString();
                String tel=etPhone.getText().toString();
                Log.d(TAG, "onClick: name:"+name+" phone:"+tel);
                addAction(name,tel);
                findAllPerson();
            }
        });
        dlg.setNegativeButton("닫기",null);
        dlg.show();
    }
}