import ws from 'k6/ws';
import { check } from 'k6';

export let options = {
  vus: 600,
  duration: '1m',
};

export default function () {
  const url = 'ws://localhost/realtime/';
  let joinConfirmed = false;

  const res = ws.connect(url, {}, function(socket) {
    socket.on('open', function() {
      socket.send(JSON.stringify({
        type: "join_lobby",
        lobbyCode: "TEST-LOBBY-9999",
      }));
    });

    socket.on('message', function(msg) {
      console.log('GOT:', msg); // debug: see message structure!
      try {
        const data = JSON.parse(msg);
        if (
          data.type === "joined_lobby" &&
          data.payload &&
          data.payload.lobbyCode === "TEST-LOBBY-9999"
        ) {
          joinConfirmed = true;
        }
      } catch (e) {
        console.log('PARSE ERROR:', msg);
      }
    });

    socket.setTimeout(function() {
      socket.close();
    }, 3000);
  });

  check(joinConfirmed, {
    "join_lobby bevestigd door server": (v) => v === true
  });
}
