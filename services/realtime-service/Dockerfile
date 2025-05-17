# ===== BASE STAGE: Install dependencies and build TypeScript =====
FROM node:18 AS base
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

# ===== TEST STAGE: For running Jest tests =====
FROM base AS test
ENV NODE_ENV=test
RUN npm install --only=dev
CMD ["npm", "test"]

FROM node:18-alpine AS prod
WORKDIR /app
COPY --from=base /app/dist ./dist
COPY --from=base /app/package*.json ./
RUN npm ci --production
RUN npm install -g pm2

ENV NODE_ENV=production
EXPOSE 5000

CMD ["pm2-runtime", "dist/app.js", "-i", "max"]
