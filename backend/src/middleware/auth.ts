import { verifyIdToken, type VerifiedToken } from "../services/firebase";

export async function requireAuth(req: Request): Promise<VerifiedToken | Response> {
  const authHeader = req.headers.get("Authorization");
  if (!authHeader || !authHeader.startsWith("Bearer ")) {
    return Response.json(
      { error: "Missing or invalid Authorization header" },
      { status: 401 }
    );
  }

  const token = authHeader.slice("Bearer ".length);
  try {
    return await verifyIdToken(token);
  } catch (err) {
    console.error("Firebase token verification failed:", err);
    return Response.json(
      { error: "Invalid or expired token" },
      { status: 401 }
    );
  }
}

export function requireAdmin(req: Request): Response | null {
  const adminKey = process.env.ADMIN_KEY;
  if (!adminKey) {
    return Response.json(
      { error: "Admin API not configured" },
      { status: 503 }
    );
  }

  const authHeader = req.headers.get("Authorization");
  if (!authHeader || !authHeader.startsWith("Bearer ")) {
    return Response.json(
      { error: "Missing or invalid Authorization header" },
      { status: 401 }
    );
  }

  const token = authHeader.slice("Bearer ".length);
  if (token !== adminKey) {
    return Response.json({ error: "Invalid admin key" }, { status: 401 });
  }

  return null;
}
