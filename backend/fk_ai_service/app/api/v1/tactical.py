
from fastapi import APIRouter, Depends, HTTPException, status
from app.dependencies import get_gemini_service
from app.schemas.tactical_dto import TacticalAnalysisRequest, TacticalAnalysisResponse
from app.services.gemini_service import GeminiService
from app.services.prompt_service import PromptService  # Uvezen tvoj servis
import json

router = APIRouter(prefix="/tactical", tags=["Tactical Analysis"])

@router.post("/generate", response_model=TacticalAnalysisResponse)
def generate_tactical_analysis(
    payload: TacticalAnalysisRequest,
    gemini_service: GeminiService = Depends(get_gemini_service)
):
    timeline_str = "\n".join([
        f"- {t.interval}: {t.momentum_description} (Ključnih događaja: {t.key_events_count})"
        for t in payload.match_timeline
    ]) if payload.match_timeline else "Nema dostupnih podataka o momentumu."

    best_str = "\n".join([f"- {p}" for p in payload.top_performers]) or "Niko se nije posebno istakao."
    weak_str = "\n".join([f"- {p}" for p in payload.underperformers]) or "Nema izrazito loših pojedinaca."
    anomalies_str = "\n".join([f"- {a}" for a in payload.tactical_anomalies]) or "Nisu detektovane sistemske greške."
    
    payload_data = {
        "expected": json.dumps(payload.expected_stats, indent=2, ensure_ascii=False),
        "actual": json.dumps(payload.actual_stats, indent=2, ensure_ascii=False),
        "best": best_str,
        "weak": weak_str,
        "timeline": timeline_str,
        "anomalies": anomalies_str
    }

    system_instruction = PromptService.get_system_instruction(role="tactical_analyst")
    prompt = PromptService.build_tactical_prompt(payload_data, payload.match_title)

    try:
        final_prompt = f"{system_instruction}\n\n{prompt}"
        
        response_text = gemini_service.generate_text(prompt=final_prompt)
        
        return TacticalAnalysisResponse(report=response_text)
    except Exception as err:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Greška pri generisanju AI analize: {str(err)}",
        )