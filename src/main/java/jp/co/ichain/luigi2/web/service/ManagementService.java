package jp.co.ichain.luigi2.web.service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.Gson;
import jp.co.ichain.luigi2.dto.ResultListDto;
import jp.co.ichain.luigi2.dto.ResultOneDto;
import jp.co.ichain.luigi2.exception.WebDataException;
import jp.co.ichain.luigi2.exception.WebParameterException;
import jp.co.ichain.luigi2.mapper.CommonMapper;
import jp.co.ichain.luigi2.mapper.DocumentsMapper;
import jp.co.ichain.luigi2.mapper.ManagementMapper;
import jp.co.ichain.luigi2.mapper.ManagementOc006Mapper;
import jp.co.ichain.luigi2.resources.CodeMasterResources;
import jp.co.ichain.luigi2.resources.Luigi2DateCode;
import jp.co.ichain.luigi2.resources.Luigi2ErrorCode;
import jp.co.ichain.luigi2.resources.Luigi2ReceiverEmailInfo.ClientMailType;
import jp.co.ichain.luigi2.resources.Luigi2ReceiverEmailInfo.ReceiverInfo;
import jp.co.ichain.luigi2.resources.Luigi2TableInfo;
import jp.co.ichain.luigi2.resources.Luigi2TableInfo.TableInfo;
import jp.co.ichain.luigi2.resources.TenantResources;
import jp.co.ichain.luigi2.resources.code.Luigi2CodeAntiSocialForceCheck.RetrievalMethod;
import jp.co.ichain.luigi2.resources.code.Luigi2CodeAntiSocialForceCheck.Return;
import jp.co.ichain.luigi2.resources.code.Luigi2CodeContractLog.ContactTransactionCode;
import jp.co.ichain.luigi2.resources.code.Luigi2CodeContractLog.ReasonCode;
import jp.co.ichain.luigi2.resources.code.Luigi2CodeContractLog.ReasonGroupCode;
import jp.co.ichain.luigi2.resources.code.Luigi2CodeContracts.ContractStatus;
import jp.co.ichain.luigi2.resources.code.Luigi2CodeCustomers.CorporateIndividualFlag;
import jp.co.ichain.luigi2.resources.code.Luigi2CodeMaintenanceRequests.FirstAssessmentResults;
import jp.co.ichain.luigi2.resources.code.Luigi2CodeMaintenanceRequests.SecondAssessmentResults;
import jp.co.ichain.luigi2.resources.code.Luigi2CodeMaintenanceRequests.TransactionCode;
import jp.co.ichain.luigi2.resources.code.Luigi2CodeMaintenanceRequestsCustomer.BeforeAfter;
import jp.co.ichain.luigi2.resources.code.Luigi2CodeMaintenanceRequestsCustomer.Role;
import jp.co.ichain.luigi2.resources.code.Luigi2CodePayment.PaymentMethodCode;
import jp.co.ichain.luigi2.resources.code.Luigi2CodeSalesProducts.RoundingType;
import jp.co.ichain.luigi2.service.AntiSocialForceCheckService;
import jp.co.ichain.luigi2.service.AwsS3Service;
import jp.co.ichain.luigi2.service.AwsS3Service.Documents;
import jp.co.ichain.luigi2.service.ContractLogService;
import jp.co.ichain.luigi2.service.NotificationService;
import jp.co.ichain.luigi2.service.NumberingService;
import jp.co.ichain.luigi2.service.OpenDateService;
import jp.co.ichain.luigi2.service.ServiceObjectsService;
import jp.co.ichain.luigi2.si.function.FunctionUtils;
import jp.co.ichain.luigi2.util.CollectionUtils;
import jp.co.ichain.luigi2.util.NumberUtils;
import jp.co.ichain.luigi2.vo.AntiSocialForceCheckVo;
import jp.co.ichain.luigi2.vo.MaintenanceRequestsCustomersVo;
import jp.co.ichain.luigi2.vo.MaintenanceRequestsTransferVo;
import jp.co.ichain.luigi2.vo.MaintenanceRequestsVo;
import lombok.val;

/**
 * 保全サービス
 * 
 * @author : [AOT] s.paku
 * @createdAt : 2021-06-28
 * @updatedAt : 2021-06-28
 */
@Service
public class ManagementService {

  @Autowired
  ManagementMapper mapper;

  @Autowired
  ServiceObjectsService serviceObjectsService;

  @Autowired
  ManagementOc006Mapper mapperForOc006;

  @Autowired
  CommonMapper commonMapper;

  @Autowired
  DocumentsMapper documentsMapper;

  @Autowired
  AwsS3Service awsS3Service;

  @Autowired
  NotificationService notificationService;

  @Autowired
  OpenDateService openDateService;

  @Autowired
  TenantResources tenantResources;

  @Autowired
  FunctionUtils functionUtils;

  @Autowired
  AntiSocialForceCheckService antiSocialForceCheckService;

  @Autowired
  ContractLogService contractLogService;

  @Autowired
  NumberingService numberingService;

  @Autowired
  CodeMasterResources codeMasterResources;

  @SuppressWarnings("serial")
  static final Map<String, String> CORPORATE_INDIVIDUAL_ID_MAP = new HashMap<String, String>() {
    {
      put("contractorCorporateIndividualFlag", "contractorCustomerId");
      put("contractorGuardianCorporateIndividualFlag", "contractorGuardianId");
      put("insuredCorporateIndividualFlag", "insuredCustomerId");
      put("insuredGuardianCorporateIndividualFlag", "insuredGuardianId");
    }
  };
  @SuppressWarnings("serial")
  static final Map<String, String> CORPORATE_INDIVIDUAL_CHANGEFG_MAP =
      new HashMap<String, String>() {
        {
          put("contractorCorporateIndividualFlag", "contractorCustomerChangeFg");
          put("contractorGuardianCorporateIndividualFlag", "contractorGuardianCustomerChangeFg");
          put("insuredCorporateIndividualFlag", "insuredCustomerChangeFg");
          put("insuredGuardianCorporateIndividualFlag", "insuredGuardianCustomerChangeFg");
        }
      };
  @SuppressWarnings("serial")
  static final Map<String, String> ROLE_CORPORATE_INDIVIDUAL_MAP = new HashMap<String, String>() {
    {
      put("contractorCorporateIndividualFlag", Role.PH.name());
      put("contractorGuardianCorporateIndividualFlag", Role.PG.name());
      put("insuredCorporateIndividualFlag", Role.IN.name());
      put("insuredGuardianCorporateIndividualFlag", Role.IG.name());

      put(Role.PH.name(), "contractorCorporateIndividualFlag");
      put(Role.PG.name(), "contractorGuardianCorporateIndividualFlag");
      put(Role.IN.name(), "insuredCorporateIndividualFlag");
      put(Role.IG.name(), "insuredGuardianCorporateIndividualFlag");
    }
  };
  @SuppressWarnings("serial")
  static final Map<String, String> ROLE_OBJECT_MAP = new HashMap<String, String>() {
    {
      put("contractorCustomer", Role.PH.name());
      put("contractorGuardianCustomer", Role.PG.name());
      put("insuredCustomer", Role.IN.name());
      put("insuredGuardianCustomer", Role.IG.name());
    }
  };

  /**
   * 保全一覧取得
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-06-29
   * @updatedAt : 2021-06-29
   * @param paramMap
   * @return
   * @throws SecurityException
   * @throws IllegalArgumentException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws IOException
   */
  @Transactional(transactionManager = "luigi2TransactionManager", readOnly = true)
  public ResultListDto<MaintenanceRequestsVo> searchMaintenanceRequests(
      Map<String, Object> paramMap) throws SecurityException, IllegalArgumentException,
      IllegalAccessException, InstantiationException, IOException {
    val result = new ResultListDto<MaintenanceRequestsVo>();

    result.setItems(mapper.searchMaintenanceRequests(paramMap));
    result.setTotalCount(mapper.searchMaintenanceRequestsTotalCount(paramMap));
    return result;
  }

  /**
   * 保全登録
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-06-30
   * @updatedAt : 2021-06-30
   * @param paramMap
   * @return
   * @throws SecurityException
   * @throws IllegalArgumentException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws IOException
   * @throws DecoderException
   * @throws InvalidKeySpecException
   * @throws NoSuchPaddingException
   * @throws InvalidAlgorithmParameterException
   * @throws BadPaddingException
   * @throws IllegalBlockSizeException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeyException
   */
  @SuppressWarnings("unchecked")
  @Transactional(transactionManager = "luigi2TransactionManager", rollbackFor = Exception.class)
  public ResultListDto<MaintenanceRequestsVo> registerMaintenanceRequests(
      Map<String, Object> paramMap)
      throws SecurityException, IllegalArgumentException, IllegalAccessException,
      InstantiationException, IOException, InvalidKeyException, NoSuchAlgorithmException,
      IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException,
      NoSuchPaddingException, InvalidKeySpecException, DecoderException {


    val tenantId = (Integer) paramMap.get("tenantId");
    // requestNo採番
    paramMap.put("requestNo", numberingService.getLockTable(TableInfo.MaintenanceRequests, tenantId,
        paramMap.get("updatedBy")));
    // DB insert
    mapper.insertMaintenanceRequests(paramMap);

    // file upload
    for (val multiFile : CollectionUtils.safe((List<MultipartFile>) paramMap.get("fileList"))) {
      awsS3Service.upload(Documents.MAINTENANCE, multiFile.getInputStream(),
          multiFile.getOriginalFilename(), tenantId, paramMap.get("requestNo"),
          paramMap.get("updatedBy"));
    }

    // contract log
    String reasonGroupCode = null;
    switch (TransactionCode.get((String) paramMap.get("transactionCode"))) {
      case DELETE:
      case RETRACTION:
        // 解除
        reasonGroupCode = ReasonGroupCode.DELETE.toString();
        break;
      case TRANSFER:
        // 名義住所変更
        reasonGroupCode = ReasonGroupCode.TRANSFER.toString();
        break;
      case RECIPIENT:
        // 受取人変更
        reasonGroupCode = ReasonGroupCode.RECIPIENT.toString();
        break;
      case CANCEL:
        // 解約
        reasonGroupCode = ReasonGroupCode.CANCEL.toString();
        break;
      case PAYMENTMETHOD:
        // 払込方法変更
        reasonGroupCode = ReasonGroupCode.PAYMENTMETHOD.toString();
        break;
      default:
        reasonGroupCode = "";
    }
    contractLogService.registerContractLog(tenantId, (String) paramMap.get("contractNo"),
        reasonGroupCode, ReasonCode.MAINTENANCE_REQUESTS.toString(),
        ContactTransactionCode.MAINTENANCE_REQUESTS.toString(), "SC_002",
        paramMap.get("updatedBy"));
    return new ResultListDto<MaintenanceRequestsVo>();
  }

  /**
   * 保全申請契約情報取得
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-10-14
   * @updatedAt : 2021-10-14
   * @param paramMap
   * @return
   */
  @Transactional(transactionManager = "luigi2TransactionManager", readOnly = true)
  public ResultOneDto<Map<String, Object>> getContractInfo(Map<String, Object> paramMap) {
    val result = new ResultOneDto<Map<String, Object>>();
    result.item = mapper.selectContractInfo(paramMap);
    return result;
  }

  /**
   * 保全支払い変更取得
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-07-06
   * @updatedAt : 2021-07-06
   * @param paramMap
   * @return
   */
  @Transactional(transactionManager = "luigi2TransactionManager", readOnly = true)
  public ResultOneDto<MaintenanceRequestsVo> getMaintenanceRequests(Map<String, Object> paramMap) {
    val result = new ResultOneDto<MaintenanceRequestsVo>();
    result.item = getSelectMaintenanceRequests(paramMap);

    if (StringUtils.isEmpty(result.item.getBillingInfo()) == false) {
      Gson gson = new Gson();
      val billingInfo = gson.fromJson(result.item.getBillingInfo(), Map.class);
      result.item.setSmartClaimId((String) billingInfo.get("smartClaimId"));
      result.item.setCifId((String) billingInfo.get("cifId"));
      result.item.setBillingInfo(null);
    }
    return result;
  }

  /**
   * 保全支払い変更1次査定
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-07-06
   * @updatedAt : 2021-07-06
   * @param paramMap
   * @return
   * @throws JsonProcessingException
   * @throws JsonMappingException
   */
  @Transactional(transactionManager = "luigi2TransactionManager", rollbackFor = Exception.class)
  public ResultOneDto<MaintenanceRequestsVo> modifyFirstMaintenanceRequestsForPayment(
      Map<String, Object> paramMap)
      throws JsonMappingException, JsonProcessingException, WebParameterException {

    val smartClaimId = (String) paramMap.get("smartClaimId");
    if (StringUtils.isEmpty(smartClaimId) == false) {
      val cifId = (String) paramMap.get("cifId");
      val map = new HashMap<String, Object>();
      map.put("smartClaimId", smartClaimId);
      map.put("cifId", cifId);
      Gson gson = new Gson();
      paramMap.put("billingInfo", gson.toJson(map));
    }

    modifyFirstMaintenanceRequests(paramMap);

    contractLogService.registerContractLogForMaintenanceRequests((Integer) paramMap.get("tenantId"),
        (String) paramMap.get("requestNo"), ReasonGroupCode.PAYMENTMETHOD.toString(),
        ReasonCode.MAINTENANCE_REQUESTS.toString(), "02", "SC_006", paramMap.get("updatedBy"));
    return new ResultListDto<MaintenanceRequestsVo>();
  }

  /**
   * 保全支払い変更2次査定
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-07-06
   * @updatedAt : 2021-07-06
   * @param paramMap
   * @return
   * @throws JsonProcessingException
   * @throws JsonMappingException
   */
  @Transactional(transactionManager = "luigi2TransactionManager", rollbackFor = Exception.class)
  public ResultOneDto<MaintenanceRequestsVo> modifySecondMaintenanceRequestsForPayment(
      Map<String, Object> paramMap) throws JsonMappingException, JsonProcessingException {
    // 保全取得
    val tenantId = (Integer) paramMap.get("tenantId");
    val maintenanceVo = mapper.selectMaintenanceRequests(tenantId, paramMap.get("requestNo"));
    // 顧客Email変更
    changeCustomerMail(paramMap, maintenanceVo);

    // 保全更新
    if (mapper.updateSecondMaintenanceRequests(paramMap) < 1) {
      throw new WebDataException(Luigi2ErrorCode.D0005);
    }

    if (SecondAssessmentResults.COMPLATE.toString()
        .equals(paramMap.get("secondAssessmentResults"))) {
      // 通知登録
      val now = Calendar.getInstance();
      now.setTime((Date) paramMap.get("batchDate"));

      // SBS 決済情報変更
      if (FirstAssessmentResults.COMPLATE.toString()
          .equals(maintenanceVo.getFirstAssessmentResults())) {
        modifySecondMaintenanceRequestsForComplate(paramMap);

        paramMap.put("billingInfo", maintenanceVo.getBillingInfo());
        mapper.updateContractsBillingInfo(paramMap);
      }

      now.add(Calendar.DAY_OF_YEAR, 1);
      paramMap.put("notificationDate", now.getTime());
      paramMap.put("comment", maintenanceVo.getCommunicationColumn());
      paramMap.put("totalRefundAmount", maintenanceVo.getTotalRefundAmount());

      if (FirstAssessmentResults.INADEQUACY.toString()
          .equals(maintenanceVo.getFirstAssessmentResults())) {
        // 不備
        notificationService.registerNotification("HZ_011", paramMap,
            ReceiverInfo.sender_emails_to_clients, ClientMailType.policy_management);
        contractLogService.registerContractLogForMaintenanceRequests(maintenanceVo, tenantId,
            ReasonGroupCode.PAYMENTMETHOD.toString(), ReasonCode.MAINTENANCE_REQUESTS.toString(),
            ContactTransactionCode.MAINTENANCE_INADEQUACY.toString(), "SC_008",
            paramMap.get("updatedBy"));
      } else if (FirstAssessmentResults.CANCEL.toString()
          .equals(maintenanceVo.getFirstAssessmentResults())) {
        paramMap.put("transanctionName", codeMasterResources.getName(tenantId,
            "maintenance_requests-transaction_code", maintenanceVo.getTransactionCode()));
        notificationService.registerNotification("HZ_012", paramMap,
            ReceiverInfo.sender_emails_to_clients, ClientMailType.policy_management,
            "transanctionName");
        contractLogService.registerContractLogForMaintenanceRequests(maintenanceVo, tenantId,
            ReasonGroupCode.PAYMENTMETHOD.toString(), ReasonCode.MAINTENANCE_REQUESTS.toString(),
            ContactTransactionCode.MAINTENANCE_CANCEL.toString(), "SC_008",
            paramMap.get("updatedBy"));
      } else if (FirstAssessmentResults.COMPLATE.toString()
          .equals(maintenanceVo.getFirstAssessmentResults())) {
        // 正常
        notificationService.registerNotification("HZ_004", paramMap,
            ReceiverInfo.sender_emails_to_clients, ClientMailType.policy_management);
        contractLogService.registerContractLogForMaintenanceRequests(maintenanceVo, tenantId,
            ReasonGroupCode.PAYMENTMETHOD.toString(), ReasonCode.MAINTENANCE_REQUESTS.toString(),
            ContactTransactionCode.MAINTENANCE_SECOND.toString(), "SC_008",
            paramMap.get("updatedBy"));
      }
    } else if (SecondAssessmentResults.REJECT.toString()
        .equals(paramMap.get("secondAssessmentResults"))) {
      // 差し戻し
      contractLogService.registerContractLogForMaintenanceRequests(maintenanceVo, tenantId,
          ReasonGroupCode.PAYMENTMETHOD.toString(), ReasonCode.MAINTENANCE_REQUESTS.toString(),
          "04", "SC_008", paramMap.get("updatedBy"));
    }
    return new ResultListDto<MaintenanceRequestsVo>();
  }

  /**
   * 保全解約取得
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-07-19
   * @updatedAt : 2021-07-19
   * @param paramMap
   * @return
   * @throws SecurityException
   * @throws IllegalArgumentException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws IOException
   * @throws InvocationTargetException
   */
  @Transactional(transactionManager = "luigi2TransactionManager", readOnly = true)
  public ResultOneDto<MaintenanceRequestsVo> selectTerminationDate(Map<String, Object> paramMap)
      throws SecurityException, IllegalArgumentException, IllegalAccessException,
      InstantiationException, IOException, InvocationTargetException {
    val result = new ResultOneDto<MaintenanceRequestsVo>();
    result.item = getSelectMaintenanceRequests(paramMap);

    val refundAmountInfo =
        mapper.selectRefundAmountInfo(paramMap.get("tenantId"), paramMap.get("requestNo"));
    if (refundAmountInfo != null) {
      result.item.setBankAccountName(refundAmountInfo.getBankAccountName());
      result.item.setBankAccountNo(refundAmountInfo.getBankAccountNo());
      result.item.setBankAccountType(refundAmountInfo.getBankAccountType());
      result.item.setBankBranchCode(refundAmountInfo.getBankBranchCode());
      result.item.setBankCode(refundAmountInfo.getBankCode());
    }

    result.item.setTerminationBaseDate((Date) functionUtils.get("function", "getTerminationDate",
        (Integer) paramMap.get("tenantId"), result.item.getSalesPlanCode(),
        result.item.getSalesPlanTypeCode(), Luigi2DateCode.C00001,
        (Date) paramMap.get("onlineDate"), result.item.getIssueDate()));
    return result;
  }

  /**
   * 保全解約1次査定
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-07-08
   * @updatedAt : 2021-07-08
   * @param paramMap
   * @return
   * @throws InvocationTargetException
   * @throws IllegalArgumentException
   * @throws IllegalAccessException
   * @throws JsonProcessingException
   * @throws JsonMappingException
   */
  @Transactional(transactionManager = "luigi2TransactionManager", rollbackFor = Exception.class)
  public ResultOneDto<MaintenanceRequestsVo> modifyFirstMaintenanceRequestsForCancel(
      Map<String, Object> paramMap) throws JsonMappingException, JsonProcessingException,
      IllegalAccessException, IllegalArgumentException, InvocationTargetException {

    modifyFirstMaintenanceRequests(paramMap);

    // 契約情報を解約
    val maintenanceVo =
        mapper.selectMaintenanceRequests(paramMap.get("tenantId"), paramMap.get("requestNo"));
    val terminationDate =
        functionUtils.get("function", "getTerminationDate", (Integer) paramMap.get("tenantId"),
            maintenanceVo.getSalesPlanCode(), maintenanceVo.getSalesPlanTypeCode(),
            Luigi2DateCode.C00001, (Date) paramMap.get("onlineDate"), maintenanceVo.getIssueDate());
    paramMap.put("terminationBaseDate", ((Date) terminationDate).getTime());
    // 解約払戻金セット
    settingRefundAmountForCancel(paramMap);

    mapper.insertRefundAmount(paramMap);
    // contract log
    contractLogService.registerContractLogForMaintenanceRequests((Integer) paramMap.get("tenantId"),
        (String) paramMap.get("requestNo"), ReasonGroupCode.CANCEL.toString(),
        ReasonCode.MAINTENANCE_REQUESTS.toString(),
        ContactTransactionCode.MAINTENANCE_FIRST.toString(), "SC_011", paramMap.get("updatedBy"));
    return new ResultListDto<MaintenanceRequestsVo>();
  }

  /**
   * 保全解約2次査定
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-07-08
   * @updatedAt : 2021-07-08
   * @param paramMap
   * @return
   * @throws SecurityException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws InvocationTargetException
   * @throws IllegalArgumentException
   * @throws JsonProcessingException
   * @throws JsonMappingException
   */
  @Transactional(transactionManager = "luigi2TransactionManager", rollbackFor = Exception.class)
  public ResultOneDto<MaintenanceRequestsVo> modifySecondMaintenanceRequestsForCancel(
      Map<String, Object> paramMap) throws InstantiationException, IllegalAccessException,
      SecurityException, JsonMappingException, JsonProcessingException, IllegalArgumentException,
      InvocationTargetException {
    // 保全取得
    val tenantId = (Integer) paramMap.get("tenantId");
    val maintenanceVo = mapper.selectMaintenanceRequests(tenantId, paramMap.get("requestNo"));
    // 顧客Email変更
    changeCustomerMail(paramMap, maintenanceVo);

    // 保全更新
    if (mapper.updateSecondMaintenanceRequests(paramMap) < 1) {
      throw new WebDataException(Luigi2ErrorCode.D0005);
    }

    if (SecondAssessmentResults.COMPLATE.toString()
        .equals(paramMap.get("secondAssessmentResults"))) {
      val now = Calendar.getInstance();
      now.setTime((Date) paramMap.get("batchDate"));
      if (FirstAssessmentResults.COMPLATE.toString()
          .equals(maintenanceVo.getFirstAssessmentResults())) {
        modifySecondMaintenanceRequestsForComplate(paramMap);

        // 払戻金（refund_amount）更新
        paramMap.put("dueDate", now.getTime());
        paramMap.put("paymentDate", openDateService.getOpenDate(tenantId, now.getTime(), 2));
        mapper.updateSecondRefundAmount(paramMap);

        // 契約情報を解約
        val terminationDate = functionUtils.get("function", "getTerminationDate",
            (Integer) paramMap.get("tenantId"), maintenanceVo.getSalesPlanCode(),
            maintenanceVo.getSalesPlanTypeCode(), Luigi2DateCode.C00001,
            (Date) paramMap.get("onlineDate"), maintenanceVo.getIssueDate());
        paramMap.put("terminationBaseDate", ((Date) terminationDate).getTime());
        // 解約払戻金セット
        settingRefundAmountForCancel(paramMap);
        mapper.insertRefundAmount(paramMap);

        // 契約情報を解約
        paramMap.put("contractStatus", ContractStatus.CANCEL.toInt());
        mapper.updateContractsStatus(paramMap);

        paramMap.put("terminationDate", ((Date) terminationDate).getTime());
      }

      // 通知登録
      now.add(Calendar.DAY_OF_YEAR, 1);
      paramMap.put("notificationDate", now.getTime());
      paramMap.put("comment", maintenanceVo.getCommunicationColumn());
      paramMap.put("totalRefundAmount", maintenanceVo.getTotalRefundAmount());

      if (FirstAssessmentResults.INADEQUACY.toString()
          .equals(maintenanceVo.getFirstAssessmentResults())) {
        notificationService.registerNotification("HZ_011", paramMap,
            ReceiverInfo.sender_emails_to_clients, ClientMailType.policy_management);
        contractLogService.registerContractLogForMaintenanceRequests(maintenanceVo, tenantId,
            ReasonGroupCode.CANCEL.toString(), ReasonCode.MAINTENANCE_REQUESTS.toString(),
            ContactTransactionCode.MAINTENANCE_INADEQUACY.toString(), "SC_013",
            paramMap.get("updatedBy"));
      } else if (FirstAssessmentResults.CANCEL.toString()
          .equals(maintenanceVo.getFirstAssessmentResults())) {
        paramMap.put("transanctionName", codeMasterResources.getName(tenantId,
            "maintenance_requests-transaction_code", maintenanceVo.getTransactionCode()));
        notificationService.registerNotification("HZ_012", paramMap,
            ReceiverInfo.sender_emails_to_clients, ClientMailType.policy_management,
            "transanctionName");
        contractLogService.registerContractLogForMaintenanceRequests(maintenanceVo, tenantId,
            ReasonGroupCode.TRANSFER.toString(), ReasonCode.MAINTENANCE_REQUESTS.toString(),
            ContactTransactionCode.MAINTENANCE_CANCEL.toString(), "SC_013",
            paramMap.get("updatedBy"));
      } else if (FirstAssessmentResults.COMPLATE.toString()
          .equals(maintenanceVo.getFirstAssessmentResults())) {
        notificationService.registerNotification("HZ_005", paramMap,
            ReceiverInfo.sender_emails_to_clients, ClientMailType.policy_management,
            "terminationDate", "totalRefundAmount");
        contractLogService.registerContractLogForMaintenanceRequests(maintenanceVo, tenantId,
            ReasonGroupCode.CANCEL.toString(), ReasonCode.MAINTENANCE_REQUESTS.toString(),
            ContactTransactionCode.MAINTENANCE_SECOND.toString(), "SC_013",
            paramMap.get("updatedBy"));
      }
    } else if (SecondAssessmentResults.REJECT.toString()
        .equals(paramMap.get("secondAssessmentResults"))) {
      // 差し戻し
      contractLogService.registerContractLogForMaintenanceRequests(maintenanceVo, tenantId,
          ReasonGroupCode.CANCEL.toString(), ReasonCode.MAINTENANCE_REQUESTS.toString(),
          ContactTransactionCode.MAINTENANCE_RETURN.toString(), "SC_013",
          paramMap.get("updatedBy"));
    }
    return new ResultListDto<MaintenanceRequestsVo>();
  }

  /**
   * 保全解約払戻金算出
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-10-15
   * @updatedAt : 2021-10-15
   * @param paramMap
   * @return
   */
  private void settingRefundAmountForCancel(Map<String, Object> paramMap) {
    // 1.2.1 契約解約の未経過保険料の計算
    long refundAmount = mapper.selectRefundAmount(paramMap);

    // 1.2.2 払戻金計算要素取得
    val salesProductsMap = mapper.selectSalesProducts(paramMap);
    // 1.2.3 払戻金計算
    // refund_amount：surrender_charge＝ROUND((MIN(最高解約控除額,MAX(最低解約控除額,(解約控除割合*未経過保険料))))
    double surrenderCharge =
        NumberUtils.getDoubleToBigDecimal(salesProductsMap.get("surrenderChargeRate"))
            * refundAmount;
    long minSurrenderCharge = NumberUtils.getLong(salesProductsMap.get("minSurrenderCharge"));
    long maxSurrenderCharge = NumberUtils.getLong(salesProductsMap.get("maxSurrenderCharge"));
    surrenderCharge = surrenderCharge > minSurrenderCharge ? surrenderCharge : minSurrenderCharge;
    surrenderCharge = surrenderCharge < maxSurrenderCharge ? surrenderCharge : maxSurrenderCharge;

    long roundSurrenderCharge = 0;
    RoundingType roundingType = null;
    if (salesProductsMap.get("roundingType") == null) {
      roundingType = RoundingType.ROUND;
    } else {
      roundingType = RoundingType.get((String) salesProductsMap.get("roundingType"));
    }
    switch (roundingType) {
      case ROUNDUP:
        roundSurrenderCharge = (long) Math.ceil(surrenderCharge);
        break;
      case ROUNDDOWN:
        roundSurrenderCharge = (long) Math.floor(surrenderCharge);
        break;
      case ROUND:
      default:
        roundSurrenderCharge = Math.round(surrenderCharge);
        break;
    }
    long totalRefundAmount = refundAmount;
    totalRefundAmount = refundAmount - roundSurrenderCharge;
    totalRefundAmount = totalRefundAmount > 0 ? totalRefundAmount : 0;

    paramMap.put("refundAmount", refundAmount);
    paramMap.put("surrenderCharge", surrenderCharge);
    paramMap.put("totalRefundAmount", totalRefundAmount);
  }

  /**
   * 返金額取得
   * 
   * @author : [AOT] s.paku, g.kim
   * @createdAt : 2021-07-12
   * @updatedAt : 2021-10-21
   * @param paramMap
   * @return
   * @throws SecurityException
   * @throws IllegalArgumentException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws IOException
   * @throws InvocationTargetException
   */
  @Transactional(transactionManager = "luigi2TransactionManager", readOnly = true)
  public ResultOneDto<Long> getRefundAmount(Map<String, Object> paramMap)
      throws SecurityException, IllegalArgumentException, IllegalAccessException,
      InstantiationException, IOException, InvocationTargetException {
    val result = new ResultOneDto<Long>();

    result.item = (long) functionUtils.get("function", "getRefundAmount",
        (Integer) paramMap.get("tenantId"), paramMap);
    return result;
  }

  /**
   * 保全解除1次査定
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-07-12
   * @updatedAt : 2021-07-12
   * @param paramMap
   * @return
   * @throws JsonProcessingException
   * @throws JsonMappingException
   */
  @Transactional(transactionManager = "luigi2TransactionManager", rollbackFor = Exception.class)
  public ResultOneDto<MaintenanceRequestsVo> modifyFirstMaintenanceRequestsForDeleted(
      Map<String, Object> paramMap) throws JsonMappingException, JsonProcessingException {

    Integer refundAmount = (Integer) paramMap.get("refundAmount");
    if (refundAmount != null && refundAmount > 0) {
      paramMap.put("paymentMethodCode", PaymentMethodCode.ACCOUNT_TRANFER.toString());
      paramMap.put("totalRefundAmount", paramMap.get("refundAmount"));
      mapper.insertRefundAmount(paramMap);
    }

    paramMap.put("applyDate", paramMap.get("terminationBaseDate"));
    modifyFirstMaintenanceRequests(paramMap);

    // contract log
    contractLogService.registerContractLogForMaintenanceRequests((Integer) paramMap.get("tenantId"),
        (String) paramMap.get("requestNo"), ReasonGroupCode.DELETE.toString(),
        ReasonCode.MAINTENANCE_REQUESTS.toString(),
        ContactTransactionCode.MAINTENANCE_FIRST.toString(), "SC_016", paramMap.get("updatedBy"));
    return new ResultListDto<MaintenanceRequestsVo>();
  }

  /**
   * 保全解除2次査定
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-07-12
   * @updatedAt : 2021-07-12
   * @param paramMap
   * @return
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws SecurityException
   * @throws InvocationTargetException
   * @throws IllegalArgumentException
   * @throws JsonProcessingException
   * @throws JsonMappingException
   */
  @Transactional(transactionManager = "luigi2TransactionManager", rollbackFor = Exception.class)
  public ResultOneDto<MaintenanceRequestsVo> modifySecondMaintenanceRequestsForDeleted(
      Map<String, Object> paramMap) throws InstantiationException, IllegalAccessException,
      SecurityException, JsonMappingException, JsonProcessingException, IllegalArgumentException,
      InvocationTargetException {
    // 保全取得
    val tenantId = (Integer) paramMap.get("tenantId");
    val maintenanceVo = mapper.selectMaintenanceRequests(tenantId, paramMap.get("requestNo"));
    // 顧客Email変更
    changeCustomerMail(paramMap, maintenanceVo);
    // 削除日計算
    val date = (Date) functionUtils.get("function", "getTerminationDate",
        (Integer) paramMap.get("tenantId"), maintenanceVo.getSalesPlanCode(),
        maintenanceVo.getSalesPlanTypeCode(), Luigi2DateCode.C00002,
        maintenanceVo.getTerminationBaseDate(), maintenanceVo.getIssueDate());
    paramMap.put("terminationBaseDate", date.getTime());

    // 保全更新
    if (mapper.updateSecondMaintenanceRequests(paramMap) < 1) {
      throw new WebDataException(Luigi2ErrorCode.D0005);
    }

    if (SecondAssessmentResults.COMPLATE.toString()
        .equals(paramMap.get("secondAssessmentResults"))) {
      if (FirstAssessmentResults.COMPLATE.toString()
          .equals(maintenanceVo.getFirstAssessmentResults())) {
        modifySecondMaintenanceRequestsForComplate(paramMap);

        val now = Calendar.getInstance();
        now.setTime((Date) paramMap.get("batchDate"));
        // 払戻金（refund_amount）更新
        paramMap.put("dueDate", now.getTime());
        paramMap.put("refundAmount", mapper.selectRefundAmount(paramMap));
        mapper.updateSecondRefundAmount(paramMap);

        // 契約情報を解除更新
        paramMap.put("contractStatus", ContractStatus.DELETE.toInt());
        mapper.updateContractsStatus(paramMap);

        // 通知登録
        now.add(Calendar.DAY_OF_YEAR, 1);
        paramMap.put("notificationDate", now.getTime());
        paramMap.put("comment", maintenanceVo.getCommunicationColumn());

        notificationService.registerNotification("HZ_007", paramMap,
            ReceiverInfo.sender_emails_to_clients, ClientMailType.policy_management,
            "terminationDate", "totalRefundAmount");
        contractLogService.registerContractLogForMaintenanceRequests(maintenanceVo, tenantId,
            ReasonGroupCode.DELETE.toString(), ReasonCode.MAINTENANCE_REQUESTS.toString(),
            ContactTransactionCode.MAINTENANCE_SECOND.toString(), "SC_018",
            paramMap.get("updatedBy"));
      }
    } else if (SecondAssessmentResults.REJECT.toString()
        .equals(paramMap.get("secondAssessmentResults"))) {
      // 差し戻し
      contractLogService.registerContractLogForMaintenanceRequests(maintenanceVo, tenantId,
          ReasonGroupCode.DELETE.toString(), ReasonCode.MAINTENANCE_REQUESTS.toString(),
          ContactTransactionCode.MAINTENANCE_RETURN.toString(), "SC_018",
          paramMap.get("updatedBy"));
    }
    return new ResultListDto<MaintenanceRequestsVo>();
  }

  /**
   * 保全住所変更顧客取得
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-01
   * @updatedAt : 2021-08-01
   * @param paramMap
   * @return
   */
  @Transactional(transactionManager = "luigi2TransactionManager", readOnly = true)
  public ResultOneDto<MaintenanceRequestsVo> getCustomerForTransfer(Map<String, Object> paramMap) {
    val result = new ResultOneDto<MaintenanceRequestsVo>();
    result.item = getSelectMaintenanceRequests(paramMap);

    val tenantId = paramMap.get("tenantId");
    val requestNo = paramMap.get("requestNo");
    val corporateIndividualFlagMap =
        mapperForOc006.selectMaintenanceRequestsCorporateIndividualFlag(tenantId, requestNo);
    val customerFlagMap = mapperForOc006.selectCustomerFlag(tenantId, requestNo);

    val customerMap = new HashMap<String, MaintenanceRequestsCustomersVo>();
    val customerChangeMap = new HashMap<String, Boolean>();
    var corporateFlag = false;
    var maintenanceExist = false;

    for (val corporateIndividualFlagName : CORPORATE_INDIVIDUAL_ID_MAP.keySet()) {
      if (corporateIndividualFlagMap != null
          && corporateIndividualFlagMap.get(corporateIndividualFlagName) != null) {
        corporateFlag = CorporateIndividualFlag.CORPORATION.toString()
            .equals(corporateIndividualFlagMap.get(corporateIndividualFlagName));
        maintenanceExist = true;
        customerChangeMap.put(corporateIndividualFlagName, true);
      } else {
        corporateFlag = CorporateIndividualFlag.CORPORATION.toString()
            .equals(customerFlagMap.get(corporateIndividualFlagName));
        customerChangeMap.put(corporateIndividualFlagName, false);
      }

      MaintenanceRequestsCustomersVo customer = null;

      if (corporateFlag) {
        // 法人
        if (maintenanceExist) {
          customer = mapperForOc006.selectMaintenanceRequestsCustomerForCorporate(tenantId,
              requestNo, ROLE_CORPORATE_INDIVIDUAL_MAP.get(corporateIndividualFlagName));
        } else {
          customer = mapperForOc006.selectCustomerForCorporate(tenantId,
              customerFlagMap.get(CORPORATE_INDIVIDUAL_ID_MAP.get(corporateIndividualFlagName)));
        }
      } else {
        // 個人
        if (maintenanceExist) {
          customer = mapperForOc006.selectMaintenanceRequestsCustomerForIndividual(tenantId,
              requestNo, ROLE_CORPORATE_INDIVIDUAL_MAP.get(corporateIndividualFlagName));
        } else {
          customer = mapperForOc006.selectCustomerForIndividual(tenantId,
              customerFlagMap.get(CORPORATE_INDIVIDUAL_ID_MAP.get(corporateIndividualFlagName)));
        }
      }
      if (customer != null) {
        customer.setCorporateIndividualFlag(
            corporateFlag ? CorporateIndividualFlag.CORPORATION.toString()
                : CorporateIndividualFlag.INDIVIDUAL.toString());

        if (maintenanceExist == false
            && "contractorCorporateIndividualFlag".equals(corporateIndividualFlagName)) {
          customer.setRelationship(result.item.getRelationship());
        }
      }

      customerMap.put(corporateIndividualFlagName, customer);
    }
    val transferVo = new MaintenanceRequestsTransferVo();
    transferVo
        .setContractorCustomerChangeFg(customerChangeMap.get("contractorCorporateIndividualFlag"));
    transferVo.setContractorCustomer(customerMap.get("contractorCorporateIndividualFlag"));
    transferVo.setContractorGuardianCustomerChangeFg(
        customerChangeMap.get("contractorGuardianCorporateIndividualFlag"));
    transferVo.setContractorGuardianCustomer(
        customerMap.get("contractorGuardianCorporateIndividualFlag"));
    transferVo.setInsuredCustomerChangeFg(customerChangeMap.get("insuredCorporateIndividualFlag"));
    transferVo.setInsuredCustomer(customerMap.get("insuredCorporateIndividualFlag"));
    transferVo.setInsuredGuardianCustomerChangeFg(
        customerChangeMap.get("insuredGuardianCorporateIndividualFlag"));
    transferVo
        .setInsuredGuardianCustomer(customerMap.get("insuredGuardianCorporateIndividualFlag"));

    result.item.setTransfer(transferVo);
    return result;
  }

  /**
   * 保全住所変更1次査定
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-01
   * @updatedAt : 2021-07-01
   * @param paramMap
   * @return
   * @throws IOException
   * @throws ClientProtocolException
   */
  @Transactional(transactionManager = "luigi2TransactionManager", rollbackFor = Exception.class)
  public ResultOneDto<MaintenanceRequestsVo> modifyFirstMaintenanceRequestsForTransfer(
      Map<String, Object> paramMap) throws ClientProtocolException, IOException {

    modifyFirstMaintenanceRequests(paramMap);

    this.registMaintenanceRequestsCustomer(paramMap, "contractorCustomer");
    this.registMaintenanceRequestsCustomer(paramMap, "contractorGuardianCustomer");
    this.registMaintenanceRequestsCustomer(paramMap, "insuredCustomer");
    this.registMaintenanceRequestsCustomer(paramMap, "insuredGuardianCustomer");
    // contract log
    contractLogService.registerContractLogForMaintenanceRequests((Integer) paramMap.get("tenantId"),
        (String) paramMap.get("requestNo"), ReasonGroupCode.TRANSFER.toString(),
        ReasonCode.MAINTENANCE_REQUESTS.toString(),
        ContactTransactionCode.MAINTENANCE_FIRST.toString(), "SC_021", paramMap.get("updatedBy"));
    return new ResultListDto<MaintenanceRequestsVo>();
  }

  /**
   * 保全住所変更2次査定
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-03
   * @updatedAt : 2021-08-03
   * @param paramMap
   * @return
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws SecurityException
   * @throws InvocationTargetException
   * @throws IllegalArgumentException
   * @throws JsonProcessingException
   * @throws JsonMappingException
   * @throws ParseException
   */
  @Transactional(transactionManager = "luigi2TransactionManager", rollbackFor = Exception.class)
  public ResultOneDto<MaintenanceRequestsVo> modifySecondMaintenanceRequestsForTransfer(
      Map<String, Object> paramMap) throws InstantiationException, IllegalAccessException,
      SecurityException, JsonMappingException, JsonProcessingException, IllegalArgumentException,
      InvocationTargetException, ParseException {
    // 保全取得
    val tenantId = (Integer) paramMap.get("tenantId");
    val requestNo = paramMap.get("requestNo");
    val maintenanceVo = mapper.selectMaintenanceRequests(tenantId, requestNo);
    // 顧客Email変更
    changeCustomerMail(paramMap, maintenanceVo);
    // 保全更新
    if (mapper.updateSecondMaintenanceRequests(paramMap) < 1) {
      throw new WebDataException(Luigi2ErrorCode.D0005);
    }

    if (SecondAssessmentResults.COMPLATE.toString()
        .equals(paramMap.get("secondAssessmentResults"))) {
      if (FirstAssessmentResults.COMPLATE.toString()
          .equals(maintenanceVo.getFirstAssessmentResults())) {
        modifySecondMaintenanceRequestsForComplate(paramMap);

        // 保全顧客取得
        val mrCustomerSubMap = new HashMap<Integer, Map<String, Object>>();
        for (val mrCustomerSub : CollectionUtils.safe(mapperForOc006
            .selectMaintenanceRequestsCustomerIndividualForMap(tenantId, requestNo))) {
          mrCustomerSubMap.put((Integer) mrCustomerSub.get("sequenceNo"), mrCustomerSub);
        }
        for (val mrCustomerSub : CollectionUtils.safe(
            mapperForOc006.selectMaintenanceRequestsCustomerCorporateForMap(tenantId, requestNo))) {
          mrCustomerSubMap.put((Integer) mrCustomerSub.get("sequenceNo"), mrCustomerSub);
        }

        // 顧客更新
        val updatedBy = paramMap.get("updatedBy");
        val changeContractsCustomerIdMap = new HashMap<String, Object>();
        val customerFlagMap = mapperForOc006.selectCustomerFlag(tenantId, requestNo);
        for (val mrCustomer : CollectionUtils
            .safe(mapperForOc006.selectMaintenanceRequestsCustomerForMap(tenantId, requestNo))) {

          val role = (String) mrCustomer.get("role");
          val sequenceNo = (Integer) mrCustomer.get("sequenceNo");
          val customerSubMap = mrCustomerSubMap.get(sequenceNo);
          // 共通データセット
          customerSubMap.put("updatedBy", updatedBy);
          mrCustomer.put("updatedBy", updatedBy);

          // relationship
          if (Role.PH.name().equals(role)) {
            changeContractsCustomerIdMap.put("relationship", mrCustomer.get("relationship"));
          }

          var customerId = (String) mrCustomer.get("customerId");
          if (customerId == null) {
            // customerId 生成
            customerId = numberingService.getLockTable(TableInfo.Customers, tenantId, updatedBy);
            mrCustomer.put("customerId", customerId);
            // 顧客追加
            mapperForOc006.insertCustomer(mrCustomer);

            customerSubMap.put("customerId", customerId);
            if (CorporateIndividualFlag.INDIVIDUAL.toString()
                .equals(mrCustomer.get("corporateIndividualFlag"))) {
              // 顧客個人追加
              mapperForOc006.insertCustomerIndividual(customerSubMap);
            } else {
              // 顧客法人追加
              mapperForOc006.insertCustomerCorporate(customerSubMap);
            }
          } else {
            Map<String, Object> beforeCustomerMap = new HashMap<String, Object>();
            // 保全顧客、Before追加
            beforeCustomerMap = mapperForOc006.selectCustomerForBefore(mrCustomer);
            beforeCustomerMap.put("requestNo", requestNo);
            beforeCustomerMap.put("role", role);
            beforeCustomerMap.put("sequenceNo", sequenceNo);
            beforeCustomerMap.put("beforeAfter", "B");
            beforeCustomerMap.put("updatedBy", paramMap.get("updatedBy"));
            mapperForOc006.insertMaintenanceRequestsCustomer(beforeCustomerMap);

            val beforeFlag = customerFlagMap.get(ROLE_CORPORATE_INDIVIDUAL_MAP.get(role));
            // 保全顧客Sub、Before追加
            val beforeCustomerSubMap =
                CorporateIndividualFlag.INDIVIDUAL.toString().equals(beforeFlag)
                    ? mapperForOc006.selectCustomerIndividualForBefore(mrCustomer)
                    : mapperForOc006.selectCustomerCorporateForBefore(mrCustomer);
            if (beforeCustomerSubMap != null) {
              beforeCustomerSubMap.put("sequenceNo", sequenceNo);
              beforeCustomerSubMap.put("requestNo", requestNo);
              beforeCustomerSubMap.put("beforeAfter", "B");
              beforeCustomerSubMap.put("updatedBy", paramMap.get("updatedBy"));
              // 保全顧客Sub更新
              if (CorporateIndividualFlag.INDIVIDUAL.toString().equals(beforeFlag)) {
                mapperForOc006.insertMaintenanceRequestsCustomerIndividual(beforeCustomerSubMap);
                mapperForOc006.updateCustomerIndividual(customerSubMap);
              } else {
                if (beforeCustomerSubMap.get("rep10eDateOfBirth") != null) {
                  beforeCustomerSubMap.put("rep10eDateOfBirth",
                      ((java.sql.Date) beforeCustomerSubMap.get("rep10eDateOfBirth")).getTime());
                }
                mapperForOc006.insertMaintenanceRequestsCustomerCorporate(beforeCustomerSubMap);
                mapperForOc006.updateCustomerCorporate(customerSubMap);
              }
            } else {
              // 保全顧客Sub登録
              if (CorporateIndividualFlag.INDIVIDUAL.toString().equals(beforeFlag)) {
                // 顧客個人追加
                mapperForOc006.insertCustomerIndividual(customerSubMap);
              } else {
                // 顧客法人追加
                mapperForOc006.insertCustomerCorporate(customerSubMap);
              }
            }
            // 顧客更新
            mapperForOc006.updateCustomer(mrCustomer);
          }
          // 後見人ID登録&証券更新準備
          changeContractsCustomerIdMap.put(role, customerId);
        }
        // 証券変更
        changeContractsCustomerIdMap.put("tenantId", tenantId);
        changeContractsCustomerIdMap.put("requestNo", requestNo);
        changeContractsCustomerIdMap.put("updatedBy", updatedBy);
        if (changeContractsCustomerIdMap.containsKey(Role.PH.name())
            || changeContractsCustomerIdMap.containsKey(Role.IN.name())) {
          mapperForOc006.updateContractsCustomerId(changeContractsCustomerIdMap);
        }

        // 後見人ID更新
        if (changeContractsCustomerIdMap.get(Role.PG.name()) != null) {
          mapperForOc006.updateCustomersIndividualGuardianId(tenantId, requestNo, Role.PG.name(),
              changeContractsCustomerIdMap.get(Role.PG.name()), updatedBy);
          mapperForOc006.updateMaintenanceRequestsCustomerIndividualGuardianId(tenantId, requestNo,
              Role.PG.name(), changeContractsCustomerIdMap.get(Role.PG.name()), updatedBy);
        }
        if (changeContractsCustomerIdMap.get(Role.IG.name()) != null) {
          mapperForOc006.updateCustomersIndividualGuardianId(tenantId, requestNo, Role.IG.name(),
              changeContractsCustomerIdMap.get(Role.IG.name()), updatedBy);
          mapperForOc006.updateMaintenanceRequestsCustomerIndividualGuardianId(tenantId, requestNo,
              Role.IG.name(), changeContractsCustomerIdMap.get(Role.IG.name()), updatedBy);
        }
      }
      // 通知登録
      val now = Calendar.getInstance();
      now.setTime((Date) paramMap.get("batchDate"));
      now.add(Calendar.DAY_OF_YEAR, 1);
      paramMap.put("notificationDate", now.getTime());
      paramMap.put("comment", maintenanceVo.getCommunicationColumn());
      paramMap.put("totalRefundAmount", maintenanceVo.getTotalRefundAmount());

      if (FirstAssessmentResults.INADEQUACY.toString()
          .equals(maintenanceVo.getFirstAssessmentResults())) {
        notificationService.registerNotification("HZ_011", paramMap,
            ReceiverInfo.sender_emails_to_clients, ClientMailType.policy_management);
        contractLogService.registerContractLogForMaintenanceRequests(maintenanceVo, tenantId,
            ReasonGroupCode.TRANSFER.toString(), ReasonCode.MAINTENANCE_REQUESTS.toString(),
            ContactTransactionCode.MAINTENANCE_INADEQUACY.toString(), "SC_023",
            paramMap.get("updatedBy"));
      } else if (FirstAssessmentResults.CANCEL.toString()
          .equals(maintenanceVo.getFirstAssessmentResults())) {
        paramMap.put("transanctionName", codeMasterResources.getName(tenantId,
            "maintenance_requests-transaction_code", maintenanceVo.getTransactionCode()));
        notificationService.registerNotification("HZ_012", paramMap,
            ReceiverInfo.sender_emails_to_clients, ClientMailType.policy_management,
            "transanctionName");
        contractLogService.registerContractLogForMaintenanceRequests(maintenanceVo, tenantId,
            ReasonGroupCode.TRANSFER.toString(), ReasonCode.MAINTENANCE_REQUESTS.toString(),
            ContactTransactionCode.MAINTENANCE_CANCEL.toString(), "SC_023",
            paramMap.get("updatedBy"));
      } else if (FirstAssessmentResults.COMPLATE.toString()
          .equals(maintenanceVo.getFirstAssessmentResults())) {
        notificationService.registerNotification("HZ_008", paramMap,
            ReceiverInfo.sender_emails_to_clients, ClientMailType.policy_management);
        contractLogService.registerContractLogForMaintenanceRequests(maintenanceVo, tenantId,
            ReasonGroupCode.TRANSFER.toString(), ReasonCode.MAINTENANCE_REQUESTS.toString(),
            ContactTransactionCode.MAINTENANCE_SECOND.toString(), "SC_023",
            paramMap.get("updatedBy"));
      }
    } else if (SecondAssessmentResults.REJECT.toString()
        .equals(paramMap.get("secondAssessmentResults"))) {
      // 差し戻し
      contractLogService.registerContractLogForMaintenanceRequests(maintenanceVo, tenantId,
          ReasonGroupCode.TRANSFER.toString(), ReasonCode.MAINTENANCE_REQUESTS.toString(),
          ContactTransactionCode.MAINTENANCE_RETURN.toString(), "SC_023",
          paramMap.get("updatedBy"));
    }
    return new ResultListDto<MaintenanceRequestsVo>();
  }

  /**
   * 住所変更反社会チェック
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-05
   * @updatedAt : 2021-08-05
   * @param paramMap
   * @return
   * @throws ClientProtocolException
   * @throws IOException
   */
  @Transactional(transactionManager = "luigi2TransactionManager", rollbackFor = Exception.class)
  public ResultOneDto<Map<String, Boolean>> checkAntisocialForTransferApi(
      Map<String, Object> paramMap) throws ClientProtocolException, IOException {

    val result = new ResultOneDto<Map<String, Boolean>>();
    result.item = new HashMap<String, Boolean>();

    result.item.put("contractorCustomerAntisocialFg", this.checkAntisocialForTransfer(paramMap,
        "contractorCustomerChangeFg", "contractorCustomer"));
    result.item.put("insuredCustomerAntisocialFg",
        this.checkAntisocialForTransfer(paramMap, "insuredCustomerChangeFg", "insuredCustomer"));

    return result;
  }

  /**
   * 保全受取人取得
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-05
   * @updatedAt : 2021-08-05
   * @param paramMap
   * @return
   * @throws SecurityException
   * @throws IllegalArgumentException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws IOException
   */
  @Transactional(transactionManager = "luigi2TransactionManager", readOnly = true)
  public ResultOneDto<MaintenanceRequestsVo> getBeneficiaries(Map<String, Object> paramMap)
      throws SecurityException, IllegalArgumentException, IllegalAccessException,
      InstantiationException, IOException {
    val result = new ResultOneDto<MaintenanceRequestsVo>();
    result.item = getSelectMaintenanceRequests(paramMap);

    val items = mapper.selectBeneficiaries(paramMap);
    if (items != null) {
      for (int i = 0; i < items.size(); i++) {
        val item = items.get(i);
        item.setSocialSeq(i);
      }
    }
    result.item.setBeneficiariesList(mapper.selectBeneficiaries(paramMap));
    return result;
  }

  /**
   * 保全受取人1次査定
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-06
   * @updatedAt : 2021-08-06
   * @param paramMap
   * @return
   * @throws IOException
   * @throws ClientProtocolException
   */
  @SuppressWarnings("unchecked")
  @Transactional(transactionManager = "luigi2TransactionManager", rollbackFor = Exception.class)
  public ResultOneDto<MaintenanceRequestsVo> modifyFirstMaintenanceRequestsForBeneficiaries(
      Map<String, Object> paramMap) throws ClientProtocolException, IOException {

    modifyFirstMaintenanceRequests(paramMap);

    val beneficiariesList = (List<Map<String, Object>>) paramMap.get("beneficiariesList");
    val addBeneficiariesList = beneficiariesList != null
        ? beneficiariesList.stream().filter((b -> b.get("id") == null)).collect(Collectors.toList())
        : new ArrayList<Map<String, Object>>();
    val updateBeneficiariesList = beneficiariesList != null
        ? beneficiariesList.stream().filter((b -> b.get("id") != null)).collect(Collectors.toList())
        : new ArrayList<Map<String, Object>>();

    // 画面から削除された受取人削除
    mapper.deleteMaintenanceRequestsBeneficiaries(paramMap.get("tenantId"),
        paramMap.get("requestNo"), updateBeneficiariesList);

    // 保全受取人更新
    if (updateBeneficiariesList != null) {
      updateBeneficiariesList.forEach((item) -> {
        item.put("tenantId", paramMap.get("tenantId"));
        item.put("requestNo", paramMap.get("requestNo"));
        item.put("updatedBy", paramMap.get("updatedBy"));
        mapper.updateMaintenanceRequestsBeneficiaries(item);
      });
    }

    // 保全受取人挿入
    if (addBeneficiariesList != null) {
      addBeneficiariesList.forEach((item) -> {
        item.put("tenantId", paramMap.get("tenantId"));
        item.put("requestNo", paramMap.get("requestNo"));
        item.put("beforeAfter", BeforeAfter.A.name());
        item.put("updatedBy", paramMap.get("updatedBy"));
        mapper.insertMaintenanceRequestsBeneficiaries(item);
      });
    }
    // contract log
    contractLogService.registerContractLogForMaintenanceRequests((Integer) paramMap.get("tenantId"),
        (String) paramMap.get("requestNo"), "09", ReasonCode.MAINTENANCE_REQUESTS.toString(),
        ContactTransactionCode.MAINTENANCE_FIRST.toString(), "SC_026", paramMap.get("updatedBy"));
    return new ResultListDto<MaintenanceRequestsVo>();
  }

  /**
   * 保全受取人2次査定
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-10
   * @updatedAt : 2021-08-10
   * @param paramMap
   * @return
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws SecurityException
   * @throws InvocationTargetException
   * @throws IllegalArgumentException
   * @throws JsonProcessingException
   * @throws JsonMappingException
   */
  @Transactional(transactionManager = "luigi2TransactionManager", rollbackFor = Exception.class)
  public ResultOneDto<MaintenanceRequestsVo> modifySecondMaintenanceRequestsForBeneficiaries(
      Map<String, Object> paramMap) throws InstantiationException, IllegalAccessException,
      SecurityException, JsonMappingException, JsonProcessingException, IllegalArgumentException,
      InvocationTargetException {
    // 保全取得
    val tenantId = (Integer) paramMap.get("tenantId");
    val requestNo = paramMap.get("requestNo");
    val maintenanceVo = mapper.selectMaintenanceRequests(tenantId, requestNo);
    // 顧客Email変更
    changeCustomerMail(paramMap, maintenanceVo);
    // 保全更新
    if (mapper.updateSecondMaintenanceRequests(paramMap) < 1) {
      throw new WebDataException(Luigi2ErrorCode.D0005);
    }

    if (SecondAssessmentResults.COMPLATE.toString()
        .equals(paramMap.get("secondAssessmentResults"))) {
      if (FirstAssessmentResults.COMPLATE.toString()
          .equals(maintenanceVo.getFirstAssessmentResults())) {
        modifySecondMaintenanceRequestsForComplate(paramMap);

        // 受取人取得
        val beneficiarieList = mapper.selectBeneficiaries(paramMap);

        // Before登録用受取人取得
        val beneficiarieForBeforeList = mapper.selectBeneficiariesForBeforeInsert(paramMap);
        if (beneficiarieForBeforeList != null) {
          beneficiarieForBeforeList.forEach(item -> {
            item.put("tenantId", paramMap.get("tenantId"));
            item.put("requestNo", paramMap.get("requestNo"));
            item.put("beforeAfter", BeforeAfter.B.name());
            item.put("updatedBy", paramMap.get("updatedBy"));
            mapper.insertMaintenanceRequestsBeneficiaries(item);
          });
        }
        // 過去の受取人削除
        mapper.updateBeneficiariesForDelete(paramMap);

        // 受取人登録
        if (beneficiarieList != null) {
          mapper.insertBeneficiaries(paramMap.get("tenantId"), paramMap.get("updatedBy"),
              (Date) paramMap.get("onlineDate"), beneficiarieList);
        }
      }

      // 通知登録
      val now = Calendar.getInstance();
      now.setTime((Date) paramMap.get("batchDate"));
      now.add(Calendar.DAY_OF_YEAR, 1);
      paramMap.put("notificationDate", now.getTime());
      paramMap.put("comment", maintenanceVo.getCommunicationColumn());
      paramMap.put("totalRefundAmount", maintenanceVo.getTotalRefundAmount());

      if (FirstAssessmentResults.INADEQUACY.toString()
          .equals(maintenanceVo.getFirstAssessmentResults())) {
        notificationService.registerNotification("HZ_011", paramMap,
            ReceiverInfo.sender_emails_to_clients, ClientMailType.policy_management);
        contractLogService.registerContractLogForMaintenanceRequests(maintenanceVo, tenantId,
            ReasonGroupCode.RECIPIENT.toString(), ReasonCode.MAINTENANCE_REQUESTS.toString(),
            ContactTransactionCode.MAINTENANCE_INADEQUACY.toString(), "SC_028",
            paramMap.get("updatedBy"));
      } else if (FirstAssessmentResults.CANCEL.toString()
          .equals(maintenanceVo.getFirstAssessmentResults())) {
        paramMap.put("transanctionName", codeMasterResources.getName(tenantId,
            "maintenance_requests-transaction_code", maintenanceVo.getTransactionCode()));
        notificationService.registerNotification("HZ_012", paramMap,
            ReceiverInfo.sender_emails_to_clients, ClientMailType.policy_management,
            "transanctionName");
        contractLogService.registerContractLogForMaintenanceRequests(maintenanceVo, tenantId,
            ReasonGroupCode.TRANSFER.toString(), ReasonCode.MAINTENANCE_REQUESTS.toString(),
            ContactTransactionCode.MAINTENANCE_CANCEL.toString(), "SC_028",
            paramMap.get("updatedBy"));
      } else if (FirstAssessmentResults.COMPLATE.toString()
          .equals(maintenanceVo.getFirstAssessmentResults())) {
        notificationService.registerNotification("HZ_009", paramMap,
            ReceiverInfo.sender_emails_to_clients, ClientMailType.policy_management);
        contractLogService.registerContractLogForMaintenanceRequests(maintenanceVo, tenantId,
            ReasonGroupCode.RECIPIENT.toString(), ReasonCode.MAINTENANCE_REQUESTS.toString(),
            ContactTransactionCode.MAINTENANCE_SECOND.toString(), "SC_028",
            paramMap.get("updatedBy"));
      }
    } else if (SecondAssessmentResults.REJECT.toString()
        .equals(paramMap.get("secondAssessmentResults"))) {
      // 差し戻し
      contractLogService.registerContractLogForMaintenanceRequests(maintenanceVo, tenantId,
          ReasonGroupCode.RECIPIENT.toString(), ReasonCode.MAINTENANCE_REQUESTS.toString(),
          ContactTransactionCode.MAINTENANCE_RETURN.toString(), "SC_028",
          paramMap.get("updatedBy"));
    }
    return new ResultListDto<MaintenanceRequestsVo>();
  }

  /**
   * 保全受取人反社会チェック
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-05
   * @updatedAt : 2021-08-05
   * @param paramMap
   * @return
   * @throws ClientProtocolException
   * @throws IOException
   */
  @SuppressWarnings("unchecked")
  @Transactional(transactionManager = "luigi2TransactionManager", rollbackFor = Exception.class)
  public ResultListDto<Map<String, Object>> checkAntisocialForBeneficiaries(
      Map<String, Object> paramMap) throws ClientProtocolException, IOException {

    val result = new ResultListDto<Map<String, Object>>();
    val items = new ArrayList<Map<String, Object>>();

    for (val item : (List<Map<String, Object>>) paramMap.get("beneficiariesList")) {
      val name = (String) paramMap.get("nameKnjSei") + (String) paramMap.get("nameKnjMei");
      AntiSocialForceCheckVo antiResult = null;
      if (CorporateIndividualFlag.INDIVIDUAL.toString()
          .equals(item.get("corporateIndividualFlag"))) {
        val dateOfBirth = item.get("dateOfBirth");
        antiResult = antiSocialForceCheckService.antisocialCheck((Integer) paramMap.get("tenantId"),
            name, dateOfBirth instanceof Long ? new Date((long) dateOfBirth) : (Date) dateOfBirth,
            (String) paramMap.get("addrKnjPref"));
      } else {
        antiResult = antiSocialForceCheckService.antisocialCheck((Integer) paramMap.get("tenantId"),
            name, (Date) paramMap.get("onlineDate"), "", RetrievalMethod.ACCORD_NAME.toString());
      }

      val map = new HashMap<String, Object>();
      map.put("socialSeq", item.get("socialSeq"));
      map.put("socialFg", Return.ACCORD_NAME.toString().equals(antiResult.getResultCode()));
      items.add(map);
    }

    result.setItems(items);
    return result;
  }

  /**
   * 保全顧客Email変更
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-04
   * @updatedAt : 2021-08-04
   * @param paramMap
   * @return
   */
  private void changeCustomerMail(Map<String, Object> paramMap,
      MaintenanceRequestsVo maintenanceVo) {
    paramMap.put("contractNo", maintenanceVo.getContractNo());
    paramMap.put("contractBranchNo", maintenanceVo.getContractBranchNo());
    // 顧客Email変更
    if (SecondAssessmentResults.COMPLATE.toString().equals(paramMap.get("secondAssessmentResults"))
        && maintenanceVo.getEmailForNotification() != null && maintenanceVo
            .getEmailForNotification().equals(maintenanceVo.getContractEmail()) == false) {
      paramMap.put("commentUnderweiter2", paramMap.get("commentUnderweiter2")
          + "\r登録されている契約者のメールアドレスと申請時のメールアドレスが異なっているため、メールアドレスを変更しました。");
      paramMap.put("email", maintenanceVo.getEmailForNotification());
      if (mapper.updateCustomerEmail(paramMap) < 1) {
        throw new WebDataException(Luigi2ErrorCode.D0005);
      }
    }
  }

  /**
   * 保全住所変更登録
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-01
   * @updatedAt : 2021-08-01
   * @param paramMap
   * @param changeFgName
   * @param customerName
   * @return
   * @throws IOException
   * @throws ClientProtocolException
   */
  @SuppressWarnings("unchecked")
  private void registMaintenanceRequestsCustomer(Map<String, Object> paramMap, String customerName)
      throws ClientProtocolException, IOException {

    val customer = (Map<String, Object>) paramMap.get(customerName);
    if (customer != null) {
      customer.put("tenantId", paramMap.get("tenantId"));
      customer.put("requestNo", paramMap.get("requestNo"));
      customer.put("beforeAfter", BeforeAfter.A.name());
      customer.put("updatedBy", paramMap.get("updatedBy"));

      val role = ROLE_OBJECT_MAP.get(customerName);
      customer.put("role", role);

      // 顧客登録
      mapperForOc006.insertMaintenanceRequestsCustomer(customer);
      if (CorporateIndividualFlag.INDIVIDUAL.toString()
          .equals(customer.get("corporateIndividualFlag"))) {
        // 個人登録
        mapperForOc006.insertMaintenanceRequestsCustomerIndividual(customer);
      } else {
        // 法人登録
        mapperForOc006.insertMaintenanceRequestsCustomerCorporate(customer);
      }
    }
  }

  /**
   * 反社会チェック
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-01
   * @updatedAt : 2021-08-01
   * @param paramMap
   * @param changeFgName
   * @param customerName
   * @return
   * @throws IOException
   * @throws ClientProtocolException
   */
  @SuppressWarnings("unchecked")
  private boolean checkAntisocialForTransfer(Map<String, Object> paramMap, String changeFgName,
      String customerName) throws ClientProtocolException, IOException {
    // 契約者の変更があった場合
    if ((boolean) paramMap.get(changeFgName)) {
      val customer = (Map<String, Object>) paramMap.get(customerName);
      val role = ROLE_OBJECT_MAP.get(customerName);

      // 反社会チェック
      val birthday = customer.get("dateOfBirth");
      val name = (String) customer.get("nameKnjSei") + (String) customer.get("nameKnjMei");
      if (role.equals(Role.PH.name()) || role.equals(Role.IN.name())) {
        val antiResult =
            antiSocialForceCheckService.antisocialCheck((Integer) paramMap.get("tenantId"), name,
                birthday instanceof Long ? new Date((long) birthday) : (Date) birthday,
                (String) customer.get("addrKnjPref"));
        return Return.ACCORD_ALL.toString().equals(antiResult.getResultCode());
      } else {
        val antiResult =
            antiSocialForceCheckService.antisocialCheck((Integer) paramMap.get("tenantId"), name,
                (Date) paramMap.get("onlineDate"), "", RetrievalMethod.ACCORD_NAME.toString());
        return Return.ACCORD_ALL.toString().equals(antiResult.getResultCode());
      }
    }
    return false;
  }

  /**
   * 保全詳細取得
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-05
   * @updatedAt : 2022-09-05
   * @param paramMap
   * @return
   */
  private MaintenanceRequestsVo getSelectMaintenanceRequests(Map<String, Object> paramMap) {
    val item = mapper.selectMaintenanceRequests(paramMap);

    paramMap.putAll(Luigi2TableInfo.getLockTable(TableInfo.MaintenanceDocuments));
    paramMap.put("ownerCode", item.getRequestNo());
    item.setDocumentsList(documentsMapper.selectDocuments(paramMap));
    item.setInherentList(mapper.selectMaintenanceRequestsServiceObjests(paramMap));
    return item;
  }

  /**
   * 共通１次査定更新
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2022/09/05
   * @updatedAt : 2022/09/05
   * @param paramMap
   * @throws WebDataException
   * @throws JsonProcessingException
   * @throws JsonMappingException
   */
  @SuppressWarnings("unchecked")
  private void modifyFirstMaintenanceRequests(Map<String, Object> paramMap) throws WebDataException,
      JsonMappingException, JsonProcessingException, WebParameterException {

    // 保全固有データ更新
    val inherentList = (List<Map<String, Object>>) paramMap.get("inherentList");
    if (inherentList != null) {
      // 固有データ修正
      val prevInherentList = mapper.selectMaintenanceRequestsServiceObjests(paramMap);
      val prevInherentMap = new LinkedHashMap<Integer, Map<String, Object>>();
      for (val inherent : prevInherentList) {
        prevInherentMap.put((Integer) inherent.get("sequenceNo"), inherent);
      }

      // 検証
      serviceObjectsService.validateInherentList((Integer) paramMap.get("tenantId"), inherentList);

      Gson gson = new Gson();
      for (val inherent : inherentList) {
        val sequenceNo = (Integer) inherent.get("sequenceNo");
        inherent.put("inherent", gson.toJson(inherent.get("inherent")));
        inherent.put("beforeAfter", "A");

        switch ((String) inherent.get("txType")) {
          case "U":
            val upi = prevInherentMap.get(sequenceNo);
            if (upi != null) {
              upi.put("inherent", inherent.get("inherent"));
            } else {
              prevInherentMap.put(sequenceNo, inherent);
            }
            break;
          case "D":
            val dpi = prevInherentMap.get(sequenceNo);
            if (dpi != null) {
              prevInherentMap.remove(sequenceNo);
              if ("U".equals(dpi.get("txType"))) {
                prevInherentMap.put(sequenceNo, inherent);
              }
            } else {
              prevInherentMap.put(sequenceNo, inherent);
            }
            break;
          case "C":
            prevInherentMap.put(sequenceNo, inherent);
            break;
          default:
            break;
        }
      }

      paramMap.put("inherentList", prevInherentMap.values());
      mapper.removeMaintenanceRequestsServiceObjests(paramMap);
      mapper.insertMaintenanceRequestsServiceObjests(paramMap);
    }

    // １次査定更新
    if (mapper.updateFirstMaintenanceRequests(paramMap) < 1) {
      throw new WebDataException(Luigi2ErrorCode.D0005);
    }
  }

  /**
   * 共通１次査定更新
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2022/09/05
   * @updatedAt : 2022/09/05
   * @param paramMap
   * @throws WebDataException
   * @throws JsonProcessingException
   * @throws JsonMappingException
   */
  private void modifySecondMaintenanceRequestsForComplate(Map<String, Object> paramMap)
      throws WebDataException, JsonMappingException, JsonProcessingException {

    // 固有データ修正
    val inherentList = mapper.selectMaintenanceRequestsServiceObjests(paramMap);

    // 固有データ before作成
    if (inherentList != null && inherentList.size() > 0) {
      val sequenceNoList = inherentList.stream().map(inherent -> {
        return inherent.get("sequenceNo");
      }).collect(Collectors.toList());

      paramMap.put("sequenceNoList", sequenceNoList);

      val originServiceObjectsList = serviceObjectsService.getServiceObjects(paramMap);
      val originInherentList = new ArrayList<Map<String, Object>>();
      val originServiceMap = new HashMap<Integer, String>();
      for (val inherent : inherentList) {
        originServiceMap.put((Integer) inherent.get("sequenceNo"), (String) inherent.get("txType"));
      }
      for (val so : CollectionUtils.safe(originServiceObjectsList)) {
        val inherent = new HashMap<String, Object>();
        inherent.put("inherent", so.getData());
        inherent.put("sequenceNo", so.getSequenceNo());
        inherent.put("txType", originServiceMap.get(so.getSequenceNo()));
        inherent.put("beforeAfter", "B");
        originInherentList.add(inherent);
      }

      if (originInherentList.size() > 0) {
        paramMap.put("inherentList", originInherentList);
        mapper.insertMaintenanceRequestsServiceObjests(paramMap);
      }

      paramMap.put("inherentList", inherentList);
      serviceObjectsService.execute(paramMap);
    }
  }
}
