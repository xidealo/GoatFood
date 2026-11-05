# Handoff: GoatFood лендинг → Amvera + www.goatfood.ru

Маркетинговый сайт из `landing/`. Деплой-пакет: `landingamvera/` (как `amvera/` для webApp заказов).

**Репозиторий:** https://github.com/xidealo/PapaKarlo (весь монорепо, как у сайта заказов)  
**Домен лендинга:** `https://www.goatfood.ru`  
**API:** `https://goatfood.ru` остаётся на приложении fooddelivery  
**Пакет:** `landingamvera/` (Docker + nginx + статика)

---

## Что сделать в Amvera (вручную)

1. Приложение `goatfood-landing`, тип **Docker**, порт **80**.
2. Репозиторий: GitHub PapaKarlo (как у web).
3. В **Конфигурация** → dockerfile:
   ```text
   landingamvera/Dockerfile
   ```
   (без точки, папка `landingamvera`).
4. Дождаться успешного билда на `*.amvera.io`.
5. **Домены** → добавить **`www.goatfood.ru`** (не `goatfood.ru`).
6. У регистратора DNS: CNAME `www` → хост из панели Amvera.
7. Дождаться SSL. Проверить: `https://www.goatfood.ru` = лендинг.

`goatfood.ru` с fooddelivery не снимать.

---

## Обновление бандла

```bash
cd /path/to/PapaKarlo
./landingamvera/deploy.sh
git add landingamvera/
git commit -m "update GoatFood landing"
git push
```

Amvera подхватит push из GitHub и пересоберёт образ.

---

## Архитектура

```text
Браузер → www.goatfood.ru (goatfood-landing, nginx)
                └─ статика из landingamvera/site/

Браузер → goatfood.ru (fooddelivery) → API
```

API заказов / papakarlo-web — отдельные приложения, этот пакет их не проксирует.

---

## Файлы

- `landing/` — исходники Vite/React
- `landingamvera/Dockerfile` — COPY путей от корня монорепо
- `landingamvera/nginx.conf`
- `landingamvera/deploy.sh`
- `landingamvera/site/` — собранный бандл

---

## Чеклист

- [ ] dockerfile в Amvera: `landingamvera/Dockerfile`
- [ ] Билд exit 0, сайт на `*.amvera.io`
- [ ] Домен `www.goatfood.ru` + DNS + SSL
- [ ] `goatfood.ru` по-прежнему API
