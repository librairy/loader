curl -u oeg:oeg2018--header "Content-Type: application/json" \
  --request POST \
  --data '{ "contactEmail": "string", "contactName": "string", "contactUrl": "string",  "credentials": { "email": "string",  "password": "string", "repository": "string", "username": "string"  },  "description": "string",  "licenseName": "Apache License Version 2.0",  "licenseUrl": "https://www.apache.org/licenses/LICENSE-2.0",  "title": "string"}' \
  http://localhost:7777/topics/exports
