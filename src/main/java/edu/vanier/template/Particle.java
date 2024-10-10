package edu.vanier.template;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

public class Particle {

    Circle circle;
    public double velocityX;
    public double velocityY;

    public Particle(Circle circle, double velocityX, double velocityY){
        this.circle = circle;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    public Circle getCircle() {
        return circle;
    }
}
