
package com.kakaopay.paymoneysprinkle.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaopay.paymoneysprinkle.consts.Constants;
import com.kakaopay.paymoneysprinkle.domain.Response;
import com.kakaopay.paymoneysprinkle.service.SprinkleEventService;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class SprinkleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SprinkleEventService sprinkleEventService;

    private Long userId;
    private String roomId;
    private String token;

    @BeforeEach
    void setUp() {
        this.token = "18r";
        this.userId = 12345L;
        this.roomId = "r0003";
    }

    @DisplayName("뿌리기API 동작 확인")
    @Test
    public void testCreateSprinkleEvent() throws Exception {

        // given
        Map<String, String> params = new HashMap<>();
        params.put("totalAmount", "50000");
        params.put("totalEventCount", "4");
        String content = objectMapper.writeValueAsString(params);

        // when
        final ResultActions actions = mockMvc.perform(post("/v1/sprinkle")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("X-USER-ID", "14567")
                .header("X-ROOM-ID", "r98")
                .content(content));

        // then
        actions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.code", is(Constants.ResponseCode.SUCCESS)))
                .andDo(print());
    }

    @DisplayName("받기 API 동작 확인")
    @Test
    public void testPickUpPayMoney() throws Exception {

        // given
        String senderId = "14567";
        String userId = "12345";
        String roomId = "r98";

        Map<String, String> params = new HashMap<>();
        params.put("totalAmount", "50000");
        params.put("totalCount", "4");

        MvcResult result = mockMvc.perform(post("/v1/sprinkle")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("X-USER-ID", senderId)
                .header("X-ROOM-ID", roomId)
                .content(objectMapper.writeValueAsString(params)))
                .andReturn();

        // get token
        Response res = objectMapper.readValue(result.getResponse().getContentAsString(), Response.class);
        Map<String, String> resultMap = (Map<String, String>) res.getResult();

        String token = resultMap.get("token");

        // when 일부수정이기때문에 patch 명령어로 변경
/*
        final ResultActions actions = mockMvc.perform(patch("/v1/sprinkle")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("X-USER-ID", userId)
                .header("X-ROOM-ID", roomId)
                .header("X-TOKEN", token));

        // then
        actions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.code", is(Constants.ResponseCode.SUCCESS)))
                .andDo(print());
                */

    }

    @DisplayName("조회 API 동작 확인")
    @Test
    public void viewSprinkleHistory() throws Exception {



        String userId = "12345";
        String roomId = "r98";

        Map<String, String> params = new HashMap<>();
        params.put("totalAmount", "50000");
        params.put("totalCount", "4");

        MvcResult result = mockMvc.perform(post("/v1/sprinkle")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("X-USER-ID", userId)
                .header("X-ROOM-ID", roomId)
                .content(objectMapper.writeValueAsString(params)))
                .andReturn();

        Response res = objectMapper.readValue(result.getResponse().getContentAsString(), Response.class);
        Map<String, String> resultMap = (Map<String, String>) res.getResult();

        String token = resultMap.get("token");

        mockMvc.perform(get("/v1/kpay/describe")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("X-USER-ID", userId)
                .header("X-ROOM-ID", roomId)
                .header("X-TOKEN", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.code", is(Constants.ResponseCode.SUCCESS)))
                .andDo(print());
    }

}