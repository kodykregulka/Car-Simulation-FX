package sample;
import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.event.ActionEvent;
import javafx.scene.text.Text;

public class Main extends Application {
    public static final int backgroundX = 1000;
    public static final int originX = backgroundX + Customer.carLength;
    public static final int driveWayX = originX - 135;
    public static final double backgroundY = backgroundX * .65;
    public static final double mainRoadY = .85 * backgroundY;
    public static final double washLineY = mainRoadY - 150;

    public static Pane rootPane, bottom, carPane, overLap;
    public static Scene mainScene;
    public static Simulation sim;

    public static Button startButton, addHourButton, addMinButton, addCustomerButton;
    public static TextField hourText, minuteText;
    public static Text addedTimeT, timeT, idleTimeT, serviceTypeT;
    public static Text maxWaitT, avWaitT, servicedT, bypassedT;

    public static ImageView worker;

    @Override
    public void start(Stage primaryStage) throws Exception {

        setup();
        primaryStage.setScene(mainScene);
        primaryStage.setTitle("Ethan's Car Wash");
        primaryStage.show();

        startButton.setOnAction((ActionEvent e) -> {
            sim.runSimulation();
        });
        addHourButton.setOnAction((ActionEvent e) -> {
            sim.simTime =Integer.parseInt(hourText.getText())*60 + sim.simTime;

            addedTimeT.setText((sim.updateCountdown()));
        });
        addMinButton.setOnAction((ActionEvent e) -> {
            sim.simTime = Integer.parseInt(minuteText.getText()) + sim.simTime;
            addedTimeT.setText(sim.updateCountdown());
        });
        addCustomerButton.setOnAction((ActionEvent e) -> {
            sim.temp = new Customer(sim.time);
            sim.temp.arrive = sim.time;

            if(!sim.waitQ.isFull()){
                (new SequentialTransition(sim.temp.stopAtWash(sim.waitQ.size))).play();
            } else{
                (new SequentialTransition(sim.temp.bypassWash())).play();
            }
            sim.temp = new Customer(sim.time);
        });
    }

    public static void setup(){
        int offsetY;
        ImageView background, whiteBox1, whiteBox2, garageCover, sign;
        Image backgroundI, whiteBoxI, garageCoverI, signI;
        Image workerI;
        HBox hb;

        offsetY  = 20;
        rootPane = new Pane();
        bottom   = new Pane();
        carPane  = new Pane();
        overLap  = new Pane();
        sim      = new Simulation();

        mainScene = new Scene(rootPane, (Customer.carLength * 2) +
                backgroundX, backgroundY + 100, Color.WHITE);

        background = new ImageView();
        background.setFitHeight(backgroundY);
        background.setFitWidth(backgroundX);
        backgroundI = new Image(Main.class.getResourceAsStream(
                "background_1.png"));
        background.setImage(backgroundI);
        background.relocate(Customer.carLength, offsetY);
        bottom.getChildren().add(background);

        whiteBox1 = new ImageView();
        whiteBox1.setFitHeight(backgroundY);
        whiteBox1.setFitWidth(Customer.carLength);
        whiteBoxI = new Image(Main.class.getResourceAsStream(
                "white_box.png"));
        whiteBox1.setImage(whiteBoxI);
        whiteBox1.relocate(2, 0);

        whiteBox2 = new ImageView();
        whiteBox2.setFitHeight(backgroundY);
        whiteBox2.setFitWidth(Customer.carLength);
        whiteBox2.setImage(whiteBoxI);
        whiteBox2.relocate(Customer.carLength + backgroundX, 0);

        garageCover = new ImageView();
        garageCover.setFitHeight(backgroundY*.28);
        garageCover.setFitWidth(backgroundX*.23);
        garageCoverI = new Image(Main.class.getResourceAsStream(
                "garage_cover.png"));
        garageCover.setImage(garageCoverI);
        garageCover.relocate(Customer.carLength +
                backgroundX*.134, offsetY + backgroundY*.452);

        sign = new ImageView();
        sign.setFitHeight(backgroundY*.095);
        sign.setFitWidth(backgroundX*.08);
        signI = new Image(Main.class.getResourceAsStream(
                "bypassed_sign.png"));
        sign.setImage(signI);
        sign.relocate(Customer.carLength + backgroundX*.8,
                offsetY + backgroundY*.693);

        worker = new ImageView();
        worker.setFitHeight(backgroundY*.1);
        worker.setFitWidth(backgroundX*.05);
        workerI = new Image(Main.class.getResourceAsStream(
                "stick_man.png"));
        worker.setImage(workerI);
        worker.relocate(Customer.carLength + backgroundX*.134,
                offsetY + backgroundY*.65);

        hb                = new HBox(15);
        hb.setPadding(new Insets(20));

        addedTimeT        = new Text("Added Time: 0:00");
        hb.getChildren().add(addedTimeT);

        startButton       = new Button("Start");
        hb.getChildren().add(startButton);

        addHourButton     = new Button("Add Hour");
        hb.getChildren().add(addHourButton);

        hourText          = new TextField();
        hb.getChildren().add(hourText);
        hourText.setPrefWidth(100);

        addMinButton      = new Button("Add Minute");
        hb.getChildren().add(addMinButton);

        minuteText        = new TextField();
        hb.getChildren().add(minuteText);
        minuteText.setPrefWidth(100);

        addCustomerButton = new Button("Add Customer");
        hb.getChildren().add(addCustomerButton);

        hb.relocate(Customer.carLength + 125,backgroundY + offsetY);
        overLap.getChildren().addAll(whiteBox1, whiteBox2, garageCover, sign, hb);

        bypassedT         = new Text(sim.bypassed + "");
        bypassedT.setFill(Color.WHITE);
        bypassedT.relocate(Customer.carLength +
                backgroundX*.865, offsetY + backgroundY*.738);

        maxWaitT          = new Text(sim.longestWait + " min");
        maxWaitT.setFill(Color.WHITE);
        maxWaitT.relocate(Customer.carLength +
                backgroundX*.88, offsetY + backgroundY*.23);

        servicedT         = new Text(sim.serviced + "");
        servicedT.setFill(Color.WHITE);
        servicedT.relocate(Customer.carLength +
                backgroundX*.93, offsetY + backgroundY*.3);

        overLap.getChildren().addAll(bypassedT, maxWaitT, servicedT, worker);

        timeT             = new Text(0 + ":" + "00");
        timeT.setFill(Color.WHITE);
        timeT.relocate(Customer.carLength +
                backgroundX*.265, offsetY + backgroundY*.507);

        idleTimeT         = new Text(sim.idleTime + " min");
        idleTimeT.setFill(Color.WHITE);
        idleTimeT.relocate(Customer.carLength +
                backgroundX*.22, offsetY + backgroundY*.638);

        serviceTypeT      = new Text("0 min");
        serviceTypeT.setFill(Color.WHITE);
        serviceTypeT.relocate(Customer.carLength +
                backgroundX*.3, offsetY + backgroundY*.63);

        avWaitT           = new Text("0 min");
        avWaitT.setFill(Color.WHITE);
        avWaitT.relocate(Customer.carLength +
                backgroundX*.88, offsetY + backgroundY*.264);

        overLap.getChildren().addAll(timeT, idleTimeT, serviceTypeT, avWaitT);

        rootPane.getChildren().addAll(bottom, carPane, overLap);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

