import { source } from '@/lib/source';
import { createFromSource } from 'fumadocs-core/search/server';

export const revalidate = false;

// Static export: indexes are built at compile time and searched in the browser.
// The default `multilingual` mode covers every configured locale.
export const { staticGET: GET } = createFromSource(source);
