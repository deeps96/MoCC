FROM nginx:latest

WORKDIR /home
RUN rm /etc/nginx/conf.d/default.conf
COPY ./assignment3-resources/frontend-nginx.conf /etc/nginx/conf.d/
RUN mv front-nginx.conf default.conf