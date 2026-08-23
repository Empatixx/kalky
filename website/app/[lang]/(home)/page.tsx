import Link from 'next/link';
import { i18n } from '@/lib/i18n';

export function generateStaticParams() {
  return i18n.languages.map((lang) => ({ lang }));
}

const copy = {
  en: {
    tagline: 'Track what you eat, without the data entry.',
    body: 'Kalky is a food and nutrition tracker for Android and iOS. Photograph a plate and it estimates the nutrition, scan a barcode for packaged food, and watch your calories and macros add up over the day.',
    cta: 'Read the guide',
    features: [
      ['Photograph a meal', 'Point the camera at a plate and get an estimate of calories, protein, carbs and fat.'],
      ['Scan a barcode', 'Packaged food is looked up in the product database, with Open Food Facts as a fallback.'],
      ['See the day add up', 'Daily totals, macro rings and a running streak of days you logged.'],
      ['Follow the trend', 'Weekly and monthly averages, weight progress and how it moves with your intake.'],
    ],
  },
  cs: {
    tagline: 'Zapisuj, co jíš, bez vyplňování tabulek.',
    body: 'Kalky je aplikace pro sledování jídla a výživy na Android a iOS. Vyfoť talíř a odhadne ti výživové hodnoty, u balených potravin načti čárový kód a sleduj, jak ti přes den narůstají kalorie a makroživiny.',
    cta: 'Přečíst průvodce',
    features: [
      ['Vyfoť jídlo', 'Namiř foťák na talíř a dostaneš odhad kalorií, bílkovin, sacharidů a tuků.'],
      ['Načti čárový kód', 'Balené potraviny se dohledají v databázi produktů, záložně přes Open Food Facts.'],
      ['Sleduj celý den', 'Denní součty, kroužky makroživin a série dní, kdy sis jídlo zapsal.'],
      ['Podívej se na trend', 'Týdenní a měsíční průměry, vývoj váhy a jak souvisí s tím, co jíš.'],
    ],
  },
} as const;

export default async function HomePage({ params }: PageProps<'/[lang]'>) {
  const { lang } = await params;
  const t = copy[lang as keyof typeof copy] ?? copy.en;

  return (
    <main className="flex flex-1 flex-col items-center px-4 py-16">
      <div className="w-full max-w-3xl text-center">
        <h1 className="mb-4 text-4xl font-bold tracking-tight sm:text-5xl">Kalky</h1>
        <p className="mb-6 text-xl font-medium text-fd-muted-foreground">{t.tagline}</p>
        <p className="mx-auto mb-8 max-w-2xl text-fd-muted-foreground">{t.body}</p>
        <Link
          href={`/${lang}/docs`}
          className="inline-flex items-center rounded-lg bg-fd-primary px-5 py-2.5 font-medium text-fd-primary-foreground transition-opacity hover:opacity-90"
        >
          {t.cta}
        </Link>
      </div>

      <div className="mt-16 grid w-full max-w-3xl gap-4 sm:grid-cols-2">
        {t.features.map(([title, description]) => (
          <div key={title} className="rounded-xl border border-fd-border bg-fd-card p-5 text-left">
            <h2 className="mb-1.5 font-semibold">{title}</h2>
            <p className="text-sm text-fd-muted-foreground">{description}</p>
          </div>
        ))}
      </div>
    </main>
  );
}
