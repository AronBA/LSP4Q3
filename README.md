# Q3 Language Server (LSP)

The **Q3 Language Server** is a Java-based server that powers the LSP support for the Q3 Language in the **Q3 Language Support** extension for Visual Studio Code. The server is designed to provide robust, real-time language features that enhance the development experience for Q3 programmers.

## Key Features

- **Code Completion**: Offers intelligent suggestions based on context, making coding faster and reducing errors.
  
- **Go to Definition**: Enables quick navigation to function or variable definitions within Q3 projects.

- **Hover Information**: Displays useful information about symbols (e.g., types, documentation) when hovering over elements in the code.

- **Find References**: Locates all usages of a symbol across the project, simplifying code analysis and refactoring.

- **Document Formatting**: Automatically formats Q3 code based on a predefined or customizable style guide.

- **Diagnostics**: Provides real-time error checking and reporting to catch issues as you write code.

## Requirements

- The Q3 Language Server requires a **Java Runtime Environment (JRE)** to run. Ensure that Java is installed on your machine and available in the system’s PATH.

## Integration with VS Code

The Q3 LSP server is automatically launched when you open a `.q3` file in VS Code, providing seamless access to the advanced language features outlined above. All communication between the extension and the server is handled via the Language Server Protocol (LSP), ensuring compatibility and performance across different development environments.

## Development and Contribution

The Q3 Language Server is open source and actively developed. Developers interested in contributing can find the source code and contribute to its development at the official repository: [LSP4Q3](https://github.com/AronBA/LSP4Q3).
