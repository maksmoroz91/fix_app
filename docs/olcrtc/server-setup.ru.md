# Настройка server.yaml для DreamWalker

Этот Android-клиент запускает olcRTC с такими параметрами:

- `auth.provider`: `jitsi`
- `net.transport`: `datachannel`
- локальный SOCKS5 внутри Android: `127.0.0.1:10808`
- liveness: `30s` / `90s` / `3` пропуска

Готовый шаблон сервера лежит в [server.yaml.example](server.yaml.example). Его нужно положить на сервер как `/etc/olcrtc/server.yaml` и заменить только свои значения:

- `room.id` - полный URL комнаты Jitsi, например `https://meet.handyweb.org/<room>`
- `crypto.key` - общий hex-ключ из 64 символов
- `socks.proxy_addr` / `socks.proxy_port` - upstream SOCKS5 на сервере, например Xray. Если Xray не нужен, удалите весь блок `socks`.

В приложении те же значения должны быть в `.env`:

```env
JITSI_ROOM_ID=https://meet.handyweb.org/<room>
OLCRTC_CARRIER=jitsi
OLCRTC_KEY=<тот же 64-hex-ключ>
```

`jitsi: waiting for peer in room (not a failure)` - это не зависание сервера. Это означает, что сервер вошёл в комнату Jitsi и ждёт второго участника. Соединение пойдёт дальше только после запуска Android-клиента с тем же `room.id`, `net.transport` и `crypto.key`.

Если сервер продолжает ждать:

- проверьте, что `room.id` и `JITSI_ROOM_ID` совпадают посимвольно, включая домен и путь;
- проверьте, что `crypto.key` и `OLCRTC_KEY` совпадают и имеют 64 hex-символа;
- используйте `liveness.interval`, а не старое/ошибочное поле `liveness.ping_interval`;
- временно включите `debug: true` и перезапустите сервис;
- проверьте, что выбранный Jitsi-домен открывается и с сервера, и с телефона.

Для Jitsi актуальная рекомендованная связка olcRTC - `jitsi + datachannel`; `vp8channel`, `seichannel` и `videochannel` для этого клиента менять не нужно.

Справка olcRTC:

- [manual.md](https://github.com/openlibrecommunity/olcrtc/blob/master/docs/manual.md)
- [settings.md](https://github.com/openlibrecommunity/olcrtc/blob/master/docs/settings.md)
