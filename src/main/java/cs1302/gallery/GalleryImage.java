package cs1302.gallery;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

/** Gallery Image class. */
public class GalleryImage extends ImageView {

    /** A default image which loads when the application starts. */
    private static final String DEFAULT_IMG =
        "http://cobweb.cs.uga.edu/~mec/cs1302/gui/default.png";

    /** Default height and width for Images. */
    private static final int DEF_HEIGHT = 100;
    private static final int DEF_WIDTH = 100;
    
    /**
    * Constructor of class GalleryImage.
    */
    public GalleryImage() {

        
        // Load the default image with the default dimensions
        Image img = new Image(DEFAULT_IMG, DEF_HEIGHT, DEF_WIDTH, false, false);

        // Add the image to its container and preserve the aspect ratio if resized
        this.setImage(img);
        this.setPreserveRatio(true);
        
    } // GalleryImage

    /**
     * Method to reset image.
     *
     * @param url the url string to be process.
     */ 
    public void loadImage(String url) {

        try {
            Image newImg = new Image(url, DEF_HEIGHT, DEF_WIDTH, false, false);
            this.setImage(newImg);
        } catch (IllegalArgumentException iae) {
            System.out.println("The supplied URL is invalid");
        } // try
        
    } // loadImage
    

}
