package butterknife.internal;

import java.util.Arrays;

import javax.annotation.processing.Processor;

/**
 * Test utilities.
 */
/* package */class ProcessorTestUtilities {

  /* package */
  static Iterable<? extends Processor> butterknifeProcessors() {
    return Arrays.asList(
        new InjectViewProcessor()
    );
  }
}
