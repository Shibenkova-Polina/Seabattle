package ru.game.seabattle.process;

import ru.game.seabattle.elements.Cell;
import ru.game.seabattle.elements.CellState;
import ru.game.seabattle.elements.Field;
import ru.game.seabattle.elements.Ship;
import ru.game.seabattle.persistence.DBPersistence;
import ru.game.seabattle.players.Computer;

import java.util.ArrayList;

public class ArrangementOfShips {
    private static final int NUMBER_OF_ORIENTATIONS = 2;
    private static final int NUMBER_OF_SHIP_TYPES = 4;
    private static final int INDEX_OF_ORIENTATIONS = 3;
    private static final int INDEX_OF_SHIP_SIZE = 2;

    private Cell[][] placementField;

    private static ArrangementOfShips instance;
    private int idPlacements = 0;

    public enum Orientation {
        HORIZONTAL,
        VERTICAL
    }

    public synchronized static ArrangementOfShips getInstance() {
        if (instance == null) {
            instance = new ArrangementOfShips();
        }
        return instance;
    }

    public void setIdPlacements(int num) {
        idPlacements = num;
    }

    public int getIdPlacements() {
        return idPlacements;
    }

    public void setPlacementField(Cell[][] placementField) {
        this.placementField = placementField;
    }

    public void createShips(Field field) {
        for (int shipSize = NUMBER_OF_SHIP_TYPES; shipSize >= 1 ; --shipSize) {
            int shipsCount = NUMBER_OF_SHIP_TYPES - shipSize + 1;

            for (int i = 0; i < shipsCount; ++i) {
                Orientation orientation;
                int x;
                int y;

                do {
                    int random = (int) (Math.random() * NUMBER_OF_ORIENTATIONS);

                    if (random == 0) {
                        orientation = Orientation.HORIZONTAL;
                    } else {
                        orientation = Orientation.VERTICAL;
                    }

                    if (orientation == Orientation.VERTICAL) {
                        x = (int) (Math.random() * (Field.FIELD_SIZE - shipSize + 1));
                        y = (int) (Math.random() * Field.FIELD_SIZE);
                    } else {
                        x = (int) (Math.random() * Field.FIELD_SIZE);
                        y = (int) (Math.random() * (Field.FIELD_SIZE - shipSize + 1));
                    }
                } while (!validPlace(x, y, shipSize, orientation));

                createShip(x, y, shipSize, orientation);

                String orient;
                if (orientation == Orientation.VERTICAL) {
                    orient = "VERTICAL";
                } else {
                    orient = "HORIZONTAL";
                }

                String str;
                if (!Computer.getInstance().getCreationBD()) {
                    str = "Computer";
                } else {
                    str = "Human";
                }

                idPlacements += 1;
                DBPersistence dbPersistence = new DBPersistence();

                dbPersistence.createShips(idPlacements, str, shipSize, x, y, orient);

            }
        }

        field.setCells(placementField);
        placementField = null;
    }

    private boolean validPlace(int x, int y, int size, Orientation orientation) {
        int xFrom = x - 1;
        int yFrom = y - 1;
        int xTo;
        int yTo;

        if (orientation == Orientation.VERTICAL) {
            xTo = x + size;
            yTo = y + 1;

            if (xTo > Field.FIELD_SIZE) {
                return false;
            }
        } else {
            xTo = x + 1;
            yTo = y + size;

            if (yTo > Field.FIELD_SIZE) {
                return false;
            }
        }

        if (xFrom < 0) {
            xFrom = 0;
        }

        if (yFrom < 0) {
            yFrom = 0;
        }

        if (yTo > Field.FIELD_SIZE - 1) {
            yTo = Field.FIELD_SIZE - 1;
        }

        if (xTo > Field.FIELD_SIZE - 1) {
            xTo = Field.FIELD_SIZE - 1;
        }

        for (int i = xFrom; i <= xTo; i++) {
            for (int j = yFrom; j <= yTo; j++) {
                if (placementField[j][i].isShip()) {
                    return false;
                }
            }
        }

        return true;
    }

    public void createShip(int x, int y, int size, Orientation orientation) {
        int xTo;
        int yTo;

        if (orientation == Orientation.VERTICAL) {
            xTo = x + size - 1;
            yTo = y;
        } else {
            xTo = x;
            yTo = y + size - 1;
        }

        ArrayList<Cell> cells = new ArrayList<>();
        ArrayList<Cell> borders = new ArrayList<>();
        Ship ship = new Ship();

        for (int i = x; i <= xTo; i++) {
            for (int j = y; j <= yTo; j++) {
                placementField[j][i].inititalizeState(CellState.SHIP);
                placementField[j][i].setShip(ship);
                cells.add(placementField[j][i]);
            }
        }

        for (int k = x - 1; k <= xTo + 1; k++) {
            for (int l = y - 1; l <= yTo + 1; l++) {
                if (k >= 0 && k < Field.FIELD_SIZE && l >= 0 && l < Field.FIELD_SIZE) {
                    if (placementField[l][k].getState() != CellState.SHIP) {
                        if (!borders.contains(placementField[l][k])) {
                            borders.add(placementField[l][k]);
                        }
                    }
                }
            }
        }

        ship.setCells(cells);
        ship.setBorders(borders);
    }

    public void createPrevShips(Field field, String[] data) {
        for (int i = 0; i < Field.FIELD_SIZE; i++) {
            String str = data[i];
            String[] coord = str.split(" ");
            int x = Integer.parseInt(coord[0]);
            int y = Integer.parseInt(coord[1]);
            int size = Integer.parseInt(coord[INDEX_OF_SHIP_SIZE]);
            ArrangementOfShips.Orientation orient = ArrangementOfShips.Orientation.valueOf(coord[INDEX_OF_ORIENTATIONS]);
            createShip(x, y, size, orient);
        }

        field.setCells(placementField);
        placementField = null;
    }
}