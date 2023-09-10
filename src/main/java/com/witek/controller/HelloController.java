package com.witek.controller;

import com.witek.model.ExcelReader;
import com.witek.model.FunctionPlot;
import com.witek.model.OptimizationParameter;
import com.witek.model.Swarm;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class HelloController {

    public VBox dynamicGraphs;
    ExecutorService executorService = Executors.newCachedThreadPool();
    Swarm swarm;
    Boolean swarmNotStarted = true;
    FunctionPlot functionPlot;
    private ObservableList<OptimizationParameter> params;

    @FXML
    public void initialize(){
        swarm = new Swarm(1,0,1);
        params = FXCollections.observableArrayList(swarm.getParameters());

        //Tworzenie dynamiczne checkboxów na podstawie parametrów
        prepareCoeficientCheckBoxes();

        stopButton.setOnAction(actionEvent -> {
            stopButton.setDisable(true);
            startButton.setDisable(false);
            if(swarm != null){
                swarm.setRunning(false);
            }
        });
    }

    private void prepareCoeficientCheckBoxes() {
        OptimizationParameter[] parameters = swarm.getParameters();
        GridPane gridPane = new GridPane();
        for (int i = 0; i < parameters.length ; i ++) {
            OptimizationParameter parameter = parameters[i];
            CheckBox checkBox = new CheckBox("param" + i);
            checkBox.setSelected(true);
            int finalI = i;
            checkBox.setOnAction(event ->{
                if(checkBox.isSelected()){
                    parameter.setOptimize(true);
                    System.out.println("param" + finalI + " został właczony");
                }else {
                    parameter.setOptimize(false);
                    System.out.println("param" + finalI + " został wyłaczony");
                }
            });
            gridPane.add(checkBox,i,0);
        }
        coefficientsFields.getChildren().add(gridPane);
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
    private TextField particles;

    public HelloController() throws IOException {
    }

    @FXML
    protected void onStartOptButtonClick() {
        this.functionPlot = new FunctionPlot(ExcelReader.getObjectPropertiesExcel("Dane_lab5.xlsx"));
        functionPlot.initialize();
        dynamicGraphs.getChildren().add(functionPlot.getGrid());
        swarm.addBestPositionObserver(()->{
            functionPlot.updatePlots(swarm.getBestPosition().getCordinates());
        });
        startButton.setDisable(true);
        stopButton.setDisable(false);
        int particles = Integer.parseInt(this.particles.getText());
        if(swarmNotStarted){
            swarm.setNumOfParticles(particles);
            swarm.initializeSwarm();
            swarmNotStarted = false;
            this.particles.setDisable(true);
            } else
            swarm.reInitializeSwarm();
        //welcomeText.setText("Symulacja została uruchomiona z : " + this.particles.getText() + " czastkami");
        //welcomeText.setTextFill(Paint.valueOf("crimson"));

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