/**
 * iOS page control: small dots, the active one stretched into a pill.
 */
export function Dots({
  count,
  active,
  tone = 'dark',
  className = '',
}: {
  count: number;
  active: number;
  tone?: 'dark' | 'light';
  className?: string;
}) {
  return (
    <div className={`flex items-center gap-1.5 ${className}`} role="presentation">
      {Array.from({ length: count }, (_, i) => {
        const on = i === active;
        return (
          <span
            key={i}
            className="block h-1.5 rounded-full transition-all duration-300 ease-[cubic-bezier(.4,0,.2,1)]"
            style={{
              width: on ? 20 : 6,
              background: on
                ? tone === 'light'
                  ? '#fff'
                  : 'var(--k-ink)'
                : tone === 'light'
                  ? 'rgba(255,255,255,.5)'
                  : 'color-mix(in srgb, var(--k-ink) 22%, transparent)',
            }}
          />
        );
      })}
    </div>
  );
}
