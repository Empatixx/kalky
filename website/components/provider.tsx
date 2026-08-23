'use client';
import SearchDialog from '@/components/search';
import { RootProvider } from 'fumadocs-ui/provider/next';
import { i18nProvider } from 'fumadocs-ui/i18n';
import { translations } from '@/lib/layout.shared';
import { type ReactNode } from 'react';

export function Provider({ lang, children }: { lang: string; children: ReactNode }) {
  return (
    <RootProvider search={{ SearchDialog }} i18n={i18nProvider(translations, lang)}>
      {children}
    </RootProvider>
  );
}
