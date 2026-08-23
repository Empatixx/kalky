import { asset } from '@/lib/asset';
import { Parallax } from './parallax';

export type SatelliteChip = { value: string; label: string; color: string };

/** A photographed meal with its macros, floating beside the hero phone. */
export function FoodSatellite({
  photo,
  alt,
  kcal,
  chips,
  className = '',
  drift = 34,
}: {
  photo: string;
  alt: string;
  kcal: string;
  chips: SatelliteChip[];
  className?: string;
  drift?: number;
}) {
  return (
    <Parallax strength={drift} className={className}>
      <figure className="w-[188px] rounded-2xl bg-white p-2.5 shadow-[0_24px_50px_-24px_rgba(11,10,15,.5)] ring-1 ring-black/5 lg:w-[210px]">
        <img
          src={asset(photo)}
          alt={alt}
          loading="lazy"
          decoding="async"
          className="aspect-[4/3] w-full rounded-xl object-cover"
        />
        <figcaption className="px-1 pb-0.5 pt-2.5">
          <p className="k-display text-lg text-zinc-900">{kcal}</p>
          <div className="mt-1.5 flex flex-wrap gap-x-2.5 gap-y-1">
            {chips.map((chip) => (
              <span key={chip.label} className="flex items-center gap-1 text-[11px] text-zinc-600">
                <span className="size-1.5 rounded-full" style={{ background: chip.color }} />
                <span className="font-semibold text-zinc-800">{chip.value}</span>
                {chip.label}
              </span>
            ))}
          </div>
        </figcaption>
      </figure>
    </Parallax>
  );
}
