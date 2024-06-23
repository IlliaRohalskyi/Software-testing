# Software Testing

## Overview
We tested the OpenWeatherAPI software, which laid the foundation for our project. Additionally, we have implemented a continuous integration (CI) pipeline using GitHub Actions to run our tests automatically.
This project was developed with the support of AI tools (GitHub Copilot, ChatGPT, Gemini) to enhance our knowledge and improve the development process. Please note, we used these tools for debugging and gaining insights to improve our implementation, not to copy the code directly.


## Continuous Integration
We have set up a small CI pipeline in GitHub Actions to run our tests whenever changes are pushed to the `main` branch.

## Running Tests Manually
To run the tests manually, please configure your system in the same way as our CI pipeline. Ensure you have JDK 22 installed and set up, and use Maven to install dependencies and run tests.

1. **Install dependencies:**
   ```sh
   mvn install -DskipTests

2. **Run Tests:**
   ```sh
    mvn test
