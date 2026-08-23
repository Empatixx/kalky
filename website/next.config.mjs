import { createMDX } from 'fumadocs-mdx/next';

const withMDX = createMDX();

// Deployed to GitHub Pages under https://empatixx.github.io/kalai
const basePath = process.env.NEXT_PUBLIC_BASE_PATH ?? '';

/** @type {import('next').NextConfig} */
const config = {
  output: 'export',
  reactStrictMode: true,
  basePath,
  trailingSlash: true,
  // The export target has no image optimisation server.
  images: { unoptimized: true },
};

export default withMDX(config);
