package main;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Snach on 08.03.16.
 */
public class Logger {
    private Class clazz;

    public Logger(Class clazz) {
        this.clazz = clazz;
    }


    public void log(@NotNull String msg) {

        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();

        System.out.append(clazz.getName() + ": " + methodName + ": " + msg + '\n');
    }
}
