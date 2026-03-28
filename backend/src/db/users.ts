import { getDb } from "./schema";

export interface User {
  id: number;
  firebase_uid: string;
  email: string | null;
  display_name: string | null;
  photo_url: string | null;
  created_at: string;
  updated_at: string;
}

export function getUserByFirebaseUid(uid: string): User | null {
  const db = getDb();
  return db.query<User, [string]>(
    "SELECT * FROM users WHERE firebase_uid = ?"
  ).get(uid);
}

export function getOrCreateUser(
  firebaseUid: string,
  email?: string,
  displayName?: string,
  photoUrl?: string
): User | null {
  const db = getDb();
  return db.query<User, Record<string, unknown>>(`
    INSERT INTO users (firebase_uid, email, display_name, photo_url)
    VALUES ($firebase_uid, $email, $display_name, $photo_url)
    ON CONFLICT(firebase_uid) DO UPDATE SET
      email = COALESCE(excluded.email, users.email),
      display_name = COALESCE(excluded.display_name, users.display_name),
      photo_url = COALESCE(excluded.photo_url, users.photo_url),
      updated_at = datetime('now')
    RETURNING *
  `).get({
    $firebase_uid: firebaseUid,
    $email: email ?? null,
    $display_name: displayName ?? null,
    $photo_url: photoUrl ?? null,
  });
}
