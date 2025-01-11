# SpringBox

SpringBox is a Spring-based application designed for secure and efficient file synchronization and backup. This project aims to provide a user-friendly interface for managing personal cloud storage solutions, leveraging the power of Spring Boot and modern web technologies.

![SpringBox Preview](springbox-preview-01.png)

## Features

- User registration and authentication
- Secure file upload and download
- File synchronization across devices
- User-friendly web interface
- Responsive design with Bootstrap
- Real-time file status updates
- Multi-device support

## Technologies Used

- **Backend**: Spring Boot
- **Database**: MySQL
- **Security**: Spring Security
- **Frontend**: Bootstrap 5
- **Containerization**: Docker

## Getting Started

### Prerequisites

- Java 22 or higher
- Docker and Docker Compose
- Maven 3.8+

### Installation

1. Clone the repository

    ```sh
    git clone https://github.com/axelfrache/SpringBox.git
    cd SpringBox
    ```

2. Build and run the application using Docker

    ```sh
    docker-compose up --build -d
    ```

3. Access the application

   Open your browser and navigate to `http://localhost:8080`

### Configuration

The application uses a MySQL database as specified in the `docker-compose.yml` file. You can customize the database configuration in the `docker-compose.yml` and `application.properties` files as needed.

### Usage

1. **Register** a new user at `http://localhost:8080/springbox/register`
2. **Login** with your credentials at `http://localhost:8080/springbox/login`
3. **Upload and manage your files** at `http://localhost:8080/springbox/files`

## Contributing

Contributions are welcome! Here's how you can contribute:

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request