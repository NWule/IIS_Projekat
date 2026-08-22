from fastapi import APIRouter, Depends, HTTPException, status

from app.dependencies import get_gemini_service
from app.schemas.chat_dto import DirectPromptRequest
from app.services.gemini_service import GeminiService


router = APIRouter(prefix="/chat")

@router.post("/query")
def generate_response(
    payload: DirectPromptRequest,
    gemini_service: GeminiService = Depends(get_gemini_service)
):
    try:
        response = gemini_service.generate_text(
            prompt=payload.prompt,
        )
        return response
    except Exception as err:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Gemini processing error: {str(err)}",
        )