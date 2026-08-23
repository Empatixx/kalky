'use client';
import { useEffect, useRef, useState, type ComponentType } from 'react';
import { Utensils, Wheat, Leaf } from 'lucide-react';

export type Macro = {
  label: string;
  value: number;
  target: number;
  unit: string;
  color: string;
  icon: 'protein' | 'carbs' | 'fat';
};

// The same three marks the app puts inside its rings.
const ICONS: Record<Macro['icon'], ComponentType<{ className?: string; style?: object }>> = {
  protein: Utensils,
  carbs: Wheat,
  fat: Leaf,
};

const RADIUS = 52;
const CIRCUMFERENCE = 2 * Math.PI * RADIUS;

function Ring({ macro, shown, index }: { macro: Macro; shown: boolean; index: number }) {
  const ratio = Math.min(macro.value / macro.target, 1);
  const offset = shown ? CIRCUMFERENCE * (1 - ratio) : CIRCUMFERENCE;

  const Icon = ICONS[macro.icon];

  return (
    <div className="flex flex-col items-center gap-3">
      <div className="relative">
        <svg viewBox="0 0 128 128" className="h-28 w-28 sm:h-32 sm:w-32" role="img" aria-label={`${macro.label} ${macro.value} ${macro.unit} of ${macro.target}`}>
        <circle
          cx="64"
          cy="64"
          r={RADIUS}
          fill="none"
          strokeWidth="11"
          stroke="color-mix(in srgb, var(--k-muted) 18%, transparent)"
        />
        <circle
          cx="64"
          cy="64"
          r={RADIUS}
          fill="none"
          strokeWidth="11"
          strokeLinecap="round"
          stroke={macro.color}
          strokeDasharray={CIRCUMFERENCE}
          strokeDashoffset={offset}
          transform="rotate(-90 64 64)"
          style={{
            transition: 'stroke-dashoffset 1.4s cubic-bezier(0.16, 1, 0.3, 1)',
            transitionDelay: `${index * 140}ms`,
          }}
        />
        </svg>
        <span
          className="absolute left-1/2 top-1/2 flex size-11 -translate-x-1/2 -translate-y-1/2 items-center justify-center rounded-full sm:size-12"
          style={{ background: `color-mix(in srgb, ${macro.color} 14%, transparent)` }}
          aria-hidden
        >
          <Icon className="size-5" style={{ color: macro.color }} />
        </span>
      </div>
      <div className="text-center">
        <p className="k-display text-2xl">
          {macro.value}
          <span className="tracking-normal text-[var(--k-muted)]">{macro.unit}</span>
        </p>
        <p className="mt-0.5 text-sm text-[var(--k-muted)]">
          {macro.label} · {macro.target}
          {macro.unit}
        </p>
      </div>
    </div>
  );
}

export function MacroRings({
  macros,
  kcal,
  kcalTarget,
  caption,
}: {
  macros: Macro[];
  kcal: number;
  kcalTarget: number;
  caption: string;
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
      { threshold: 0.45 },
    );

    observer.observe(el);
    return () => observer.disconnect();
  }, []);

  return (
    <div ref={ref} className="flex flex-col items-center">
      <p className="k-display text-6xl sm:text-7xl md:text-8xl">
        {kcal}
        <span className="ml-3 align-middle text-2xl font-semibold tracking-normal text-[var(--k-muted)] sm:text-3xl">
          / {kcalTarget} kcal
        </span>
      </p>
      <p className="mt-4 max-w-md text-center text-base text-[var(--k-muted)] sm:text-lg">{caption}</p>
      <div className="mt-12 flex flex-wrap justify-center gap-8 sm:gap-14">
        {macros.map((macro, i) => (
          <Ring key={macro.label} macro={macro} shown={shown} index={i} />
        ))}
      </div>
    </div>
  );
}
