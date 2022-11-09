package jp.co.ichain.luigi2.condition;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jp.co.ichain.luigi2.mapper.ManagementMapper;
import jp.co.ichain.luigi2.resources.code.Luigi2CodeCustomers.CorporateIndividualFlag;
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
public class Oc00601Condition {

  @Autowired
  ManagementMapper mapper;

  static final String[] REQ_COP = {"corpNameOfficial", "corpNameKana",
      "corpAddrZipCode", "corpAddrKnjPref", "corpAddrKnj1",
      "rep10eNameKnjSei", "rep10eNameKnjMei", "rep10eNameKanaSei", "rep10eNameKanaMei",
      "rep10eDateOfBirth", "rep10eSex", "rep10eAddrZipCode", "rep10eAddrKnjPref",
      "rep10eAddrKnj1", "rep10eAddrTel1", "contactNameKnjSei", "contactNameKnjMei",
      "contactNameKanaSei", "contactNameKanaMei", "contactAddrZipCode", "contactAddrKnjPref",
      "contactAddrKnj1", "contactAddrTel1", "contactEmail"};

  static final String[] REQ_IND = {"nameKnjSei", "nameKnjMei", "nameKanaSei",
      "nameKanaMei", "sex", "dateOfBirth", "addrZipCode", "addrKnjPref", "addrKnj1", "addrTel1",
      "email"};

  /**
   * 更新フラグ対象が存在するかチェック
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-03
   * @updatedAt : 2021-08-03
   * @param data
   * @param tenantId
   * @param paramList
   * @return
   */
  public boolean checkRequiredCustomerToChangeFg(Object data, Integer tenantId,
      List<Object> paramList) {
    @SuppressWarnings("unchecked")
    val paramMap = (Map<String, Object>) data;

    if (paramMap.get("contractorCustomerChangeFg") != null
        && (boolean) paramMap.get("contractorCustomerChangeFg")
        && paramMap.get("contractorCustomer") == null) {
      return false;
    } else if (paramMap.get("contractorGuardianCustomerChangeFg") != null
        && (boolean) paramMap.get("contractorGuardianCustomerChangeFg")
        && paramMap.get("contractorGuardianCustomer") == null) {
      return false;
    } else if (paramMap.get("insuredCustomerChangeFg") != null
        && (boolean) paramMap.get("insuredCustomerChangeFg")
        && paramMap.get("insuredCustomer") == null) {
      return false;
    } else if (paramMap.get("insuredGuardianCustomerChangeFg") != null
        && (boolean) paramMap.get("insuredGuardianCustomerChangeFg")
        && paramMap.get("insuredGuardianCustomer") == null) {
      return false;
    }
    return true;
  }

  /**
   * 顧客個人法人による必須項目を検証する
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-03
   * @updatedAt : 2021-08-03
   * @param data
   * @param tenantId
   * @param paramList
   * @return
   */
  public boolean checkRequiredCustomerToCorporateIndividualFlag(Object data, Integer tenantId,
      List<Object> paramList) {
    @SuppressWarnings("unchecked")
    val paramMap = (Map<String, Object>) data;

    if (CorporateIndividualFlag.CORPORATION.toString()
        .equals(paramMap.get("corporateIndividualFlag"))) {
      for (val corpColumn : REQ_COP) {
        if (paramMap.get(corpColumn) == null) {
          return false;
        }
      }
    } else {
      for (val corpColumn : REQ_IND) {
        if (paramMap.get(corpColumn) == null) {
          return false;
        }
      }
    }
    return true;
  }
}
