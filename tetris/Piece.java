package tetris;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class Piece {
    private final int[][] coords;
    private final int baseX, baseY;
    private final Color color;
    private final int rotateIndex;

    public Piece(int[][] coords, Color color, int rotateIndex) {
        this.coords = coords;
        this.color = color;
        this.rotateIndex = rotateIndex;
        this.baseX = coords[rotateIndex][1];
        this.baseY = coords[rotateIndex][0];
    }

    public int[][] getCoords() {
        return coords;
    }

    public Color getColor() {
        return color;
    }

    public Piece toErase() {
        return new Piece(coords, null, rotateIndex);
    }

    public Piece downOne() {
        int[][] newCoords = new int[coords.length][coords.length == 0 ? 0 : coords[0].length];
        for (int i = 0;i < coords.length; i++) {
            newCoords[i] = Arrays.copyOf(coords[i], coords[i].length);
            newCoords[i][0] += 1;
        }
        return new Piece(newCoords, color, rotateIndex);
    }

    public Piece rotateLeft() {
        int[][] newCoords = new int[coords.length][coords.length == 0 ? 0 : coords[0].length];
        for (int i = 0; i < coords.length; i++) {
            newCoords[i] = Arrays.copyOf(coords[i], coords[i].length);
            int y = newCoords[i][0] - baseY;
            int x = newCoords[i][1] - baseX;
            newCoords[i][0] = baseY + x;
            newCoords[i][1] = baseX - y;
        }
        return new Piece(newCoords, color, rotateIndex);
    }

    public Piece moveLeft() {
        int[][] newCoords = new int[coords.length][coords.length == 0 ? 0 : coords[0].length];
        for (int i = 0;i < coords.length; i++) {
            newCoords[i] = Arrays.copyOf(coords[i], coords[i].length);
            newCoords[i][1] -= 1;
        }
        return new Piece(newCoords, color, rotateIndex);
    }

    public Piece moveRight() {
        int[][] newCoords = new int[coords.length][coords.length == 0 ? 0 : coords[0].length];
        for (int i = 0;i < coords.length; i++) {
            newCoords[i] = Arrays.copyOf(coords[i], coords[i].length);
            newCoords[i][1] += 1;
        }
        return new Piece(newCoords, color, rotateIndex);
    }

    public boolean coordNotInPiece(int row, int x) {
        for (int[] coord: coords) {
            if (coord[0] == row && coord[1] == x) {
                return false;
            }
        }
        return true;
    }

    public Piece dropDown(Color[][] grid) {
        ArrayList<Integer> bottomIndices = new ArrayList<>(4);
        int maxDrop = Integer.MAX_VALUE;
        for (int i = 0;i < coords.length; i++) {
            boolean hasCoordBelow = false;
            for (int j = 0; j < coords.length; j++) {
                if (i != j && coords[j][0] > coords[i][0]) {
                    hasCoordBelow = true;
                    break;
                }
            }
            if (!hasCoordBelow) {
                bottomIndices.add(i);
                maxDrop = Math.min(maxDrop, grid.length - 1 - coords[i][0]);
            }
        }

        for (int x = 0; x < grid[0].length; x++) {
            for (int row = 0; row < grid.length; row++) {
                if (grid[row][x] != null) {
                    if (coordNotInPiece(row, x)) {
                        int finalX = x;
                        Optional<Integer> found = bottomIndices.stream().filter(i -> coords[i][1] == finalX).findFirst();
                        if (found.isPresent()) {
                            int drop = row - 1 - coords[found.get()][0];
                            if (drop < maxDrop) {
                                maxDrop = Math.max(drop, 0);
                            }
                        }
                        break;
                    }
                }
            }
        }
        if (maxDrop == 0) {
            return this;
        }
        int[][] newCoords = new int[coords.length][coords.length == 0 ? 0 : coords[0].length];
        for (int i = 0;i < coords.length; i++) {
            newCoords[i] = Arrays.copyOf(coords[i], coords[i].length);
            newCoords[i][0] += maxDrop;
        }
        return new Piece(newCoords, color, rotateIndex);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("Color: ").append(color).append(", Coords: [");
        for (int i = 0; i < coords.length - 1; i++) {
            builder.append(Arrays.toString(coords[i])).append(", ");
        }
        return builder.append(Arrays.toString(coords[coords.length - 1])).append("]").toString();
    }
}
