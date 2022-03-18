package ru.ominit.journey;

public class ShortProgress {
    private final String haystackId;
    private final int maxProgress;
    private final int currentProgress;

    public int getCurrentProgress() {
        return currentProgress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public String getHaystackId() {
        return haystackId;
    }

    public ShortProgress(String haystackId, int maxProgress, int currentProgress) {
        this.haystackId = haystackId;
        this.maxProgress = maxProgress;
        this.currentProgress = currentProgress;
    }
}
