# Fumadocs user documentation — design

Date: 2026-08-23
Status: approved

## Goal

Publish a user-facing guide to what the Kalky app does, in English and Czech,
as a static site on GitHub Pages, built and validated by CI.

## Decisions

| Question | Decision |
|---|---|
| Deployment | GitHub Pages, Next.js static export (`output: 'export'`) |
| Audience | End users — what the app does, not how it is built |
| Languages | English and Czech, both first-class (`/en/`, `/cs/`) |
| Images | Real screenshots captured on an Android emulator |
| Location | `website/` at the repo root, self-contained Node app |

## Architecture

Fumadocs (Next.js) lives in `website/`, isolated from Gradle. Content is MDX
under `website/content/docs/{en,cs}/`. The scaffold uses the
`+next+fuma-docs-mdx+static` template with Orama search.

Content sits inside the app rather than in the repo-root `docs/` folder:
`docs/` already holds the roadmap and design specs, and MDX with Fumadocs
components does not render correctly in GitHub's file view anyway. The docs
are meant to be read on the published site.

### i18n against static export

Fumadocs routes locales through a middleware that redirects to the user's
language. Next.js does not support middleware under `output: 'export'`, and
its own documentation notes the middleware is optional. So:

- `app/[lang]/` with `en` (default) and `cs`; `generateStaticParams` emits both
- a static redirect page at the root sends `/` to `/en/`
- search uses a build-time Orama index per locale, resolved in the browser

### Next.js configuration

```js
output: 'export'
basePath: '/kalky'          // project pages live under Empatixx.github.io/kalky
trailingSlash: true
images: { unoptimized: true }
```

## Content outline

1. What is Kalky — overview
2. Getting started — onboarding, profile, BMR/TDEE goals
3. Logging food — photo AI analysis, barcode, search, custom foods, manual entry
4. Daily overview — totals, macros, portion and nutrient editing
5. Analytics — trends, weight, streaks
6. Settings — language, units, notifications
7. Privacy — what is stored and where

English pages name Czech UI labels in parentheses so readers can match text on
screen.

## Screenshots

Captured on the `kalky_pixel` AVD (Android 35, Google APIs) using the local
`google-services.json`. `DatabaseSeeder.seedIfEmpty()` populates demo food, so
the home and analytics screens show realistic data without a backend.

Capturable: home, daily overview, food detail, nutrient edit, analytics,
profile, settings, onboarding.

Not capturable: photo analysis, search, and barcode scanning, because the
backend has been decommissioned; and the login screen, because Google Sign-In
is unreliable on a system image without Play Store. These flows are described
in prose, and the PNGs under `website/public/img/` can be swapped in later.

## CI

`.github/workflows/docs.yml`, filtered to `website/**`:

- pull request and push: `pnpm install` then `pnpm build` as validation
- push to main: deploy to Pages via `actions/deploy-pages`

GitHub Pages requires a paid plan on a private repository. The deploy job is
therefore enabled only once the repository becomes public; until then CI
builds the site without publishing it.

## Out of scope

Developer and architecture documentation, docs versioning, and custom theming.
The default Fumadocs theme is used as-is.
