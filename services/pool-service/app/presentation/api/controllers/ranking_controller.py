# app/presentation/api/controllers/user_controller.py
from fastapi import APIRouter, Depends, HTTPException
from dependency_injector.wiring import inject, Provide
from app.core.interfaces.serv.i_ranking_service import IRankingService
from app.infrastructure.config.container import Container
from app.core.dtos.ranking_dto import GetRankingDTO

router = APIRouter()

@router.get("/ranking")
@inject
def get_ranking(
    # ranking_dto: GetRankingDTO,
    ranking_service: IRankingService = Depends(Provide[Container.ranking_service])
):
    try:
        print("🔗 Injected Service:", ranking_service)  
        
        result = ranking_service.get_ranking()
        
        print("✅ Ranking Service Result:", result)  
        
        return result
    except Exception as e:
        print("❌ Error Occurred:", e)  # Debugging: Log errors
        raise HTTPException(status_code=400, detail=str(e))
