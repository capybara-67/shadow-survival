import java.awt.Rectangle;
import javax.swing.SwingUtilities;

public class GameTest {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            ShadowSurvivalAnimated game = new ShadowSurvivalAnimated();
            game.timer.stop();
            game.bullets.add(new Rectangle(100,110,6,10));
            game.enemies.add(new Rectangle(100,100,30,30));
            game.actionPerformed(null);
            if (game.score != 10 || !game.enemies.isEmpty() || !game.bullets.isEmpty()) throw new AssertionError("Collision score");
            game.paused = true;
            int tick = game.tick;
            game.actionPerformed(null);
            if (game.tick != tick) throw new AssertionError("Paused updates");
            game.health = 0; game.gameOver = true;
            game.restart();
            if (game.health != 100 || game.score != 0 || game.gameOver || game.paused) throw new AssertionError("Restart reset");
        });
        System.out.println("Passed projectile collision, pause, and restart checks.");
    }
}
