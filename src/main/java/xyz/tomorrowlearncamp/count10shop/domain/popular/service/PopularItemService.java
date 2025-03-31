package xyz.tomorrowlearncamp.count10shop.domain.popular.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

import lombok.RequiredArgsConstructor;
import xyz.tomorrowlearncamp.count10shop.domain.item.entity.Item;

@Service
@RequiredArgsConstructor
@EnableSchedulerLock(defaultLockAtMostFor = "PT10M")
public class PopularItemService {

	private static final String RANK = "ranking";
	private final RedisTemplate<String, String> redisTemplate;

	public void updateViews(Item item, Long userId) {
		String item_index = RANK + "_" + item.getItemName() + "_" + item.getId();
		String userKey = "" + userId;

		// 이미 조회수를 올린 유저
		Boolean isViewer = redisTemplate.opsForSet().isMember(item_index, userKey);
		if (Boolean.TRUE.equals(isViewer)) {
			return;
		}

		// 미조회 유저
		redisTemplate.opsForSet().add(item_index, userKey);

		// 각 아이템의 조회수
		redisTemplate.opsForZSet().incrementScore(RANK, item_index, 1);
	}

	public Map<String, Long> getRanking() {
		Set<TypedTuple<String>> resultSet = redisTemplate.opsForZSet().reverseRangeWithScores(RANK, 0, 4);

		// 결과를 Map<String, Long> 형태로 변환
		Map<String, Long> itemScores = new HashMap<>();
		if (resultSet != null) {
			for (TypedTuple<String> tuple : resultSet) {
				if (tuple.getValue() != null) {
					itemScores.put(tuple.getValue(), tuple.getScore().longValue()); // Object → String 변환
				}
			}
		}
		return itemScores;
	}

	@Scheduled(cron = "0 0 0 * * *") // 매일 00:00 실행
	@SchedulerLock(name = "resetViewCounts", lockAtMostFor = "PT5M", lockAtLeastFor = "PT1M")
	public void resetViewCounts() {
		redisTemplate.delete(RANK); // ZSet 삭제하여 초기화

		Set<String> userKeys = redisTemplate.keys(RANK + "*");
		if (!userKeys.isEmpty()) {
			for (String str : userKeys) {
				redisTemplate.delete(str); // 유저 조회 기록 삭제
			}
		}
	}
}

