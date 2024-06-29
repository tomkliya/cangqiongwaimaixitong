package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Service
public class ReportServiceImpl implements ReportService  {


    @Autowired
    private WorkspaceService workspaceService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Override
    public TurnoverReportVO getturnoverStatistics(LocalDate begin, LocalDate end) {

        //构建dateList
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }



        //构建turnoverList
        List<Double> turnoverList = new ArrayList<>();

        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

  // 创建一个HashMap对象
  Map map = new HashMap<>();
  // 将beginTime放入HashMap中
  map.put("beginTime", beginTime);
  // 将endTime放入HashMap中
  map.put("endTime", endTime);
  // 将status放入HashMap中，并设置其值为Orders.COMPLETED
  map.put("status",Orders.COMPLETED);
  // 使用orderMapper的sumByMap方法，根据HashMap中的值计算出总金额
  Double v = orderMapper.sumByMap(map);
  // 如果v为null，则将其设置为0.0
  v = v==null?0.0:v;
  // 将计算出的总金额放入turnoverList中
  turnoverList.add(v);
        }


        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    /**
     * 获取用户统计信息
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {

        //构建dateList
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        //构建userList，每天用户总量
        List<Integer> userList = new ArrayList<>();
        //构建newuserList,每天新增用户
        List<Integer> newuserList = new ArrayList<>();

        for (LocalDate date : dateList) {

            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap<>();
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);
            Double v = userMapper.getNewUserCount(map);
            Double v1 = userMapper.getUserCount(map);
            v = v==null?0:v;
            v1 = v1==null?0:v1;
            userList.add(v1.intValue());
            newuserList.add(v.intValue());
        }


        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(newuserList, ","))
                .totalUserList(StringUtils.join(userList, ","))
                .build();
    }

    /**
     * 获取订单统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        //构建dateList
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Integer> orderCountList = new ArrayList<>();//每天订单总量
        List<Integer> validOrderCountList = new ArrayList<>();//每天新增订单
        Integer totalOrderCount = 0;
        Integer validOrderCount = 0;
        Double orderCompletionRate = 0.0;;
//        //日期，以逗号分隔，例如：2022-10-01,2022-10-02,2022-10-03
//        private String dateList;
//        //每日订单数，以逗号分隔，例如：260,210,215
//        private String orderCountList;
//        //每日有效订单数，以逗号分隔，例如：20,21,10
//        private String validOrderCountList;
//        //订单总数
//        private Integer totalOrderCount;
//        //有效订单数
//        private Integer validOrderCount;
//        //订单完成率
//        private Double orderCompletionRate;

        for (LocalDate date : dateList) {
            //获取每天订单总量
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap<>();
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);
            Integer v = orderMapper.getOrderTotalCount(map);
            map.put("status",Orders.COMPLETED);
            Integer v1 = orderMapper.getOrderTotalCount(map);
            orderCountList.add(v1.intValue());
            validOrderCountList.add(v.intValue());
            totalOrderCount += v;
            validOrderCount += v1;

        }
        //计算订单完成率
        if (totalOrderCount != 0){
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount ;
        }

        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    @Override
    public SalesTop10ReportVO getSalesTop10Statistics(LocalDate begin, LocalDate end) {
        List<GoodsSalesDTO>  list = orderMapper.getSalesTopTen(begin,end);
        List<String> nameList = new ArrayList<>();
        List<Integer> numberList = new ArrayList<>();
        for (GoodsSalesDTO goodsSalesDTO : list) {

            nameList.add(goodsSalesDTO.getName());
            numberList.add(goodsSalesDTO.getNumber());


        }
        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList, ","))
                .numberList(StringUtils.join(numberList, ","))
                .build();

    }
    @Override
    public void exportBusinessData(HttpServletResponse response) throws IOException {
        LocalDate start = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);

        BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(start, LocalTime.MIN), LocalDateTime.of(end, LocalTime.MAX));


        try(
            InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("tempelete/运营数据报表模板.xlsx");
            XSSFWorkbook sheets = new XSSFWorkbook(resourceAsStream);) {

            XSSFSheet sheetAt = sheets.getSheetAt(0);
            //第一行
            XSSFRow row1 = sheetAt.getRow(1);
            row1.getCell(1).setCellValue("时间："+ start + "~" + end);

            sheetAt.getRow(3).getCell(1).setCellValue(businessData.getTurnover());
            sheetAt.getRow(3).getCell(3).setCellValue(businessData.getOrderCompletionRate());
            sheetAt.getRow(3).getCell(5).setCellValue(businessData.getNewUsers());

            sheetAt.getRow(4).getCell(1).setCellValue(businessData.getValidOrderCount());
            sheetAt.getRow(4).getCell(3).setCellValue(businessData.getUnitPrice());
            ServletOutputStream outputStream = response.getOutputStream();

           sheets.write(outputStream);

           //详细数据
            for (int i = 0;i < 30;i++){
                LocalDate localDate = start.minusDays(1);
                BusinessDataVO businessDataVO = workspaceService.getBusinessData(LocalDateTime.of(localDate, LocalTime.MIN), LocalDateTime.of(localDate, LocalTime.MAX));
                XSSFRow row = sheetAt.getRow(7 + i);
                row.getCell(1).setCellValue(localDate.toString());

                row.getCell(2).setCellValue(businessDataVO.getTurnover());

                row.getCell(3).setCellValue(businessDataVO.getValidOrderCount());

                row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());

                row.getCell(5).setCellValue(businessDataVO.getUnitPrice());

                row.getCell(6).setCellValue(businessDataVO.getNewUsers());
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }


}
