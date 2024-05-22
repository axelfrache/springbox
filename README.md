# SpringBox

SpringBox is a Spring-based application designed for secure and efficient file synchronization and backup. This project aims to provide a user-friendly interface for managing personal cloud storage solutions, leveraging the power of Spring Boot and modern web technologies.

## Features

- User registration and authentication
- Secure file upload and download
- File synchronization across devices
- User-friendly web interface
- Responsive design with Bootstrap

## Getting Started

### Installation

1. Clone the repository

    ```sh
    git clone https://github.com/axelfrache/SpringBox.git
    cd SpringBox
    ```

2. Build the project

    ```sh
    mvn clean install
    ```

3. Run the application

    ```sh
    mvn spring-boot:run
    ```

4. Access the application

   Open your browser and navigate to `http://localhost:8080`

### Configuration

The application uses an H2 in-memory database by default. For production use, you can configure a different database in `src/main/resources/application.properties`.

### Usage

1. **Register** a new user at `http://localhost:8080/springbox/register`
2. **Login** with your credentials at `http://localhost:8080/springbox/login`
3. **Upload and manage your files** at `http://localhost:8080/springbox/files`
