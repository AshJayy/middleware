# wms-adapter/Dockerfile
FROM python:3.11-slim

# Set workdir
WORKDIR /app

# Install dependencies
COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

# Copy code
COPY . ./

# Expose the FastAPI port
EXPOSE 8003

# Set environment variables for production
ENV PYTHONUNBUFFERED=1

# Run app
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8003"]
