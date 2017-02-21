package com.bc.ywjphone.readily.business;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.bc.ywjphone.readily.R;
import com.bc.ywjphone.readily.business.base.BaseBusiness;
import com.bc.ywjphone.readily.entity.Payout;
import com.bc.ywjphone.readily.entity.Statistics;
import com.bc.ywjphone.readily.utils.DateUtil;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.NumberFormat;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * Created by Administrator on 2016/12/28 0028.
 */
public class StatisticsBusiness extends BaseBusiness {
    //sdcard的保存路径
    public static final String SDCARD_PATH= Environment.getExternalStorageDirectory().getPath()
            +"/Readily/Export/";
    private PayoutBusiness payoutBusiness;
    private UserBusiness userBusiness;
    private AccountBookBusiness accountBookBusiness;

    public StatisticsBusiness(Context context) {
        super(context);
        payoutBusiness = new PayoutBusiness(context);
        userBusiness = new UserBusiness(context);
        accountBookBusiness = new AccountBookBusiness(context);
    }

    //得到一个拆分好的集合
    private List<Statistics> getStatisticsList(String condition) {
        //按照付款人的Id排序取出消费记录
        List<Payout> payoutList = payoutBusiness.getPayoutOrderByPayoutUserId(condition);
        //获取计算方式的数组
        String[] payoutTypeArray = context.getResources().getStringArray(R.array.payoutType);
        List<Statistics> statisticsList = new ArrayList<>();
        if (payoutList != null) {
            //遍历消费记录列表
            for (int i = 0; i < payoutList.size(); i++) {
                //取出一条消费记录
                Payout payout = payoutList.get(i);
                //将消费人Id返回一个消费人姓名
                String[] payoutUserName = userBusiness.getUserNameByUserId(payout.getPayoutUserId()).split(",");
                String[] payoutUserId = payout.getPayoutUserId().split(",");
                //取出当前的消费记录的计算方式
                String payoutType = payout.getPayoutType();
                //存放计算后金额
                BigDecimal cost;
                //判断本次消费的类型
                if (payoutType.equals(payoutTypeArray[0])) {//均分
                    //得到消费人数
                    int payoutToal = payoutUserName.length;
                    /**
                     * 得到消费后的平均消费金额
                     *divide 表示除法
                     * 2 表示精确到小数点后二位
                     * BigDecimal.ROUND_HALF_EVEN 表示四舍五入
                     */
                    cost = payout.getAmount().divide(new BigDecimal(payoutToal),
                            2, BigDecimal.ROUND_HALF_EVEN);
                } else {//借贷或个人消费
                    cost = payout.getAmount();//直接取出
                }
                //遍历这条消费记录所有消费人的数组
                for (int j = 0; j < payoutUserId.length; j++) {
                    //如果是借贷的就跳过第一份索引，因为第一个人是消费人自己
                    //j==0表示是自己
                    if (payoutType.equals(payoutTypeArray[1]) && j == 0) {
                        continue;
                    }
                    //声明一个统计类
                    Statistics statistics = new Statistics();
                    //将统计类的付款人设置成消费人数组的第一个人
                    statistics.payoutUserId = payoutUserName[0];
                    //设置消费人
                    statistics.consumerUserId = payoutUserName[j];
                    //设置消费类型
                    statistics.payoutType = payoutType;
                    //设置算好的金额
                    statistics.count = cost;
                    //添加到集合里边
                    statisticsList.add(statistics);

                }
            }
        }
        return statisticsList;
    }

    //得到总统计结果的集合
    public List<Statistics> getPayoutUserId(String condition) {
        //得到拆分好的统计信息
        List<Statistics> list = getStatisticsList(condition);
        //存放付款人分类的临时统计信息
        List<Statistics> listTemp = new ArrayList<>();
        //存放统计好的汇总
        List<Statistics> totalList = new ArrayList<>();
        String result = "";
        //遍历统计好的消费信息
        for (int i = 0; i < list.size(); i++) {
            //得到拆分好的一条信息
            Statistics statistics = list.get(i);
            result += statistics.payoutUserId + "#" + statistics.consumerUserId +
                    "#" + statistics.count + "\r\n";
            //保存当前付款人的Id
            String currentPayoutUserId = statistics.payoutUserId;
            //把当前信息按付款人分类的临时数据
            Statistics statisticsTemp = new Statistics();
            statisticsTemp.payoutUserId = statistics.payoutUserId;
            statisticsTemp.consumerUserId = statistics.consumerUserId;
            statisticsTemp.count = statistics.count;
            statisticsTemp.payoutType = statistics.payoutType;
            listTemp.add(statisticsTemp);

            //计算下一行的索引
            int nextIndex;
            //如果下一行索引小于统计信息的索引，则可以+1
            if ((i + 1) < list.size()) {
                nextIndex = i + 1;
            } else {//否则证明已经到尾，就赋值给当前行
                nextIndex = i;
            }
            //如果当前付款人与下一位付款人不同，则证明已经到尾，或者已经循环到了数组的最后一位
            if (!currentPayoutUserId.equals(list.get(nextIndex).payoutUserId) || nextIndex == i) {
                //进行当前分类统计数组的统计
                for (int j = 0; j < listTemp.size(); j++) {
                    Statistics statisticsTotal = listTemp.get(j);
                    //判断在总统计数组当中是否已经存在该付款人和消费人的信息
                    int index = getPositionByConsumerUserId(totalList, statisticsTotal.payoutUserId,
                            statisticsTotal.consumerUserId);
                    //如果已经存在,则累加,add表示加法
                    if (index != -1) {
                        totalList.get(index).count = totalList.get(index).count.add(statisticsTotal.count);
                    } else {
                        //否则就是一条新消息，添加到统计数组当中
                        totalList.add(statisticsTotal);
                    }
                }
                //清空临时统计数组
                listTemp.clear();
            }
        }
        return totalList;
    }

    //判断在总统计数组当中是否存在该付款人和消费人信息
    private int getPositionByConsumerUserId(List<Statistics> totalList, String payoutUserId, String consumerUserId) {
        int index = -1;
        for (int i = 0; i < totalList.size(); i++) {
            if (totalList.get(i).payoutUserId.equals(payoutUserId) && totalList
                    .get(i).consumerUserId.equals(consumerUserId)) {
                index = i;
                break;
            }
        }
        return index;
    }

    public String getPayoutUserIdByAccountBookId(int accountBookId) {
        String result = "";
        //得到一个总统计结果的集合
        List<Statistics> totalList = getPayoutUserId(" and accountBookId="
                + accountBookId);
        //将得到的信息进行转换，以方便观看
        for (int i = 0; i < totalList.size(); i++) {
            Statistics statistics = totalList.get(i);
            if ("个人".equals(statistics.payoutType)) {
                result += statistics.payoutUserId + "个人消费" +
                        statistics.count.toString() + "元\r\n";
            } else if ("均分".equals(statistics.payoutType)) {
                if (statistics.payoutUserId.equals(statistics.consumerUserId)) {
                    result += statistics.payoutUserId + "个人消费" + statistics.count.toString() + "元\r\n";
                } else {
                    result += statistics.consumerUserId + "应支付给" + statistics.payoutUserId +
                            statistics.count.toString() + "元\r\n";
                }
            } else if ("借贷".equals(statistics.payoutType)) {
                result += statistics.consumerUserId + "应支付给" + statistics.payoutUserId +
                        statistics.count.toString() + "元\r\n";
            }
        }
        return result;
    }

    public String exportStatistics(int accountBookId) throws Exception{
        String result="";
        //判断是否有外存储设备
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //根据账本ID取出账本名称
            String accountBookName=accountBookBusiness.getAccountBookNameByAccountId(accountBookId);
            String fileName=accountBookName+ DateUtil.getTFormatString("yyyyMMdd")+".xls";
            Log.e("TAG",SDCARD_PATH+fileName);
            File fileDir=new File(SDCARD_PATH);
            if(!fileDir.exists()) {
                fileDir.mkdirs();
            }
            File file=new File(SDCARD_PATH+fileName);
            if(!file.exists()) {
                file.createNewFile();
            }
            //声明一个可写的表对象
            WritableWorkbook workBookData;
            //创建工具薄。需要告诉他往哪个文件里面创建
            workBookData= Workbook.createWorkbook(file);
            //创建工作表
            WritableSheet wsAccountBook=workBookData.createSheet(accountBookName,0);
            //声明表头数据
            String[] titles={"编号","姓名","金额","消费信息","消费类型"};
            //声明一个文本标签
            Label lable;
            //添加标题行
            for(int i = 0; i <titles.length ; i++) {
                //列，行，内容
              lable=new Label(i,0,titles[i]);
                //将文本标签填入一个单元格
                wsAccountBook.addCell(lable);
            }
            //添加行
            List<Statistics> totalList=getPayoutUserId(" and accountBookId="+accountBookId);
            for(int i = 0; i < totalList.size(); i++) {
              Statistics statistics=totalList.get(i);
                //添加标号列
                //number导入的包是jxl的
                Number idCell=new Number(0,i+1,i+1);
                wsAccountBook.addCell(idCell);
                //添加姓名
                Label nameLable=new Label(1,i+1,statistics.payoutUserId);
                wsAccountBook.addCell(nameLable);
                //格式化金额类型显示 #.##表示格式化小数点后两位
                //注意导入的包 jxl
                NumberFormat moneyFormat=new NumberFormat("#.##");
                WritableCellFormat wcf=new WritableCellFormat(moneyFormat);
                //添加金额
                Number costCell=new Number(2,i+1,statistics.count.doubleValue(),wcf);
                wsAccountBook.addCell(costCell);
                //添加消费信息
                String info= "";
                    if ("个人".equals(statistics.payoutType)) {
                        info += statistics.payoutUserId + "个人消费" +
                                statistics.count.toString() + "元\r\n";
                    } else if ("均分".equals(statistics.payoutType)) {
                        if (statistics.payoutUserId.equals(statistics.consumerUserId)) {
                            info += statistics.payoutUserId + "个人消费" + statistics.count.toString() + "元\r\n";
                        } else {
                            info += statistics.consumerUserId + "应支付给" + statistics.payoutUserId +
                                    statistics.count.toString() + "元\r\n";
                        }
                    } else if ("借贷".equals(statistics.payoutType)) {
                        info += statistics.consumerUserId + "应支付给" + statistics.payoutUserId +
                                statistics.count.toString() + "元\r\n";
                    }
                Label infoLabel=new Label(3,i+1,info);
                wsAccountBook.addCell(infoLabel);
                //添加消费类型
                Label payoutTypeLabel=new Label(4,i+1,statistics.payoutType);
                wsAccountBook.addCell(payoutTypeLabel);
            }
            //写入sd卡
            workBookData.write();
            workBookData.close();
            result="数据已经导出，位置在"+SDCARD_PATH+fileName;
        }else{
            result="抱歉，未检测出SD卡,数据无法导出";
        }
        return result;
    }
}
