package com.zx.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.zx.test.templinedetails.IsYaxisShow;
import com.zx.test.templinedetails.ShowYaxisView;
import com.zx.test.templinedetails.TempDetailsDataBean;
import com.zx.test.templinedetails.TempLineDetailsView;

public class MainActivity extends AppCompatActivity implements IsYaxisShow {

    TempLineDetailsView tempLineDetailsView; //温度曲线图
    ShowYaxisView showYaxisView; //温度刻度的y轴

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tempLineDetailsView = (TempLineDetailsView) findViewById(R.id.tempLineDetailsView);
        showYaxisView = (ShowYaxisView) findViewById(R.id.showYaxisView);
        tempLineDetailsView.setIsYaxisShow(this);

//        setData();

        findViewById(R.id.add_data_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setData();
            }
        });


    }

    private void setData() {
        String[] mTimes = new String[11];
        float[] mTemps = new float[11];
        mTimes[0] = "14:44";
        mTemps[0] = 32.4f;
        mTimes[1] = "14:44";
        mTemps[1] = 32.8f;
        mTimes[2] = "14:45";
        mTemps[2] = 33.2f;
        mTimes[3] = "14:45";
        mTemps[3] = 33.5f;
        mTimes[4] = "14:45";
        mTemps[4] = 33.7f;
        mTimes[5] = "14:45";
        mTemps[5] = 33.9f;
        mTimes[6] = "14:46";
        mTemps[6] = 34.2f;
        mTimes[7] = "14:46";
        mTemps[7] = 34.3f;
        mTimes[8] = "14:46";
        mTemps[8] = 34.5f;
        mTimes[9] = "14:46";
        mTemps[9] = 34.6f;
        mTimes[10] = "14:47";
        mTemps[10] = 34.8f;


        TempDetailsDataBean tempDetailsDataBean = new TempDetailsDataBean();
        tempDetailsDataBean.setTimeDates(mTimes);
        tempDetailsDataBean.setTempDates(mTemps);
        tempLineDetailsView.setTempData(tempDetailsDataBean);
    }

    @Override
    public void getStatus(boolean isShow) {
        if (isShow) {
            showYaxisView.setVisibility(View.VISIBLE);
        } else {
            showYaxisView.setVisibility(View.INVISIBLE);
        }
    }
}
