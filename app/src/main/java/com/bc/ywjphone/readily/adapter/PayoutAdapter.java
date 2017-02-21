package com.bc.ywjphone.readily.adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bc.ywjphone.readily.R;
import com.bc.ywjphone.readily.adapter.base.SimpleBaseAdapter;
import com.bc.ywjphone.readily.business.PayoutBusiness;
import com.bc.ywjphone.readily.business.UserBusiness;
import com.bc.ywjphone.readily.entity.Payout;
import com.bc.ywjphone.readily.utils.DataUtils;
import com.bc.ywjphone.readily.utils.DateUtil;

import java.util.List;

public class PayoutAdapter extends SimpleBaseAdapter {
    private UserBusiness userBusiness;
    private PayoutBusiness payoutBusiness;
    private int accountBookId;

    public PayoutAdapter(Context context, int accountBookId) {
        super(context, null);
        payoutBusiness = new PayoutBusiness(context);
        userBusiness = new UserBusiness(context);
        this.accountBookId = accountBookId;
        setListFromBusiess();
    }

    public void updateList() {
        setListFromBusiess();
        updateDisplay();
    }

    private void setListFromBusiess() {
        //根据账本Id查询消费记录，注意排序
        List<Payout> list = payoutBusiness.getPayoutListByAccountBookId(accountBookId);
        setList(list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.payout_list_item,
                    null);
            holder = new Holder();
            holder.payout_item_icon_iv = (ImageView) convertView
                    .findViewById(R.id.payout_item_icon_iv);
            holder.payout_item_name_tv = (TextView) convertView
                    .findViewById(R.id.payout_item_name_tv);
            holder.payout_item_amount_tv=(TextView) convertView
                    .findViewById(R.id.payout_item_amount_tv);
            holder.payout_item_user_and_type_tv=(TextView) convertView
                    .findViewById(R.id.payout_item_user_and_type_tv);
            holder.payout_item_time_rl=(View) convertView
                    .findViewById(R.id.payout_list_item_rl);
            convertView.setTag(holder);

        } else {
            holder = (Holder) convertView.getTag();
        }

        Payout payout= (Payout) getItem(position);


        Log.e("TAG", "这个对象======"+payout);


        String payoutDate= DateUtil.getFormattedString(payout.getPayoutDate(),"yyyy-MM-dd");
        boolean isShow=false;
        if(position>0) {
            //获取上一个实体
            Payout payoutLast=(Payout) getItem(position-1);
            //获取上一个实体的日期
            String payoutDateLast= DataUtils.getFormatDate(payoutLast.getPayoutDate());
            //如果当前日期与上一个实体的日期不等，就显示
            isShow=!payoutDate.equals(payoutDateLast);
        }

        Log.e("TAG", "要用来比较的日期是====="+payoutDate);
        //要求用一条语句，查出两个结果，使用sql内置函数，不许list.size(),sum,count
        List msg=payoutBusiness.getPayoutTotalMessage(payoutDate,accountBookId);

        if(isShow||position==0) {
            holder.payout_item_time_rl.setVisibility(View.VISIBLE);
        }
        //设置蓝条文字
        ((TextView)holder.payout_item_time_rl.findViewById(R.id.payout_item_time_tv))
                .setText(payoutDate);
        ((TextView)holder.payout_item_time_rl.findViewById(R.id.payout_item_total_tv))
                .setText("共"+msg.get(1)+"笔，一共消费"+msg.get(0)+"元");
        holder.payout_item_icon_iv.setImageResource(R.drawable.grid_payout);
        holder.payout_item_name_tv.setText(payout.getCategoryName());
        holder.payout_item_amount_tv.setText(context.getString(R.string.textview_text_payout_amount,
                new Object[]{payout.getAmount()}) );
        Log.e("TAG", "userBusiness===="+userBusiness);
        Log.e("TAG", "payout======="+payout.getPayoutUserId());
        String userName=userBusiness.getUserNameByUserId(payout.getPayoutUserId());
        holder.payout_item_user_and_type_tv.setText(userName+" "+payout.getPayoutType());

        return convertView;
    }

    class Holder {
        ImageView payout_item_icon_iv;
        TextView payout_item_name_tv, payout_item_amount_tv;
        TextView  payout_item_user_and_type_tv;
        View  payout_item_time_rl;
    }
}


