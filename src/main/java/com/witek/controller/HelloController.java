package com.witek.controller;

import com.witek.model.Swarm;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class HelloController {

    ExecutorService executorService = Executors.newCachedThreadPool();
    Swarm swarm;

    @FXML
    public void initialize(){
        swarm = new Swarm(1,0,1);
        swarm.addBestEvalObserver(()-> {
            double bestEval = swarm.getBestEval();
            Platform.runLater(() -> {
                welcomeText.setText(String.valueOf(bestEval));
            });
        });
        stopButton.setOnAction(actionEvent -> {
            stopButton.setDisable(true);
            startButton.setDisable(false);
            if(swarm != null){
                swarm.setRunning(false);
            }
        });
    }

    @FXML
    public VBox coefficientsFields;

    @FXML
    public Button startButton;

    @FXML
    public Button stopButton;

    @FXML
    public Label bestEval;

    @FXML
    private Label welcomeText;

    @FXML
    private TextField particles;

    public HelloController() throws IOException {
    }

    @FXML
    protected void onStartOptButtonClick() {
        startButton.setDisable(true);
        stopButton.setDisable(false);
        int particles = Integer.parseInt(this.particles.getText());
        swarm.setNumOfParticles(particles);
        welcomeText.setText("Symulacja została uruchomiona z : " + this.particles.getText() + " czastkami");
        welcomeText.setTextFill(Paint.valueOf("crimson"));
        executorService.execute(swarm);
        swarm.setRunning(true);
        System.out.println("Symulacja uruchomiona");
    }

    @FXML
    public void onStopOptButtonClick() {
        if(swarm != null){
            swarm.setRunning(false);
        }
        System.out.println("Symulacja zatrzymana");
        executorService.shutdown();
        if(executorService.isShutdown()){
            System.out.println("EXECUTOR SHUTDOWN");
        }
    }

    private void processTSNE(){

    }
}