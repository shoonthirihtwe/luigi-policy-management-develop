package jp.co.ichain.luigi2.function;

import java.util.Map;
import org.apache.sis.math.MathFunctions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jp.co.ichain.luigi2.exception.WebParameterException;
import jp.co.ichain.luigi2.mapper.ManagementMapper;
import jp.co.ichain.luigi2.resources.Luigi2ErrorCode;
import jp.co.ichain.luigi2.si.function.Function;
import lombok.val;

@Service
@Function
public class Oc005Function {

  @Autowired
  ManagementMapper mapper;

  /**
   * 返金額取得(Pm001)
   *
   * @author : [AOT] g.kim
   * @createdAt : 2021-10-22
   * @updatedAt : 2021-10-22
   * @param param
   * @return
   */
  @SuppressWarnings("unchecked")
  public Object getRefundAmount_pm001(Integer tenantId, Object... params) {

    if (params == null || params.length < 1) {
      throw new WebParameterException(Luigi2ErrorCode.V0000);
    }
    val paramMap = (Map<String, Object>) params[0];
    return mapper.selectRefundAmountPm001(paramMap);
  }

  /**
   * 返金額取得(Sm001)
   *
   * @author : [AOT] g.kim
   * @createdAt : 2021-10-22
   * @updatedAt : 2021-10-22
   * @param param
   * @return
   */
  @SuppressWarnings("unchecked")
  public Object getRefundAmount_sm001(Integer tenantId, Object... params) {

    double[] refundArray = {0.6, 0.6, 0.54, 0.49, 0.43, 0.38, 0.33, 0.27, 0.22, 0.16, 0.11, 0.05};
    if (params == null || params.length < 1) {
      throw new WebParameterException(Luigi2ErrorCode.V0000);
    }
    val paramMap = mapper.selectRefundAmountSm001((Map<String, Object>) params[0]);
    int refundMonth = ((Long) paramMap.get("refund_month")).intValue();
    if (refundArray.length - 1 < refundMonth) {
      return 0L;
    }
    return Double.valueOf((Long) paramMap.get("total_premium") * refundArray[refundMonth])
        .longValue();
  }
}
