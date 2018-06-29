package com.topaiebiz.timetask.settlement;
/**
 * Description： 结算管理的定时任务.
 *
 * <p>Author Harry
 *
 * <p>Date 2017年11月24日 上午10:20:33
 *
 * <p>Copyright Cognieon technology group co.LTD. All rights reserved.
 *
 * <p>Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
import com.topaiebiz.settlement.api.SettlementApi;
import com.topaiebiz.timetask.quartzaop.aop.QuartzContextOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SettlementTimeTask {

  @Autowired private SettlementApi settlementApi;

  /**
   * Description： 定时计算商家的结算。
   *
   * @return
   */
  @QuartzContextOperation
  @Scheduled(cron = "0 0 3 * * * ")
  public void timingTask() {
    settlementApi.startSettlementJob();
  }
}
