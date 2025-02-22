# app/infrastructure/config/container.py
from dependency_injector import containers, providers
from app.core.services.ranking_service_impl import RankingServiceImpl
from app.core.interfaces.serv.i_ranking_service import IRankingService

class Container(containers.DeclarativeContainer):
    # Providing the service as the interface type
    ranking_service: IRankingService = providers.Factory(RankingServiceImpl)
