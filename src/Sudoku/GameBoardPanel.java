package Sudoku;

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
    }

    /**
     * Generate a new puzzle; and reset the game board of cells based on the puzzle.
     * You can call this method to start a new game.
     */
    public void newGame() {
        String input = JOptionPane.showInputDialog(null, "Pilih level yang diinginkan (Easy/Medium/Hard):", "Input Level", JOptionPane.QUESTION_MESSAGE);
        int cellsToGuess;
        if (input.equalsIgnoreCase("Easy")){
            cellsToGuess = 4;
        } else if (input.equalsIgnoreCase("Medium")) {
            cellsToGuess = 7;
        }
        else
            cellsToGuess = 10;

        try {
            cellsToGuess = Integer.parseInt(input); // Konversi ke angka
            if (cellsToGuess < 1 || cellsToGuess > 25) { // Pastikan dalam rentang 1-81
                throw new NumberFormatException("Out of range");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Input tidak valid! Menggunakan default 4.", "Error", JOptionPane.ERROR_MESSAGE);
            cellsToGuess = 4; // Default jika input tidak valid
        }

        // Tampilkan dialog pemberitahuan

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
    private class CellInputListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            // Get a reference of the JTextField that triggers this action event
            Cell sourceCell = (Cell)e.getSource();

            // Retrieve the int entered
            int numberIn = Integer.parseInt(sourceCell.getText());
            // For debugging
            System.out.println("You entered " + numberIn);
            if (numberIn == sourceCell.number) {
                   sourceCell.status = CellStatus.CORRECT_GUESS;
                } else {
                    sourceCell.status = CellStatus.WRONG_GUESS;
                }
                sourceCell.paint();
            if (isSolved()){
                JOptionPane.showMessageDialog(null,"Congratulation!");
            }
        }
    }
}

