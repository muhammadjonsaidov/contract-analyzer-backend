# Contract Analyzer

A Spring Boot application for analyzing contracts using AI-powered document processing and vector search. Built with Spring AI, OpenAI GPT-4, and PostgreSQL vector store.

## Features

- **Document Ingestion**: Upload and process PDF contracts
- **AI-Powered Analysis**: Use OpenAI GPT-4 for contract analysis and Q&A
- **Vector Search**: Semantic search through contract content using PGVector
- **User Authentication**: JWT-based auth with Google OAuth2 login
- **REST API**: Full RESTful API for contract management and chat

## Prerequisites

- Java 21
- Maven 3.9+
- PostgreSQL 15+ (with PGVector extension)
- Docker & Docker Compose (for easy setup)

## Quick Start

### 1. Clone the Repository
```bash
git clone <repository-url>
cd contract-analyzer
```

### 2. Environment Setup
Create a `.env` file in the project root with the following variables:

```env
# Database
POSTGRES_DB=contractdb
POSTGRES_USER=postgres
POSTGRES_PASSWORD=rootpassword

# JWT
JWT_SECRET=your-256-bit-secret-key-here

# OpenAI
OPENAI_API_KEY=your-openai-api-key

# Google OAuth2
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
```

### 3. Start PostgreSQL with PGVector
```bash
docker run --name postgres-vector -e POSTGRES_DB=contractdb -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=rootpassword -p 5432:5432 -d ankane/pgvector
```

Or use Docker Compose:
```bash
docker-compose up -d
```

### 4. Run the Application
```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`.

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login with credentials
- `GET /oauth2/authorization/google` - Google OAuth2 login
- `GET /api/auth/callback` - OAuth2 callback (returns JWT token)

### Contracts
- `POST /api/contracts/upload` - Upload PDF contract
- `GET /api/contracts` - List user's contracts
- `GET /api/contracts/{id}` - Get contract details

### Chat/Analysis
- `POST /api/chat` - Ask questions about contracts
- `GET /api/chat/sessions` - List chat sessions

## Configuration

### Application Properties
Key configurations in `src/main/resources/application.yaml`:

- **Database**: PostgreSQL connection
- **AI**: OpenAI API settings and embedding dimensions
- **Security**: JWT expiration and OAuth2 client settings
- **Vector Store**: PGVector configuration

### Google OAuth2 Setup
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing
3. Enable Google+ API
4. Create OAuth 2.0 credentials
5. Add `http://localhost:8080/login/oauth2/code/google` as authorized redirect URI
6. Copy Client ID and Client Secret to `.env`

## Development

### Building
```bash
./mvnw clean package
```

### Running Tests
```bash
./mvnw test
```

### Database Schema
The application uses JPA with `ddl-auto: update` for automatic schema creation. PGVector tables are initialized automatically.

## Architecture

- **Backend**: Spring Boot 3.5 with Spring Security
- **AI**: Spring AI with OpenAI GPT-4 and text-embedding-3-small
- **Database**: PostgreSQL with PGVector for semantic search
- **Authentication**: JWT tokens with Google OAuth2
- **Frontend**: REST API ready for SPA integration

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

This project is licensed under the MIT License.
