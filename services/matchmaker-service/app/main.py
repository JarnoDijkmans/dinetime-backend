from fastapi import FastAPI
from contextlib import asynccontextmanager

from app.presentation.routes import matchmaking_router
from app.infrastructure.config.container import Container

@asynccontextmanager
async def lifespan(app: FastAPI):
    container = Container()
    app.container = container
    
    container.wire(modules=[
        "app.presentation.controllers.matchmaking_controller",
    ])
    
    print("🚀 App Startup: Container wired.")
    
    # Yield control to the application, yield acts as a bridge between the setup and teardown phases.
    yield
    
    container.unwire()
    print("🛑 App Shutdown: Container unwired.")

app = FastAPI(lifespan=lifespan)

app.include_router(matchmaking_router)

@app.get("/")
def root():
    return "Service is working"
