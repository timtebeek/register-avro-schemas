package com.github.timtebeek.avro;

import org.apache.avro.specific.SpecificRecordBase;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

	@Test
	void roundtripByteBufferAndCompareToString() {
		var order = Order.newBuilder()
				.setOrderId(UUID.randomUUID())
				.setPrice(Price.newBuilder()
						.setValue(BigDecimal.valueOf(1234567890L, 3))
						.build())
				.build();
		String asString = toAndFromByteBufferToString(order);
		assertThat(asString).isEqualToIgnoringWhitespace("""
				{
					"orderId": %s,
					"price": {
						"value": 1234567.890
					}
				}""".formatted(order.getOrderId()));
	}

	/**
	 * Serialize to ByteBuffer & read from ByteBuffer, after which convert to String.
	 * This would trigger any invalid value serialization issues such as mismatched decimal scale and precision.
	 * 
	 * @param record
	 * @return
	 */
	private static String toAndFromByteBufferToString(SpecificRecordBase record) {
		try {
			var toByteBuffer = record.getClass().getMethod("toByteBuffer");
			var fromByteBuffer = record.getClass().getMethod("fromByteBuffer", ByteBuffer.class);
			ByteBuffer result = (ByteBuffer) toByteBuffer.invoke(record);
			return fromByteBuffer.invoke(record, result).toString();
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

}
