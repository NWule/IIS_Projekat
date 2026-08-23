#!/bin/bash
set -e

echo "Generating OpenAPI schema from Python microservice..."
cd fk_ai_service
GEMINI_API_KEY=dummy python -c "import json; from main import app; schema = app.openapi(); schema['openapi'] = '3.0.3'; open('../fudbalski_klub/src/main/resources/openapi.json', 'w', encoding='utf-8').write(json.dumps(schema))"

echo "Compiling OpenAPI client in fudbalski_klub..."
cd ../fudbalski_klub
./mvnw clean compile

echo "Done! Microservice client is generated and ready to use."