package edu.vanier.template;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.BoundingBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;

public class Particle {

    Circle circle;
    public double velocityX;
    public double velocityY;
    public double velocity;
    public double particleAngle;
    Timeline timeline;
    Pane canvas;
    Line lid;
    BoundingBox boundingBox = new BoundingBox(10, 10, 480,480);

    /**
     *
     * @param circle it's the shape of the particle
     *
     * @param canvas canvas
     */
    public Particle(Circle circle, double velocity, Pane canvas, Line lid){
        this.circle = circle;
        this.velocity = velocity;
        this.velocityX = Math.random()*velocity;
        this.velocityY = Math.sqrt(Math.pow(velocity, 2) - Math.pow(velocityX, 2));
        this.particleAngle = Math.acos(velocityX / this.velocity);
        this.canvas = canvas;
        this.lid = lid;
    }

    public double getVelocityX() {
        return velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public void setVelocity(double velocity) {
        if (velocity == 0) {
            this.velocity = 0;
            this.velocityX = 0;
            this.velocityY = 0;
        } else {
            if (this.velocity != 0) {
                this.particleAngle = Math.atan2(velocityY, velocityX);
            }
            this.velocity = velocity;

            velocityX = velocity * Math.cos(particleAngle); // New x velocity
            velocityY = velocity * Math.sin(particleAngle); // New y velocity
        }
    }

    public double getVelocity() {
        return velocity;
    }

    /**
     *
     * @return circle from the particle
     */
    public Circle getCircle() {return circle;}

    public void setCircle(Circle circle) {
        this.circle = circle;
    }

    /**
     * Function for playing timeline associated with the specific particle
     */
    public void play(){timeline.play();}

    /**
     * Function for pausing the timeline associated with the specific particle
     */
    public void pause(){timeline.pause();}

    /**
     * Timeline for the movement of the circle
     * Timeline with a duration of 10 millis that repeats the function moveCircle()
     * Timeline plays and infinite amount of time
     */
    public void createTimeline(){
        KeyFrame keyFrame = new KeyFrame(Duration.millis(10),(e -> moveCircle(circle)));
        Timeline timeline1 = new Timeline();
        timeline1.getKeyFrames().add(keyFrame);
        timeline1.setCycleCount(Animation.INDEFINITE);
        this.timeline = timeline1;
    }

    /**
     *
     * @param particle
     * This class handles the circle moving as well as wall collisions
     * If it passes into a certain area, the velocity will become negative
     * The circle is also moved by the amount declared in velocityX and velocityY
     *
     */
    private void moveCircle(Circle particle) {
        if (particle.getBoundsInParent().intersects(boundingBox)){
            if (canvas.getChildren().contains(lid)){
                if (particle.getCenterX() <= particle.getRadius() + 2) {
                    velocityX *= -1;
                    particle.setCenterX(particle.getRadius() + 5);
                }
                if (particle.getCenterX() >= canvas.getWidth() - particle.getRadius() - 2) {
                    velocityX *= -1;
                    particle.setCenterX(canvas.getWidth() - particle.getRadius() - 5);
                }
                if (particle.getCenterY() <= particle.getRadius() - 2) {
                    velocityY *= -1;
                    particle.setCenterY(particle.getRadius() + 5);
                }
                if (particle.getCenterY() >= canvas.getHeight()-particle.getRadius() - 2) {
                    velocityY *= -1;
                    particle.setCenterY(canvas.getHeight() - particle.getRadius() - 5);
                }
            }
            else {
                if (particle.getCenterX() <= particle.getRadius() + 2) {
                    velocityX *= -1;
                    particle.setCenterX(particle.getRadius() + 5);
                }
                if (particle.getCenterX() >= canvas.getWidth() - particle.getRadius() - 2) {
                    velocityX *= -1;
                    particle.setCenterX(canvas.getWidth() - particle.getRadius() - 5);
                }
                /*if (particle.getCenterY() <= particle.getRadius() - 2) {
                    velocityY *= -1;
                    particle.setCenterY(particle.getRadius() + 5);
                }*/
                if (particle.getCenterY() >= canvas.getHeight()-particle.getRadius() - 2) {
                    velocityY *= -1;
                    particle.setCenterY(canvas.getHeight() - particle.getRadius() - 5);
                }
            }

        }
        particle.setCenterX(particle.getCenterX() + velocityX);
        particle.setCenterY(particle.getCenterY() + velocityY);
    }
}
