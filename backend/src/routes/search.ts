import { searchProducts } from "../db/products";

export async function handleSearch(url: URL): Promise<Response> {
  const query = url.searchParams.get("q")?.trim();
  if (!query) {
    return Response.json({ error: "Missing query parameter 'q'" }, { status: 400 });
  }
  const products = await searchProducts(query);
  return Response.json(products);
}
