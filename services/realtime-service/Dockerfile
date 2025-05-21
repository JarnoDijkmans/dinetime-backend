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

# ===== PRODUCTION STAGE: Minimal final image =====
FROM node:18-alpine AS prod
WORKDIR /app
COPY --from=base /app/dist ./dist
COPY --from=base /app/package*.json ./
RUN npm ci --production
ENV NODE_ENV=production
EXPOSE 80
CMD ["node", "dist/app.js"]
