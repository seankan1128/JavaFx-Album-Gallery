package cs1302.gallery;

import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Button;

/**
 * The Play/pause button class.
 */
public class PlayPauseButton extends Button {

    static String def_text = "Pause";
    static String alt_text = "Play";

    /**
     * Constructor.
     * @param t timeline to be controlled.
     */
    public PlayPauseButton(Timeline t) {
        super(def_text);
        this.setOnAction(e -> {
            Thread task = new Thread (() -> toPause(t));
            task.setDaemon(true);
            task.start();
        });
    }

    /**
     * To pause method. Pausing the timeline and change button actionevent.
     * @param t timeline
     */
    void toPause(Timeline t) {
        Runnable r = () -> {
            t.pause();
            this.setText(alt_text);
            this.setOnAction(e -> {
                Thread task = new Thread (() -> toPlay(t));
                task.setDaemon(true);
                task.start();
            });
        };
        Platform.runLater(r);
    }
    
    /**
     * To play method. to play the timeline and change button mode.
     * @param t
     */
    void toPlay(Timeline t) {
        Runnable r = () -> {
            t.play();
            this.setText(def_text);
            this.setOnAction(e -> {
                Thread task = new Thread (() -> toPause(t));
                task.setDaemon(true);
                task.start();
            });
        };
        Platform.runLater(r);
    }
}    
