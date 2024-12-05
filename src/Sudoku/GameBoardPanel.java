
package Sudoku;
import javax.swing.border.Border;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class GameBoardPanel extends JPanel {
    private static final long serialVersionUID = 1L;  // to prevent serial warning

    // Define named constants for UI sizes
    public static final int CELL_SIZE = 60;   // Cell width/height in pixels
    public static final int BOARD_WIDTH = CELL_SIZE * SudokuConstants.GRID_SIZE;
    public static final int BOARD_HEIGHT = CELL_SIZE * SudokuConstants.GRID_SIZE;
    // Board width/height in pixels

    // Define properties
    /**
     * The game board composes of 9x9 Cells (customized JTextFields)
     */
    private Cell[][] cells = new Cell[SudokuConstants.GRID_SIZE][SudokuConstants.GRID_SIZE];
    /**
     * It also contains a Puzzle with array numbers and isGiven
     */
    private Puzzle puzzle = new Puzzle();


    /**
     * Constructor
     */
    public GameBoardPanel() {
        super.setLayout(new GridLayout(SudokuConstants.GRID_SIZE, SudokuConstants.GRID_SIZE));  // JPanel

        // Allocate the 2D array of Cell, and added into JPanel.
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                cells[row][col] = new Cell(row, col);
                super.add(cells[row][col]);   // JPanel
            }
        }

        // [TODO 3] Allocate a common listener as the ActionEvent listener for all the
        //  Cells (JTextFields)
        CellInputListener listener = new CellInputListener();

        // [TODO 4] Adds this common listener to all editable cells
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) { // Loop through all rows
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) { // Loop through all columns
                if (cells[row][col].isEditable()) { // Check if the cell is editable
                    cells[row][col].addActionListener(listener); // Add the listener
                }
            }
        }
        super.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                // Tentukan border untuk setiap sel
                int top = (row % SudokuConstants.SUBGRID_SIZE == 0) ? 3 : 1;  // Lebih tebal di baris sub-grid
                int left = (col % SudokuConstants.SUBGRID_SIZE == 0) ? 3 : 1; // Lebih tebal di kolom sub-grid
                int bottom = (row == SudokuConstants.GRID_SIZE - 1) ? 3 : 1; // Border bawah untuk sel terakhir
                int right = (col == SudokuConstants.GRID_SIZE - 1) ? 3 : 1;  // Border kanan untuk sel terakhir

                // Menambahkan border tebal di batas sub-grid 3x3
                if (row % SudokuConstants.SUBGRID_SIZE == 0) {
                    top = 5;  // Memberikan border yang lebih tebal pada bagian atas sub-grid
                }
                if (col % SudokuConstants.SUBGRID_SIZE == 0) {
                    left = 5;  // Memberikan border yang lebih tebal pada bagian kiri sub-grid
                }

                // Membuat border tebal di sisi sub-grid
                Border border = BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK);
                // Menambahkan border tipis pada sisi non-sub-grid
                Border thinGrayBorder = BorderFactory.createMatteBorder(
                        (top == 1) ? 1 : 0, (left == 1) ? 1 : 0, (bottom == 1) ? 1 : 0, (right == 1) ? 1 : 0,
                        Color.WHITE);

                // Menggabungkan kedua border (tebal dan tipis)
                cells[row][col].setBorder(BorderFactory.createCompoundBorder(border, thinGrayBorder));

                super.add(cells[row][col]);
            }
        }
    }

    /**
     * Generate a new puzzle; and reset the game board of cells based on the puzzle.
     * You can call this method to start a new game.
     */
    public void newGame(int cellsToGuess) {
        // Generate puzzle baru
        puzzle.newPuzzle(cellsToGuess);

        // Initialize all the 9x9 cells, based on the puzzle.
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                cells[row][col].newGame(puzzle.numbers[row][col], puzzle.isGiven[row][col]);
            }
        }
    }
    /**
     * Return true if the puzzle is solved
     * i.e., none of the cell have status of TO_GUESS or WRONG_GUESS
     */
    public boolean isSolved() {
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                if (cells[row][col].status == CellStatus.TO_GUESS || cells[row][col].status == CellStatus.WRONG_GUESS) {
                    return false;
                }
            }
        }
        return true;
    }
    // [TODO 2] Define a Listener Inner Class for all the editable Cells
// .........
    public int countCellsRemaining() {
        int count = 0;
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                if (cells[row][col].status == CellStatus.TO_GUESS || cells[row][col].status == CellStatus.WRONG_GUESS) { // Only count TO_GUESS cells
                    count++;
                }
            }
        }
        return count;
    }
    private class CellInputListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Cell sourceCell = (Cell) e.getSource();

            try {
                int numberIn = Integer.parseInt(sourceCell.getText());
                if (numberIn == sourceCell.number) {
                    sourceCell.status = CellStatus.CORRECT_GUESS;
                } else {
                    sourceCell.status = CellStatus.WRONG_GUESS;
                    ((Sudoku) SwingUtilities.getWindowAncestor(GameBoardPanel.this)).incrementWrongAttempts(); // Inform main class
                }
                sourceCell.paint();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Input harus berupa angka.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            ((Sudoku) SwingUtilities.getWindowAncestor(GameBoardPanel.this)).updateStatusBar(); // Update the status bar
        }
        }
}
