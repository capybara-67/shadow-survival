import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Iterator;

public class ShadowSurvivalAnimated extends JPanel
        implements ActionListener, KeyListener {

    static final int WIDTH = 800;
    static final int HEIGHT = 600;

    javax.swing.Timer timer;

    int px = WIDTH / 2, py = HEIGHT / 2;
    int size = 32;
    int speed = 4;
    int health = 100;

    int frame = 0;
    int frameTick = 0;

    boolean up, down, left, right, shoot;

    ArrayList<Rectangle> bullets = new ArrayList<>();
    ArrayList<Rectangle> enemies = new ArrayList<>();

    int score = 0;
    int spawnRate = 60;
    int tick = 0;
    boolean paused = false;
    boolean gameOver = false;
    private final Random random = new Random();

    public ShadowSurvivalAnimated() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                up = down = left = right = shoot = false;
                paused = true;
                repaint();
            }
        });

        timer = new javax.swing.Timer(16, this);
        timer.start();
    }

    void spawnEnemy() {
        int x = random.nextBoolean() ? 0 : WIDTH - 30;
        int y = random.nextInt(HEIGHT - 30 + 1);
        enemies.add(new Rectangle(x, y, 30, 30));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (paused || gameOver) return;
        tick++;
        frameTick++;

        if (frameTick >= 8) {
            frame = (frame + 1) % 4;
            frameTick = 0;
        }

        if (up) py -= speed;
        if (down) py += speed;
        if (left) px -= speed;
        if (right) px += speed;

        px = Math.max(0, Math.min(WIDTH - size, px));
        py = Math.max(0, Math.min(HEIGHT - size, py));

        if (shoot && tick % 10 == 0) {
            bullets.add(new Rectangle(px + size / 2 - 3, py, 6, 10));
        }

        bullets.removeIf(b -> {
            b.y -= 10;
            return b.y < 0;
        });

        if (tick % spawnRate == 0) spawnEnemy();

        Rectangle player = new Rectangle(px, py, size, size);

        for (Rectangle en : enemies) {
            if (en.x < px) en.x += 2;
            if (en.x > px) en.x -= 2;
            if (en.y < py) en.y += 2;
            if (en.y > py) en.y -= 2;
        }

        bullets.removeIf(b -> {
            for (Iterator<Rectangle> it = enemies.iterator(); it.hasNext();) {
                Rectangle en = it.next();
                if (b.intersects(en)) {
                    it.remove();
                    score += 10;
                    return true;
                }
            }
            return false;
        });

        enemies.removeIf(en -> {
            if (en.intersects(player)) {
                health -= 10;
                return true;
            }
            return false;
        });

        if (health <= 0) {
            health = 0;
            gameOver = true;
            up = down = left = right = shoot = false;
        }

        repaint();
    }

    void restart() {
        px = WIDTH / 2; py = HEIGHT / 2;
        health = 100; score = 0; tick = 0; frame = 0; frameTick = 0;
        bullets.clear(); enemies.clear();
        up = down = left = right = shoot = false;
        paused = gameOver = false;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(frame % 2 == 0 ? Color.CYAN : Color.BLUE);
        g.fillRect(px, py, size, size);

        g.setColor(Color.YELLOW);
        for (Rectangle b : bullets)
            g.fillRect(b.x, b.y, b.width, b.height);

        for (Rectangle en : enemies) {
            g.setColor(frame % 2 == 0 ? Color.RED : Color.PINK);
            g.fillRect(en.x, en.y, en.width, en.height);
        }

        g.setColor(Color.WHITE);
        g.drawString("Health: " + health, 10, 20);
        g.drawString("Score: " + score, 10, 40);
        g.drawString("WASD / arrows: move   Space: shoot   P: pause   R: restart", 10, HEIGHT - 16);
        if (paused || gameOver) {
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(0, 0, WIDTH, HEIGHT);
            g.setColor(Color.WHITE);
            g.setFont(g.getFont().deriveFont(Font.BOLD, 24));
            g.drawString(gameOver ? "Game over — press R to restart" : "Paused — press P to resume", 180, HEIGHT / 2);
        }
    }

    @Override public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_R) { restart(); return; }
        if (e.getKeyCode() == KeyEvent.VK_P) { paused = !paused; up = down = left = right = shoot = false; repaint(); return; }
        if (gameOver || paused) return;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> up = true;
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> down = true;
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> left = true;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> right = true;
            case KeyEvent.VK_SPACE -> shoot = true;
        }
    }

    @Override public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> up = false;
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> down = false;
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> left = false;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> right = false;
            case KeyEvent.VK_SPACE -> shoot = false;
        }
    }

    @Override public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame window = new JFrame("Shadow Survival");
            ShadowSurvivalAnimated game = new ShadowSurvivalAnimated();
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.add(game);
            window.pack();
            window.setResizable(false);
            window.setLocationRelativeTo(null);
            window.setVisible(true);
            game.requestFocusInWindow();
        });
    }
}
