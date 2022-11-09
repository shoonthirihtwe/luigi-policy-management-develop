package com.ichain.luigi2;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
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
 * OC002 保全申請受付一覧画面
 *
 * @author : [AOT] anh
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
public class TestOc002 {
  private MockMvc mockMvc;
  @Autowired
  private WebApplicationContext ctx;

  @Autowired
  private TestScriptUtils testScriptUtils;

  private String sql = "sql/insert_OC002.sql";
  private String url = "/OC00201";

  /**
   * OC002 保全申請受付一覧画面 setup
   *
   * @author : [AOT] anh
   * @createdAt : 2021-07-29
   * @updatedAt : 2021-07-29
   */
  @BeforeEach
  void setup() throws SQLException {

    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.ctx)
        .addFilters(new CharacterEncodingFilter("UTF-8", true)).build();
    // テストに必要なSQL実行
    testScriptUtils.executeSqlScript(sql);
  }

  /**
   * OC002-01 保全申請受付一覧画面 テスト実行#1
   *
   * @author : [AOT] anh
   * @createdAt : 2021-08-12
   * @updatedAt : 2021-08-12
   */
  @Test
  void test_Oc00201() throws Exception {

    String requestJson = "json/request/oc002/req_OC002_01_data.json";
    String resultJson = "json/response/oc002/res_OC002_01_data.json";
    // 比較する行の総数
    int totalRow = 5;

    runTestCase(requestJson, resultJson, totalRow);
  }

  /**
   * OC002-02 保全申請受付一覧画面 テスト実行#2
   *
   * @author : [AOT] anh
   * @createdAt : 2021-07-29
   * @updatedAt : 2021-07-29
   */
  @Test
  void test_Oc00202() throws Exception {

    String requestJson = "json/request/oc002/req_OC002_02_data.json";
    String resultJson = "json/response/oc002/res_OC002_02_data.json";
    // 比較する行の総数
    int totalRow = 3;

    runTestCase(requestJson, resultJson, totalRow);
  }

  /**
   * OC002-03 保全申請受付一覧画面 テスト実行#3
   *
   * @author : [AOT] anh
   * @createdAt : 2021-08-12
   * @updatedAt : 2021-08-12
   */
  @Test
  void test_Oc00203() throws Exception {

    String requestJson = "json/request/oc002/req_OC002_03_data.json";
    String resultJson = "json/response/oc002/res_OC002_03_data.json";
    // 比較する行の総数
    int totalRow = 1;

    runTestCase(requestJson, resultJson, totalRow);
  }

  /**
   * OC002-04 保全申請受付一覧画面 テスト実行#4
   *
   * @author : [AOT] anh
   * @createdAt : 2021-08-12
   * @updatedAt : 2021-08-12
   */
  @Test
  void test_Oc00204() throws Exception {

    String requestJson = "json/request/oc002/req_OC002_04_data.json";
    String resultJson = "json/response/oc002/res_OC002_04_data.json";
    // 比較する行の総数
    int totalRow = 3;

    runTestCase(requestJson, resultJson, totalRow);
  }

  /**
   * OC00201-05 保全申請受付一覧画面 テスト実行#５
   *
   * @author : [AOT] anh
   * @createdAt : 2021-08-12
   * @updatedAt : 2021-08-12
   */
  @Test
  void test_Oc00205() throws Exception {

    String requestJson = "json/request/oc002/req_OC002_05_data.json";
    String resultJson = "json/response/oc002/res_OC002_05_data.json";
    // 比較する行の総数
    int totalRow = 4;

    runTestCase(requestJson, resultJson, totalRow);
  }

  /**
   * Mock request
   * 
   * @author : [AOT] anh
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
  private MvcResult mockMvc(String requestJson) throws JsonParseException, JsonMappingException,
      UnsupportedEncodingException, IOException, Exception {
    // APIコール
    return mockMvc
        .perform(MockMvcRequestBuilders.get(url).header("x-frontend-domain", "localhost")
            .params(testScriptUtils.loadJsonToMultiValueMap(requestJson))
            .contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
  }

  /**
   * 色なテストケースを実行
   * 
   * @author : [VJP] アイン
   * @param requestJson
   * @param resultJson
   * @param totalRow
   * @throws Exception
   * @createdAt : 2021-08-18
   * @updatedAt : 2021-08-18
   */
  @SuppressWarnings("unchecked")
  private void runTestCase(String requestJson, String resultJson, int totalRow) throws Exception {

    // APIコール
    MvcResult mvcResult = mockMvc(requestJson);

    ObjectMapper mapper = new ObjectMapper();

    // 予想結果ロード
    List<HashMap<String, String>> targets = (List<HashMap<String, String>>) mapper
        .readValue(mvcResult.getResponse().getContentAsString(), HashMap.class).get("items");
    List<HashMap<String, String>> expecteds =
        (List<HashMap<String, String>>) testScriptUtils.loadJsonToObject(resultJson);

    // 検索結果件数
    assertEquals(totalRow, targets.size());
    assertEquals(totalRow, expecteds.size());

    for (int i = 0; i < expecteds.size(); i++) {
      // 受付日
      assertEquals(expecteds.get(i).get("receivedDate"), targets.get(i).get("receivedDate"));
      // 証券番号
      assertEquals(expecteds.get(i).get("contractNo"), targets.get(i).get("contractNo"));
      // 契約者名
      assertEquals(expecteds.get(i).get("contractNameKnj"), targets.get(i).get("contractNameKnj"));
      // 契約者名(カナ)
      assertEquals(expecteds.get(i).get("contractNameKana"),
          targets.get(i).get("contractNameKana"));
      // 処理内容
      assertEquals(expecteds.get(i).get("transactionCode"), targets.get(i).get("transactionCode"));
      // 保全申請ステータス
      assertEquals(expecteds.get(i).get("requestStatus"), targets.get(i).get("requestStatus"));
    }
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
  @AfterEach
  void clean() throws ScriptException, SQLException {
    testScriptUtils.cleanUpDatabase();
  }
}
