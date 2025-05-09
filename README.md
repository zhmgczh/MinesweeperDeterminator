# Minesweeper Determinator

**Minesweeper Determinator** is a Java application that analyzes the screen to detect the current state of a running Minesweeper game. It determines whether the next move requires guessing or if a logical step can be taken. When guessing is not necessary, it suggests the next safe move and can probably help you click if you want.

Currently, it only focuses on the fitness of the website [https://minesweeper-pro.com/](https://minesweeper-pro.com/).

## Features

- 📷 **Screen Capture and Analysis**: Automatically captures and recognizes the current Minesweeper board state from the screen.
- 💣 **Mine Counter Detection**: Identifies the number of remaining mines.
- 🧠 **Guess Detection Logic**: Determines whether a guess is required to proceed.
- ✅ **Safe Move Suggestion**: Provides a recommended move when logical deduction is possible.
- ⚙️ **Cross-Platform (Java)**: Runs on any system with Java installed.

## Requirements

- Java 8 or above
- A standard Minesweeper game running on your screen (any version with consistent visual layout)
- Desktop environment (not headless)

## How to Use

1. Clone this repository:
   ```bash
   git clone https://github.com/yourusername/MinesweeperDeterminator.git
   cd MinesweeperDeterminator
   ```

2. Compile and run the project:
   ```bash
   javac -d bin src/*.java
   java -cp bin Main
   ```

3. Make sure the Minesweeper window is visible on your screen when running the program.

4. Follow on-screen instructions to let the program analyze the game state and suggest your next move.

## Example

Here's an example output from the program:

```
Detected 12 remaining mines.
Analyzing current board state...
✅ Safe move found at (5, 3). Proceed without guessing.
```

or

```
Detected 3 remaining mines.
Analyzing current board state...
⚠️ No deterministic move found. You must guess between (2, 4) and (2, 5).
```

## Future Improvements

- Add support for different Minesweeper themes and resolutions.
- Improve OCR accuracy for mine counter detection.
- Add GUI for visualization of analysis results.
- Support direct interaction with the game (auto-play).

## License

MIT License. See [LICENSE](LICENSE) for details.

---

*This project is not affiliated with or endorsed by any official Minesweeper developer.*
