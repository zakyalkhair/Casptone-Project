
package Sudoku;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;

/**
 * The main Sudoku program
 */
public class Sudoku extends JFrame {
    private static final long serialVersionUID = 1L;  // to prevent serial warning

    // private variables
    private GameBoardPanel board = new GameBoardPanel();
    private JButton btnNewGame = new JButton("New Game");
    private JButton btnPlay = new JButton("Play");
    private JButton btnPause = new JButton("Pause");
    private JLabel timerLabel = new JLabel("05:00");

    private Timer timer;
    private int timeLeft = 300; // 5 menit dalam detik
    private final int timeLeftEasy = 120;  // 2 menit
    private final int timeLeftMedium = 240;  // 4 menit
    private final int timeLeftHard = 360;  // 6 menit
    private boolean isTimerRunning = false;
    private int wrongAttempts = 0; // Track wrong attempts
    private JLabel messageLabel = new JLabel("Welcome to Sudoku!");
    private Font customFont;

    // Constructor
    public Sudoku() {
        try {
            InputStream fontStream = getClass().getResourceAsStream("/resources/fonts/HelveticaNeueMedium.otf");
            customFont = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(16f); // Ukuran default 16
        } catch (Exception e) {
            e.printStackTrace();
            customFont = new Font("Arial", Font.BOLD, 16); // Fallback ke Arial
        }
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());

        cp.add(board, BorderLayout.CENTER);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setFont(customFont.deriveFont(18f)); // Ukuran lebih besar
        cp.add(messageLabel, BorderLayout.NORTH); // Tambahkan di atas papan


        JPanel btnPanel = new JPanel(); // Create a panel to hold the button
        btnPanel.add(btnNewGame);
        btnPanel.add(timerLabel);
        btnPanel.add(btnPlay);
        btnPanel.add(btnPause);
        btnPanel.add(btnNewGame);
        cp.add(btnPanel, BorderLayout.SOUTH);

        btnNewGame.addActionListener(e -> startNewGame());
        btnPlay.addActionListener(e -> startTimer());
        btnPause.addActionListener(e -> pauseTimer());

        // Timer untuk hitungan mundur
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (timeLeft > 0) {
                    timeLeft--;
                    updateTimerLabel();
                } else {
                    timer.stop();
                    isTimerRunning = false;
                    JOptionPane.showMessageDialog(Sudoku.this, "Time's up! You lost.", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                }

                // Periksa jika permainan selesai
                if (board.isSolved()) {
                    timer.stop(); // Hentikan timer saat permainan selesai
                    isTimerRunning = false;
                    // Gunakan pesan "Congratulations!" yang sudah ada sebelumnya
                    JOptionPane.showMessageDialog(Sudoku.this, "Congratulations!", "You Win", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        // Mulai game baru saat aplikasi dijalankan
        startNewGame();

        pack();     // Pack the UI components, instead of using setSize()
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // to handle window-closing
        setTitle("Sudoku");
        setVisible(true);
    }

    // Memulai permainan baru
   private void startNewGame() {
        String[] options = {"Easy", "Medium", "Hard"};
        int choice = JOptionPane.showOptionDialog(
                null,
                "Pilih level yang diinginkan:",
                "Input Level",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        int cellsToGuess;
        if (choice == 0) { // Easy
            timeLeft = timeLeftEasy;
            cellsToGuess = 4;
        } else if (choice == 1) { // Medium
            timeLeft = timeLeftMedium;
            cellsToGuess = 7;
        } else if (choice == 2) { // Hard
            timeLeft = timeLeftHard;
            cellsToGuess = 10;
        } else { // Default jika tidak ada pilihan
            JOptionPane.showMessageDialog(null, "Tidak ada level yang dipilih. Menggunakan default Easy.", "Info", JOptionPane.INFORMATION_MESSAGE);
            timeLeft = timeLeftEasy;
            cellsToGuess = 4;
        }

        // Menampilkan jumlah tebakan berdasarkan level
        JOptionPane.showMessageDialog(null, "Anda memilih level dengan " + cellsToGuess + " cells to guess.");

        board.newGame(cellsToGuess); // Berikan parameter cellsToGuess
        updateTimerLabel();
        startTimer();
    }

    // Memulai timer
    private void startTimer() {
        if (!isTimerRunning) {
            timer.start();
            isTimerRunning = true;
        }
    }

    // Menjeda timer
    private void pauseTimer() {
        if (isTimerRunning) {
            timer.stop();
            isTimerRunning = false;
        }
    }

    // Memperbarui label timer
    private void updateTimerLabel() {
        int minutes = timeLeft / 60;
        int seconds = timeLeft % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }
    public void updateStatusBar() {
        int cellsRemaining = board.countCellsRemaining();
        messageLabel.setText(String.format("Cells remaining: %d | Wrong attempts: %d", cellsRemaining, wrongAttempts));
    }

    public void incrementWrongAttempts() {
        wrongAttempts++;
        updateStatusBar();
        if (wrongAttempts >= 3) {
            JOptionPane.showMessageDialog(this, "Three wrong attempts! Restarting the game.", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            startNewGame();
        }
    }
    public void resetMessageLabel() {
        messageLabel.setText("Welcome to Sudoku!");
    }
}
