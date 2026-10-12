import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import javax.swing.SwingUtilities;

public class GameTest {
    static KeyEvent key(ShadowSurvivalAnimated game, int code, boolean press) {
        return new KeyEvent(game, press ? KeyEvent.KEY_PRESSED : KeyEvent.KEY_RELEASED,
                System.currentTimeMillis(), 0, code, KeyEvent.CHAR_UNDEFINED);
    }
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
            game.keyPressed(key(game, KeyEvent.VK_P, true));
            game.keyPressed(key(game, KeyEvent.VK_P, true));
            if (!game.paused) throw new AssertionError("Auto-repeat toggled pause twice");
            game.keyReleased(key(game, KeyEvent.VK_P, false));
            game.keyPressed(key(game, KeyEvent.VK_P, true));
            if (game.paused) throw new AssertionError("Second press should resume");
            game.keyPressed(key(game, KeyEvent.VK_W, true));
            game.keyPressed(key(game, KeyEvent.VK_UP, true));
            game.keyReleased(key(game, KeyEvent.VK_W, false));
            if (!game.up) throw new AssertionError("Releasing W cancelled a held arrow");
            game.keyReleased(key(game, KeyEvent.VK_UP, false));
            if (game.up) throw new AssertionError("Movement stayed active after release");
            game.keyPressed(key(game, KeyEvent.VK_R, true));
            game.score = 10;
            game.keyPressed(key(game, KeyEvent.VK_R, true));
            if (game.score != 10) throw new AssertionError("Auto-repeat restarted again");
        });
        System.out.println("Passed projectile collision, pause, restart, overlapping keys and auto-repeat checks.");
    }
}
