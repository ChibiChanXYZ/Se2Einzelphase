package com.example.se2einzelphase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {
    TextView responseServerVisual;
    EditText matrNrVisual;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Variablen:
        responseServerVisual = findViewById(R.id.MessageServer);
        matrNrVisual = findViewById(R.id.MatrNr);
    }

    //Sends to server
    public void clickButtonSend(View view) throws InterruptedException{
        String matrNrString=  matrNrVisual.getText().toString();                //read out the String from Field MatrNrVisual

        ThreadForButton threadForServerConnection = new ThreadForButton(matrNrString);      //make new Thread for building the connection

        threadForServerConnection.start();                                          //call start for run methode to be called (multithreading just works with start not with run)

        threadForServerConnection.join();                                           //waiting for thread to finish

        responseServerVisual.setText(threadForServerConnection.getResponseServer());        //set response text in app with getter methode from Thread

    }

    //Sorts MartNr with even numbers first then uneven numbers
    public void clickButtonCalculateThenSortMatrNr(View view) {
        String matrNrString = matrNrVisual.getText().toString();
        char[] matrNrCharArray = matrNrString.toCharArray();

        Arrays.sort(matrNrCharArray);

        ArrayList<Character> evenNumbers = new ArrayList<>();
        ArrayList<Character> unevenNumbers = new ArrayList<>();


        for (int i = 0; i < matrNrCharArray.length; i++) {
            if (matrNrCharArray[i] % 2 == 0) {
                evenNumbers.add(matrNrCharArray[i]);
            } else {
                unevenNumbers.add(matrNrCharArray[i]);
            }
        }

        ArrayList<Character> modifiedMatrNrList = new ArrayList<Character>();
        modifiedMatrNrList.addAll(evenNumbers);
        modifiedMatrNrList.addAll(unevenNumbers);

        responseServerVisual.setText(modifiedMatrNrList.toString());

    }
}


 class ThreadForButton extends Thread {
     String matrNrToServer;
     String responseServer;

     ThreadForButton(String matrNr){
         matrNrToServer = matrNr;
     }

     String getResponseServer(){
         return responseServer;
     }

    public void run(){
        try {
            Socket clientSocket = new Socket("se2-isys.aau.at", 53212);         //Erstellen einer Verknüpfung zum Server

            //Erstellen von Streams zum Versenden und Empfangen von Daten vom Server
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            outToServer.writeBytes(matrNrToServer + '\n');                          //versenden unserer Daten zum Server

            responseServer = inFromServer.readLine();                                //lesen der Daten vom Server

            clientSocket.close();                                               //Verbindung wieder schließen

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}