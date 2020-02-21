import java.util.Random;

public class ControlledRandom extends Random {
    private int[] values;
    private int lastIndex;

    public ControlledRandom(int[] values) {
        if (values.length == 0) {
            throw new IllegalArgumentException("Controlled random should contain some values");
        }
        this.values = values;
        this.lastIndex = 0;
    }

    @Override
    public int nextInt(int bound) {
        int next = values[lastIndex];
        lastIndex++;
        if (lastIndex >= values.length) {
            lastIndex = 0;
        }
        return next % bound;
    }
}
