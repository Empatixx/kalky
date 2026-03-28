import { getOrCreateUser } from "../db/users";
import type { VerifiedToken } from "../services/firebase";

export async function handleAuthMe(authData: VerifiedToken): Promise<Response> {
  const user = getOrCreateUser(
    authData.uid,
    authData.email,
    authData.name,
    authData.picture
  );

  if (!user) {
    return Response.json(
      { error: "Failed to create or retrieve user" },
      { status: 500 }
    );
  }

  return Response.json(user, { status: 200 });
}
