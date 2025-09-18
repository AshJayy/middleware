# Start with a lightweight official Python image
FROM python:3.10-slim

# Set the working directory inside the container
WORKDIR /app

# Copy and install dependencies first to leverage Docker's build cache
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy the rest of your application code
COPY . .

# Expose the port the app will run on
EXPOSE 8000

# The command to run your application
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]