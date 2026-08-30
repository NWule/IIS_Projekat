from fastapi import FastAPI
from app.api.router import api_router
from app.config import settings
from app.api.v1 import tactical

app = FastAPI(
    title=settings.APP_NAME,
    description=settings.APP_DESCRIPTION,
    version="1.0.0"
)

api_router.include_router(tactical.router)
app.include_router(api_router)


@app.get("/health", tags=["System"])
def health_check():
    return {"status": "UP"}