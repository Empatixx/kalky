import Link from 'next/link';
import { i18n } from '@/lib/i18n';
import { Phone } from '@/components/landing/phone';
import { Reveal } from '@/components/landing/reveal';
import { Showcase, type Beat } from '@/components/landing/showcase';
import { MacroRings, type Macro } from '@/components/landing/macro-rings';

export function generateStaticParams() {
  return i18n.languages.map((lang) => ({ lang }));
}

type Copy = {
  wordmark: string;
  headline: string;
  sub: string;
  scroll: string;
  beats: Beat[];
  ringsCaption: string;
  macros: Macro[];
  closing: string;
  cta: string;
};

const COPY: Record<'en' | 'cs', Copy> = {
  en: {
    wordmark: 'Kalky',
    headline: 'Photograph. Done.',
    sub: 'A food diary that reads your plate, so you stop typing one out.',
    scroll: 'Scroll',
    beats: [
      {
        src: '/img/en/add-food.png',
        alt: 'Add food screen listing recently eaten meals',
        title: 'A meal in one tap',
        body: 'What you eat often is already waiting. Pick it, and the day recalculates.',
      },
      {
        src: '/img/en/detail.png',
        alt: 'Food detail screen showing calories and macronutrients',
        title: 'Fix what is off',
        body: 'A photo estimate is a starting point, not a verdict. Correct the numbers and move on.',
      },
      {
        src: '/img/en/analytics.png',
        alt: 'Analytics screen with a weight chart and average intake',
        title: 'Watch the trend',
        body: 'Weeks of averages beside your weight — the measurement that actually answers the question.',
      },
    ],
    ringsCaption: 'One ordinary Sunday, the way the app recorded it.',
    macros: [
      { label: 'Protein', value: 38, target: 187, unit: 'g', color: 'var(--k-protein)' },
      { label: 'Carbs', value: 100, target: 249, unit: 'g', color: 'var(--k-carbs)' },
      { label: 'Fat', value: 34, target: 83, unit: 'g', color: 'var(--k-fat)' },
    ],
    closing: 'Open source. Bring your own backend.',
    cta: 'Read the guide',
  },
  cs: {
    wordmark: 'Kalky',
    headline: 'Vyfoť. Hotovo.',
    sub: 'Deník jídla, který si přečte tvůj talíř, abys ho nemusel vypisovat.',
    scroll: 'Posuň dolů',
    beats: [
      {
        src: '/img/cs/add-food.png',
        alt: 'Obrazovka přidání jídla s naposledy použitými položkami',
        title: 'Jídlo na jedno klepnutí',
        body: 'Co jíš často, na tebe už čeká. Vybereš a den se přepočítá.',
      },
      {
        src: '/img/cs/detail.png',
        alt: 'Detail jídla s kaloriemi a makroživinami',
        title: 'Oprav, co nesedí',
        body: 'Odhad z fotky je výchozí bod, ne rozsudek. Přepíšeš čísla a jdeš dál.',
      },
      {
        src: '/img/cs/analytics.png',
        alt: 'Obrazovka analýzy s grafem váhy a průměrným příjmem',
        title: 'Sleduj trend',
        body: 'Týdny průměrů vedle tvé váhy — a právě váha na tu otázku odpovídá.',
      },
    ],
    ringsCaption: 'Jedna obyčejná neděle, jak si ji appka zapsala.',
    macros: [
      { label: 'Bílkoviny', value: 38, target: 187, unit: 'g', color: 'var(--k-protein)' },
      { label: 'Sacharidy', value: 100, target: 249, unit: 'g', color: 'var(--k-carbs)' },
      { label: 'Tuky', value: 34, target: 83, unit: 'g', color: 'var(--k-fat)' },
    ],
    closing: 'Open source. Backend si přineseš vlastní.',
    cta: 'Přečíst průvodce',
  },
};

export default async function HomePage({ params }: PageProps<'/[lang]'>) {
  const { lang } = await params;
  const t = COPY[lang as 'en' | 'cs'] ?? COPY.en;
  const heroShot = lang === 'cs' ? '/img/cs/home.png' : '/img/en/home.png';

  return (
    <main className="k-landing">
      {/* Hero ---------------------------------------------------------- */}
      <section className="relative overflow-hidden px-6 pt-24 pb-32 sm:pt-32">
        <div
          className="k-bloom"
          style={{
            width: '46rem',
            height: '46rem',
            top: '-16rem',
            right: '-14rem',
            background:
              'radial-gradient(circle, color-mix(in srgb, var(--k-violet) 26%, transparent), color-mix(in srgb, var(--k-pink) 16%, transparent) 55%, transparent 72%)',
          }}
        />
        <div
          className="k-bloom"
          style={{
            width: '34rem',
            height: '34rem',
            top: '-8rem',
            left: '-12rem',
            background:
              'radial-gradient(circle, color-mix(in srgb, var(--k-sky) 22%, transparent), transparent 70%)',
          }}
        />

        <div className="relative mx-auto max-w-4xl text-center">
          <Reveal>
            <p className="k-eyebrow">{t.wordmark}</p>
            <h1 className="k-display mt-5 text-balance text-[clamp(2.75rem,10vw,6.5rem)]">{t.headline}</h1>
            <p className="mx-auto mt-6 max-w-md text-lg text-[var(--k-muted)] sm:text-xl">{t.sub}</p>
          </Reveal>

          <Reveal delay={160}>
            <div className="mx-auto mt-16 w-[210px] sm:w-[250px]">
              <Phone src={heroShot} alt={t.wordmark} priority />
            </div>
          </Reveal>

          <Reveal delay={320}>
            <p className="k-eyebrow mt-14">{t.scroll}</p>
          </Reveal>
        </div>
      </section>

      {/* Scroll sequence ----------------------------------------------- */}
      <Showcase beats={t.beats} />

      {/* Signature: the day, drawn ------------------------------------- */}
      <section className="relative overflow-hidden px-6 py-32">
        <div
          className="k-bloom"
          style={{
            width: '40rem',
            height: '40rem',
            bottom: '-18rem',
            left: '50%',
            transform: 'translateX(-50%)',
            background:
              'radial-gradient(circle, color-mix(in srgb, var(--k-violet) 18%, transparent), transparent 70%)',
          }}
        />
        <div className="relative mx-auto max-w-3xl">
          <MacroRings
            macros={t.macros}
            kcal={880}
            kcalTarget={2494}
            caption={t.ringsCaption}
          />
        </div>
      </section>

      {/* Close ---------------------------------------------------------- */}
      <section className="px-6 pt-8 pb-40">
        <Reveal className="mx-auto max-w-2xl text-center">
          <p className="k-display text-balance text-3xl sm:text-4xl">{t.closing}</p>
          <Link
            href={`/${lang}/docs`}
            className="mt-8 inline-flex items-center rounded-full bg-[var(--k-ink)] px-7 py-3 font-semibold text-[var(--k-surface)] transition-transform hover:scale-[1.03]"
          >
            {t.cta}
          </Link>
        </Reveal>
      </section>
    </main>
  );
}
