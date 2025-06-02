# Liber Cinema

Liber Cinema to aplikacja internetowa do zarządzania kolekcją filmów i książek, umożliwiająca użytkownikom tworzenie list oglądanych i planowanych do obejrzenia filmów, ocenianie ich oraz interakcję z innymi użytkownikami.

## Zastosowane technologie

### Backend
- **Java 21**
- **Spring Boot 3.x** - framework do tworzenia aplikacji webowych
- **Spring Security** - uwierzytelnianie i autoryzacja
- **Spring Data JPA** - warstwa dostępu do danych
- **PostgreSQL** - relacyjna baza danych
- **JSON Web Token (JWT)** - bezpieczna autoryzacja
- **Lombok** - redukcja kodu boilerplate
- **Maven** - zarządzanie zależnościami i budowanie projektu

### Frontend
- **React 18** - biblioteka JavaScript do budowania interfejsów użytkownika
- **TypeScript** - typowany JavaScript
- **Vite** - szybkie narzędzie do budowania aplikacji
- **CSS** - stylizacja komponentów

### Narzędzia deweloperskie i infrastruktura
- **Docker** - konteneryzacja aplikacji
- **Docker Compose** - orkiestracja kontenerów
- **OMDB API** - zewnętrzne API do pobierania informacji o filmach (wymaga własnego klucza API)

### Uzasadnienie
- **Backend** : Stabilny, bezpieczny, wydajny stos z Javą i Spring.
- **Frontend** : Nowoczesny, szybki, typowany interfejs w React i TS.
- **Docker** : Spójne środowisko, łatwe uruchamianie i wdrażanie aplikacji.

## Uruchomienie z wykorzystaniem Docker

Projekt można uruchomić z wykorzystaniem Docker i Docker Compose, co znacznie upraszcza proces konfiguracji środowiska.

### Wymagania
- Zainstalowany Docker
- Zainstalowany Docker Compose

### Instrukcje uruchomienia

1. Sklonuj repozytorium:
```bash
git clone <adres-repozytorium>
cd liber-cinema
```

2. Upewnij się, że w pliku `docker-compose.yml` zdefiniowana jest zmienna środowiskowa `API_KEY` dla backendu:
```yaml
backend:
  # ...pozostałe konfiguracje...
  environment:
    # ...pozostałe zmienne...
    - API_KEY=twój_klucz_api  # Klucz API dla OMDB
```

> **Uwaga:** Klucz API dla OMDB jest prywatny i należy go uzyskać na stronie [http://www.omdbapi.com/apikey.aspx](http://www.omdbapi.com/apikey.aspx). W powyższym przykładzie należy zastąpić `twój_klucz_api` własnym kluczem API.

3. Uruchom aplikację za pomocą Docker Compose:
```bash
docker-compose up -d
```

4. Aplikacja będzie dostępna pod adresami:
   - Frontend: http://localhost:5173
   - Backend API: http://localhost:8080
   - Baza danych PostgreSQL: localhost:5432 

5. Aby zatrzymać aplikację:
```bash
docker-compose down
```

6. Aby zatrzymać aplikację i usunąć dane (wolumeny):
```bash
docker-compose down -v
```

## Uruchomienie lokalnie (bez Dockera)

### Wymagania
- Java 21 JDK
- Node.js (wersja 16+)
- npm lub yarn
- PostgreSQL (wersja 13+)

### Konfiguracja bazy danych
1. Zainstaluj i uruchom PostgreSQL
2. Utwórz bazę danych:
```sql
CREATE DATABASE liber_cinema;
```

### Uruchomienie backendu
1. Skonfiguruj zmienne środowiskowe:
   - Utwórz plik `.env` w głównym katalogu projektu z następującą zawartością:
   ```
   API_KEY=twój_klucz_api
   ```
   
   > **Uwaga:** Klucz API dla OMDB jest prywatny i należy go uzyskać na stronie [http://www.omdbapi.com/apikey.aspx](http://www.omdbapi.com/apikey.aspx). W powyższym przykładzie należy zastąpić `twój_klucz_api` własnym kluczem API.

2. Skonfiguruj połączenie do bazy danych w pliku `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/liber_cinema
spring.datasource.username=twój_użytkownik
spring.datasource.password=twoje_hasło
```

3. Uruchom aplikację backendową:
```bash
./mvnw spring-boot:run
```
Dla Windows:
```bash
mvnw.cmd spring-boot:run
```

### Uruchomienie frontendu
1. Przejdź do katalogu frontend:
```bash
cd frontend
```

2. Zainstaluj zależności:
```bash
npm install
# lub
yarn install
```

3. Uruchom serwer deweloperski:
```bash
npm run dev
# lub
yarn dev
```

4. Frontend będzie dostępny pod adresem http://localhost:5173


## Struktura projektu

- `src/main/java` - kod źródłowy backendu
- `src/main/resources` - pliki konfiguracyjne i zasoby
- `frontend/src` - kod źródłowy frontendu
- `Dockerfile` - konfiguracja kontenera dla backendu
- `frontend/Dockerfile` - konfiguracja kontenera dla frontendu
- `docker-compose.yml` - konfiguracja Docker Compose dla całej aplikacji
