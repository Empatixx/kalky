import { asset } from '@/lib/asset';

// The screenshots are 1080x2400, so the frame keeps that aspect exactly.
export function Phone({
  src,
  alt,
  priority = false,
  className = '',
}: {
  src: string;
  alt: string;
  priority?: boolean;
  className?: string;
}) {
  return (
    <div
      className={`relative aspect-[1080/2400] overflow-hidden rounded-[2.2rem] bg-black p-[3px] shadow-[0_30px_80px_-20px_rgba(11,10,15,0.45)] ${className}`}
    >
      <img
        src={asset(src)}
        alt={alt}
        loading={priority ? 'eager' : 'lazy'}
        decoding="async"
        className="h-full w-full rounded-[2rem] object-cover"
      />
    </div>
  );
}
