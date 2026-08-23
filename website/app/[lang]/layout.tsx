import '../global.css';
import { Inter, DM_Sans } from 'next/font/google';
import { Provider } from '@/components/provider';
import { i18n } from '@/lib/i18n';

const inter = Inter({
  subsets: ['latin', 'latin-ext'],
});

// Display face for the landing page; the docs keep Inter.
const display = DM_Sans({
  subsets: ['latin', 'latin-ext'],
  variable: '--font-display',
});

export function generateStaticParams() {
  return i18n.languages.map((lang) => ({ lang }));
}

export default async function Layout({ params, children }: LayoutProps<'/[lang]'>) {
  const { lang } = await params;

  return (
    <html lang={lang} className={`${inter.className} ${display.variable}`} suppressHydrationWarning>
      <body className="flex flex-col min-h-screen">
        <Provider lang={lang}>{children}</Provider>
      </body>
    </html>
  );
}
