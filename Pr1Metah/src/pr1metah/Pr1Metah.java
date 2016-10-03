/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pr1metah;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Xenahort
 *
 * ------- Errores pendientes ------
 *
 */
public class Pr1Metah {

    static int cubre[];
    static float ratio[];
    static int matriz[][];
    static int x, y;
    static int solucion[];
    static Pair cubreOrdenado[];

    public static void eliminaRedundancias() {
        for (int i = 0; i < x-1; i++) {
            System.out.println(cubreOrdenado[i].lugar + ":" + cubreOrdenado[i].cubre);
        }
        System.out.println("\n\n");
        
        MyQuickSort sorter = new MyQuickSort();
        sorter.sort(cubreOrdenado);
        
        for (int i = 0; i < x-1; i++) {
            System.out.println(cubreOrdenado[i].lugar + ":" + cubreOrdenado[i].cubre);
        }
        System.out.println("\n\n");
    }

    public static boolean faltanPorCubir() {
        for (int i = 1; i < x; i++) {
            if (cubre[i] != 0) {
                return true;
            }
        }
        return false;
    }

    public static void mostrarSolucion() {
        System.out.println("Solucion:");
        for (int i = 1; i < x; i++) {
            System.out.print(i + ":" + solucion[i] + " ");
        }
        System.out.println("\n");
    }

    public static void mostrarMatrizYVector() {
        for (int i = 1; i < y; i++) { // MUESTRO LA MATRIZ
            for (int j = 0; j < x; j++) {
                System.out.print(matriz[i][j] + " ");
            }
            System.out.print("\n");
        }
        System.out.println("\n Vector de cuantos cubre cada uno:");
        for (int i = 1; i < x; i++) {// MUESTRO a CUANTOS CUBRE
            System.out.print(i + ":" + cubre[i] + "\t");
        }
        System.out.println("\n Vector del ratio:");
        for (int i = 1; i < x; i++) { // MUESTRO EL RATIO
            System.out.print(i + ":" + ratio[i] + "\t");
        }
        System.out.print("\n");
    }

    public static void buscarMayorRatio() {

        int mayor = 1;
        for (int i = 2; i < x; i++) {
            if (ratio[i] >= ratio[mayor]) {
                if (ratio[i] == ratio[mayor]) {
                    if (cubre[i] < cubre[mayor]) {
                        break;
                    }
                } else {
                    mayor = i;
                }
            }
        }
        System.out.println("Aquel con mayor ratio es el numero " + mayor + "\n");
        solucion[mayor] = 1; //Establezco el que tiene mas ratio como solucion

        for (int i = 1; i < y; i++) {
            if (matriz[i][mayor] == 1 && matriz[i][0] == 0) {
                ++matriz[i][0]; //DEBERA SER SIEMPRE 1, EL ++ ES PARA IR VIENDO SI ME REPITO
                for (int j = 1; j < x; j++) {
                    if (matriz[i][j] == 1) {
                        --cubre[j];
                    }
                }
            }
        }

    }

    public static void rellenarRatio() {
        ratio = new float[x];
        cubre[0] = 0;
        for (int i = 1; i < x; i++) {
            ratio[i] = cubre[i] / matriz[0][i];
        }
    }

    public static void leerFichero(String fich) throws FicheroNoEncontrado {
        if (!(new File(fich)).exists()) {
            throw new FicheroNoEncontrado("Fichero no encontrado \n");
        }
        File archivo;
        FileReader fr = null;
        BufferedReader br;
        try {
            archivo = new File(fich);
            fr = new FileReader(archivo);
            br = new BufferedReader(fr);
            String texto;
            String[] datos;
            System.out.print("Fichero abierto correctamente\n");
            texto = br.readLine();
            datos = texto.split(" ");
            y = Integer.parseInt(datos[1]) + 1;
            x = Integer.parseInt(datos[2]) + 1;

            matriz = new int[y][x];
            cubre = new int[x];

            for (int i = 0; i < x; i++) {
                cubre[i] = 0;
            }

            for (int i = 1; i < y; i++) {
                for (int j = 0; j < x; j++) {
                    matriz[i][j] = 0;
                }
            }
            matriz[0][0] = 0; //<---------PONER A -1

            int comisariasV = 1;
            while (x != comisariasV) {
                texto = br.readLine();
                datos = texto.split(" ");
                for (int i = 1; i < datos.length; i++) {
                    matriz[0][comisariasV] = Integer.parseInt(datos[i]);
                    ++comisariasV;
                }
            }
            int cont;
            for (int i = 1; i < y; i++) {
                texto = br.readLine();
                datos = texto.split(" ");
                cont = Integer.parseInt(datos[1]);
                while (cont != 0) {
                    texto = br.readLine();
                    datos = texto.split(" ");
                    for (int j = 1; j < datos.length; j++) {
                        matriz[i][Integer.parseInt(datos[j])] = 1;
                        ++cubre[Integer.parseInt(datos[j])];
                        --cont;
                    }
                }
            }

            solucion = new int[x];
            for (int i = 0; i < x; i++) {
                solucion[i] = 0;
            }
        } catch (IOException | NumberFormatException e) {
        } finally {
            try {
                if (null != fr) {
                    fr.close();
                    System.out.print("Fichero cerrado correctamente\n");
                }
            } catch (Exception e2) {
            }
        }
    }

    public static void main(String[] args) {
        String errores = "";
        try {

            leerFichero("scpe1.txt");

            //<--------------
            x = 11;
            y = 21;
            int cont;
            for (int j = 1; j < x; j++) {
                cont = 0;
                for (int i = 1; i < y; i++) {
                    if (matriz[i][j] == 1) {
                        ++cont;
                    }
                }
                cubre[j] = cont;
            }
            //------------->

            cubreOrdenado = new Pair[x-1];
            for (int i = 0; i < x-1; i++) {
                cubreOrdenado[i] = new Pair(i+1, cubre[i+1]);
            }

            rellenarRatio();
            System.out.println("Estado inicial: ");
            mostrarMatrizYVector();
            mostrarSolucion();

            while (faltanPorCubir()) {
                System.out.println("\n\n\n");
                buscarMayorRatio();
                rellenarRatio();
                mostrarMatrizYVector();
                mostrarSolucion();
                for (int i = 1; i < x; i++) {
                    if (cubre[i] < 0) {
                        System.out.println("HAY ALGUN PUTO FALLO REVISAR EL " + i);
                        return;
                    }
                }
            }
            eliminaRedundancias();

        } catch (FicheroNoEncontrado error) {
            errores = error.getMessage();
        }
        System.out.println(errores);
    }

    public static class Pair {

        private int lugar;
        private int cubre;

        public Pair(int lugarr, int cubree) {
            super();
            this.lugar = lugarr;
            this.cubre = cubree;
        }

        public int getFirst() {
            return lugar;
        }

        public void setFirst(int first) {
            this.lugar = first;
        }

        public int getSecond() {
            return cubre;
        }

        public void setSecond(int second) {
            this.cubre = second;
        }
    }

    public static class MyQuickSort {

        private Pair array[];
        private int length;

        public void sort(Pair[] inputArr) {
            if (inputArr == null || inputArr.length == 0) {
                return;
            }
            this.array = inputArr;
            length = inputArr.length;
            quickSort(0, length - 1);
        }

        private void quickSort(int lowerIndex, int higherIndex) {
            int i = lowerIndex;
            int j = higherIndex;
            // calculate pivot number, I am taking pivot as middle index number
            Pair pivot = array[lowerIndex + (higherIndex - lowerIndex) / 2];
            // Divide into two arrays
            while (i <= j) {
                /**
                 * In each iteration, we will identify a number from left side
                 * which is greater then the pivot value, and also we will
                 * identify a number from right side which is less then the
                 * pivot value. Once the search is done, then we exchange both
                 * numbers.
                 */
                while (array[i].cubre > pivot.cubre) {
                    ++i;
                }
                while (array[j].cubre < pivot.cubre) {
                    --j;
                }
                if (i <= j) {
                    exchangeNumbers(i, j);
                    //move index to next position on both sides
                    ++i;
                    --j;
                }
            }
            // call quickSort() method recursively
            if (lowerIndex < j) {
                quickSort(lowerIndex, j);
            }
            if (i < higherIndex) {
                quickSort(i, higherIndex);
            }
        }

        private void exchangeNumbers(int i, int j) {
            Pair temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }

    }

}
