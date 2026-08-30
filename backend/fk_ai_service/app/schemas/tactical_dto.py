from pydantic import BaseModel, Field
from typing import List, Dict

class MatchTimelineInterval(BaseModel):
    interval: str
    momentum_description: str
    key_events_count: int

class TacticalAnalysisRequest(BaseModel):
    match_title: str
    
    expected_stats: Dict[str, float]
    actual_stats: Dict[str, float]
    top_performers: List[str]
    underperformers: List[str]
    
    match_timeline: List[MatchTimelineInterval]
    
    tactical_anomalies: List[str]

class TacticalAnalysisResponse(BaseModel):
    report: str