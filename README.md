# Warrior RPG Game

A simple 2D top-down RPG game built with Java Swing. This project was created to practice game development fundamentals, including animations, player input, and state management.

<img width="801" height="571" alt="current state" src="https://github.com/user-attachments/assets/ea1c4e41-8d01-4c2b-b175-f9347e751285" />

## Features

* **Player Movement:** Full 8-directional movement.
* **Animations:** Smooth, sprite-based animations for idle, running, and attacking.
* **Combat:** Two distinct attack animations triggered by user input.
* **Tile-based World:** A simple, tileable grass background.
* **Enemy:** Automated spawning flying eneny that folow the player.
* **Projectile System:** Enemies fire fireballs at the player. Basic collision detection.
* **Debug info:** Display of the current enemy count, fireball count, and player coordinates.

## How to Compile and Run

You must have a Java Development Kit (JDK) installed.

1.  Clone the repository to your local machine:
    ```bash
    git clone https://github.com/suraj-2810/2D_TOPDOWN_RPG
    ```
2.  Navigate to the project directory:
    ```bash
    cd TopDownRPG
    ```
3.  Compile all the Java source files:
    ```bash
    javac *.java
    ```
4.  Run the game:
    ```bash
    java Gamewindow
    ```

## Controls

* **Move Up:** `W,UP`
* **Move Down:** `S,DOWN`
* **Move Left:** `A,LEFT`
* **Move Right:** `D,RIGHT`
* **Attack 1:** `J`
* **Attack 2:** `K`
