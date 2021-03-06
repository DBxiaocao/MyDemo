package me.xiaocao.demo;

import android.os.Bundle;
import android.view.View;

import me.xiaocao.demo.base.BaseActivity;
import me.xiaocao.demo.chart.MPChartActivity;
import me.xiaocao.demo.design.DesignActivity;
import me.xiaocao.demo.map.BaiduMapActivity;
import me.xiaocao.demo.marquee.MarqueeActivity;
import me.xiaocao.demo.pay.PayActivity;
import me.xiaocao.demo.signview.SgnatureActivity;
import me.xiaocao.demo.tabscroll.TabScrollActivity;
import me.xiaocao.demo.tagview.TagViewActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btnMarquee).setOnClickListener(view -> GoActivity(MarqueeActivity.class));
        findViewById(R.id.btnSgnature).setOnClickListener(view -> GoActivity(SgnatureActivity.class));
        findViewById(R.id.btnMap).setOnClickListener(view -> GoActivity(BaiduMapActivity.class));
        findViewById(R.id.btnMPChartLib).setOnClickListener(view -> GoActivity(MPChartActivity.class));
        findViewById(R.id.btnDesign).setOnClickListener(view -> GoActivity(DesignActivity.class));
        findViewById(R.id.btnPay).setOnClickListener(view -> GoActivity(PayActivity.class));
        findViewById(R.id.btnTagView).setOnClickListener(view -> GoActivity(TagViewActivity.class));
        findViewById(R.id.btnScroll).setOnClickListener(view -> GoActivity(TabScrollActivity.class));
    }


}
