# 🌲 PEPSE: Chill 8-bit Inspired Game With Nostalgic Scent

> **PEPSE** is a Java-based platformer featuring an infinite, procedurally generated world, a dynamic day/night cycle, and realistic physics.

https://github.com/user-attachments/assets/e81ccc02-c591-4ff9-9e68-9e3ed3de0492

## 📖 About The Project

We developed this game as our final project for the Object-Oriented Programming course at Hebrew University.

This was also my **first major pair-programming experience**. We moved beyond just writing code to actually *engineering* a system together - debating architecture, implementing **Design Patterns** (like Strategy and Factory) to keep our code clean, and debugging complex logic as a team.

## ⚙️ Technical Highlights & Architecture

### 1. Infinite World & Memory ($O(1)$ Complexity)
We focused on algorithmic efficiency to create a world that feels alive but runs smoothly:
* **Infinite Procedural Generation:** The terrain and trees are not stored maps but are generated on-the-fly.
* **Dynamic Chunking:** The world is generated in vertical columns just outside the camera's view.
* **Garbage Collection:** As you run forward, the terrain behind you is destroyed. This keeps the memory usage constant whether you run for 5 minutes or 5 hours.

### 2. Deterministic Generation (Seeds)
We made sure that the world is persistent, even though it's being created on-the-fly:
* **The Logic:** Using pseudo-random algorithms with random seeds to calculate the terrain height and tree placement mathematically.
* **The Result:** If you leave an area and come back to it later, it regenerates *exactly* the same way.

### 3. Optimization (Layer Separation)
We realized that checking collisions for every single block would kill the frame rate (FPS).
* **Solution:** We separated the world into "Active" and "Static" layers. Deep terrain blocks that the player can't touch are moved to a static layer, completely removing them from the physics engine's calculations.

## 🎨 Creative Direction & Assets

Beyond our shared work on the core game logic, I was responsible for the game's aesthetics, including the environmental effects and animations.

* **Atmospheric Immersion**: Designed a dynamic Day/Night Cycle featuring working moon phases, sunset gradients, and parallax backgrounds for depth.
* **Custom Animations:** Edited sprite sheet to create physics-based animations, such as wing-flapping mechanics and a "scared" expression during freefall.
* **Reactive Audio**: Integrated a soundscape where audio effects sync directly with specific physics events and player movement.

## 🎮 How to Play (Controls)

* **Move:** Left / Right Arrows
* **Jump:** Space
* **Fly:** Shift + Space

## 🛠️ Installation

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/cfiramar/PEPSE-Game.git
    ```
2.  **Run the Game:**
    * **Windows:** Double-click `RunToPlay.bat`.
    * **Manual:** Run `java -cp "PEPSE-Game.jar;libs/DanoGameLab.jar" pepse.PepseGameManager` in your terminal.

*(Requirement: Java 11 or higher installed)*

## 📸 Gallery

| **Peaceful Sunrise** | **Beautiful Full Moon** | **HELP IM FALLING** |
|:---:|:---:|:---:|
| ![Sunrise](https://raw.githubusercontent.com/cfiramar/PEPSE-Game/main/assets/README/sunrise.png) | ![Full Moon](https://raw.githubusercontent.com/cfiramar/PEPSE-Game/main/assets/README/full_moon.png) | ![Falling](https://raw.githubusercontent.com/cfiramar/PEPSE-Game/main/assets/README/falling.png) |

---

*Created by Cfir Amar \& Tomer Chechik*
