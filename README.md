# Shadow Survival

A Java Swing arcade prototype. Move with WASD or the arrow keys, fire upward with Space, and survive enemies that chase the player. A collision costs health; hitting an enemy earns ten points.

## Run

Requires JDK 17 or newer.

```sh
mkdir -p build
javac -d build src/ShadowSurvivalAnimated.java
java -cp build ShadowSurvivalAnimated
```

`P` pauses or resumes. `R` restarts. Losing window focus pauses the game and clears held keys. Game over keeps the window open for another attempt.

## Test

```sh
javac -d build src/ShadowSurvivalAnimated.java tests/GameTest.java
java -Djava.awt.headless=true -cp build GameTest
```

Checks cover projectile collisions, score updates, pause behavior, and restart state. The game runs on Swing's event thread with a 16ms timer. Movement is tick-based; art is drawn with Java2D and needs no external assets.
