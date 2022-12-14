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
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
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
import jp.co.ichain.luigi2.vo.MaintenanceRequestsCustomersVo;
import jp.co.ichain.luigi2.vo.MaintenanceRequestsVo;



/**
 * OC-006 ??????????????????
 *
 * @author : [VJP] HoangNH
 * @createdAt : 2021-08-18
 * @updatedAt : 2021-08-18
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = Luigi2Application.class)
@TestPropertySource("classpath:application-common-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class TestOc006 {

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

  private String sql = "sql/insert_OC006.sql";

  // ??????????????????
  JsonDeserializer<Date> deser = (json, typeOfT, context) -> json == null ? null
      : new Date(Long.valueOf(json.getAsString().replaceAll("\\D", "")));
  Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, deser).create();
  Type responseType = new TypeToken<ResultOneDto<MaintenanceRequestsVo>>() {}.getType();

  /**
   * OC-006 ?????????????????? setup
   *
   * @author : [VJP] HoangNH
   * @throws ParseException
   * @throws IOException
   * @throws SecurityException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws UnsupportedEncodingException
   * @throws JsonProcessingException
   * @throws JsonMappingException
   * @throws JsonParseException
   * @createdAt : 2021-08-18
   * @updatedAt : 2021-08-18
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
    testScriptUtils.updateOnlineDate("2021-08-01", 1);
    testScriptUtils.updateBatchDate("2021-08-01", 1);
  }

  /**
   * #1_OC-006,SC-020-021
   * 
   * @author : [VJP] HoangNH
   * @param requestJson
   * @param url
   * @param resultJson
   * @throws Exception
   * @createdAt : 2021-08-18
   * @updatedAt : 2021-08-18
   */
  @Test
  void testOc00601() throws Exception {
    MvcResult mvcResult = mockMvcForGet("/OC00603", "json/request/oc006/req_OC00601_data_get.json");
    // API?????????
    ResultOneDto<MaintenanceRequestsVo> res =
        gson.fromJson(mvcResult.getResponse().getContentAsString(), responseType);
    MaintenanceRequestsVo actual = res.getItem();
    // ?????????????????????
    MaintenanceRequestsVo expected = gson.fromJson(
        testScriptUtils.loadJsonToString("json/response/oc006/res_OC006_01_data.json"),
        MaintenanceRequestsVo.class);

    compareMaintenanceRequests(actual, expected);
    mockMvcForPut("/OC00601", "json/request/oc006/req_OC00601_data_put.json");

    Map<String, Integer> paramDatatable = new HashMap<String, Integer>();
    paramDatatable.put("maintenance_requests", 0);
    paramDatatable.put("maintenance_requests_customer", 5);
    paramDatatable.put("maintenance_requests_customer_corporate", 2);

    compareTable("xml/oc006/result_OC00601.xml", 0, paramDatatable);
    // ??????????????????
    compareContractLog("xml/oc006/result_OC00601.xml");
  }

  /**
   * #2_OC-006,SC-020-021
   * 
   * @author : [VJP] HoangNH
   * @param requestJson
   * @param url
   * @param resultJson
   * @throws Exception
   * @createdAt : 2021-08-18
   * @updatedAt : 2021-08-18
   */
  @Test
  void testOc00602() throws Exception {
    MvcResult mvcResult = mockMvcForGet("/OC00603", "json/request/oc006/req_OC00602_data_get.json");
    // API?????????
    ResultOneDto<MaintenanceRequestsVo> res =
        gson.fromJson(mvcResult.getResponse().getContentAsString(), responseType);

    MaintenanceRequestsVo actual = res.getItem();
    // ?????????????????????
    MaintenanceRequestsVo expected = gson.fromJson(
        testScriptUtils.loadJsonToString("json/response/oc006/res_OC006_02_data.json"),
        MaintenanceRequestsVo.class);

    compareMaintenanceRequests(actual, expected);
    mockMvcForPut("/OC00601", "json/request/oc006/req_OC00602_data_put.json");

    Map<String, Integer> paramDatatable = new HashMap<String, Integer>();
    paramDatatable.put("maintenance_requests", 2);

    // ??????????????????
    compareContractLog("xml/oc006/result_OC00602.xml");

    compareTable("xml/oc006/result_OC00602.xml", 0, paramDatatable);
  }

  /**
   * #3_OC-006,SC-020-021
   * 
   * @author : [VJP] HoangNH
   * @param requestJson
   * @param url
   * @param resultJson
   * @throws Exception
   * @createdAt : 2021-08-18
   * @updatedAt : 2021-08-18
   */
  @Test
  void testOc00603() throws Exception {
    MvcResult mvcResult = mockMvcForGet("/OC00603", "json/request/oc006/req_OC00603_data_get.json");
    // API?????????
    ResultOneDto<MaintenanceRequestsVo> res =
        gson.fromJson(mvcResult.getResponse().getContentAsString(), responseType);

    MaintenanceRequestsVo actual = res.getItem();
    // ?????????????????????
    MaintenanceRequestsVo expected = gson.fromJson(
        testScriptUtils.loadJsonToString("json/response/oc006/res_OC006_03_data.json"),
        MaintenanceRequestsVo.class);

    compareMaintenanceRequests(actual, expected);
    mockMvcForPut("/OC00601", "json/request/oc006/req_OC00603_data_put.json");

    Map<String, Integer> paramDatatable = new HashMap<String, Integer>();
    paramDatatable.put("maintenance_requests", 3);
    paramDatatable.put("maintenance_requests_customer", 5);

    paramDatatable.put("contract_log", 0);
    compareTable("xml/oc006/result_OC00603.xml", 0, paramDatatable);
    Integer[] params = {3, 4};
    compareCustomersIndividualTable2("xml/oc006/result_OC00603.xml", params);

    // ??????????????????
    compareContractLog("xml/oc006/result_OC00603.xml");
  }


  /**
   * #4_OC-006,SC-020-021
   * 
   * @author : [VJP] HoangNH
   * @param requestJson
   * @param url
   * @param resultJson
   * @throws Exception
   * @createdAt : 2021-08-18
   * @updatedAt : 2021-08-18
   */
  @Test
  void testOc00604() throws Exception {
    MvcResult mvcResult = mockMvcForGet("/OC00603", "json/request/oc006/req_OC00604_data_get.json");
    // API?????????
    ResultOneDto<MaintenanceRequestsVo> res =
        gson.fromJson(mvcResult.getResponse().getContentAsString(), responseType);
    MaintenanceRequestsVo actual = res.getItem();
    // ?????????????????????
    MaintenanceRequestsVo expected = gson.fromJson(
        testScriptUtils.loadJsonToString("json/response/oc006/res_OC006_04_data.json"),
        MaintenanceRequestsVo.class);

    compareMaintenanceRequests(actual, expected);
    mockMvcForPut("/OC00601", "json/request/oc006/req_OC00604_data_put.json");

    Map<String, Integer> paramDatatable = new HashMap<String, Integer>();
    paramDatatable.put("maintenance_requests", 5);
    paramDatatable.put("maintenance_requests_customer", 5);
    paramDatatable.put("maintenance_requests_customer_corporate", 2);

    compareTable("xml/oc006/result_OC00604.xml", 0, paramDatatable);

    // ??????????????????
    compareContractLog("xml/oc006/result_OC00604.xml");
  }

  /**
   * #5_OC-006,SC-020-021
   * 
   * @author : [VJP] hale
   * @param requestJson
   * @param url
   * @param resultJson
   * @throws Exception
   * @createdAt : 2021-08-19
   * @updatedAt : 2021-08-19
   */
  @Test
  void testOc00605() throws Exception {
    String urlForGet = "/OC00603";
    String requestUrlForGet = "json/request/oc006/req_OC00605_data_get.json";
    String resultJson = "json/response/oc006/res_OC006_05_data.json";

    String urlForPut = "/OC00602";
    String requestUrlForPut = "json/request/oc006/req_OC00605_data_put.json";
    String resultXml = "xml/oc006/result_OC00605.xml";

    // GET
    MvcResult mvcResult = mockMvcForGet(urlForGet, requestUrlForGet);
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

    compareMaintenanceRequests(actual, expected);

    // PUT
    mockMvcForPut(urlForPut, requestUrlForPut);

    // ??????
    // maintenance_requests ????????????
    compareMaintenanceRequestsTable(resultXml, 6);

    // ??????????????????
    compareContractLog(resultXml);

    // notifications ????????????
    compareNotificationsTable(resultXml, 0);

  }

  /**
   * #6_OC-006,SC-020-021
   * 
   * @author : [VJP] hale
   * @param requestJson
   * @param url
   * @param resultJson
   * @throws Exception
   * @createdAt : 2021-08-19
   * @updatedAt : 2021-08-19
   */
  @Test
  void testOc00606() throws Exception {
    String urlForGet = "/OC00603";
    String requestUrlForGet = "json/request/oc006/req_OC00606_data_get.json";
    String resultJson = "json/response/oc006/res_OC006_06_data.json";

    String urlForPut = "/OC00602";
    String requestUrlForPut = "json/request/oc006/req_OC00606_data_put.json";
    String resultXml = "xml/oc006/result_OC00606.xml";

    // GET
    MvcResult mvcResult = mockMvcForGet(urlForGet, requestUrlForGet);
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

    compareMaintenanceRequests(actual, expected);

    // PUT
    mockMvcForPut(urlForPut, requestUrlForPut);

    // ??????
    // maintenance_requests ????????????
    compareMaintenanceRequestsTable(resultXml, 8);

    // maintenance_requests_customer ????????????
    compareMaintenanceRequestsCustomerTable(resultXml, 1, 0);
    compareMaintenanceRequestsCustomerTable(resultXml, 2, 1);

    // maintenance_requests_customer_corporate ????????????
    compareMaintenanceRequestsCustomerCorporateTable(resultXml, 0, 0);
    compareMaintenanceRequestsCustomerCorporateTable(resultXml, 1, 1);
    compareMaintenanceRequestsCustomerCorporateTable(resultXml, 2, 2);
    compareMaintenanceRequestsCustomerCorporateTable(resultXml, 3, 3);

    // customers ????????????
    compareCustomersTable(resultXml, 3, 0);
    compareCustomersTable(resultXml, 4, 1);

    // customers_corporate ????????????
    compareCustomersCorporateTable(resultXml, 0, 0);
    compareCustomersCorporateTable(resultXml, 1, 1);

    // ??????????????????
    compareContractLog(resultXml);

    // notifications ????????????
    compareNotificationsTable(resultXml, 0);

  }

  /**
   * #7_OC-006,SC-020-021
   * 
   * @author : [VJP] hale
   * @param requestJson
   * @param url
   * @param resultJson
   * @throws Exception
   * @createdAt : 2021-08-20
   * @updatedAt : 2021-08-20
   */
  @Test
  void testOc00607() throws Exception {
    String urlForGet = "/OC00603";
    String requestUrlForGet = "json/request/oc006/req_OC00607_data_get.json";
    String resultJson = "json/response/oc006/res_OC006_07_data.json";

    String urlForPut = "/OC00602";
    String requestUrlForPut = "json/request/oc006/req_OC00607_data_put.json";
    String resultXml = "xml/oc006/result_OC00607.xml";

    // GET
    MvcResult mvcResult = mockMvcForGet(urlForGet, requestUrlForGet);
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

    compareMaintenanceRequests(actual, expected);

    // PUT
    mockMvcForPut(urlForPut, requestUrlForPut);

    // ??????
    // maintenance_requests ????????????
    compareMaintenanceRequestsTable(resultXml, 9);

    // ??????????????????
    compareContractLog(resultXml);

    // notifications ????????????
    compareNotificationsTable(resultXml, 0);

  }

  /**
   * #8_OC-006,SC-020-021
   * 
   * @author : [VJP] hale
   * @param requestJson
   * @param url
   * @param resultJson
   * @throws Exception
   * @createdAt : 2021-08-20
   * @updatedAt : 2021-08-20
   */
  @Test
  void testOc00608() throws Exception {
    String urlForGet = "/OC00603";
    String requestUrlForGet = "json/request/oc006/req_OC00608_data_get.json";
    String resultJson = "json/response/oc006/res_OC006_08_data.json";

    String urlForPut = "/OC00602";
    String requestUrlForPut = "json/request/oc006/req_OC00608_data_put.json";
    String resultXml = "xml/oc006/result_OC00608.xml";

    // GET
    MvcResult mvcResult = mockMvcForGet(urlForGet, requestUrlForGet);
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

    compareMaintenanceRequests(actual, expected);

    // PUT
    mockMvcForPut(urlForPut, requestUrlForPut);

    // ??????
    // maintenance requests ????????????
    compareMaintenanceRequestsTable(resultXml, 10);

    // maintenance_requests_customer ????????????
    compareMaintenanceRequestsCustomerTable(resultXml, 3, 0);
    compareMaintenanceRequestsCustomerTable(resultXml, 4, 1);

    // maintenance_requests_customer_individual ????????????
    compareMaintenanceRequestsCustomerIndividualTable(resultXml, 1, 0);
    compareMaintenanceRequestsCustomerIndividualTable(resultXml, 2, 1);
    compareMaintenanceRequestsCustomerIndividualTable(resultXml, 3, 2);
    compareMaintenanceRequestsCustomerIndividualTable(resultXml, 4, 3);

    // customers_individual ????????????
    compareCustomersIndividualTable(resultXml, 0);

    // customers ????????????
    compareCustomersTable(resultXml, 0, 0);

    // ??????????????????
    compareContractLog(resultXml);

    // notifications ????????????
    compareNotificationsTable(resultXml, 0);

  }

  /**
   * ??????????????????
   * 
   * @author : [VJP] HoangNH
   * @createdAt : 2021-08-11
   * @updatedAt : 2021-08-11
   * @param expected
   * @param actual
   */
  private void compareMaintenanceRequests(MaintenanceRequestsVo actual,
      MaintenanceRequestsVo expected) {
    // ?????????
    assertEquals(expected.getReceivedDate(), actual.getReceivedDate());
    // ????????????
    assertEquals(expected.getContractNo(), actual.getContractNo());
    // ??????????????????
    assertEquals(expected.getContractBranchNo(), actual.getContractBranchNo());
    // ????????????????????????
    assertEquals(expected.getContractNameKnj(), actual.getContractNameKnj());
    // ????????????????????????
    assertEquals(expected.getContractNameKana(), actual.getContractNameKana());
    // ????????????
    assertEquals(expected.getTransactionCode(), actual.getTransactionCode());
    // ??????????????????????????????
    assertEquals(expected.getContractEmail(), actual.getContractEmail());
    // ??????
    assertEquals(expected.getDocumentsList().size(), actual.getDocumentsList().size());
    // ?????????????????????
    // ?????????????????????
    compareInforUser(expected.getTransfer().getContractorCustomer(),
        actual.getTransfer().getContractorCustomer());

    // ??????????????????????????????
    compareInforUser(expected.getTransfer().getInsuredCustomer(),
        actual.getTransfer().getInsuredCustomer());

    // ????????????????????????
    assertEquals(expected.getSalesPlanCode(), actual.getSalesPlanCode());
    // ?????????????????????????????????
    assertEquals(expected.getSalesPlanTypeCode(), actual.getSalesPlanTypeCode());
  }

  /**
   * ?????????????????????
   * 
   * @author : [VJP] HoangNH
   * @createdAt : 2021-08-11
   * @updatedAt : 2021-08-11
   * @param expected
   * @param actual
   */
  private void compareInforUser(MaintenanceRequestsCustomersVo expected,
      MaintenanceRequestsCustomersVo actual) {
    // ??????ID
    assertEquals(expected.getCustomerId(), actual.getCustomerId());
    // ???????????????????????????
    assertEquals(expected.getCorporateIndividualFlag(), actual.getCorporateIndividualFlag());
    // ??????????????????(??????)
    assertEquals(expected.getNameKnj(), actual.getNameKnj());
    // ???????????????
    assertEquals(expected.getNameKnjSei(), actual.getNameKnjSei());
    // ???????????????
    assertEquals(expected.getNameKnjMei(), actual.getNameKnjMei());
    // ??????????????????(??????)
    assertEquals(expected.getNameKana(), actual.getNameKana());
    // ??????????????????
    assertEquals(expected.getNameKanaSei(), actual.getNameKanaSei());
    // ??????????????????
    assertEquals(expected.getNameKanaMei(), actual.getNameKanaMei());
    // ??????????????????
    assertEquals(expected.getSex(), actual.getSex());
    // ????????????????????????
    assertEquals(expected.getDateOfBirth(), actual.getDateOfBirth());
    // ??????????????????(????????????)
    assertEquals(expected.getAddrZipCode(), actual.getAddrZipCode());
    // ??????????????????(????????????)
    assertEquals(expected.getAddrKnjPref(), actual.getAddrKnjPref());
    // ??????????????????1
    assertEquals(expected.getAddrKnj1(), actual.getAddrKnj1());
    // ??????????????????2
    assertEquals(expected.getAddrKnj2(), actual.getAddrKnj2());
    // ????????????????????????1
    assertEquals(expected.getAddrTel1(), actual.getAddrTel1());
    // ????????????????????????2
    assertEquals(expected.getAddrTel2(), actual.getAddrTel2());
    // ?????????????????????????????????
    assertEquals(expected.getEmail(), actual.getEmail());
    // ????????????????????????????????????
    assertEquals(expected.getRelationship(), actual.getRelationship());
    // ???????????????????????????
    assertEquals(expected.getGuardianCustomerId(), actual.getGuardianCustomerId());
    // ?????????????????????(??????)
    assertEquals(expected.getCorpNameOfficial(), actual.getCorpNameOfficial());
    // ?????????????????????(??????)
    assertEquals(expected.getCorpNameKana(), actual.getCorpNameKana());
    // ???????????????????????????(????????????)
    assertEquals(expected.getCorpAddrZipCode(), actual.getCorpAddrZipCode());
    // ???????????????????????????(????????????)
    assertEquals(expected.getCorpAddrKnjPref(), actual.getCorpAddrKnjPref());
    // ???????????????????????????1
    assertEquals(expected.getCorpAddrKnj1(), actual.getCorpAddrKnj1());
    // ???????????????????????????2
    assertEquals(expected.getCorpAddrKnj2(), actual.getCorpAddrKnj2());
    // ??????????????????????????????(??????) ???
    assertEquals(expected.getRep10eNameKnjSei(), actual.getRep10eNameKnjSei());
    // ??????????????????????????????(??????) ???
    assertEquals(expected.getRep10eNameKnjMei(), actual.getRep10eNameKnjMei());
    // ??????????????????????????????(??????) ??????
    assertEquals(expected.getRep10eNameKanaSei(), actual.getRep10eNameKanaSei());
    // ??????????????????????????????(??????) ??????
    assertEquals(expected.getRep10eNameKanaMei(), actual.getRep10eNameKanaMei());
    // ????????????????????????????????????
    assertEquals(expected.getRep10eDateOfBirth(), actual.getRep10eDateOfBirth());
    // ??????????????????????????????
    assertEquals(expected.getRep10eSex(), actual.getRep10eSex());
    // ??????????????????????????????(????????????)
    assertEquals(expected.getRep10eAddrZipCode(), actual.getRep10eAddrZipCode());
    // ??????????????????????????????????????????(????????????)
    assertEquals(expected.getRep10eAddrKnjPref(), actual.getRep10eAddrKnjPref());
    // ??????????????????????????????1
    assertEquals(expected.getRep10eAddrKnj1(), actual.getRep10eAddrKnj1());
    // ??????????????????????????????2
    assertEquals(expected.getRep10eAddrKnj2(), actual.getRep10eAddrKnj2());
    // ????????????????????????????????????1
    assertEquals(expected.getRep10eAddrTel1(), actual.getRep10eAddrTel1());
    // ????????????????????????????????????2
    assertEquals(expected.getRep10eAddrTel2(), actual.getRep10eAddrTel2());
    // ??????????????????????????????(??????) ???
    assertEquals(expected.getContactNameKnjSei(), actual.getContactNameKnjSei());
    // ???????????????????????????(??????) ???
    assertEquals(expected.getContactNameKnjMei(), actual.getContactNameKnjMei());
    // ??????????????????????????????(??????) ??????
    assertEquals(expected.getContactNameKanaSei(), actual.getContactNameKanaSei());
    // ??????????????????????????????(??????) ??????
    assertEquals(expected.getContactNameKanaMei(), actual.getContactNameKanaMei());
    // ??????????????????????????????(????????????)
    assertEquals(expected.getContactAddrZipCode(), actual.getContactAddrZipCode());
    // ??????????????????????????????(????????????)
    assertEquals(expected.getContactAddrKnjPref(), actual.getContactAddrKnjPref());
    // ??????????????????????????????1
    assertEquals(expected.getContactAddrKnj1(), actual.getContactAddrKnj1());
    // ??????????????????????????????2
    assertEquals(expected.getContactAddrKnj2(), actual.getContactAddrKnj2());
    // ????????????????????????????????????1
    assertEquals(expected.getContactAddrTel1(), actual.getContactAddrTel1());
    // ????????????????????????????????????2
    assertEquals(expected.getContactAddrTel2(), actual.getContactAddrTel2());
    // ?????????????????????????????????????????????
    assertEquals(expected.getContactEmail(), actual.getContactEmail());
  }

  /**
   * ?????????????????????XML????????????????????????????????????
   * 
   * @author : [VJP] HoangNH
   * @createdAt : 2021-08-11
   * @updatedAt : 2021-08-11
   * 
   * @param actualTable
   * @param expectedTable
   * @param actualIndex
   * @param expectedIndex
   * @throws DataSetException
   */
  private void compareTable(String resultXml, int expectedIndex, Map<String, Integer> param)
      throws DataSetException, SQLException, IOException {
    // ???????????????????????????
    IDataSet databaseDataSet = this.idatabaseConnection.createDataSet();
    // ????????????
    ITable maintenanceRequestsActualTable = databaseDataSet.getTable("maintenance_requests");
    // ??????????????????
    ITable maintenanceRequestsCustomerActualTable =
        databaseDataSet.getTable("maintenance_requests_customer");
    // ??????????????????
    ITable maintenanceRequestsCustomerCorporateActualTable =
        databaseDataSet.getTable("maintenance_requests_customer_corporate");

    // ?????????????????????
    try (InputStream is = getClass().getClassLoader().getResourceAsStream(resultXml)) {
      IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(is);
      if (param.get("maintenance_requests") != null && param.get("maintenance_requests") >= 0) {
        ITable maintenanceRequestsExpectedTable = expectedDataSet.getTable("maintenance_requests");
        // ?????????????????????Equal??????
        compareTableMaintenanceRequest(maintenanceRequestsActualTable,
            maintenanceRequestsExpectedTable, param.get("maintenance_requests"), 0);
      }

      if (param.get("maintenance_requests_customer") != null
          && param.get("maintenance_requests_customer") >= 0) {
        ITable maintenanceRequestsCustomerRequestsExpectedTable =
            expectedDataSet.getTable("maintenance_requests_customer");
        for (int i = 0; i < maintenanceRequestsCustomerRequestsExpectedTable.getRowCount(); i++) {
          compareTableMaintenanceRequestsCustomer(maintenanceRequestsCustomerActualTable,
              maintenanceRequestsCustomerRequestsExpectedTable,
              param.get("maintenance_requests_customer") + i, i);
        }
      }

      if (param.get("maintenance_requests_customer_corporate") != null
          && param.get("maintenance_requests_customer_corporate") >= 0) {
        ITable maintenanceRequestsCustomerCorporateRequestsExpectedTable =
            expectedDataSet.getTable("maintenance_requests_customer_corporate");
        compareTableMaintenanceRequestsCustomerCorporate(
            maintenanceRequestsCustomerCorporateActualTable,
            maintenanceRequestsCustomerCorporateRequestsExpectedTable,
            param.get("maintenance_requests_customer_corporate"), 0);
      }
    }
  }

  /**
   * ????????????????????????????????????????????????XML????????????????????????????????????
   * 
   * @author : [VJP] HoangNH
   * @createdAt : 2021-08-11
   * @updatedAt : 2021-08-11
   * 
   * @param actualTable
   * @param expectedTable
   * @param actualIndex
   * @param expectedIndex
   * @throws DataSetException
   */
  void compareContractLog(String resultXml) throws Exception {
    // ???????????????????????????
    IDataSet databaseDataSet = this.idatabaseConnection.createDataSet();
    // ????????????
    ITable actualTable = databaseDataSet.getTable("contract_log");

    // ?????????????????????
    try (InputStream is = getClass().getClassLoader().getResourceAsStream(resultXml)) {
      IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(is);
      ITable expectedTable = expectedDataSet.getTable("contract_log");
      int expectedIndex = 0;
      int actualIndex = 0;
      // ????????????ID
      assertEquals(expectedTable.getValue(expectedIndex, "tenant_id"),
          actualTable.getValue(actualIndex, "tenant_id").toString());

      // ??????????????????
      assertEquals(expectedTable.getValue(expectedIndex, "contract_no"),
          actualTable.getValue(actualIndex, "contract_no"));
      // ??????????????????
      assertEquals(expectedTable.getValue(expectedIndex, "contract_branch_no"),
          actualTable.getValue(actualIndex, "contract_branch_no"));
      // ??????
      assertEquals(expectedTable.getValue(expectedIndex, "sequence_no"),
          actualTable.getValue(actualIndex, "sequence_no").toString());
      // ???????????????
      assertEquals(expectedTable.getValue(expectedIndex, "log_type"),
          actualTable.getValue(actualIndex, "log_type"));
      // ??????????????????
      assertEquals(null, actualTable.getValue(actualIndex, "message_code"));
      // ?????????????????????
      assertEquals(null, actualTable.getValue(actualIndex, "message_group"));
      // ???????????????????????????
      assertEquals(expectedTable.getValue(expectedIndex, "reason_group_code"),
          actualTable.getValue(actualIndex, "reason_group_code"));
      // ??????ID
      assertEquals(expectedTable.getValue(expectedIndex, "reason_code"),
          actualTable.getValue(actualIndex, "reason_code"));
      // ???????????????
      assertEquals(expectedTable.getValue(expectedIndex, "contact_transaction_code"),
          actualTable.getValue(actualIndex, "contact_transaction_code"));
      // ??????
      assertEquals(expectedTable.getValue(expectedIndex, "description"),
          actualTable.getValue(actualIndex, "description"));
      // ?????????????????????
      assertEquals(expectedTable.getValue(expectedIndex, "program_name"),
          actualTable.getValue(actualIndex, "program_name"));
      // ????????????
      assertEquals(expectedTable.getValue(expectedIndex, "update_count"),
          actualTable.getValue(actualIndex, "update_count").toString());
    }
  }



  /**
   * ??????????????????????????????????????????????????????XML????????????????????????????????????
   * 
   * @author : [VJP] HoangNH
   * @createdAt : 2021-08-11
   * @updatedAt : 2021-08-11
   * 
   * @param actualTable
   * @param expectedTable
   * @param actualIndex
   * @param expectedIndex
   * @throws DataSetException
   */
  private void compareTableMaintenanceRequestsCustomerCorporate(ITable actualTable,
      ITable expectedTable, int actualIndex, int expectedIndex) throws DataSetException {
    // ????????????ID
    assertEquals(expectedTable.getValue(expectedIndex, "tenant_id"),
        actualTable.getValue(actualIndex, "tenant_id").toString());

    assertEquals(expectedTable.getValue(expectedIndex, "request_no"),
        actualTable.getValue(actualIndex, "request_no"));
    // ??????
    assertEquals(expectedTable.getValue(expectedIndex, "sequence_no"),
        actualTable.getValue(actualIndex, "sequence_no").toString());

    assertEquals(expectedTable.getValue(expectedIndex, "before_after"),
        actualTable.getValue(actualIndex, "before_after"));

    assertEquals(expectedTable.getValue(expectedIndex, "corp_name_kana"),
        actualTable.getValue(actualIndex, "corp_name_kana"));

    assertEquals(expectedTable.getValue(expectedIndex, "corp_name_official"),
        actualTable.getValue(actualIndex, "corp_name_official"));

    assertEquals(expectedTable.getValue(expectedIndex, "corp_addr_zip_code"),
        actualTable.getValue(actualIndex, "corp_addr_zip_code"));

    assertEquals(expectedTable.getValue(expectedIndex, "corp_addr_knj_pref"),
        actualTable.getValue(actualIndex, "corp_addr_knj_pref"));

    assertEquals(expectedTable.getValue(expectedIndex, "corp_addr_knj_1"),
        actualTable.getValue(actualIndex, "corp_addr_knj_1"));

    assertEquals(expectedTable.getValue(expectedIndex, "corp_addr_knj_2"),
        actualTable.getValue(actualIndex, "corp_addr_knj_2"));

    assertEquals(expectedTable.getValue(expectedIndex, "rep10e_sex"),
        actualTable.getValue(actualIndex, "rep10e_sex"));

    assertEquals(expectedTable.getValue(expectedIndex, "rep10e_date_of_birth"),
        actualTable.getValue(actualIndex, "rep10e_date_of_birth").toString());

    assertEquals(expectedTable.getValue(expectedIndex, "rep10e_name_kana_sei"),
        actualTable.getValue(actualIndex, "rep10e_name_kana_sei"));

    assertEquals(expectedTable.getValue(expectedIndex, "rep10e_name_kana_mei"),
        actualTable.getValue(actualIndex, "rep10e_name_kana_mei"));

    assertEquals(expectedTable.getValue(expectedIndex, "rep10e_name_knj_sei"),
        actualTable.getValue(actualIndex, "rep10e_name_knj_sei"));

    assertEquals(expectedTable.getValue(expectedIndex, "rep10e_name_knj_mei"),
        actualTable.getValue(actualIndex, "rep10e_name_knj_mei"));

    assertEquals(expectedTable.getValue(expectedIndex, "rep10e_addr_zip_code"),
        actualTable.getValue(actualIndex, "rep10e_addr_zip_code"));

    assertEquals(expectedTable.getValue(expectedIndex, "rep10e_addr_knj_pref"),
        actualTable.getValue(actualIndex, "rep10e_addr_knj_pref"));

    assertEquals(expectedTable.getValue(expectedIndex, "rep10e_addr_knj_1"),
        actualTable.getValue(actualIndex, "rep10e_addr_knj_1"));

    assertEquals(expectedTable.getValue(expectedIndex, "rep10e_addr_knj_2"),
        actualTable.getValue(actualIndex, "rep10e_addr_knj_2"));

    assertEquals(expectedTable.getValue(expectedIndex, "rep10e_addr_tel1"),
        actualTable.getValue(actualIndex, "rep10e_addr_tel1"));

    assertEquals(expectedTable.getValue(expectedIndex, "rep10e_addr_tel2"),
        actualTable.getValue(actualIndex, "rep10e_addr_tel2"));

    assertEquals(expectedTable.getValue(expectedIndex, "contact_name_kana_sei"),
        actualTable.getValue(actualIndex, "contact_name_kana_sei"));

    assertEquals(expectedTable.getValue(expectedIndex, "contact_name_kana_mei"),
        actualTable.getValue(actualIndex, "contact_name_kana_mei"));

    assertEquals(expectedTable.getValue(expectedIndex, "contact_name_knj_sei"),
        actualTable.getValue(actualIndex, "contact_name_knj_sei"));

    assertEquals(expectedTable.getValue(expectedIndex, "contact_name_knj_mei"),
        actualTable.getValue(actualIndex, "contact_name_knj_mei"));

    assertEquals(expectedTable.getValue(expectedIndex, "contact_addr_knj_pref"),
        actualTable.getValue(actualIndex, "contact_addr_knj_pref"));

    assertEquals(expectedTable.getValue(expectedIndex, "contact_addr_knj_1"),
        actualTable.getValue(actualIndex, "contact_addr_knj_1"));

    assertEquals(expectedTable.getValue(expectedIndex, "contact_addr_knj_2"),
        actualTable.getValue(actualIndex, "contact_addr_knj_2"));

    assertEquals(expectedTable.getValue(expectedIndex, "contact_addr_tel1"),
        actualTable.getValue(actualIndex, "contact_addr_tel1"));

    assertEquals(expectedTable.getValue(expectedIndex, "contact_addr_tel2"),
        actualTable.getValue(actualIndex, "contact_addr_tel2"));

    assertEquals(expectedTable.getValue(expectedIndex, "contact_email"),
        actualTable.getValue(actualIndex, "contact_email"));

    assertEquals(expectedTable.getValue(expectedIndex, "update_count"),
        actualTable.getValue(actualIndex, "update_count").toString());

  }

  /**
   * ??????????????????????????????????????????????????????XML????????????????????????????????????
   * 
   * @author : [VJP] HoangNH
   * @createdAt : 2021-08-11
   * @updatedAt : 2021-08-11
   * 
   * @param actualTable
   * @param expectedTable
   * @param actualIndex
   * @param expectedIndex
   * @throws DataSetException
   */
  private void compareTableMaintenanceRequestsCustomer(ITable actualTable, ITable expectedTable,
      int actualIndex, int expectedIndex) throws DataSetException {
    // ????????????ID
    assertEquals(expectedTable.getValue(expectedIndex, "tenant_id"),
        actualTable.getValue(actualIndex, "tenant_id").toString());

    assertEquals(expectedTable.getValue(expectedIndex, "request_no"),
        actualTable.getValue(actualIndex, "request_no"));
    // ??????
    assertEquals(expectedTable.getValue(expectedIndex, "sequence_no"),
        actualTable.getValue(actualIndex, "sequence_no").toString());

    assertEquals(expectedTable.getValue(expectedIndex, "before_after"),
        actualTable.getValue(actualIndex, "before_after"));

    assertEquals(expectedTable.getValue(expectedIndex, "corporate_individual_flag"),
        actualTable.getValue(actualIndex, "corporate_individual_flag"));

    assertEquals(expectedTable.getValue(expectedIndex, "role"),
        actualTable.getValue(actualIndex, "role"));

  }

  /**
   * ????????????????????????????????????????????????XML????????????????????????????????????
   * 
   * @author : [VJP] HoangNH
   * @createdAt : 2021-08-11
   * @updatedAt : 2021-08-11
   * 
   * @param actualTable
   * @param expectedTable
   * @param actualIndex
   * @param expectedIndex
   * @throws DataSetException
   */
  private void compareTableMaintenanceRequest(ITable actualTable, ITable expectedTable,
      int actualIndex, int expectedIndex) throws DataSetException {
    // ????????????ID
    assertEquals(expectedTable.getValue(expectedIndex, "tenant_id"),
        actualTable.getValue(actualIndex, "tenant_id").toString());
    // ??????????????????
    assertEquals(expectedTable.getValue(expectedIndex, "request_no"),
        actualTable.getValue(actualIndex, "request_no"));
    // ????????????
    assertEquals(expectedTable.getValue(expectedIndex, "contract_no"),
        actualTable.getValue(actualIndex, "contract_no"));
    // ??????????????????
    assertEquals(expectedTable.getValue(expectedIndex, "contract_branch_no"),
        actualTable.getValue(actualIndex, "contract_branch_no"));
    // ??????/???????????????
    assertEquals(expectedTable.getValue(expectedIndex, "active_inactive"),
        actualTable.getValue(actualIndex, "active_inactive"));
    // ??????????????????
    assertEquals(expectedTable.getValue(expectedIndex, "transaction_code"),
        actualTable.getValue(actualIndex, "transaction_code"));
    // ???????????????????????????
    assertEquals(expectedTable.getValue(expectedIndex, "request_status"),
        actualTable.getValue(actualIndex, "request_status"));
    // ?????????
    assertEquals(expectedTable.getValue(expectedIndex, "application_date"),
        actualTable.getValue(actualIndex, "application_date").toString());
    // ????????????
    assertEquals(null, actualTable.getValue(actualIndex, "application_time"));
    // ????????????
    assertEquals(expectedTable.getValue(expectedIndex, "application_method"),
        actualTable.getValue(actualIndex, "application_method"));
    // ?????????
    assertEquals(expectedTable.getValue(expectedIndex, "received_date"),
        actualTable.getValue(actualIndex, "received_date").toString());
    // ????????????
    assertEquals(null, actualTable.getValue(actualIndex, "received_at"));
    // ????????????????????????
    assertEquals(expectedTable.getValue(expectedIndex, "comment_underweiter1"),
        actualTable.getValue(actualIndex, "comment_underweiter1"));
    // ??????????????????
    assertEquals(expectedTable.getValue(expectedIndex, "first_assessment_results"),
        actualTable.getValue(actualIndex, "first_assessment_results"));
    // ????????????????????????
    assertEquals(null, actualTable.getValue(actualIndex, "comment_underweiter2"));
    // ??????????????????
    assertEquals(null, actualTable.getValue(actualIndex, "second_assessment_results"));
    // ?????????
    if (expectedTable.getValue(expectedIndex, "communication_column") != null) {
      assertEquals(expectedTable.getValue(expectedIndex, "communication_column"),
          actualTable.getValue(actualIndex, "communication_column"));
    }

    // ?????????
    assertEquals(expectedTable.getValue(expectedIndex, "apply_date"),
        actualTable.getValue(actualIndex, "apply_date").toString());
    // ??????????????????
    assertEquals(expectedTable.getValue(expectedIndex, "entry_type"),
        actualTable.getValue(actualIndex, "entry_type"));
    // ?????????????????????
    assertEquals(null, actualTable.getValue(actualIndex, "payment_method_code"));
    // ???????????????????????????
    assertEquals(null, actualTable.getValue(actualIndex, "factoring_company_code"));

    // ???????????????
    assertEquals(null, actualTable.getValue(actualIndex, "bank_code"));
    // ???????????????
    assertEquals(null, actualTable.getValue(actualIndex, "bank_branch_code"));
    // ????????????
    assertEquals(null, actualTable.getValue(actualIndex, "bank_account_type"));
    // ????????????
    assertEquals(null, actualTable.getValue(actualIndex, "bank_account_no"));
    // ???????????????
    assertEquals(null, actualTable.getValue(actualIndex, "bank_account_name"));
    // ???????????????(????????????)
    assertEquals(null, actualTable.getValue(actualIndex, "token_no"));
    // ??????????????????????????????
    assertEquals(null, actualTable.getValue(actualIndex, "email_for_notification"));
    // ???????????????
    assertEquals(null, actualTable.getValue(actualIndex, "termination_base_date"));
    // ????????????
    assertEquals(null, actualTable.getValue(actualIndex, "termination_title"));
    // ????????????
    assertEquals(expectedTable.getValue(expectedIndex, "update_count"),
        actualTable.getValue(actualIndex, "update_count").toString());
  }

  /**
   * ?????????????????????????????????(????????????)
   * 
   * @author : [VJP] hale
   * @param requestJson
   * @param url
   * @param resultJson
   * @throws Exception
   * @createdAt : 2021-08-20
   * @updatedAt : 2021-08-20
   */
  private void compareMaintenanceRequestsTable(String resultXml, int actualIndex) throws Exception {
    // ????????????????????????
    int expectedIndex = 0;
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
          actualTable.getValue(actualIndex, "request_no"));
      // ????????????
      assertEquals(expectedTable.getValue(expectedIndex, "contract_no"),
          actualTable.getValue(actualIndex, "contract_no"));
      // ??????????????????
      assertEquals(expectedTable.getValue(expectedIndex, "contract_branch_no"),
          actualTable.getValue(actualIndex, "contract_branch_no"));
      // ??????/???????????????
      assertEquals(expectedTable.getValue(expectedIndex, "active_inactive"),
          actualTable.getValue(actualIndex, "active_inactive"));
      // ??????????????????
      assertEquals(expectedTable.getValue(expectedIndex, "transaction_code"),
          actualTable.getValue(actualIndex, "transaction_code"));
      // ???????????????????????????
      assertEquals(expectedTable.getValue(expectedIndex, "request_status"),
          actualTable.getValue(actualIndex, "request_status"));
      // ?????????
      assertEquals(expectedTable.getValue(expectedIndex, "application_date"),
          actualTable.getValue(actualIndex, "application_date").toString());
      // ????????????
      assertEquals(expectedTable.getValue(expectedIndex, "application_method"),
          actualTable.getValue(actualIndex, "application_method"));
      // ?????????
      assertEquals(expectedTable.getValue(expectedIndex, "received_date"),
          actualTable.getValue(actualIndex, "received_date").toString());
      // ????????????????????????
      assertEquals(expectedTable.getValue(expectedIndex, "comment_underweiter1"),
          actualTable.getValue(actualIndex, "comment_underweiter1"));
      // ??????????????????
      assertEquals(expectedTable.getValue(expectedIndex, "first_assessment_results"),
          actualTable.getValue(actualIndex, "first_assessment_results"));
      // ????????????????????????
      assertEquals(expectedTable.getValue(expectedIndex, "comment_underweiter2"),
          actualTable.getValue(actualIndex, "comment_underweiter2"));
      // ??????????????????
      assertEquals(expectedTable.getValue(expectedIndex, "second_assessment_results"),
          actualTable.getValue(actualIndex, "second_assessment_results"));
      // ?????????
      assertEquals(expectedTable.getValue(expectedIndex, "apply_date"),
          actualTable.getValue(actualIndex, "apply_date").toString());
    }
  }

  /**
   * ?????????????????????????????????(????????????)
   * 
   * @author : [VJP] hale
   * @param requestJson
   * @param url
   * @param resultJson
   * @throws Exception
   * @createdAt : 2021-08-20
   * @updatedAt : 2021-08-20
   */
  private void compareNotificationsTable(String resultXml, int actualIndex) throws Exception {
    // ????????????????????????
    int expectedIndex = 0;
    // ???????????????????????????
    IDataSet databaseDataSet = this.idatabaseConnection.createDataSet();
    // ????????????
    ITable actualTable = databaseDataSet.getTable("notifications");
    // ?????????????????????
    try (InputStream is = getClass().getClassLoader().getResourceAsStream(resultXml)) {
      IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(is);
      // ?????????????????????????????????
      ITable expectedTable = expectedDataSet.getTable("notifications");
      // ?????????????????????Equal??????
      assertEquals(expectedTable.getValue(expectedIndex, "notification_date").toString(),
          actualTable.getValue(actualIndex, "notification_date").toString());
      assertEquals(expectedTable.getValue(expectedIndex, "contract_no"),
          actualTable.getValue(actualIndex, "contract_no"));
      assertEquals(expectedTable.getValue(expectedIndex, "contract_branch_no"),
          actualTable.getValue(actualIndex, "contract_branch_no"));
      assertEquals(expectedTable.getValue(expectedIndex, "template_number"),
          actualTable.getValue(actualIndex, "template_number"));
      assertEquals(expectedTable.getValue(expectedIndex, "notification_implementation"),
          actualTable.getValue(actualIndex, "notification_implementation"));
      assertEquals(expectedTable.getValue(expectedIndex, "sendee"),
          actualTable.getValue(actualIndex, "sendee"));
      assertEquals(expectedTable.getValue(expectedIndex, "notification_method"),
          actualTable.getValue(actualIndex, "notification_method"));
      assertEquals(expectedTable.getValue(expectedIndex, "email"),
          actualTable.getValue(actualIndex, "email"));
      assertEquals(expectedTable.getValue(expectedIndex, "update_count").toString(),
          actualTable.getValue(actualIndex, "update_count").toString());
    }
  }

  /**
   * ?????????????????????????????????(????????????)
   * 
   * @author : [VJP] hale
   * @param requestJson
   * @param url
   * @param resultJson
   * @throws Exception
   * @createdAt : 2021-08-20
   * @updatedAt : 2021-08-20
   */
  private void compareMaintenanceRequestsCustomerTable(String resultXml, int actualIndex,
      int expectedIndex) throws Exception {
    // ???????????????????????????
    IDataSet databaseDataSet = this.idatabaseConnection.createDataSet();
    // ????????????
    ITable actualTable = databaseDataSet.getTable("maintenance_requests_customer");
    // ?????????????????????
    try (InputStream is = getClass().getClassLoader().getResourceAsStream(resultXml)) {
      IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(is);
      // ?????????????????????????????????
      ITable expectedTable = expectedDataSet.getTable("maintenance_requests_customer");
      // ?????????????????????Equal??????
      // ????????????ID
      assertEquals(expectedTable.getValue(expectedIndex, "tenant_id"),
          actualTable.getValue(actualIndex, "tenant_id").toString());

      assertEquals(expectedTable.getValue(expectedIndex, "request_no"),
          actualTable.getValue(actualIndex, "request_no"));
      // ??????
      assertEquals(expectedTable.getValue(expectedIndex, "sequence_no"),
          actualTable.getValue(actualIndex, "sequence_no").toString());

      assertEquals(expectedTable.getValue(expectedIndex, "before_after"),
          actualTable.getValue(actualIndex, "before_after"));

      assertEquals(expectedTable.getValue(expectedIndex, "corporate_individual_flag"),
          actualTable.getValue(actualIndex, "corporate_individual_flag"));

      assertEquals(expectedTable.getValue(expectedIndex, "role"),
          actualTable.getValue(actualIndex, "role"));
    }
  }

  /**
   * ?????????????????????????????????(????????????)
   * 
   * @author : [VJP] hale
   * @param requestJson
   * @param url
   * @param resultJson
   * @throws Exception
   * @createdAt : 2021-08-20
   * @updatedAt : 2021-08-20
   */
  private void compareMaintenanceRequestsCustomerCorporateTable(String resultXml, int actualIndex,
      int expectedIndex) throws Exception {
    // ???????????????????????????
    IDataSet databaseDataSet = this.idatabaseConnection.createDataSet();
    // ????????????
    ITable actualTable = databaseDataSet.getTable("maintenance_requests_customer_corporate");
    // ?????????????????????
    try (InputStream is = getClass().getClassLoader().getResourceAsStream(resultXml)) {
      IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(is);
      // ?????????????????????????????????
      ITable expectedTable = expectedDataSet.getTable("maintenance_requests_customer_corporate");
      // ?????????????????????Equal??????
      // ????????????ID
      assertEquals(expectedTable.getValue(expectedIndex, "tenant_id"),
          actualTable.getValue(actualIndex, "tenant_id").toString());

      assertEquals(expectedTable.getValue(expectedIndex, "request_no"),
          actualTable.getValue(actualIndex, "request_no"));
      // ??????
      assertEquals(expectedTable.getValue(expectedIndex, "sequence_no"),
          actualTable.getValue(actualIndex, "sequence_no").toString());

      assertEquals(expectedTable.getValue(expectedIndex, "before_after"),
          actualTable.getValue(actualIndex, "before_after"));

      assertEquals(expectedTable.getValue(expectedIndex, "customer_id"),
          actualTable.getValue(actualIndex, "customer_id"));

      assertEquals(expectedTable.getValue(expectedIndex, "corp_name_kana"),
          actualTable.getValue(actualIndex, "corp_name_kana"));

      assertEquals(expectedTable.getValue(expectedIndex, "corp_name_official"),
          actualTable.getValue(actualIndex, "corp_name_official"));

      assertEquals(expectedTable.getValue(expectedIndex, "corp_addr_zip_code"),
          actualTable.getValue(actualIndex, "corp_addr_zip_code"));

      assertEquals(expectedTable.getValue(expectedIndex, "corp_addr_knj_pref"),
          actualTable.getValue(actualIndex, "corp_addr_knj_pref"));

      assertEquals(expectedTable.getValue(expectedIndex, "corp_addr_knj_1"),
          actualTable.getValue(actualIndex, "corp_addr_knj_1"));

      assertEquals(expectedTable.getValue(expectedIndex, "corp_addr_knj_2"),
          actualTable.getValue(actualIndex, "corp_addr_knj_2"));

      assertEquals(expectedTable.getValue(expectedIndex, "rep10e_sex"),
          actualTable.getValue(actualIndex, "rep10e_sex"));

      assertEquals(expectedTable.getValue(expectedIndex, "rep10e_date_of_birth").toString(),
          actualTable.getValue(actualIndex, "rep10e_date_of_birth").toString());

      assertEquals(expectedTable.getValue(expectedIndex, "rep10e_name_kana_sei"),
          actualTable.getValue(actualIndex, "rep10e_name_kana_sei"));

      assertEquals(expectedTable.getValue(expectedIndex, "rep10e_name_kana_mei"),
          actualTable.getValue(actualIndex, "rep10e_name_kana_mei"));

      assertEquals(expectedTable.getValue(expectedIndex, "rep10e_name_knj_sei"),
          actualTable.getValue(actualIndex, "rep10e_name_knj_sei"));

      assertEquals(expectedTable.getValue(expectedIndex, "rep10e_name_knj_mei"),
          actualTable.getValue(actualIndex, "rep10e_name_knj_mei"));

      assertEquals(expectedTable.getValue(expectedIndex, "rep10e_addr_zip_code"),
          actualTable.getValue(actualIndex, "rep10e_addr_zip_code"));

      assertEquals(expectedTable.getValue(expectedIndex, "rep10e_addr_knj_pref"),
          actualTable.getValue(actualIndex, "rep10e_addr_knj_pref"));

      assertEquals(expectedTable.getValue(expectedIndex, "rep10e_addr_knj_1"),
          actualTable.getValue(actualIndex, "rep10e_addr_knj_1"));

      assertEquals(expectedTable.getValue(expectedIndex, "rep10e_addr_knj_2"),
          actualTable.getValue(actualIndex, "rep10e_addr_knj_2"));

      assertEquals(expectedTable.getValue(expectedIndex, "rep10e_addr_tel1"),
          actualTable.getValue(actualIndex, "rep10e_addr_tel1"));

      if (expectedTable.getValue(expectedIndex, "rep10e_addr_tel2") == "") {
        assertEquals(null, actualTable.getValue(actualIndex, "rep10e_addr_tel2"));
      } else {
        assertEquals(expectedTable.getValue(expectedIndex, "rep10e_addr_tel2"),
            actualTable.getValue(actualIndex, "rep10e_addr_tel2"));
      }

      assertEquals(expectedTable.getValue(expectedIndex, "contact_name_kana_sei"),
          actualTable.getValue(actualIndex, "contact_name_kana_sei"));

      assertEquals(expectedTable.getValue(expectedIndex, "contact_name_kana_mei"),
          actualTable.getValue(actualIndex, "contact_name_kana_mei"));

      assertEquals(expectedTable.getValue(expectedIndex, "contact_name_knj_sei"),
          actualTable.getValue(actualIndex, "contact_name_knj_sei"));

      assertEquals(expectedTable.getValue(expectedIndex, "contact_name_knj_mei"),
          actualTable.getValue(actualIndex, "contact_name_knj_mei"));

      assertEquals(expectedTable.getValue(expectedIndex, "contact_addr_knj_pref"),
          actualTable.getValue(actualIndex, "contact_addr_knj_pref"));

      assertEquals(expectedTable.getValue(expectedIndex, "contact_addr_knj_1"),
          actualTable.getValue(actualIndex, "contact_addr_knj_1"));
      // TODO HOANGNH check validtion errro
      assertEquals(expectedTable.getValue(expectedIndex, "contact_addr_knj_2"),
          actualTable.getValue(actualIndex, "contact_addr_knj_2"));

      assertEquals(expectedTable.getValue(expectedIndex, "contact_addr_tel1"),
          actualTable.getValue(actualIndex, "contact_addr_tel1"));

      assertEquals(expectedTable.getValue(expectedIndex, "contact_addr_tel2"),
          actualTable.getValue(actualIndex, "contact_addr_tel2"));

      assertEquals(expectedTable.getValue(expectedIndex, "contact_email"),
          actualTable.getValue(actualIndex, "contact_email"));

      assertEquals(expectedTable.getValue(expectedIndex, "update_count").toString(),
          actualTable.getValue(actualIndex, "update_count").toString());
    }
  }

  /**
   * ?????????????????????????????????(????????????)
   * 
   * @author : [VJP] hale
   * @param requestJson
   * @param url
   * @param resultJson
   * @throws Exception
   * @createdAt : 2021-08-20
   * @updatedAt : 2021-08-20
   */
  private void compareCustomersTable(String resultXml, int actualIndex, int expectedIndex)
      throws Exception {
    // ???????????????????????????
    IDataSet databaseDataSet = this.idatabaseConnection.createDataSet();
    // ????????????
    ITable actualTable = databaseDataSet.getTable("customers");
    // ?????????????????????
    try (InputStream is = getClass().getClassLoader().getResourceAsStream(resultXml)) {
      IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(is);
      // ?????????????????????????????????
      ITable expectedTable = expectedDataSet.getTable("customers");
      // ?????????????????????Equal??????
      assertEquals(expectedTable.getValue(expectedIndex, "tenant_id"),
          actualTable.getValue(actualIndex, "tenant_id").toString());
      assertEquals(expectedTable.getValue(expectedIndex, "customer_id"),
          actualTable.getValue(actualIndex, "customer_id"));
      assertEquals(expectedTable.getValue(expectedIndex, "corporate_individual_flag"),
          actualTable.getValue(actualIndex, "corporate_individual_flag"));
    }
  }

  /**
   * ?????????????????????????????????(????????????)
   * 
   * @author : [VJP] hale
   * @param requestJson
   * @param url
   * @param resultJson
   * @throws Exception
   * @createdAt : 2021-08-20
   * @updatedAt : 2021-08-20
   */
  private void compareCustomersCorporateTable(String resultXml, int actualIndex, int expectedIndex)
      throws Exception {
    // ???????????????????????????
    IDataSet databaseDataSet = this.idatabaseConnection.createDataSet();
    // ????????????
    ITable actualTable = databaseDataSet.getTable("customers_corporate");
    // ?????????????????????
    try (InputStream is = getClass().getClassLoader().getResourceAsStream(resultXml)) {
      IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(is);
      // ?????????????????????????????????
      ITable expectedTable = expectedDataSet.getTable("customers_corporate");
      // ?????????????????????Equal??????
      assertEquals(expectedTable.getValue(expectedIndex, "tenant_id"),
          actualTable.getValue(actualIndex, "tenant_id").toString());
      assertEquals(expectedTable.getValue(expectedIndex, "customer_id"),
          actualTable.getValue(actualIndex, "customer_id"));
      assertEquals(expectedTable.getValue(expectedIndex, "corp_name_kana"),
          actualTable.getValue(actualIndex, "corp_name_kana"));
      assertEquals(expectedTable.getValue(expectedIndex, "corp_name_official"),
          actualTable.getValue(actualIndex, "corp_name_official").toString());
      assertEquals(expectedTable.getValue(expectedIndex, "corp_addr_zip_code"),
          actualTable.getValue(actualIndex, "corp_addr_zip_code"));
      assertEquals(expectedTable.getValue(expectedIndex, "corp_addr_knj_pref"),
          actualTable.getValue(actualIndex, "corp_addr_knj_pref"));
      assertEquals(expectedTable.getValue(expectedIndex, "corp_addr_knj_1"),
          actualTable.getValue(actualIndex, "corp_addr_knj_1"));
      assertEquals(expectedTable.getValue(expectedIndex, "corp_addr_knj_2"),
          actualTable.getValue(actualIndex, "corp_addr_knj_2"));
      assertEquals(expectedTable.getValue(expectedIndex, "rep10e_sex"),
          actualTable.getValue(actualIndex, "rep10e_sex").toString());
      assertEquals(expectedTable.getValue(expectedIndex, "rep10e_date_of_birth"),
          actualTable.getValue(actualIndex, "rep10e_date_of_birth").toString());
      assertEquals(expectedTable.getValue(expectedIndex, "rep10e_name_kana_sei"),
          actualTable.getValue(actualIndex, "rep10e_name_kana_sei"));
      assertEquals(expectedTable.getValue(expectedIndex, "rep10e_name_kana_mei"),
          actualTable.getValue(actualIndex, "rep10e_name_kana_mei"));
      assertEquals(expectedTable.getValue(expectedIndex, "rep10e_name_knj_sei"),
          actualTable.getValue(actualIndex, "rep10e_name_knj_sei"));
      assertEquals(expectedTable.getValue(expectedIndex, "rep10e_name_knj_mei"),
          actualTable.getValue(actualIndex, "rep10e_name_knj_mei").toString());
      assertEquals(expectedTable.getValue(expectedIndex, "rep10e_addr_zip_code"),
          actualTable.getValue(actualIndex, "rep10e_addr_zip_code"));
      assertEquals(expectedTable.getValue(expectedIndex, "rep10e_addr_knj_pref"),
          actualTable.getValue(actualIndex, "rep10e_addr_knj_pref"));
      assertEquals(expectedTable.getValue(expectedIndex, "rep10e_addr_knj_1"),
          actualTable.getValue(actualIndex, "rep10e_addr_knj_1"));
      assertEquals(expectedTable.getValue(expectedIndex, "rep10e_addr_knj_2"),
          actualTable.getValue(actualIndex, "rep10e_addr_knj_2"));
      assertEquals(expectedTable.getValue(expectedIndex, "rep10e_addr_tel1"),
          actualTable.getValue(actualIndex, "rep10e_addr_tel1"));
      assertEquals(expectedTable.getValue(expectedIndex, "contact_name_kana_sei"),
          actualTable.getValue(actualIndex, "contact_name_kana_sei"));
      assertEquals(expectedTable.getValue(expectedIndex, "contact_name_kana_mei"),
          actualTable.getValue(actualIndex, "contact_name_kana_mei"));
      assertEquals(expectedTable.getValue(expectedIndex, "contact_name_knj_sei"),
          actualTable.getValue(actualIndex, "contact_name_knj_sei"));
      assertEquals(expectedTable.getValue(expectedIndex, "contact_name_knj_mei"),
          actualTable.getValue(actualIndex, "contact_name_knj_mei"));
      assertEquals(expectedTable.getValue(expectedIndex, "contact_addr_zip_code"),
          actualTable.getValue(actualIndex, "contact_addr_zip_code"));
      assertEquals(expectedTable.getValue(expectedIndex, "contact_addr_knj_pref"),
          actualTable.getValue(actualIndex, "contact_addr_knj_pref"));
      assertEquals(expectedTable.getValue(expectedIndex, "contact_addr_knj_1"),
          actualTable.getValue(actualIndex, "contact_addr_knj_1"));
      assertEquals(expectedTable.getValue(expectedIndex, "contact_addr_knj_2"),
          actualTable.getValue(actualIndex, "contact_addr_knj_2"));
      assertEquals(expectedTable.getValue(expectedIndex, "contact_addr_tel1"),
          actualTable.getValue(actualIndex, "contact_addr_tel1"));
      assertEquals(expectedTable.getValue(expectedIndex, "contact_addr_tel2"),
          actualTable.getValue(actualIndex, "contact_addr_tel2"));
      assertEquals(expectedTable.getValue(expectedIndex, "contact_email"),
          actualTable.getValue(actualIndex, "contact_email"));
    }
  }

  /**
   * ?????????????????????????????????(????????????)
   * 
   * @author : [VJP] hale
   * @param requestJson
   * @param url
   * @param resultJson
   * @throws Exception
   * @createdAt : 2021-08-20
   * @updatedAt : 2021-08-20
   */
  private void compareMaintenanceRequestsCustomerIndividualTable(String resultXml, int actualIndex,
      int expectedIndex) throws Exception {
    // ???????????????????????????
    IDataSet databaseDataSet = this.idatabaseConnection.createDataSet();
    // ????????????
    ITable actualTable = databaseDataSet.getTable("maintenance_requests_customer_individual");
    // ?????????????????????
    try (InputStream is = getClass().getClassLoader().getResourceAsStream(resultXml)) {
      IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(is);
      // ?????????????????????????????????
      ITable expectedTable = expectedDataSet.getTable("maintenance_requests_customer_individual");
      // ?????????????????????Equal??????
      assertEquals(expectedTable.getValue(expectedIndex, "tenant_id"),
          actualTable.getValue(actualIndex, "tenant_id").toString());
      assertEquals(expectedTable.getValue(expectedIndex, "request_no"),
          actualTable.getValue(actualIndex, "request_no"));
      assertEquals(expectedTable.getValue(expectedIndex, "sequence_no"),
          actualTable.getValue(actualIndex, "sequence_no").toString());
      assertEquals(expectedTable.getValue(expectedIndex, "before_after"),
          actualTable.getValue(actualIndex, "before_after"));
      assertEquals(expectedTable.getValue(expectedIndex, "customer_id"),
          actualTable.getValue(actualIndex, "customer_id"));
      assertEquals(expectedTable.getValue(expectedIndex, "name_kana_sei"),
          actualTable.getValue(actualIndex, "name_kana_sei"));
      assertEquals(expectedTable.getValue(expectedIndex, "name_kana_mei"),
          actualTable.getValue(actualIndex, "name_kana_mei"));
      assertEquals(expectedTable.getValue(expectedIndex, "name_knj_sei"),
          actualTable.getValue(actualIndex, "name_knj_sei"));
      assertEquals(expectedTable.getValue(expectedIndex, "name_knj_mei"),
          actualTable.getValue(actualIndex, "name_knj_mei"));
      assertEquals(expectedTable.getValue(expectedIndex, "sex"),
          actualTable.getValue(actualIndex, "sex"));
      assertEquals(expectedTable.getValue(expectedIndex, "date_of_birth"),
          actualTable.getValue(actualIndex, "date_of_birth").toString());
      assertEquals(expectedTable.getValue(expectedIndex, "addr_zip_code"),
          actualTable.getValue(actualIndex, "addr_zip_code"));
      assertEquals(expectedTable.getValue(expectedIndex, "addr_knj_pref"),
          actualTable.getValue(actualIndex, "addr_knj_pref"));
      assertEquals(expectedTable.getValue(expectedIndex, "addr_knj_1"),
          actualTable.getValue(actualIndex, "addr_knj_1"));
      assertEquals(expectedTable.getValue(expectedIndex, "addr_knj_2"),
          actualTable.getValue(actualIndex, "addr_knj_2"));
      assertEquals(expectedTable.getValue(expectedIndex, "addr_tel1"),
          actualTable.getValue(actualIndex, "addr_tel1"));
      assertEquals(expectedTable.getValue(expectedIndex, "email"),
          actualTable.getValue(actualIndex, "email"));
    }
  }

  /**
   * 
   * @param resultXml
   * @param params
   * @throws Exception
   */
  private void compareCustomersIndividualTable2(String resultXml, Integer[] params)
      throws Exception {
    // ???????????????????????????
    IDataSet databaseDataSet = this.idatabaseConnection.createDataSet();
    // ????????????
    ITable actualTable = databaseDataSet.getTable("maintenance_requests_customer_individual");
    // ?????????????????????
    try (InputStream is = getClass().getClassLoader().getResourceAsStream(resultXml)) {
      IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(is);
      // ?????????????????????????????????
      ITable expectedTable = expectedDataSet.getTable("maintenance_requests_customer_individual");
      // ?????????????????????Equal??????
      for (int i = 0; i < params.length; i++) {
        assertEquals(expectedTable.getValue(i, "tenant_id").toString(),
            actualTable.getValue(params[i], "tenant_id").toString());
        assertEquals(expectedTable.getValue(i, "name_kana_sei"),
            actualTable.getValue(params[i], "name_kana_sei"));
        assertEquals(expectedTable.getValue(i, "name_kana_mei"),
            actualTable.getValue(params[i], "name_kana_mei"));
        assertEquals(expectedTable.getValue(i, "name_knj_sei"),
            actualTable.getValue(params[i], "name_knj_sei"));
        assertEquals(expectedTable.getValue(i, "name_knj_mei"),
            actualTable.getValue(params[i], "name_knj_mei"));
        assertEquals(expectedTable.getValue(i, "sex"), actualTable.getValue(params[i], "sex"));
        assertEquals(expectedTable.getValue(i, "date_of_birth"),
            actualTable.getValue(params[i], "date_of_birth").toString());
        assertEquals(expectedTable.getValue(i, "addr_zip_code"),
            actualTable.getValue(params[i], "addr_zip_code"));
        assertEquals(expectedTable.getValue(i, "addr_knj_pref"),
            actualTable.getValue(params[i], "addr_knj_pref"));
        assertEquals(expectedTable.getValue(i, "addr_knj_1"),
            actualTable.getValue(params[i], "addr_knj_1"));
        assertEquals(expectedTable.getValue(i, "addr_knj_2"),
            actualTable.getValue(params[i], "addr_knj_2"));
        assertEquals(expectedTable.getValue(i, "addr_tel1"),
            actualTable.getValue(params[i], "addr_tel1"));
        assertEquals(expectedTable.getValue(i, "email"), actualTable.getValue(params[i], "email"));
      }
    }
  }

  /**
   * ?????????????????????????????????(????????????)
   * 
   * @author : [VJP] hale
   * @param requestJson
   * @param url
   * @param resultJson
   * @throws Exception
   * @createdAt : 2021-08-20
   * @updatedAt : 2021-08-20
   */
  private void compareCustomersIndividualTable(String resultXml, int actualIndex) throws Exception {
    // ????????????????????????
    int expectedIndex = 0;
    // ???????????????????????????
    IDataSet databaseDataSet = this.idatabaseConnection.createDataSet();
    // ????????????
    ITable actualTable = databaseDataSet.getTable("customers_individual");
    // ?????????????????????
    try (InputStream is = getClass().getClassLoader().getResourceAsStream(resultXml)) {
      IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(is);
      // ?????????????????????????????????
      ITable expectedTable = expectedDataSet.getTable("customers_individual");
      // ?????????????????????Equal??????
      assertEquals(expectedTable.getValue(expectedIndex, "tenant_id"),
          actualTable.getValue(actualIndex, "tenant_id").toString());
      assertEquals(expectedTable.getValue(expectedIndex, "customer_id"),
          actualTable.getValue(actualIndex, "customer_id"));
      assertEquals(expectedTable.getValue(expectedIndex, "name_kana_sei"),
          actualTable.getValue(actualIndex, "name_kana_sei"));
      assertEquals(expectedTable.getValue(expectedIndex, "name_kana_mei"),
          actualTable.getValue(actualIndex, "name_kana_mei"));
      assertEquals(expectedTable.getValue(expectedIndex, "name_knj_sei"),
          actualTable.getValue(actualIndex, "name_knj_sei"));
      assertEquals(expectedTable.getValue(expectedIndex, "name_knj_mei"),
          actualTable.getValue(actualIndex, "name_knj_mei"));
      assertEquals(expectedTable.getValue(expectedIndex, "sex"),
          actualTable.getValue(actualIndex, "sex"));
      assertEquals(expectedTable.getValue(expectedIndex, "date_of_birth"),
          actualTable.getValue(actualIndex, "date_of_birth").toString());
      assertEquals(expectedTable.getValue(expectedIndex, "addr_zip_code"),
          actualTable.getValue(actualIndex, "addr_zip_code"));
      assertEquals(expectedTable.getValue(expectedIndex, "addr_knj_pref"),
          actualTable.getValue(actualIndex, "addr_knj_pref"));
      assertEquals(expectedTable.getValue(expectedIndex, "addr_knj_1"),
          actualTable.getValue(actualIndex, "addr_knj_1"));
      assertEquals(expectedTable.getValue(expectedIndex, "addr_knj_2"),
          actualTable.getValue(actualIndex, "addr_knj_2"));
      assertEquals(expectedTable.getValue(expectedIndex, "addr_tel1"),
          actualTable.getValue(actualIndex, "addr_tel1"));
      assertEquals(expectedTable.getValue(expectedIndex, "email"),
          actualTable.getValue(actualIndex, "email"));
    }
  }

  /**
   * Mock request for get
   * 
   * @author : [VJP] HoangNH
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
   * @author : [VJP] HoangNH
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
   * ?????????????????????DB?????????
   *
   * @author : [AOT] g.kim
   * @throws SQLException
   * @throws ScriptException
   * @createdAt : 2021-07-13
   * @updatedAt : 2021-07-13
   */
  @AfterEach
  void clean() throws ScriptException, SQLException {
    testScriptUtils.cleanUpDatabase();
    this.connection.close();
  }
}
