import { updateFcmToken } from "../db/users";
import type { VerifiedToken } from "../services/firebase";

export async function handleFcmToken(req: Request, authData: VerifiedToken): Promise<Response> {
    try {
        const body = await req.json() as { token?: string };
        if (!body.token) {
            return Response.json({ error: "Missing token" }, { status: 400 });
        }
        updateFcmToken(authData.uid, body.token);
        return Response.json({ status: "ok" });
    } catch (err) {
        return Response.json({ error: "Failed to update FCM token" }, { status: 500 });
    }
}
