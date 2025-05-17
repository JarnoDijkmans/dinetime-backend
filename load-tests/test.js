import ws from 'k6/ws';
import { check } from 'k6';

export let options = {
  scenarios: {
    constant_connections: {
      executor: 'per-vu-iterations',
      vus: 300,
      iterations: 1,
      maxDuration: '2m', // Safety net
    },
  },
};


export default function () {
  const url = 'ws://localhost/realtime/';
  const params = { tags: { test_type: 'persistent_connection' } };
  const res = ws.connect(url, params, function (socket) {
    socket.on('open', () => {
      console.log(`VU ${__VU} connected`);
      socket.setInterval(() => {
        socket.ping();
        console.log(`VU ${__VU} sent ping`);
      }, 10000);

      // Keep alive for full duration, then close
      socket.setTimeout(() => {
        console.log(`VU ${__VU} closing after 2 minutes`);
        socket.close();
      }, 60000);
    });

    socket.on('pong', () => {
      console.log(`VU ${__VU} received pong`);
    });

    socket.on('close', () => {
      console.log(`VU ${__VU} disconnected`);
    });

    socket.on('error', (e) => {
      console.error(`VU ${__VU} encountered error: ${e.error()}`);
    });
  });

  check(res, { 'status is 101': (r) => r && r.status === 101 });
}
