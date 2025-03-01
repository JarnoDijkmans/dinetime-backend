from fastapi import APIRouter

from app.presentation.api.controllers.ranking_controller import router as ranking_router

routers = APIRouter()
router_list = [ranking_router]

for router in router_list:
    routers.include_router(router)