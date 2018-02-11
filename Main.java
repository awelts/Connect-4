package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;

public class Main extends Application {
    public static Pane root=new Pane();
    public static Pane gameBoardPane=new Pane();
    public static int box=100;
    public static double mouseClickX;

    @Override
    public void start(Stage primaryStage) throws Exception{
        makeLines();
        Scene mainScene=new Scene(root,720,680);
        root.setStyle("-fx-background-color: #0000FF");//#797D7F
        primaryStage.setTitle("Connect 4");
        primaryStage.setScene(mainScene);
        primaryStage.show();

        mainScene.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                mouseClickX=mouseEvent.getSceneX();
                System.out.println("mouse click detected! " + mouseClickX);
            }
        });

    }

    public static void makeLines() {
        Line temp;
        Circle temp0, temp1;

        for (int i = 0; i < 9; i++) {
            int j=0;
                while (j< 7) {
                    temp0 = new Circle(60 + box * j, 60, 45, Color.LIGHTGRAY);
                    gameBoardPane.getChildren().addAll(temp0);
                    int k=0;
                    while(k < 6) {
                        temp1 = new Circle(60+box*j, 60 + box * k, 45, Color.LIGHTGRAY);
                        gameBoardPane.getChildren().addAll(temp1);
                        k++;
                    }
                    j++;
                }
            }
            root.getChildren().addAll(gameBoardPane);
    }
        public static void main (String[]args){
            launch(args);
        }
        /*
        public class Cell{
            public void Cell(String name, int x){

            }
        }*/
}

