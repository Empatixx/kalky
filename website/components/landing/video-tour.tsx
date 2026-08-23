'use client';
import { useEffect, useRef, useState } from 'react';
import { asset } from '@/lib/asset';
import { Dots } from './dots';

export type Clip = { src: string; poster: string; title: string; body: string; alt: string };

export function VideoTour({ clips, eyebrow, title }: { clips: Clip[]; eyebrow: string; title: string }) {
  const ref = useRef<HTMLDivElement>(null);
  const videos = useRef<(HTMLVideoElement | null)[]>([]);
  const [active, setActive] = useState(0);
  const [visible, setVisible] = useState(false);

  useEffect(() => {
    const el = ref.current;
    if (!el) return;
    const observer = new IntersectionObserver(([e]) => setVisible(e.isIntersecting), { threshold: 0.3 });
    observer.observe(el);
    return () => observer.disconnect();
  }, []);

  // Only the visible, active clip plays; the rest stay parked at their first frame.
  useEffect(() => {
    videos.current.forEach((video, i) => {
      if (!video) return;
      if (i === active && visible) {
        video.currentTime = 0;
        void video.play().catch(() => {});
      } else {
        video.pause();
      }
    });
  }, [active, visible]);

  const next = () => setActive((i) => (i + 1) % clips.length);

  return (
    <div ref={ref} className="mx-auto max-w-5xl">
      <p className="k-eyebrow text-center">{eyebrow}</p>
      <h2 className="k-display mt-4 text-balance text-center text-3xl sm:text-4xl md:text-5xl">{title}</h2>

      <div
        className="relative mt-12 overflow-hidden rounded-3xl px-6 py-14 sm:py-16"
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
        <div className="mx-auto grid max-w-3xl items-center justify-center gap-10 md:grid-cols-[auto_minmax(0,24rem)] md:gap-14">
          {/* Phone */}
          <div className="relative mx-auto aspect-[540/1200] w-[210px] shrink-0 overflow-hidden rounded-[2rem] bg-black p-[3px] shadow-[0_30px_70px_-25px_rgba(11,10,15,.55)] sm:w-[240px]">
            {clips.map((clip, i) => (
              <video
                key={clip.src}
                ref={(el) => {
                  videos.current[i] = el;
                }}
                src={asset(clip.src)}
                poster={asset(clip.poster)}
                muted
                playsInline
                preload={i === 0 ? 'auto' : 'metadata'}
                aria-label={clip.alt}
                onEnded={next}
                className="absolute inset-[3px] h-[calc(100%-6px)] w-[calc(100%-6px)] rounded-[1.8rem] object-cover transition-opacity duration-500"
                style={{ opacity: i === active ? 1 : 0 }}
              />
            ))}
          </div>

          {/* Caption */}
          <div className="relative min-h-[8.5rem] text-center md:text-left">
            {clips.map((clip, i) => (
              <div
                key={clip.title}
                aria-hidden={i !== active}
                className="transition-[opacity,transform] duration-500 ease-[cubic-bezier(.16,1,.3,1)]"
                style={{
                  position: i === 0 ? 'relative' : 'absolute',
                  inset: i === 0 ? undefined : 0,
                  opacity: i === active ? 1 : 0,
                  transform: i === active ? 'none' : 'translateY(10px)',
                }}
              >
                <h3 className="k-display text-2xl text-zinc-900 sm:text-3xl">{clip.title}</h3>
                <p className="mx-auto mt-3 max-w-sm text-[15px] leading-relaxed text-zinc-700 md:mx-0 sm:text-base">
                  {clip.body}
                </p>
              </div>
            ))}

            <div className="mt-8 flex justify-center md:justify-start">
              {clips.map((clip, i) => (
                <button
                  key={clip.src}
                  type="button"
                  onClick={() => setActive(i)}
                  aria-label={clip.title}
                  aria-current={i === active}
                  className="px-1 py-2 outline-none focus-visible:ring-2 focus-visible:ring-white/80 rounded-full"
                >
                  <span
                    className="block h-1.5 rounded-full transition-all duration-300 ease-[cubic-bezier(.4,0,.2,1)]"
                    style={{
                      width: i === active ? 20 : 6,
                      background: i === active ? '#18181b' : 'rgba(24,24,27,.28)',
                    }}
                  />
                </button>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
