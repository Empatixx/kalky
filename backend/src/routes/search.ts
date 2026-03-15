import { searchProducts } from "../db/products";

export function handleSearch(url: URL): Response {
  const query = url.searchParams.get("q")?.trim();
  if (!query) {
    return Response.json({ error: "Missing query parameter 'q'" }, { status: 400 });
  }
  const products = searchProducts(query);
  return Response.json(products);
}
