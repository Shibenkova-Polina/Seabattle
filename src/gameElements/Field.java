package gameElements;

import gameProcess.ArrangementOfShips;

public class Field {
    public static final int FIELD_SIZE = 10;

    private Cell[][] cells = new Cell[FIELD_SIZE][FIELD_SIZE];

    public Field() {
        for (int i = 0; i < FIELD_SIZE; i++) {
            for (int j = 0; j < FIELD_SIZE; j++) {
                cells[j][i] = new Cell(j, i, Cell.CellState.SEA, null);
            }
        }

        ArrangementOfShips.setPlacementField(cells);
        ArrangementOfShips.createShips(this);
    }

    public Cell[][] getCells() {
        return cells;
    }

    public void setCells(Cell[][] cells) {
        this.cells = cells;
    }

    public void resetCells(){
        for (int i = 0; i < FIELD_SIZE; i++) {
            for (int j = 0; j < FIELD_SIZE; j++) {
                cells[j][i].setState(Cell.CellState.SEA);
            }
        }

        ArrangementOfShips.setPlacementField(cells);
        ArrangementOfShips.createShips(this);
    }
}
