package butterknife.compiler;

import butterknife.compiler.ButterKnifeProcessor;

import java.util.Arrays;
import javax.annotation.processing.Processor;

final class ProcessorTestUtilities {
  static Iterable<? extends Processor> butterknifeProcessors() {
    return Arrays.asList(
        new ButterKnifeProcessor()
    );
  }
}
