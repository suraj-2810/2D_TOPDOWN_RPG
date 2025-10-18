# 🎮 Warrior RPG Game

A 2D top-down action RPG built with Java Swing. Control a warrior battling waves of flying enemies — dodge projectiles, strike back, and survive as long as possible!

<img width="801" height="571" alt="game screenshot" src="https://github.com/user-attachments/assets/074b71af-9862-44d2-a420-6a45d2bbbc89" />

## 🧩 Features

### 🧍 Player
- **8-Directional Movement** – Smooth WASD/Arrow key control with diagonal normalization
- **Directional Animations** – Left/right sprites for idle, run, and two attack moves
- **Health System** – 100 HP with visible health bar and feedback
- **Combat System** – Melee attacks (J / K) with hitbox visualization and 100 damage per hit

### 👾 Enemy AI
- **Autonomous Flying Enemies** – AI-driven movement with three states: Flying, Idle, Attack
- **Dynamic Pursuit** – Enemies approach within 200 units but maintain a 150-unit minimum distance
- **Projectile Combat** – Homing fireballs fired every 2.5s
- **Spawning System** – New enemies spawn every 3s (max 10 active, at least 250 units from player)

### ⚔️ Combat & Game Flow
- **Projectile Physics** – Fireball trajectory and collision detection
- **Damage System** – Player loses 10 HP per fireball hit
- **Kill Counter** – Displays total enemy kills on-screen
- **Game Over Screen** – Smooth fade-out, restart button, and full state reset

### ⚙️ Technical Highlights
- **60 FPS Game Loop** – Time-based updates using delta calculations
- **Sprite Sheet Animation** – Frame slicing, loop control, and state transitions
- **Tile-Based Background** – Scalable grass tilemap
- **Debug Overlay** – Shows enemy/fireball count, player position, and HP

## 🗂️ Project Structure

```
TopDownRPG/
├── Animation.java     # Handles frame-based animation
├── Enemy.java         # Enemy AI, states, and projectiles
├── Fireball.java      # Projectile behavior and collisions
├── GamePanel.java     # Core loop, rendering, and logic
├── Gamewindow.java    # JFrame setup and main entry
├── Player.java        # Player control and combat
├── TileManager.java   # Background tile rendering
└── assets/
    ├── Warrior_Idle.png
    ├── Warrior_Run.png
    ├── Warrior_Attack1.png
    ├── Warrior_Attack2.png
    ├── Warrior_Idle_Left.png
    ├── Warrior_Run_Left.png
    ├── Warrior_Attack1_Left.png
    ├── Warrior_Attack2_Left.png
    ├── FLYING.png
    ├── ATTACK.png
    ├── projectile.png
    └── Ground/Tilemap_Flat.png
```

## 💻 Requirements

- **Java Development Kit (JDK)** – Version 8 or higher
- **Terminal / Command Prompt** – For compiling and running

## 🚀 Installation & Setup

```bash
# Clone the repository
git clone https://github.com/suraj-2810/2D_TOPDOWN_RPG
cd TopDownRPG

# Verify assets exist in /assets

# Compile
javac *.java

# Run
java Gamewindow
```

## 🎮 Controls

| Action | Keys |
|--------|------|
| Move | `W` `A` `S` `D` or Arrow Keys |
| Attack 1 | `J` |
| Attack 2 | `K` |
| Restart (Game Over) | Click Restart Button |

## 🧠 Gameplay Tips

- **Stay mobile** — dodging is key to survival
- **Attack before enemies enter attack range** (150–200 units)
- **Enemies fire every 2.5 seconds** — time your attacks wisely
- **Track your kill count** and aim for a higher score!

## 🔬 Technical Details

### Animation System
- Time-based frame updates and looping support
- Non-looping animations trigger state transitions automatically
- Directional sprite control for left/right facing

### Collision Detection
- Rectangle-based bounding boxes for entities
- Accurate player–fireball and hitbox–enemy collisions
- Screen boundary clamping for player movement

### Game Over Flow
1. Player HP reaches 0
2. Game freezes and fades to black
3. "GAME OVER" text and restart button appear
4. Restart resets full state

## 🧱 Known Limitations

- Single 800×600 screen (no scrolling camera)
- Enemies spawn only at screen edges
- No direct contact damage (only projectiles)
- Max 10 enemies and 50 fireballs
- No difficulty scaling or persistent scoring

## 🚧 Future Enhancements

- Camera with world scrolling
- Multiple enemy types & AI patterns
- Power-ups, drops, and health pickups
- Score system & high-score saving
- Sound effects and background music
- Particle and hit effects
- Boss fights and progression system

## 🪄 Debug Overlay

Displays:
- Active enemies
- Active fireballs
- Player position (X, Y)
- Player health

## 👤 Credits

- **Developer**: Suraj
- **Sprites**: Community sources
- **Framework**: Java Swing

## ⚖️ License

This project is open source and available for educational purposes.

---

**Repository**: [github.com/suraj-2810/2D_TOPDOWN_RPG](https://github.com/suraj-2810/2D_TOPDOWN_RPG)