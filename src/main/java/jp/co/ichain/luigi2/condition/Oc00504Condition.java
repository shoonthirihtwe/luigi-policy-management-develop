package jp.co.ichain.luigi2.condition;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jp.co.ichain.luigi2.mapper.ManagementMapper;
import jp.co.ichain.luigi2.resources.TenantResources;
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
public class Oc00504Condition {

  @Autowired
  ManagementMapper mapper;

  @Autowired
  TenantResources tenantResources;

  /**
   * 解除日検証
   * 
   * @author : [AOT] g.kim
   * @createdAt : 2021-12-15
   * @updatedAt : 2021-12-15
   * @param data
   * @param tenantId
   * @param paramList
   * @return
   * @throws SecurityException
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  public boolean checkRefundAmountTerminationBaseDate(Object data, Integer tenantId,
      List<Object> paramList)
      throws InstantiationException, IllegalAccessException, SecurityException {
    @SuppressWarnings("unchecked")
    val paramMap = (Map<String, Object>) data;
    paramMap.put("tenantId", tenantId);
    val contractNo = mapper.selectRefundAmountTerminationBaseDate(paramMap);

    if (contractNo == null) {
      return false;
    }

    return true;
  }

}
