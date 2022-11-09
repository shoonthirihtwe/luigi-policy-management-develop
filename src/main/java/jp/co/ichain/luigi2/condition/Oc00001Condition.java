package jp.co.ichain.luigi2.condition;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jp.co.ichain.luigi2.mapper.ManagementMapper;
import jp.co.ichain.luigi2.resources.code.Luigi2CodeMaintenanceRequests.RequestStatus;
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
public class Oc00001Condition {

  @Autowired
  ManagementMapper mapper;

  /**
   * 1次査定、２時査定時に想定した業務なのか検証
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-07-27
   * @updatedAt : 2021-07-27
   * @param data
   * @param tenantId
   * @param paramList
   * @return
   */
  public boolean checkTransactionCode(Object data, Integer tenantId, List<Object> paramList) {
    val map = mapper.selectTransactionCode(tenantId, data);

    if (map.get("tenantId").equals(tenantId)) {
      return false;
    }
    return paramList.contains(map.get("transactionCode"));
  }

  /**
   * 1次査定完了しているか検証
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-07-07
   * @updatedAt : 2021-07-07
   * @param data
   * @param tenantId
   * @param paramList
   * @return
   */
  public boolean checkFirstAssessmentCompleted(Object data, Integer tenantId,
      List<Object> paramList) {
    @SuppressWarnings("unchecked")
    val paramMap = (Map<String, Object>) data;
    val status = mapper.selectMaintenanceRequestsStatus(tenantId, paramMap.get("requestNo"));
    return RequestStatus.FIRST.toString().equals(status);
  }

  /**
   * 2次査定完了していないか検証
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-07-07
   * @updatedAt : 2021-07-07
   * @param data
   * @param tenantId
   * @param paramList
   * @return
   */
  public boolean checkSecondAssessmentCompleted(Object data, Integer tenantId,
      List<Object> paramList) {
    @SuppressWarnings("unchecked")
    val paramMap = (Map<String, Object>) data;
    val status = mapper.selectMaintenanceRequestsStatus(tenantId, paramMap.get("requestNo"));
    return RequestStatus.SECOND.toString().equals(status) == false;
  }

}
