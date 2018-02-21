package sample;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import java.text.DecimalFormat;
import java.util.Random;
import static sample.Main.*;
import static sample.Simulation.TIMEOP;

public class Customer {
    public static final int MINSERVICE = 2;
    public static final int MAXSERVICE = 5;
    public static final int MINARRIVAL = 1;
    public static final int MAXARRIVAL = 5;

    public static int sumWait;
    public static DecimalFormat avFormat = new DecimalFormat("##0.#");
    public final static int carLength = 70;
    int arrive, service, start;
    ImageView car;

    public Customer(int a){
        Image temp;
        arrive = genRand(MINARRIVAL, MAXARRIVAL) + a;
        service =  genRand(MINSERVICE, MAXSERVICE);
        car = new ImageView();
        car.setFitHeight(carLength*.5);
        car.setFitWidth(carLength*1.15);
        temp = new Image(Main.class.getResourceAsStream(getAddress()));
        car.setImage(temp);

        System.out.println("Hello new customer.");

        carPane.getChildren().add(this.car);
    }
    public int waitTime() {return start - arrive; }

    public SequentialTransition stopAtWash(int spot) {
        SequentialTransition enterLine;
        enterLine = new SequentialTransition(this.car);

        TranslateTransition approach = new TranslateTransition(Duration.millis(TIMEOP*1));
        approach.setFromY(mainRoadY);
        approach.setFromX(originX);
        approach.setToX(driveWayX);

        TranslateTransition admit = new TranslateTransition(Duration.millis(TIMEOP*1));
        admit.setFromY(mainRoadY);
        admit.setFromX(driveWayX);
        admit.setToY(washLineY);

        TranslateTransition nextInLine = new TranslateTransition(Duration.millis(TIMEOP*2));
        nextInLine.setFromY(washLineY);
        nextInLine.setFromX(driveWayX);
        nextInLine.setToX(driveWayX - (500 - (carLength)*spot));

        enterLine.getChildren().addAll(approach, admit, nextInLine);

        sim.waitQ.add(this);

        return enterLine;
    }
    public SequentialTransition exitWash(){
        SequentialTransition results;
        TranslateTransition out, down, exit;
        results = new SequentialTransition(this.car);

        out = new TranslateTransition(Duration.millis(TIMEOP*1));
        out.setFromY(washLineY);
        out.setFromX(driveWayX - (500 + carLength));
        out.setToX(driveWayX - (750 + carLength));

        down = new TranslateTransition(Duration.millis(TIMEOP*1));
        down.setFromY(washLineY);
        down.setFromX(driveWayX - (750 + carLength));
        down.setToY(mainRoadY);

        exit = new TranslateTransition(Duration.millis(TIMEOP*1));
        exit.setFromY(mainRoadY);
        exit.setFromX(driveWayX - (750 + carLength));
        exit.setToX(0);

        results.getChildren().addAll(out, down, exit);
        results.setOnFinished(event ->
                updateService());

        return results;
    }
    public SequentialTransition enterWash(){
        int i;
        SequentialTransition first, results;
        TranslateTransition admitCar, moveUp;
        ParallelTransition nextInLine, following;

        first = new SequentialTransition(this.car);

        admitCar = new TranslateTransition(Duration.millis(TIMEOP*1));
        admitCar.setFromY(washLineY);
        admitCar.setFromX(driveWayX - 500);
        admitCar.setToX(driveWayX - (500 + carLength));

        first.getChildren().add(admitCar);
        first.setOnFinished(event ->
                serviceTypeT.setText(this.service + " min"));

        results = new SequentialTransition();
        results.getChildren().add(first);

        i = 0;
        if(sim.waitQ.size > 0) {
            nextInLine = new ParallelTransition();
            while (i < sim.waitQ.size) {

                moveUp = new TranslateTransition(Duration.millis(TIMEOP*.8));
                moveUp.setFromY(washLineY);
                moveUp.setFromX(driveWayX - (500 - (carLength) * (i + 1)));
                moveUp.setToX(driveWayX - (500 - (carLength) * (i)));

                following = new ParallelTransition(((Customer) (sim.waitQ.show(i))).car);
                following.getChildren().add(moveUp);
                nextInLine.getChildren().add(following);
                i++;
            }
            results.getChildren().add(nextInLine);
        }
        return results;
    }
    public SequentialTransition bypassWash(){
        TranslateTransition driveBy;
        SequentialTransition results;
        results = new SequentialTransition(this.car);
        driveBy = new TranslateTransition(Duration.millis(TIMEOP*3));
        driveBy.setFromY(mainRoadY);
        driveBy.setFromX(originX);
        driveBy.setToX(originX - (carLength + backgroundX));
        driveBy.setCycleCount(1);
        sim.bypassed++;
        bypassedT.setText(sim.bypassed + "");
        results.getChildren().add(driveBy);
        return results;
    }
    public void updateService(){
        servicedT.setText(sim.serviced + "");
        maxWaitT.setText(sim.longestWait + " min");
        avWaitT.setText(avFormat.format((double)sumWait/sim.serviced) + " min");
    }
    public String getAddress(){
        int command;
        command = genRand(0, 4);
        switch (command){
            case 0:
                return "blue_car_V4.png";
            case 1:
                return "green_car_V4.png";
            case 2:
                return "red_car_V4.png";
            case 3:
                return "yellow_car_v4.png";
            default:
                System.exit(-7);
        }
        return "error";
    }
    public static int genRand(int min, int max){
        Random rand;
        int range;
        range = max - min;
        rand  = new Random();
        return rand.nextInt(range)+ min;
    }
}



