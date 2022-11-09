package com.ichain.luigi2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.dbunit.Assertion;
import org.dbunit.DatabaseUnitException;
import org.dbunit.assertion.comparer.value.ValueComparer;
import org.dbunit.assertion.comparer.value.ValueComparers;
import org.dbunit.assertion.comparer.value.builder.ColumnValueComparerMapBuilder;
import org.dbunit.database.DatabaseSequenceFilter;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.IRowValueProvider;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.RowFilterTable;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.dataset.filter.IRowFilter;
import org.dbunit.dataset.filter.ITableFilter;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.mysql.MySqlConnection;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
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
 * OC-001 申込受付
 *
 * @author : [VJP] HoangNH
 * @createdAt : 2021-08-13
 * @updatedAt : 2021-08-13
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = Luigi2Application.class)
@TestPropertySource("classpath:application-common-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class TestOc001 {

  private MockMvc mockMvc;
  @Autowired
  private WebApplicationContext ctx;

  @Autowired
  private TestScriptUtils testScriptUtils;

  private Connection connection;
  private IDatabaseConnection idatabaseConnection;
  private ITableFilter filteredTable;

  @Autowired
  @Qualifier("luigi2DataSource")
  private DataSource dataSource;

  private String sql = "sql/insert_OC001.sql";

  /**
   * OC-001 申込受付 setup
   *
   * @author : [VJP] HoangNH
   * @createdAt : 2021-08-13
   * @updatedAt : 2021-08-13
   */
  @BeforeAll
  void setup() throws SQLException, DatabaseUnitException {
    this.connection = dataSource.getConnection();
    this.idatabaseConnection = new MySqlConnection(connection, "luigi2_test");
    this.filteredTable = new DatabaseSequenceFilter(this.idatabaseConnection);

    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.ctx)
        .addFilters(new CharacterEncodingFilter("UTF-8", true)).build();


    // テストに必要なSQL実行
    testScriptUtils.executeSqlScript(sql);
  }

  /**
   * #1_OC_001、SC_002テストケースを実行
   * 
   * @author : [VJP] HoangNH
   * @param requestJson
   * @param url
   * @param resultJson
   * @throws Exception
   * @createdAt : 2021-08-13
   * @updatedAt : 2021-08-13
   */
  void testOc00101() throws Exception {
    String requestUrl = "json/request/oc001/req_OC00101_data.json";
    mockMvcForPostWithFiles(requestUrl);
    compareResult("xml/oc001/result_Oc00101.xml", "0000000000000000000001");
  }

  /**
   * #2_OC_001、SC_002テストケースを実行
   * 
   * @author : [VJP] HoangNH
   * @param requestJson
   * @param url
   * @param resultJson
   * @throws Exception
   * @createdAt : 2021-08-13
   * @updatedAt : 2021-08-13
   */
  void testOc00102() throws Exception {
    testOc00101();
    mockMvcForPostWithFile("json/request/oc001/req_OC00102_data.json");
    compareResult("xml/oc001/result_Oc00102.xml", "0000000000000000000002");
  }

  /**
   * #3_OC_001、SC_002テストケースを実行
   * 
   * @author : [VJP] HoangNH
   * @param requestJson
   * @param url
   * @param resultJson
   * @throws Exception
   * @createdAt : 2021-08-13
   * @updatedAt : 2021-08-13
   */
  void testOc00103() throws Exception {
    testOc00102();
    mockMvcForPostWithFile("json/request/oc001/req_OC00103_data.json");
    compareResult("xml/oc001/result_Oc00103.xml", "0000000000000000000003");
  }

  /**
   * #4_OC_001、SC_002テストケースを実行
   * 
   * @author : [VJP] HoangNH
   * @param requestJson
   * @param url
   * @param resultJson
   * @throws Exception
   * @createdAt : 2021-08-13
   * @updatedAt : 2021-08-13
   */
  void testOc00104() throws Exception {
    testOc00103();
    mockMvcForPostWithFile("json/request/oc001/req_OC00104_data.json");
    compareResult("xml/oc001/result_Oc00104.xml", "0000000000000000000004");
  }

  /**
   * #5_OC_001、SC_002テストケースを実行
   * 
   * @author : [VJP] HoangNH
   * @param requestJson
   * @param url
   * @param resultJson
   * @throws Exception
   * @createdAt : 2021-08-13
   * @updatedAt : 2021-08-13
   */
  void testOc00105() throws Exception {
    testOc00104();
    mockMvcForPostWithFile("json/request/oc001/req_OC00105_data.json");
    compareResult("xml/oc001/result_Oc00105.xml", "0000000000000000000005");
  }

  /**
   * #6_OC_001、SC_002テストケースを実行
   * 
   * @author : [VJP] HoangNH
   * @param requestJson
   * @param url
   * @param resultJson
   * @throws Exception
   * @createdAt : 2021-08-13
   * @updatedAt : 2021-08-13
   */
  @SuppressWarnings("unchecked")
  void testOc00106() throws Exception {
    testOc00105();
    MvcResult mvcResult = mockMvcEmpty("json/request/oc001/req_OC00106_data.json");
    ObjectMapper mapper = new ObjectMapper();
    // 予想結果ロード
    List<HashMap<String, String>> results = (List<HashMap<String, String>>) mapper
        .readValue(mvcResult.getResponse().getContentAsString(), HashMap.class).get("items");
    assertEquals("V0012", results.get(0).get("code"));

    String response = results.toArray()[0].toString();
    assertEquals(true, 0 < response.indexOf("receivedDate"));
  }

  /**
   * #7_OC_001、SC_002テストケースを実行
   * 
   * @author : [VJP] HoangNH
   * @param requestJson
   * @param url
   * @param resultJson
   * @throws Exception
   * @createdAt : 2021-08-13
   * @updatedAt : 2021-08-13
   */
  @SuppressWarnings("unchecked")
  @Test
  void testOc00107() throws Exception {
    testOc00106();
    MvcResult mvcResult = mockMvcEmpty("json/request/oc001/req_OC00107_data.json");
    ObjectMapper mapper = new ObjectMapper();
    // 予想結果ロード
    List<HashMap<String, String>> results = (List<HashMap<String, String>>) mapper
        .readValue(mvcResult.getResponse().getContentAsString(), HashMap.class).get("items");
    assertEquals("D0002", results.get(1).get("code"));

    String response = results.toArray()[1].toString();
    assertEquals(true, 0 < response.indexOf("contractNo"));
  }

  void compareResult(String tarrgetUrl, String requestNo)
      throws SQLException, IOException, DatabaseUnitException {
    // 結果テーブルデータ
    FilteredDataSet databaseDataSet =
        new FilteredDataSet(filteredTable, this.idatabaseConnection.createDataSet());

    IRowFilter rowFilter = new IRowFilter() {
      @Override
      public boolean accept(IRowValueProvider rowValueProvider) {
        Object columnValue = null;
        try {
          columnValue = rowValueProvider.getColumnValue("request_no");
        } catch (DataSetException e) {
          e.printStackTrace();
        }
        if (((String) columnValue).equals(requestNo)) {
          return true;
        }
        return false;
      }
    };


    // 保全申請
    ITable maintenaceRequestTable =
        new RowFilterTable(databaseDataSet.getTable("maintenance_requests"), rowFilter);
    // 保全申請資料
    ITable maintenaceDocument =
        new RowFilterTable(databaseDataSet.getTable("maintenance_documents"), rowFilter);
    // 保全申請番号
    ITable maintenaceDocumentNo =
        new RowFilterTable(databaseDataSet.getTable("maintenance_requests_no"), rowFilter);

    // 予想結果ロード
    try (InputStream is = getClass().getClassLoader().getResourceAsStream(tarrgetUrl)) {
      IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(is);
      // 予想結果テーブルデータ
      ITable underwritingsExpectedTable = expectedDataSet.getTable("maintenance_requests");

      // 予想結果テーブルでカラムFiltering
      ITable maintenanceRequestsFilteredActualTable = DefaultColumnFilter.includedColumnsTable(
          maintenaceRequestTable, underwritingsExpectedTable.getTableMetaData().getColumns());

      ValueComparer maintenanceRequestsDefaultValueComparer =
          ValueComparers.isActualEqualToExpected;
      Map<String, ValueComparer> underwritingsColumnValueComparers =
          new ColumnValueComparerMapBuilder().build();

      // 予想結果と値をEqual比較
      Assertion.assertWithValueComparer(underwritingsExpectedTable,
          maintenanceRequestsFilteredActualTable, maintenanceRequestsDefaultValueComparer,
          underwritingsColumnValueComparers);

      // 予想結果テーブルデータ
      ITable documentsExpectedTable = expectedDataSet.getTable("maintenance_documents");

      // 予想結果テーブルでカラムFiltering
      ITable documentsFilteredActualTable = DefaultColumnFilter.includedColumnsTable(
          maintenaceDocument, documentsExpectedTable.getTableMetaData().getColumns());

      ValueComparer documentsDefaultValueComparer = ValueComparers.isActualEqualToExpected;
      Map<String, ValueComparer> documentsColumnValueComparers =
          new ColumnValueComparerMapBuilder().build();

      // 予想結果と値をEqual比較
      Assertion.assertWithValueComparer(documentsExpectedTable, documentsFilteredActualTable,
          documentsDefaultValueComparer, documentsColumnValueComparers);


      // 予想結果テーブルデータ
      ITable expectedTable = expectedDataSet.getTable("maintenance_requests_no");
      int actualIndex = 0;
      int expectedIndex = 0;
      assertEquals(expectedTable.getValue(expectedIndex, "tenant_id").toString(),
          maintenaceDocumentNo.getValue(actualIndex, "tenant_id").toString());
      assertEquals(expectedTable.getValue(expectedIndex, "request_no"),
          maintenaceDocumentNo.getValue(actualIndex, "request_no"));
      assertEquals("2", maintenaceDocumentNo.getValue(actualIndex, "update_count").toString());
    }
  }

  /**
   * Mock request
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
  private void mockMvcForPostWithFiles(String requestJson) throws JsonParseException,
      JsonMappingException, UnsupportedEncodingException, IOException, Exception {

    // １．対象ファイルロード
    ClassPathResource resourceFile1 = new ClassPathResource("file/oc001/ドキュメント1.csv");
    ClassPathResource resourceFile2 = new ClassPathResource("file/oc001/ドキュメント2.csv");
    ClassPathResource resourceFile3 = new ClassPathResource("file/oc001/ドキュメント3.csv");
    ClassPathResource resourceFile4 = new ClassPathResource("file/oc001/ドキュメント4.csv");
    ClassPathResource resourceFile5 = new ClassPathResource("file/oc001/ドキュメント5.csv");
    ClassPathResource resourceFile6 = new ClassPathResource("file/oc001/ドキュメント6.csv");
    ClassPathResource resourceFile7 = new ClassPathResource("file/oc001/ドキュメント7.csv");
    ClassPathResource resourceFile8 = new ClassPathResource("file/oc001/ドキュメント8.csv");
    ClassPathResource resourceFile9 = new ClassPathResource("file/oc001/ドキュメント9.csv");
    ClassPathResource resourceFile10 = new ClassPathResource("file/oc001/ドキュメント10.csv");

    // "fileList"はパラメータ名
    MockMultipartFile file1 = new MockMultipartFile("fileList", resourceFile1.getFilename(),
        "text/plain", resourceFile1.getInputStream().readAllBytes());
    MockMultipartFile file2 = new MockMultipartFile("fileList", resourceFile2.getFilename(),
        "text/plain", resourceFile2.getInputStream().readAllBytes());
    MockMultipartFile file3 = new MockMultipartFile("fileList", resourceFile3.getFilename(),
        "text/plain", resourceFile3.getInputStream().readAllBytes());
    MockMultipartFile file4 = new MockMultipartFile("fileList", resourceFile4.getFilename(),
        "text/plain", resourceFile4.getInputStream().readAllBytes());
    MockMultipartFile file5 = new MockMultipartFile("fileList", resourceFile5.getFilename(),
        "text/plain", resourceFile5.getInputStream().readAllBytes());
    MockMultipartFile file6 = new MockMultipartFile("fileList", resourceFile6.getFilename(),
        "text/plain", resourceFile6.getInputStream().readAllBytes());
    MockMultipartFile file7 = new MockMultipartFile("fileList", resourceFile7.getFilename(),
        "text/plain", resourceFile7.getInputStream().readAllBytes());
    MockMultipartFile file8 = new MockMultipartFile("fileList", resourceFile8.getFilename(),
        "text/plain", resourceFile8.getInputStream().readAllBytes());
    MockMultipartFile file9 = new MockMultipartFile("fileList", resourceFile9.getFilename(),
        "text/plain", resourceFile9.getInputStream().readAllBytes());
    MockMultipartFile file10 = new MockMultipartFile("fileList", resourceFile10.getFilename(),
        "text/plain", resourceFile10.getInputStream().readAllBytes());

    // // ２．MockMultipartHttpServletRequestBuilder生成
    MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/OC00101");

    // ３．APIコール
    mockMvc.perform(builder.file(file1).file(file2).file(file3).file(file4).file(file5).file(file6)
        .file(file7).file(file8).file(file9).file(file10).header("x-frontend-domain", "localhost")
        .params(testScriptUtils.loadJsonToMultiValueMap(requestJson))
        .contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")
        .accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print());
  }


  /**
   * Mock request
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
  private void mockMvcForPostWithFile(String requestJson) throws JsonParseException,
      JsonMappingException, UnsupportedEncodingException, IOException, Exception {

    // １．対象ファイルロード
    ClassPathResource resourceFile1 = new ClassPathResource("file/oc001/ドキュメント1.csv");

    // "fileList"はパラメータ名
    MockMultipartFile file1 = new MockMultipartFile("fileList", resourceFile1.getFilename(),
        "text/plain", resourceFile1.getInputStream().readAllBytes());

    // // ２．MockMultipartHttpServletRequestBuilder生成
    MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/OC00101");

    // ３．APIコール
    mockMvc
        .perform(builder.file(file1).header("x-frontend-domain", "localhost")
            .params(testScriptUtils.loadJsonToMultiValueMap(requestJson))
            .contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk()).andDo(print());
  }

  private MvcResult mockMvcEmpty(String requestJson) throws Exception {

    // １．対象ファイルロード
    ClassPathResource resourceFile1 = new ClassPathResource("file/oc001/ドキュメント1.csv");

    // "fileList"はパラメータ名
    MockMultipartFile file1 = new MockMultipartFile("fileList", resourceFile1.getFilename(),
        "text/plain", resourceFile1.getInputStream().readAllBytes());

    // // ２．MockMultipartHttpServletRequestBuilder生成
    MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/OC00101");

    // ３．APIコール
    return mockMvc
        .perform(builder.file(file1).header("x-frontend-domain", "localhost")
            .params(testScriptUtils.loadJsonToMultiValueMap(requestJson))
            .contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError()).andDo(print()).andReturn();

  }

  /**
   * 各テストことにDB初期化
   *
   * @author : [AOT] g.kim
   * @throws SQLException
   * @throws ScriptException
   * @createdAt : 2021-07-13
   * @updatedAt : 2021-07-13
   */
  @AfterAll
  void clean() throws ScriptException, SQLException {
    testScriptUtils.deleteUploadFiles();
    testScriptUtils.cleanUpDatabase();
    this.connection.close();
  }
}
