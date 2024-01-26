# Stage 1: Build the application
FROM node:lts as build

WORKDIR /dist/src/app
COPY package.json package-lock.json ./
RUN npm install

COPY . .
RUN npm run build

# Stage 2: Serve the application
FROM nginx:alpine

RUN rm -rf  /usr/share/nginx/html/*
COPY --from=build /dist/src/app/dist/todo-app-frontend /usr/share/nginx/html
COPY nginx.conf  /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
