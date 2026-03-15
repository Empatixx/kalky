import { getProductByBarcode } from "../db/products";

export function handleBarcode(code: string): Response {
  const product = getProductByBarcode(code);
  if (!product) {
    return Response.json({ error: "Product not found" }, { status: 404 });
  }
  return Response.json(product);
}
