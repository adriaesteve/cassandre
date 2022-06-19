package cat.adria.tradingbot;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import tech.cassandre.trading.bot.dto.position.PositionStatusDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.dto.util.GainDTO;
import tech.cassandre.trading.bot.test.mock.TickerFluxMock;

/**
 * Simple strategy test.
 */
@SpringBootTest
@Import(TickerFluxMock.class)
@DisplayName("Simple strategy test")
public class SimpleStrategyTest {

	@Autowired
	private TickerFluxMock tickerFluxMock;

	/** Dumb strategy. */
	@Autowired
	private SimpleStrategy strategy;

	private final String USDT = "USDT";
	
	/**
	 * Check data reception.
	 */
	@Test
	@DisplayName("Check strategy behavioir")
	public void checkStrategy() {
		await().forever().until(() -> tickerFluxMock.isFluxDone());

		// =============================================================================================================
		System.out.println("");
		System.out.println("Gains by position");
		strategy.getPositions().values().forEach(positionDTO -> {
			if (positionDTO.getStatus().equals(PositionStatusDTO.CLOSED)) {
				System.out.println(
						"Position " + positionDTO.getPositionId() + " closed with gain: " + positionDTO.getGain());
			} else {
				System.out.println("Position " + positionDTO.getPositionId() + " NOT closed with latest gain: "
						+ positionDTO.getLatestCalculatedGain().get());
			}
		});

		// =============================================================================================================
		System.out.println("");
		System.out.println("Global gains");
		Map<CurrencyDTO, GainDTO> gains = strategy.getGains();
		gains.values().forEach(gainDTO -> System.out.println(gainDTO.getAmount()));
		assertFalse(gains.isEmpty(), "Failure, no gains");
		assertNotNull(gains.get(USDT), "Failure, USDT gains");
		assertTrue(gains.get(USDT).isSuperiorTo(GainDTO.ZERO), "Failure, USDT inferior to zero");
	}

}
