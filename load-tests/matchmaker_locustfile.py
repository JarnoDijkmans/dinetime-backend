import json
from locust import HttpUser, task, between
import random

class MatchmakerUser(HttpUser):
    wait_time = between(1, 3)

    CUISINES = ["Italian", "Mexican", "Chinese", "Indian", "French"]
    INGREDIENTS = ["Tomato", "Cheese", "Chicken", "Basil", "Rice"]

    def on_start(self):
        self.game_code = None

    @task(2)
    def generate_initial_pool(self):

        ingredients = random.sample(self.INGREDIENTS, len(self.INGREDIENTS))

        required = ingredients[:2]
    
        payload = {
            "userId": "123",
            "cuisine": random.sample(self.CUISINES, k=2),
            "requiredIngredients": required,
            "excludedIngredients": []
        }

        print("▶️ Sending payload:", json.dumps(payload))
        response = self.client.post("/matchmaker/generate-initial-pool", json=payload)

        print("🔄 Status:", response)

        if response.status_code == 200:
            try:
                self.game_code = response.json().get("gameCode")
            except Exception as e:
                print("⚠️ Failed to parse gameCode:", e)

    @task(1)
    def get_current_pool(self):
        if self.game_code:
            self.client.get(f"/matchmaker/get-pool/{self.game_code}")
