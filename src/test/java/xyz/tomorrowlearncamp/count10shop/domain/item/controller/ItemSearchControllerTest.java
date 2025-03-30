package xyz.tomorrowlearncamp.count10shop.domain.item.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import xyz.tomorrowlearncamp.count10shop.domain.item.service.ItemSearchService;

@WebMvcTest(ItemSearchController.class)
public class ItemSearchControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ItemSearchService itemSearchService;

	@Test
	@DisplayName("keyword가 @NotBlank 검증에 실패한다")
	void search_shouldReturnBadRequest_whenKeywordIsBlank() throws Exception {
		String blankKeyword = " ";

		ResultActions result = mockMvc.perform(
			get("/api/v1/search")
				.param("keyword", blankKeyword)
				.param("page", "1")
				.param("size", "10")
		);

		result.andExpect(status().isBadRequest());
	}
}
