# app/presentation/controllers/user_controller.py
from fastapi import APIRouter, Depends, HTTPException
from dependency_injector.wiring import inject, Provide
from app.ports.input.matchmaker_service_interface import MatchmakerServiceInterface
from app.infrastructure.config.container import Container
from app.presentation.request.initial_pool import InitialPool 

router = APIRouter()

@router.post("/matchmaking/generate-initial-pool")
@inject
def generate_pool(
    user_input: InitialPool,
    matchmaker_service: MatchmakerServiceInterface = Depends(Provide[Container.matchmaker_service])
):
    try:
        result = matchmaker_service.generate_initial_pool(user_input)
        return result
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))
