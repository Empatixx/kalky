import { initializeApp, cert, getApps } from "firebase-admin/app";
import { getAuth, type DecodedIdToken } from "firebase-admin/auth";

function ensureInitialized() {
  if (getApps().length === 0) {
    initializeApp({
      credential: cert(
        process.env.GOOGLE_APPLICATION_CREDENTIALS as string
      ),
    });
  }
}

export interface VerifiedToken {
  uid: string;
  email?: string;
  name?: string;
  picture?: string;
}

export async function verifyIdToken(token: string): Promise<VerifiedToken> {
  ensureInitialized();
  const decoded: DecodedIdToken = await getAuth().verifyIdToken(token);
  return {
    uid: decoded.uid,
    email: decoded.email,
    name: decoded.name,
    picture: decoded.picture,
  };
}
