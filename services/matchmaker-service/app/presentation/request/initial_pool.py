from pydantic import BaseModel
from typing import List, Optional;

class InitialPool(BaseModel):
    user_id: str
    cuisine: Optional[List[str]] = []
    required_ingredients: Optional[List[str]] = []
    excluded_ingredients: Optional[List[str]] = []
    
