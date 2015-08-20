package com.etiennek.check.integration.store;

import static com.jayway.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;

import com.etiennek.check.integration.store.bgcoza.BgCoZaExtractor;
import com.etiennek.check.integration.store.gamersquest.GamersQuestExtractor;
import com.etiennek.check.integration.store.loot.LootExtractor;
import com.etiennek.check.integration.store.raru.RaruExtractor;
import com.etiennek.check.integration.store.takealot.TakealotExtractor;
import com.etiennek.check.integration.store.timeless.TimelessExtractor;

public class AllExtractorsTests {

	private Log log = LogFactory.getLog(getClass());

	// @Test
	public void TakealotExtractor_Should_be_able_to_extract_Items() {
		baseTest(new TakealotExtractor(), 450, 60);
	}

	// @Test
	public void TimelessExtractor_Should_be_able_to_extract_Items() {
		baseTest(new TimelessExtractor(), 100, 20);
	}

	// @Test
	public void BgCoZaExtractor_Should_be_able_to_extract_Items() {
		baseTest(new BgCoZaExtractor(), 160, 60);
	}

	// @Test
	public void GamersQuestExtractor_Should_be_able_to_extract_Items() {
		baseTest(new GamersQuestExtractor(), 350, 60);
	}

	// @Test
	public void LootExtractor_Should_be_able_to_extract_Items() {
		baseTest(new LootExtractor(), 200, 60);
	}

	@Test
	public void RaruExtractor_Should_be_able_to_extract_Items() {
		baseTest(new RaruExtractor(), 400, 60);
	}

	public void baseTest(Extractor toTest, int minItems, long waitForSeconds) {
		AtomicInteger count = new AtomicInteger(0);
		AtomicBoolean done = new AtomicBoolean(false);

		toTest.extract().subscribe((item) -> {
			log.info("OnNext: " + count.get() + ": " + item);
			if (!item.isValid()) {
				throw new IllegalStateException("Invalid Item");
			}
			count.incrementAndGet();
		} , ex -> {
			log.error("OnError: ", ex);
		} , () -> {
			log.info(String.format("OnDone [%s]", toTest.getClass()));
			done.set(true);
		});

		await().atMost(waitForSeconds, SECONDS).until(() -> Assert.assertTrue(done.get()));
		Assert.assertTrue(count.get() >= minItems);
	}

}
