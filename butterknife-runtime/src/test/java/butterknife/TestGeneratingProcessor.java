package butterknife;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.testing.compile.JavaFileObjects;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

/**
 * A test {@link javax.annotation.processing.Processor} that produces one file.
 *
 * To use it, include {@link #ANNOTATION} in your source set and
 * mark something with @PerformGeneration so the processor has something to latch onto
 */
public class TestGeneratingProcessor extends AbstractProcessor {

  public static final JavaFileObject ANNOTATION = JavaFileObjects.forSourceString("test.PerformGeneration", ""
          + "package test;\n"
          + "import java.lang.annotation.*;\n"
          + "@Target(ElementType.TYPE)\n"
          + "public @interface PerformGeneration {\n"
          + "}");

  private final String generatedClassName;
  private final String generatedSource;
  private boolean processed;

  TestGeneratingProcessor(String generatedClassName, String... source) {
    this.generatedClassName = generatedClassName;
    this.generatedSource = Joiner.on("\n").join(source);
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return ImmutableSet.of("test.PerformGeneration");
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    if (!processed) {
      processed = true;
      try (Writer writer = processingEnv.getFiler()
          .createSourceFile(generatedClassName)
          .openWriter()) {
        writer.append(generatedSource);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return false;
  }
}
