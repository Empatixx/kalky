import { Reveal } from './reveal';

export type Feature = { name: string; body: string; color: string };

export function Features({ title, features }: { title: string; features: Feature[] }) {
  return (
    <div className="mx-auto max-w-5xl">
      <Reveal>
        <h2 className="k-display text-balance text-3xl sm:text-4xl">{title}</h2>
      </Reveal>

      <div className="mt-12 grid gap-x-12 gap-y-10 sm:grid-cols-2 lg:grid-cols-3">
        {features.map((feature, i) => (
          <Reveal key={feature.name} delay={i * 70}>
            <div className="border-t border-[color-mix(in_srgb,var(--k-muted)_22%,transparent)] pt-5">
              <span
                className="mb-3 block h-2 w-2 rounded-full"
                style={{ background: feature.color }}
                aria-hidden
              />
              <h3 className="text-lg font-semibold">{feature.name}</h3>
              <p className="mt-1.5 text-sm leading-relaxed text-[var(--k-muted)]">{feature.body}</p>
            </div>
          </Reveal>
        ))}
      </div>
    </div>
  );
}
