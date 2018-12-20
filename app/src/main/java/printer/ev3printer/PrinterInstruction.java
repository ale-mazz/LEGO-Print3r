package printer.ev3printer;

public class PrinterInstruction {
    public enum Direction{
        FORWARD,
        LEFT,
        RIGHT,
        POINT
    }
    private Direction direction;
    private int amount;

    public PrinterInstruction(Direction direction, int amount){
        this.direction = direction;
        this.amount = amount;
    }
    public Direction getDirection() {
        return direction;
    }
    public int getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        String name = "Dir: ";
        switch (direction) {
            case FORWARD:
                name += "FORWARD";
                break;
            case LEFT:
                name += "LEFT";
                break;
            case POINT:
                name += "POINT";
                break;
            case RIGHT:
                name += "RIGHT";
                break;
            default:
                name += "NONE";
                break;
        }
        return name + " amount: " + amount;
    }
}
