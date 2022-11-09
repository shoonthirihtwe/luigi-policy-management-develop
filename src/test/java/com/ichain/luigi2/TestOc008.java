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
 * OC-008 受取人変更
 *
 * @author : [VJP] タン
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

  // 希望インデックス
  private int expectedIndex = 0;
  // インデックス０
  private int index0 = 0;
  // インデックス１
  private int index1 = 1;
  // インデックス２
  private int index2 = 2;
  // maintenance_requestsの初期件数
  private int maintenanceCount = 7;

  /**
   * OC-008 受取人変更 setup
   *
   * @author : [VJP] タン
   * @createdAt : 2021-08-17
   * @updatedAt : 2021-08-17
   */
  @BeforeEach
  void setup() throws SQLException, DatabaseUnitException {
    this.connection = dataSource.getConnection();
    this.idatabaseConnection = new MySqlConnection(connection, "luigi2_test");

    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.ctx)
        .addFilters(new CharacterEncodingFilter("UTF-8", true)).build();
    // テストに必要なSQL実行
    testScriptUtils.executeSqlScript(sql);
  }

  /**
   * OC-008 受取人変更 テスト実行
   * 
   * #1_OC-007,SC-025-026
   * 
   * @author : [VJP] タン
   * @createdAt : 2021-08-17
   * @updatedAt : 2021-08-17
   */
  @Test
  void testOc00801() throws Exception {
    // 取得
    String requestJsonForGet = "json/request/oc008/req_OC008_01_get_data.json";
    String urlForGet = "/OC00803";
    String resultJsonForGet = "json/response/oc008/res_OC008_01_data.json";
    runTestCaseForGet(requestJsonForGet, urlForGet, resultJsonForGet);

    // 取得→一次査定
    String requestJsonForPut = "json/request/oc008/req_OC008_01_put_data.json";
    String urlForPut = "/OC00801";
    String resultXmlForPut = "xml/oc008/result_OC008_01.xml";
    runTestCaseForFirstPut(requestJsonForPut, urlForPut, resultXmlForPut, index0);
  }

  /**
   * OC-008 受取人変更 テスト実行
   * 
   * #2_OC-007,SC-025-026
   * 
   * @author : [VJP] タン
   * @createdAt : 2021-08-17
   * @updatedAt : 2021-08-17
   */
  @Test
  void testOc00802() throws Exception {
    // 取得
    String requestJsonForGet = "json/request/oc008/req_OC008_02_get_data.json";
    String urlForGet = "/OC00803";
    String resultJsonForGet = "json/response/oc008/res_OC008_02_data.json";
    runTestCaseForGet(requestJsonForGet, urlForGet, resultJsonForGet);

    // 取得→一次査定
    String requestJsonForPut = "json/request/oc008/req_OC008_02_put_data.json";
    String urlForPut = "/OC00801";
    String resultXmlForPut = "xml/oc008/result_OC008_02.xml";
    runTestCaseForFirstPut(requestJsonForPut, urlForPut, resultXmlForPut, index1);
  }

  /**
   * OC-008 受取人変更 テスト実行
   * 
   * #3_OC-007,SC-025-026
   * 
   * @author : [VJP] タン
   * @createdAt : 2021-08-17
   * @updatedAt : 2021-08-17
   */
  @Test
  void testOc00803() throws Exception {
    // 取得
    String requestJsonForGet = "json/request/oc008/req_OC008_03_get_data.json";
    String urlForGet = "/OC00803";
    String resultJsonForGet = "json/response/oc008/res_OC008_03_data.json";
    runTestCaseForGet(requestJsonForGet, urlForGet, resultJsonForGet);

    // 取得→一次査定
    String requestJsonForPut = "json/request/oc008/req_OC008_03_put_data.json";
    String urlForPut = "/OC00801";
    String resultXmlForPut = "xml/oc008/result_OC008_03.xml";
    runTestCaseForFirstPut(requestJsonForPut, urlForPut, resultXmlForPut, index2);
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
   * @author : [VJP] タン
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
    // 処理内容
    assertEquals(expected.getTransactionCode(), actual.getTransactionCode());
    // 契約者メールアドレス
    assertEquals(expected.getContractEmail(), actual.getContractEmail());
    // 受取人
    int beneficiariesExpectedSize = expected.getBeneficiariesList().size();
    int beneficiariesActualSize = actual.getBeneficiariesList().size();
    assertEquals(beneficiariesExpectedSize, beneficiariesActualSize);
    for (int i = 0; i < beneficiariesExpectedSize; i++) {
      // 受取人
      BeneficialiesVo expectedBeneficiaries = expected.getBeneficiariesList().get(i);
      BeneficialiesVo actualBeneficiaries = actual.getBeneficiariesList().get(i);

      // 個人/法人区分
      assertEquals(expectedBeneficiaries.getCorporateIndividualFlag(),
          actualBeneficiaries.getCorporateIndividualFlag());
      // 氏名(漢字) 姓
      assertEquals(expectedBeneficiaries.getNameKnjSei(), actualBeneficiaries.getNameKnjSei());
      // 氏名(漢字) 名
      assertEquals(expectedBeneficiaries.getNameKnjMei(), actualBeneficiaries.getNameKnjMei());
      // 氏名(漢字) セイ
      assertEquals(expectedBeneficiaries.getNameKanaSei(), actualBeneficiaries.getNameKanaSei());
      // 氏名(漢字) メイ
      assertEquals(expectedBeneficiaries.getNameKanaMei(), actualBeneficiaries.getNameKanaMei());
      // 被保険者との続柄
      assertEquals(expectedBeneficiaries.getRelShipToInsured(),
          actualBeneficiaries.getRelShipToInsured());
      // 受取割合
      assertEquals(expectedBeneficiaries.getShare(), actualBeneficiaries.getShare());
    }
    // 書類
    int documentsExpectedSize = expected.getDocumentsList().size();
    int documentsActualSize = actual.getDocumentsList().size();
    assertEquals(documentsExpectedSize, documentsActualSize);
    for (int i = 0; i < documentsExpectedSize; i++) {
      // タイトル
      assertEquals(expected.getDocumentsList().get(i).getDocumentTitle(),
          actual.getDocumentsList().get(i).getDocumentTitle());
      // URL
      assertEquals(expected.getDocumentsList().get(i).getDocumentUrl(),
          actual.getDocumentsList().get(i).getDocumentUrl());
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
   * @createdAt : 2021-08-17
   * @updatedAt : 2021-08-17
   */
  private void runTestCaseForFirstPut(String requestJson, String url, String resultXml,
      int actualIndex) throws Exception {
    mockMvcForPut(url, requestJson);
    // 結果テーブルデータ
    IDataSet databaseDataSet = this.idatabaseConnection.createDataSet();
    // 保全申請
    ITable maintenanceRequestsActualTable = databaseDataSet.getTable("maintenance_requests");
    // 保全申請受取人
    ITable beneficiariesActualTable =
        databaseDataSet.getTable("maintenance_requests_beneficiaries");
    // 契約ログ
    ITable contractLogActualTable = databaseDataSet.getTable("contract_log");

    // 予想結果ロード
    try (InputStream is = getClass().getClassLoader().getResourceAsStream(resultXml)) {
      IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(is);
      // 予想結果テーブルデータ
      // 保全申請
      ITable maintenanceRequestsExpectedTable = expectedDataSet.getTable("maintenance_requests");
      // 予想結果と値をEqual比較
      // テナントID
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "tenant_id"),
          maintenanceRequestsActualTable.getValue(actualIndex, "tenant_id").toString());
      // 保全申請番号
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "request_no"),
          maintenanceRequestsActualTable.getValue(actualIndex, "request_no"));
      // 証券番号
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "contract_no"),
          maintenanceRequestsActualTable.getValue(actualIndex, "contract_no"));
      // 証券番号枝番
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "contract_branch_no"),
          maintenanceRequestsActualTable.getValue(actualIndex, "contract_branch_no"));
      // 有効/無効フラグ
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "active_inactive"),
          maintenanceRequestsActualTable.getValue(actualIndex, "active_inactive"));
      // 保全申請分類
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "transaction_code"),
          maintenanceRequestsActualTable.getValue(actualIndex, "transaction_code"));
      // 保全申請ステータス
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "request_status"),
          maintenanceRequestsActualTable.getValue(actualIndex, "request_status"));
      // 申込日
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "application_date"),
          maintenanceRequestsActualTable.getValue(actualIndex, "application_date").toString());
      // 申込時刻
      assertEquals(null, maintenanceRequestsActualTable.getValue(actualIndex, "application_time"));
      // 申込経路
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "application_method"),
          maintenanceRequestsActualTable.getValue(actualIndex, "application_method"));
      // 受付日
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "received_date"),
          maintenanceRequestsActualTable.getValue(actualIndex, "received_date").toString());
      // 受付場所
      assertEquals(null, maintenanceRequestsActualTable.getValue(actualIndex, "received_at"));
      // 一次査定コメント
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "comment_underweiter1"),
          maintenanceRequestsActualTable.getValue(actualIndex, "comment_underweiter1"));
      // 一次査定結果
      assertEquals(
          maintenanceRequestsExpectedTable.getValue(expectedIndex, "first_assessment_results"),
          maintenanceRequestsActualTable.getValue(actualIndex, "first_assessment_results"));
      // 二次査定コメント
      assertEquals(null,
          maintenanceRequestsActualTable.getValue(actualIndex, "comment_underweiter2"));
      // 二次査定結果
      assertEquals(null,
          maintenanceRequestsActualTable.getValue(actualIndex, "second_assessment_results"));
      // 通信欄
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "communication_column"),
          maintenanceRequestsActualTable.getValue(actualIndex, "communication_column"));
      // 適用日
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "apply_date"),
          maintenanceRequestsActualTable.getValue(actualIndex, "apply_date").toString());
      // 処理起票区分
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "entry_type"),
          maintenanceRequestsActualTable.getValue(actualIndex, "entry_type"));
      // 払込方法コード
      assertEquals(null,
          maintenanceRequestsActualTable.getValue(actualIndex, "payment_method_code"));
      // 収納代行会社コード
      assertEquals(null,
          maintenanceRequestsActualTable.getValue(actualIndex, "factoring_company_code"));

      // 銀行コード
      assertEquals(null, maintenanceRequestsActualTable.getValue(actualIndex, "bank_code"));
      // 支店コード
      assertEquals(null, maintenanceRequestsActualTable.getValue(actualIndex, "bank_branch_code"));
      // 口座種別
      assertEquals(null, maintenanceRequestsActualTable.getValue(actualIndex, "bank_account_type"));
      // 口座番号
      assertEquals(null, maintenanceRequestsActualTable.getValue(actualIndex, "bank_account_no"));
      // 口座名義人
      assertEquals(null, maintenanceRequestsActualTable.getValue(actualIndex, "bank_account_name"));
      // カード番号(トークン)
      assertEquals(null, maintenanceRequestsActualTable.getValue(actualIndex, "token_no"));
      // 通知用メールアドレス
      assertEquals(null,
          maintenanceRequestsActualTable.getValue(actualIndex, "email_for_notification"));
      // 消滅基準日
      assertEquals(null,
          maintenanceRequestsActualTable.getValue(actualIndex, "termination_base_date"));
      // 消滅事由
      assertEquals(null, maintenanceRequestsActualTable.getValue(actualIndex, "termination_title"));
      // ロック用
      assertEquals(maintenanceRequestsExpectedTable.getValue(expectedIndex, "update_count"),
          maintenanceRequestsActualTable.getValue(actualIndex, "update_count").toString());

      // 保全申請受取人
      if (beneficiariesActualTable.getRowCount() > maintenanceCount) {
        ITable beneficiariesExpectedTable =
            expectedDataSet.getTable("maintenance_requests_beneficiaries");
        for (int i = 0; i < beneficiariesExpectedTable.getRowCount(); i++) {
          // 存在してるレコード: 7
          int j = i + maintenanceCount;
          // テナントID
          assertEquals(beneficiariesExpectedTable.getValue(i, "tenant_id"),
              beneficiariesActualTable.getValue(j, "tenant_id").toString());
          // 保全申請番号
          assertEquals(beneficiariesExpectedTable.getValue(i, "request_no"),
              beneficiariesActualTable.getValue(j, "request_no"));
          // 申請適用前後フラグ
          assertEquals(beneficiariesExpectedTable.getValue(i, "before_after"),
              beneficiariesActualTable.getValue(j, "before_after"));
          // ロールタイプ
          assertEquals(beneficiariesExpectedTable.getValue(i, "role_type"),
              beneficiariesActualTable.getValue(j, "role_type"));
          // ロール連番
          assertEquals(beneficiariesExpectedTable.getValue(i, "role_sequence_no"),
              beneficiariesActualTable.getValue(j, "role_sequence_no").toString());
          // 法人/個人区分
          assertEquals(beneficiariesExpectedTable.getValue(i, "corporate_individual_flag"),
              beneficiariesActualTable.getValue(j, "corporate_individual_flag"));
          // 個人姓(漢字)/法人名(正式)
          assertEquals(beneficiariesExpectedTable.getValue(i, "name_knj_sei"),
              beneficiariesActualTable.getValue(j, "name_knj_sei"));
          // 個人名(漢字)
          assertEquals(beneficiariesExpectedTable.getValue(i, "name_knj_mei"),
              beneficiariesActualTable.getValue(j, "name_knj_mei"));
          // 個人姓(カナ)/法人名(カナ)
          assertEquals(beneficiariesExpectedTable.getValue(i, "name_kana_sei"),
              beneficiariesActualTable.getValue(j, "name_kana_sei"));
          // 個人名(カナ)
          assertEquals(beneficiariesExpectedTable.getValue(i, "name_kana_mei"),
              beneficiariesActualTable.getValue(j, "name_kana_mei"));
          // 受取の割合
          assertEquals(beneficiariesExpectedTable.getValue(i, "share"),
              beneficiariesActualTable.getValue(j, "share").toString());
          // 被保険者からみた続柄
          assertEquals(beneficiariesExpectedTable.getValue(i, "rel_ship_to_insured"),
              beneficiariesActualTable.getValue(j, "rel_ship_to_insured"));
          // ロック用
          assertEquals(beneficiariesExpectedTable.getValue(i, "update_count"),
              beneficiariesActualTable.getValue(j, "update_count").toString());
        }
      }

      // 契約ログ
      if (contractLogActualTable.getRowCount() > 0) {
        if (actualIndex != expectedIndex) {
          actualIndex = 0;
        }
        // 契約ログ
        ITable contractLogExpectedTable = expectedDataSet.getTable("contract_log");
        // テナントID
        assertEquals(contractLogExpectedTable.getValue(expectedIndex, "tenant_id"),
            contractLogActualTable.getValue(actualIndex, "tenant_id").toString());

        // 契約管理番号
        assertEquals(contractLogExpectedTable.getValue(expectedIndex, "contract_no"),
            contractLogActualTable.getValue(actualIndex, "contract_no"));
        // 証券番号枝番
        assertEquals(contractLogExpectedTable.getValue(expectedIndex, "contract_branch_no"),
            contractLogActualTable.getValue(actualIndex, "contract_branch_no"));
        // 連番
        assertEquals(contractLogExpectedTable.getValue(expectedIndex, "sequence_no"),
            contractLogActualTable.getValue(actualIndex, "sequence_no").toString());
        // ログタイプ
        assertEquals(contractLogExpectedTable.getValue(expectedIndex, "log_type"),
            contractLogActualTable.getValue(actualIndex, "log_type"));
        // エラーコード
        assertEquals(null, contractLogActualTable.getValue(actualIndex, "message_code"));
        // エラーグループ
        assertEquals(null, contractLogActualTable.getValue(actualIndex, "message_group"));
        // 事由種別（大分類）
        assertEquals(contractLogExpectedTable.getValue(expectedIndex, "reason_group_code"),
            contractLogActualTable.getValue(actualIndex, "reason_group_code"));
        // 事由ID
        assertEquals(contractLogExpectedTable.getValue(expectedIndex, "reason_code"),
            contractLogActualTable.getValue(actualIndex, "reason_code"));
        // 異動コード
        assertEquals(contractLogExpectedTable.getValue(expectedIndex, "contact_transaction_code"),
            contractLogActualTable.getValue(actualIndex, "contact_transaction_code"));
        // 摘要
        assertEquals(contractLogExpectedTable.getValue(expectedIndex, "description"),
            contractLogActualTable.getValue(actualIndex, "description"));
        // 処理プログラム
        assertEquals(contractLogExpectedTable.getValue(expectedIndex, "program_name"),
            contractLogActualTable.getValue(actualIndex, "program_name"));
        // ロック用
        assertEquals(contractLogExpectedTable.getValue(expectedIndex, "update_count"),
            contractLogActualTable.getValue(actualIndex, "update_count").toString());
      }
    }
  }

  /**
   * 各テストことにDB初期化
   *
   * @author : [VJP] タン
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
