# app/infrastructure/config/container.py
from dependency_injector import containers, providers
from app.application.services.matchmaker_service import MatchmakerService
from app.infrastructure.repositories.meal_search_repository import MealSearchRepository
from app.ports.input.matchmaker_service_interface import MatchmakerServiceInterface
from app.ports.output.meal_search_repository_interface import MealSearchRepositoryInterface

class Container(containers.DeclarativeContainer):
  
    meal_search_repository: MealSearchRepositoryInterface = providers.Singleton(
        MealSearchRepository
    )

    matchmaker_service: MatchmakerServiceInterface = providers.Factory(
        MatchmakerService,
        meal_search_repository=meal_search_repository
    )