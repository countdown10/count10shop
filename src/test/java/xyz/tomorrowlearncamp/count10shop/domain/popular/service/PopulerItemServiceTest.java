package xyz.tomorrowlearncamp.count10shop.domain.popular.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ZSetOperations;

import xyz.tomorrowlearncamp.count10shop.domain.item.entity.Item;
import xyz.tomorrowlearncamp.count10shop.domain.item.enums.Category;
import xyz.tomorrowlearncamp.count10shop.domain.item.enums.Status;

@ExtendWith(MockitoExtension.class)
class PopularItemServiceTest {

	@InjectMocks
	private PopularItemService popularItemService;

	@Mock
	private RedisTemplate<String, String> redisTemplate;

	@Mock
	private SetOperations<String, String> setOperations;

	@Mock
	private ZSetOperations<String, String> zSetOperations;

	@Test
	@DisplayName("미조회 유저의 조회")
	void updateViews_success() {
		//given
		given(redisTemplate.opsForSet()).willReturn(setOperations);
		given(redisTemplate.opsForZSet()).willReturn(zSetOperations);
		given(setOperations.isMember(anyString(), anyString())).willReturn(false);
		given(setOperations.add(anyString(), anyString())).willReturn(1L);
		given(zSetOperations.incrementScore(anyString(), anyString(), anyDouble())).willReturn(1.0);

		Item item = new Item("item", Category.BOOK, "description", 1000L, 10L, Status.SALE);
		//when
		popularItemService.updateViews(item, 1L);

		//then
		verify(setOperations, times(1)).add(anyString(), anyString());
		verify(zSetOperations, times(1)).incrementScore(anyString(), anyString(), anyDouble());
	}

	@Test
	@DisplayName("이미 조회한 유저의 조회")
	void updateViews_failed() {
		// given
		given(redisTemplate.opsForSet()).willReturn(setOperations);
		given(setOperations.isMember(anyString(), anyString())).willReturn(true);

		Item item = new Item("item", Category.BOOK, "description", 1000L, 10L, Status.SALE);
		String item_index = "ranking" + "_" + item.getItemName() + "_" + item.getId();
		String userKey = "1";

		// when
		popularItemService.updateViews(item, 1L);

		// then
		verify(setOperations, times(1)).isMember(eq(item_index), anyString());
		verify(setOperations, never()).add(eq(item_index), eq(userKey));
	}

	@Test
	@DisplayName("빈 랭킹을 가져온다.")
	void getRanking_null() {
		//given
		Map<String, Long> itemScores = new HashMap<>();

		given(redisTemplate.opsForZSet()).willReturn(zSetOperations);
		given(zSetOperations.reverseRangeWithScores("ranking", 0, 4)).willReturn(new HashSet<>());

		// when
		itemScores = popularItemService.getRanking();

		// then
		assertEquals(new HashMap<>(), itemScores);
	}

	@Test
	@DisplayName("인기순위를 초기화한다.")
	void resetViewCounts_all() {
		//given
		Set<String> key = new HashSet<>();
		key.add("ranking_item_1");
		key.add("ranking_item_2");

		given(redisTemplate.keys(anyString())).willReturn(key);

		//when
		popularItemService.resetViewCounts();

		//then
		verify(redisTemplate, times(3)).delete(anyString());
	}

	@Test
	@DisplayName("인기순위가 없는 경우 초기화")
	void resetViewCounts_empty() {
		//given
		given(redisTemplate.keys(anyString())).willReturn(new HashSet<>());

		//when
		popularItemService.resetViewCounts();

		//then
		verify(redisTemplate, times(1)).delete(anyString());
	}
}