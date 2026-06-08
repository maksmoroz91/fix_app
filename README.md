# DreamWalker

Android VPN-клиент для olcRTC. Приложение поднимает WebRTC-туннель через
Jitsi и направляет трафик Android VPN через локальный SOCKS5.

## Клиентская настройка

Создайте `.env` на основе [.env.example](.env.example):

```env
JITSI_ROOM_ID=https://meet.handyweb.org/<room>
OLCRTC_CARRIER=jitsi
OLCRTC_KEY=<64-hex-key>
```

`JITSI_ROOM_ID` и `OLCRTC_KEY` должны совпадать с серверным
`/etc/olcrtc/server.yaml`.

## Серверная настройка

Готовый шаблон для сервера:

- [docs/olcrtc/server.yaml.example](docs/olcrtc/server.yaml.example)
- [docs/olcrtc/server-setup.ru.md](docs/olcrtc/server-setup.ru.md)

Главные поля:

- `mode: srv`
- `auth.provider: jitsi`
- `net.transport: datachannel`
- `room.id`: тот же URL, что и `JITSI_ROOM_ID`
- `crypto.key`: тот же ключ, что и `OLCRTC_KEY`
- `liveness.interval: 30s`
- `liveness.timeout: 90s`
- `liveness.failures: 3`

Сообщение `jitsi: waiting for peer in room (not a failure)` означает, что
сервер уже вошёл в комнату Jitsi и ждёт Android-клиент с тем же URL комнаты,
транспортом и ключом.

Если сервер должен выпускать весь трафик через Xray SOCKS5, оставьте в
`server.yaml` блок:

```yaml
socks:
  proxy_addr: "127.0.0.1"
  proxy_port: 10808
```

Если Xray не используется, удалите этот блок, и сервер будет ходить в интернет
напрямую.
