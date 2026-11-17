# syntax=docker/dockerfile:1

# ---------------------------------------------------------------- build
FROM node:26-alpine AS build
WORKDIR /app

COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npm run build

# ---------------------------------------------------------------- runtime
FROM nginx:1.29-alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf
# El application builder de Angular deja la SPA en dist/awra-public/browser.
COPY --from=build /app/dist/awra-public/browser /usr/share/nginx/html

EXPOSE 80

HEALTHCHECK --interval=15s --timeout=5s --start-period=10s --retries=5 \
    # 127.0.0.1 y no localhost: dentro del contenedor localhost resuelve a ::1
    # y nginx escucha solo en IPv4.
    CMD wget -q -O /dev/null http://127.0.0.1/ || exit 1
