import cv2
import numpy as np
from fastapi import FastAPI, File, UploadFile

app = FastAPI(title="face-recognition-service")

_face_cascade = cv2.CascadeClassifier(
    cv2.data.haarcascades + "haarcascade_frontalface_default.xml"
)

TAMANIO_ROSTRO = (200, 200)
DISTANCIA_MAXIMA_HEURISTICA = 100.0


def _detectar_rostro(imagen_bytes: bytes):
    arr = np.frombuffer(imagen_bytes, dtype=np.uint8)
    imagen = cv2.imdecode(arr, cv2.IMREAD_GRAYSCALE)
    if imagen is None:
        return None

    rostros = _face_cascade.detectMultiScale(
        imagen, scaleFactor=1.1, minNeighbors=5, minSize=(60, 60)
    )
    if len(rostros) == 0:
        return None

    x, y, w, h = max(rostros, key=lambda r: r[2] * r[3])
    recorte = imagen[y : y + h, x : x + w]
    return cv2.resize(recorte, TAMANIO_ROSTRO)


@app.get("/health")
def health():
    return {"status": "ok"}


@app.post("/comparar-rostros")
async def comparar_rostros(
    referencia: UploadFile = File(...), captura: UploadFile = File(...)
):
    rostro_referencia = _detectar_rostro(await referencia.read())
    rostro_captura = _detectar_rostro(await captura.read())

    if rostro_referencia is None or rostro_captura is None:
        return {
            "rostroDetectadoReferencia": rostro_referencia is not None,
            "rostroDetectadoCaptura": rostro_captura is not None,
            "similitud": 0.0,
            "distancia": None,
        }

    recognizer = cv2.face.LBPHFaceRecognizer_create()
    recognizer.train([rostro_referencia], np.array([0]))
    _, distancia = recognizer.predict(rostro_captura)

    similitud = max(0.0, 1.0 - (distancia / DISTANCIA_MAXIMA_HEURISTICA))

    return {
        "rostroDetectadoReferencia": True,
        "rostroDetectadoCaptura": True,
        "similitud": round(float(similitud), 4),
        "distancia": round(float(distancia), 2),
    }
