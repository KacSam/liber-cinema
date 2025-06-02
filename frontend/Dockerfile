FROM node:20-alpine

WORKDIR /app

# Kopiowanie plików package.json i package-lock.json
COPY package*.json ./

# Instalacja zależności
RUN npm install

# Kopiowanie reszty kodu źródłowego
COPY . .

# Expose port 5173 (domyślny port Vite)
EXPOSE 5173

# Uruchomienie serwera deweloperskiego
CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0"]
