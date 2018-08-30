package hash.include.ui;

public interface RiseNumberBase {
    public void start();
    public hash.include.ui.RiseNumberTextView withNumber(float number);
    public hash.include.ui.RiseNumberTextView withNumber(int number);
    public hash.include.ui.RiseNumberTextView setDuration(long duration);
    public void setOnEnd(hash.include.ui.RiseNumberTextView.EndListener callback);
}
