# app/domain/models/meal.py
from pydantic import BaseModel
from typing import List

class Meal(BaseModel):
    id: str
    name: str
    cuisine: str
    ingredients: List[str]
