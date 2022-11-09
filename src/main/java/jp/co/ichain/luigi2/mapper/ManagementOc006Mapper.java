package jp.co.ichain.luigi2.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import jp.co.ichain.luigi2.config.datasource.Luigi2Mapper;
import jp.co.ichain.luigi2.vo.MaintenanceRequestsCustomersVo;

/**
 * Accounting Mapper
 * 
 * @author : [AOT] s.paku
 * @createdAt : 2021-06-24
 * @updatedAt : 2021-06-24
 */
@Repository
@Luigi2Mapper
public interface ManagementOc006Mapper {
  /**
   * 保全が作成されているか、個人か法人かを取得
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-02
   * @updatedAt : 2021-08-02
   * @param maintenanceRequestsId
   * @return
   */
  Map<String, Object> selectMaintenanceRequestsCorporateIndividualFlag(
      @Param("tenantId") Object tenantId,
      @Param("requestNo") Object requestNo);

  /**
   * 顧客フラグ取得
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-07-30
   * @updatedAt : 2021-07-30
   * @param maintenanceRequestsId
   * @return
   */
  Map<String, Object> selectCustomerFlag(@Param("tenantId") Object tenantId,
      @Param("requestNo") Object requestNo);

  /**
   * 顧客個人取得
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-02
   * @updatedAt : 2021-08-02
   * @param maintenanceRequestsId
   * @param maintenanceExist
   * @return
   */
  MaintenanceRequestsCustomersVo selectCustomerForIndividual(@Param("tenantId") Object tenantId,
      @Param("customerId") Object customerId);

  /**
   * 保全顧客個人取得
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-02
   * @updatedAt : 2021-08-02
   * @param maintenanceRequestsId
   * @param maintenanceExist
   * @return
   */
  MaintenanceRequestsCustomersVo selectMaintenanceRequestsCustomerForIndividual(
      @Param("tenantId") Object tenantId,
      @Param("requestNo") Object requestNo, @Param("type") String type);

  /**
   * 顧客法人取得
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-02
   * @updatedAt : 2021-08-02
   * @param maintenanceRequestsId
   * @param maintenanceExist
   * @return
   */
  MaintenanceRequestsCustomersVo selectCustomerForCorporate(@Param("tenantId") Object tenantId,
      @Param("customerId") Object customerId);

  /**
   * 保全顧客法人取得
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-02
   * @updatedAt : 2021-08-02
   * @param maintenanceRequestsId
   * @param maintenanceExist
   * @return
   */
  MaintenanceRequestsCustomersVo selectMaintenanceRequestsCustomerForCorporate(
      @Param("tenantId") Object tenantId,
      @Param("requestNo") Object requestNo, @Param("type") String type);

  /**
   * 保全顧客取得
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-04
   * @updatedAt : 2021-08-04
   * @param maintenanceRequestsId
   * @return
   */
  List<Map<String, Object>> selectMaintenanceRequestsCustomerForMap(
      @Param("tenantId") Object tenantId,
      @Param("requestNo") Object requestNo);

  /**
   * 保全顧客個人取得
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-04
   * @updatedAt : 2021-08-04
   * @param maintenanceRequestsId
   * @return
   */
  List<Map<String, Object>> selectMaintenanceRequestsCustomerIndividualForMap(
      @Param("tenantId") Object tenantId,
      @Param("requestNo") Object requestNo);

  /**
   * 保全顧客法人取得
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-04
   * @updatedAt : 2021-08-04
   * @param maintenanceRequestsId
   * @return
   */
  List<Map<String, Object>> selectMaintenanceRequestsCustomerCorporateForMap(
      @Param("tenantId") Object tenantId,
      @Param("requestNo") Object requestNo);

  /**
   * 保全顧客登録
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-02
   * @updatedAt : 2021-08-02
   * @param param
   */
  void insertMaintenanceRequestsCustomer(Map<String, Object> param);

  /**
   * 保全顧客個人登録
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-03
   * @updatedAt : 2021-08-03
   * @param param
   */
  void insertMaintenanceRequestsCustomerIndividual(Map<String, Object> param);

  /**
   * 保全顧客法人登録
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-03
   * @updatedAt : 2021-08-03
   * @param param
   */
  void insertMaintenanceRequestsCustomerCorporate(Map<String, Object> param);

  /**
   * 顧客追加
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-04
   * @updatedAt : 2021-08-04
   * @param param
   */
  void insertCustomer(Map<String, Object> param);

  /**
   * 顧客個人追加
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-04
   * @updatedAt : 2021-08-04
   * @param param
   */
  void insertCustomerIndividual(Map<String, Object> param);

  /**
   * 顧客法人追加
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-04
   * @updatedAt : 2021-08-04
   * @param param
   */
  void insertCustomerCorporate(Map<String, Object> param);

  /**
   * 顧客個人更新
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-04
   * @updatedAt : 2021-08-04
   * @param param
   * @return
   */
  int updateCustomer(Map<String, Object> param);

  /**
   * 顧客個人更新
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-04
   * @updatedAt : 2021-08-04
   * @param param
   * @return
   */
  int updateCustomerIndividual(Map<String, Object> param);

  /**
   * 顧客法人更新
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-04
   * @updatedAt : 2021-08-04
   * @param param
   * @return
   */
  int updateCustomerCorporate(Map<String, Object> param);

  /**
   * 顧客取得
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-04
   * @updatedAt : 2021-08-04
   * @param param
   * @return
   */
  Map<String, Object> selectCustomerForBefore(Map<String, Object> param);

  /**
   * 顧客個人取得
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-04
   * @updatedAt : 2021-08-04
   * @param param
   * @return
   */
  Map<String, Object> selectCustomerIndividualForBefore(Map<String, Object> param);

  /**
   * 顧客法人取得
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-04
   * @updatedAt : 2021-08-04
   * @param param
   * @return
   */
  Map<String, Object> selectCustomerCorporateForBefore(Map<String, Object> param);

  /**
   * 証券の顧客変更
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-04
   * @updatedAt : 2021-08-04
   * @param param
   * @return
   */
  int updateContractsCustomerId(Map<String, Object> param);

  /**
   * 顧客の後見人変更
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-04
   * @updatedAt : 2021-08-04
   * @param param
   * @return
   */
  int updateCustomersIndividualGuardianId(@Param("tenantId") Object tenantId,
      @Param("requestNo") Object requestNo,
      @Param("role") Object role, @Param("customerId") Object customerId,
      @Param("updatedBy") Object updatedBy);

  /**
   * 証券の後見人顧客変更
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-04
   * @updatedAt : 2021-08-04
   * @param param
   * @return
   */
  int updateMaintenanceRequestsCustomerIndividualGuardianId(
      @Param("tenantId") Object tenantId,
      @Param("requestNo") Object requestNo, @Param("role") Object role,
      @Param("customerId") Object customerId, @Param("updatedBy") Object updatedBy);
}
