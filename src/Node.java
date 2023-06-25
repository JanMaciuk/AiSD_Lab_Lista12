public class Node {
    protected int frequency;
    protected Character symbol;
    protected Node left;
    protected Node right;

    public Node(int frequency, Character symbol) {
        this.frequency = frequency;
        this.symbol = symbol;
    }

    @Override // Jeżeli węzeł przechowuje ten sam symbol to jest identyczny, używane przy budowie ArrayList bez powtórzeń.
    public boolean equals(Object obj) {
        if (obj instanceof Node node) {
            return this.symbol.equals(node.symbol);
        }
        return false;
    }
}
