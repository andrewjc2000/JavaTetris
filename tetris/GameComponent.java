package tetris;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;

public class GameComponent extends JComponent implements KeyListener {

    private static final int CELL_LENGTH = 25;
    private static final int NUM_COLS = 10;
    private static final int CLEAR_TICKS = 50;

    private int UPDATE_EVERY = 50;

    private int level, score;

    private int getPointsForNextLevel() {
        return (level + 1) * 4000;
    }

    private static final int[][][] PIECE_COORD_DATA = {
        {{0, 5}, {1, 5}, {2, 5}, {3, 5}},
        {{0, 4}, {0, 5}, {0, 6}, {1, 5}},
        {{0, 5}, {0, 4}, {1, 5}, {1, 4}},
        {{0, 4}, {0, 5}, {1, 5}, {2, 5}},
        {{0, 5}, {0, 4}, {1, 4}, {2, 4}},
        {{0, 5}, {1, 5}, {1, 4}, {2, 4}},
        {{0, 4}, {1, 4}, {1, 5}, {2, 5}}
    };
    private static final Piece[] PIECE_DATA;
    static {
        PIECE_DATA = new Piece[7];
        PIECE_DATA[0] = new Piece(PIECE_COORD_DATA[0], Color.CYAN, 1);
        PIECE_DATA[1] = new Piece(PIECE_COORD_DATA[1], Color.MAGENTA, 1);
        PIECE_DATA[2] = new Piece(PIECE_COORD_DATA[2], Color.YELLOW, -1);
        PIECE_DATA[3] = new Piece(PIECE_COORD_DATA[3], Color.ORANGE, 2);
        PIECE_DATA[4] = new Piece(PIECE_COORD_DATA[4], Color.BLUE, 2);
        PIECE_DATA[5] = new Piece(PIECE_COORD_DATA[5], Color.RED, 1);
        PIECE_DATA[6] = new Piece(PIECE_COORD_DATA[6], Color.GREEN, 1);
    }

    public void levelUp() {
        if (UPDATE_EVERY > 0) {
            UPDATE_EVERY--;
        }
        level++;
    }

    private Color[][] grid;
    private Piece piece;
    private int counter;
    private final HashSet<Integer> clearingRows;
    private int clearProgress;
    private boolean gameOver;

    public GameComponent() {
        counter = 0;
        grid = new Color[0][0];
        piece = null;
        gameOver = false;
        clearingRows = new HashSet<>(4);
        clearProgress = 0;
        level = 1;
        score = 0;
    }

    private void initGrid() {
        grid = new Color[this.getHeight() / CELL_LENGTH - 1][NUM_COLS];
    }

    private boolean onAnotherPieceOrAtBottom() {
        for (int[] coord: piece.getCoords()) {
            if (coord[0] == grid.length - 1 || (grid[coord[0] + 1][coord[1]] != null && piece.coordNotInPiece(coord[0] + 1, coord[1]))) {
                return true;
            }
        }
        return false;
    }

    private boolean canMove(Piece newPiece) {
        for (int[] coord: newPiece.getCoords()) {
            if (coord[0] < 0 || coord[1] < 0 || coord[0] >= grid.length || coord[1] >= grid[0].length) {
                return false;
            }
            if (grid[coord[0]][coord[1]] != null) {
                boolean found = false;
                if (piece != null) {
                    for (int[] currCoord : piece.getCoords()) {
                        if (currCoord[0] == coord[0] && currCoord[1] == coord[1]) {
                            found = true;
                            break;
                        }
                    }
                }
                if (!found) {
                    return false;
                }
            }
        }
        return true;
    }

    private void placePiece(Piece piece) {
        for (int[] coord: piece.getCoords()) {
            grid[coord[0]][coord[1]] = piece.getColor();
        }
    }

    private void updatePiece(Piece newPiece) {
        if (canMove(newPiece)) {
            placePiece(piece.toErase());
            piece = newPiece;
            placePiece(piece);
        }
    }

    private void checkRows() {
        for (int y = 0; y < grid.length; y++) {
            boolean foundNull = false;
            for (int x = 0; x < grid[0].length; x++) {
                if (grid[y][x] == null) {
                    foundNull = true;
                    break;
                }
            }
            if (!foundNull) {
                clearingRows.add(y);
            }
        }
        clearProgress = CLEAR_TICKS;
    }

    private void clearRows() {
        for (int row: clearingRows) {
            for (int x = 0; x < grid[0].length; x++) {
                grid[row][x] = null;
            }
        }
        for (int row = grid.length - 2; row >= 0; row--) {
            if (!clearingRows.contains(row)) {
                final int rowCopy = row;
                int amountBelow = (int) clearingRows.stream().filter(r -> r > rowCopy).count();
                if (amountBelow > 0) {
                    for (int x = 0; x < grid[0].length; x++) {
                        grid[row + amountBelow][x] = grid[row][x];
                        grid[row][x] = null;
                    }
                }
            }
        }
        clearingRows.clear();
    }

    public void update() {
        if (!gameOver) {
            if (!clearingRows.isEmpty()) {
                if (clearProgress == 0) {
                    clearRows();
                } else {
                    clearProgress--;
                }
            }
            else if (counter == 0) {
                if (piece == null) {
                    Piece newPiece = PIECE_DATA[(int) (Math.random() * PIECE_DATA.length)];
                    if (canMove(newPiece)) {
                        piece = newPiece;
                        placePiece(piece);
                    } else {
                        gameOver = true;
                    }
                } else {
                    if (onAnotherPieceOrAtBottom()) {
                        piece = null;
                        checkRows();
                        if (!clearingRows.isEmpty()) {
                            score += (int) Math.pow(150 * clearingRows.size(), 1.1);
                        } else {
                            score += 10;
                        }
                        if (score >= getPointsForNextLevel()) {
                            levelUp();
                        }
                        counter = UPDATE_EVERY - 1;
                    } else {
                        updatePiece(piece.downOne());
                    }
                }
            }
        }
        counter++;
        counter %= UPDATE_EVERY;
        repaint();
    }

    private void drawCell(Graphics g, int x, int y, Color c, boolean clearing) {
        Color toDraw = c == null ? Color.BLACK : c;
        if (clearing) {
            toDraw = new Color(toDraw.getRed(), toDraw.getGreen(), toDraw.getBlue(), (int) ((255.0 / CLEAR_TICKS) * clearProgress));
        }
        g.setColor(toDraw);
        g.fillRect(x, y, CELL_LENGTH, CELL_LENGTH);
        g.setColor(Color.WHITE);
        g.drawRect(x, y, CELL_LENGTH, CELL_LENGTH);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, Tetris.WIDTH, Tetris.HEIGHT);
        int totalPossibleCols = Tetris.WIDTH / CELL_LENGTH;
        int startingX = (totalPossibleCols - NUM_COLS) / 2;
        if (grid.length == 0) {
            initGrid();
        }
        for (int x = 0; x < grid[0].length; x++) {
            for (int y = 0; y < grid.length; y++) {
                drawCell(g, (startingX + x) * CELL_LENGTH, 5 + y * CELL_LENGTH, grid[y][x], clearingRows.contains(y));
            }
        }
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 100, 100);
        g.drawString("Level: " + level, 100, 150);
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (piece != null) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                updatePiece(piece.moveLeft());
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                updatePiece(piece.moveRight());
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                updatePiece(piece.downOne());
            } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                updatePiece(piece.rotateLeft());
            } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                updatePiece(piece.dropDown(grid));
                counter = UPDATE_EVERY - 1;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}
