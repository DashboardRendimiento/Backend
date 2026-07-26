FROM python:3.11-slim
WORKDIR /app

# opencv-contrib-python necesita estas librerias de sistema aunque se use en
# modo headless (sin ellas falla al importar cv2: "libGL.so.1: cannot open
# shared object file"). No vienen en python:3.11-slim por defecto.
RUN apt-get update && apt-get install -y --no-install-recommends \
    libgl1 \
    libglib2.0-0 \
    libsm6 \
    libxext6 \
    libxrender1 \
    && rm -rf /var/lib/apt/lists/*

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY main.py .

RUN useradd --create-home appuser
USER appuser

# Cloud Run inyecta $PORT (default 8080 alli, pero este servicio usa 8000
# en local); el CMD respeta la variable si esta presente.
EXPOSE 8000
CMD ["sh", "-c", "uvicorn main:app --host 0.0.0.0 --port ${PORT:-8000}"]
