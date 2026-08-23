import { asset } from '@/lib/asset';
import { Parallax } from './parallax';

/** Full-bleed photograph with one line over it. */
export function Band({ photo, alt, line }: { photo: string; alt: string; line: string }) {
  return (
    <section className="relative h-[60vh] min-h-[380px] overflow-hidden">
      <Parallax strength={70} className="absolute inset-0 -top-[12%] h-[124%]">
        <img src={asset(photo)} alt={alt} loading="lazy" decoding="async" className="h-full w-full object-cover" />
      </Parallax>
      <div className="absolute inset-0 bg-gradient-to-t from-black/65 via-black/25 to-black/10" />
      <div className="absolute inset-0 flex items-end">
        <p className="k-display text-balance px-6 pb-12 text-3xl text-white sm:px-12 sm:pb-16 sm:text-5xl">
          {line}
        </p>
      </div>
    </section>
  );
}
