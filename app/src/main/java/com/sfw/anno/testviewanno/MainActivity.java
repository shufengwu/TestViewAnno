package com.sfw.anno.testviewanno;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.show_tv1)
    private TextView showTv1;
    @InjectView(R.id.show_tv2)
    private TextView showTv2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Anno.inject(this);
        Anno.onClickInject(this);
    }

    @OnClick({R.id.show_tv1,R.id.show_tv2})
    public void showOnClick(View view){
        switch (view.getId()){
            case R.id.show_tv1:
                Toast.makeText(MainActivity.this,"1",Toast.LENGTH_SHORT).show();

                break;
            case R.id.show_tv2:
                Toast.makeText(MainActivity.this,"2",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
