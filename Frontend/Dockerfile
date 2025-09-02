## Multi-stage build for React (Vite) frontend
FROM node:20-alpine AS build
WORKDIR /app

# Install deps separately for better caching
COPY package*.json ./
RUN npm ci --no-audit --no-fund

# Copy source and build
COPY . .
RUN npm run build

## Runtime stage (Nginx)
FROM nginx:1.27-alpine

# Remove default config and add ours (SPA + /api proxy)
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Static assets
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80

# (Optional) Basic healthcheck hitting index.html
HEALTHCHECK CMD wget -q -O /dev/null http://localhost || exit 1
