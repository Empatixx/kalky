'use client';
import { useEffect, useRef, useState } from 'react';
import { asset } from '@/lib/asset';

export type Chip = { value: string; label: string; color?: string };

export function PhotoProof({
  photo,
  alt,
  kcal,
  chips,
  title,
  body,
}: {
  photo: string;
  alt: string;
  kcal: string;
  chips: Chip[];
  title: string;
  body: string;
}) {
  const ref = useRef<HTMLDivElement>(null);
  const [shown, setShown] = useState(false);

  useEffect(() => {
    const el = ref.current;
    if (!el) return;

    if (window.matchMedia('(prefers-reduced-motion: reduce)').matches) {
      setShown(true);
      return;
    }

    const observer = new IntersectionObserver(
      ([entry]) => {
        if (!entry.isIntersecting) return;
        setShown(true);
        observer.disconnect();
      },
      { threshold: 0.35 },
    );
    observer.observe(el);
    return () => observer.disconnect();
  }, []);

  const chipStyle = (i: number) => ({
    opacity: shown ? 1 : 0,
    transform: shown ? 'none' : 'translateY(14px) scale(0.94)',
    filter: shown ? 'blur(0)' : 'blur(6px)',
    transition: 'opacity .7s cubic-bezier(.16,1,.3,1), transform .7s cubic-bezier(.16,1,.3,1), filter .7s ease',
    transitionDelay: `${260 + i * 130}ms`,
  });

  return (
    <div ref={ref} className="mx-auto grid max-w-5xl items-center gap-12 md:grid-cols-[1fr_0.9fr]">
      <div className="relative mx-auto w-full max-w-sm">
        <img
          src={asset(photo)}
          alt={alt}
          loading="lazy"
          decoding="async"
          className="w-full rounded-[1.75rem] shadow-[0_40px_90px_-30px_rgba(11,10,15,0.5)]"
        />

        {/* The headline number lands first, then the macros. */}
        <div
          className="absolute -right-3 top-8 rounded-2xl bg-white/85 px-5 py-3 shadow-lg backdrop-blur-md sm:-right-6"
          style={{ ...chipStyle(-1), transitionDelay: '120ms' }}
        >
          <p className="k-display text-2xl text-[#0b0a0f] sm:text-3xl">{kcal}</p>
        </div>

        <div className="absolute -left-3 bottom-8 flex flex-col gap-2 sm:-left-6">
          {chips.map((chip, i) => (
            <div
              key={chip.label}
              className="flex items-center gap-2.5 rounded-full bg-white/85 py-2 pl-3 pr-4 shadow-lg backdrop-blur-md"
              style={chipStyle(i)}
            >
              <span className="h-2.5 w-2.5 rounded-full" style={{ background: chip.color }} />
              <span className="text-sm font-semibold text-[#0b0a0f]">{chip.value}</span>
              <span className="text-sm text-[#6b6b76]">{chip.label}</span>
            </div>
          ))}
        </div>
      </div>

      <div>
        <h2 className="k-display text-balance text-3xl sm:text-4xl md:text-5xl">{title}</h2>
        <p className="mt-5 max-w-sm text-base leading-relaxed text-[var(--k-muted)] sm:text-lg">{body}</p>
      </div>
    </div>
  );
}
