package tetris;

import java.awt.Color;
import java.util.Arrays;

public class Piece {
    private final int[][] coords;
    private final int baseX, baseY;
    private final Color color;
    private final int rotateIndex;

    public Piece(int[][] coords, Color color, int rotateIndex) {
        this.coords = coords;
        this.color = color;
        this.rotateIndex = rotateIndex;
        this.baseX = rotateIndex == -1 ? -1 : coords[rotateIndex][1];
        this.baseY = rotateIndex == -1 ? -1 : coords[rotateIndex][0];
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
        if (rotateIndex == -1) {
            return this;
        }
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
        int maxDrop = Integer.MAX_VALUE;
        for (int[] baseCoord: coords) {
            boolean hasCoordBelow = false;
            for (int[] otherCoord : coords) {
                if (otherCoord[0] > baseCoord[0] && baseCoord[1] == otherCoord[1]) {
                    hasCoordBelow = true;
                    break;
                }
            }
            if (!hasCoordBelow) {
                int x = baseCoord[1];
                int drop = 0;
                for (int row = baseCoord[0] + 1; row < grid.length && grid[row][x] == null; row++) {
                    drop++;
                }
                maxDrop = Math.min(maxDrop, drop);
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
