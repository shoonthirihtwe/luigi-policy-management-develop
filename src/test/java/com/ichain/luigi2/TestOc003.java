package com.ichain.luigi2;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import javax.sql.DataSource;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.mysql.MySqlConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;
import jp.co.ichain.luigi2.Luigi2Application;
import jp.co.ichain.luigi2.dto.ResultOneDto;
import jp.co.ichain.luigi2.test.TestScriptUtils;
import jp.co.ichain.luigi2.vo.MaintenanceRequestsVo;

/**
 * OC-003 払込経路変更
 *
 * @author : [VJP] タン
 * @createdAt : 2021-08-11
 * @updatedAt : 2021-08-11
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = Luigi2Application.class)
@TestPropertySource("classpath:application-common-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class TestOc003 {

  private MockMvc mockMvc;
  @Autowired
  private WebApplicationContext ctx;

  @Autowired
  private TestScriptUtils testScriptUtils;

  private Connection connection;
  private IDatabaseConnection idatabaseConnection;

  @Autowired
  @Qualifier("luigi2DataSource")
  private DataSource dataSource;

  private String sql = "sql/insert_OC003.sql";

  // 希望インデックス
  private int expectedIndex = 0;
  // #2_OC-003_SC-007,008,009
  // customers.corporate_individual_flag=2の場合(for case test case02-01)
  private int testCase21 = 2;
  // customers.corporate_individual_flag=1の場合(for case test case02-02)
  private int testCase22 = 1;
  // #4_OC-003_SC-007,008,009
  private int testCase4 = 4;
  // #6_OC-003_SC-007,008,009
  private int testCase6 = 6;
  // インデックス１
  private int index1 = 1;
  // インデックス２
  private int index2 = 2;
  // インデックス３
  private int index3 = 3;

  /**
   * OC-003 払込経路変更 setup
   *
   * @author : [VJP] タン
   * @throws ParseException
   * @throws IOException
   * @throws SecurityException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws UnsupportedEncodingException
   * @throws JsonProcessingException
   * @throws JsonMappingException
   * @throws JsonParseException
   * @createdAt : 2021-08-11
   * @updatedAt : 2021-08-11
   */
  @BeforeEach
  void setup() throws SQLException, DatabaseUnitException, JsonParseException, JsonMappingException,
      JsonProcessingException, UnsupportedEncodingException, InstantiationException,
      IllegalAccessException, SecurityException, IOException, ParseException {
    this.connection = dataSource.getConnection();
    this.idatabaseConnection = new MySqlConnection(connection, "luigi2_test");

    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.ctx)
        .addFilters(new CharacterEncodingFilter("UTF-8", true)).build();
    // テストに必要なSQL実行
    testScriptUtils.executeSqlScript(sql);

    // date setting
    testScriptUtils.updateBatchDate("2023-03-01", 1);
  }

  /**
   * OC-003 払込経路変更 テスト実行
   * 
   * #1_OC-003_SC-005,006,009
   * 
   * @author : [VJP] タン
   * @createdAt : 2021-08-11
   * @updatedAt : 2021-08-11
   */
  @Test
  void testOc00301() throws Exception {
    // 取得
    String requestJsonForGet = "json/request/oc003/req_OC003_01_get_data.json";
    String urlForGet = "/OC00303";
    String resultJsonForGet = "json/response/oc003/res_OC003_01_data.json";
    runTestCaseForGet(requestJsonForGet, urlForGet, resultJsonForGet, false);

    // 取得→一次査定
    String requestJsonForPut = "json/request/oc003/req_OC003_01_put_data.json";
    String urlForPut = "/OC00301";
    String resultXmlForPut = "xml/oc003/result_OC003_01.xml";
    runTestCaseForFirstPut(requestJsonForPut, urlForPut, resultXmlForPut, index2);
  }

  /**
   * OC-003 払込経路変更 テスト実行
   * 
   * #2_OC-003_SC-007,008,009
   * 
   * @author : [VJP] タン
   * @createdAt : 2021-08-11
   * @updatedAt : 2021-08-11
   */
  @Test
  void testOc0030201() throws Exception {
    // customers.corporate_individual_flag=2の場合
    // 一次査定
    testOc00301();

    // 一次査定→取得
    String requestJsonForGet = "json/request/oc003/req_OC003_01_get_data.json";
    String urlForGet = "/OC00303";
    String resultJsonForGet = "json/response/oc003/res_OC003_02_data.json";
    runTestCaseForGet(requestJsonForGet, urlForGet, resultJsonForGet, true);

    // 一次査定→取得→２時査定
    String requestJsonForPutSecond = "json/request/oc003/req_OC003_02_put_data.json";
    String urlForPutSecond = "/OC00302";
    String resultXmlForPutSecond = "xml/oc003/result_OC003_02.xml";
    runTestCaseForSecondPut(requestJsonForPutSecond, urlForPutSecond, resultXmlForPutSecond, index2,
        testCase21);
  }

  @Test
  void testOc0030202() throws Exception {
    // customers.corporate_individual_flag=1の場合
    testScriptUtils.executeSqlScript("sql/insert_OC003_01.sql");
    // 一次査定
    testOc00301();

    // 一次査定→取得
    String requestJsonForGet = "json/request/oc003/req_OC003_01_get_data.json";
    String urlForGet = "/OC00303";
    String resultJsonForGet = "json/response/oc003/res_OC003_02_data.json";
    runTestCaseForGet(requestJsonForGet, urlForGet, resultJsonForGet, true);

    // 一次査定→取得→２時査定
    String requestJsonForPutSecond = "json/request/oc003/req_OC003_02_put_data.json";
    String urlForPutSecond = "/OC00302";
    String resultXmlForPutSecond = "xml/oc003/result_OC003_02_02.xml";
    runTestCaseForSecondPut(requestJsonForPutSecond, urlForPutSecond, resultXmlForPutSecond, index2,
        testCase22);
  }

  /**
   * OC-003 払込経路変更 テスト実行
   * 
   * #3_OC-003_SC-005,006,009
   * 
   * @author : [VJP] タン
   * @createdAt : 2021-08-11
   * @updatedAt : 2021-08-11
   */
  @Test
  void testOc00303() throws Exception {
    // 取得
    String requestJsonForGet = "json/request/oc003/req_OC003_03_get_data.json";
    String urlForGet = "/OC00303";
    String resultJsonForGet = "json/response/oc003/res_OC003_03_data.json";
    runTestCaseForGet(requestJsonForGet, urlForGet, resultJsonForGet, false);

    // 取得→一次査定
    String requestJsonForPut = "json/request/oc003/req_OC003_03_put_data.json";
    String urlForPut = "/OC00301";
    String resultXmlForPut = "xml/oc003/result_OC003_03.xml";
    runTestCaseForFirstPut(requestJsonForPut, urlForPut, resultXmlForPut, index3);
  }

  /**
   * OC-003 払込経路変更 テスト実行
   * 
   * #4_OC-003_SC-007,008,009
   * 
   * @author : [VJP] タン
   * @createdAt : 2021-08-11
   * @updatedAt : 2021-08-11
   */
  @Test
  void testOc00304() throws Exception {
    // 一次査定
    testOc00303();

    // 一次査定→取得
    String requestJsonForGet = "json/request/oc003/req_OC003_03_get_data.json";
    String urlForGet = "/OC00303";
    String resultJsonForGet = "json/response/oc003/res_OC003_04_data.json";
    runTestCaseForGet(requestJsonForGet, urlForGet, resultJsonForGet, true);

    // 一次査定→取得→２時査定
    String requestJsonForPutSecond = "json/request/oc003/req_OC003_04_put_data.json";
    String urlForPutSecond = "/OC00302";
    String resultXmlForPutSecond = "xml/oc003/result_OC003_04.xml";
    runTestCaseForSecondPut(requestJsonForPutSecond, urlForPutSecond, resultXmlForPutSecond, index3,
        testCase4);
  }

  /**
   * OC-003 払込経路変更 テスト実行
   * 
   * #5_OC-003_SC-005,006,009
   * 
   * @author : [VJP] タン
   * @createdAt : 2021-08-11
   * @updatedAt : 2021-08-11
   */
  @Test
  void testOc00305() throws Exception {
    // 二次査定
    testOc00304();
    // 二次査定→取得
    String requestJsonForGet = "json/request/oc003/req_OC003_03_get_data.json";
    String urlForGet = "/OC00303";
    String resultJsonForGet = "json/response/oc003/res_OC003_05_data.json";
    runTestCaseForGet(requestJsonForGet, urlForGet, resultJsonForGet, false);

    // 二次査定→取得→一次査定
    String requestJsonForPut = "json/request/oc003/req_OC003_05_put_data.json";
    String urlForPut = "/OC00301";
    String resultXmlForPut = "xml/oc003/result_OC003_05.xml";
    runTestCaseForFirstPut(requestJsonForPut, urlForPut, resultXmlForPut, index3);
  }

  /**
   * OC-003 払込経路変更 テスト実行
   * 
   * #6_OC-003_SC-007,008,009
   * 
   * @author : [VJP] タン
   * @createdAt : 2021-08-11
   * @updatedAt : 2021-08-11
   */
  @Test
  void testOc00306() throws Exception {
    // 一次査定
    testOc00305();
    // 一次査定→取得
    String requestJsonForGet = "json/request/oc003/req_OC003_03_get_data.json";
    String urlForGet = "/OC00303";
    String resultJsonForGet = "json/response/oc003/res_OC003_06_data.json";
    runTestCaseForGet(requestJsonForGet, urlForGet, resultJsonForGet, true);

    // 一次査定→取得→２時査定
    String requestJsonForPutSecond = "json/request/oc003/req_OC003_06_put_data.json";
    String urlForPutSecond = "/OC00302";
    String resultXmlForPutSecond = "xml/oc003/result_OC003_06.xml";
    runTestCaseForSecondPut(requestJsonForPutSecond, urlForPutSecond, resultXmlForPutSecond, index3,
        testCase6);
  }

  /**
   * Mock request for get
   * 
   * @author : [VJP] タン
   * @param sql
   * @param url
   * @param requestJson
   * @return
   * @throws JsonParseException
   * @throws JsonMappingException
   * @throws UnsupportedEncodingException
   * @throws IOException
   * @throws Exception
   * @createdAt : 2021-08-11
   * @updatedAt : 2021-08-11
   */
  private MvcResult mockMvcForGet(String url, String requestJson) throws JsonParseException,
      JsonMappingException, UnsupportedEncodingException, IOException, Exception {
    // APIコール
    return mockMvc
        .perform(MockMvcRequestBuilders.get(url).header("x-frontend-domain", "localhost")
            .params(testScriptUtils.loadJsonToMultiValueMap(requestJson))
            .contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
  }

  /**
   * Mock request for put
   * 
   * @author : [VJP] タン
   * @param sql
   * @param url
   * @param requestJson
   * @return
   * @throws JsonParseException
   * @throws JsonMappingException
   * @throws UnsupportedEncodingException
   * @throws IOException
   * @throws Exception
   * @createdAt : 2021-08-11
   * @updatedAt : 2021-08-11
   */
  private MvcResult mockMvcForPut(String url, String requestJson) throws JsonParseException,
      JsonMappingException, UnsupportedEncodingException, IOException, Exception {
    // APIコール
    return mockMvc
        .perform(MockMvcRequestBuilders.put(url).header("x-frontend-domain", "localhost")
            .content(testScriptUtils.loadJsonToString(requestJson))
            .contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
  }

  /**
   * 色なテストケースを実行(取得)
   * 
   * @author : [VJP] タン
   * @param requestJson
   * @param url
   * @param resultJson
   * @param second 二次査定
   * @throws Exception
   * @createdAt : 2021-08-11
   * @updatedAt : 2021-08-11
   */
  private void runTestCaseForGet(String requestJson, String url, String resultJson, boolean second)
      throws Exception {
    MvcResult mvcResult = mockMvcForGet(url, requestJson);

    // 日付型を設定
    JsonDeserializer<Date> deser = (json, typeOfT, context) -> json == null ? null
        : new Date(Long.valueOf(json.getAsString().replaceAll("\\D", "")));
    Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, deser).create();
    Type responseType = new TypeToken<ResultOneDto<MaintenanceRequestsVo>>() {}.getType();

    // APIコール
    ResultOneDto<MaintenanceRequestsVo> res =
        gson.fromJson(mvcResult.getResponse().getContentAsString(), responseType);
    MaintenanceRequestsVo actual = res.getItem();

    // 予想結果ロード
    MaintenanceRequestsVo expected =
        gson.fromJson(testScriptUtils.loadJsonToString(resultJson), MaintenanceRequestsVo.class);

    // 結果比較
    // 受付日
    assertEquals(expected.getReceivedDate(), actual.getReceivedDate());
    // 証券番号
    assertEquals(expected.getContractNo(), actual.getContractNo());
    // 契約者名
    assertEquals(expected.getContractNameKnj(), actual.getContractNameKnj());
    // 契約者メールアドレス
    assertEquals(expected.getContractEmail(), actual.getContractEmail());
    // 経路
    assertEquals(expected.getPaymentMethodCode(), actual.getPaymentMethodCode());
    // 通知メールアドレス
    assertEquals(expected.getEmailForNotification(), actual.getEmailForNotification());
    if (second) {
      // 払込方法コード
      assertEquals(expected.getPaymentMethodCode(), actual.getPaymentMethodCode());
      // 銀行コード
      assertEquals(expected.getBankCode(), actual.getBankCode());
      // 支店コード
      assertEquals(expected.getBankBranchCode(), actual.getBankBranchCode());
      // 口座種別
      assertEquals(expected.getBankAccountType(), actual.getBankAccountType());
      // 口座番号
      assertEquals(expected.getBankAccountNo(), actual.getBankAccountNo());
      // 口座名義人
      assertEquals(expected.getBankAccountName(), actual.getBankAccountName());
      // 通信欄
      assertEquals(expected.getCommunicationColumn(), actual.getCommunicationColumn());
      // 一次査定コメント
      assertEquals(expected.getCommentUnderweiter1(), actual.getCommentUnderweiter1());
      // 一次査定結果
      assertEquals(expected.getFirstAssessmentResults(), actual.getFirstAssessmentResults());
    }
  }

  /**
   * 色なテストケースを実行(一次査定)
   * 
   * @author : [VJP] タン
   * @param requestJson
   * @param url
   * @param resultJson
   * @throws Exception
   * @createdAt : 2021-08-11
   * @updatedAt : 2021-08-11
   */
  private void runTestCaseForFirstPut(String requestJson, String url, String resultXml,
      int actualIndex) throws Exception {
    mockMvcForPut(url, requestJson);
    // 結果テーブルデータ
    IDataSet databaseDataSet = this.idatabaseConnection.createDataSet();
    // 保全申請
    ITable actualTable = databaseDataSet.getTable("maintenance_requests");
    // 予想結果ロード
    try (InputStream is = getClass().getClassLoader().getResourceAsStream(resultXml)) {
      IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(is);
      // 予想結果テーブルデータ
      ITable expectedTable = expectedDataSet.getTable("maintenance_requests");

      // 予想結果と値をEqual比較
      // tenant_id
      assertEquals(expectedTable.getValue(expectedIndex, "tenant_id").toString(),
          actualTable.getValue(actualIndex, "tenant_id").toString());
      // 保全申請番号
      assertEquals(expectedTable.getValue(expectedIndex, "request_no"),
          actualTable.getValue(actualIndex, "request_no"));
      // 証券番号
      assertEquals(expectedTable.getValue(expectedIndex, "contract_no"),
          actualTable.getValue(actualIndex, "contract_no"));
      // 払込方法コード
      assertEquals(expectedTable.getValue(expectedIndex, "payment_method_code"),
          actualTable.getValue(actualIndex, "payment_method_code"));
      // 銀行コード
      assertEquals(expectedTable.getValue(expectedIndex, "bank_code"),
          actualTable.getValue(actualIndex, "bank_code"));
      // 支店コード
      assertEquals(expectedTable.getValue(expectedIndex, "bank_branch_code"),
          actualTable.getValue(actualIndex, "bank_branch_code"));
      // 口座種別
      assertEquals(expectedTable.getValue(expectedIndex, "bank_account_type"),
          actualTable.getValue(actualIndex, "bank_account_type"));
      // 口座番号
      assertEquals(expectedTable.getValue(expectedIndex, "bank_account_no"),
          actualTable.getValue(actualIndex, "bank_account_no"));
      // 口座名義人
      assertEquals(expectedTable.getValue(expectedIndex, "bank_account_name"),
          actualTable.getValue(actualIndex, "bank_account_name"));
      // 通知用メールアドレス
      assertEquals(expectedTable.getValue(expectedIndex, "email_for_notification"),
          actualTable.getValue(actualIndex, "email_for_notification"));
      // 通信欄
      assertEquals(expectedTable.getValue(expectedIndex, "communication_column"),
          actualTable.getValue(actualIndex, "communication_column"));
      // 一次査定結果
      assertEquals(expectedTable.getValue(expectedIndex, "first_assessment_results"),
          actualTable.getValue(actualIndex, "first_assessment_results"));
      // 一次査定コメント
      assertEquals(expectedTable.getValue(expectedIndex, "comment_underweiter1"),
          actualTable.getValue(actualIndex, "comment_underweiter1"));
      // 保全申請ステータス
      assertEquals(expectedTable.getValue(expectedIndex, "request_status"),
          actualTable.getValue(actualIndex, "request_status"));
      // ロック用
      assertEquals(expectedTable.getValue(expectedIndex, "update_count"),
          actualTable.getValue(actualIndex, "update_count").toString());
    }
  }

  /**
   * 色なテストケースを実行(二次査定)
   * 
   * @author : [VJP] タン
   * @param requestJson
   * @param url
   * @param resultJson
   * @throws Exception
   * @createdAt : 2021-08-11
   * @updatedAt : 2021-08-11
   */
  private void runTestCaseForSecondPut(String requestJson, String url, String resultXml,
      int actualIndex, int testCase) throws Exception {
    mockMvcForPut(url, requestJson);
    // 結果テーブルデータ
    IDataSet databaseDataSet = this.idatabaseConnection.createDataSet();
    // 保全申請
    ITable maintenanceRequestsActualTable = databaseDataSet.getTable("maintenance_requests");
    // 通知内容
    ITable notificationsActualTable = databaseDataSet.getTable("notifications");
    // 法人顧客マスタ
    ITable customersCorporateActualTable = databaseDataSet.getTable("customers_corporate");


    // 契約ログ
    ITable contractLogActualTable = databaseDataSet.getTable("contract_log");
    // 予想結果ロード
    try (InputStream is = getClass().getClassLoader().getResourceAsStream(resultXml)) {
      IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(is);
      // 予想結果テーブルデータ
      // 保全申請
      ITable maintenanceRequestsExpectedTable = expectedDataSet.getTable("maintenance_requests");

      // 予想結果と値をEqual比較
      // 保全申請
      // 保全申請番号
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "request_no"),
          maintenanceRequestsActualTable.getValue(actualIndex, "request_no"));
      // 証券番号
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "contract_no"),
          maintenanceRequestsActualTable.getValue(actualIndex, "contract_no"));
      // 2次査定結果
      assertEquals(
          maintenanceRequestsExpectedTable.getValue(expectedIndex, "second_assessment_results"),
          maintenanceRequestsActualTable.getValue(actualIndex, "second_assessment_results"));
      // 2次査定コメント
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "comment_underweiter2"),
          maintenanceRequestsActualTable.getValue(actualIndex, "comment_underweiter2"));
      // 保全申請ステータス
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "request_status"),
          maintenanceRequestsActualTable.getValue(actualIndex, "request_status"));
      // ロック用
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "update_count"),
          maintenanceRequestsActualTable.getValue(actualIndex, "update_count").toString());

      // 通知内容
      if (testCase != testCase4) {
        // 通知内容
        ITable notificationsExpectedTable = expectedDataSet.getTable("notifications");
        // 通知予定日
        assertEquals(notificationsExpectedTable.getValue(expectedIndex, "notification_date"),
            notificationsActualTable.getValue(expectedIndex, "notification_date").toString());
        // 証券番号
        assertEquals(notificationsExpectedTable.getValue(expectedIndex, "contract_no"),
            notificationsActualTable.getValue(expectedIndex, "contract_no"));
        // 証券番号枝番
        assertEquals(notificationsExpectedTable.getValue(expectedIndex, "contract_branch_no"),
            notificationsActualTable.getValue(expectedIndex, "contract_branch_no"));
        // テンプレートナンバー
        assertEquals(notificationsExpectedTable.getValue(expectedIndex, "template_number"),
            notificationsActualTable.getValue(expectedIndex, "template_number"));
        // 通知実施
        assertEquals(
            notificationsExpectedTable.getValue(expectedIndex, "notification_implementation"),
            notificationsActualTable.getValue(expectedIndex, "notification_implementation"));
        // 通信欄コメント
        assertEquals(notificationsExpectedTable.getValue(expectedIndex, "comment"),
            notificationsActualTable.getValue(expectedIndex, "comment"));
        // 通知対象者
        assertEquals(notificationsExpectedTable.getValue(expectedIndex, "sendee"),
            notificationsActualTable.getValue(expectedIndex, "sendee"));
        // 通知方法
        assertEquals(notificationsExpectedTable.getValue(expectedIndex, "notification_method"),
            notificationsActualTable.getValue(expectedIndex, "notification_method"));
        // 通知対象・Eメールアドレス
        assertEquals(notificationsExpectedTable.getValue(expectedIndex, "email"),
            notificationsActualTable.getValue(expectedIndex, "email"));
        // エラーフラグ
        assertEquals(null, notificationsActualTable.getValue(expectedIndex, "error_flag"));
        // 埋込変数データ
        assertEquals(notificationsExpectedTable.getValue(expectedIndex, "data"),
            notificationsActualTable.getValue(expectedIndex, "data"));
      }

      // #2_OC-003_SC-007,008,009
      if (testCase == testCase21) {
        // 法人顧客マスタ
        ITable customersCorporateExpectedTable = expectedDataSet.getTable("customers_corporate");
        // 通信先・Eメールアドレス
        assertEquals(customersCorporateExpectedTable.getValue(expectedIndex, "contact_email"),
            customersCorporateActualTable.getValue(expectedIndex, "contact_email"));
      }

      if (testCase == testCase22) {
        // 個人顧客マスタ
        ITable customersIndivisualActualTable = databaseDataSet.getTable("customers_individual");
        // 法人顧客マスタ
        ITable customersIndivisualExpectedTable = expectedDataSet.getTable("customers_individual");
        // 通信先・Eメールアドレス
        assertEquals(customersIndivisualActualTable.getValue(1, "email"),
            customersIndivisualExpectedTable.getValue(0, "email"));
      }
      // 契約ログ
      int expectedLogIndex = expectedIndex;
      if (testCase == testCase22) {
        expectedLogIndex = 1;
      }
      // #2_OC-003_SC-007,008,009と#4_OC-003_SC-007,008,009場合
      if (testCase == testCase21 || testCase == testCase4) {
        expectedLogIndex = index1;
      }
      // #6_OC-003_SC-007,008,009
      if (testCase == testCase6) {
        expectedLogIndex = index3;
      }

      // 契約ログ
      ITable contractLogExpectedTable = expectedDataSet.getTable("contract_log");

      // 契約管理番号
      assertEquals(contractLogExpectedTable.getValue(expectedIndex, "contract_no"),
          contractLogActualTable.getValue(expectedLogIndex, "contract_no"));
      // 証券番号枝番
      assertEquals(contractLogExpectedTable.getValue(expectedIndex, "contract_branch_no"),
          contractLogActualTable.getValue(expectedLogIndex, "contract_branch_no"));
      // 連番
      assertEquals(contractLogExpectedTable.getValue(expectedIndex, "sequence_no").toString(),
          contractLogActualTable.getValue(expectedLogIndex, "sequence_no").toString());
      // ログタイプ
      assertEquals(contractLogExpectedTable.getValue(expectedIndex, "log_type"),
          contractLogActualTable.getValue(expectedLogIndex, "log_type"));
      // エラーコード
      assertEquals(null, contractLogActualTable.getValue(expectedLogIndex, "message_code"));
      // エラーグループ
      assertEquals(null, contractLogActualTable.getValue(expectedLogIndex, "message_group"));
      // 事由種別（大分類）
      assertEquals(contractLogExpectedTable.getValue(expectedIndex, "reason_group_code"),
          contractLogActualTable.getValue(expectedLogIndex, "reason_group_code"));
      // 事由ID
      assertEquals(contractLogExpectedTable.getValue(expectedIndex, "reason_code"),
          contractLogActualTable.getValue(expectedLogIndex, "reason_code"));
      // 異動コード
      assertEquals(contractLogExpectedTable.getValue(expectedIndex, "contact_transaction_code"),
          contractLogActualTable.getValue(expectedLogIndex, "contact_transaction_code"));
      // 摘要
      assertEquals(contractLogExpectedTable.getValue(expectedIndex, "description"),
          contractLogActualTable.getValue(expectedLogIndex, "description"));
      // 処理プログラム
      assertEquals(contractLogExpectedTable.getValue(expectedIndex, "program_name"),
          contractLogActualTable.getValue(expectedLogIndex, "program_name"));
    }
  }

  /**
   * 各テストことにDB初期化
   *
   * @author : [VJP] タン
   * @throws SQLException
   * @throws ScriptException
   * @createdAt : 2021-08-11
   * @updatedAt : 2021-08-11
   */
  @AfterEach
  void clean() throws ScriptException, SQLException {
    testScriptUtils.cleanUpDatabase();
    this.connection.close();
  }
}
