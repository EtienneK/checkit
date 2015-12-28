package com.etiennek.check.batch;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.etiennek.check.domain.model.Item;
import com.etiennek.check.domain.repo.ItemRepository;
import com.etiennek.check.integration.store.Extractor;
import com.etiennek.check.integration.store.StockStatus;
import com.etiennek.check.integration.store.bgcoza.BgCoZaExtractor;
import com.etiennek.check.integration.store.gamersquest.GamersQuestExtractor;
import com.etiennek.check.integration.store.loot.LootExtractor;
import com.etiennek.check.integration.store.raru.RaruExtractor;
import com.etiennek.check.integration.store.takealot.TakealotExtractor;
import com.etiennek.check.integration.store.timeless.TimelessExtractor;
import com.google.common.collect.ImmutableMap;

@Component
public class ExtractItems {

	private Logger log = LoggerFactory.getLogger(getClass());

	private @Autowired ItemRepository itemRepository;
	private @Autowired List<Extractor> extractors;

	private Map<Class<? extends Extractor>, Long> storeIndex = ImmutableMap.<Class<? extends Extractor>, Long> builder()
			.put(BgCoZaExtractor.class, 3L).put(TakealotExtractor.class, 1L).put(TimelessExtractor.class, 2L)
			.put(GamersQuestExtractor.class, 4L).put(LootExtractor.class, 5L).put(RaruExtractor.class, 6L).build();

	@Scheduled(fixedDelay = 3_600_000)
	public void extract() {
		log.info("Started with item extraction.");
		LocalDateTime startingTime = LocalDateTime.now();
		extractors.forEach(e -> {
			AtomicInteger count = new AtomicInteger();
			Long storeId = storeIndex.get(e.getClass());
			if (storeId == null) {
				throw new IllegalStateException(e.getClass() + " does not have a store index");
			}
			e.extract().subscribe(i -> {
				if (!i.isValid()) {
					log.error("Invalid item: " + i.getInvalidDetails(), i.getInvalidException());
					return;
				}
				log.debug(String.format("Got item #[%s] for extractor[%s]: %s", count.get(), e.getClass(), i));

				String id = storeId + "-" + i.getId();
				boolean inStock = i.getStockStatus() == StockStatus.OUT_OF_STOCK ? false : true;
				itemRepository.index(new Item(id, i.getId(), storeId, i.getName(), i.getUrl(), i.getPrice(),
						i.getNormalPrice(), inStock).audit(LocalDateTime.now()));
				count.getAndIncrement();
			} , ex -> log.error("Unknown error occured", ex), () -> {
				itemRepository.findByLastModifiedDateLessThanAndStoreIdAndInStockTrue(startingTime, storeId)
						.forEach(i -> {
					i.setInStock(false);
					itemRepository.index(i);
				});

				log.info(String.format("Done with extracting [%s] number of items for extractor [%s]", count.get(),
						e.getClass()));
			});
		});
	}
}
