from fastapi import FastAPI
from app.presentation.routes import ranking_router
from app.infrastructure.config.container import Container
class AppCreator:
    def __init__(self):
        self.app = FastAPI()

        self.container = Container()
        self.app.container = self.container


        @self.app.get("/")
        def root():
            return "Serivce is working"
        
        self.app.include_router(ranking_router)

app_creator = AppCreator()
app = app_creator.app
container = app_creator.container