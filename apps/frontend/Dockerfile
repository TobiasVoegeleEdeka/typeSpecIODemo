# Run stage - Fast & Lightweight
FROM nginx:alpine
# Copy the locally built Angular app directly
COPY dist/apps/frontend/browser /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
