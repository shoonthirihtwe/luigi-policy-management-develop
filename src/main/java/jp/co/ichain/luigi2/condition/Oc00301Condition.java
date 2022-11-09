package jp.co.ichain.luigi2.condition;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jp.co.ichain.luigi2.mapper.ManagementMapper;
import jp.co.ichain.luigi2.resources.code.Luigi2CodeContracts.PaymentMethod;
import jp.co.ichain.luigi2.validity.Condition;
import lombok.val;

/**
 * 固有一般条件検証サービス
 * 
 * @author : [AOT] s.paku
 * @createdAt : 2021-06-10
 * @updatedAt : 2021-08-12
 */
@Service
@Condition
public class Oc00301Condition {

  @Autowired
  ManagementMapper mapper;

  /**
   * 支払い経路をカードから口座に変更しないか検証
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-07-06
   * @updatedAt : 2021-07-06
   * @param data
   * @param tenantId
   * @param paramList
   * @return
   */
  public boolean changeFromCardToBank(Object data, Integer tenantId, List<Object> paramList) {
    @SuppressWarnings("unchecked")
    val paramMap = (Map<String, Object>) data;

    val contractPaymentMethodCode =
        mapper.selectContentsPaymentMethod(tenantId, paramMap.get("requestNo"));
    val paymentMethodCode = paramMap.get("paymentMethodCode");
    if (contractPaymentMethodCode.equals(PaymentMethod.CREDIT.toString())
        && paymentMethodCode.equals(PaymentMethod.TRANSFER.toString())) {
      return false;
    }
    return true;
  }

  /**
   * bankCode必須チェック
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-07-06
   * @updatedAt : 2021-07-06
   * @param data
   * @param tenantId
   * @param paramList
   * @return
   */
  public boolean checkRequiredBankCode(Object data, Integer tenantId, List<Object> paramList) {
    @SuppressWarnings("unchecked")
    val paramMap = (Map<String, Object>) data;

    if (paramMap.get("paymentMethodCode").equals(PaymentMethod.TRANSFER.toString())) {
      return paramMap.get("bankCode") != null;
    }
    return true;
  }

  /**
   * bankBranchCode必須チェック
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-07-06
   * @updatedAt : 2021-07-06
   * @param data
   * @param tenantId
   * @param paramList
   * @return
   */
  public boolean checkRequiredBankBranchCode(Object data, Integer tenantId,
      List<Object> paramList) {
    @SuppressWarnings("unchecked")
    val paramMap = (Map<String, Object>) data;

    if (paramMap.get("paymentMethodCode").equals(PaymentMethod.TRANSFER.toString())) {
      return paramMap.get("bankBranchCode") != null;
    }
    return true;
  }

  /**
   * bankAccountType必須チェック
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-07-06
   * @updatedAt : 2021-07-06
   * @param data
   * @param tenantId
   * @param paramList
   * @return
   */
  public boolean checkRequiredBankAccountType(Object data, Integer tenantId,
      List<Object> paramList) {
    @SuppressWarnings("unchecked")
    val paramMap = (Map<String, Object>) data;

    if (paramMap.get("paymentMethodCode").equals(PaymentMethod.TRANSFER.toString())) {
      return paramMap.get("bankAccountType") != null;
    }
    return true;
  }

  /**
   * bankAccountNo必須チェック
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-07-06
   * @updatedAt : 2021-07-06
   * @param data
   * @param tenantId
   * @param paramList
   * @return
   */
  public boolean checkRequiredBankAccountNo(Object data, Integer tenantId, List<Object> paramList) {
    @SuppressWarnings("unchecked")
    val paramMap = (Map<String, Object>) data;

    if (paramMap.get("paymentMethodCode").equals(PaymentMethod.TRANSFER.toString())) {
      return paramMap.get("bankAccountNo") != null;
    }
    return true;
  }

  /**
   * bankAccountName必須チェック
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-07-06
   * @updatedAt : 2021-07-06
   * @param data
   * @param tenantId
   * @param paramList
   * @return
   */
  public boolean checkRequiredBankAccountName(Object data, Integer tenantId,
      List<Object> paramList) {
    @SuppressWarnings("unchecked")
    val paramMap = (Map<String, Object>) data;

    if (paramMap.get("paymentMethodCode").equals(PaymentMethod.TRANSFER.toString())) {
      return paramMap.get("bankAccountName") != null;
    }
    return true;
  }
}
