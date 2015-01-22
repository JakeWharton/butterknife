package butterknife.compiler;

import javax.annotation.processing.Processor;
import java.util.Arrays;

final class ProcessorTestUtilities {
  static Iterable<? extends Processor> butterknifeProcessors() {
    return Arrays.asList(
        new ButterKnifeProcessor()
    );
  }
}
