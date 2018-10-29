package lung.calculator.exception;

/**
 * @Author WAN, Kwok Lung
 */
public class NegativeNumberException extends RuntimeException {

    public NegativeNumberException(String numbers) {
        super("negatives not allowed: " + numbers);
    }
}
