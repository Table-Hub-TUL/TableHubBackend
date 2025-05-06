# backend

## Docker tutorial
1. W th-backend (tam gdzie jest Dockerfile) najpierw uruchamiamy komende: **docker build . -t th-backend**
2. Jeśli wszystko poszło bez problemów, wracamy do lokalizacji, w której znajduje się docker-compose.yml
3. Aby uruchomic contener wystarczy wpisać: **docker compose up -d**
4. Jak chcemy wyłączyć contener, wpisujemy: **docker compose down**

### Ważne: Aby komendy działały musimy mieć włączonego w tle Docker Desktop