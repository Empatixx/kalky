// Static export serves from a sub-path on GitHub Pages, and raw string srcs are
// not rewritten for us.
const basePath = process.env.NEXT_PUBLIC_BASE_PATH ?? '';

export function asset(path: string) {
  return `${basePath}${path}`;
}
