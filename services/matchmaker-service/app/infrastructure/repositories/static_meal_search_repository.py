from app.domain.models.meal import Meal
from app.ports.output.meal_search_repository_interface import MealSearchRepositoryInterface
from typing import Optional, List

class StaticMealSearchRepository(MealSearchRepositoryInterface):
    def __init__(self):
        self.meals = [
            Meal(id="1", name="Spaghetti Bolognese", cuisine="Italian", ingredients=["pasta", "tomato", "beef"]),
            Meal(id="2", name="Margherita Pizza", cuisine="Italian", ingredients=["dough", "cheese", "tomato"]),
            Meal(id="3", name="Pad Thai", cuisine="Thai", ingredients=["noodles", "tofu", "peanuts", "egg"]),
            Meal(id="4", name="Green Curry", cuisine="Thai", ingredients=["coconut milk", "chicken", "basil", "lime"]),
            Meal(id="5", name="Chickpea Curry", cuisine="Indian", ingredients=["chickpeas", "tomato", "garlic"]),
            Meal(id="6", name="Sushi Roll", cuisine="Japanese", ingredients=["rice", "seaweed", "salmon", "avocado"]),
            Meal(id="7", name="Falafel Wrap", cuisine="Middle Eastern", ingredients=["falafel", "pita", "lettuce", "tomato"]),
        ]
    
    def search_meals(
        self,
        cuisine: Optional[str],
        required_ingredients: Optional[List[str]] = [],
        excluded_ingredients: Optional[List[str]] = []
    ) -> List[Meal]:
        return [
            meal for meal in self.meals
            if (not cuisine or meal.cuisine.lower() == cuisine.lower())
            and any(ingredient in meal.ingredients for ingredient in required_ingredients)
            and not any(ingredient in meal.ingredients for ingredient in excluded_ingredients)
        ]
