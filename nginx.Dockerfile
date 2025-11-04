FROM nginx:latest

# Copy your custom Nginx config into the image
COPY nginx.conf /etc/nginx/nginx.conf
