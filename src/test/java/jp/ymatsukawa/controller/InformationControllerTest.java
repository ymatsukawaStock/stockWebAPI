package jp.ymatsukawa.controller;

import jp.ymatsukawa.stockapi.controller.InformationController;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InformationControllerTest {
  private MockMvc mvc;

  @Before
  public void before() throws Exception {
    this.mvc = MockMvcBuilders.standaloneSetup(new InformationController()).build();
  }

  @Test
  public void test_Get_Subject_OK() throws Exception {
    long limit = 20;
    this.mvc.perform(get("/information?limit={limit}", limit))
            .andExpect(status().isOk());
  }
}
