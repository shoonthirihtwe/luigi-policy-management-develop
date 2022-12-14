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
 * OC-003 ??????????????????
 *
 * @author : [VJP] ??????
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

  // ????????????????????????
  private int expectedIndex = 0;
  // #2_OC-003_SC-007,008,009
  // customers.corporate_individual_flag=2?????????(for case test case02-01)
  private int testCase21 = 2;
  // customers.corporate_individual_flag=1?????????(for case test case02-02)
  private int testCase22 = 1;
  // #4_OC-003_SC-007,008,009
  private int testCase4 = 4;
  // #6_OC-003_SC-007,008,009
  private int testCase6 = 6;
  // ?????????????????????
  private int index1 = 1;
  // ?????????????????????
  private int index2 = 2;
  // ?????????????????????
  private int index3 = 3;

  /**
   * OC-003 ?????????????????? setup
   *
   * @author : [VJP] ??????
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
    // ?????????????????????SQL??????
    testScriptUtils.executeSqlScript(sql);

    // date setting
    testScriptUtils.updateBatchDate("2023-03-01", 1);
  }

  /**
   * OC-003 ?????????????????? ???????????????
   * 
   * #1_OC-003_SC-005,006,009
   * 
   * @author : [VJP] ??????
   * @createdAt : 2021-08-11
   * @updatedAt : 2021-08-11
   */
  @Test
  void testOc00301() throws Exception {
    // ??????
    String requestJsonForGet = "json/request/oc003/req_OC003_01_get_data.json";
    String urlForGet = "/OC00303";
    String resultJsonForGet = "json/response/oc003/res_OC003_01_data.json";
    runTestCaseForGet(requestJsonForGet, urlForGet, resultJsonForGet, false);

    // ?????????????????????
    String requestJsonForPut = "json/request/oc003/req_OC003_01_put_data.json";
    String urlForPut = "/OC00301";
    String resultXmlForPut = "xml/oc003/result_OC003_01.xml";
    runTestCaseForFirstPut(requestJsonForPut, urlForPut, resultXmlForPut, index2);
  }

  /**
   * OC-003 ?????????????????? ???????????????
   * 
   * #2_OC-003_SC-007,008,009
   * 
   * @author : [VJP] ??????
   * @createdAt : 2021-08-11
   * @updatedAt : 2021-08-11
   */
  @Test
  void testOc0030201() throws Exception {
    // customers.corporate_individual_flag=2?????????
    // ????????????
    testOc00301();

    // ?????????????????????
    String requestJsonForGet = "json/request/oc003/req_OC003_01_get_data.json";
    String urlForGet = "/OC00303";
    String resultJsonForGet = "json/response/oc003/res_OC003_02_data.json";
    runTestCaseForGet(requestJsonForGet, urlForGet, resultJsonForGet, true);

    // ????????????????????????????????????
    String requestJsonForPutSecond = "json/request/oc003/req_OC003_02_put_data.json";
    String urlForPutSecond = "/OC00302";
    String resultXmlForPutSecond = "xml/oc003/result_OC003_02.xml";
    runTestCaseForSecondPut(requestJsonForPutSecond, urlForPutSecond, resultXmlForPutSecond, index2,
        testCase21);
  }

  @Test
  void testOc0030202() throws Exception {
    // customers.corporate_individual_flag=1?????????
    testScriptUtils.executeSqlScript("sql/insert_OC003_01.sql");
    // ????????????
    testOc00301();

    // ?????????????????????
    String requestJsonForGet = "json/request/oc003/req_OC003_01_get_data.json";
    String urlForGet = "/OC00303";
    String resultJsonForGet = "json/response/oc003/res_OC003_02_data.json";
    runTestCaseForGet(requestJsonForGet, urlForGet, resultJsonForGet, true);

    // ????????????????????????????????????
    String requestJsonForPutSecond = "json/request/oc003/req_OC003_02_put_data.json";
    String urlForPutSecond = "/OC00302";
    String resultXmlForPutSecond = "xml/oc003/result_OC003_02_02.xml";
    runTestCaseForSecondPut(requestJsonForPutSecond, urlForPutSecond, resultXmlForPutSecond, index2,
        testCase22);
  }

  /**
   * OC-003 ?????????????????? ???????????????
   * 
   * #3_OC-003_SC-005,006,009
   * 
   * @author : [VJP] ??????
   * @createdAt : 2021-08-11
   * @updatedAt : 2021-08-11
   */
  @Test
  void testOc00303() throws Exception {
    // ??????
    String requestJsonForGet = "json/request/oc003/req_OC003_03_get_data.json";
    String urlForGet = "/OC00303";
    String resultJsonForGet = "json/response/oc003/res_OC003_03_data.json";
    runTestCaseForGet(requestJsonForGet, urlForGet, resultJsonForGet, false);

    // ?????????????????????
    String requestJsonForPut = "json/request/oc003/req_OC003_03_put_data.json";
    String urlForPut = "/OC00301";
    String resultXmlForPut = "xml/oc003/result_OC003_03.xml";
    runTestCaseForFirstPut(requestJsonForPut, urlForPut, resultXmlForPut, index3);
  }

  /**
   * OC-003 ?????????????????? ???????????????
   * 
   * #4_OC-003_SC-007,008,009
   * 
   * @author : [VJP] ??????
   * @createdAt : 2021-08-11
   * @updatedAt : 2021-08-11
   */
  @Test
  void testOc00304() throws Exception {
    // ????????????
    testOc00303();

    // ?????????????????????
    String requestJsonForGet = "json/request/oc003/req_OC003_03_get_data.json";
    String urlForGet = "/OC00303";
    String resultJsonForGet = "json/response/oc003/res_OC003_04_data.json";
    runTestCaseForGet(requestJsonForGet, urlForGet, resultJsonForGet, true);

    // ????????????????????????????????????
    String requestJsonForPutSecond = "json/request/oc003/req_OC003_04_put_data.json";
    String urlForPutSecond = "/OC00302";
    String resultXmlForPutSecond = "xml/oc003/result_OC003_04.xml";
    runTestCaseForSecondPut(requestJsonForPutSecond, urlForPutSecond, resultXmlForPutSecond, index3,
        testCase4);
  }

  /**
   * OC-003 ?????????????????? ???????????????
   * 
   * #5_OC-003_SC-005,006,009
   * 
   * @author : [VJP] ??????
   * @createdAt : 2021-08-11
   * @updatedAt : 2021-08-11
   */
  @Test
  void testOc00305() throws Exception {
    // ????????????
    testOc00304();
    // ?????????????????????
    String requestJsonForGet = "json/request/oc003/req_OC003_03_get_data.json";
    String urlForGet = "/OC00303";
    String resultJsonForGet = "json/response/oc003/res_OC003_05_data.json";
    runTestCaseForGet(requestJsonForGet, urlForGet, resultJsonForGet, false);

    // ????????????????????????????????????
    String requestJsonForPut = "json/request/oc003/req_OC003_05_put_data.json";
    String urlForPut = "/OC00301";
    String resultXmlForPut = "xml/oc003/result_OC003_05.xml";
    runTestCaseForFirstPut(requestJsonForPut, urlForPut, resultXmlForPut, index3);
  }

  /**
   * OC-003 ?????????????????? ???????????????
   * 
   * #6_OC-003_SC-007,008,009
   * 
   * @author : [VJP] ??????
   * @createdAt : 2021-08-11
   * @updatedAt : 2021-08-11
   */
  @Test
  void testOc00306() throws Exception {
    // ????????????
    testOc00305();
    // ?????????????????????
    String requestJsonForGet = "json/request/oc003/req_OC003_03_get_data.json";
    String urlForGet = "/OC00303";
    String resultJsonForGet = "json/response/oc003/res_OC003_06_data.json";
    runTestCaseForGet(requestJsonForGet, urlForGet, resultJsonForGet, true);

    // ????????????????????????????????????
    String requestJsonForPutSecond = "json/request/oc003/req_OC003_06_put_data.json";
    String urlForPutSecond = "/OC00302";
    String resultXmlForPutSecond = "xml/oc003/result_OC003_06.xml";
    runTestCaseForSecondPut(requestJsonForPutSecond, urlForPutSecond, resultXmlForPutSecond, index3,
        testCase6);
  }

  /**
   * Mock request for get
   * 
   * @author : [VJP] ??????
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
    // API?????????
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
   * @author : [VJP] ??????
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
    // API?????????
    return mockMvc
        .perform(MockMvcRequestBuilders.put(url).header("x-frontend-domain", "localhost")
            .content(testScriptUtils.loadJsonToString(requestJson))
            .contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
  }

  /**
   * ?????????????????????????????????(??????)
   * 
   * @author : [VJP] ??????
   * @param requestJson
   * @param url
   * @param resultJson
   * @param second ????????????
   * @throws Exception
   * @createdAt : 2021-08-11
   * @updatedAt : 2021-08-11
   */
  private void runTestCaseForGet(String requestJson, String url, String resultJson, boolean second)
      throws Exception {
    MvcResult mvcResult = mockMvcForGet(url, requestJson);

    // ??????????????????
    JsonDeserializer<Date> deser = (json, typeOfT, context) -> json == null ? null
        : new Date(Long.valueOf(json.getAsString().replaceAll("\\D", "")));
    Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, deser).create();
    Type responseType = new TypeToken<ResultOneDto<MaintenanceRequestsVo>>() {}.getType();

    // API?????????
    ResultOneDto<MaintenanceRequestsVo> res =
        gson.fromJson(mvcResult.getResponse().getContentAsString(), responseType);
    MaintenanceRequestsVo actual = res.getItem();

    // ?????????????????????
    MaintenanceRequestsVo expected =
        gson.fromJson(testScriptUtils.loadJsonToString(resultJson), MaintenanceRequestsVo.class);

    // ????????????
    // ?????????
    assertEquals(expected.getReceivedDate(), actual.getReceivedDate());
    // ????????????
    assertEquals(expected.getContractNo(), actual.getContractNo());
    // ????????????
    assertEquals(expected.getContractNameKnj(), actual.getContractNameKnj());
    // ??????????????????????????????
    assertEquals(expected.getContractEmail(), actual.getContractEmail());
    // ??????
    assertEquals(expected.getPaymentMethodCode(), actual.getPaymentMethodCode());
    // ???????????????????????????
    assertEquals(expected.getEmailForNotification(), actual.getEmailForNotification());
    if (second) {
      // ?????????????????????
      assertEquals(expected.getPaymentMethodCode(), actual.getPaymentMethodCode());
      // ???????????????
      assertEquals(expected.getBankCode(), actual.getBankCode());
      // ???????????????
      assertEquals(expected.getBankBranchCode(), actual.getBankBranchCode());
      // ????????????
      assertEquals(expected.getBankAccountType(), actual.getBankAccountType());
      // ????????????
      assertEquals(expected.getBankAccountNo(), actual.getBankAccountNo());
      // ???????????????
      assertEquals(expected.getBankAccountName(), actual.getBankAccountName());
      // ?????????
      assertEquals(expected.getCommunicationColumn(), actual.getCommunicationColumn());
      // ????????????????????????
      assertEquals(expected.getCommentUnderweiter1(), actual.getCommentUnderweiter1());
      // ??????????????????
      assertEquals(expected.getFirstAssessmentResults(), actual.getFirstAssessmentResults());
    }
  }

  /**
   * ?????????????????????????????????(????????????)
   * 
   * @author : [VJP] ??????
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
    // ???????????????????????????
    IDataSet databaseDataSet = this.idatabaseConnection.createDataSet();
    // ????????????
    ITable actualTable = databaseDataSet.getTable("maintenance_requests");
    // ?????????????????????
    try (InputStream is = getClass().getClassLoader().getResourceAsStream(resultXml)) {
      IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(is);
      // ?????????????????????????????????
      ITable expectedTable = expectedDataSet.getTable("maintenance_requests");

      // ?????????????????????Equal??????
      // tenant_id
      assertEquals(expectedTable.getValue(expectedIndex, "tenant_id").toString(),
          actualTable.getValue(actualIndex, "tenant_id").toString());
      // ??????????????????
      assertEquals(expectedTable.getValue(expectedIndex, "request_no"),
          actualTable.getValue(actualIndex, "request_no"));
      // ????????????
      assertEquals(expectedTable.getValue(expectedIndex, "contract_no"),
          actualTable.getValue(actualIndex, "contract_no"));
      // ?????????????????????
      assertEquals(expectedTable.getValue(expectedIndex, "payment_method_code"),
          actualTable.getValue(actualIndex, "payment_method_code"));
      // ???????????????
      assertEquals(expectedTable.getValue(expectedIndex, "bank_code"),
          actualTable.getValue(actualIndex, "bank_code"));
      // ???????????????
      assertEquals(expectedTable.getValue(expectedIndex, "bank_branch_code"),
          actualTable.getValue(actualIndex, "bank_branch_code"));
      // ????????????
      assertEquals(expectedTable.getValue(expectedIndex, "bank_account_type"),
          actualTable.getValue(actualIndex, "bank_account_type"));
      // ????????????
      assertEquals(expectedTable.getValue(expectedIndex, "bank_account_no"),
          actualTable.getValue(actualIndex, "bank_account_no"));
      // ???????????????
      assertEquals(expectedTable.getValue(expectedIndex, "bank_account_name"),
          actualTable.getValue(actualIndex, "bank_account_name"));
      // ??????????????????????????????
      assertEquals(expectedTable.getValue(expectedIndex, "email_for_notification"),
          actualTable.getValue(actualIndex, "email_for_notification"));
      // ?????????
      assertEquals(expectedTable.getValue(expectedIndex, "communication_column"),
          actualTable.getValue(actualIndex, "communication_column"));
      // ??????????????????
      assertEquals(expectedTable.getValue(expectedIndex, "first_assessment_results"),
          actualTable.getValue(actualIndex, "first_assessment_results"));
      // ????????????????????????
      assertEquals(expectedTable.getValue(expectedIndex, "comment_underweiter1"),
          actualTable.getValue(actualIndex, "comment_underweiter1"));
      // ???????????????????????????
      assertEquals(expectedTable.getValue(expectedIndex, "request_status"),
          actualTable.getValue(actualIndex, "request_status"));
      // ????????????
      assertEquals(expectedTable.getValue(expectedIndex, "update_count"),
          actualTable.getValue(actualIndex, "update_count").toString());
    }
  }

  /**
   * ?????????????????????????????????(????????????)
   * 
   * @author : [VJP] ??????
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
    // ???????????????????????????
    IDataSet databaseDataSet = this.idatabaseConnection.createDataSet();
    // ????????????
    ITable maintenanceRequestsActualTable = databaseDataSet.getTable("maintenance_requests");
    // ????????????
    ITable notificationsActualTable = databaseDataSet.getTable("notifications");
    // ?????????????????????
    ITable customersCorporateActualTable = databaseDataSet.getTable("customers_corporate");


    // ????????????
    ITable contractLogActualTable = databaseDataSet.getTable("contract_log");
    // ?????????????????????
    try (InputStream is = getClass().getClassLoader().getResourceAsStream(resultXml)) {
      IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(is);
      // ?????????????????????????????????
      // ????????????
      ITable maintenanceRequestsExpectedTable = expectedDataSet.getTable("maintenance_requests");

      // ?????????????????????Equal??????
      // ????????????
      // ??????????????????
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "request_no"),
          maintenanceRequestsActualTable.getValue(actualIndex, "request_no"));
      // ????????????
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "contract_no"),
          maintenanceRequestsActualTable.getValue(actualIndex, "contract_no"));
      // 2???????????????
      assertEquals(
          maintenanceRequestsExpectedTable.getValue(expectedIndex, "second_assessment_results"),
          maintenanceRequestsActualTable.getValue(actualIndex, "second_assessment_results"));
      // 2?????????????????????
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "comment_underweiter2"),
          maintenanceRequestsActualTable.getValue(actualIndex, "comment_underweiter2"));
      // ???????????????????????????
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "request_status"),
          maintenanceRequestsActualTable.getValue(actualIndex, "request_status"));
      // ????????????
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "update_count"),
          maintenanceRequestsActualTable.getValue(actualIndex, "update_count").toString());

      // ????????????
      if (testCase != testCase4) {
        // ????????????
        ITable notificationsExpectedTable = expectedDataSet.getTable("notifications");
        // ???????????????
        assertEquals(notificationsExpectedTable.getValue(expectedIndex, "notification_date"),
            notificationsActualTable.getValue(expectedIndex, "notification_date").toString());
        // ????????????
        assertEquals(notificationsExpectedTable.getValue(expectedIndex, "contract_no"),
            notificationsActualTable.getValue(expectedIndex, "contract_no"));
        // ??????????????????
        assertEquals(notificationsExpectedTable.getValue(expectedIndex, "contract_branch_no"),
            notificationsActualTable.getValue(expectedIndex, "contract_branch_no"));
        // ??????????????????????????????
        assertEquals(notificationsExpectedTable.getValue(expectedIndex, "template_number"),
            notificationsActualTable.getValue(expectedIndex, "template_number"));
        // ????????????
        assertEquals(
            notificationsExpectedTable.getValue(expectedIndex, "notification_implementation"),
            notificationsActualTable.getValue(expectedIndex, "notification_implementation"));
        // ?????????????????????
        assertEquals(notificationsExpectedTable.getValue(expectedIndex, "comment"),
            notificationsActualTable.getValue(expectedIndex, "comment"));
        // ???????????????
        assertEquals(notificationsExpectedTable.getValue(expectedIndex, "sendee"),
            notificationsActualTable.getValue(expectedIndex, "sendee"));
        // ????????????
        assertEquals(notificationsExpectedTable.getValue(expectedIndex, "notification_method"),
            notificationsActualTable.getValue(expectedIndex, "notification_method"));
        // ???????????????E?????????????????????
        assertEquals(notificationsExpectedTable.getValue(expectedIndex, "email"),
            notificationsActualTable.getValue(expectedIndex, "email"));
        // ??????????????????
        assertEquals(null, notificationsActualTable.getValue(expectedIndex, "error_flag"));
        // ?????????????????????
        assertEquals(notificationsExpectedTable.getValue(expectedIndex, "data"),
            notificationsActualTable.getValue(expectedIndex, "data"));
      }

      // #2_OC-003_SC-007,008,009
      if (testCase == testCase21) {
        // ?????????????????????
        ITable customersCorporateExpectedTable = expectedDataSet.getTable("customers_corporate");
        // ????????????E?????????????????????
        assertEquals(customersCorporateExpectedTable.getValue(expectedIndex, "contact_email"),
            customersCorporateActualTable.getValue(expectedIndex, "contact_email"));
      }

      if (testCase == testCase22) {
        // ?????????????????????
        ITable customersIndivisualActualTable = databaseDataSet.getTable("customers_individual");
        // ?????????????????????
        ITable customersIndivisualExpectedTable = expectedDataSet.getTable("customers_individual");
        // ????????????E?????????????????????
        assertEquals(customersIndivisualActualTable.getValue(1, "email"),
            customersIndivisualExpectedTable.getValue(0, "email"));
      }
      // ????????????
      int expectedLogIndex = expectedIndex;
      if (testCase == testCase22) {
        expectedLogIndex = 1;
      }
      // #2_OC-003_SC-007,008,009???#4_OC-003_SC-007,008,009??????
      if (testCase == testCase21 || testCase == testCase4) {
        expectedLogIndex = index1;
      }
      // #6_OC-003_SC-007,008,009
      if (testCase == testCase6) {
        expectedLogIndex = index3;
      }

      // ????????????
      ITable contractLogExpectedTable = expectedDataSet.getTable("contract_log");

      // ??????????????????
      assertEquals(contractLogExpectedTable.getValue(expectedIndex, "contract_no"),
          contractLogActualTable.getValue(expectedLogIndex, "contract_no"));
      // ??????????????????
      assertEquals(contractLogExpectedTable.getValue(expectedIndex, "contract_branch_no"),
          contractLogActualTable.getValue(expectedLogIndex, "contract_branch_no"));
      // ??????
      assertEquals(contractLogExpectedTable.getValue(expectedIndex, "sequence_no").toString(),
          contractLogActualTable.getValue(expectedLogIndex, "sequence_no").toString());
      // ???????????????
      assertEquals(contractLogExpectedTable.getValue(expectedIndex, "log_type"),
          contractLogActualTable.getValue(expectedLogIndex, "log_type"));
      // ??????????????????
      assertEquals(null, contractLogActualTable.getValue(expectedLogIndex, "message_code"));
      // ?????????????????????
      assertEquals(null, contractLogActualTable.getValue(expectedLogIndex, "message_group"));
      // ???????????????????????????
      assertEquals(contractLogExpectedTable.getValue(expectedIndex, "reason_group_code"),
          contractLogActualTable.getValue(expectedLogIndex, "reason_group_code"));
      // ??????ID
      assertEquals(contractLogExpectedTable.getValue(expectedIndex, "reason_code"),
          contractLogActualTable.getValue(expectedLogIndex, "reason_code"));
      // ???????????????
      assertEquals(contractLogExpectedTable.getValue(expectedIndex, "contact_transaction_code"),
          contractLogActualTable.getValue(expectedLogIndex, "contact_transaction_code"));
      // ??????
      assertEquals(contractLogExpectedTable.getValue(expectedIndex, "description"),
          contractLogActualTable.getValue(expectedLogIndex, "description"));
      // ?????????????????????
      assertEquals(contractLogExpectedTable.getValue(expectedIndex, "program_name"),
          contractLogActualTable.getValue(expectedLogIndex, "program_name"));
    }
  }

  /**
   * ?????????????????????DB?????????
   *
   * @author : [VJP] ??????
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
