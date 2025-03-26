package xyz.tomorrowlearncamp.count10shop.domain.payment.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import xyz.tomorrowlearncamp.count10shop.domain.common.exception.InvalidRequestException;
import xyz.tomorrowlearncamp.count10shop.domain.item.entity.Item;
import xyz.tomorrowlearncamp.count10shop.domain.item.enums.Category;
import xyz.tomorrowlearncamp.count10shop.domain.item.enums.Status;
import xyz.tomorrowlearncamp.count10shop.domain.item.service.ItemService;
import xyz.tomorrowlearncamp.count10shop.domain.payment.dto.response.PaymentResponseDto;
import xyz.tomorrowlearncamp.count10shop.domain.payment.entity.Payment;
import xyz.tomorrowlearncamp.count10shop.domain.payment.repository.PaymentRepository;

@ExtendWith(MockitoExtension.class)
class NoLockPaymentServiceTest {
	@InjectMocks
	private NoLockPaymentService noLockPaymentService;
	@Mock
	private ItemService itemService;
	@Mock
	private PaymentRepository paymentRepository;

	@Test
	void 상품_재고_O_판매_O_구매_성공() {
		// given
		Item item = new Item("옷", Category.CLOTHES, "", 10000L, 5L, Status.SALE);
		ReflectionTestUtils.setField(item, "id", 1L);

		Payment savedPayment = new Payment(1L, 1L, "옷", 10000L, 10000L);

		given(itemService.findItemByIdOrElseThrow(1L)).willReturn(item);
		given(paymentRepository.save(any(Payment.class))).willReturn(savedPayment);

		// when
		PaymentResponseDto dto = noLockPaymentService.purchaseItem(1L);

		// then
		assertNotNull(dto);
		assertEquals(item.getQuantity(), 4L);
	}

	@Test
	void 상품_재고_O_판매_X_구매_실패() {
		// given
		Item item = new Item("옷", Category.CLOTHES, "", 10000L, 5L, Status.NON_SALE);
		ReflectionTestUtils.setField(item, "id", 1L);

		given(itemService.findItemByIdOrElseThrow(1L)).willReturn(item);

		// when
		InvalidRequestException exception = assertThrows(InvalidRequestException.class,
			() -> noLockPaymentService.purchaseItem(1L));

		// then
		assertEquals("해당 상품은 판매하지 않습니다.", exception.getMessage());
	}

	@Test
	void 상품_재고_X_판매_X_구매_실패() {
		// given
		Item item = new Item("옷", Category.CLOTHES, "", 10000L, 0L, Status.SOLD_OUT);
		ReflectionTestUtils.setField(item, "id", 1L);

		given(itemService.findItemByIdOrElseThrow(1L)).willReturn(item);

		// when
		InvalidRequestException exception = assertThrows(InvalidRequestException.class,
			() -> noLockPaymentService.purchaseItem(1L));

		// then
		assertEquals("재고가 없습니다.", exception.getMessage());
	}

	@Test
	void 상품이_존재할땐_구매_O_존재하지_않을땐_구매_X() {
		// given
		Item item = new Item("옷", Category.CLOTHES, "", 10000L, 2L, Status.SALE);
		ReflectionTestUtils.setField(item, "id", 1L);

		Payment savedPayment = new Payment(1L, 1L, "옷", 10000L, 10000L);

		given(itemService.findItemByIdOrElseThrow(1L)).willReturn(item);
		given(paymentRepository.save(any(Payment.class))).willReturn(savedPayment);

		// when
		PaymentResponseDto dto = noLockPaymentService.purchaseItem(1L);

		// then
		assertNotNull(dto);
		assertEquals(item.getQuantity(), 1L);
		assertEquals(item.getStatus(), Status.SALE);

		// when
		PaymentResponseDto dto2 = noLockPaymentService.purchaseItem(1L);

		// then
		assertNotNull(dto2);
		assertEquals(item.getQuantity(), 0L);
		assertEquals(item.getStatus(), Status.SOLD_OUT);

		// when
		InvalidRequestException exception = assertThrows(InvalidRequestException.class,
			() -> noLockPaymentService.purchaseItem(1L));

		// then
		assertEquals("재고가 없습니다.", exception.getMessage());
	}

	@Test
	public void 동시성_테스트() throws InterruptedException {
		int testCount = 1000;
		Item item = new Item("옷", Category.CLOTHES, "", 10000L, Integer.toUnsignedLong(testCount), Status.SALE);
		ReflectionTestUtils.setField(item, "id", 1L);

		Payment savedPayment = new Payment(1L, 1L, "옷", 10000L, 10000L);

		given(itemService.findItemByIdOrElseThrow(1L)).willReturn(item);
		given(paymentRepository.save(any(Payment.class))).willReturn(savedPayment);

		ExecutorService executorService = Executors.newFixedThreadPool(10);
		CountDownLatch latch = new CountDownLatch(testCount);

		AtomicInteger successfulUpdates = new AtomicInteger(testCount);

		for (int i = 0; i < testCount; i++) {
			executorService.submit(() -> {
				try {
					noLockPaymentService.purchaseItem(1L);
					successfulUpdates.decrementAndGet();
				} catch (Exception e) {
					System.out.println("예외 발생: " + e.getMessage());
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await(); // 모든 스레드가 작업을 완료할 때까지 대기

		int finalCount = item.getQuantity().intValue();

		// 락을 사용하지 않았기 때문에 동시성 문제가 발생할 수 있으며,
		// 성공한 업데이트 수와 count 값이 다를 수 있음
		// assertEquals(successfulUpdates.get(), finalCount);
		assertNotEquals(successfulUpdates.get(), finalCount);
	}
}