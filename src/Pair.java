public class Pair<K, V> {
    private K first;
    private V second;

    public Pair(K first, V second) {
        super();
        this.first = first;
        this.second = second;
    }

    public int hashCode() {
        int hashFirst = first != null ? first.hashCode() : 0;
        int hashSecond = second != null ? second.hashCode() : 0;
        return (hashFirst + hashSecond) * hashSecond + hashFirst;
    }

    public boolean equals(Object other) {
        if (other instanceof Pair) {
            Pair otherPair = (Pair) other;
            return ((this.first == otherPair.first ||
                    (this.first != null && otherPair.first != null &&
                            this.first.equals(otherPair.first))) &&
                    (this.second == otherPair.second ||
                            (this.second != null && otherPair.second != null &&
                                    this.second.equals(otherPair.second))));
        }
        return false;
    }

    public String toString() {
        return "(" + first + ", " + second + ")";
    }

    public K getFirst() {
        return first;
    }

    public K setFirst(K first) {
        K oldFirst = this.first;
        this.first = first;
        return oldFirst;
    }

    public V getSecond() {
        return second;
    }

    public V setSecond(V second) {
        V oldSecond = this.second;
        this.second = second;
        return oldSecond;
    }
}
