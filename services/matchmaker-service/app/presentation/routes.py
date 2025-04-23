from fastapi import APIRouter

from app.presentation.controllers.matchmaking_controller import router as matchmaking_router

routers = APIRouter()
router_list = [matchmaking_router]

for router in router_list:
    routers.include_router(router)