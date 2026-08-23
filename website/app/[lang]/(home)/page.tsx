import Link from 'next/link';
import { i18n } from '@/lib/i18n';
import { Phone } from '@/components/landing/phone';
import { Reveal } from '@/components/landing/reveal';
import { Parallax } from '@/components/landing/parallax';
import { MacroRings, type Macro } from '@/components/landing/macro-rings';
import { PhotoProof, type Chip } from '@/components/landing/photo-proof';
import { FoodSatellite, type SatelliteChip } from '@/components/landing/food-satellite';
import { VideoTour, type Clip } from '@/components/landing/video-tour';
import { ScrollSpy } from '@/components/landing/scroll-spy';

export function generateStaticParams() {
  return i18n.languages.map((lang) => ({ lang }));
}

type Copy = {
  wordmark: string;
  headline: string;
  sub: string;
  scroll: string;
  sections: { id: string; label: string }[];
  tourEyebrow: string;
  tourTitle: string;
  clips: Clip[];
  proof: { title: string; body: string; kcal: string; alt: string; chips: Chip[] };
  expandLabel: string;
  closeLabel: string;
  satellites: { photo: string; alt: string; kcal: string; chips: SatelliteChip[] }[];
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
    sections: [
      { id: 'hero', label: 'Start' },
      { id: 'tour', label: 'In use' },
      { id: 'proof', label: 'From a photo' },
      { id: 'day', label: 'A day' },
    ],
    tourEyebrow: 'The app itself',
    tourTitle: 'Watch it happen',
    clips: [
      {
        src: '/video/add.mp4',
        poster: '/video/add.jpg',
        alt: 'Screen recording of adding two meals, after which the daily total updates',
        title: 'A meal in one tap',
        body: 'What you eat often is already waiting. Pick it, and the day recalculates.',
      },
      {
        src: '/video/detail.mp4',
        poster: '/video/detail.jpg',
        alt: 'Screen recording of a food entry opening to show calories and macronutrients',
        title: 'Fix what is off',
        body: 'A photo estimate is a starting point, not a verdict. Correct the numbers and move on.',
      },
      {
        src: '/video/analytics.mp4',
        poster: '/video/analytics.jpg',
        alt: 'Screen recording of the analytics screen with weight and intake charts',
        title: 'Watch the trend',
        body: 'Weeks of averages beside your weight — the measurement that actually answers the question.',
      },
    ],
    proof: {
      title: 'A plate, then the numbers',
      body: 'Photograph what is in front of you. A vision model works out what is on the plate and roughly what it holds.',
      kcal: '480 kcal',
      alt: 'Overhead photograph of a salad with a fried egg, a rice cake and a coffee',
      chips: [
        { value: '24 g', label: 'Protein', color: 'var(--k-protein)' },
        { value: '38 g', label: 'Carbs', color: 'var(--k-carbs)' },
        { value: '26 g', label: 'Fat', color: 'var(--k-fat)' },
      ],
    },
    expandLabel: 'Play this recording larger',
    closeLabel: 'Close',
    satellites: [
      {
        photo: '/img/food/bowl-oats.jpg',
        alt: 'Oat bowl with kiwi, blueberries and almonds',
        kcal: '350 kcal',
        chips: [
          { value: '12 g', label: 'protein', color: 'var(--k-protein)' },
          { value: '55 g', label: 'carbs', color: 'var(--k-carbs)' },
          { value: '8 g', label: 'fat', color: 'var(--k-fat)' },
        ],
      },
      {
        photo: '/img/food/bowl-poke.jpg',
        alt: 'Poke bowl with mango, avocado, carrot and noodles',
        kcal: '490 kcal',
        chips: [
          { value: '30 g', label: 'protein', color: 'var(--k-protein)' },
          { value: '52 g', label: 'carbs', color: 'var(--k-carbs)' },
          { value: '16 g', label: 'fat', color: 'var(--k-fat)' },
        ],
      },
    ],
    ringsCaption: 'One ordinary Sunday, the way the app recorded it.',
    macros: [
      { icon: 'protein', label: 'Protein', value: 76, target: 150, unit: 'g', color: 'var(--k-protein)' },
      { icon: 'carbs', label: 'Carbs', value: 160, target: 250, unit: 'g', color: 'var(--k-carbs)' },
      { icon: 'fat', label: 'Fat', value: 68, target: 70, unit: 'g', color: 'var(--k-fat)' },
    ],
    closing: 'Open source. Bring your own backend.',
    cta: 'Read the guide',
  },
  cs: {
    wordmark: 'Kalky',
    headline: 'Vyfoť. Hotovo.',
    sub: 'Deník jídla, který si přečte tvůj talíř, abys ho nemusel vypisovat.',
    scroll: 'Posuň dolů',
    sections: [
      { id: 'hero', label: 'Začátek' },
      { id: 'tour', label: 'V provozu' },
      { id: 'proof', label: 'Z fotky' },
      { id: 'day', label: 'Den' },
    ],
    tourEyebrow: 'Přímo z aplikace',
    tourTitle: 'Podívej se, jak to jde',
    clips: [
      {
        src: '/video/add.mp4',
        poster: '/video/add.jpg',
        alt: 'Záznam obrazovky s přidáním dvou jídel, po kterém se přepočítá denní součet',
        title: 'Jídlo na jedno klepnutí',
        body: 'Co jíš často, na tebe už čeká. Vybereš a den se přepočítá.',
      },
      {
        src: '/video/detail.mp4',
        poster: '/video/detail.jpg',
        alt: 'Záznam obrazovky s otevřením detailu jídla a jeho výživovými hodnotami',
        title: 'Oprav, co nesedí',
        body: 'Odhad z fotky je výchozí bod, ne rozsudek. Přepíšeš čísla a jdeš dál.',
      },
      {
        src: '/video/analytics.mp4',
        poster: '/video/analytics.jpg',
        alt: 'Záznam obrazovky analýzy s grafy váhy a příjmu',
        title: 'Sleduj trend',
        body: 'Týdny průměrů vedle tvé váhy — a právě váha na tu otázku odpovídá.',
      },
    ],
    proof: {
      title: 'Z talíře rovnou čísla',
      body: 'Vyfoť, co máš před sebou. Model odhadne, co na talíři je a kolik toho v sobě zhruba má.',
      kcal: '480 kcal',
      alt: 'Fotka salátu se sázeným vejcem, rýžovým chlebíčkem a kávou shora',
      chips: [
        { value: '24 g', label: 'Bílkoviny', color: 'var(--k-protein)' },
        { value: '38 g', label: 'Sacharidy', color: 'var(--k-carbs)' },
        { value: '26 g', label: 'Tuky', color: 'var(--k-fat)' },
      ],
    },
    expandLabel: 'Přehrát záznam ve větším',
    closeLabel: 'Zavřít',
    satellites: [
      {
        photo: '/img/food/bowl-oats.jpg',
        alt: 'Ovesná miska s kiwi, borůvkami a mandlemi',
        kcal: '350 kcal',
        chips: [
          { value: '12 g', label: 'bílkoviny', color: 'var(--k-protein)' },
          { value: '55 g', label: 'sacharidy', color: 'var(--k-carbs)' },
          { value: '8 g', label: 'tuky', color: 'var(--k-fat)' },
        ],
      },
      {
        photo: '/img/food/bowl-poke.jpg',
        alt: 'Poke bowl s mangem, avokádem, mrkví a nudlemi',
        kcal: '490 kcal',
        chips: [
          { value: '30 g', label: 'bílkoviny', color: 'var(--k-protein)' },
          { value: '52 g', label: 'sacharidy', color: 'var(--k-carbs)' },
          { value: '16 g', label: 'tuky', color: 'var(--k-fat)' },
        ],
      },
    ],
    ringsCaption: 'Jedna obyčejná neděle, jak si ji appka zapsala.',
    macros: [
      { icon: 'protein', label: 'Bílkoviny', value: 76, target: 150, unit: 'g', color: 'var(--k-protein)' },
      { icon: 'carbs', label: 'Sacharidy', value: 160, target: 250, unit: 'g', color: 'var(--k-carbs)' },
      { icon: 'fat', label: 'Tuky', value: 68, target: 70, unit: 'g', color: 'var(--k-fat)' },
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
      <ScrollSpy sections={t.sections} />

      {/* Hero ---------------------------------------------------------- */}
      <section id="hero" className="relative overflow-hidden px-6 pt-24 pb-32 sm:pt-32">
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
            <div className="relative mx-auto mt-16 flex w-full max-w-3xl items-center justify-center">
              <FoodSatellite
                {...t.satellites[0]}
                drift={44}
                className="absolute left-0 top-6 hidden lg:block"
              />

              <Parallax strength={26} className="w-[210px] sm:w-[250px]">
                <Phone src={heroShot} alt={t.wordmark} priority />
              </Parallax>

              <FoodSatellite
                {...t.satellites[1]}
                drift={-30}
                className="absolute right-0 top-40 hidden lg:block"
              />
            </div>

            {/* Below the phone on narrow screens, where there is no room beside it. */}
            <div className="mt-10 flex justify-center gap-4 lg:hidden">
              {t.satellites.map((satellite) => (
                <FoodSatellite key={satellite.photo} {...satellite} drift={0} className="w-[46%] max-w-[188px]" />
              ))}
            </div>
          </Reveal>

          <Reveal delay={320}>
            <p className="k-eyebrow mt-14">{t.scroll}</p>
          </Reveal>
        </div>
      </section>

      {/* The app, recorded --------------------------------------------- */}
      <section id="tour" className="px-6 pb-24 sm:pb-32">
        <VideoTour
          clips={t.clips}
          eyebrow={t.tourEyebrow}
          title={t.tourTitle}
          expandLabel={t.expandLabel}
          closeLabel={t.closeLabel}
        />
      </section>

      {/* Photograph becomes nutrition ---------------------------------- */}
      <section id="proof" className="px-6 py-24 sm:py-32">
        <PhotoProof
          photo="/img/food/plate.jpg"
          alt={t.proof.alt}
          kcal={t.proof.kcal}
          chips={t.proof.chips}
          title={t.proof.title}
          body={t.proof.body}
        />
      </section>

      {/* Signature: the day, drawn ------------------------------------- */}
      <section id="day" className="relative overflow-hidden px-6 py-32">
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
          <MacroRings macros={t.macros} kcal={1600} kcalTarget={2230} caption={t.ringsCaption} />
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
