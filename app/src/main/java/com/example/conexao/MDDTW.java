package com.example.conexao;



import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MDDTW {

    private double distancia = 0;

    public MDDTW(SensorData caminho1, SensorData caminho2) {

        int sizeCaminho1 = caminho1.getDataSize();
        List<Double> caminho1X;
        List<Double> caminho1Y;
        List<Double> caminho1Z;

        int sizeCaminho2 = caminho2.getDataSize();
        List<Double> caminho2X;
        List<Double> caminho2Y;
        List<Double> caminho2Z;

        Log.d("SIZES", "" + sizeCaminho1 + " e " + sizeCaminho2);

        caminho1X = caminho1.getXData();
        caminho1Y = caminho1.getYData();
        caminho1Z = caminho1.getZData();

        caminho2X = caminho2.getXData();
        caminho2Y = caminho2.getYData();
        caminho2Z = caminho2.getZData();

        caminho1X = normalizacaoZScore(caminho1X);
        caminho1Y = normalizacaoZScore(caminho1Y);
        caminho1Z = normalizacaoZScore(caminho1Z);

        caminho2X = normalizacaoZScore(caminho2X);
        caminho2Y = normalizacaoZScore(caminho2Y);
        caminho2Z = normalizacaoZScore(caminho2Z);

        Double somatorio = 0.0;
        double[][] distancia = new double[caminho1X.size()][caminho2X.size()];
        for (int i = 0; i < caminho1X.size(); i++) {
            for (int j = 0; j < caminho2Y.size(); j++) {

                somatorio = 0.0;
                somatorio += Math.pow(caminho1X.get(i) - caminho2X.get(j), 2);
                somatorio += Math.pow(caminho1Y.get(i) - caminho2Y.get(j), 2);
                somatorio += Math.pow(caminho1Z.get(i) - caminho2Z.get(j), 2);

                distancia[i][j] = somatorio;
            }
        }

        double[][] distanciaAcumulada = new double[caminho1X.size()][caminho2X.size()];
        distanciaAcumulada[0][0] = distancia[0][0];

        for (int cont = 1; cont < caminho1X.size(); cont++) {
            distanciaAcumulada[cont][0] = distancia[cont][0] + distanciaAcumulada[cont-1][0];
        }

        for (int cont = 1; cont < caminho2X.size(); cont++) {
            distanciaAcumulada[0][cont] = distancia[0][cont] + distanciaAcumulada[0][cont-1];
        }

        for (int contLinha = 1; contLinha < caminho1X.size(); contLinha++) {
            for (int contColuna = 1; contColuna < caminho2X.size(); contColuna++) {
                distanciaAcumulada[contLinha][contColuna] = distancia[contLinha][contColuna]+ getMenorNumero(
                        distanciaAcumulada[contLinha-1][contColuna],
                        distanciaAcumulada[contLinha-1][contColuna-1],
                        distanciaAcumulada[contLinha][contColuna-1]);
            }
        }
        this.distancia = distanciaAcumulada[caminho1X.size()-1][caminho2X.size()-1];
    }

    private List<Double> normalizacaoZScore(List<Double> vet) {
        List<Double> aux = new ArrayList<Double>(vet.size());

        Double media = getMedia(vet);
        double desvioPadrao = getDesvioPadrao(vet,media);

        if (desvioPadrao == 0) {
            for (int cont = 0; cont < vet.size(); cont++) {
                aux.add(cont, (vet.get(cont) - media));
            }
        } else {
            for (int cont = 0; cont < vet.size(); cont++) {
                aux.add(cont, (vet.get(cont) - media) / desvioPadrao);
            }
        }
        return aux;
    }

    private Double getMedia(List<Double> vet) {
        Double soma = 0.0;

        for (int cont = 0;cont < vet.size(); cont++) {
            soma += vet.get(cont);
        }
        return soma/vet.size();
    }

    private double getDesvioPadrao(List<Double> vet, Double media) {
        Double soma = 0.0;

        for (int cont = 0;cont < vet.size(); cont++) {
            soma += Math.pow((vet.get(cont)-media),2);
        }
        return Math.sqrt(soma/(vet.size()-1));
    }

    private double getMenorNumero(double num1, double num2, double num3) {
        if((num1<num2)&&(num1<num3)) {
            return num1;
        } else if ((num2<num1)&&(num2<num3)) {
            return num2;
        } else {
            return num3;
        }
    }

    private int getMenorPosicao(double num1, double num2, double num3) {
        if((num1<num2)&&(num2<num3)) {
            return 1;
        } else if ((num2<num3)&&(num3<num1)) {
            return 2;
        } else {
            return 3;
        }
    }

    public double getDistancia() {
        return this.distancia;
    }

}