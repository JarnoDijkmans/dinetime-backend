from abc import ABC, abstractmethod
from typing import List
from app.domain.models.meal import Meal

class MealSearchRepositoryInterface(ABC):
    @abstractmethod
    def search_meals(self, cuisine, required_ingredients, excluded_ingredients) -> List[Meal]:
        pass
