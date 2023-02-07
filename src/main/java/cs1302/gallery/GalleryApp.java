package cs1302.gallery;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.application.Platform;
import javafx.scene.layout.HBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Priority;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;
import javafx.scene.layout.TilePane;
import javafx.scene.control.Separator;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.net.URL;
import java.io.IOException;
import java.io.InputStreamReader;
import com.google.gson.*;

/** 
 * Represents an iTunes GalleryApp!
 */
public class GalleryApp extends Application {

    /* Timeline of the app.*/
    Timeline timeline = new Timeline();

    /* The root container of the scene. */
    VBox mainPane = new VBox();

    /* The Menu. */
    final Menu menu1 = new Menu("File");
    MenuBar menuBar = new MenuBar();
    MenuItem menuItem = new MenuItem("Exit");

    /* The search area of the app. */
    ToolBar toolbar = new ToolBar();

    /* The text on search area. */
    Text t = new Text("Search Query: ");

    /* The Textfield for user to input search target. */
    TextField SearchField = new TextField();

    /* The Serach Button. */
    Button SearchButton = new Button("Load");

    /* The Pause Button. */
    PlayPauseButton PauseButton = new PlayPauseButton(timeline);

    /* List of GalleryImage. */
    List<GalleryImage> gimg = new ArrayList<>();

    /* The container for the image graph. */
    TilePane tile;

    /* The progress bar. */
    ProgressBar p1 = new ProgressBar();

    /** {@inheritdoc} */
    @Override
    public void start(Stage stage) {

        HBox.setHgrow(SearchField, Priority.ALWAYS);

        menuItem.setOnAction(e -> {
            Thread task = new Thread (() -> exitApplication(e));
            task.setDaemon(true);
            task.start();
        });

        menu1.getItems().add(menuItem);

        menuBar.getMenus().addAll(menu1);

        mainPane.getChildren().add(menuBar);

        toolbar.getItems().addAll(PauseButton, new Separator(), t, SearchField, SearchButton);

        mainPane.getChildren().add(toolbar);

        tile =  new TilePane();
        tile.setPrefColumns(5);
        for (int i = 0; i < 20; i++) {
            gimg.add(new GalleryImage());
            tile.getChildren().add(gimg.get(i));
        }

        EventHandler<ActionEvent> handler = event -> {
            runNow(() -> {
                try {
                    timeline.getKeyFrames().clear();
                    JsonObject root = getJSON();
                    List<String> urls = getUrls(root);
                    Set<String> set = new HashSet<>(urls);
                    urls.clear();
                    urls.addAll(set);
                    if (urls.size() < 21) {
                        // Need to display dialog
                        System.out.println("not enough urls");
                    } else {
                        setProgress(0);
                        Platform.runLater(() -> { 
                            PauseButton.toPause(timeline); 
                        });
                        for (int i = 0; i < 20; i++) {
                                gimg.get(i).loadImage(urls.get(i));
                        }
                        for (int i = 0; i < urls.size(); i++) { 
                            Random rand = new Random();
                            EventHandler<ActionEvent> handler2 = createHandler(rand.nextInt(20), urls.get(i));
                            Platform.runLater(() -> { 
                                timeline = new Timeline();
                                KeyFrame keyFrame = new KeyFrame(Duration.seconds(2), handler2);
                                timeline.setCycleCount(Timeline.INDEFINITE);
                                timeline.getKeyFrames().add(keyFrame); 
                                timeline.play(); // change to RUNNING
                            });
                            setProgress(1.0 * i / urls.size()); // NICE!
                        } // for
                        setProgress(1);
                        Platform.runLater(() -> { 
                            PauseButton.toPlay(timeline); 
                        }); // change to RUNNING
                    }

                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            });      
        };
        SearchButton.setOnAction(handler);

        mainPane.getChildren().add(tile);

        mainPane.getChildren().add(p1);
        setProgress(0);

        Scene scene = new Scene(mainPane);
        stage.setMaxWidth(640);
        stage.setMaxHeight(600);
        stage.setTitle("GalleryApp!");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
        
    } // start

    /**
     * Method to exit the application.
     * @param event The event to trigger the method
     */
    public void exitApplication(ActionEvent event) {
        Platform.exit();
    }

    /**
     * The method to get JSON file and read it.
     * @param event The event to trigger the method
     * @throws IOException
     */
    public JsonObject getJSON() throws IOException {
        String term = URLEncoder.encode(SearchField.getText(), "UTF-8");
        String limit = "200";
        String media = "music";
        String Surl = "https://itunes.apple.com/search?term=" + term + "&limit=" + limit + "&media=" + media;
        URL url = new URL(Surl);
        InputStreamReader reader = new InputStreamReader(url.openStream());
        JsonElement je = JsonParser.parseReader(reader);
        JsonObject root = je.getAsJsonObject();
        return root;   
    }

    /**
     * Method to get URLs from JsonObject.
     * @param root the JsonObject
     * @return list of url strings
     */
    public List<String> getUrls(JsonObject root) {
        JsonArray results = root.getAsJsonArray("results");          // "results" array
        int numResults = results.size();                             // "results" array size
        List<String> urls = new ArrayList<String>();
        for (int i = 0; i < numResults; i++) {
            JsonObject result = results.get(i).getAsJsonObject();
            urls.add(result.get("artworkUrl100").getAsString()); // artworkUrl100 member
        }
        return urls;
    }

    /** 
     * function to create the EventHandler outside the loop.
     */
    EventHandler<ActionEvent> createHandler(int i, String url) {
        EventHandler<ActionEvent> handler = e -> {
            // something involving i
            // Platform.runLater(() -> {gimg.get(i).loadImage(url);});
            gimg.get(i).loadImage(url);
        };
        return handler;
    } // createHandler

    /**
     * Creates and immediately starts a new daemon thread that executes
     * {@code target.run()}. This method, which may be called from any thread,
     * will return immediately its the caller.
     * @param target the object whose {@code run} method is invoked when this
     *               thread is started
     */
    public static void runNow(Runnable target) {
        Thread t = new Thread(target);
        t.setDaemon(true);
        t.start();
    } // runNow

    /**
     * Method to set progress bar progress.
     * @param progress
     */
    private void setProgress(final double progress) {
        Platform.runLater(() -> p1.setProgress(progress));
    } // setProgress
    

} // GalleryApp

