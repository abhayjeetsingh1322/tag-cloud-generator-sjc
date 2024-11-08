# Standard Library Tag Cloud Generator

## Description
This project builds upon the **Tag Cloud Generator** but exclusively uses components from the **standard Java libraries** to achieve the same functionality. It reads a text file, analyzes word frequency, and generates a tag cloud in HTML format with word sizes proportional to their frequency.

---

## Objectives
- Replace custom OSU CSE components with **Java Collections Framework** components (e.g., `Map`, `List`, and `Collections`).
- Use **Java standard I/O components** (`FileReader`, `BufferedReader`, `FileWriter`, `BufferedWriter`, `PrintWriter`) for file handling.
- Handle **checked exceptions** like `IOException` using `try-catch` blocks.
- Meet the same functional requirements as the previous project.

---

## Features
### 1. Input and Output
- **Input**:
  - Reads a text file specified by the user.
  - Accepts the number of words (N) for the tag cloud.
- **Output**:
  - Generates an HTML file displaying the N most frequent words.
  - Displays the input file name in the heading and includes styled words in alphabetical order.

### 2. Word Processing
- Words are defined by customizable delimiters (e.g., ` \t\n\r,-.!?[]';:/()`).
- Case-insensitive processing ensures accurate word counting.

### 3. Sorting
- Uses Java's `Comparator` interface for:
  - Sorting words by frequency (descending order).
  - Sorting words alphabetically.

### 4. Styling
- The HTML file includes CSS references to format the tag cloud.
- Font sizes range from `f11` to `f48`, scaled to word frequency.

---

## Technologies Used
- **Java Collections Framework**: For data storage and sorting.
- **Java Standard I/O**: For file reading and writing.
- **HTML/CSS**: For output formatting and styling.

---

## How to Run
### Prerequisites
- Java Development Kit (JDK) installed on your system.
- A text file to use as input.

### Steps
1. Clone the repository:
   ```bash
   git clone [repository URL]
