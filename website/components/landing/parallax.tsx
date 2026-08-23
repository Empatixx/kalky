'use client';
import { useEffect, useRef, type ReactNode } from 'react';

/** Translates its child against the scroll direction while it is on screen. */
export function Parallax({
  children,
  strength = 60,
  className = '',
}: {
  children: ReactNode;
  strength?: number;
  className?: string;
}) {
  const ref = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const el = ref.current;
    if (!el) return;
    if (window.matchMedia('(prefers-reduced-motion: reduce)').matches) return;

    let frame = 0;
    const update = () => {
      frame = 0;
      const rect = el.getBoundingClientRect();
      const viewport = window.innerHeight;
      if (rect.bottom < 0 || rect.top > viewport) return;
      // -1 entering from the bottom, 1 leaving through the top.
      const progress = (viewport / 2 - (rect.top + rect.height / 2)) / (viewport / 2 + rect.height / 2);
      el.style.setProperty('--k-parallax', `${progress * strength}px`);
    };

    const onScroll = () => {
      if (frame) return;
      frame = window.requestAnimationFrame(update);
    };

    update();
    window.addEventListener('scroll', onScroll, { passive: true });
    window.addEventListener('resize', onScroll);
    return () => {
      window.removeEventListener('scroll', onScroll);
      window.removeEventListener('resize', onScroll);
      if (frame) window.cancelAnimationFrame(frame);
    };
  }, [strength]);

  return (
    <div ref={ref} className={className} style={{ transform: 'translate3d(0, var(--k-parallax, 0px), 0)' }}>
      {children}
    </div>
  );
}
