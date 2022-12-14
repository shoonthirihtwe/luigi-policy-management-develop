package com.ichain.luigi2;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;
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
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;
import jp.co.ichain.luigi2.Luigi2Application;
import jp.co.ichain.luigi2.dto.ResultOneDto;
import jp.co.ichain.luigi2.test.TestScriptUtils;
import jp.co.ichain.luigi2.vo.BeneficialiesVo;
import jp.co.ichain.luigi2.vo.MaintenanceRequestsVo;

/**
 * OC-008 ???????????????
 *
 * @author : [VJP] ??????
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
class TestOc008 {

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

  private String sql = "sql/insert_OC008.sql";

  // ????????????????????????
  private int expectedIndex = 0;
  // ?????????????????????
  private int index0 = 0;
  // ?????????????????????
  private int index1 = 1;
  // ?????????????????????
  private int index2 = 2;
  // maintenance_requests???????????????
  private int maintenanceCount = 7;

  /**
   * OC-008 ??????????????? setup
   *
   * @author : [VJP] ??????
   * @createdAt : 2021-08-17
   * @updatedAt : 2021-08-17
   */
  @BeforeEach
  void setup() throws SQLException, DatabaseUnitException {
    this.connection = dataSource.getConnection();
    this.idatabaseConnection = new MySqlConnection(connection, "luigi2_test");

    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.ctx)
        .addFilters(new CharacterEncodingFilter("UTF-8", true)).build();
    // ?????????????????????SQL??????
    testScriptUtils.executeSqlScript(sql);
  }

  /**
   * OC-008 ??????????????? ???????????????
   * 
   * #1_OC-007,SC-025-026
   * 
   * @author : [VJP] ??????
   * @createdAt : 2021-08-17
   * @updatedAt : 2021-08-17
   */
  @Test
  void testOc00801() throws Exception {
    // ??????
    String requestJsonForGet = "json/request/oc008/req_OC008_01_get_data.json";
    String urlForGet = "/OC00803";
    String resultJsonForGet = "json/response/oc008/res_OC008_01_data.json";
    runTestCaseForGet(requestJsonForGet, urlForGet, resultJsonForGet);

    // ?????????????????????
    String requestJsonForPut = "json/request/oc008/req_OC008_01_put_data.json";
    String urlForPut = "/OC00801";
    String resultXmlForPut = "xml/oc008/result_OC008_01.xml";
    runTestCaseForFirstPut(requestJsonForPut, urlForPut, resultXmlForPut, index0);
  }

  /**
   * OC-008 ??????????????? ???????????????
   * 
   * #2_OC-007,SC-025-026
   * 
   * @author : [VJP] ??????
   * @createdAt : 2021-08-17
   * @updatedAt : 2021-08-17
   */
  @Test
  void testOc00802() throws Exception {
    // ??????
    String requestJsonForGet = "json/request/oc008/req_OC008_02_get_data.json";
    String urlForGet = "/OC00803";
    String resultJsonForGet = "json/response/oc008/res_OC008_02_data.json";
    runTestCaseForGet(requestJsonForGet, urlForGet, resultJsonForGet);

    // ?????????????????????
    String requestJsonForPut = "json/request/oc008/req_OC008_02_put_data.json";
    String urlForPut = "/OC00801";
    String resultXmlForPut = "xml/oc008/result_OC008_02.xml";
    runTestCaseForFirstPut(requestJsonForPut, urlForPut, resultXmlForPut, index1);
  }

  /**
   * OC-008 ??????????????? ???????????????
   * 
   * #3_OC-007,SC-025-026
   * 
   * @author : [VJP] ??????
   * @createdAt : 2021-08-17
   * @updatedAt : 2021-08-17
   */
  @Test
  void testOc00803() throws Exception {
    // ??????
    String requestJsonForGet = "json/request/oc008/req_OC008_03_get_data.json";
    String urlForGet = "/OC00803";
    String resultJsonForGet = "json/response/oc008/res_OC008_03_data.json";
    runTestCaseForGet(requestJsonForGet, urlForGet, resultJsonForGet);

    // ?????????????????????
    String requestJsonForPut = "json/request/oc008/req_OC008_03_put_data.json";
    String urlForPut = "/OC00801";
    String resultXmlForPut = "xml/oc008/result_OC008_03.xml";
    runTestCaseForFirstPut(requestJsonForPut, urlForPut, resultXmlForPut, index2);
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
   * @author : [VJP] ??????
   * @param requestJson
   * @param url
   * @param resultJson
   * @throws Exception
   * @createdAt : 2021-08-17
   * @updatedAt : 2021-08-17
   */
  private void runTestCaseForGet(String requestJson, String url, String resultJson)
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
    // ????????????
    assertEquals(expected.getTransactionCode(), actual.getTransactionCode());
    // ??????????????????????????????
    assertEquals(expected.getContractEmail(), actual.getContractEmail());
    // ?????????
    int beneficiariesExpectedSize = expected.getBeneficiariesList().size();
    int beneficiariesActualSize = actual.getBeneficiariesList().size();
    assertEquals(beneficiariesExpectedSize, beneficiariesActualSize);
    for (int i = 0; i < beneficiariesExpectedSize; i++) {
      // ?????????
      BeneficialiesVo expectedBeneficiaries = expected.getBeneficiariesList().get(i);
      BeneficialiesVo actualBeneficiaries = actual.getBeneficiariesList().get(i);

      // ??????/????????????
      assertEquals(expectedBeneficiaries.getCorporateIndividualFlag(),
          actualBeneficiaries.getCorporateIndividualFlag());
      // ??????(??????) ???
      assertEquals(expectedBeneficiaries.getNameKnjSei(), actualBeneficiaries.getNameKnjSei());
      // ??????(??????) ???
      assertEquals(expectedBeneficiaries.getNameKnjMei(), actualBeneficiaries.getNameKnjMei());
      // ??????(??????) ??????
      assertEquals(expectedBeneficiaries.getNameKanaSei(), actualBeneficiaries.getNameKanaSei());
      // ??????(??????) ??????
      assertEquals(expectedBeneficiaries.getNameKanaMei(), actualBeneficiaries.getNameKanaMei());
      // ????????????????????????
      assertEquals(expectedBeneficiaries.getRelShipToInsured(),
          actualBeneficiaries.getRelShipToInsured());
      // ????????????
      assertEquals(expectedBeneficiaries.getShare(), actualBeneficiaries.getShare());
    }
    // ??????
    int documentsExpectedSize = expected.getDocumentsList().size();
    int documentsActualSize = actual.getDocumentsList().size();
    assertEquals(documentsExpectedSize, documentsActualSize);
    for (int i = 0; i < documentsExpectedSize; i++) {
      // ????????????
      assertEquals(expected.getDocumentsList().get(i).getDocumentTitle(),
          actual.getDocumentsList().get(i).getDocumentTitle());
      // URL
      assertEquals(expected.getDocumentsList().get(i).getDocumentUrl(),
          actual.getDocumentsList().get(i).getDocumentUrl());
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
   * @createdAt : 2021-08-17
   * @updatedAt : 2021-08-17
   */
  private void runTestCaseForFirstPut(String requestJson, String url, String resultXml,
      int actualIndex) throws Exception {
    mockMvcForPut(url, requestJson);
    // ???????????????????????????
    IDataSet databaseDataSet = this.idatabaseConnection.createDataSet();
    // ????????????
    ITable maintenanceRequestsActualTable = databaseDataSet.getTable("maintenance_requests");
    // ?????????????????????
    ITable beneficiariesActualTable =
        databaseDataSet.getTable("maintenance_requests_beneficiaries");
    // ????????????
    ITable contractLogActualTable = databaseDataSet.getTable("contract_log");

    // ?????????????????????
    try (InputStream is = getClass().getClassLoader().getResourceAsStream(resultXml)) {
      IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(is);
      // ?????????????????????????????????
      // ????????????
      ITable maintenanceRequestsExpectedTable = expectedDataSet.getTable("maintenance_requests");
      // ?????????????????????Equal??????
      // ????????????ID
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "tenant_id"),
          maintenanceRequestsActualTable.getValue(actualIndex, "tenant_id").toString());
      // ??????????????????
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "request_no"),
          maintenanceRequestsActualTable.getValue(actualIndex, "request_no"));
      // ????????????
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "contract_no"),
          maintenanceRequestsActualTable.getValue(actualIndex, "contract_no"));
      // ??????????????????
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "contract_branch_no"),
          maintenanceRequestsActualTable.getValue(actualIndex, "contract_branch_no"));
      // ??????/???????????????
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "active_inactive"),
          maintenanceRequestsActualTable.getValue(actualIndex, "active_inactive"));
      // ??????????????????
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "transaction_code"),
          maintenanceRequestsActualTable.getValue(actualIndex, "transaction_code"));
      // ???????????????????????????
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "request_status"),
          maintenanceRequestsActualTable.getValue(actualIndex, "request_status"));
      // ?????????
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "application_date"),
          maintenanceRequestsActualTable.getValue(actualIndex, "application_date").toString());
      // ????????????
      assertEquals(null, maintenanceRequestsActualTable.getValue(actualIndex, "application_time"));
      // ????????????
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "application_method"),
          maintenanceRequestsActualTable.getValue(actualIndex, "application_method"));
      // ?????????
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "received_date"),
          maintenanceRequestsActualTable.getValue(actualIndex, "received_date").toString());
      // ????????????
      assertEquals(null, maintenanceRequestsActualTable.getValue(actualIndex, "received_at"));
      // ????????????????????????
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "comment_underweiter1"),
          maintenanceRequestsActualTable.getValue(actualIndex, "comment_underweiter1"));
      // ??????????????????
      assertEquals(
          maintenanceRequestsExpectedTable.getValue(expectedIndex, "first_assessment_results"),
          maintenanceRequestsActualTable.getValue(actualIndex, "first_assessment_results"));
      // ????????????????????????
      assertEquals(null,
          maintenanceRequestsActualTable.getValue(actualIndex, "comment_underweiter2"));
      // ??????????????????
      assertEquals(null,
          maintenanceRequestsActualTable.getValue(actualIndex, "second_assessment_results"));
      // ?????????
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "communication_column"),
          maintenanceRequestsActualTable.getValue(actualIndex, "communication_column"));
      // ?????????
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "apply_date"),
          maintenanceRequestsActualTable.getValue(actualIndex, "apply_date").toString());
      // ??????????????????
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "entry_type"),
          maintenanceRequestsActualTable.getValue(actualIndex, "entry_type"));
      // ?????????????????????
      assertEquals(null,
          maintenanceRequestsActualTable.getValue(actualIndex, "payment_method_code"));
      // ???????????????????????????
      assertEquals(null,
          maintenanceRequestsActualTable.getValue(actualIndex, "factoring_company_code"));

      // ???????????????
      assertEquals(null, maintenanceRequestsActualTable.getValue(actualIndex, "bank_code"));
      // ???????????????
      assertEquals(null, maintenanceRequestsActualTable.getValue(actualIndex, "bank_branch_code"));
      // ????????????
      assertEquals(null, maintenanceRequestsActualTable.getValue(actualIndex, "bank_account_type"));
      // ????????????
      assertEquals(null, maintenanceRequestsActualTable.getValue(actualIndex, "bank_account_no"));
      // ???????????????
      assertEquals(null, maintenanceRequestsActualTable.getValue(actualIndex, "bank_account_name"));
      // ???????????????(????????????)
      assertEquals(null, maintenanceRequestsActualTable.getValue(actualIndex, "token_no"));
      // ??????????????????????????????
      assertEquals(null,
          maintenanceRequestsActualTable.getValue(actualIndex, "email_for_notification"));
      // ???????????????
      assertEquals(null,
          maintenanceRequestsActualTable.getValue(actualIndex, "termination_base_date"));
      // ????????????
      assertEquals(null, maintenanceRequestsActualTable.getValue(actualIndex, "termination_title"));
      // ????????????
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "update_count"),
          maintenanceRequestsActualTable.getValue(actualIndex, "update_count").toString());

      // ?????????????????????
      if (beneficiariesActualTable.getRowCount() > maintenanceCount) {
        ITable beneficiariesExpectedTable =
            expectedDataSet.getTable("maintenance_requests_beneficiaries");
        for (int i = 0; i < beneficiariesExpectedTable.getRowCount(); i++) {
          // ???????????????????????????: 7
          int j = i + maintenanceCount;
          // ????????????ID
          assertEquals(beneficiariesExpectedTable.getValue(i, "tenant_id"),
              beneficiariesActualTable.getValue(j, "tenant_id").toString());
          // ??????????????????
          assertEquals(beneficiariesExpectedTable.getValue(i, "request_no"),
              beneficiariesActualTable.getValue(j, "request_no"));
          // ???????????????????????????
          assertEquals(beneficiariesExpectedTable.getValue(i, "before_after"),
              beneficiariesActualTable.getValue(j, "before_after"));
          // ??????????????????
          assertEquals(beneficiariesExpectedTable.getValue(i, "role_type"),
              beneficiariesActualTable.getValue(j, "role_type"));
          // ???????????????
          assertEquals(beneficiariesExpectedTable.getValue(i, "role_sequence_no"),
              beneficiariesActualTable.getValue(j, "role_sequence_no").toString());
          // ??????/????????????
          assertEquals(beneficiariesExpectedTable.getValue(i, "corporate_individual_flag"),
              beneficiariesActualTable.getValue(j, "corporate_individual_flag"));
          // ?????????(??????)/?????????(??????)
          assertEquals(beneficiariesExpectedTable.getValue(i, "name_knj_sei"),
              beneficiariesActualTable.getValue(j, "name_knj_sei"));
          // ?????????(??????)
          assertEquals(beneficiariesExpectedTable.getValue(i, "name_knj_mei"),
              beneficiariesActualTable.getValue(j, "name_knj_mei"));
          // ?????????(??????)/?????????(??????)
          assertEquals(beneficiariesExpectedTable.getValue(i, "name_kana_sei"),
              beneficiariesActualTable.getValue(j, "name_kana_sei"));
          // ?????????(??????)
          assertEquals(beneficiariesExpectedTable.getValue(i, "name_kana_mei"),
              beneficiariesActualTable.getValue(j, "name_kana_mei"));
          // ???????????????
          assertEquals(beneficiariesExpectedTable.getValue(i, "share"),
              beneficiariesActualTable.getValue(j, "share").toString());
          // ??????????????????????????????
          assertEquals(beneficiariesExpectedTable.getValue(i, "rel_ship_to_insured"),
              beneficiariesActualTable.getValue(j, "rel_ship_to_insured"));
          // ????????????
          assertEquals(beneficiariesExpectedTable.getValue(i, "update_count"),
              beneficiariesActualTable.getValue(j, "update_count").toString());
        }
      }

      // ????????????
      if (contractLogActualTable.getRowCount() > 0) {
        if (actualIndex != expectedIndex) {
          actualIndex = 0;
        }
        // ????????????
        ITable contractLogExpectedTable = expectedDataSet.getTable("contract_log");
        // ????????????ID
        assertEquals(contractLogExpectedTable.getValue(expectedIndex, "tenant_id"),
            contractLogActualTable.getValue(actualIndex, "tenant_id").toString());

        // ??????????????????
        assertEquals(contractLogExpectedTable.getValue(expectedIndex, "contract_no"),
            contractLogActualTable.getValue(actualIndex, "contract_no"));
        // ??????????????????
        assertEquals(contractLogExpectedTable.getValue(expectedIndex, "contract_branch_no"),
            contractLogActualTable.getValue(actualIndex, "contract_branch_no"));
        // ??????
        assertEquals(contractLogExpectedTable.getValue(expectedIndex, "sequence_no"),
            contractLogActualTable.getValue(actualIndex, "sequence_no").toString());
        // ???????????????
        assertEquals(contractLogExpectedTable.getValue(expectedIndex, "log_type"),
            contractLogActualTable.getValue(actualIndex, "log_type"));
        // ??????????????????
        assertEquals(null, contractLogActualTable.getValue(actualIndex, "message_code"));
        // ?????????????????????
        assertEquals(null, contractLogActualTable.getValue(actualIndex, "message_group"));
        // ???????????????????????????
        assertEquals(contractLogExpectedTable.getValue(expectedIndex, "reason_group_code"),
            contractLogActualTable.getValue(actualIndex, "reason_group_code"));
        // ??????ID
        assertEquals(contractLogExpectedTable.getValue(expectedIndex, "reason_code"),
            contractLogActualTable.getValue(actualIndex, "reason_code"));
        // ???????????????
        assertEquals(contractLogExpectedTable.getValue(expectedIndex, "contact_transaction_code"),
            contractLogActualTable.getValue(actualIndex, "contact_transaction_code"));
        // ??????
        assertEquals(contractLogExpectedTable.getValue(expectedIndex, "description"),
            contractLogActualTable.getValue(actualIndex, "description"));
        // ?????????????????????
        assertEquals(contractLogExpectedTable.getValue(expectedIndex, "program_name"),
            contractLogActualTable.getValue(actualIndex, "program_name"));
        // ????????????
        assertEquals(contractLogExpectedTable.getValue(expectedIndex, "update_count"),
            contractLogActualTable.getValue(actualIndex, "update_count").toString());
      }
    }
  }

  /**
   * ?????????????????????DB?????????
   *
   * @author : [VJP] ??????
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
