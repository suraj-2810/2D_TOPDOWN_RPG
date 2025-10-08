# Warrior RPG Game

A 2D top-down action RPG built with Java Swing. Control a warrior navigating through waves of flying enemies while managing health and dodging projectile attacks.

<img width="801" height="571" alt="current state" src="https://github.com/user-attachments/assets/074b71af-9862-44d2-a420-6a45d2bbbc89" />

## Features

### Player Mechanics
* **8-Directional Movement** - Smooth WASD/Arrow key controls with diagonal normalization
* **Animated Sprite System** - State-based animations for idle, running, and two distinct attack moves
* **Health System** - 100 HP with visual health bar and damage feedback

### Enemy AI
* **Smart Flying Enemies** - Autonomous behavior with three states: Flying, Idle, and Attack
* **Dynamic Pursuit** - Enemies approach within attack range while maintaining minimum distance
* **Automated Spawning** - New enemies spawn every 3 seconds (max 10 active)
* **Projectile Combat** - Enemies fire homing fireballs with 2.5s cooldown

### Combat System
* **Projectile Physics** - Fireball trajectory calculation with collision detection
* **Player Damage** - 10 HP per fireball hit with real-time health updates
* **Visual Feedback** - Attack animations and health bar updates

### Technical Features
* **60 FPS Game Loop** - Consistent frame timing with delta time calculations
* **Sprite Sheet Animation** - Efficient frame slicing and animation state management
* **Tile-Based Background** - Scalable grass tilemap rendering
* **Debug Overlay** - Real-time display of enemy count, projectile count, and player position

## Project Structure

```
TopDownRPG/
├── Animation.java       # Frame-based animation system with loop control
├── Enemy.java          # Enemy AI with state machine and shooting behavior
├── Fireball.java       # Projectile physics and collision
├── GamePanel.java      # Main game loop and rendering
├── Gamewindow.java     # JFrame initialization
├── Player.java         # Player controls and combat states
├── TileManager.java    # Background tile rendering
└── assets/
    ├── Warrior_Idle.png
    ├── Warrior_Run.png
    ├── Warrior_Attack1.png
    ├── Warrior_Attack2.png
    ├── FLYING.png
    ├── ATTACK.png
    ├── projectile.png
    └── Ground/
        └── Tilemap_Flat.png
```

## Requirements

* **Java Development Kit (JDK)** - Version 8 or higher
* **Terminal/Command Prompt** - For compilation and execution

## Installation & Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/suraj-2810/2D_TOPDOWN_RPG
   cd TopDownRPG
   ```

2. **Verify asset files exist in the `assets/` directory**

3. **Compile all Java files:**
   ```bash
   javac *.java
   ```

4. **Run the game:**
   ```bash
   java Gamewindow
   ```

## Controls

| Action | Keys |
|--------|------|
| Move Up | `W` or `↑` |
| Move Down | `S` or `↓` |
| Move Left | `A` or `←` |
| Move Right | `D` or `→` |
| Attack 1 | `J` |
| Attack 2 | `K` |

## Gameplay Tips

* **Maintain Distance** - Enemies have a minimum approach distance; use this to dodge fireballs
* **Watch the Cooldown** - Enemies fire every 2.5 seconds; time your movements
* **Stay Mobile** - Continuous movement makes you harder to hit
* **Monitor Health** - The green health bar depletes with each hit (10 HP per fireball)

## Technical Highlights

### Animation System
* Time-based frame advancement with configurable FPS
* Support for looping and non-looping animations
* Automatic state transitions on animation completion

### Enemy AI States
* **FLYING**: Chase player until within attack range
* **IDLE**: Maintain position and shoot at player
* **ATTACK**: Play attack animation and spawn fireball

### Collision Detection
* Rectangle-based bounding boxes for all entities
* Intersection testing for player-fireball collisions
* Screen boundary clamping for player movement

## Known Limitations

* Player is confined to single screen (no camera follow)
* Enemies spawn at screen edges only
* No enemy-player collision damage
* Maximum 10 active enemies, 50 active fireballs

## Future Enhancements

* Camera system with world scrolling
* Enemy health and combat damage
* Power-ups and item pickups
* Multiple enemy types
* Sound effects and music
* Game over and restart screens

## Credits

* **Developer**: Suraj
* **Sprites**: Community sprite sheets
* **Framework**: Java Swing

## License

This project is open source and available for educational purposes.

---

**Repository**: [github.com/suraj-2810/2D_TOPDOWN_RPG](https://github.com/suraj-2810/2D_TOPDOWN_RPG)