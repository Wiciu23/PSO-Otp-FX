package com.witek.model;/*
package PSO;

/**
 * Can represent a position as well as a velocity.
 */

import java.util.Arrays;

/**
 * Can represent a position as well as a velocity.
 */
class Vector {

    private double a[];
    private double limit = Double.MAX_VALUE;

    Vector (int length) {
        this( new double[length]);
    }

    Vector (double _a[]) {
        a = new double[_a.length];
        for (int i = 0; i < _a.length; i++){
            a[i] = _a[i];
        }
    }

    public double[] getCordinates(){
        double cordinates[] = a;
        return cordinates;
    }
    public double getVectorComponent(int index) {
        return a[index];
    }

    void set (double[] _a) {
        System.arraycopy(_a, 0, this.a, 0, _a.length);
    }



    void add (Vector v) {
        for (int i = 0; i < v.a.length; i++){
            this.a[i] += v.a[i];
        }
        limit();
    }

    void sub (Vector v) {
        for (int i = 0; i < v.a.length; i++){
            this.a[i] -= v.a[i];
        }
        limit();
    }

    void mul (double s) {
        for (int i = 0; i < a.length; i++){
            this.a[i] *= s;
        }
        limit();
    }

    void div (Vector v) {
        for (int i = 0; i < v.a.length; i++){
            this.a[i] /= v.a[i];
        }
        limit();
    }

    void normalize () {
        double m = mag();
        if (m > 0) for (int i = 0; i < a.length; i++){
            this.a[i] /= m;
        }
    }

    private double mag () {
        return Math.sqrt(Arrays.stream(a).sum());
    }

    void limit (double l) {
        limit = l;
        limit();
    }

    private void limit () {
        double m = mag();
        if (m > limit) {
            double ratio = m / limit;
            for (int i = 0; i < a.length; i++){
                this.a[i] /= ratio;
            }
        }
    }

    public Vector clone () {
        return new Vector(a);
    }

    public String toString () {
        return Arrays.toString(a);
    }

}





/*
class Vector {

    private double x, y, z;
    private double limit = Double.MAX_VALUE;

    Vector () {
        this(0, 0, 0);
    }

    Vector (double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    double getX () {
        return x;
    }

    double getY () {
        return y;
    }

    double getZ () {
        return z;
    }

    void set (double x, double y, double z) {
        setX(x);
        setY(y);
        setZ(z);
    }

    private void setX (double x) {
        this.x = x;
    }

    private void setY (double y) {
        this.y = y;
    }

    private void setZ (double z) {
        this.z = z;
    }

    void add (Vector v) {
        x += v.x;
        y += v.y;
        z += v.z;
        limit();
    }

    void sub (Vector v) {
        x -= v.x;
        y -= v.y;
        z -= v.z;
        limit();
    }

    void mul (double s) {
        x *= s;
        y *= s;
        z *= s;
        limit();
    }

    void div (double s) {
        x /= s;
        y /= s;
        z /= s;
        limit();
    }

    void normalize () {
        double m = mag();
        if (m > 0) {
            x /= m;
            y /= m;
            z /= m;
        }
    }

    private double mag () {
        return Math.sqrt(x*x + y*y);
    }

    void limit (double l) {
        limit = l;
        limit();
    }

    private void limit () {
        double m = mag();
        if (m > limit) {
            double ratio = m / limit;
            x /= ratio;
            y /= ratio;
        }
    }

    public Vector clone () {
        return new Vector(x, y, z);
    }

    public String toString () {
        return "(" + x + ", " + y + ", " + z + ")";
    }

}
*/