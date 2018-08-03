package com.example.gregoire.datasetbuilder;

import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Button takePicture = findViewById(R.id.button_label_chosen);
    takePicture.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String nameClass = ((EditText) findViewById(R.id.name_class)).getText().toString();

        Intent intent = new Intent(MainActivity.this, CameraActivity.class);
        intent.putExtra("class_name", nameClass);
        startActivity(intent);
      }
    });
  }
}
