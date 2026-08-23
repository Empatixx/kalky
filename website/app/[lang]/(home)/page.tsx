import Link from 'next/link';
import { i18n } from '@/lib/i18n';
import { Phone } from '@/components/landing/phone';
import { Reveal } from '@/components/landing/reveal';
import { Showcase, type Beat } from '@/components/landing/showcase';
import { MacroRings, type Macro } from '@/components/landing/macro-rings';
import { PhotoProof, type Chip } from '@/components/landing/photo-proof';
import { Features, type Feature } from '@/components/landing/features';
import { Band } from '@/components/landing/band';
import { Parallax } from '@/components/landing/parallax';
import { MealCards, type Meal } from '@/components/landing/meal-cards';
import { ScrollSpy } from '@/components/landing/scroll-spy';

export function generateStaticParams() {
  return i18n.languages.map((lang) => ({ lang }));
}

type Copy = {
  wordmark: string;
  headline: string;
  sub: string;
  scroll: string;
  beats: Beat[];
  mealsEyebrow: string;
  mealsTitle: string;
  meals: Meal[];
  sections: { id: string; label: string }[];
  proof: { title: string; body: string; kcal: string; alt: string; chips: Chip[] };
  bandLine: string;
  bandAlt: string;
  featuresTitle: string;
  features: Feature[];
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
    mealsEyebrow: 'Every meal, counted',
    mealsTitle: 'Three meals, already added up',
    meals: [
      { name: 'Avocado toast', kcal: 310, protein: 8, carbs: 28, fat: 18 },
      { name: 'Poke bowl', kcal: 490, protein: 30, carbs: 52, fat: 16 },
      { name: 'Turkey sandwich', kcal: 450, protein: 32, carbs: 38, fat: 16 },
    ],
    sections: [
      { id: 'hero', label: 'Start' },
      { id: 'meals', label: 'Meals' },
      { id: 'proof', label: 'From a photo' },
      { id: 'tour', label: 'Tour' },
      { id: 'features', label: 'Features' },
      { id: 'day', label: 'A day' },
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
    bandLine: 'Breakfast, lunch, dinner. One photo each.',
    bandAlt: 'A table covered with plates of brunch food',
    featuresTitle: 'What Kalky does',
    features: [
      { name: 'Photo analysis', body: 'Point the camera at a meal and get an estimate of calories and macronutrients.', color: 'var(--k-violet)' },
      { name: 'Barcode scanning', body: 'Packaged food is looked up in the product database, with Open Food Facts as a fallback.', color: 'var(--k-sky)' },
      { name: 'Product search', body: 'Type a name and pick from the database when the camera is not to hand.', color: 'var(--k-pink)' },
      { name: 'Your own foods', body: 'Save what you eat regularly and add it again in a single tap.', color: 'var(--k-protein)' },
      { name: 'Daily targets', body: 'Calories and macros worked out from your profile, activity and goal.', color: 'var(--k-carbs)' },
      { name: 'Trends and streaks', body: 'Averages over a period, weight over time, and the days you logged in a row.', color: 'var(--k-fat)' },
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
    mealsEyebrow: 'Každé jídlo sečtené',
    mealsTitle: 'Tři jídla, už spočítaná',
    meals: [
      { name: 'Avokádový toast', kcal: 310, protein: 8, carbs: 28, fat: 18 },
      { name: 'Poke bowl', kcal: 490, protein: 30, carbs: 52, fat: 16 },
      { name: 'Krůtí sendvič', kcal: 450, protein: 32, carbs: 38, fat: 16 },
    ],
    sections: [
      { id: 'hero', label: 'Začátek' },
      { id: 'meals', label: 'Jídla' },
      { id: 'proof', label: 'Z fotky' },
      { id: 'tour', label: 'Průchod' },
      { id: 'features', label: 'Funkce' },
      { id: 'day', label: 'Den' },
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
    bandLine: 'Snídaně, oběd, večeře. Pokaždé jedna fotka.',
    bandAlt: 'Stůl plný talířů s brunchem',
    featuresTitle: 'Co Kalky umí',
    features: [
      { name: 'Analýza z fotky', body: 'Namiř foťák na jídlo a dostaneš odhad kalorií a makroživin.', color: 'var(--k-violet)' },
      { name: 'Čárové kódy', body: 'Balené potraviny se dohledají v databázi, záložně přes Open Food Facts.', color: 'var(--k-sky)' },
      { name: 'Vyhledávání', body: 'Napiš název a vyber z databáze, když zrovna nemáš foťák po ruce.', color: 'var(--k-pink)' },
      { name: 'Vlastní jídla', body: 'Ulož si, co jíš pravidelně, a přidávej to na jedno klepnutí.', color: 'var(--k-protein)' },
      { name: 'Denní cíle', body: 'Kalorie a makra spočítané z tvého profilu, aktivity a cíle.', color: 'var(--k-carbs)' },
      { name: 'Trendy a série', body: 'Průměry za období, vývoj váhy a počet dní v řadě.', color: 'var(--k-fat)' },
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
      <ScrollSpy sections={t.sections} />

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
            <Parallax strength={26} className="mx-auto mt-16 w-[210px] sm:w-[250px]">
              <Phone src={heroShot} alt={t.wordmark} priority />
            </Parallax>
          </Reveal>

          <Reveal delay={320}>
            <p className="k-eyebrow mt-14">{t.scroll}</p>
          </Reveal>
        </div>
      </section>

      {/* Three meals, counted ------------------------------------------ */}
      <section id="meals" className="px-6 pb-24 sm:pb-32">
        <MealCards meals={t.meals} eyebrow={t.mealsEyebrow} title={t.mealsTitle} />
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

      {/* Scroll sequence ----------------------------------------------- */}
      <div id="tour">
        <Showcase beats={t.beats} />
      </div>

      {/* Full-bleed band ------------------------------------------------ */}
      <Band photo="/img/food/table.jpg" alt={t.bandAlt} line={t.bandLine} />

      {/* Features -------------------------------------------------------- */}
      <section id="features" className="px-6 py-24 sm:py-32">
        <Features title={t.featuresTitle} features={t.features} />
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
