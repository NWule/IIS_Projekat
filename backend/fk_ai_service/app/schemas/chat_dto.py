from pydantic import BaseModel, Field


class DirectPromptRequest(BaseModel):
    prompt: str