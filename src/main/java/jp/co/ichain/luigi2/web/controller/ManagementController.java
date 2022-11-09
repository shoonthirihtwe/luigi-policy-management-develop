package jp.co.ichain.luigi2.web.controller;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartRequest;
import jp.co.ichain.luigi2.dto.ResultWebDto;
import jp.co.ichain.luigi2.resources.Luigi2Endpoint;
import jp.co.ichain.luigi2.util.ControllerUtils;
import jp.co.ichain.luigi2.web.service.ManagementService;

/**
 * 保全API
 * 
 * @author : [AOT] s.paku
 * @createdAt : 2021-06-28
 * @updatedAt : 2021-06-28
 */
@Controller
public class ManagementController {

  @Autowired
  ManagementService service;

  @Autowired
  ControllerUtils controllerUtils;

  /**
   * 保全一覧
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-06-28
   * @updatedAt : 2021-06-28
   * @param request
   * @param response
   * @param param
   * @return
   * @throws Exception
   */
  @Produces(MediaType.APPLICATION_JSON)
  @RequestMapping(value = "/OC00201", method = {RequestMethod.GET})
  public @ResponseBody ResultWebDto searchManagement(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    return controllerUtils.makeGetControllerHandler(request, Luigi2Endpoint.OC00201, (param) -> {
      return service.searchMaintenanceRequests(param);
    });
  }

  /**
   * 保全申請受付
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-06-28
   * @updatedAt : 2021-06-28
   * @param request
   * @param response
   * @param param
   * @return
   * @throws Exception
   */
  @RequestMapping(value = "/OC00101", method = {RequestMethod.POST})
  public @ResponseBody ResultWebDto registManagement(HttpServletRequest request,
      HttpServletResponse response, MultipartRequest mreq) throws Exception {
    return controllerUtils.makeFileControllerHandler().apply(request, Luigi2Endpoint.OC00101, mreq,
        (param) -> {
          return service.registerMaintenanceRequests(param);
        });
  }

  /**
   * 保全申込契約者情報取得
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-10-14
   * @updatedAt : 2021-10-14
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  @Produces(MediaType.APPLICATION_JSON)
  @RequestMapping(value = "/OC00102", method = {RequestMethod.GET})
  public @ResponseBody ResultWebDto getContractInfo(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    return controllerUtils.makeGetControllerHandler(request, Luigi2Endpoint.OC00102, (param) -> {
      return service.getContractInfo(param);
    });
  }

  /**
   * 保全詳細取得
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-06-28
   * @updatedAt : 2021-06-28
   * @param request
   * @param response
   * @param param
   * @return
   * @throws Exception
   */
  @Produces(MediaType.APPLICATION_JSON)
  @RequestMapping(value = "/OC00303", method = {RequestMethod.GET})
  public @ResponseBody ResultWebDto getManagement(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    return controllerUtils.makeGetControllerHandler(request, Luigi2Endpoint.OC00303, (param) -> {
      return service.getMaintenanceRequests(param);
    });
  }

  /**
   * 保全支払い変更1次査定
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-06-28
   * @updatedAt : 2021-06-28
   * @param request
   * @param response
   * @param param
   * @return
   * @throws Exception
   */
  @RequestMapping(value = "/OC00301", method = {RequestMethod.PUT})
  public @ResponseBody ResultWebDto modifyFirstManagementForPayment(HttpServletRequest request,
      HttpServletResponse response, @RequestBody Map<String, Object> param) throws Exception {
    return controllerUtils.makeCommonControllerHandler().apply(request, Luigi2Endpoint.OC00301,
        param, () -> {
          return service.modifyFirstMaintenanceRequestsForPayment(param);
        });
  }

  /**
   * 保全支払い変更2次査定
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-06-28
   * @updatedAt : 2021-06-28
   * @param request
   * @param response
   * @param param
   * @return
   * @throws Exception
   */
  @RequestMapping(value = "/OC00302", method = {RequestMethod.PUT})
  public @ResponseBody ResultWebDto modifySecondManagementForPayment(HttpServletRequest request,
      HttpServletResponse response, @RequestBody Map<String, Object> param) throws Exception {
    return controllerUtils.makeCommonControllerHandler().apply(request, Luigi2Endpoint.OC00302,
        param, () -> {
          return service.modifySecondMaintenanceRequestsForPayment(param);
        });
  }

  /**
   * 解約日取得
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-07-19
   * @updatedAt : 2021-07-19
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  @Produces(MediaType.APPLICATION_JSON)
  @RequestMapping(value = "/OC00403", method = {RequestMethod.GET})
  public @ResponseBody ResultWebDto getTerminationDate(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    return controllerUtils.makeGetControllerHandler(request, Luigi2Endpoint.OC00403, (param) -> {
      return service.selectTerminationDate(param);
    });
  }

  /**
   * 保全解約1次査定
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-06-28
   * @updatedAt : 2021-06-28
   * @param request
   * @param response
   * @param param
   * @return
   * @throws Exception
   */
  @RequestMapping(value = "/OC00401", method = {RequestMethod.PUT})
  public @ResponseBody ResultWebDto modifyFirstManagementForCancel(HttpServletRequest request,
      HttpServletResponse response, @RequestBody Map<String, Object> param) throws Exception {
    return controllerUtils.makeCommonControllerHandler().apply(request, Luigi2Endpoint.OC00401,
        param, () -> {
          return service.modifyFirstMaintenanceRequestsForCancel(param);
        });
  }

  /**
   * 保全解約2次査定
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-06-28
   * @updatedAt : 2021-06-28
   * @param request
   * @param response
   * @param param
   * @return
   * @throws Exception
   */
  @RequestMapping(value = "/OC00402", method = {RequestMethod.PUT})
  public @ResponseBody ResultWebDto modifySecondManagementForCancel(HttpServletRequest request,
      HttpServletResponse response, @RequestBody Map<String, Object> param) throws Exception {
    return controllerUtils.makeCommonControllerHandler().apply(request, Luigi2Endpoint.OC00402,
        param, () -> {
          return service.modifySecondMaintenanceRequestsForCancel(param);
        });
  }



  /**
   * 保全解除1次査定
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-07-12
   * @updatedAt : 2021-07-12
   * @param request
   * @param response
   * @param param
   * @return
   * @throws Exception
   */
  @RequestMapping(value = "/OC00501", method = {RequestMethod.PUT})
  public @ResponseBody ResultWebDto modifyFirstManagementForDeleted(HttpServletRequest request,
      HttpServletResponse response, @RequestBody Map<String, Object> param) throws Exception {
    return controllerUtils.makeCommonControllerHandler().apply(request, Luigi2Endpoint.OC00501,
        param, () -> {
          return service.modifyFirstMaintenanceRequestsForDeleted(param);
        });
  }

  /**
   * 保全解除2次査定
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-07-12
   * @updatedAt : 2021-07-12
   * @param request
   * @param response
   * @param param
   * @return
   * @throws Exception
   */
  @RequestMapping(value = "/OC00502", method = {RequestMethod.PUT})
  public @ResponseBody ResultWebDto modifySecondManagementForDeleted(HttpServletRequest request,
      HttpServletResponse response, @RequestBody Map<String, Object> param) throws Exception {
    return controllerUtils.makeCommonControllerHandler().apply(request, Luigi2Endpoint.OC00502,
        param, () -> {
          return service.modifySecondMaintenanceRequestsForDeleted(param);
        });
  }

  /**
   * 保全削除情報取得
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-19
   * @updatedAt : 2021-08-19
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  @Produces(MediaType.APPLICATION_JSON)
  @RequestMapping(value = "/OC00503", method = {RequestMethod.GET})
  public @ResponseBody ResultWebDto getManagementForDeleted(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    return controllerUtils.makeGetControllerHandler(request, Luigi2Endpoint.OC00303, (param) -> {
      return service.getMaintenanceRequests(param);
    });
  }

  /**
   * 返却金取得
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-07-12
   * @updatedAt : 2021-07-12
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  @Produces(MediaType.APPLICATION_JSON)
  @RequestMapping(value = "/OC00504", method = {RequestMethod.GET})
  public @ResponseBody ResultWebDto getRefundAmount(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    return controllerUtils.makeGetControllerHandler(request, Luigi2Endpoint.OC00504, (param) -> {
      return service.getRefundAmount(param);
    });
  }

  /**
   * 保全住所変更顧客取得
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-07-12
   * @updatedAt : 2021-07-12
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  @Produces(MediaType.APPLICATION_JSON)
  @RequestMapping(value = "/OC00603", method = {RequestMethod.GET})
  public @ResponseBody ResultWebDto getCustomerForTransfer(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    return controllerUtils.makeGetControllerHandler(request, Luigi2Endpoint.OC00603, (param) -> {
      return service.getCustomerForTransfer(param);
    });
  }

  /**
   * 保全住所変更1次査定
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-03
   * @updatedAt : 2021-08-03
   * @param request
   * @param response
   * @param param
   * @return
   * @throws Exception
   */
  @RequestMapping(value = "/OC00601", method = {RequestMethod.PUT})
  public @ResponseBody ResultWebDto modifyFirstManagementForTransfer(HttpServletRequest request,
      HttpServletResponse response, @RequestBody Map<String, Object> param) throws Exception {
    return controllerUtils.makeCommonControllerHandler().apply(request, Luigi2Endpoint.OC00601,
        param, () -> {
          return service.modifyFirstMaintenanceRequestsForTransfer(param);
        });
  }

  /**
   * 保全住所変更2次査定
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-04
   * @updatedAt : 2021-08-04
   * @param request
   * @param response
   * @param param
   * @return
   * @throws Exception
   */
  @RequestMapping(value = "/OC00602", method = {RequestMethod.PUT})
  public @ResponseBody ResultWebDto modifySecondManagementForTransfer(HttpServletRequest request,
      HttpServletResponse response, @RequestBody Map<String, Object> param) throws Exception {
    return controllerUtils.makeCommonControllerHandler().apply(request, Luigi2Endpoint.OC00602,
        param, () -> {
          return service.modifySecondMaintenanceRequestsForTransfer(param);
        });
  }

  /**
   * 住所変更反社会チェック
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-05
   * @updatedAt : 2021-08-05
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  @Produces(MediaType.APPLICATION_JSON)
  @RequestMapping(value = "/OC00604", method = {RequestMethod.GET})
  public @ResponseBody ResultWebDto checkAntisocialForTransfer(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    return controllerUtils.makeGetControllerHandler(request, Luigi2Endpoint.OC00604, (param) -> {
      return service.checkAntisocialForTransferApi(param);
    });
  }

  /**
   * 保全受取人取得
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-07-12
   * @updatedAt : 2021-07-12
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  @Produces(MediaType.APPLICATION_JSON)
  @RequestMapping(value = "/OC00803", method = {RequestMethod.GET})
  public @ResponseBody ResultWebDto getCustomerForBeneficiaries(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    return controllerUtils.makeGetControllerHandler(request, Luigi2Endpoint.OC00803, (param) -> {
      return service.getBeneficiaries(param);
    });
  }

  /**
   * 保全受取人変更1次査定
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-03
   * @updatedAt : 2021-08-03
   * @param request
   * @param response
   * @param param
   * @return
   * @throws Exception
   */
  @RequestMapping(value = "/OC00801", method = {RequestMethod.PUT})
  public @ResponseBody ResultWebDto modifyFirstManagementForBeneficiaries(
      HttpServletRequest request,
      HttpServletResponse response, @RequestBody Map<String, Object> param) throws Exception {
    return controllerUtils.makeCommonControllerHandler().apply(request, Luigi2Endpoint.OC00801,
        param, () -> {
          return service.modifyFirstMaintenanceRequestsForBeneficiaries(param);
        });
  }

  /**
   * 保全受取人変更2次査定
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-10
   * @updatedAt : 2021-08-10
   * @param request
   * @param response
   * @param param
   * @return
   * @throws Exception
   */
  @RequestMapping(value = "/OC00802", method = {RequestMethod.PUT})
  public @ResponseBody ResultWebDto modifySecondManagementForBeneficiaries(
      HttpServletRequest request,
      HttpServletResponse response, @RequestBody Map<String, Object> param) throws Exception {
    return controllerUtils.makeCommonControllerHandler().apply(request, Luigi2Endpoint.OC00802,
        param, () -> {
          return service.modifySecondMaintenanceRequestsForBeneficiaries(param);
        });
  }

  /**
   * 保全受取人反社会チェック
   * 
   * @author : [AOT] s.paku
   * @createdAt : 2021-08-11
   * @updatedAt : 2021-08-11
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  @Produces(MediaType.APPLICATION_JSON)
  @RequestMapping(value = "/OC00804", method = {RequestMethod.GET})
  public @ResponseBody ResultWebDto checkAntisocialForBeneficiaries(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    return controllerUtils.makeGetControllerHandler(request, Luigi2Endpoint.OC00804, (param) -> {
      return service.checkAntisocialForBeneficiaries(param);
    });
  }
}
