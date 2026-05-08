import { getProductByBarcode } from "../db/products";

export async function handleBarcode(code: string): Promise<Response> {
  const product = await getProductByBarcode(code);
  if (!product) {
    return Response.json({ error: "Product not found" }, { status: 404 });
  }
  return Response.json(product);
}
