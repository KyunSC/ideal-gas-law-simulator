package edu.vanier.template;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

public class Particle {

    Circle circle;
    public double velocityX;
    public double velocityY;
    Timeline timeline;
    Pane canvas;

    public Particle(Circle circle, double velocityX, double velocityY, Pane canvas){
        this.circle = circle;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.canvas = canvas;
    }

    public Circle getCircle() {
        return circle;
    }

    public void play(){
        timeline.play();
    }

    public void createTimeline(){
        KeyFrame keyFrame = new KeyFrame(Duration.millis(10),(e -> moveCircle(circle)));
        Timeline timeline1 = new Timeline();
        timeline1.getKeyFrames().add(keyFrame);
        timeline1.setCycleCount(Animation.INDEFINITE);
        this.timeline = timeline1;
    }

    private void moveCircle(Circle particle) {
        if (particle.getCenterX() <= particle.getRadius() || particle.getCenterX() >= canvas.getWidth() - particle.getRadius() - 1) velocityX *= -1;
        if (particle.getCenterY() <= particle.getRadius() - 1 || particle.getCenterY() >= canvas.getHeight()-particle.getRadius()) velocityY *= -1;
        particle.setCenterX(particle.getCenterX() + velocityX);
        particle.setCenterY(particle.getCenterY() + velocityY);

    }
}
