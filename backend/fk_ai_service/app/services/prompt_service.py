from typing import Any, Dict
from pydantic import BaseModel


class AnalysisContext(BaseModel):
    user_role: str = "User"
    domain: str = "General"
    additional_constraints: list[str] | None = None


class PromptService:
    """Handles prompt template rendering and system instruction construction."""

    SYSTEM_INSTRUCTIONS: Dict[str, str] = {
        "default": (
            "You are an expert AI assistant. Provide concise, accurate, and structured responses. "
            "Follow all formatting constraints strictly."
        ),
        "data_analyst": (
            "You are a Senior Data Scientist and Analyst. Provide insights backed by logic, "
            "highlight key statistical anomalies, and format findings clearly using markdown."
        ),
    }

    @classmethod
    def get_system_instruction(cls, role: str = "default", extra_context: str | None = None) -> str:
        """Retrieves and enhances system instructions for a given role."""
        base_instruction = cls.SYSTEM_INSTRUCTIONS.get(role, cls.SYSTEM_INSTRUCTIONS["default"])
        if extra_context:
            return f"{base_instruction}\n\nAdditional Guidance:\n{extra_context}"
        return base_instruction

    @staticmethod
    def build_analysis_prompt(data: Dict[str, Any], context: AnalysisContext | None = None) -> str:
        """Formats raw input data into a clean structured prompt."""
        prompt_parts = ["Analyze the following data payload and extract key insights:"]
        
        if context:
            prompt_parts.append(f"\nDomain Context: {context.domain}")
            if context.additional_constraints:
                constraints = "\n".join(f"- {c}" for c in context.additional_constraints)
                prompt_parts.append(f"Constraints:\n{constraints}")

        prompt_parts.append("\n--- Data Payload ---")
        for key, value in data.items():
            prompt_parts.append(f"{key}: {value}")
        prompt_parts.append("--- End Payload ---")

        return "\n".join(prompt_parts)