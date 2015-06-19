package com.etiennek.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class Utils {

  /**
   * Shorthand for creating an immutable map builder. Just makes it a bit cleaner when creating
   * attributes, models, etc.
   * 
   * @return
   */
  public static Builder<String, Object> m() {
    return ImmutableMap.<String, Object>builder();
  }
}
