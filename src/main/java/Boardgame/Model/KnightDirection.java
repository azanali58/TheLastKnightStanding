package Boardgame.Model;

public enum KnightDirection implements Direction{
    /** Left up direction of knight. */
    LEFT_UP(-1, -2),
    /** Up left direction of knight.*/
    UP_LEFT(-2, -1),
    /** Up right direction of knight.*/
    UP_RIGHT(-2, 1),
    /** Right up direction of knight. */
    RIGHT_UP(-1, 2),
    /** Right down direction of knight. */
    RIGHT_DOWN(1, 2),
    /** Down right direction of knight. */
    DOWN_RIGHT(2, 1),
    /** Down left direction of knight. */
    DOWN_LEFT(2, -1),
    /** Left down direction of knight. */
    LEFT_DOWN(1, -2);

    private final int rowChange;

    private final int colChange;

    KnightDirection(int rowChange, int colChange) {
        this.rowChange = rowChange;
        this.colChange = colChange;
    }

    public int getRowChange() {
        return rowChange;
    }

    public int getColChange() {
        return colChange;
    }

    public static KnightDirection of(int rowChange, int colChange) {
        for (var direction : values()) {
            if (direction.rowChange == rowChange && direction.colChange == colChange) {
                return direction;
            }
        }
        throw new IllegalArgumentException();
    }

}
