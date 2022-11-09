package jp.co.ichain.luigi2.condition;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jp.co.ichain.luigi2.mapper.ManagementMapper;
import jp.co.ichain.luigi2.resources.code.Luigi2CodeCustomers.CorporateIndividualFlag;
import jp.co.ichain.luigi2.util.CollectionUtils;
import jp.co.ichain.luigi2.validity.Condition;
import lombok.val;

/**
 * 固有一般条件検証サービス
 * 
 * @author : [AOT] s.paku
 * @createdAt : 2021-08-03
 * @updatedAt : 2021-08-03
 */
@Service
@Condition
public class Oc00801Condition {

  @Autowired
  ManagementMapper mapper;

  /**
   * 受取人が一人以上か検証
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-10
   * @updatedAt : 2021-08-10
   * @param data
   * @param tenantId
   * @param paramList
   * @return
   */
  @SuppressWarnings("unchecked")
  public boolean checkRequiredBeneficiariesList(Object data, Integer tenantId,
      List<Object> paramList) {
    val paramMap = (Map<String, Object>) data;
    val beneficiariesList = (List<Map<String, Object>>) paramMap.get("beneficiariesList");
    return beneficiariesList != null && beneficiariesList.size() > 0;
  }

  /**
   * 受取人個人の場合、名必須チェック
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-17
   * @updatedAt : 2021-08-17
   * @param data
   * @param tenantId
   * @param paramList
   * @return
   */
  @SuppressWarnings("unchecked")
  public boolean checkRequiredIndividualNameKnjMei(Object data, Integer tenantId,
      List<Object> paramList) {
    val paramMap = (Map<String, Object>) data;
    val beneficiariesList = (List<Map<String, Object>>) paramMap.get("beneficiariesList");
    for (val beneficiarie : CollectionUtils.safe(beneficiariesList)) {
      if (CorporateIndividualFlag.INDIVIDUAL.equals(beneficiarie.get("corporateIndividualFlag"))) {
        if (paramMap.get("nameKnjMei") == null) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * 受取人個人の場合、名必須チェック
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-17
   * @updatedAt : 2021-08-17
   * @param data
   * @param tenantId
   * @param paramList
   * @return
   */
  @SuppressWarnings("unchecked")
  public boolean checkRequiredIndividualNameKanaMei(Object data, Integer tenantId,
      List<Object> paramList) {
    val paramMap = (Map<String, Object>) data;
    val beneficiariesList = (List<Map<String, Object>>) paramMap.get("beneficiariesList");
    for (val beneficiarie : CollectionUtils.safe(beneficiariesList)) {
      if (CorporateIndividualFlag.INDIVIDUAL.equals(beneficiarie.get("corporateIndividualFlag"))) {
        if (paramMap.get("nameKanaMei") == null) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * 受取の割合合計が100なのか検証
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-10
   * @updatedAt : 2021-08-10
   * @param data
   * @param tenantId
   * @param paramList
   * @return
   */
  @SuppressWarnings("unchecked")
  public boolean checkShare(Object data, Integer tenantId,
      List<Object> paramList) {
    val paramMap = (Map<String, Object>) data;
    val beneficiariesList = (List<Map<String, Object>>) paramMap.get("beneficiariesList");
    int share = 0;
    for (val map : CollectionUtils.safe(beneficiariesList)) {
      val s = map.get("share");
      if (s != null) {
        share += (int) s;
      }
    }
    return share == 100;
  }

}
