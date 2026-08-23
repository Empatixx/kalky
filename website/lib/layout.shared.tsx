import type { BaseLayoutProps } from 'fumadocs-ui/layouts/shared';
import { uiTranslations } from 'fumadocs-ui/i18n';
import { i18n } from '@/lib/i18n';
import { appName, gitConfig } from './shared';

// Fumadocs ships no Czech language pack, so the UI strings are provided here.
// Anything left out falls back to English.
export const translations = i18n
  .translations()
  .extend(uiTranslations())
  .add({
    en: {
      displayName: 'English',
    },
    cs: {
      displayName: 'Čeština',
      'Back to Home(404 not found page)': 'Zpět na úvod',
      'Choose a language(language switcher)': 'Vyberte jazyk',
      'Choose a language(language switcher)(aria-label)': 'Vyberte jazyk',
      'Close Search(search dialog)(aria-label)': 'Zavřít vyhledávání',
      'Close Sidebar(aria-label)': 'Zavřít postranní panel',
      'Close Sidebar(sidebar)(aria-label)': 'Zavřít postranní panel',
      'Collapse Sidebar(sidebar)(aria-label)': 'Sbalit postranní panel',
      'Copied Text(code block)(aria-label)': 'Zkopírováno',
      'Copy Anchor Link(heading anchor)(aria-label)': 'Kopírovat odkaz na nadpis',
      'Copy Text(code block)(aria-label)': 'Kopírovat text',
      'Dark(theme switcher)(aria-label)': 'Tmavý',
      'Edit on GitHub(edit page)': 'Upravit na GitHubu',
      'Hide Sidebar(sidebar)': 'Skrýt postranní panel',
      'Last updated on(page footer)': 'Naposledy upraveno',
      'Light(theme switcher)(aria-label)': 'Světlý',
      'Next Page(pagination)': 'Další stránka',
      'No Headings(table of contents)': 'Žádné nadpisy',
      'No results found(search dialog)': 'Nic nenalezeno',
      'On this page(table of contents)': 'Na této stránce',
      'Open Search(search trigger)(aria-label)': 'Otevřít vyhledávání',
      'Open Sidebar(aria-label)': 'Otevřít postranní panel',
      'Open Sidebar(sidebar)(aria-label)': 'Otevřít postranní panel',
      'Page Not Found(404 not found page)': 'Stránka nenalezena',
      'Previous Page(pagination)': 'Předchozí stránka',
      'Search(search dialog)': 'Hledat',
      'Search(search trigger)': 'Hledat',
      'Show Sidebar(sidebar)': 'Zobrazit postranní panel',
      'System(theme switcher)(aria-label)': 'Systémový',
      'Table of Contents(inline table of contents)': 'Obsah',
      'The page you are looking for might have been removed, had its name changed, or is temporarily unavailable.(404 not found page)':
        'Hledaná stránka mohla být odstraněna, přejmenována nebo je dočasně nedostupná.',
      'Toggle Menu(home layout header)(aria-label)': 'Přepnout nabídku',
      'Toggle Theme(theme switcher)(aria-label)': 'Přepnout motiv',
    },
  });

export function baseOptions(locale: string): BaseLayoutProps {
  return {
    nav: {
      title: appName,
      url: `/${locale}`,
    },
    githubUrl: `https://github.com/${gitConfig.user}/${gitConfig.repo}`,
    links: [
      {
        type: 'main',
        text: locale === 'cs' ? 'Dokumentace' : 'Documentation',
        url: `/${locale}/docs`,
      },
    ],
  };
}
