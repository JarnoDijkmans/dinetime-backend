import json

from app.domain.models.meal import Meal
from app.ports.output.meal_search_repository_interface import MealSearchRepositoryInterface
from typing import Optional, List

from typing import Optional, List

class MealSearchRepository(MealSearchRepositoryInterface):
    def __init__(self):
        with open("meals.json", "r", encoding="utf-8") as f:
            raw_meals = json.load(f)

        self.meals = []
        for idx, item in enumerate(raw_meals):
            self.meals.append(
                Meal(
                    id=str(idx + 1),  
                    name=item["title"],
                    cuisine=item.get("cuisine", "Unknown"),
                    ingredients=item["ingredients"]
                )
            )

    def search_meals(
        self,
        cuisine: Optional[List[str]] = None,
        required_ingredients: Optional[List[str]] = None,
        excluded_ingredients: Optional[List[str]] = None,
        limit: int = 10
        ) -> List[Meal]:
            cuisine = cuisine or []
            required_ingredients = required_ingredients or []
            excluded_ingredients = excluded_ingredients or []

            scored_meals = []

            for meal in self.meals:
                meal_ingredients = [ing.lower() for ing in meal.ingredients]

                if not self.is_valid_meal(meal_ingredients, excluded_ingredients):
                    continue

                score = 0

                for cui in cuisine:
                    if cui.lower() in meal.cuisine.lower():
                        score += 1

                for req in required_ingredients:
                    if any(req.lower() in ing for ing in meal_ingredients):
                        score += 1

                scored_meals.append((meal, score))

            scored_meals.sort(key=lambda x: x[1], reverse=True)

            return [meal for meal, _ in scored_meals[:limit]]

    

    def is_valid_meal(self, meal_ingredients, excluded_terms):
        for ex in excluded_terms:
            if any(ex.lower() in ing for ing in meal_ingredients):
                return False
        return True

