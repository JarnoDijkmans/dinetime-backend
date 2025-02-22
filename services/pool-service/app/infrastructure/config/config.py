import os

from dotenv import load_dotenv
from pydantic import BaseSettings

load_dotenv()


class Configs(BaseSettings):
    # base
    ENV: str = os.getenv("ENV", "dev")
    API: str = "/api"
    API_V1_STR: str = "/api/v1"
    API_V2_STR: str = "/api/v2"
    PROJECT_NAME: str = "ranking-service"


    class Config:
        case_sensitive = True


configs = Configs()