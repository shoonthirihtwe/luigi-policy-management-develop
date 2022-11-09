package jp.co.ichain.luigi2.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import jp.co.ichain.luigi2.config.datasource.Luigi2Mapper;
import jp.co.ichain.luigi2.vo.BeneficialiesVo;
import jp.co.ichain.luigi2.vo.MaintenanceRequestsVo;

/**
 * Accounting Mapper
 * 
 * @author : [AOT] s.paku
 * @createdAt : 2021-06-24
 * @updatedAt : 2021-06-24
 */
@Repository
@Luigi2Mapper
public interface ManagementMapper {

  /**
   * 保全一覧取得
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-06-28
   * @updatedAt : 2021-06-28
   * @param param
   * @return
   */
  List<MaintenanceRequestsVo> searchMaintenanceRequests(Map<String, Object> param);

  /**
   * 保全一覧総個数取得
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-06-28
   * @updatedAt : 2021-06-28
   * @param param
   * @return
   */
  int searchMaintenanceRequestsTotalCount(Map<String, Object> param);

  /**
   * 保全挿入
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-07-02
   * @updatedAt : 2021-07-02
   * @param param
   */
  void insertMaintenanceRequests(Map<String, Object> param);

  /**
   * 契約者情報取得
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-10-14
   * @updatedAt : 2021-10-14
   * @param param
   * @return
   */
  Map<String, Object> selectContractInfo(Map<String, Object> param);

  /**
   * 契約ステータス取得
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-11-02
   * @updatedAt : 2021-11-02
   * @param tenantId
   * @param contractNo
   * @return
   */
  String selectUnderwritingsContractStatus(@Param("tenantId") Object tenantId,
      @Param("contractNo") Object contractNo);

  /**
   * 保全詳細取得
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-07-06
   * @updatedAt : 2021-07-06
   * @param param
   * @return
   */
  MaintenanceRequestsVo selectMaintenanceRequests(Map<String, Object> param);

  /**
   * 保全詳細取得
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-07-07
   * @updatedAt : 2021-07-07
   * @param maintenanceRequestsId
   * @return
   */
  MaintenanceRequestsVo selectMaintenanceRequests(@Param("tenantId") Object tenantId,
      @Param("requestNo") Object requestNo);

  /**
   * refund_amount情報取得
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-09-16
   * @updatedAt : 2021-09-16
   * @param tenantId
   * @param requestNo
   * @return
   */
  MaintenanceRequestsVo selectRefundAmountInfo(@Param("tenantId") Object tenantId,
      @Param("requestNo") Object requestNo);

  /**
   * 保全1次査定
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-07-06
   * @updatedAt : 2021-07-06
   * @param param
   * @return
   */
  int updateFirstMaintenanceRequests(Map<String, Object> param);

  /**
   * 保全2次査定
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-07-06
   * @updatedAt : 2021-07-06
   * @param param
   * @return
   */
  int updateSecondMaintenanceRequests(Map<String, Object> param);

  /**
   * 契約支払い経路取得
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-07-06
   * @updatedAt : 2021-07-06
   * @return
   */
  String selectContentsPaymentMethod(@Param("tenantId") Object tenantId,
      @Param("requestNo") Object requestNo);

  /**
   * 保全ステータス取得
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-05
   * @updatedAt : 2021-08-05
   * @param tenantId
   * @param requestNo
   * @return
   */
  String selectMaintenanceRequestsStatus(@Param("tenantId") Object tenantId,
      @Param("requestNo") Object requestNo);

  /**
   * 払戻金挿入
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-07-08
   * @updatedAt : 2021-07-08
   * @param param
   */
  void insertRefundAmount(Map<String, Object> param);

  /**
   * 払戻金2次査定更新
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-07-08
   * @updatedAt : 2021-07-08
   * @param param
   * @return
   */
  int updateSecondRefundAmount(Map<String, Object> param);

  /**
   * 契約情報を解約に変更
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-07-09
   * @updatedAt : 2021-07-09
   * @param param
   * @return
   */
  int updateContractsStatus(Map<String, Object> param);

  /**
   * 顧客メール変更
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-07-09
   * @updatedAt : 2021-07-09
   * @param param
   * @return
   */
  int updateCustomerEmail(Map<String, Object> param);

  /**
   * 返金額取得
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-07-12
   * @updatedAt : 2021-07-12
   * @param param
   * @return
   */
  long selectRefundAmount(Map<String, Object> param);

  /**
   * sales_products情報取得
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-10-14
   * @updatedAt : 2021-10-14
   * @param param
   * @return
   */
  Map<String, Object> selectSalesProducts(Map<String, Object> param);

  /**
   * 保全申請分類取得
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-07-27
   * @updatedAt : 2021-07-27
   * @param param
   * @return
   */
  Map<String, Object> selectTransactionCode(@Param("tenantId") Object tenantId,
      @Param("requestNo") Object requestNo);

  // OC008 受取人
  /**
   * 保全受取人取得
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-06
   * @updatedAt : 2021-08-06
   * @param param
   * @return
   */
  List<BeneficialiesVo> selectBeneficiaries(Map<String, Object> param);

  /**
   * 保全受取人１次査定時削除
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-10
   * @updatedAt : 2021-08-10
   * @param param
   * @return
   */
  int deleteMaintenanceRequestsBeneficiaries(@Param("tenantId") Object tenantId,
      @Param("requestNo") Object requestNo,
      @Param("beneficiariesList") List<Map<String, Object>> beneficiariesList);

  /**
   * 保全受取人１次査定時更新
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-10
   * @updatedAt : 2021-08-10
   * @param param
   * @return
   */
  int updateMaintenanceRequestsBeneficiaries(Map<String, Object> param);

  /**
   * 保全受取人１次査定時挿入
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-10
   * @updatedAt : 2021-08-10
   * @param param
   * @return
   */
  void insertMaintenanceRequestsBeneficiaries(Map<String, Object> param);

  /**
   * 保全受取人2次査定時削除
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-10
   * @updatedAt : 2021-08-10
   * @param param
   * @return
   */
  int updateBeneficiariesForDelete(Map<String, Object> param);

  /**
   * 保全受取人2次査定時挿入
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-10
   * @updatedAt : 2021-08-10
   * @param param
   * @return
   */
  int insertBeneficiaries(@Param("tenantId") Object tenantId, @Param("updatedBy") Object updatedBy,
      @Param("onlineDate") Date onlineDate,
      @Param("beneficiariesList") List<BeneficialiesVo> beneficiarieList);

  /**
   * 保全受取人2次査定、Before登録用受取人取得
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-10
   * @updatedAt : 2021-08-10
   * @param param
   * @return
   */
  List<Map<String, Object>> selectBeneficiariesForBeforeInsert(Map<String, Object> param);

  /**
   * 返金額取得(Pm001)
   * 
   * @author : [AOT] g.kim
   * @createdAt : 2021-10-22
   * @updatedAt : 2021-10-22
   * @param param
   * @return
   */
  long selectRefundAmountPm001(Map<String, Object> param);

  /**
   * 返金額取得(Sm001)
   *
   * @author : [AOT] g.kim
   * @createdAt : 2022-08-22
   * @updatedAt : 2022-08-22
   * @param param
   * @return
   */
  Map<String, Object> selectRefundAmountSm001(Map<String, Object> param);

  /**
   * 返金額解除日検証
   *
   * @author : [AOT] g.kim
   * @createdAt : 2021-12-15
   * @updatedAt : 2021-12-15
   * @param maintenanceRequestsId
   * @return
   */
  String selectRefundAmountTerminationBaseDate(Map<String, Object> param);

  /**
   * 保全支払い変更（SBS決済情報変更）F
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2022/08/31
   * @updatedAt : 2022/08/31
   * @param param
   * @return
   */
  int updateContractsBillingInfo(Map<String, Object> param);

  /**
   * 保全ServiceObjects取得
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2022/09/05
   * @updatedAt : 2022/09/05
   * @param param
   * @return
   */
  List<Map<String, Object>> selectMaintenanceRequestsServiceObjests(Map<String, Object> param);

  /**
   * 保全ServiceObjects挿入
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2022/09/05
   * @updatedAt : 2022/09/05
   * @param param
   */
  void insertMaintenanceRequestsServiceObjests(Map<String, Object> param);
  
  /**
   * 保全ServiceObjects削除
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2022/09/06
   * @updatedAt : 2022/09/06
   * @param param
   */
  void removeMaintenanceRequestsServiceObjests(Map<String, Object> param);
}
