export type JwtClaims = GuestJwtClaims | UserJwtClaims;

export interface BaseJwtClaims {
  sub: string;       
  iss: string;
  iat: number;
  exp: number;
  role: string;
  [key: string]: any;
}

export interface GuestJwtClaims extends BaseJwtClaims {
  device: string;
}

export interface UserJwtClaims extends BaseJwtClaims {
  userId: string;
  email?: string;
}

export function isGuestClaims(claims: JwtClaims): claims is GuestJwtClaims {
  return claims.role === "guest";
}
export function isUserClaims(claims: JwtClaims): claims is UserJwtClaims {
  return claims.role === "user";
}