# Minesweeper Determinator

(Note that all content in this repository is released under the MIT license. Please carefully read the [LICENSE](LICENSE) file and properly credit the content before reusing, copying, or adapting it.)

**Minesweeper Determinator** is a Java application that analyzes the screen to detect the current state of a running Minesweeper game. It determines whether the next move requires guessing or if a logical step can be taken. When guessing is not necessary, it suggests the next safe move and can probably help you click if you want.

Currently, it only focuses on the fitness of the website [https://minesweeper-pro.com/](https://minesweeper-pro.com/). The website is fully functionally cloned in the repository [MinesweeperPro](https://github.com/zhmgczh/MinesweeperPro) and is deployed at [https://zhmgczh.github.io/MinesweeperPro/](https://zhmgczh.github.io/MinesweeperPro/).

## Features

- 📷 **Screen Capture and Analysis**: Automatically captures and recognizes the current Minesweeper board state from the screen.
- 💣 **Mine Counter Detection**: Identifies the number of remaining mines.
- 🧠 **Guess Detection Logic**: Determines whether a guess is required to proceed.
- ✅ **Safe Move Suggestion & Autoplay**: Provides a recommended move and clicks for you when logical deduction is possible.
- ⚙️ **Cross-Platform (Java)**: Runs on any system with Java installed.

## Requirements

- OpenJDK 22 or above
- A browser with the website [https://minesweeper-pro.com/](https://minesweeper-pro.com/) open
- Desktop environment (not headless)

## How to Use

1. Clone this repository:
   ```bash
   git clone https://github.com/zhmgczh/MinesweeperDeterminator.git
   cd MinesweeperDeterminator
   ```

2. Run the project through the main class in `Main.java`, or simply run it via JAR:
   ```bash
   java -jar minesweeper-determinator.jar
   ```

3. Make sure the Minesweeper window is visible on your screen when running the program.

4. Follow on-screen instructions to let the program analyze the game state and suggest your next move. The best zoom-in percentage is 200%. The board needs to be completely shown on screen.

## Future Improvements

- Add support for different Minesweeper themes and resolutions.
- Improve OCR accuracy for board state and mine counter detection.

---

*This project is not affiliated with or endorsed by any official Minesweeper developer.*