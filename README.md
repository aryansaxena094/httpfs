# HTTP File Server

This is a simple HTTP file server that allows for basic file operations over the HTTP protocol. It has the capability to list, read, and write files to a specified directory on the server.

## Table of Contents
1. [Components](#components)
2. [Usage](#usage)
3. [Configuration](#configuration)
4. [Error Handling](#error-handling)
5. [Logging](#logging)

## Components
- **FileManager**: Responsible for managing file operations on the server-side. This includes listing files, reading file content, and writing content to a file.
- **httpfs**: The main class that initializes the server. It sets up logging, parses command-line arguments, and starts the server.
- **HttpServer**: The core server logic is here. It listens for incoming client connections and spawns a new thread (`RequestHandler`) for each connection to handle the request.
- **RequestHandler**: This class is responsible for handling an individual client request. It parses the incoming HTTP request, determines the request type (GET, POST, etc.), uses the `FileManager` to carry out necessary file operations, and sends the appropriate HTTP response back to the client.
- **ServerConfig**: Used to parse and store server configurations based on the provided command-line arguments.

## Usage
To start the server, run the `httpfs` class. This will initialize and start the server with the provided configurations.

## Configuration
The server can be configured using the following command-line arguments:
- `-v`: Enable verbose mode. The server will output detailed debug logs.
- `-p <port>`: Specify the port number for the server to listen on. If not provided, the default port is `8080`.
- `-d <directory_path>`: Specify the directory path for the server to use for file operations. If not provided, the default directory is the current directory (`.`).

Example:
```
-v -p 8080 -d thedirectorypath
```

## Error Handling
Errors are gracefully handled in the server:
- Argument parsing errors will result in a clear error message and the server will not start.
- If a client request encounters an error, it will be logged and the client will receive an appropriate error response. The server continues running and will still accept new client connections.

In the case of file operations, the server checks if the file exists or if the requested operation would cause the file to be accessed outside of the base directory. In such cases, an error is thrown to ensure the security and integrity of the file system.

## Logging
Logging is incorporated throughout the server. By default, logs of level `WARNING` and above will be displayed. If the verbose (`-v`) flag is provided when starting the server, then all logs including debug logs (`FINE` level) will be displayed.

Loggers are set up in the main classes (`httpfs`, `HttpServer`, and `RequestHandler`) to capture essential events and potential errors in the application.
