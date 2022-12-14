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
 * OC-005 ??????
 *
 * @author : [VJP] ?????????
 * @createdAt : 2021-08-17
 * @updatedAt : 2021-08-17
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = Luigi2Application.class)
@TestPropertySource("classpath:application-common-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class TestOc005 {

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

  private String sql = "sql/insert_OC005.sql";
  // ????????????????????????
  private int expectedIndex = 0;
  // ??????????????????
  private int maintenanceRequestsActualIndex = 2;

  /**
   * OC-005 ?????? setup
   *
   * @author : [VJP] ?????????
   * @throws ParseException
   * @throws IOException
   * @throws SecurityException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws UnsupportedEncodingException
   * @throws JsonProcessingException
   * @throws JsonMappingException
   * @throws JsonParseException
   * @createdAt : 2021-08-17
   * @updatedAt : 2021-08-17
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
    testScriptUtils.updateOnlineDate("2022-08-09", 1);
    testScriptUtils.updateBatchDate("2022-08-09", 1);
  }

  /**
   * OC-005 ?????? ???????????????
   * 
   * #1_OC-005_SC-015,016,019 ??????1?????????????????????????????????
   * 
   * @author : [VJP] ?????????
   * @createdAt : 2021-08-17
   * @updatedAt : 2021-08-17
   */
  @Test
  void testOc00501() throws Exception {
    // ??????
    String requestJsonForGet = "json/request/oc005/req_OC005_get_data.json";
    String urlForGet = "/OC00303";
    String resultJsonForGet = "json/response/oc005/res_OC005_01_get_data.json";
    runTestCaseForGet(requestJsonForGet, urlForGet, resultJsonForGet, false);

    // ?????????????????????
    String requestJsonForPut = "json/request/oc005/req_OC005_01_put_data.json";
    String urlForPut = "/OC00501";
    String resultXmlForPut = "xml/oc005/result_OC005_01_put.xml";
    runTestCaseForFirstPut(requestJsonForPut, urlForPut, resultXmlForPut);
  }

  /**
   * OC-005 ?????? ???????????????
   * 
   * #2_OC-005_SC-017,019,019 ??????2?????????????????????????????????
   * 
   * @author : [VJP] ?????????
   * @createdAt : 2021-08-17
   * @updatedAt : 2021-08-17
   */
  @Test
  void testOc00502() throws Exception {
    // ????????????
    testOc00501();

    // ?????????????????????
    String requestJsonForGet = "json/request/oc005/req_OC005_get_data.json";
    String urlForGet = "/OC00303";
    String resultJsonForGet = "json/response/oc005/res_OC005_02_get_data.json";
    runTestCaseForGet(requestJsonForGet, urlForGet, resultJsonForGet, true);

    // ????????????????????????????????????
    String requestJsonForPutSecond = "json/request/oc005/req_OC005_02_put_data.json";
    String urlForPutSecond = "/OC00502";
    String resultXmlForPutSecond = "xml/oc005/result_OC005_02_put.xml";
    runTestCaseForSecondPut(requestJsonForPutSecond, urlForPutSecond, resultXmlForPutSecond);
  }

  /**
   * Mock request for get
   * 
   * @author : [VJP] ?????????
   * @param sql
   * @param url
   * @param requestJson
   * @return
   * @throws JsonParseException
   * @throws JsonMappingException
   * @throws UnsupportedEncodingException
   * @throws IOException
   * @throws Exception
   * @createdAt : 2021-08-17
   * @updatedAt : 2021-08-17
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
   * @author : [VJP] ?????????
   * @param sql
   * @param url
   * @param requestJson
   * @return
   * @throws JsonParseException
   * @throws JsonMappingException
   * @throws UnsupportedEncodingException
   * @throws IOException
   * @throws Exception
   * @createdAt : 2021-08-17
   * @updatedAt : 2021-08-17
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
   * @author : [VJP] ?????????
   * @param requestJson
   * @param url
   * @param resultJson
   * @param second ????????????
   * @throws Exception
   * @createdAt : 2021-08-17
   * @updatedAt : 2021-08-17
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
    int documentsExpectedSize = expected.getDocumentsList().size();
    int documentsActualSize = actual.getDocumentsList().size();
    assertEquals(documentsExpectedSize, documentsActualSize);
    for (int i = 0; i < documentsExpectedSize; i++) {
      // URL
      assertEquals(expected.getDocumentsList().get(i).getDocumentUrl(),
          actual.getDocumentsList().get(i).getDocumentUrl());
      // ????????????
      assertEquals(expected.getDocumentsList().get(i).getDocumentTitle(),
          actual.getDocumentsList().get(i).getDocumentTitle());
    }
    if (second) {
      // ?????????
      assertEquals(expected.getTerminationBaseDate(), actual.getTerminationBaseDate());
      // ????????????????????????
      assertEquals(expected.getCommentUnderweiter1(), actual.getCommentUnderweiter1());
      // ??????????????????
      assertEquals(expected.getFirstAssessmentResults(), actual.getFirstAssessmentResults());
    }
  }

  /**
   * ???????????????????????????(???????????????????????????)
   * 
   * @author : [VJP] ?????????
   * @param requestJson
   * @param url
   * @param resultJson
   * @throws Exception
   * @createdAt : 2021-08-17
   * @updatedAt : 2021-08-17
   */
  private void runTestCaseForFirstPut(String requestJson, String url, String resultXml)
      throws Exception {

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
      // ??????????????????
      assertEquals(expectedTable.getValue(expectedIndex, "request_no"),
          actualTable.getValue(maintenanceRequestsActualIndex, "request_no"));
      // ????????????
      assertEquals(expectedTable.getValue(expectedIndex, "contract_no"),
          actualTable.getValue(maintenanceRequestsActualIndex, "contract_no"));
      // ?????????
      assertEquals(expectedTable.getValue(expectedIndex, "apply_date"),
          actualTable.getValue(maintenanceRequestsActualIndex, "apply_date").toString());
      // ???????????????
      assertEquals(expectedTable.getValue(expectedIndex, "termination_base_date"),
          actualTable.getValue(maintenanceRequestsActualIndex, "termination_base_date").toString());
      // ?????????
      assertEquals(null,
          actualTable.getValue(maintenanceRequestsActualIndex, "communication_column"));
      // ??????????????????
      assertEquals(expectedTable.getValue(expectedIndex, "first_assessment_results"),
          actualTable.getValue(maintenanceRequestsActualIndex, "first_assessment_results"));
      // ????????????????????????
      assertEquals(expectedTable.getValue(expectedIndex, "comment_underweiter1"),
          actualTable.getValue(maintenanceRequestsActualIndex, "comment_underweiter1"));
      // ???????????????????????????
      assertEquals(expectedTable.getValue(expectedIndex, "request_status"),
          actualTable.getValue(maintenanceRequestsActualIndex, "request_status"));

    }
  }

  /**
   * ???????????????????????????(???????????????????????????)
   * 
   * @author : [VJP] ?????????
   * @param requestJson
   * @param url
   * @param resultJson
   * @throws Exception
   * @createdAt : 2021-08-17
   * @updatedAt : 2021-08-17
   */
  private void runTestCaseForSecondPut(String requestJson, String url, String resultXml)
      throws Exception {

    int contractsActualIndex = 1;
    int notificationsActualIndex = 0;
    int contractLogActualIndex = 1;

    mockMvcForPut(url, requestJson);
    // ???????????????????????????
    IDataSet databaseDataSet = this.idatabaseConnection.createDataSet();

    // ????????????
    ITable maintenanceRequestsActualTable = databaseDataSet.getTable("maintenance_requests");
    // ????????????
    ITable notificationsActualTable = databaseDataSet.getTable("notifications");
    // ??????
    ITable contractsActualTable = databaseDataSet.getTable("contracts");
    // ????????????
    ITable contractLogActualTable = databaseDataSet.getTable("contract_log");

    // ?????????????????????
    try (InputStream is = getClass().getClassLoader().getResourceAsStream(resultXml)) {
      IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(is);

      // ????????????????????????????????????????????????
      ITable maintenanceRequestsExpectedTable = expectedDataSet.getTable("maintenance_requests");

      // ?????????????????????Equal?????????????????????
      // ??????????????????
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "request_no"),
          maintenanceRequestsActualTable.getValue(maintenanceRequestsActualIndex, "request_no"));
      // ????????????
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "contract_no"),
          maintenanceRequestsActualTable.getValue(maintenanceRequestsActualIndex, "contract_no"));
      // 2???????????????
      assertEquals(
          maintenanceRequestsExpectedTable.getValue(expectedIndex, "second_assessment_results"),
          maintenanceRequestsActualTable.getValue(maintenanceRequestsActualIndex,
              "second_assessment_results"));
      // 2?????????????????????
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "comment_underweiter2"),
          maintenanceRequestsActualTable.getValue(maintenanceRequestsActualIndex,
              "comment_underweiter2"));
      // ???????????????????????????
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "request_status"),
          maintenanceRequestsActualTable.getValue(maintenanceRequestsActualIndex,
              "request_status"));

      // ??????????????????????????????????????????
      ITable contractsExpectedTable = expectedDataSet.getTable("contracts");

      // ?????????????????????Equal???????????????
      // ????????????
      assertEquals(contractsExpectedTable.getValue(expectedIndex, "contract_no"),
          contractsActualTable.getValue(contractsActualIndex, "contract_no"));
      // ??????????????????
      assertEquals(contractsExpectedTable.getValue(expectedIndex, "contract_branch_no"),
          contractsActualTable.getValue(contractsActualIndex, "contract_branch_no"));
      // ?????????????????????
      assertEquals(contractsExpectedTable.getValue(expectedIndex, "contract_status"),
          contractsActualTable.getValue(contractsActualIndex, "contract_status"));
      // ???????????????
      assertEquals(contractsExpectedTable.getValue(expectedIndex, "termination_date"),
          contractsActualTable.getValue(contractsActualIndex, "termination_date").toString());

      // ????????????????????????????????????????????????
      ITable notificationsExpectedTable = expectedDataSet.getTable("notifications");

      // ?????????????????????Equal?????????????????????
      // ???????????????
      assertEquals(notificationsExpectedTable.getValue(expectedIndex, "notification_date"),
          notificationsActualTable.getValue(notificationsActualIndex, "notification_date")
              .toString());
      // ????????????
      assertEquals(notificationsExpectedTable.getValue(expectedIndex, "contract_no"),
          notificationsActualTable.getValue(notificationsActualIndex, "contract_no"));
      // ??????????????????
      assertEquals(notificationsExpectedTable.getValue(expectedIndex, "contract_branch_no"),
          notificationsActualTable.getValue(notificationsActualIndex, "contract_branch_no"));
      // ??????????????????????????????
      assertEquals(notificationsExpectedTable.getValue(expectedIndex, "template_number"),
          notificationsActualTable.getValue(notificationsActualIndex, "template_number"));
      // ????????????
      assertEquals(
          notificationsExpectedTable.getValue(expectedIndex, "notification_implementation"),
          notificationsActualTable.getValue(notificationsActualIndex,
              "notification_implementation"));
      // ?????????????????????
      assertEquals(null, notificationsActualTable.getValue(expectedIndex, "comment"));
      // ???????????????
      assertEquals(notificationsExpectedTable.getValue(expectedIndex, "sendee"),
          notificationsActualTable.getValue(notificationsActualIndex, "sendee"));
      // ????????????
      assertEquals(notificationsExpectedTable.getValue(expectedIndex, "notification_method"),
          notificationsActualTable.getValue(notificationsActualIndex, "notification_method"));
      // E?????????????????????
      assertEquals(notificationsExpectedTable.getValue(expectedIndex, "email"),
          notificationsActualTable.getValue(notificationsActualIndex, "email"));
      // ??????????????????
      assertEquals(null, notificationsActualTable.getValue(notificationsActualIndex, "error_flag"));

      // ????????????????????????????????????????????????
      ITable contractLogExpectedTable = expectedDataSet.getTable("contract_log");

      // ?????????????????????Equal?????????????????????
      // ??????????????????
      assertEquals(contractLogExpectedTable.getValue(expectedIndex, "contract_no"),
          contractLogActualTable.getValue(contractLogActualIndex, "contract_no"));
      // ??????????????????
      assertEquals(contractLogExpectedTable.getValue(expectedIndex, "contract_branch_no"),
          contractLogActualTable.getValue(contractLogActualIndex, "contract_branch_no"));
      // ??????
      assertEquals(contractLogExpectedTable.getValue(expectedIndex, "sequence_no").toString(),
          contractLogActualTable.getValue(contractLogActualIndex, "sequence_no").toString());
      // ???????????????
      assertEquals(contractLogExpectedTable.getValue(expectedIndex, "log_type"),
          contractLogActualTable.getValue(contractLogActualIndex, "log_type"));
      // ??????????????????
      assertEquals(null, contractLogActualTable.getValue(contractLogActualIndex, "message_code"));
      // ?????????????????????
      assertEquals(null, contractLogActualTable.getValue(contractLogActualIndex, "message_group"));
      // ???????????????????????????
      assertEquals(contractLogExpectedTable.getValue(expectedIndex, "reason_group_code"),
          contractLogActualTable.getValue(contractLogActualIndex, "reason_group_code"));
      // ??????ID
      assertEquals(contractLogExpectedTable.getValue(expectedIndex, "reason_code"),
          contractLogActualTable.getValue(contractLogActualIndex, "reason_code"));
      // ???????????????
      assertEquals(contractLogExpectedTable.getValue(expectedIndex, "contact_transaction_code"),
          contractLogActualTable.getValue(contractLogActualIndex, "contact_transaction_code"));
      // ??????
      assertEquals(contractLogExpectedTable.getValue(expectedIndex, "description"),
          contractLogActualTable.getValue(contractLogActualIndex, "description"));
      // ?????????????????????
      assertEquals(contractLogExpectedTable.getValue(expectedIndex, "program_name"),
          contractLogActualTable.getValue(contractLogActualIndex, "program_name"));
    }
  }

  /**
   * ?????????????????????DB?????????
   *
   * @author : [VJP] ?????????
   * @throws SQLException
   * @throws ScriptException
   * @createdAt : 2021-08-17
   * @updatedAt : 2021-08-17
   */
  @AfterEach
  void clean() throws ScriptException, SQLException {
    testScriptUtils.cleanUpDatabase();
    this.connection.close();
  }
}
