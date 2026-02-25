package View;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class HomeView extends StackPane {

    public HomeView() {

        // Load homepage image
        Image image = new Image("resources/gemeentelogo-Nijmegen.png");
        ImageView imageView = new ImageView(image);

        imageView.setFitWidth(1052);
        imageView.setFitHeight(432);

        this.setAlignment(Pos.CENTER);
        this.getChildren().add(imageView);
    }
}
