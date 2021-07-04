public class Pair<T, U> {
    private final T _first;
    private final U _second;
    public Pair(T first, U second) {
        _first = first;
        _second = second;
    }
    public T getFirst() { return _first; }
    public U getSecond() { return _second; }
}
