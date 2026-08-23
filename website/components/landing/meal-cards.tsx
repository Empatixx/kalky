'use client';
import { useEffect, useRef, useState } from 'react';
import { Flame, Drumstick, Wheat, Leaf } from 'lucide-react';
import { Dots } from './dots';

export type Meal = {
  name: string;
  kcal: number;
  protein: number;
  carbs: number;
  fat: number;
};

const MACROS = [
  { key: 'protein', Icon: Drumstick, color: 'var(--k-protein)' },
  { key: 'carbs', Icon: Wheat, color: 'var(--k-carbs)' },
  { key: 'fat', Icon: Leaf, color: 'var(--k-fat)' },
] as const;

function MealCard({ meal, focused }: { meal: Meal; focused: boolean }) {
  return (
    <div
      className="w-[min(19rem,82vw)] rounded-2xl bg-white p-5 ring-1 ring-black/5 transition-[transform,box-shadow,opacity] duration-500 ease-[cubic-bezier(.16,1,.3,1)]"
      style={{
        transform: focused ? 'scale(1)' : 'scale(0.955)',
        opacity: focused ? 1 : 0.72,
        boxShadow: focused
          ? '0 26px 55px -22px rgba(11,10,15,.45)'
          : '0 14px 30px -20px rgba(11,10,15,.35)',
      }}
    >
      <div className="flex items-start justify-between gap-3">
        <p className="truncate text-[15px] font-semibold text-zinc-900">{meal.name}</p>
        <span className="flex shrink-0 items-center gap-1.5 rounded-lg bg-zinc-900 px-2.5 py-1">
          <Flame className="size-3.5 text-white" strokeWidth={2.4} />
          <span className="text-[12px] font-bold text-white">{meal.kcal}</span>
        </span>
      </div>

      <div className="mt-4 flex items-center gap-2">
        {MACROS.map(({ key, Icon, color }) => (
          <div key={key} className="flex flex-1 items-center gap-1.5 rounded-xl bg-zinc-50 px-2.5 py-2">
            <Icon className="size-3.5 shrink-0" style={{ color }} strokeWidth={2.4} />
            <span className="text-[12px] font-semibold text-zinc-800">{meal[key]} g</span>
          </div>
        ))}
      </div>

      {/* Macro split, drawn to scale. */}
      <div className="mt-3 flex h-1.5 overflow-hidden rounded-full bg-zinc-100">
        {MACROS.map(({ key, color }) => {
          const total = meal.protein + meal.carbs + meal.fat;
          return (
            <span
              key={key}
              className="h-full transition-[width] duration-700 ease-[cubic-bezier(.16,1,.3,1)]"
              style={{ width: focused ? `${(meal[key] / total) * 100}%` : '0%', background: color }}
            />
          );
        })}
      </div>
    </div>
  );
}

export function MealCards({ meals, eyebrow, title }: { meals: Meal[]; eyebrow: string; title: string }) {
  const ref = useRef<HTMLDivElement>(null);
  const [active, setActive] = useState(0);
  const [live, setLive] = useState(false);

  useEffect(() => {
    const el = ref.current;
    if (!el) return;
    const observer = new IntersectionObserver(([e]) => setLive(e.isIntersecting), { threshold: 0.4 });
    observer.observe(el);
    return () => observer.disconnect();
  }, []);

  useEffect(() => {
    if (!live) return;
    if (window.matchMedia('(prefers-reduced-motion: reduce)').matches) return;
    const id = window.setInterval(() => setActive((i) => (i + 1) % meals.length), 2600);
    return () => window.clearInterval(id);
  }, [live, meals.length]);

  return (
    <div ref={ref} className="mx-auto max-w-5xl">
      <p className="k-eyebrow text-center">{eyebrow}</p>
      <h2 className="k-display mt-4 text-balance text-center text-3xl sm:text-4xl md:text-5xl">{title}</h2>

      <div
        className="relative mt-12 overflow-hidden rounded-3xl px-6 py-14 sm:py-20"
        style={{
          backgroundImage: [
            'radial-gradient(at 15% 15%, color-mix(in srgb, var(--k-sky) 55%, transparent) 0%, transparent 60%)',
            'radial-gradient(at 85% 10%, color-mix(in srgb, var(--k-violet) 50%, transparent) 0%, transparent 55%)',
            'radial-gradient(at 75% 85%, color-mix(in srgb, var(--k-pink) 50%, transparent) 0%, transparent 60%)',
            'radial-gradient(at 20% 80%, color-mix(in srgb, var(--k-violet) 40%, transparent) 0%, transparent 55%)',
            'linear-gradient(160deg, #cfe6ff 0%, #f3d9ff 55%, #ffd9f2 100%)',
          ].join(', '),
        }}
      >
        <div className="flex flex-col items-center gap-4 sm:gap-5">
          {meals.map((meal, i) => (
            <MealCard key={meal.name} meal={meal} focused={i === active} />
          ))}
        </div>

        <Dots count={meals.length} active={active} tone="light" className="mt-10 justify-center" />
      </div>
    </div>
  );
}
