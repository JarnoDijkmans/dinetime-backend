# app/core/dtos/user_dto.py
from pydantic import BaseModel
from uuid import UUID

class GetRankingDTO(BaseModel):
    name: str

class RankingResponseDTO(BaseModel):
    user_id: UUID
    name: str
