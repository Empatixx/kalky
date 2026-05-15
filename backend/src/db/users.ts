import type { User as PrismaUser } from "@prisma/client";
import { prisma } from "./prisma";

export type User = PrismaUser;

export async function getUserByFirebaseUid(firebaseUid: string): Promise<User | null> {
  return prisma.user.findUnique({ where: { firebaseUid } });
}

export async function getOrCreateUser(
  firebaseUid: string,
  email?: string,
  displayName?: string,
  photoUrl?: string,
): Promise<User> {

  return prisma.user.upsert({
    where: { firebaseUid },
    create: {
      firebaseUid,
      email: email ?? null,
      displayName: displayName ?? null,
      photoUrl: photoUrl ?? null,
    },
    update: {
      ...(email !== undefined ? { email } : {}),
      ...(displayName !== undefined ? { displayName } : {}),
      ...(photoUrl !== undefined ? { photoUrl } : {}),
    },
  });
}

export async function updateFcmToken(firebaseUid: string, fcmToken: string): Promise<void> {
  await prisma.user.update({
    where: { firebaseUid },
    data: { fcmToken },
  });
}
