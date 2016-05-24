package base;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Snach on 24.04.16.
 */
@SuppressWarnings("unused")
public interface GameMechanics {

    int getMyScore(String user);

    int getEnemyScore(String user);

    void addUser(@NotNull String user);

    void removeUser(@NotNull String user);

    void incrementScore(String userName);

    void run();

    void starGame(@NotNull String first, @NotNull String second);

    void gmStep();
}
