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
 * OC-005 解除
 *
 * @author : [VJP] アイン
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
  // 希望インデックス
  private int expectedIndex = 0;
  // インデックス
  private int maintenanceRequestsActualIndex = 2;

  /**
   * OC-005 解除 setup
   *
   * @author : [VJP] アイン
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

    // テストに必要なSQL実行
    testScriptUtils.executeSqlScript(sql);

    // date setting
    testScriptUtils.updateOnlineDate("2022-08-09", 1);
    testScriptUtils.updateBatchDate("2022-08-09", 1);
  }

  /**
   * OC-005 解除 テスト実行
   * 
   * #1_OC-005_SC-015,016,019 解除1次査定入力→確認→完了
   * 
   * @author : [VJP] アイン
   * @createdAt : 2021-08-17
   * @updatedAt : 2021-08-17
   */
  @Test
  void testOc00501() throws Exception {
    // 取得
    String requestJsonForGet = "json/request/oc005/req_OC005_get_data.json";
    String urlForGet = "/OC00303";
    String resultJsonForGet = "json/response/oc005/res_OC005_01_get_data.json";
    runTestCaseForGet(requestJsonForGet, urlForGet, resultJsonForGet, false);

    // 取得→一次査定
    String requestJsonForPut = "json/request/oc005/req_OC005_01_put_data.json";
    String urlForPut = "/OC00501";
    String resultXmlForPut = "xml/oc005/result_OC005_01_put.xml";
    runTestCaseForFirstPut(requestJsonForPut, urlForPut, resultXmlForPut);
  }

  /**
   * OC-005 解除 テスト実行
   * 
   * #2_OC-005_SC-017,019,019 解除2次査定入力→確認→完了
   * 
   * @author : [VJP] アイン
   * @createdAt : 2021-08-17
   * @updatedAt : 2021-08-17
   */
  @Test
  void testOc00502() throws Exception {
    // 一次査定
    testOc00501();

    // 一次査定→取得
    String requestJsonForGet = "json/request/oc005/req_OC005_get_data.json";
    String urlForGet = "/OC00303";
    String resultJsonForGet = "json/response/oc005/res_OC005_02_get_data.json";
    runTestCaseForGet(requestJsonForGet, urlForGet, resultJsonForGet, true);

    // 一次査定→取得→２時査定
    String requestJsonForPutSecond = "json/request/oc005/req_OC005_02_put_data.json";
    String urlForPutSecond = "/OC00502";
    String resultXmlForPutSecond = "xml/oc005/result_OC005_02_put.xml";
    runTestCaseForSecondPut(requestJsonForPutSecond, urlForPutSecond, resultXmlForPutSecond);
  }

  /**
   * Mock request for get
   * 
   * @author : [VJP] アイン
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
   * @author : [VJP] アイン
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
   * @author : [VJP] アイン
   * @param requestJson
   * @param url
   * @param resultJson
   * @param second 二次査定
   * @throws Exception
   * @createdAt : 2021-08-17
   * @updatedAt : 2021-08-17
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
    // 書類
    int documentsExpectedSize = expected.getDocumentsList().size();
    int documentsActualSize = actual.getDocumentsList().size();
    assertEquals(documentsExpectedSize, documentsActualSize);
    for (int i = 0; i < documentsExpectedSize; i++) {
      // URL
      assertEquals(expected.getDocumentsList().get(i).getDocumentUrl(),
          actual.getDocumentsList().get(i).getDocumentUrl());
      // タイトル
      assertEquals(expected.getDocumentsList().get(i).getDocumentTitle(),
          actual.getDocumentsList().get(i).getDocumentTitle());
    }
    if (second) {
      // 解除日
      assertEquals(expected.getTerminationBaseDate(), actual.getTerminationBaseDate());
      // 一次査定コメント
      assertEquals(expected.getCommentUnderweiter1(), actual.getCommentUnderweiter1());
      // 一次査定結果
      assertEquals(expected.getFirstAssessmentResults(), actual.getFirstAssessmentResults());
    }
  }

  /**
   * テストケースを実行(保全・解除一次査定)
   * 
   * @author : [VJP] アイン
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
      // 保全申請番号
      assertEquals(expectedTable.getValue(expectedIndex, "request_no"),
          actualTable.getValue(maintenanceRequestsActualIndex, "request_no"));
      // 証券番号
      assertEquals(expectedTable.getValue(expectedIndex, "contract_no"),
          actualTable.getValue(maintenanceRequestsActualIndex, "contract_no"));
      // 適用日
      assertEquals(expectedTable.getValue(expectedIndex, "apply_date"),
          actualTable.getValue(maintenanceRequestsActualIndex, "apply_date").toString());
      // 消滅基準日
      assertEquals(expectedTable.getValue(expectedIndex, "termination_base_date"),
          actualTable.getValue(maintenanceRequestsActualIndex, "termination_base_date").toString());
      // 通信欄
      assertEquals(null,
          actualTable.getValue(maintenanceRequestsActualIndex, "communication_column"));
      // 一次査定結果
      assertEquals(expectedTable.getValue(expectedIndex, "first_assessment_results"),
          actualTable.getValue(maintenanceRequestsActualIndex, "first_assessment_results"));
      // 一次査定コメント
      assertEquals(expectedTable.getValue(expectedIndex, "comment_underweiter1"),
          actualTable.getValue(maintenanceRequestsActualIndex, "comment_underweiter1"));
      // 保全申請ステータス
      assertEquals(expectedTable.getValue(expectedIndex, "request_status"),
          actualTable.getValue(maintenanceRequestsActualIndex, "request_status"));

    }
  }

  /**
   * テストケースを実行(保全・解除二次査定)
   * 
   * @author : [VJP] アイン
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
    // 結果テーブルデータ
    IDataSet databaseDataSet = this.idatabaseConnection.createDataSet();

    // 保全申請
    ITable maintenanceRequestsActualTable = databaseDataSet.getTable("maintenance_requests");
    // 通知内容
    ITable notificationsActualTable = databaseDataSet.getTable("notifications");
    // 契約
    ITable contractsActualTable = databaseDataSet.getTable("contracts");
    // 契約ログ
    ITable contractLogActualTable = databaseDataSet.getTable("contract_log");

    // 予想結果ロード
    try (InputStream is = getClass().getClassLoader().getResourceAsStream(resultXml)) {
      IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(is);

      // 予想結果テーブルデータ・保全申請
      ITable maintenanceRequestsExpectedTable = expectedDataSet.getTable("maintenance_requests");

      // 予想結果と値をEqual比較・保全申請
      // 保全申請番号
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "request_no"),
          maintenanceRequestsActualTable.getValue(maintenanceRequestsActualIndex, "request_no"));
      // 証券番号
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "contract_no"),
          maintenanceRequestsActualTable.getValue(maintenanceRequestsActualIndex, "contract_no"));
      // 2次査定結果
      assertEquals(
          maintenanceRequestsExpectedTable.getValue(expectedIndex, "second_assessment_results"),
          maintenanceRequestsActualTable.getValue(maintenanceRequestsActualIndex,
              "second_assessment_results"));
      // 2次査定コメント
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "comment_underweiter2"),
          maintenanceRequestsActualTable.getValue(maintenanceRequestsActualIndex,
              "comment_underweiter2"));
      // 保全申請ステータス
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "request_status"),
          maintenanceRequestsActualTable.getValue(maintenanceRequestsActualIndex,
              "request_status"));

      // 予想結果テーブルデータ・契約
      ITable contractsExpectedTable = expectedDataSet.getTable("contracts");

      // 予想結果と値をEqual比較・契約
      // 証券番号
      assertEquals(contractsExpectedTable.getValue(expectedIndex, "contract_no"),
          contractsActualTable.getValue(contractsActualIndex, "contract_no"));
      // 証券番号枝番
      assertEquals(contractsExpectedTable.getValue(expectedIndex, "contract_branch_no"),
          contractsActualTable.getValue(contractsActualIndex, "contract_branch_no"));
      // 契約ステータス
      assertEquals(contractsExpectedTable.getValue(expectedIndex, "contract_status"),
          contractsActualTable.getValue(contractsActualIndex, "contract_status"));
      // 契約消滅日
      assertEquals(contractsExpectedTable.getValue(expectedIndex, "termination_date"),
          contractsActualTable.getValue(contractsActualIndex, "termination_date").toString());

      // 予想結果テーブルデータ・通知内容
      ITable notificationsExpectedTable = expectedDataSet.getTable("notifications");

      // 予想結果と値をEqual比較・通知内容
      // 通知予定日
      assertEquals(notificationsExpectedTable.getValue(expectedIndex, "notification_date"),
          notificationsActualTable.getValue(notificationsActualIndex, "notification_date")
              .toString());
      // 証券番号
      assertEquals(notificationsExpectedTable.getValue(expectedIndex, "contract_no"),
          notificationsActualTable.getValue(notificationsActualIndex, "contract_no"));
      // 証券番号枝番
      assertEquals(notificationsExpectedTable.getValue(expectedIndex, "contract_branch_no"),
          notificationsActualTable.getValue(notificationsActualIndex, "contract_branch_no"));
      // テンプレートナンバー
      assertEquals(notificationsExpectedTable.getValue(expectedIndex, "template_number"),
          notificationsActualTable.getValue(notificationsActualIndex, "template_number"));
      // 通知実施
      assertEquals(
          notificationsExpectedTable.getValue(expectedIndex, "notification_implementation"),
          notificationsActualTable.getValue(notificationsActualIndex,
              "notification_implementation"));
      // 通信欄コメント
      assertEquals(null, notificationsActualTable.getValue(expectedIndex, "comment"));
      // 通知対象者
      assertEquals(notificationsExpectedTable.getValue(expectedIndex, "sendee"),
          notificationsActualTable.getValue(notificationsActualIndex, "sendee"));
      // 通知方法
      assertEquals(notificationsExpectedTable.getValue(expectedIndex, "notification_method"),
          notificationsActualTable.getValue(notificationsActualIndex, "notification_method"));
      // Eメールアドレス
      assertEquals(notificationsExpectedTable.getValue(expectedIndex, "email"),
          notificationsActualTable.getValue(notificationsActualIndex, "email"));
      // エラーフラグ
      assertEquals(null, notificationsActualTable.getValue(notificationsActualIndex, "error_flag"));

      // 予想結果テーブルデータ・契約ログ
      ITable contractLogExpectedTable = expectedDataSet.getTable("contract_log");

      // 予想結果と値をEqual比較・契約ログ
      // 契約管理番号
      assertEquals(contractLogExpectedTable.getValue(expectedIndex, "contract_no"),
          contractLogActualTable.getValue(contractLogActualIndex, "contract_no"));
      // 証券番号枝番
      assertEquals(contractLogExpectedTable.getValue(expectedIndex, "contract_branch_no"),
          contractLogActualTable.getValue(contractLogActualIndex, "contract_branch_no"));
      // 連番
      assertEquals(contractLogExpectedTable.getValue(expectedIndex, "sequence_no").toString(),
          contractLogActualTable.getValue(contractLogActualIndex, "sequence_no").toString());
      // ログタイプ
      assertEquals(contractLogExpectedTable.getValue(expectedIndex, "log_type"),
          contractLogActualTable.getValue(contractLogActualIndex, "log_type"));
      // エラーコード
      assertEquals(null, contractLogActualTable.getValue(contractLogActualIndex, "message_code"));
      // エラーグループ
      assertEquals(null, contractLogActualTable.getValue(contractLogActualIndex, "message_group"));
      // 事由種別（大分類）
      assertEquals(contractLogExpectedTable.getValue(expectedIndex, "reason_group_code"),
          contractLogActualTable.getValue(contractLogActualIndex, "reason_group_code"));
      // 事由ID
      assertEquals(contractLogExpectedTable.getValue(expectedIndex, "reason_code"),
          contractLogActualTable.getValue(contractLogActualIndex, "reason_code"));
      // 異動コード
      assertEquals(contractLogExpectedTable.getValue(expectedIndex, "contact_transaction_code"),
          contractLogActualTable.getValue(contractLogActualIndex, "contact_transaction_code"));
      // 摘要
      assertEquals(contractLogExpectedTable.getValue(expectedIndex, "description"),
          contractLogActualTable.getValue(contractLogActualIndex, "description"));
      // 処理プログラム
      assertEquals(contractLogExpectedTable.getValue(expectedIndex, "program_name"),
          contractLogActualTable.getValue(contractLogActualIndex, "program_name"));
    }
  }

  /**
   * 各テストことにDB初期化
   *
   * @author : [VJP] アイン
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
