package connect4;
import javafx.animation.TranslateTransition;
import javafx.scene.shape.Circle;

import javafx.scene.paint.Color;
import javafx.util.Duration;

import static connect4.connect4GUI.box;
import static connect4.connect4GUI.chipPane;

public class chip {
    Circle image;
    chip(short value){
        if(value == 1)image = new Circle(40, Color.RED);
        else image = new Circle(40, Color.BLACK);
    }
    TranslateTransition placeChip(short column, short row){
        TranslateTransition results;
        chipPane.getChildren().add(image);
        results = new TranslateTransition(Duration.millis(800),image);
        results.setFromX(60 + column*box);
        results.setFromY(0);
        results.setToY(560 - row*box);
        return results;
    }

}
