package com.ranajeet2205.qrcodegenscan;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    QRCodeWriter qrCodeWriter;
    BitMatrix bitMatrix;
    String title;
    int width,height;
    Bitmap bitmap;
    ImageView imageView;
    Button button;
    //qr code scanner object
    private IntentIntegrator qrScan;
    TextView name_tv,address_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.image_view);
        name_tv = findViewById(R.id.name_tv);
        address_tv = findViewById(R.id.adress_txt);
        button = findViewById(R.id.scan);

        qrCodeWriter = new QRCodeWriter();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Name","Ranajeet");
            jsonObject.put("Address","OUAT");
            title = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        try {
            bitMatrix = qrCodeWriter.encode(title, BarcodeFormat.QR_CODE,512,512);
            width = bitMatrix.getWidth();
            height = bitMatrix.getHeight();
            bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.RGB_565);

            for (int i=0;i<width;i++){
                for (int j=0;j<height;j++){
                    int color;
                    if (bitMatrix.get(i,j)){
                        color = Color.BLACK;
                    }else{
                        color = Color.WHITE;
                    }
                    bitmap.setPixel(i, j, color);
                }
            }

            imageView.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrScan = new IntentIntegrator(MainActivity.this);
                qrScan.setOrientationLocked(false);
                qrScan.initiateScan();
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    //converting the data to json
                    JSONObject obj = new JSONObject(result.getContents());
                    //setting values to textviews
                    name_tv.setText(obj.getString("Name"));
                    address_tv.setText(obj.getString("Address"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
