# app/core/services/user_service.py
from app.ports.input.matchmaker_service_interface import MatchmakerServiceInterface
from app.ports.output.meal_search_repository_interface import MealSearchRepositoryInterface
from app.presentation.request.initial_pool import InitialPool

class MatchmakerService(MatchmakerServiceInterface):
    def __init__(self, meal_search_repository: MealSearchRepositoryInterface):
        self.meal_search_repository = meal_search_repository
    
    def generate_initial_pool(self, user_input: InitialPool) -> list: 
        
        return self.meal_search_repository.search_meals(user_input.cuisine, user_input.required_ingredients, user_input.excluded_ingredients)