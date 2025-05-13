import { check } from "k6";
import ws from "k6/ws";
import { sleep } from "k6";
import { uuidv4 } from "https://jslib.k6.io/k6-utils/1.4.0/index.js";

export let options = {
  vus: 1000,
  stages: [
    { duration: "30s", target: 5000 },
    { duration: "30s", target: 10000 },
    { duration: "1m", target: 20000 },
    { duration: "1m", target: 30000 },
  ]
};

const MEAL_IDS = [
  "meal-001", "meal-002", "meal-003", "meal-004", "meal-005",
  "meal-006", "meal-007", "meal-008", "meal-009", "meal-010",
];

export default function () {
    const userId = uuidv4();
  
    // 👇 Each user gets a unique lobby by using the VU number
    const lobbyCode = `LOBBY-${(__VU % 5).toString().padStart(5, "0")}`;
  
    const url = "ws://localhost/realtime/leaderboard-events";
  
    const res = ws.connect(url, {}, function (socket) {
      console.log(`🔌 ${userId} connected`);
  
      socket.on("open", function () {
        const joinMsg = {
          type: "join_lobby",
          lobbyCode: lobbyCode,
        };
        socket.send(JSON.stringify(joinMsg));
        console.log(`🟢 ${userId} joined lobby ${lobbyCode}`);
  
        for (let i = 0; i < MEAL_IDS.length; i++) {
          const voteMsg = {
            type: "vote_meal",
            userId: userId,
            mealId: MEAL_IDS[i],
            score: Math.floor(Math.random() * 201) - 100,
            lobbyCode: lobbyCode,
          };
          socket.send(JSON.stringify(voteMsg));
          console.log(`🍽️ ${userId} voted on ${MEAL_IDS[i]}`);
          sleep(1);
        }
  
        socket.close();
      });
  
      socket.on("message", function (data) {
        console.log(`📩 ${userId} received: ${data}`);
      });
  
      socket.on("close", function () {
        console.log(`❌ ${userId} disconnected`);
      });
  
      socket.on("error", function (e) {
        console.log(`🚨 ${userId} error: ${e.error()}`);
      });
    });
  
    check(res, { "connection established": (r) => r && r.status === 101 });
  }