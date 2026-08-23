'use client';
import { useEffect, useRef, useState } from 'react';
import { Phone } from './phone';
import { Dots } from './dots';

export type Beat = {
  src: string;
  alt: string;
  title: string;
  body: string;
};

export function Showcase({ beats }: { beats: Beat[] }) {
  const sectionRef = useRef<HTMLElement>(null);
  const [active, setActive] = useState(0);

  useEffect(() => {
    const section = sectionRef.current;
    if (!section) return;

    let frame = 0;

    const update = () => {
      frame = 0;
      const { top, height } = section.getBoundingClientRect();
      const scrollable = height - window.innerHeight;
      if (scrollable <= 0) return;

      // 0 at the moment the section pins, 1 when it releases.
      const progress = Math.min(Math.max(-top / scrollable, 0), 1);
      const index = Math.min(Math.floor(progress * beats.length), beats.length - 1);
      setActive(index);
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
  }, [beats.length]);

  return (
    <section ref={sectionRef} style={{ height: `${beats.length * 100}vh` }} className="relative">
      <div className="sticky top-0 flex h-screen items-center overflow-hidden">
        <div className="mx-auto grid w-full max-w-5xl items-center gap-10 px-6 md:grid-cols-2 md:gap-16">
          {/* Phone: one stack, crossfaded. */}
          <div className="relative mx-auto w-[190px] sm:w-[230px] md:w-[270px]">
            {beats.map((beat, i) => (
              <div
                key={beat.src}
                aria-hidden={i !== active}
                className="transition-[opacity,transform] duration-700 ease-[cubic-bezier(0.16,1,0.3,1)]"
                style={{
                  gridArea: '1 / 1',
                  position: i === 0 ? 'relative' : 'absolute',
                  inset: i === 0 ? undefined : 0,
                  opacity: i === active ? 1 : 0,
                  transform: i === active ? 'none' : 'scale(0.96) translateY(12px)',
                }}
              >
                <Phone src={beat.src} alt={beat.alt} priority={i === 0} />
              </div>
            ))}
          </div>

          {/* Caption: same crossfade, driven by the same index. */}
          <div className="relative min-h-[9rem]">
            {beats.map((beat, i) => (
              <div
                key={beat.title}
                aria-hidden={i !== active}
                className="transition-[opacity,transform] duration-700 ease-[cubic-bezier(0.16,1,0.3,1)]"
                style={{
                  position: i === 0 ? 'relative' : 'absolute',
                  inset: i === 0 ? undefined : 0,
                  opacity: i === active ? 1 : 0,
                  transform: i === active ? 'none' : 'translateY(16px)',
                }}
              >
                <p className="k-eyebrow mb-3">
                  {String(i + 1).padStart(2, '0')} / {String(beats.length).padStart(2, '0')}
                </p>
                <h2 className="k-display text-3xl sm:text-4xl md:text-5xl">{beat.title}</h2>
                <p className="mt-4 max-w-sm text-base leading-relaxed text-[var(--k-muted)] sm:text-lg">
                  {beat.body}
                </p>
              </div>
            ))}

            <Dots count={beats.length} active={active} className="mt-10" />
          </div>
        </div>
      </div>
    </section>
  );
}
