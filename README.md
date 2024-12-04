
# advent-2024

## Overview

Welcome to the **advent-2024** project! This repository contains solutions for [Advent of Code 2024](https://adventofcode.com/2024), an annual programming challenge created by Eric Wastl. The challenge consists of small daily programming puzzles released every December, designed to improve problem-solving skills and programming expertise.

Each day's challenge is implemented in Clojure, and the repository is organized to help you understand the solutions and run them easily.

---

## Structure

The project is organized by day, with each day's code located in the `src/advent_2024` directory:

```
advent-2024/
├── README.md            # Project documentation
├── deps.edn             # Dependency configuration
└── src/
    └── advent_2024/
        ├── 01/
        │   └── core.clj
        ├── 02/
        │   └── core.clj
        ├── 03/
        │   └── core.clj
        └── ...          # Additional days
```

Each day's solution is self-contained in its respective `core.clj` file and processes the day's input (e.g., `01.txt`, `02.txt`, `03.txt`) from the corresponding directory.

---

## Running the Solutions

### Prerequisites
1. Install [Clojure CLI tools](https://clojure.org/guides/getting_started).
2. Clone this repository:
   ```bash
   git clone https://github.com/your-repo/advent-2024.git
   cd advent-2024
   ```

### Running Individual Days
Navigate to the desired day's folder and run its corresponding solution:

#### Example: Running Day 2
```bash
clj -M -m advent-2024.02.core
```

#### Example: Running Day 3
```bash
clj -M -m advent-2024.03.core
```

---

## Notes

- **Input Files**: Each day processes its input file (`01.txt`, `02.txt`, etc.), located in the corresponding subdirectory.
- **Output**: Solutions may generate intermediate or cleaned files (e.g., `cleaned_part1.txt`, `cleaned_part2.txt` for Day 3).
- **Dependencies**: Defined in `deps.edn`.

---

## About Advent of Code

[Advent of Code](https://adventofcode.com/2024) is an Advent calendar of programming puzzles for a variety of skill levels. It is widely used for:
- Practice problems
- University coursework
- Interview preparation
- Programming contests

The problems are designed to run efficiently, even on older hardware. Advent of Code is free to use and encourages community participation.

---

## Contributing

Feel free to fork this repository, submit pull requests, or suggest improvements. Contributions are welcome!

---

Happy coding! 🎄
