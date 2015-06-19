package com.etiennek.check.integration.store;

import static com.jayway.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.etiennek.check.integration.store.takealot.TakealotExtractor;
import com.google.common.base.Throwables;

public class TakealotExtractorTest {

  private TakealotExtractor toTest = new TakealotExtractor();

  @Before
  public void init() {

  }

  @Test
  public void Should_be_able_to_extract_Items() {
    AtomicInteger count = new AtomicInteger(0);
    AtomicBoolean done = new AtomicBoolean(false);

    toTest.extract()
          .subscribe((item) -> {
            System.out.println(count.get() + ": " + item);
            count.incrementAndGet();
          }, ex -> {
            throw Throwables.propagate(ex);
          }, () -> {
            done.set(true);
          });

    await().atMost(20, SECONDS)
           .until(() -> Assert.assertTrue(done.get()));
    Assert.assertTrue(count.get() > 200);
  }
}
