package com.witek.model;

import org.apache.commons.math3.analysis.function.*;


class Function {
    private static ObjectProperties[] dataTable = ExcelReader.getObjectPropertiesExcel("Dane_lab5.xlsx");

    /**
     * Calculate the result of (x^4)-2(x^3).
     * Domain is (-infinity, infinity).
     * Minimum is -1.6875 at x = 1.5.
     * @param x     the x component
     * @return      the y component
     */
    private static double R = 8.314;

    // TRZEBA USUNĄĆ PRZEKAZYWANIE PARAMETRÓW T EPSILON I EPSILON_DOT, WSZYSTKIE TE DANE BĘDĄ POBIERANE Z EXCELA
    static double testowaFunkcja(){
        ObjectProperties[] dataModel = ExcelReader.getObjectPropertiesExcel("C:\\Users\\LENOVO\\Desktop\\Wimip\\identyfikacja_modeli\\dane\\Doswiadczenia.xlsx");
        double suma = 0;
        for (ObjectProperties data:
                dataModel) {
            for (int i = 0 ; i < data.sigma.length; i++){
                suma += data.sigma[i];

            }
        }
        return suma;
    }

    static double[] funkcja_lab5(double ro, double epsilon, double mT, double a[]){
        double t = 0.1;  //Czas rozwiązywania
        double delta_h = 0.0001;
        //double a[] = {2.1E-3,176.0,19.5E3,0.000148*3E10,151E3,0.973,5.77,1.0,0.0,0.262,0.0E13,0.000605E13,0.167};
        double p[] = new double[(int)(t/delta_h)+2];
        double e_dot = 10;
        double T = mT + 273;
        double Q = 312000;
        double t_cr = t; //Maksymalny czas na początku żeby znaleźć t_cr
        p[0] = 1E4;
        double ro_cr = -a[10]+a[11]*Math.pow(DifferentialEq.ZenerHollomon(e_dot,Q,T,R),a[9]);
        //Obliczenie t_cr
        int j = 0;
        for (double i = 0; i < t; i = i + delta_h) {
            if(p[j] >= ro_cr) {
                t_cr = i;
                break;
            }
            p[j + 1] = p[j] + delta_h * DifferentialEq.functionB(p[j], e_dot, i, p, j, delta_h,T,a,t_cr);
            j++;
        }

        j = 0;
        for (double i = 0; i < t; i = i + delta_h) {
            p[j + 1] = p[j] + delta_h * DifferentialEq.functionB(p[j], e_dot, i, p, j, delta_h,T,a,t_cr);
            System.out.println(p[j]);
            j++;
        }
        return p;
    }


    static double funkcja_lab2(double epsilon, double epsilon_dot, double T, double a[]){
        Asinh asinh = new Asinh();
        double Q_def = 312000;
        double Rr;
        double Z = epsilon_dot*Math.exp(Q_def/(R*(T+273))); //OK
        double sigma_0 = (1/(a[2]))*asinh.value(Math.pow((Z/a[0]),(1/a[1]))); //OK
        double sigma_sse = (1/a[5])*asinh.value(Math.pow((Z/a[3]),(1/a[4]))); //OK
        double sigma_ss = (1/a[10])*asinh.value(Math.pow((Z/a[8]),(1/a[9]))); //OK
        double epsilon_r = (a[6]+a[7]*Math.pow(sigma_sse,2))/3.23; //OK
        double epsilon_c = a[11]*Math.pow((Z/Math.pow(sigma_sse,2)),a[12]); //OK
        double delta_exs_ec = a[13]*Math.pow((Z/Math.pow(sigma_sse,2)),a[14]); //OK
        double delta_exr_ec = (delta_exs_ec/1.98); //OK
        if(epsilon > epsilon_c){
            Rr = (sigma_sse - sigma_ss)*(1-Math.exp(-Math.pow(((epsilon-epsilon_c)/(delta_exr_ec)),2))); //OK
        }else  Rr = 0;

        double sigmaP = sigma_0 + ((sigma_sse - sigma_0)*Math.pow((1-Math.exp(-((epsilon)/(epsilon_r)))),0.5)) - Rr; //OK

        return sigmaP;
    }

    static double funkcja(double epsilon,double epsilon_dot, double T,double a[]){

        double W = Math.exp(-a[6]*epsilon);
        double p1 = a[0]*Math.pow(epsilon,a[1]);
        double p2 = Math.exp((a[3])/(R*(T+273)));
        double p3 = (1-W)*a[4];
        double p4 = Math.exp((a[5])/(R*(T+273)));
        double p5 = Math.pow(epsilon_dot,a[2]);
        double sigmaP = (W*p1*p2+p3*p4)*p5;
        return sigmaP;
    }

    static double sigma0(double epsilon_dot, double T, double a[]){
        //DODAĆ 273 DO T
        Asinh asinh = new Asinh();
        double Q_def = 312000;
        double Z = epsilon_dot*Math.exp(Q_def/(R*(T+273)));
        double sigma_0 = (1/(a[2]))*asinh.value(Math.pow((Z/a[0]),(1/a[1])));

        return sigma_0;
    }

    static double functionB1(double a[]) {
        double totalError = 0;
        ObjectProperties[] dataModel = ExcelReader.getObjectPropertiesExcel("C:\\Users\\LENOVO\\Desktop\\Wimip\\identyfikacja_modeli\\dane\\Doswiadczenia-v03.xlsx");
        for (ObjectProperties data :
                dataModel) {

            //Pętla iterująca po pomiarach dla T i epsilon_dot
            for (int i = 0; i < 1; i++) {
                //obliczanie sumy błędów
                totalError += Math.pow((data.sigma[i] - sigma0(data.dot_epsilon, data.temperature, a)) / (data.sigma[i] + 0.0001), 2);
            }
        }
        return totalError;
    }

    static double functionB (double a[]){
        ObjectProperties[] dataModel = ExcelReader.getObjectPropertiesExcel("C:\\Users\\Wiciu\\Desktop\\INF_WIMIP\\sem_1\\identyfikacja_modeli\\Doswiadczenia-v03.xlsx");
        double totalError = 0;
        double localError = 0;
        double sigmaM = 0;
        double ro = 10;
        double gamma = 0;
        int n, m = 0;
        a[0] = 1.3498872562941594E13;
        a[1] = 4.344006308903154;
        a[2] = 0.018700272034451595;
        a[4] = a[1];
        n = dataModel.length;
        //Pętla iterująca po doświadczeniach
        for (ObjectProperties data:
                dataModel) {
            localError = 0;
        //Pętla iterująca po pomiarach dla T i epsilon_dot
            for (int i = 0 ; i < data.sigma.length; i++) {
                //obliczanie sumy błędów
                localError += Math.pow((data.sigma[i] - funkcja_lab2(data.epsilon[i], data.dot_epsilon, data.temperature, a)) / (data.sigma[i] + 0.0001), 2);
                /*localError += 0.01*Math.abs(data.sigma[40] - funkcja_lab2(data.epsilon[40], data.dot_epsilon, data.temperature, a));
                localError += 0.01*Math.abs(data.sigma[45] - funkcja_lab2(data.epsilon[50], data.dot_epsilon, data.temperature, a));
                localError += 0.01*Math.abs(data.sigma[30] - funkcja_lab2(data.epsilon[10], data.dot_epsilon, data.temperature, a));*/
            }



            //Człon ekstrapolacji sigma <1,3>
            sigmaM = funkcja_lab2(1,data.dot_epsilon,data.temperature,a); //wartość początkowa dla epsilon = 1
            for(double j = 1.1; j < 3 ; j += 0.1){
                double extrapolationValue = funkcja_lab2(j,data.dot_epsilon,data.temperature,a);
                if(sigmaM > extrapolationValue ){
            //Wartość minimalna naprężenia z ekstrapolacji
                       sigmaM = extrapolationValue;
                }
            }
            //Obliczanie kary
            if(sigmaM<sigma0(data.dot_epsilon,data.temperature,a)){
                gamma = ro*Math.abs(sigma0(data.dot_epsilon,data.temperature,a)-sigmaM);
            } else gamma = 0;
                m = data.sigma.length;
            totalError += (localError/(double) m) + gamma;
        }
        totalError = (totalError/(double) n);
        return totalError;
    }

    static double functionA (double a[]){
        ObjectProperties[] dataModel = ExcelReader.getObjectPropertiesExcel("C:\\Users\\Wiciu\\Desktop\\INF_WIMIP\\sem_1\\identyfikacja_modeli\\Doswiadczenia.xlsx");
        long totalError = 0;
        for (ObjectProperties data:
                dataModel) {
            for (int i = 0 ; i < data.sigma.length; i++){
                totalError += Math.pow((data.sigma[i] - funkcja(data.epsilon[i], data.dot_epsilon, data.temperature,a))/(data.sigma[i]+0.0001),2);
                //System.out.println(funkcja(data.epsilon[i], data.dot_epsilon, data.temperature,a));
            }
        }
        return totalError;
    }

    static double functionC(double[] a){
        ObjectProperties[] dataModel = dataTable;
        double totalError = 0;
        double Q = 312000;

        for (ObjectProperties data:
                dataModel) {
            //Obiekty danych jest przechowywany w obiekcie roju:
            double[] obliczone = DifferentialEq.Euler(data.epsilon[100000],data.epsilon[1],a,data.dot_epsilon,data.temperature + 273,Q,data.epsilon[100001]);
            for (int i = 0 ; i < obliczone.length; i++){
                totalError += Math.pow((data.sigma[i] - obliczone[i])/(data.sigma[i]+0.0001),2);
                //System.out.println(funkcja(data.epsilon[i], data.dot_epsilon, data.temperature,a));
            }
            totalError = totalError/(data.sigma.length);
        }
        totalError = totalError/(dataModel.length);
        return totalError;
    }
/*
    double functionC(double a[],double A[], double ro0, double ){
        double A1 = A[0];
        double A2 = A[1];
        double A3 = A[2];
        Tcr =
        for()
    }
*/

    /**
     * Perform Ackley's function.
     * Domain is [5, 5]
     * Minimum is 0 at x = 0 & y = 0.
     * @param x     the x component
     * @param y     the y component
     * @return      the z component
     */
    static double ackleysFunction (double x, double y) {
        double p1 = -20*Math.exp(-0.2*Math.sqrt(0.5*((x*x)+(y*y))));
        double p2 = Math.exp(0.5*(Math.cos(2*Math.PI*x)+Math.cos(2*Math.PI*y)));
        return p1 - p2 + Math.E + 20;
    }

    /**
     * Perform Booth's function.
     * Domain is [-10, 10]
     * Minimum is 0 at x = 1 & y = 3.
     * @param a     the x component
     * @param a    the y component
     * @return      the z component
     */
    static double boothsFunction (double a[]) {
        double p1 = Math.pow(a[0] + 2*a[1] - 7, 2);
        double p2 = Math.pow(2*a[0] + a[1] - 5, 2);
        return p1 + p2;
    }

    /**
     * Perform the Three-Hump Camel function.
     * @param x     the x component
     * @param y     the y component
     * @return      the z component
     */
    static double threeHumpCamelFunction (double x, double y) {
        double p1 = 2*x*x;
        double p2 = 1.05*Math.pow(x, 4);
        double p3 = Math.pow(x, 6) / 6;
        return p1 - p2 + p3 + x*y + y*y;
    }

    static double rosenBrockFunction (double x, double y){
        double p1 = 100*(y- Math.pow(x,2));
        double p2 = Math.pow((1-x),2);
        return p1 + p2;
    }

}