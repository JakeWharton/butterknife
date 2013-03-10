package butterknife;

/**
 * User: Nicolas PICON
 * Date: 09/03/13 - 21:02
 */
public class UnableToInjectException extends RuntimeException  {
  UnableToInjectException(String message, Throwable cause) {
    super(message, cause);
  }
}
