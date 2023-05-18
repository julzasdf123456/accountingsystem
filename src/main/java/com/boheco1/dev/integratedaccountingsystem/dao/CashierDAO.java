package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.ORItemSummary;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CashierDAO {

    /**
     * Retrieves DCR Breakdown for OR items from a specified date
     * @param year The posting year
     * @param month The posting month
     * @param day The posting day
     * @param teller The posting day
     * @return list The list of dcr breakdown
     * @throws Exception obligatory from DB.getConnection()
     */
    public static List<ORItemSummary> getOrItems(int year, int month, int day, String teller) throws Exception{
        String sql = "SELECT "+
                "( "+
                "        (SUM(ISNULL(p.CurrentBills,0)) + SUM(ISNULL(p.Within30Days,0)) + SUM(ISNULL(p.Over30Days,0))) "+
                "        - (SUM(IIF(p.ConsumerType = 'R', ISNULL(p.surcharge*0.12,0), 0))) "+
                "        - (SUM(ISNULL(p.pr,0)) + SUM(ISNULL(p.others,0))) "+
                "        - SUM(ISNULL(p.surcharge,0)) "+
                "        - SUM(ISNULL(p.Item2, 0)) "+
                "        + (SUM(ISNULL(p.SLAdjustment,0)) + SUM(ISNULL(p.PromptPayment,0))) "+
                "        + SUM(ISNULL(p.otherdeduction, 0)) "+
                "        - SUM(ISNULL(p.SeniorCitizenDiscount,0)) "+
                "        + SUM(IIF(p.ConsumerType = 'E', ISNULL(p.others,0), 0)) "+
                "        + SUM(IIF(p.ConsumerType = 'B', ISNULL(p.others,0), 0)) "+
                "        + SUM(ISNULL(p.Amount2306, 0)) "+
                "        + SUM(ISNULL(p.Amount2307, 0)) "+
                "        + SUM(ISNULL(b.FBHCAmt, 0)) "+
                "        + SUM(ISNULL(x.Item16, 0)) "+
                "        + SUM(ISNULL(x.Item17, 0))  "+
                "	) AS Energy, "+
                "SUM(ISNULL(p.pr,0)) AS TransformerRental, "+
                "SUM(ISNULL(p.others,0)) AS Others, "+
                "SUM(ISNULL(p.PromptPayment,0)) AS PPD, "+
                "SUM(ISNULL(p.surcharge,0)) AS Surcharge, "+
                "(SUM(ISNULL(p.Item2, 0)) "+
                "        + (SUM(IIF(p.ConsumerType = 'R', p.surcharge*0.12, 0))) "+
                "        - SUM(ISNULL(b.FBHCAmt, 0)) "+
                "        - SUM(ISNULL(x.Item17, 0)) "+
                "        - SUM(ISNULL(x.Item16, 0)) "+
                "        - SUM(IIF(p.Period >'201502', (ISNULL(p.TransmissionVAT, 0)), 0)) "+
                "        - "+
                "        ( "+
                "                SUM(IIF(p.Period >'201502', (ISNULL(p.GenerationVAT,0)), 0)) "+
                "                        + SUM(IIF(p.Period >'201503', (ISNULL(p.SLVAT,0)), 0)) "+
                "                        + SUM(IIF(p.Period >'201503', (ISNULL(b.DAA_VAT,0)), 0)) "+
                "                        + SUM(IIF(p.Period >'201503', (ISNULL(b.ACRM_VAT,0)), 0)) "+
                "                        + SUM(IIF(p.Period >'202111', (ISNULL(b.Item1,0)), 0)) "+
                "        ) "+
                ") AS Evat, "+

                "SUM(ISNULL(p.SLAdjustment,0)) AS SLAdj, "+
                "SUM(ISNULL(p.otherdeduction,0)) AS OtherDeduction, "+
                "SUM(ISNULL(p.Amount2306, 0)) AS Amount2306, "+
                "SUM(ISNULL(p.Amount2307, 0)) AS Amount2307, "+
                "SUM(ISNULL(p.MDRefund, 0)) AS MDRefund, "+
                "SUM(ISNULL(p.PowerKWH, 0)) AS PowerKWH, "+
                "SUM(ISNULL(p.NetAmount,0)) AS AmountPaid, "+
                "SUM(ISNULL(pb.cashAmount, p.NetAmount)) AS CashAmount, "+
                "SUM(ISNULL(pb.checkAmount, 0)) AS CheckAmount, "+
                "SUM(ISNULL(p.SeniorCitizenDiscount,0)) AS SeniorCitizenDiscount, "+
                "SUM(ISNULL(b.NetAmount,0)) AS AmountDue, "+
                "SUM(IIF(p.Period >'201502', (ISNULL(p.TransmissionVAT, 0)), 0)) AS ARVATTrans, "+
                "(SUM(IIF(p.Period >'201502', (ISNULL(p.GenerationVAT,0)), 0)) "+
                "        + SUM(IIF(p.Period >'201503', (ISNULL(p.SLVAT,0)), 0)) "+
                "        + SUM(IIF(p.Period >'201503', (ISNULL(b.DAA_VAT,0)), 0)) "+
                "        + SUM(IIF(p.Period >'201503', (ISNULL(b.ACRM_VAT,0)), 0)) "+
                "        + SUM(IIF(p.Period >'202111', (ISNULL(b.Item1,0)), 0))) AS ARVATGen, "+
                "SUM(ISNULL(p.KatasAMount, 0)) AS KatasNgVAT "+
                "FROM Bills b "+
                "INNER JOIN BillsExtension x ON b.AccountNumber=x.AccountNumber AND b.ServicePeriodEnd=x.ServicePeriodEnd "+
                "INNER JOIN PaidBillsWithRoute p ON b.AccountNumber=p.AccountNumber AND b.ServicePeriodEnd=p.ServicePeriodEnd "+
                "INNER JOIN PaidBills pb ON b.AccountNumber=pb.AccountNumber AND b.ServicePeriodEnd=pb.ServicePeriodEnd "+
                "WHERE p.PostingDate >= '"+year+"-"+month+"-"+day+" 00:00:00' AND p.PostingDate <= '"+year+"-"+month+"-"+day+" 23:59:59' AND p.TELLER = ?";

        PreparedStatement ps = DB.getConnection(Utility.DB_BILLING).prepareStatement(sql);

        if (teller != null)
            ps.setString(1, teller);

        ResultSet rs = ps.executeQuery();

        List<ORItemSummary> tempORItems = new ArrayList<>();
        List<ORItemSummary> orItems = new ArrayList<>();

        while(rs.next()) {
            double energy = rs.getDouble("Energy");
            double tr = rs.getDouble("TransformerRental");
            double others = rs.getDouble("Others");
            double surcharge = rs.getDouble("Surcharge");
            double evat = rs.getDouble("Evat");
            double slAdj = rs.getDouble("SLAdj")*-1;
            double ppD = rs.getDouble("PPD")*-1;
            double katasNgVAT = Math.abs(rs.getDouble("KatasNgVAT"))*-1;
            double otherDeduction = Math.abs(rs.getDouble("OtherDeduction"))*-1;
            double mdRefund = Math.abs(rs.getDouble("MDRefund"))*-1;
            double scDiscount = Math.abs(rs.getDouble("SeniorCitizenDiscount"))*-1;
            double amount2307 = Math.abs(rs.getDouble("Amount2307"))*-1;
            double amount2306 = Math.abs(rs.getDouble("Amount2306"))*-1;
            double arTrans = rs.getDouble("ARVATTrans");
            double arGen = rs.getDouble("ARVATGen");


            double total = (energy + tr + others + surcharge + evat) + (slAdj + ppD + katasNgVAT + otherDeduction + mdRefund + scDiscount + amount2307 + amount2306) + arTrans + arGen;

            ORItemSummary energyItem = new ORItemSummary("12410101000","Energy Bill", energy);
            ORItemSummary trItem = new ORItemSummary("43040300000","Transformer rental", tr);
            ORItemSummary energyBillsOthers = new ORItemSummary("124101010001","Energy Bills - Others", others+otherDeduction);
            //ORItemSummary othersItem = new ORItemSummary("124101010001","Others", others);
            ORItemSummary surChargeItem = new ORItemSummary("43040500000","Surcharge", surcharge);
            ORItemSummary evatItem = new ORItemSummary("22420414001","EVAT", evat);
            ORItemSummary slAdjItem = new ORItemSummary("56150304000","Systems Loss Adjustment", slAdj);
            ORItemSummary ppdItem = new ORItemSummary("51350210000","Prompt payment discount", ppD);
            ORItemSummary katasvatItem = new ORItemSummary("11310806000","Sinking Fund - Katas ng VAT", katasNgVAT);
            ORItemSummary otherDeductionsItem = new ORItemSummary("124101010001","Other Deduction", otherDeduction);
            ORItemSummary mdRefundItem = new ORItemSummary("21720112002","Meter Deposit-Main", mdRefund);
            ORItemSummary scDiscountItem = new ORItemSummary("12410101000","Senior Citizen Discount", scDiscount);
            ORItemSummary ch2307Item = new ORItemSummary("12910111002","Prepayments - Others 2307", amount2307);
            ORItemSummary ch2306Item = new ORItemSummary("22420414002","EVAT 2307", amount2306);
            ORItemSummary arVATTransItem = new ORItemSummary("12410311000","AR VAT - Transco", arTrans);
            ORItemSummary arVATGenItem = new ORItemSummary("12410310000","AR VAT - GenCo", arGen);
           // ORItemSummary totalItem = new ORItemSummary(Utility.getAccountCodeProperty(),"Grand Total", total);//do not change the description it used during teller save transaction details

            tempORItems.add(energyItem);
            tempORItems.add(trItem);
            tempORItems.add(energyBillsOthers);
            //tempORItems.add(othersItem);
            tempORItems.add(surChargeItem);
            tempORItems.add(evatItem);
            tempORItems.add(slAdjItem);
            tempORItems.add(ppdItem);
            tempORItems.add(katasvatItem);
            tempORItems.add(otherDeductionsItem);
            tempORItems.add(mdRefundItem);
            tempORItems.add(scDiscountItem);
            tempORItems.add(ch2306Item);
            tempORItems.add(ch2307Item);
            tempORItems.add(arVATTransItem);
            tempORItems.add(arVATGenItem);
           // tempORItems.add(totalItem);


            for(ORItemSummary or : tempORItems){
                if(or.getAmount()!=0)
                    orItems.add(or);
            }
        }

        rs.close();
        ps.close();

        return orItems;
    }
}
