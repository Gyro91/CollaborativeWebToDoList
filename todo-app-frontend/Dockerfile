# Stage 1: Build the application
FROM node:lts as build

WORKDIR /todo-app-frontend
COPY package.json package-lock.json ./
RUN npm install

COPY . .
RUN npm run build

# Stage 2: Serve the application
FROM nginx:alpine

COPY --from=build /todo-app-frontend/dist/todo-app-frontend /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
