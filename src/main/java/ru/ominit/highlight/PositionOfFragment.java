package ru.ominit.highlight;

public class PositionOfFragment {
    public final int grainIndex;
    public final int startInWheat;
    public final int startInGrain;

    public PositionOfFragment(int grainIndex, int startInWheat, int startInGrain) {
        this.grainIndex = grainIndex;
        this.startInWheat = startInWheat;
        this.startInGrain = startInGrain;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PositionOfFragment that = (PositionOfFragment) o;

        if (grainIndex != that.grainIndex) return false;
        if (startInWheat != that.startInWheat) return false;
        return startInGrain == that.startInGrain;
    }

    @Override
    public int hashCode() {
        int result = grainIndex;
        result = 31 * result + startInWheat;
        result = 31 * result + startInGrain;
        return result;
    }

    @Override
    public String toString() {
        return "PositionOfFragment{" +
                "grainIndex=" + grainIndex +
                ", startInWheat=" + startInWheat +
                ", startInGrain=" + startInGrain +
                '}';
    }
}
