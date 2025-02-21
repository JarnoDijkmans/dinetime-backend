# app/core/services/user_service.py
from app.core.interfaces.serv.i_ranking_service import IRankingService
from app.core.dtos.ranking_dto import GetRankingDTO

class RankingServiceImpl(IRankingService):
    def get_ranking(self, ranking_dto: GetRankingDTO) -> dict:
        # Implementation logic here
        return {"message": f"User {ranking_dto.name} created"}
