package borkowski.rc_controller;

public class CarMove {
    private int turn;
    private int straight;

    public int getStraight() {
        return straight;
    }
    public int getTurn() {
        return turn;
    }

    public CarMove(int turn, int straight) {
        this.turn = turn;
        this.straight = straight;
    }

    @Override
    public String toString() {
        return "CarMove{" +
                "turn=" + turn +
                ", straight=" + straight +
                '}';
    }
}
