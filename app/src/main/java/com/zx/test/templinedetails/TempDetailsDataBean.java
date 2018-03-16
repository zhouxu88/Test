package com.zx.test.templinedetails;

/**
 * Created by zhouxu on 2018/2/10.
 */

public class TempDetailsDataBean {
    private String[] timeDates; //时间的数组
    private float[] tempDates; //温度的数组

    public String[] getTimeDates() {
        return timeDates;
    }

    public void setTimeDates(String[] timeDates) {
        this.timeDates = timeDates;
    }

    public float[] getTempDates() {
        return tempDates;
    }

    public void setTempDates(float[] tempDates) {
        this.tempDates = tempDates;
    }
}
