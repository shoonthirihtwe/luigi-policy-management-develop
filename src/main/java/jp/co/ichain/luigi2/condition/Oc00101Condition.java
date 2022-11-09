package jp.co.ichain.luigi2.condition;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jp.co.ichain.luigi2.mapper.ManagementMapper;
import jp.co.ichain.luigi2.validity.Condition;
import lombok.val;

/**
 * 固有受付条件検証サービス
 * 
 * @author : [AOT] s.paku
 * @createdAt : 2021-10-16
 * @updatedAt : 2021-10-16
 */
@Service
@Condition
public class Oc00101Condition {

  @Autowired
  ManagementMapper mapper;

  /**
   * 契約受付日より保全請求受付日が未来日なのかチェック
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-10-16
   * @updatedAt : 2021-10-16
   * @param data
   * @param tenantId
   * @param paramList
   * @return
   */
  public boolean checkReceivedDate(Object data, Integer tenantId, List<Object> paramList) {
    @SuppressWarnings("unchecked")
    val map = (Map<String, Object>) data;
    val vo = mapper.selectContractInfo(map);

    val receivedDate = Long.valueOf((String) map.get("receivedDate"));
    Date applicationDate = (Date) vo.get("applicationDate");
    if (applicationDate != null && applicationDate.getTime() > receivedDate) {
      return false;
    }
    return true;
  }

  /**
   * 契約ステータスが「受付済」の契約の場合は保全申請ができない。
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-11-02
   * @updatedAt : 2021-11-02
   * @param data
   * @param tenantId
   * @param paramList
   * @return
   */
  public boolean checkContractsStatus(Object data, Integer tenantId, List<Object> paramList) {
    val status = mapper.selectUnderwritingsContractStatus(tenantId, data);

    if (status != null && status.equals("00")) {
      return false;
    }
    return true;
  }
}
