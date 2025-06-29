# PDF to DOCX Conversion Microservice

A Quarkus-based microservice that converts PDF files to DOCX format using the Aspose.PDF library.

## Features

- REST API for converting PDF to DOCX
- GraalVM native image support
- Fault tolerance with circuit breakers and timeouts
- Kubernetes-ready with health checks
- AWS deployment support

## Prerequisites

- JDK 17+
- Maven 3.8+
- GraalVM 22.3+ (for native builds)
- Docker (for containerization)
- Aspose.PDF license (commercial)

## Building the Application

### JVM Mode

```bash
mvn clean package
```

### Native Mode

```bash
mvn clean package -Pnative
```

### Container Image

```bash
# Build native image and Docker container
mvn clean package -Pnative -Dquarkus.native.container-build=true -Dquarkus.container-image.build=true
```

## Running the Application

### Development Mode

```bash
mvn quarkus:dev
```

### Standalone

```bash
# JVM mode
java -jar target/quarkus-app/quarkus-run.jar

# Native mode
./target/pdf-convert-1.0.0-SNAPSHOT-runner
```

### Docker

```bash
docker build -t pdf-convert:latest .
docker run -p 8080:8080 pdf-convert:latest
```

## Aspose License Configuration

1. Place your Aspose.PDF license file in `src/main/resources/license.xml`
2. OR set the environment variable `PDF_LICENSE_PATH` to point to your license file:
   ```bash
   export PDF_LICENSE_PATH=/path/to/license.xml
   ```

For production deployment, use Kubernetes secrets to store the license.

## API Usage

### Converting PDF to DOCX

**Endpoint:** `POST /api/convert/pdf-to-docx`

**Content-Type:** `multipart/form-data`

**Parameters:**
- `file`: PDF file to convert (required)
- `options`: JSON string with conversion options (optional)

**Example options:**
```json
{
  "mode": "flow",
  "recognizeBullets": true,
  "relativeHorizontalProximity": 2.5
}
```

**cURL Example:**

```bash
curl -X POST "http://localhost:8080/api/convert/pdf-to-docx" \
  -H "Content-Type: multipart/form-data" \
  -F "file=@example.pdf" \
  -F "options={\"mode\":\"flow\",\"recognizeBullets\":true}" \
  --output converted.docx
```

## AWS Deployment

### Pushing to Amazon ECR

```bash
# Login to ECR
aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com

# Create repository if it doesn't exist
aws ecr create-repository --repository-name pdf-convert --region $AWS_REGION

# Tag and push the image
docker tag pdf-convert:latest $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/pdf-convert:latest
docker push $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/pdf-convert:latest
```

### Deploying to EKS

```bash
# Create Kubernetes secrets for Aspose license
kubectl create secret generic pdf-convert-secrets \
  --from-file=license=path/to/license.xml \
  --from-literal=license-path=/work/license/license.xml

# Deploy the application
kubectl apply -f kubernetes/deployment.yaml
```

## License

This project uses the following commercial components:
- Aspose.PDF for Java: Requires a valid license from Aspose (https://www.aspose.com/) 