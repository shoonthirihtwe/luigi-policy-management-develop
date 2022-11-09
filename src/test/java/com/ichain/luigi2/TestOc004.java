package com.ichain.luigi2;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
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
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jp.co.ichain.luigi2.Luigi2Application;
import jp.co.ichain.luigi2.test.TestScriptUtils;

/**
 * OC-004 test
 *
 * @author : [VJP] hale
 * @createdAt : 2021-08-12
 * @updatedAt : 2021-08-12
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = Luigi2Application.class)
@TestPropertySource("classpath:application-common-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class TestOc004 {

  private MockMvc mockMvc;
  @Autowired
  private WebApplicationContext ctx;

  private Connection connection;
  private IDatabaseConnection idatabaseConnection;

  @Autowired
  @Qualifier("luigi2DataSource")
  private DataSource dataSource;

  @Autowired
  private TestScriptUtils testScriptUtils;

  // インデックス
  private int index = 0;
  private int index1 = 1;
  private int index2 = 2;
  private int index3 = 3;
  // sql
  private String sql = "sql/insert_OC004.sql";

  @BeforeEach
  void setup() throws SQLException, DatabaseUnitException {

    this.connection = dataSource.getConnection();
    this.idatabaseConnection = new MySqlConnection(connection, "luigi2_test");

    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.ctx)
        .addFilters(new CharacterEncodingFilter("UTF-8", true)).build();
  }

  /**
   * OC-00401 テスト実行
   *
   * @author : [VJP] hale
   * @createdAt : 2021-08-12
   * @updatedAt : 2021-08-12
   */
  @SuppressWarnings("unchecked")
  @Test
  void test_Oc00401() throws Exception {

    // テストに必要なSQL実行
    testScriptUtils.executeSqlScript(sql);

    // date setting
    testScriptUtils.updateOnlineDate("2022-08-09", 1);
    testScriptUtils.updateBatchDate("2022-08-09", 1);

    // APIコール
    // GET
    MvcResult mvcResult =
        mockMvcForGet("/OC00403", "json/request/oc004/req_OC004_01_get_data.json");

    ObjectMapper mapper = new ObjectMapper();
    HashMap<String, String> result = (HashMap<String, String>) mapper
        .readValue(mvcResult.getResponse().getContentAsString(), HashMap.class).get("item");

    // 予想結果ロード
    List<HashMap<String, String>> expectedResult = (List<HashMap<String, String>>) testScriptUtils
        .loadJsonToObject("json/response/oc004/res_OC004_01_data.json");

    // 検索結果件数
    assertEquals(1, expectedResult.size());
    //
    for (int i = 0; i < expectedResult.size(); i++) {
      assertEquals(result.get("contractNo"), expectedResult.get(i).get("contractNo"));
      assertEquals(result.get("contractNameKnj"), expectedResult.get(i).get("contractNameKnj"));
      assertEquals(result.get("receivedDate"), expectedResult.get(i).get("receivedDate"));
      assertEquals(result.get("contractEmail"), expectedResult.get(i).get("contractEmail"));
      assertEquals(result.get("emailForNotification"),
          expectedResult.get(i).get("emailForNotification"));
      assertEquals(result.get("documentsList"), expectedResult.get(i).get("documentsList"));
    }

    // APIコール
    // PUT
    mockMvcForPut("/OC00401", "json/request/oc004/req_OC004_01_put_data.json");

    // 結果テーブルデータ
    IDataSet databaseDataSet = this.idatabaseConnection.createDataSet();
    ITable actualTableMaintenanceRequests = databaseDataSet.getTable("maintenance_requests");
    ITable actualTableRefundAmount = databaseDataSet.getTable("refund_amount");

    // 予想結果ロード
    try (InputStream is =
        getClass().getClassLoader().getResourceAsStream("xml/oc004/result_OC00401.xml")) {
      IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(is);
      // 予想結果テーブルデータ
      ITable expectedTableMaintenanceRequests = expectedDataSet.getTable("maintenance_requests");

      // 予想結果と値をEqual比較
      compareMaintenanceRequestsTableForFirst(actualTableMaintenanceRequests,
          expectedTableMaintenanceRequests, index2);

      // 予想結果テーブルデータ
      ITable expectedTableRefundAmount = expectedDataSet.getTable("refund_amount");

      // 予想結果と値をEqual比較
      assertEquals(actualTableRefundAmount.getValue(index, "bank_code"),
          expectedTableRefundAmount.getValue(index, "bank_code"));
      assertEquals(actualTableRefundAmount.getValue(index, "bank_branch_code"),
          expectedTableRefundAmount.getValue(index, "bank_branch_code"));
      assertEquals(actualTableRefundAmount.getValue(index, "bank_account_type"),
          expectedTableRefundAmount.getValue(index, "bank_account_type"));
      assertEquals(actualTableRefundAmount.getValue(index, "bank_account_no"),
          expectedTableRefundAmount.getValue(index, "bank_account_no"));
      assertEquals(actualTableRefundAmount.getValue(index, "bank_account_holder"),
          expectedTableRefundAmount.getValue(index, "bank_account_holder"));
      assertEquals(actualTableRefundAmount.getValue(index, "refund_amount").toString(),
          expectedTableRefundAmount.getValue(index, "refund_amount").toString());
      assertEquals(actualTableRefundAmount.getValue(index, "contract_no"),
          expectedTableRefundAmount.getValue(index, "contract_no"));
      assertEquals(actualTableRefundAmount.getValue(index, "contract_branch_no"),
          expectedTableRefundAmount.getValue(index, "contract_branch_no"));
      assertEquals(actualTableRefundAmount.getValue(index, "request_no"),
          expectedTableRefundAmount.getValue(index, "request_no"));
      assertEquals(actualTableRefundAmount.getValue(index, "active_inactive"),
          expectedTableRefundAmount.getValue(index, "active_inactive"));
      assertEquals(actualTableRefundAmount.getValue(index, "pay_reason"),
          expectedTableRefundAmount.getValue(index, "pay_reason"));
      assertEquals(actualTableRefundAmount.getValue(index, "pay_method"),
          expectedTableRefundAmount.getValue(index, "pay_method"));

    }

  }

  /**
   * OC-00402 テスト実行
   *
   * @author : [VJP] hale
   * @createdAt : 2021-08-12
   * @updatedAt : 2021-08-12
   */
  @SuppressWarnings("unchecked")
  @Test
  void test_Oc00402() throws Exception {

    test_Oc00401();

    // APIコール
    // GET
    MvcResult mvcResult =
        mockMvcForGet("/OC00403", "json/request/oc004/req_OC004_01_get_data.json");

    ObjectMapper mapper = new ObjectMapper();
    HashMap<String, String> result = (HashMap<String, String>) mapper
        .readValue(mvcResult.getResponse().getContentAsString(), HashMap.class).get("item");

    // 予想結果ロード
    List<HashMap<String, String>> expectedResult = (List<HashMap<String, String>>) testScriptUtils
        .loadJsonToObject("json/response/oc004/res_OC004_02_data.json");

    // 検索結果件数
    assertEquals(1, expectedResult.size());
    //
    for (int i = 0; i < expectedResult.size(); i++) {
      assertEquals(result.get("contractNo"), expectedResult.get(i).get("contractNo"));
      assertEquals(result.get("contractNameKnj"), expectedResult.get(i).get("contractNameKnj"));
      assertEquals(result.get("receivedDate"), expectedResult.get(i).get("receivedDate"));
      assertEquals(result.get("firstAssessmentResults"),
          expectedResult.get(i).get("firstAssessmentResults"));
      assertEquals(result.get("commentUnderweiter1"),
          expectedResult.get(i).get("commentUnderweiter1"));
      assertEquals(result.get("communicationColumn"),
          expectedResult.get(i).get("communicationColumn"));
      assertEquals(result.get("bankCode"), expectedResult.get(i).get("bankCode"));
      assertEquals(result.get("bankBranchCode"), expectedResult.get(i).get("bankBranchCode"));
      assertEquals(result.get("bankAccountType"), expectedResult.get(i).get("bankAccountType"));
      assertEquals(result.get("bankAccountNo"), expectedResult.get(i).get("bankAccountNo"));
      assertEquals(result.get("bankAccountHolder"), expectedResult.get(i).get("bankAccountHolder"));
      assertEquals(result.get("refundAmount"), expectedResult.get(i).get("refundAmount"));
      assertEquals(result.get("contractEmail"), expectedResult.get(i).get("contractEmail"));
      assertEquals(result.get("emailForNotification"),
          expectedResult.get(i).get("emailForNotification"));
      assertEquals(result.get("documentsList"), expectedResult.get(i).get("documentsList"));
    }

    // APIコール
    // PUT
    mockMvcForPut("/OC00402", "json/request/oc004/req_OC004_02_put_data.json");

    // 結果テーブルデータ
    IDataSet databaseDataSet = this.idatabaseConnection.createDataSet();
    ITable actualTableMaintenanceRequests = databaseDataSet.getTable("maintenance_requests");
    ITable actualTableContractLog = databaseDataSet.getTable("contract_log");

    // 予想結果ロード
    try (InputStream is =
        getClass().getClassLoader().getResourceAsStream("xml/oc004/result_OC00402.xml")) {
      IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(is);
      // 予想結果テーブルデータ
      ITable expectedTableMaintenanceRequests = expectedDataSet.getTable("maintenance_requests");

      // 予想結果と値をEqual比較
      compareMaintenanceRequestsTableForSecond(actualTableMaintenanceRequests,
          expectedTableMaintenanceRequests, index2);

      // 予想結果テーブルデータ
      ITable expectedTableContractLog = expectedDataSet.getTable("contract_log");

      // 予想結果と値をEqual比較
      compareContractLogTable(actualTableContractLog, expectedTableContractLog, index1);
    }

  }

  /**
   * OC-00403 テスト実行
   *
   * @author : [VJP] hale
   * @createdAt : 2021-08-12
   * @updatedAt : 2021-08-12
   */
  @SuppressWarnings("unchecked")
  @Test
  void test_Oc00403() throws Exception {
    test_Oc00402();

    // APIコール
    // GET
    MvcResult mvcResult =
        mockMvcForGet("/OC00403", "json/request/oc004/req_OC004_01_get_data.json");

    ObjectMapper mapper = new ObjectMapper();
    HashMap<String, String> result = (HashMap<String, String>) mapper
        .readValue(mvcResult.getResponse().getContentAsString(), HashMap.class).get("item");

    // 予想結果ロード
    List<HashMap<String, String>> expectedResult = (List<HashMap<String, String>>) testScriptUtils
        .loadJsonToObject("json/response/oc004/res_OC004_03_data.json");

    // // 検索結果件数
    assertEquals(1, expectedResult.size());
    //
    for (int i = 0; i < expectedResult.size(); i++) {
      assertEquals(result.get("contractNo"), expectedResult.get(i).get("contractNo"));
      assertEquals(result.get("contractNameKnj"), expectedResult.get(i).get("contractNameKnj"));
      assertEquals(result.get("receivedDate"), expectedResult.get(i).get("receivedDate"));
      assertEquals(result.get("commentUnderweiter1"),
          expectedResult.get(i).get("commentUnderweiter1"));
      assertEquals(result.get("communicationColumn"),
          expectedResult.get(i).get("communicationColumn"));
      assertEquals(result.get("secondAssessmentResults"),
          expectedResult.get(i).get("secondAssessmentResults"));
      assertEquals(result.get("commentUnderweiter2"),
          expectedResult.get(i).get("commentUnderweiter2"));
      assertEquals(result.get("bankCode"), expectedResult.get(i).get("bankCode"));
      assertEquals(result.get("bankBranchCode"), expectedResult.get(i).get("bankBranchCode"));
      assertEquals(result.get("bankAccountType"), expectedResult.get(i).get("bankAccountType"));
      assertEquals(result.get("bankAccountNo"), expectedResult.get(i).get("bankAccountNo"));
      assertEquals(result.get("bankAccountHolder"), expectedResult.get(i).get("bankAccountHolder"));
      assertEquals(result.get("refundAmount"), expectedResult.get(i).get("refundAmount"));
      assertEquals(result.get("contractEmail"), expectedResult.get(i).get("contractEmail"));
      assertEquals(result.get("emailForNotification"),
          expectedResult.get(i).get("emailForNotification"));
      assertEquals(result.get("documentsList"), expectedResult.get(i).get("documentsList"));
    }

    // APIコール
    // PUT
    mockMvcForPut("/OC00401", "json/request/oc004/req_OC004_03_put_data.json");

    // 結果テーブルデータ
    IDataSet databaseDataSet = this.idatabaseConnection.createDataSet();
    ITable actualTableMaintenanceRequests = databaseDataSet.getTable("maintenance_requests");

    // 予想結果ロード
    try (InputStream is =
        getClass().getClassLoader().getResourceAsStream("xml/oc004/result_OC00403.xml")) {
      IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(is);
      // 予想結果テーブルデータ
      ITable expectedTableMaintenanceRequests = expectedDataSet.getTable("maintenance_requests");

      // 予想結果と値をEqual比較
      compareMaintenanceRequestsTableForFirst(actualTableMaintenanceRequests,
          expectedTableMaintenanceRequests, index2);
    }

  }

  /**
   * OC-00404 テスト実行
   *
   * @author : [VJP] hale
   * @createdAt : 2021-08-12
   * @updatedAt : 2021-08-12
   */
  @SuppressWarnings("unchecked")
  @Test
  void test_Oc00404() throws Exception {

    test_Oc00403();

    // APIコール
    // GET
    MvcResult mvcResult =
        mockMvcForGet("/OC00403", "json/request/oc004/req_OC004_01_get_data.json");

    ObjectMapper mapper = new ObjectMapper();
    HashMap<String, String> result = (HashMap<String, String>) mapper
        .readValue(mvcResult.getResponse().getContentAsString(), HashMap.class).get("item");

    // 予想結果ロード
    List<HashMap<String, String>> expectedResult = (List<HashMap<String, String>>) testScriptUtils
        .loadJsonToObject("json/response/oc004/res_OC004_04_data.json");

    // // 検索結果件数
    assertEquals(1, expectedResult.size());
    //
    for (int i = 0; i < expectedResult.size(); i++) {
      assertEquals(result.get("contractNo"), expectedResult.get(i).get("contractNo"));
      assertEquals(result.get("contractNameKnj"), expectedResult.get(i).get("contractNameKnj"));
      assertEquals(result.get("receivedDate"), expectedResult.get(i).get("receivedDate"));
      assertEquals(result.get("firstAssessmentResults"),
          expectedResult.get(i).get("firstAssessmentResults"));
      assertEquals(result.get("commentUnderweiter1"),
          expectedResult.get(i).get("commentUnderweiter1"));
      assertEquals(result.get("commentUnderweiter2"),
          expectedResult.get(i).get("commentUnderweiter2"));
      assertEquals(result.get("contractEmail"), expectedResult.get(i).get("contractEmail"));
      assertEquals(result.get("emailForNotification"),
          expectedResult.get(i).get("emailForNotification"));
      assertEquals(result.get("documentsList"), expectedResult.get(i).get("documentsList"));
    }

    // APIコール
    // PUT
    mockMvcForPut("/OC00402", "json/request/oc004/req_OC004_04_put_data.json");

    // 結果テーブルデータ
    IDataSet databaseDataSet = this.idatabaseConnection.createDataSet();
    ITable actualTableMaintenanceRequests = databaseDataSet.getTable("maintenance_requests");
    ITable actualTableRefundAmount = databaseDataSet.getTable("refund_amount");
    ITable actualTableContracts = databaseDataSet.getTable("contracts");
    ITable actualTableContractLog = databaseDataSet.getTable("contract_log");
    ITable actualTableNotifications = databaseDataSet.getTable("notifications");

    // 予想結果ロード
    try (InputStream is =
        getClass().getClassLoader().getResourceAsStream("xml/oc004/result_OC00404.xml")) {
      IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(is);

      // 予想結果テーブルデータ
      ITable expectedTableMaintenanceRequests = expectedDataSet.getTable("maintenance_requests");

      // 予想結果と値をEqual比較
      compareMaintenanceRequestsTableForSecond(actualTableMaintenanceRequests,
          expectedTableMaintenanceRequests, index2);

      // 予想結果テーブルデータ
      ITable expectedTableRefundAmount = expectedDataSet.getTable("refund_amount");

      // 予想結果と値をEqual比較
      assertEquals(actualTableRefundAmount.getValue(index, "due_date").toString(),
          expectedTableRefundAmount.getValue(index, "due_date").toString());
      assertEquals(actualTableRefundAmount.getValue(index, "payment_date").toString(),
          expectedTableRefundAmount.getValue(index, "payment_date").toString());

      // 予想結果テーブルデータ
      ITable expectedTableContracts = expectedDataSet.getTable("contracts");

      // 予想結果と値をEqual比較
      assertEquals(actualTableContracts.getValue(index1, "contract_status"),
          expectedTableContracts.getValue(index, "contract_status"));
      assertEquals(actualTableContracts.getValue(index1, "termination_date").toString(),
          expectedTableContracts.getValue(index, "termination_date").toString());

      // 予想結果テーブルデータ
      ITable expectedTableNotifications = expectedDataSet.getTable("notifications");

      // 予想結果と値をEqual比較
      compareNotificationsTable(actualTableNotifications, expectedTableNotifications);

      // 予想結果テーブルデータ
      ITable expectedTableContractLog = expectedDataSet.getTable("contract_log");

      // 予想結果と値をEqual比較
      compareContractLogTable(actualTableContractLog, expectedTableContractLog, index3);
    }

  }

  /**
   * OC-00405 テスト実行
   *
   * @author : [VJP] hale
   * @createdAt : 2021-08-12
   * @updatedAt : 2021-08-12
   */
  @SuppressWarnings("unchecked")
  @Test
  void test_Oc00405() throws Exception {

    // テストに必要なSQL実行
    testScriptUtils.executeSqlScript(sql);

    // date setting
    testScriptUtils.updateOnlineDate("2022-08-10", 1);
    testScriptUtils.updateBatchDate("2022-08-10", 1);
    // APIコール
    // GET
    MvcResult mvcResult =
        mockMvcForGet("/OC00403", "json/request/oc004/req_OC004_05_get_data.json");

    ObjectMapper mapper = new ObjectMapper();
    HashMap<String, String> result = (HashMap<String, String>) mapper
        .readValue(mvcResult.getResponse().getContentAsString(), HashMap.class).get("item");

    // 予想結果ロード
    List<HashMap<String, String>> expectedResult = (List<HashMap<String, String>>) testScriptUtils
        .loadJsonToObject("json/response/oc004/res_OC004_05_data.json");

    // 検索結果件数
    assertEquals(1, expectedResult.size());
    //
    for (int i = 0; i < expectedResult.size(); i++) {
      assertEquals(result.get("contractNo"), expectedResult.get(i).get("contractNo"));
      assertEquals(result.get("contractNameKnj"), expectedResult.get(i).get("contractNameKnj"));
      assertEquals(result.get("receivedDate"), expectedResult.get(i).get("receivedDate"));
      assertEquals(result.get("contractEmail"), expectedResult.get(i).get("contractEmail"));
      assertEquals(result.get("emailForNotification"),
          expectedResult.get(i).get("emailForNotification"));
    }

    // APIコール
    // PUT
    mockMvcForPut("/OC00401", "json/request/oc004/req_OC004_05_put_data.json");

    // 結果テーブルデータ
    IDataSet databaseDataSet = this.idatabaseConnection.createDataSet();
    ITable actualTableMaintenanceRequests = databaseDataSet.getTable("maintenance_requests");

    // 予想結果ロード
    try (InputStream is =
        getClass().getClassLoader().getResourceAsStream("xml/oc004/result_OC00405.xml")) {
      IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(is);
      // 予想結果テーブルデータ
      ITable expectedTableMaintenanceRequests = expectedDataSet.getTable("maintenance_requests");

      // 予想結果と値をEqual比較
      compareMaintenanceRequestsTableForFirst(actualTableMaintenanceRequests,
          expectedTableMaintenanceRequests, index3);
    }

  }

  /**
   * OC-00406 テスト実行
   *
   * @author : [VJP] hale
   * @createdAt : 2021-08-12
   * @updatedAt : 2021-08-12
   */
  @SuppressWarnings("unchecked")
  @Test
  void test_Oc00406() throws Exception {

    test_Oc00405();

    // APIコール
    // GET
    MvcResult mvcResult =
        mockMvcForGet("/OC00403", "json/request/oc004/req_OC004_05_get_data.json");

    ObjectMapper mapper = new ObjectMapper();
    HashMap<String, String> result = (HashMap<String, String>) mapper
        .readValue(mvcResult.getResponse().getContentAsString(), HashMap.class).get("item");

    // 予想結果ロード
    List<HashMap<String, String>> expectedResult = (List<HashMap<String, String>>) testScriptUtils
        .loadJsonToObject("json/response/oc004/res_OC004_06_data.json");

    // 検索結果件数
    assertEquals(1, expectedResult.size());
    //
    for (int i = 0; i < expectedResult.size(); i++) {
      assertEquals(result.get("contractNo"), expectedResult.get(i).get("contractNo"));
      assertEquals(result.get("contractNameKnj"), expectedResult.get(i).get("contractNameKnj"));
      assertEquals(result.get("receivedDate"), expectedResult.get(i).get("receivedDate"));
      assertEquals(result.get("firstAssessmentResults"),
          expectedResult.get(i).get("firstAssessmentResults"));
      assertEquals(result.get("commentUnderweiter1"),
          expectedResult.get(i).get("commentUnderweiter1"));
      assertEquals(result.get("communicationColumn"),
          expectedResult.get(i).get("communicationColumn"));
      assertEquals(result.get("contractEmail"), expectedResult.get(i).get("contractEmail"));
      assertEquals(result.get("emailForNotification"),
          expectedResult.get(i).get("emailForNotification"));
    }

    // APIコール
    // PUT
    mockMvcForPut("/OC00402", "json/request/oc004/req_OC004_06_put_data.json");

    // 結果テーブルデータ
    IDataSet databaseDataSet = this.idatabaseConnection.createDataSet();
    ITable actualTableMaintenanceRequests = databaseDataSet.getTable("maintenance_requests");
    ITable actualTableContractLog = databaseDataSet.getTable("contract_log");
    ITable actualTableNotifications = databaseDataSet.getTable("notifications");

    // 予想結果ロード
    try (InputStream is =
        getClass().getClassLoader().getResourceAsStream("xml/oc004/result_OC00406.xml")) {
      IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(is);
      // 予想結果テーブルデータ
      ITable expectedTableMaintenanceRequests = expectedDataSet.getTable("maintenance_requests");

      // 予想結果と値をEqual比較
      compareMaintenanceRequestsTableForSecond(actualTableMaintenanceRequests,
          expectedTableMaintenanceRequests, index3);

      // 予想結果テーブルデータ
      ITable expectedTableNotifications = expectedDataSet.getTable("notifications");

      // 予想結果と値をEqual比較
      compareNotificationsTable(actualTableNotifications, expectedTableNotifications);

      // 予想結果テーブルデータ
      ITable expectedTableContractLog = expectedDataSet.getTable("contract_log");

      // 予想結果と値をEqual比較
      compareContractLogTable(actualTableContractLog, expectedTableContractLog, index1);
    }

  }

  /**
   * Mock request for get
   * 
   * @author : [VJP] hale
   * @param sql
   * @param url
   * @param requestJson
   * @return
   * @throws JsonParseException
   * @throws JsonMappingException
   * @throws UnsupportedEncodingException
   * @throws IOException
   * @throws Exception
   * @createdAt : 2021-08-12
   * @updatedAt : 2021-08-12
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
   * @author : [VJP] hale
   * @param sql
   * @param url
   * @param requestJson
   * @return
   * @throws JsonParseException
   * @throws JsonMappingException
   * @throws UnsupportedEncodingException
   * @throws IOException
   * @throws Exception
   * @createdAt : 2021-08-12
   * @updatedAt : 2021-08-12
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
   * 色なテストケースを実行
   * 
   * @author : [VJP] hale
   * @param actualTableNotifications
   * @param expectedTableNotifications
   * @throws Exception
   * @createdAt : 2021-08-12
   * @updatedAt : 2021-08-12
   */
  private void compareNotificationsTable(ITable actualTableNotifications,
      ITable expectedTableNotifications) throws Exception {
    // 予想結果と値をEqual比較
    assertEquals(actualTableNotifications.getValue(index, "notification_date").toString(),
        expectedTableNotifications.getValue(index, "notification_date").toString());
    assertEquals(actualTableNotifications.getValue(index, "contract_no"),
        expectedTableNotifications.getValue(index, "contract_no"));
    assertEquals(actualTableNotifications.getValue(index, "contract_branch_no"),
        expectedTableNotifications.getValue(index, "contract_branch_no"));
    assertEquals(actualTableNotifications.getValue(index, "template_number"),
        expectedTableNotifications.getValue(index, "template_number"));
    assertEquals(actualTableNotifications.getValue(index, "notification_implementation"),
        expectedTableNotifications.getValue(index, "notification_implementation"));
    assertEquals(actualTableNotifications.getValue(index, "sendee"),
        expectedTableNotifications.getValue(index, "sendee"));
    assertEquals(actualTableNotifications.getValue(index, "notification_method"),
        expectedTableNotifications.getValue(index, "notification_method"));
    assertEquals(actualTableNotifications.getValue(index, "email"),
        expectedTableNotifications.getValue(index, "email"));
  }

  /**
   * 色なテストケースを実行
   * 
   * @author : [VJP] hale
   * @param actualTableContractLog
   * @param expectedTableContractLog
   * @param actualIndex
   * @throws Exception
   * @createdAt : 2021-08-12
   * @updatedAt : 2021-08-12
   */
  private void compareContractLogTable(ITable actualTableContractLog,
      ITable expectedTableContractLog, int actualIndex) throws Exception {
    // 予想結果と値をEqual比較
    assertEquals(actualTableContractLog.getValue(actualIndex, "contract_no"),
        expectedTableContractLog.getValue(index, "contract_no"));
    assertEquals(actualTableContractLog.getValue(actualIndex, "contract_branch_no"),
        expectedTableContractLog.getValue(index, "contract_branch_no"));
    assertEquals(actualTableContractLog.getValue(actualIndex, "sequence_no").toString(),
        expectedTableContractLog.getValue(index, "sequence_no").toString());
    assertEquals(actualTableContractLog.getValue(actualIndex, "log_type"),
        expectedTableContractLog.getValue(index, "log_type"));
    assertEquals(actualTableContractLog.getValue(actualIndex, "reason_group_code"),
        expectedTableContractLog.getValue(index, "reason_group_code"));
    assertEquals(actualTableContractLog.getValue(actualIndex, "reason_code"),
        expectedTableContractLog.getValue(index, "reason_code"));
    assertEquals(actualTableContractLog.getValue(actualIndex, "contact_transaction_code"),
        expectedTableContractLog.getValue(index, "contact_transaction_code"));
    assertEquals(actualTableContractLog.getValue(actualIndex, "description"),
        expectedTableContractLog.getValue(index, "description"));
    assertEquals(actualTableContractLog.getValue(actualIndex, "program_name"),
        expectedTableContractLog.getValue(index, "program_name"));
  }

  /**
   * 色なテストケースを実行(1次査定)
   * 
   * @author : [VJP] hale
   * @param actualTableMaintenanceRequests
   * @param expectedTableMaintenanceRequests
   * @param actualIndex
   * @throws Exception
   * @createdAt : 2021-08-12
   * @updatedAt : 2021-08-12
   */
  private void compareMaintenanceRequestsTableForFirst(ITable actualTableMaintenanceRequests,
      ITable expectedTableMaintenanceRequests, int actualIndex) throws Exception {
    // 予想結果と値をEqual比較
    assertEquals(actualTableMaintenanceRequests.getValue(actualIndex, "email_for_notification"),
        expectedTableMaintenanceRequests.getValue(index, "email_for_notification"));
    assertEquals(actualTableMaintenanceRequests.getValue(actualIndex, "first_assessment_results"),
        expectedTableMaintenanceRequests.getValue(index, "first_assessment_results"));
    assertEquals(actualTableMaintenanceRequests.getValue(actualIndex, "communication_column"),
        expectedTableMaintenanceRequests.getValue(index, "communication_column"));
    assertEquals(actualTableMaintenanceRequests.getValue(actualIndex, "comment_underweiter1"),
        expectedTableMaintenanceRequests.getValue(index, "comment_underweiter1"));
    assertEquals(actualTableMaintenanceRequests.getValue(actualIndex, "request_status"),
        expectedTableMaintenanceRequests.getValue(index, "request_status"));
  }

  /**
   * 色なテストケースを実行(２次査定)
   * 
   * @author : [VJP] hale
   * @param actualTableMaintenanceRequests
   * @param expectedTableMaintenanceRequests
   * @param actualIndex
   * @throws Exception
   * @createdAt : 2021-08-12
   * @updatedAt : 2021-08-12
   */
  private void compareMaintenanceRequestsTableForSecond(ITable actualTableMaintenanceRequests,
      ITable expectedTableMaintenanceRequests, int actualIndex) throws Exception {
    // 予想結果と値をEqual比較
    assertEquals(actualTableMaintenanceRequests.getValue(actualIndex, "second_assessment_results"),
        expectedTableMaintenanceRequests.getValue(index, "second_assessment_results"));
    assertEquals(actualTableMaintenanceRequests.getValue(actualIndex, "comment_underweiter2"),
        expectedTableMaintenanceRequests.getValue(index, "comment_underweiter2"));
    assertEquals(actualTableMaintenanceRequests.getValue(actualIndex, "request_status"),
        expectedTableMaintenanceRequests.getValue(index, "request_status"));
  }

  /**
   * 各テストことにDB初期化
   *
   * @author : [VJP] hale
   * @throws SQLException
   * @throws ScriptException
   * @createdAt : 2021-08-12
   * @updatedAt : 2021-08-12
   */
  @AfterEach
  void clean() throws ScriptException, SQLException {
    testScriptUtils.cleanUpDatabase();
    this.connection.close();
  }
}
