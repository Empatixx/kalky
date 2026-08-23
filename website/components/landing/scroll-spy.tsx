'use client';
import { useEffect, useState } from 'react';

export type Section = { id: string; label: string };

/** Fixed rail of dots that tracks which section is on screen. */
export function ScrollSpy({ sections }: { sections: Section[] }) {
  const [active, setActive] = useState(0);

  useEffect(() => {
    const nodes = sections
      .map((s) => document.getElementById(s.id))
      .filter((n): n is HTMLElement => n !== null);
    if (!nodes.length) return;

    let frame = 0;
    const update = () => {
      frame = 0;
      const line = window.innerHeight * 0.42;
      // The last section whose top has crossed the reading line wins.
      let current = 0;
      nodes.forEach((node, i) => {
        if (node.getBoundingClientRect().top <= line) current = i;
      });
      setActive(current);
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
  }, [sections]);

  return (
    <nav
      aria-label="Sections"
      className="pointer-events-none fixed right-5 top-1/2 z-30 hidden -translate-y-1/2 lg:block"
    >
      <ul className="flex flex-col items-end gap-3">
        {sections.map((section, i) => (
          <li key={section.id}>
            <a
              href={`#${section.id}`}
              className="pointer-events-auto group flex items-center justify-end gap-2.5 rounded-full outline-none focus-visible:ring-2 focus-visible:ring-[var(--k-violet)]"
            >
              <span
                className="rounded-full bg-[var(--k-surface)]/85 px-2 py-0.5 text-[11px] font-medium text-[var(--k-muted)] opacity-0 shadow-sm backdrop-blur transition-opacity duration-200 group-hover:opacity-100 group-focus-visible:opacity-100"
              >
                {section.label}
              </span>
              <span
                aria-current={i === active ? 'true' : undefined}
                className="block h-1.5 rounded-full transition-all duration-300 ease-[cubic-bezier(.4,0,.2,1)]"
                style={{
                  width: i === active ? 18 : 6,
                  background:
                    i === active
                      ? 'var(--k-ink)'
                      : 'color-mix(in srgb, var(--k-ink) 25%, transparent)',
                }}
              />
              <span className="sr-only">{section.label}</span>
            </a>
          </li>
        ))}
      </ul>
    </nav>
  );
}
