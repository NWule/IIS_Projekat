import os
import logging
from google import genai
from google.genai import types
from google.genai.errors import APIError

from app.services.prompt_service import PromptService
from app.config import settings

logger = logging.getLogger(__name__)

class GeminiService():
    def __init__(
        self,
        default_model: str = settings.DEFAULT_GEMINI_MODEL
    ) -> None:
        self.client = genai.Client(api_key=settings.GEMINI_API_KEY)
        self.default_model = default_model

    def generate_text(
        self,
        prompt: str,
        role: str = "default",
        extra_context: str | None = None,
        system_instruction: str | None = None,
        temperature: float = settings.GEMINI_TEMPERATURE,
        model: str | None = None,
    ) -> str:
        """Generates text response using Gemini AI client."""
        config = types.GenerateContentConfig(
            temperature=temperature,
            system_instruction=system_instruction or PromptService.get_system_instruction(
                role=role,
                extra_context=extra_context,
            ),
        )

        try:
            response = self.client.models.generate_content(
                model=model or self.default_model,
                contents=prompt,
                config=config
            )
            return response.text or ""
        except APIError as e:
            logger.error(f"Gemini API Exception: {e}")
            raise RuntimeError(f"Gemini generation failed: {e}") from e
        