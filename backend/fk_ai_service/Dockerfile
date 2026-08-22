FROM python:3.11-slim

# Prevent Python from writing .pyc files and ensure stdout/stderr are unbuffered
ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1

WORKDIR /app

# Installs system utilities
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    && rm -rf /var/lib/apt/lists/*

COPY requirements.txt .

# Installs required python libraries
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

EXPOSE 8000

# Runs the app
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]