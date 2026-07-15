# face-recognition-service

Microservicio Python (FastAPI + OpenCV) que compara dos fotos de rostro (1:1, verificacion, no
identificacion) y devuelve una similitud entre 0 y 1. Lo usa el backend Java al fichar la
"Entrada" en el modulo Asistencia, para decidir si el fichaje queda verificado automaticamente o
pendiente de revision humana (esa decision de umbral vive en el backend Java, no aca).

## Instalar y correr (Windows, PowerShell)

```powershell
cd face-recognition-service
python -m venv venv
.\venv\Scripts\Activate.ps1
pip install -r requirements.txt
uvicorn main:app --port 8000
```

## Instalar y correr (bash)

```bash
cd face-recognition-service
python -m venv venv
source venv/Scripts/activate   # Windows con Git Bash
pip install -r requirements.txt
uvicorn main:app --port 8000
```

Queda escuchando en `http://localhost:8000`. El backend Java lo llama vía
`app.reconocimiento-facial.url` (`application.properties`).

## Probar

```bash
curl -X POST http://localhost:8000/comparar-rostros \
  -F "referencia=@foto1.jpg" \
  -F "captura=@foto2.jpg"
```

Responde siempre `200`, con `rostroDetectadoReferencia`/`rostroDetectadoCaptura` en `false` si no
encontró una cara en alguna de las dos imágenes (en vez de fallar), y `similitud`/`distancia` solo
cuando pudo comparar.
