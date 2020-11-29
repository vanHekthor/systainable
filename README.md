# Deployed at


[Frontend](https://green-configurator-frontend.herokuapp.com/)

[Backend](https://green-configurator-backend.herokuapp.com/)

# How to use

## Backend
in `backend` directory:

Running tests
```bash
gradle check
```

Running locally on Port 8080 (configurable in `backend/src/main/resources/application-dev.properties`):
```bash
gradle bootRun --args='--spring.profiles.active=dev'
```

For running tests:
```bash
gradle check
```

Building:
```bash
gradle build
```

## Frontend
in `frontend` directory:

Downloading dependencies:
```bash
npm install
```

Running lint tests:
```bash
npm run lint
```

Running locally on Port 8080:
```bash
npm run serve
```

Building:
```bash
npm run build
```
