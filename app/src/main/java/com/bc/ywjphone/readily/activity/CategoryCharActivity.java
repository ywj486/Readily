package com.bc.ywjphone.readily.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.bc.ywjphone.readily.R;
import com.bc.ywjphone.readily.activity.base.FrameActivity;
import com.bc.ywjphone.readily.entity.CategoryTotal;

import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import java.util.List;

/**
 * Created by Administrator on 2016/12/29 0029.
 */
public class CategoryCharActivity extends FrameActivity {
    private List<CategoryTotal> categoryTotalList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariable();
        View pie_view = categoryStatistics();
        appendMainBody(pie_view);
        removeBottomBox();//移除底部菜单
        setTitle();
    }

    private void setTitle() {
        String title = getString(R.string.catetory_total);
        setTopBarTitle(title);
    }

    //初始化变量
    private void initVariable() {
        //反序列化
        categoryTotalList = (List<CategoryTotal>) getIntent().getSerializableExtra("total");
    }

    //类别统计
    private View categoryStatistics() {
        int[] color = new int[]{Color.parseColor("#FF5552"),
                Color.parseColor("#2A94F1"),
                Color.parseColor("#F12792"),
                Color.parseColor("#FFFF52"),
                Color.parseColor("#84D911"),
                Color.parseColor("#5255FF")};
        //调用自定义的构建渲染器方法得到一个渲染器
        DefaultRenderer defaultRenderer = buildCategoryRenderer(color);
        //获取数据源
        CategorySeries categorySeries = buildCategoryDataset("测试饼图", categoryTotalList);
        //获取饼图的view
        //上下文，数据源，渲染器
        View pie_view = ChartFactory.getPieChartView(this, categorySeries, defaultRenderer);
        return pie_view;
    }

    //构建渲染器
    private DefaultRenderer buildCategoryRenderer(int[] colors) {
        DefaultRenderer renderer = new DefaultRenderer();
        //显示缩放按钮，默认不显示
        renderer.setZoomButtonsVisible(true);
        //设置图表标题的文字大小
        renderer.setChartTitleTextSize(30);
        //设置图表的标题，默认居中顶部显示
        renderer.setChartTitle(getString(R.string.payout_category_statistics));
        //设置介绍说明文字的大小
        renderer.setLegendTextSize(15);
        //设置标签文字的大小
        renderer.setLabelsColor(18);
        //设置标签文字的颜色
        renderer.setLabelsColor(Color.BLUE);

        int color = 0;//颜色下标从0开始
        for (int i = 0; i < categoryTotalList.size(); i++) {
            //初始化一个简单序列渲染器
            SimpleSeriesRenderer ssr = new SimpleSeriesRenderer();
            //设置扇瓣的颜色
            ssr.setColor(colors[color]);
            //把扇瓣添加到渲染器中
            renderer.addSeriesRenderer(ssr);
            color++;
            //类别多，超过颜色数组，就让颜色从头开始循环
            if (color >= colors.length) {
                color = 0;
            }
        }
        return renderer;
    }

    //构建数据源
    private CategorySeries buildCategoryDataset(String title, List<CategoryTotal> values) {
        CategorySeries categorySeries = new CategorySeries(title);
        for (CategoryTotal value : values) {
            categorySeries.add(value.categoryName+":"+value.count+"条\r\n合计:"
            +value.sumAmount+"元",Double.parseDouble(value.sumAmount));
        }
        return categorySeries;
    }
}
