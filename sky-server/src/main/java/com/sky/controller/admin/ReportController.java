package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

/**
 * @Description: 数据统计相关接口
 */
@RequestMapping("/admin/report")
@RestController
@Api(tags = "数据统计相关接口")
@Slf4j
public class ReportController {
    @Autowired
    private ReportService reportService;

    /**
     * 获取营业额统计
     * @param begin
     * @param end
     * @return
     */
    @ApiOperation("获取营业额统计")
    @GetMapping("/turnoverStatistics")
    public Result<TurnoverReportVO> turnoverStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin
            , @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("获取营业额统计{},{}",begin,end);

        TurnoverReportVO turnoverReportVO = reportService.getturnoverStatistics(begin, end);

        return Result.success(turnoverReportVO);
    }

    /**
     * 获取用户统计
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/userStatistics")
    @ApiOperation("获取用户统计")
    public Result<UserReportVO> userStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin
            , @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end
    ){
        log.info("获取用户统计");
        return Result.success(reportService.getUserStatistics(begin,end));
    }

    /**
     * 获取订单统计
     * @param begin
     * @param end
     * @return
     */
    @ApiOperation("获取订单统计")
    @GetMapping("/ordersStatistics")
    public Result<OrderReportVO> orderStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin
            , @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("获取订单统计{},{}",begin,end);
        return Result.success(reportService.getOrderStatistics(begin,end));
    }

    @ApiOperation("获取销售排名")
    @GetMapping("top10")
    public Result<SalesTop10ReportVO> salesTop10Statistics(

            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin

            , @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){

        return Result.success(reportService.getSalesTop10Statistics(begin,end));

    }
    @GetMapping("/export")
    @ApiOperation("数据统计相关接口")
    public void getExport(HttpServletResponse response) throws IOException {

        reportService.exportBusinessData(response);
    }


}
