import Image from 'next/image';

// Unoptimized next/image passes a string src through untouched, so basePath has
// to be applied here or the images 404 under the GitHub Pages sub-path.
const basePath = process.env.NEXT_PUBLIC_BASE_PATH ?? '';
export function Screenshot({
  src,
  alt,
  caption,
}: {
  src: string;
  alt: string;
  caption?: string;
}) {
  return (
    <figure className="my-6 flex flex-col items-center gap-2">
      <Image
        src={`${basePath}${src}`}
        alt={alt}
        width={360}
        height={800}
        className="w-full max-w-[280px] rounded-xl border border-fd-border shadow-sm"
      />
      {caption ? (
        <figcaption className="text-center text-sm text-fd-muted-foreground">{caption}</figcaption>
      ) : null}
    </figure>
  );
}

export function ScreenshotRow({ children }: { children: React.ReactNode }) {
  return <div className="my-6 flex flex-wrap justify-center gap-4">{children}</div>;
}
