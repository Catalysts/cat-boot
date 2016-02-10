package cc.catalysts.boot.thymeleaf.webjars;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Klaus Lehner
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {WebjarsDialectIntegrationTest.Context.class})
public class WebjarsDialectIntegrationTest {

    @Autowired
    WebApplicationContext webApplicationContext;

    MockMvc mockMvc;

    @Before
    public void before() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void resolveVars() throws Exception {
        final ResultActions resultActions = mockMvc.perform(get("/webjarstest")).andExpect(status().isOk());
        String response = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(response).contains("<link rel=\"stylesheet\" href=\"/webjars/bootstrap/3.3.5/dist/css/bootstrap.min.css\" />");
        assertThat(response).contains("<script src=\"/webjars/jquery/2.2.0/dist/jquery.min.js\"></script>");
        assertThat(response).contains("<script src=\"/webjars/bootstrap/3.3.5/dist/js/bootstrap.min.js\"></script>");
    }

    @Configuration
    @EnableAutoConfiguration
    public static class Context {

        @Bean
        public WebjarRegistrar wpcWebjars() {
            return () -> Webjars.webjars;
        }

        @Bean
        public Controller controller() {
            return new Controller();
        }

    }

    @org.springframework.stereotype.Controller
    public static class Controller {

        @RequestMapping("/webjarstest")
        public String webjarstest() {
            return "webjarstest";
        }
    }
}