# 🌲 PEPSE: Chill 8-bit Inspired Game With Nostalgic Scent

> **PEPSE** is a Java-based platformer featuring an infinite, procedurally generated world, a dynamic day/night cycle, and realistic physics.

![Gameplay Demo](https://place-hold.it/800x400?text=Insert+Gameplay+GIF+Here&fontsize=30)
*(Suggested: A GIF showing the avatar running as the world generates ahead)*

## 📖 About The Project

We built this engine from scratch as our final project for the Object-Oriented Programming course at Hebrew University.

The goal was simple but technically demanding: **Build a world that goes on forever without crashing the computer.**

This was also my **first major pair-programming experience**. We moved beyond just writing code to actually *engineering* a system together - debating architecture, implementing **Design Patterns** (like Strategy and Factory) to keep our code clean, and debugging complex logic as a team.

## ⚙️ Technical Highlights & Architecture

### 1. Infinite World & Memory ($O(1)$ Complexity)
We focused on algorithmic efficiency to create a world that feels alive but runs smoothly:
* **Infinite Procedural Generation:** The terrain and trees are not stored maps but are generated on-the-fly.
* **Dynamic Chunking:** The world is generated in vertical columns just outside the camera's view.
* **Garbage Collection:** As you run forward, the terrain behind you is destroyed. This keeps the memory usage constant ($O(1)$) whether you run for 5 minutes or 5 hours.

### 2. Deterministic Generation (Seeds)
We made sure that the world is persistent, even though it's being created on-the-fly:
* **The Logic:** Using pseudo-random algorithms with random seeds to calculate the terrain height and tree placement mathematically.
* **The Result:** If you leave an area and come back to it later, it regenerates *exactly* the same way.

### 3. Optimization (Layer Separation)
We realized that checking collisions for every single block would kill the frame rate (FPS).
* **Solution:** We separated the world into "Active" and "Static" layers. Deep terrain blocks that the player can't touch are moved to a static layer, completely removing them from the physics engine's calculations.

## 🎨 Creative Direction & Assets

While the core engine was a joint effort, I took ownership of the game's "Soul" - its atmosphere, visuals, and polish.

* **Atmospheric Immersion:** I designed a **Day/Night Cycle** that feels organic, implementing a working **Moon Phase** system, custom sunset color gradients, and parallax stars/clouds to create depth.
* **Custom Animations:** I manually edited sprite sheets to create specific physics-based animations, such as the Avatar **"Flapping Wings"** when flying and deploying a **Parachute** when falling.
* **Reactive Audio:** I integrated a soundscape where effects are tied to physics events - for example, the "landing" sound only triggers when the physics engine detects a collision with high velocity.

## 📸 Gallery

| **Day/Night Transition** | **Flight Mechanics** |
|:---:|:---:|
| ![Sunset](https://place-hold.it/300x200?text=Screenshot:+Sunset+Cycle) | ![Flight](https://place-hold.it/300x200?text=Screenshot:+Flight+Mechanic) |
| *Real-time sunset gradients* | *Energy-limited flight system* |

## 🚀 How to Play

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/cfiramar/PEPSE-Game.git](https://github.com/cfiramar/PEPSE-Game.git)
    ```
2.  **Run the Game:**
    * **Windows:** Double-click `PlayGame.bat`.
    * **Manual:** Run `java -cp "PEPSE.jar;libs/DanoGameLab.jar" pepse.PepseGameManager` in your terminal.

*(Requirement: Java 11 or higher installed)*

---

*Created by Cfir Amar \& Tomer Chechik.*
