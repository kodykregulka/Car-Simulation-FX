package sample;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.util.Duration;
import java.text.DecimalFormat;

import static sample.Main.*;

public class Simulation {

    public static final int TIMEOP = 1000;
    public static SequentialTransition currentAnimation;
    public static DecimalFormat timeFormat = new DecimalFormat("00");

    int prevTime, idleTime, simTime, serviced, time, bypassed, longestWait, visitCount;
    boolean free, isVisibleStickMan;
    Customer temp, currentCar;
    MyQueue waitQ;

    public Simulation(){
        prevTime = 0;
        idleTime = 0;
        time = 0;
        bypassed = 0;
        free = true;
        isVisibleStickMan = true;
        temp = new Customer(prevTime);
        waitQ = new MyQueue();
    }
    public void runSimulation(){
        PauseTransition pause;

        if(time < simTime) {
            currentAnimation = new SequentialTransition();

            if (temp.arrive == time) {
                addToLine(temp);
                temp = new Customer(time);
            }
            free = finishWash(time, free);
            free = serviceNewCar(time, free);
            if (free) {
                showStickMan();
                idleTime++;
            }
            time++;
            pause = new PauseTransition(Duration.millis(TIMEOP/2));
            pause.setOnFinished(event ->
                    updateTime());
            currentAnimation.getChildren().add(pause);
            currentAnimation.play();
            currentAnimation.setOnFinished(event ->
                    runSimulation());
        }
    }
    public void updateTime(){
        int hour, min;
        hour = time/60;
        min  = time%60;
        timeT.setText(hour + ":" + timeFormat.format(min));
        idleTimeT.setText(idleTime + " min");
        addedTimeT.setText(updateCountdown());
    }
    public String updateCountdown(){
        int timeLeft, hourValue, minuteValue;
        timeLeft = simTime - time;
        hourValue = timeLeft/60;
        minuteValue = timeLeft%60;
        return ("Added Time: " +
                hourValue + ":" + timeFormat.format(minuteValue));
    }
    public void addToLine(Customer temp){
        visitCount++;
        if(!waitQ.isFull()) {
            currentAnimation.getChildren().add(temp.stopAtWash(waitQ.size));
        } else currentAnimation.getChildren().add(temp.bypassWash());
    }
    public boolean finishWash(int time, boolean free){
        if(free)return true;

        if(!(free) && (time - currentCar.start == currentCar.service)){
            currentCar.sumWait = currentCar.waitTime() + currentCar.sumWait;

            if(currentCar.waitTime() > longestWait){
                longestWait = currentCar.waitTime();
            }
            serviced++;
            currentAnimation.getChildren().add( currentCar.exitWash());
            return true;
        } else return false;
    }
    public boolean serviceNewCar(int time, boolean free){
        if(!free)return false;
        if(free && !waitQ.isEmpty()) {
            currentCar       = (Customer) waitQ.remove();
            currentCar.start = time;
            currentAnimation.getChildren().add(currentCar.enterWash());
            hideStickMan();
            return false;
        } else return true;
    }
    public void hideStickMan(){
        FadeTransition backToWork;
        if(isVisibleStickMan) {
            backToWork = new FadeTransition(Duration.millis(500),worker);
            backToWork.setFromValue(1.0);
            backToWork.setToValue(0);
            currentAnimation.getChildren().add(backToWork);
            isVisibleStickMan = false;
        }
    }
    public void showStickMan(){
        FadeTransition waitOutside;
        if(!isVisibleStickMan){
            waitOutside = new FadeTransition(Duration.millis(500),worker);
            waitOutside.setFromValue(0);
            waitOutside.setToValue(1.0);
            currentAnimation.getChildren().add(waitOutside);
            isVisibleStickMan = true;
        }
    }
}
