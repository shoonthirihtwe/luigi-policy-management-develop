package jp.co.ichain.luigi2.condition;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jp.co.ichain.luigi2.mapper.ManagementMapper;
import jp.co.ichain.luigi2.resources.TenantResources;
import jp.co.ichain.luigi2.resources.code.Luigi2CodeMaintenanceRequests.TransactionCode;
import jp.co.ichain.luigi2.validity.Condition;
import lombok.val;

/**
 * 固有一般条件検証サービス
 * 
 * @author : [AOT] s.paku
 * @createdAt : 2021-06-10
 * @updatedAt : 2021-06-10
 */
@Service
@Condition
public class Oc00501Condition {

  @Autowired
  ManagementMapper mapper;

  @Autowired
  TenantResources tenantResources;

  /**
   * 解除日検証
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-07-12
   * @updatedAt : 2021-07-12
   * @param data
   * @param tenantId
   * @param paramList
   * @return
   * @throws SecurityException
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  public boolean checkTerminationBaseDate(Object data, Integer tenantId, List<Object> paramList)
      throws InstantiationException, IllegalAccessException, SecurityException {
    @SuppressWarnings("unchecked")
    val paramMap = (Map<String, Object>) data;

    val mrVo = mapper.selectMaintenanceRequests(tenantId, paramMap.get("requestNo"));
    Long occurredDate = (Long) paramMap.get("terminationBaseDate");
    if (occurredDate == null) {
      return false;
    } else if (TransactionCode.DELETE.toString().equals(mrVo.getTransactionCode())) {
      if (occurredDate > tenantResources.get(tenantId).getOnlineDate().getTime()) {
        return false;
      }
    } else if (TransactionCode.RETRACTION.toString().equals(mrVo.getTransactionCode())) {
      if (occurredDate != mrVo.getApplicationDate().getTime()) {
        return false;
      }
    } else {
      return false;
    }
    return true;
  }

  /**
   * 返金額チェック
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-07-12
   * @updatedAt : 2021-07-12
   * @param data
   * @param tenantId
   * @param paramList
   * @return
   */
  public boolean checkRequiredRefundAmount(Object data, Integer tenantId, List<Object> paramList) {
    @SuppressWarnings("unchecked")
    val paramMap = (Map<String, Object>) data;

    val refundYn = (Boolean) paramMap.get("refundYn");
    if (refundYn != null && refundYn && paramMap.get("refundAmount") == null) {
      return false;
    }
    return true;
  }
}
