from abc import ABC, abstractmethod
from app.presentation.request.initial_pool import InitialPool

class MatchmakerServiceInterface(ABC):
    
    @abstractmethod
    def generate_initial_pool(self, user_input: InitialPool) -> list:
        pass