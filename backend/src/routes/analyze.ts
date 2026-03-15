import { analyzeImage } from "../services/openai";

export async function handleAnalyze(req: Request): Promise<Response> {
  const contentType = req.headers.get("content-type") || "";
  if (!contentType.includes("image/")) {
    return Response.json(
      { error: "Expected Content-Type: image/jpeg" },
      { status: 400 }
    );
  }

  const imageBytes = await req.arrayBuffer();
  if (imageBytes.byteLength === 0) {
    return Response.json({ error: "Empty image body" }, { status: 400 });
  }

  const base64 = Buffer.from(imageBytes).toString("base64");
  const analysis = await analyzeImage(base64);
  return Response.json(analysis);
}
