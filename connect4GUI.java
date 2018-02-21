package connect4;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

import java.io.*;
import java.util.BitSet;
import java.util.Formatter;
import java.util.Scanner;
import java.util.Vector;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.Random;


public class connect4GUI extends Application {
    public static Pane root = new Pane();
    public static Pane gameBoardPane = new Pane();
    public static Pane chipPane = new Pane();
    public static Pane coverPane = new Pane();
    public static int box = 100;
    public static int gameCount;
    public static Text currentTurnText, currentPlayer=new Text();
    public static boolean isUserTurn, playerWon=false;
    public static short [][] board = new short [7][6];
    public static short userValue, aiValue;
    public static final short DATASIZE = 64;  //49 unique address, 7 choices,4 bits of distance, 1 bit win/loss, 2 bits free, 1 leftToRight;
    public static final short BRANCHLIMIT = 7;  //normally 7, must be odd to end on a memory search of Ai
    public static final short DECISIONADDRESS = 49;
    public static PauseTransition pause;

    public static GridPane startPane = new GridPane(), winnerGridPane = new GridPane();
    public StackPane stackPane1 = new StackPane();
    public static String usrName = null;
    public Button startBtn;
    public Scene mainScene,startScene;
    public ListView winnerList_LV;
    public ChoiceBox firstPlayerCB;
    public String CBInput;
    public static String playerName;
    public static File winnersFile=new File("C:\\Users\\awelts\\IdeaProjects\\connect4Gui\\src\\connect4\\winners.txt");
    public HBox hbox=new HBox();
    public static Vector<String> winners=new Vector<>();
    public ObservableList<String> winnerList_OL;
    public static Formatter f;


    @Override
    public void start(Stage primaryStage) throws Exception{

        winnerList_OL=FXCollections.observableArrayList();

        if(!winnersFile.exists()){
            winnersFile.createNewFile();
            System.out.println("File created.");
        }
        Scanner in=new Scanner(winnersFile);
        while (in.hasNext()){
            String temp=in.next();
            winners.add(temp);
        }
        in.close();
        for(int i=0;i<winners.size();i++){
            winnerList_OL.add(winners.get(i));
        }

        //winner list stuff
        Label winnerLabel=new Label("Winner List: ");
        winnerGridPane.setConstraints(winnerLabel,0,0);
        winnerList_LV=new ListView(winnerList_OL);
        winnerGridPane.setConstraints(winnerList_LV,0,1);
        winnerGridPane.setVgap(10);
        winnerGridPane.setPadding(new Insets(10,10,10,50));


        //Start Screen
        hbox.getChildren().addAll(startPane,winnerGridPane);
        hbox.setStyle("-fx-background-color: #cccccc");//"-fx-background-color: #0000FF"
        startScene=new Scene(stackPane1, 600,475);
        primaryStage.setResizable(false);
        stackPane1.getChildren().addAll(hbox);
        startPane.setVgap(10);
        startPane.setHgap(10);
        startPane.setPadding(new Insets(10,50,10,10));

        //Name input
        Label nameLabel= new Label("Write Your Name Here: ");
        TextField nameTF= new TextField();
        nameTF.setPromptText("Enter your name..");
        startPane.setConstraints(nameLabel, 0,0);
        startPane.setConstraints(nameTF,0,1);

        //Start Button stuff
        startBtn=new Button("START");
        startPane.setConstraints(startBtn,0,4);

        //Choice Box stuff
        Label firstPlayerLabel=new Label("Select who goes first: \n(Whoever is first is the color Red)");
        startPane.setConstraints(firstPlayerLabel,0,2);
        firstPlayerCB=new ChoiceBox(FXCollections.observableArrayList("Computer", "Player"));
        startPane.setConstraints(firstPlayerCB,0,3);
        firstPlayerCB.getSelectionModel().select(1);

        startPane.getChildren().addAll(nameLabel,nameTF,firstPlayerLabel,firstPlayerCB,startBtn);
        winnerGridPane.getChildren().addAll(winnerList_LV,winnerLabel);


        // Moves to game board scene and starts game
        startBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                playerName = nameTF.getText();
                CBInput = firstPlayerCB.getValue().toString();
                if (CBInput == ("Player")) {
                    isUserTurn = true;
                } else isUserTurn = false;
                if (!isUserTurn) {
                    aiValue = 1;
                    userValue = 2;
                    pause = new PauseTransition(Duration.millis(1000));
                    pause.play();
                    pause.setOnFinished(event1 -> {
                                try {
                                    aiTurn();
                                } catch (Exception e) {
                                    System.out.println(e);
                                }
                            }
                    );

                }
                if (isUserTurn) {
                    userValue = 1;
                    aiValue = 2;
                    isUserTurn = true;
                }

                mainScene = new Scene(root, 720, 680);
                root.setStyle("-fx-background-color: #0000FF");//#797D7F
                makeLines();

                primaryStage.setTitle("Connect 4");
                primaryStage.setScene(mainScene);
                primaryStage.show();


                mainScene.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        double mouseClickX, mouseClickY;
                        mouseClickX = mouseEvent.getSceneX();
                        mouseClickY = mouseEvent.getSceneY();
                        System.out.println(mouseClickY);
                        try {
                            if (mouseClickY < 610) {
                                if (mouseClickX < 15) ;
                                else if (mouseClickX < 110)
                                    takeUserInput(0);
                                else if (mouseClickX < 210)
                                    takeUserInput(1);
                                else if (mouseClickX < 310)
                                    takeUserInput(2);
                                else if (mouseClickX < 410)
                                    takeUserInput(3);
                                else if (mouseClickX < 510)
                                    takeUserInput(4);
                                else if (mouseClickX < 610)
                                    takeUserInput(5);
                                else if (mouseClickX < 710)
                                    takeUserInput(6);
                            }
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                });
            }
        });
        gameCount = 0;



        currentTurnText = new Text("Turn: ");
        currentTurnText.setFill(Color.WHITE);
        currentPlayer.setFill(Color.WHITE);
        currentTurnText.relocate(320, 630);
        root.getChildren().addAll(currentTurnText, currentPlayer);


        primaryStage.setTitle("Connect 4");
        primaryStage.setScene(startScene);
        primaryStage.show();


    }

    public static void takeUserInput(int command) {
        short nextSpot;
        short[][] temp;
        TranslateTransition dropping;
        temp = board.clone();
        if (isUserTurn) {
            nextSpot = findNextSpot(temp[command]);
            if (nextSpot < 6) {
                temp[command][nextSpot] = userValue;
                board = temp;
                gameCount++;
                dropping = updateBoardSituation((short) command, nextSpot);
                isUserTurn = false;
                pause = new PauseTransition(Duration.millis(10));
                dropping.play();
                dropping.setOnFinished(event -> {
                            try {
                                aiTurn();
                            } catch (Exception e) {
                                System.out.println(e);
                            }
                        }
                );

            }
        }
    }

    public static void aiTurn() {
        short column, row, valueOfBranch, bestBranch;
        TranslateTransition dropping;
        short[][] temp;
        Random rand;
        boolean redo;
        BitSet memoryResults;
        boolean[] safeBranches = new boolean[7];
        temp = new short[7][6];

        BitSet head = new BitSet(56);
        byte[] foo;

        foo = head.toByteArray();

        ///////////////////////////////////////////////////////////////////////////


        System.out.println("AI turn");
        column = 3;
        bestBranch = -100;

        memoryResults = searchMemory(encodeBoard(temp));
        if (memoryResults.cardinality() > 0) {
            //pull info in the winDistance
            //record to the least sig digit
            //9 = close, decrement by one for every step
            // results is equal to a one digit number
            //return some value. must be single digit to not alert the mini
            column = 0;
        } else {

            for (short i = 0; i < 7; i++) {
                for (int j = 0; j < temp.length; j++) {
                    temp[j] = board[j].clone();
                }
                row = findNextSpot(temp[i]);
                if (row > 5 || row < 0) continue;
                temp[i][row] = aiValue;
                System.out.println("new big branch " + i); //**************************************************
                printConsoleBoard(temp);


                if (checkWin(i, row, temp)) {
                    valueOfBranch = 90;
                } else {
                    valueOfBranch = analyzeMinBranch(temp, 0);
                    if (valueOfBranch == 0)
                        safeBranches[i] = true;
                }
                System.out.println("Value of branch: " + valueOfBranch);
                System.out.println("");
                if (isLargerThanMax(valueOfBranch, bestBranch)) {
                    bestBranch = valueOfBranch;
                    column = i;
                }
            }
        }
        for (int j = 0; j < temp.length; j++) {
            temp[j] = board[j].clone();
        }

        if (bestBranch == 0) {
            do {
                rand = new Random();
                column = (short) rand.nextInt(7);
                if (findNextSpot(temp[column]) > 5 || !safeBranches[column])
                    redo = true;
                else redo = false;
            } while (redo);
        }

        row = findNextSpot(temp[column]);
        temp[column][row] = aiValue;
        board = temp;
        gameCount++;
        dropping = updateBoardSituation(column, row);
        dropping.play();
        isUserTurn = true;
    }

    public static short analyzeMaxBranch(short[][] input, int branchDepth) {
        BitSet memoryResults;
        short row, valueOfBranch, maxBranch;
        short[][] temp = new short[7][6];

        for (int j = 0; j < temp.length; j++) {
            temp[j] = input[j].clone();
        }

        // 1. check memory
        // 2. if not in memory then check branches

        maxBranch = -100;
        if (branchDepth < BRANCHLIMIT) {
            memoryResults = searchMemory(encodeBoard(temp));
            if (memoryResults.cardinality() > 0) {
                //pull info in the winDistance
                //record to the least sig digit
                //9 = close, decrement by one for every step
                // results is equal to a one digit number
                //return some value. must be single digit to not alert the mini
                return 0;
            } else {
                for (short i = 0; i < 7; i++) {

                    for (int j = 0; j < temp.length; j++)
                        temp[j] = input[j].clone();

                    row = findNextSpot(temp[i]);
                    if (row > 5 || row < 0)
                        continue;
                    else {
                        temp[i][row] = aiValue;
                    }
                    if (checkWin(i, row, temp)) {
                        valueOfBranch = 90;  //90 states it is a win
                    } else {
                        valueOfBranch = analyzeMinBranch(temp, branchDepth + 1);
                        if (valueOfBranch == 100)
                            valueOfBranch = -100;
                        if (valueOfBranch > 19)
                            valueOfBranch = (short) (valueOfBranch - 10);
                        else if (valueOfBranch < -19)
                            valueOfBranch = (short) (valueOfBranch + 10);
                        if (valueOfBranch / 10 > 1)
                            valueOfBranch = (short) (valueOfBranch - 1); // mem cannot be negative
                    }
                    if (isLargerThanMax(valueOfBranch, maxBranch))
                        maxBranch = valueOfBranch;
                }
                return maxBranch;
            }
        } else return 0;
    }

    public static boolean isLargerThanMax(short newBranch, short maxBranch) {
        short newBranchValue, newMemValue, maxBranchValue, maxMemValue, newRep, maxRep;

        newRep = maxRep = 0;

        if (Math.abs(newBranch) == 100 && Math.abs(maxBranch) == 100)
            return false;
        else if (Math.abs(maxBranch) == 100)
            return true;

        newBranchValue = (short) (newBranch / 10);
        newMemValue = (short) (newBranch % 10);
        if (newMemValue > 4)
            newMemValue = (short) (2 * newMemValue - 9);
        else if (newMemValue > 0)
            newMemValue = 1;
        else newMemValue = 0;

        maxBranchValue = (short) (maxBranch / 10);
        maxMemValue = (short) (maxBranch % 10);
        if (maxMemValue > 4)
            maxMemValue = (short) (2 * maxMemValue - 9);
        else if (maxMemValue > 0)
            maxMemValue = 1;
        else maxMemValue = 0;


        if (newBranchValue == newMemValue) {
            if (newMemValue > 1) {
                newRep = newMemValue;
            }
        } else if (newBranchValue < 0) {
            newRep = newBranchValue;
        } else if (newMemValue > newBranchValue) {
            newRep = newMemValue;
        } else {
            newRep = newBranchValue;
        }

        if (maxBranchValue == maxMemValue) {
            if (maxMemValue > 1) {
                maxRep = maxMemValue;
            }
        } else if (maxBranchValue < 0) {
            maxRep = maxBranchValue;
        } else if (maxMemValue > maxBranchValue) {
            maxRep = maxMemValue;
        } else {
            maxRep = maxBranchValue;
        }

        if (newRep > maxRep) return true;
        else if (newRep < maxRep) return false;
        else if (newBranch % 10 > maxBranch % 10)
            return true;
        else return false;


    }

    public static short analyzeMinBranch(short[][] input, int branchDepth) {
        short row, valueOfBranch, minBranch;

        short[][] temp = new short[7][6];

        //  check branches
        minBranch = 100;

        if (branchDepth < BRANCHLIMIT) {
            for (short i = 0; i < 7; i++) {

                for (int j = 0; j < temp.length; j++)
                    temp[j] = input[j].clone();

                row = findNextSpot(temp[i]);
                if (row > 5 || row < 0) continue;
                else {
                    temp[i][row] = userValue;
                }
                if (checkWin(i, row, temp)) {
                    valueOfBranch = -90;  //90 states it is a win
                } else {
                    valueOfBranch = analyzeMaxBranch(temp, branchDepth + 1);
                    if (valueOfBranch == -100)
                        valueOfBranch = 100;
                    else {
                        if (valueOfBranch > 19)
                            valueOfBranch = (short) (valueOfBranch - 10);
                        else if (valueOfBranch < -19)
                            valueOfBranch = (short) (valueOfBranch + 10);
                    }
                }
                if (valueOfBranch / 10 <= minBranch / 10 && valueOfBranch != 100) {
                    if (valueOfBranch % 10 >= minBranch % 10 && minBranch != 100) {
                        minBranch = valueOfBranch;
                    } else if (valueOfBranch / 10 < minBranch / 10) {
                        minBranch = valueOfBranch;
                    }
                }
            }
            return minBranch;
        } else return 0;
    }


    public static BitSet searchMemory(BitSet command) {
        //search hashmap full of saved games
        //returns 0000000 if none found
        //returns 7 decision bits if found
        BitSet results = new BitSet(7);
        return results;
    }

    public static BitSet encodeBoard(short[][] temp) {
        BitSet results;
        short count;
        boolean isZero, isLeftToRight;
        int column;

        results = new BitSet(DATASIZE);

        count = 0;

        isLeftToRight = findLowestColumn(temp);
        if (isLeftToRight) column = 0;
        else column = 6;

        while (column < 7 && column > -1) {
            isZero = false;
            for (int row = 0; row < 6; row++) {
                if (isZero) {
                    results.set(count, (boolean) results.get(count - 1));
                } else if (temp[column][row] == aiValue) {
                    results.set(count, true);
                    isZero = false;
                } else if (temp[column][row] == userValue) {
                    results.set(count, false);
                    isZero = false;
                } else if (row == 0 && temp[column][0] == 0) {
                    results.set(count, false);
                    isZero = true;
                } else {
                    results.set(count, !(boolean) results.get(count - 1));
                    isZero = true;
                }
                count++;
            }
            if (isZero)
                results.set(count, results.get(count - 1));
            else
                results.set(count, !results.get(count - 1));
            count++;
            if (isLeftToRight) column++;
            else column--;
        }
        results.set(DATASIZE - 1, isLeftToRight);
        return results;
    }

    public static boolean findLowestColumn(short[][] temp) {
        int diff;

        for (int column = 0; column < 3; column++) {
            for (int row = 0; row < 6; row++) {
                diff = temp[column][row] - temp[6 - column][row];
                if (diff > 0) {
                    //System.out.println(column + " : " + row);
                    return true;
                } else if (diff < 0) {
                    //System.out.println(column + " : " + row);
                    return false;
                }
            }
        }
        return true;

    }

    public static short[][] decodeBoard(BitSet command) {
        short[][] temp;
        boolean current;
        short count, bitCount, i;

        temp = new short[7][6];

        if (command.get(DATASIZE - 1)) i = 0;
        else i = 6;

        while (i < 7 && i > -1) {    //7 columns

            bitCount = (short) (7 * (i + 1) - 1);
            count = 1;
            current = command.get(bitCount);


            while (count < 7) {
                if (current == command.get(bitCount - count))
                    count++;
                else break;
            }
            while (count < 7) {
                current = command.get(bitCount - count);
                if (current) {                    //ai is true
                    temp[i][6 - count] = aiValue;
                } else {                             //user is false
                    temp[i][6 - count] = userValue;
                }
                count++;
            }
            if (command.get(DATASIZE - 1)) i++;
            else i--;
        }
        return temp;
    }

    public static TranslateTransition updateBoardSituation(short column, short row) {
        chip temp;
        TranslateTransition dropping;
        temp = new chip(board[column][row]);
        dropping = temp.placeChip(column, row);


        printConsoleBoard(board);


        if(checkWin(column, row, board)){
            Stage victoryStage=new Stage();
            Label victoryText=new Label();





            if(isUserTurn){
                victoryText.setText(playerName+", you have won!");
                winners.add(playerName);
            }
            else if(!isUserTurn){
                victoryText.setText("The Computer has won!");
            }

            writeFile();
            victoryText.setAlignment(Pos.CENTER);
            Scene victoryScene=new Scene(victoryText,200,200);
            victoryText.setStyle("-fx-background-color: #cccccc");
            victoryStage.setScene(victoryScene);
            victoryStage.show();

        }

        else if(gameCount >= 42){
            System.out.println("Draw");
            //save game
        }

        if(!isUserTurn){
            currentPlayer.setText(" Player");
            currentPlayer.relocate(360,630);

        }
        else{
            currentPlayer.setText(" Computer");
            currentPlayer.relocate(360,630);

        }
        return dropping;
    }
    public static void writeFile(){
        try{
            f=new Formatter("C:\\Users\\awelts\\IdeaProjects\\connect4Gui\\src\\connect4\\winners.txt");
        }catch(Exception e){
            System.out.println(e);
        }

        for(int i=0;i<winners.size();i++){
            f.format(winners.get(i)+"\n");
        }
        f.close();
    }

    public static void printConsoleBoard(short[][] temp) {
        for (int row = 5; row >= 0; row--) {
            for (int column = 0; column <= 6; column++) {
                System.out.print(" " + temp[column][row] + " ");
            }
            System.out.println("");
        }
        System.out.println(" A  B  C  D  E  F  G");
    }

    public static boolean checkWin(short column, short row, short[][] temp) {
        short sum;
        sum = 0;

        sum += checkHorizontal(column, row, temp);
        sum += checkVertical(column, row, temp);
        sum += checkForwardSlash(column, row, temp);
        sum += checkBackwardSlash(column, row, temp);
        if (sum > 0) return true;
        else return false;
    }

    public static short checkHorizontal(short column, short row, short[][] temp) {
        int sum;
        sum = 0;

        for (int i = 1; (column - i) >= 0; i++) {
            if (temp[column - i][row] == (temp[column][row])) {
                sum++;
            } else break;
        }
        for (int k = 1; (column + k) <= 6; k++) {
            if (temp[column + k][row] == (temp[column][row])) {
                sum++;
            } else break;
        }
        if (sum >= 3) return 1;
        else return 0;
    }

    public static short checkVertical(short column, short row, short temp[][]) {
        int sum;
        sum = 0;

        for (int i = 1; (row - i) >= 0; i++) {
            if (temp[column][row - i] == (temp[column][row])) {
                sum++;
            } else break;
        }
        for (int k = 1; (row + k) <= 5; k++) {
            if (temp[column][row + k] == (temp[column][row])) {
                sum++;
            } else break;
        }
        if (sum >= 3) return 1;
        else return 0;
    }

    public static short checkForwardSlash(int column, int row, short temp[][]) {
        int sum;
        sum = 0;

        for (int i = 1; ((column - i) >= 0) && ((row + i) <= 5); i++) {
            if (temp[column - i][row + i] == (temp[column][row])) {
                sum++;
            } else break;
        }
        for (int k = 1; ((column + k) <= 6) && ((row - k) >= 0); k++) {
            if (temp[column + k][row - k] == (temp[column][row])) {
                sum++;
            } else break;
        }
        if (sum >= 3) return 1;
        else return 0;
    }

    public static short checkBackwardSlash(int column, int row, short temp[][]) {
        int sum;
        sum = 0;

        for (int i = 1; ((column - i) >= 0) && ((row - i) >= 0); i++) {
            if (temp[column - i][row - i] == (temp[column][row])) {
                sum++;
            } else break;
        }
        for (int k = 1; ((column + k) <= 6) && ((row + k) <= 5); k++) {
            if (temp[column + k][row + k] == (temp[column][row])) {
                sum++;
            } else break;
        }
        if (sum >= 3) return 1;
        else return 0;
    }

    public static short findNextSpot(short[] column) {
        short results;
        results = -1;

        for (int i = 0; i < column.length; i++) {
            if (column[i] == 0) {
                results = (short) i;
                break;
            }
        }
        return results;
    }

    public static void makeLines() {
        ImageView coverImage;
        Image tempI;
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
        root.getChildren().add(gameBoardPane);
        coverImage = new ImageView();
        coverImage.setFitHeight(620);
        coverImage.setFitWidth(720);
        tempI = new Image(connect4GUI.class.getResourceAsStream("CoverBoard.png"));
        coverImage.setImage(tempI);
        coverPane.getChildren().add(coverImage);
        root.getChildren().add(chipPane);
        root.getChildren().add(coverPane);

    }
    public static void main (String[]args){
        launch(args);
    }
}
