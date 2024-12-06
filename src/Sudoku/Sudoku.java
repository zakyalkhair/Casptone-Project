
package Sudoku;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    // Other class variables
    JButton musicToggleButton = new JButton("Toggle Music");

    private Clip backgroundMusicClip; // For looping background music
    private ExecutorService executorService = Executors.newSingleThreadExecutor(); // To handle background music playback
    private boolean isMusicPlaying = false; // Music status flag

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
            InputStream fontStream = getClass().getResourceAsStream("/HelveticaNeueMedium.otf");
            customFont = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(16f); // Ukuran default 16
        } catch (Exception e) {
            e.printStackTrace();
            customFont = new Font("Arial", Font.BOLD, 16); // Fallback ke Arial
        }
        //Setup Frame
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        cp.setBackground(Color.BLACK);
        //Configure Board
        cp.add(board, BorderLayout.CENTER);
        //Configure message label
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setPreferredSize(new Dimension(250, 50));
        messageLabel.setFont(customFont.deriveFont(20f)); // Ukuran lebih besar
        messageLabel.setForeground(new Color(176, 224, 230)); // Gaming theme color
        cp.add(messageLabel, BorderLayout.NORTH); // Tambahkan di atas papan
        //COnfigure musicbutton
        musicToggleButton.setFont(customFont);
        musicToggleButton.setBackground(Color.DARK_GRAY);
        musicToggleButton.setForeground(Color.WHITE);
        musicToggleButton.setFocusPainted(false);
        //timerlabel
        timerLabel.setFont(customFont.deriveFont(16f)); // Set font yang lebih besar agar pas dengan ukuran baru
        // Create a panel to hold the button
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.BLACK); // Gaming theme color
        btnPanel.add(timerLabel);
        btnPanel.add(createStyledButton(btnPlay));
        btnPanel.add(createStyledButton(btnPause));
        btnPanel.add(createStyledButton(btnNewGame));
        btnPanel.add(musicToggleButton);
        cp.add(btnPanel, BorderLayout.SOUTH);

        btnNewGame.addActionListener(e -> startNewGame());
        btnPlay.addActionListener(e -> startTimer());
        btnPause.addActionListener(e -> pauseTimer());
        musicToggleButton.addActionListener(e -> toggleMusic());

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
                    playSound("The Price is Right Losing Horn - Sound Effect (HD).wav");
                    JOptionPane.showMessageDialog(Sudoku.this, "Time's up! You lost.", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                }

                // Periksa jika permainan selesai
                if (board.isSolved()) {
                    timer.stop(); // Hentikan timer saat permainan selesai
                    isTimerRunning = false;
                    playSound("The Price is Right Losing Horn - Sound Effect (HD).wav");
                    // Gunakan pesan "Congratulations!" yang sudah ada sebelumnya
                    JOptionPane.showMessageDialog(Sudoku.this, "Congratulations!", "You Win", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        // Mulai game baru saat aplikasi dijalankan
        startNewGame();
        startBackgroundMusic("/The Price is Right Losing Horn - Sound Effect (HD).wav");
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
        JOptionPane.showMessageDialog(null, "Anda memilih level dengan " + cellsToGuess + " cells to guess.");
        wrongAttempts = 0; // Reset wrong attempts
        updateStatusBar(); // Update the status bar
        updateTimerLabel();
        board.newGame(cellsToGuess); // Berikan parameter cellsToGuess
        startTimer(); // Otomatis mulai timer
        resetMessageLabel();
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
        timerLabel.setForeground(new Color(176, 224, 230));
    }
    public void updateStatusBar() {
        int cellsRemaining = board.countCellsRemaining();
        messageLabel.setText(String.format("Cells remaining: %d | Wrong attempts: %d", cellsRemaining, wrongAttempts));
    }

    public void incrementWrongAttempts() {
        wrongAttempts++;
        updateStatusBar();
        if (wrongAttempts >= 3) {
            playSound("The Price is Right Losing Horn - Sound Effect (HD).wav"); // Play losing sound
            JOptionPane.showMessageDialog(this, "Three wrong attempts! Restarting the game.", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            startNewGame();
        }
    }
    public void resetMessageLabel() {
        messageLabel.setText("Welcome to Sudoku!");
    }
    private JButton createStyledButton(JButton button) {
        button.setFont(customFont);
        button.setBackground(Color.DARK_GRAY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }
    private void playSound(String soundFileName) {
        try {
            File soundFile = new File(getClass().getResource("/" + soundFileName).toURI());
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(soundFile));
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Start background music
    private void startBackgroundMusic(String musicFileName) {
        try {
            InputStream audioSrc = getClass().getResourceAsStream(musicFileName);
            if (audioSrc == null) {
                throw new IllegalArgumentException("Music file not found: " + musicFileName);
            }
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            backgroundMusicClip = AudioSystem.getClip();
            backgroundMusicClip.open(AudioSystem.getAudioInputStream(bufferedIn));
            backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY); // Loop the music
            backgroundMusicClip.start();
            isMusicPlaying = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Stop background music
    private void stopBackgroundMusic() {
        if (backgroundMusicClip != null && backgroundMusicClip.isRunning()) {
            backgroundMusicClip.stop();
            backgroundMusicClip.close();
            isMusicPlaying = false;
        }
    }

    // Toggle background music
    private void toggleMusic() {
        if (isMusicPlaying) {
            stopBackgroundMusic();
        } else {
            startBackgroundMusic("/The Price is Right Losing Horn - Sound Effect (HD).wav");
        }
    }

    @Override
    public void dispose() {
        stopBackgroundMusic(); // Stop music on exit
        executorService.shutdownNow(); // Clean up the executor service
        super.dispose();
    }


    // Play sound effect
}
