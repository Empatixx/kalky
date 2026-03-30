# Kalky - Food & Nutrition Tracking App

## Project Specification

Course: Vývoj aplikací pro mobilní platformy
Authors: Jan Herold (učo 550508), Jiří Krokviak (učo 582916)
Date: March 2026

---

## 1. Abstract

Kalky is a food and nutrition tracking app for Android and iOS that helps users monitor their daily diet and reach their health goals.

Users can log meals in several ways: by taking a photo of their food (the app uses AI to recognize the dish and estimate its nutritional content), by scanning a product barcode, by searching a food database, or by entering values manually.

During the initial setup, users provide basic personal information (weight, height, age, gender, and activity level) along with their goal (lose weight, maintain, or gain). The app then calculates personalized daily targets for calories, protein, carbohydrates, and fat.

The home screen shows daily progress toward these targets with clear visual indicators. A streak counter motivates users by tracking consecutive days of logging. The analytics section offers charts for weight trends and nutrition history over any chosen date range.

Kalky is built as a cross-platform application using Kotlin Multiplatform, sharing core logic between Android and iOS while keeping the user interface native to each platform.

---

## 2. Functional Requirements

### 2.1 Onboarding

A guided setup flow collects user preferences and body metrics to calculate personalized nutritional targets. The steps are:

1. Language selection (Czech / English)
2. Unit system (Metric / Imperial)
3. Theme preference (System / Light / Dark)
4. Gender (Male / Female)
5. Body weight
6. Body height
7. Age
8. Activity level (Sedentary / Light / Active / Very Active)
9. Fitness goal (Weight loss / Maintenance / Weight gain)
10. Macro targets (auto-calculated, manually adjustable)
11. Promo code (optional)

The flow supports swipe navigation, displays a progress bar, and saves all data on completion.

### 2.2 Food Logging

Users can add food items through four methods:

- Photo analysis: take a photo of a meal. AI recognizes the food and estimates calories, protein, fat, and carbohydrates. A loading indicator is shown while the analysis runs.
- Barcode scanning: scan a product barcode to look up its nutritional information. If the product is not found locally, the app falls back to an online food database. Users choose portion size before adding.
- Text search: search for foods by name across saved foods and an online database. Results show the food name, calories, and macros with portion size selection.
- Manual entry: create custom foods from scratch or combine existing foods as ingredients. Portions are adjustable with automatic nutritional recalculation.

### 2.3 Daily Tracking & Progress

The home screen provides an at-a-glance overview of the user's daily nutrition progress:

- Calorie progress card showing current vs. target calories
- Three macronutrient cards (protein, carbs, fat) with circular progress indicators
- Week-based date picker for navigating between days
- List of recently added food items for the selected date
- Streak counter showing consecutive days with logged food
- Selection mode for bulk operations (save as custom food, delete)

### 2.4 Custom Food Management

Users can create and organize their own food entries for quick reuse:

- Create custom foods with name, calories, and macronutrient values
- Build composite foods from multiple ingredients with adjustable portions
- Browse and search user-created custom foods
- View recently used foods for quick re-logging
- Save selected food combinations as new custom foods

### 2.5 Food Detail Editing

Each logged food item can be reviewed and modified:

- View and edit food name, protein, fat, and carbohydrate values
- View food photograph (if captured via camera)
- Re-analyze food image to get updated nutritional data
- Share or delete food entries

### 2.6 Nutrient Target Management

Users can fine-tune their daily macronutrient goals after onboarding:

- View current calorie progress
- Adjust daily targets for protein, carbs, and fat (0–500g range)
- Targets are auto-calculated during onboarding and manually adjustable afterward

### 2.7 Analytics

The analytics section visualizes nutrition and body data over time:

- Configurable date range selection
- Weight trend line chart over selected period
- Daily nutrition bar charts showing calorie and macronutrient totals

### 2.8 Profile Management

Users can update their personal information at any time:

- Edit weight, height, and age
- Gender selection (Male / Female)
- BMI calculation and category display
- Activity level selection (Sedentary / Light / Active / Very Active)

### 2.9 Settings

The settings screen provides app-wide configuration options:

- Theme: System / Light / Dark appearance modes
- Language: Czech and English
- Units: Metric (kg, cm) and Imperial (lbs, in)
- Notifications: toggle meal reminder notifications
- App version display

### 2.10 Meal Reminders

The app sends smart notifications to encourage consistent logging:

- Notifications active between 7:00 and 21:00
- Suppressed if food was logged within the last 90 minutes
- Reminds user if no food logged after 10:00
- Reminds user if behind on daily macro targets

### 2.11 Streak System

A motivational feature that rewards daily consistency:

- Tracks consecutive days with at least one logged food item
- Displayed on the home screen
- Resets when a day is missed

---

## 3. Technologies

### 3.1 Mobile

- Kotlin Multiplatform (shared logic for Android and iOS)
- Jetpack Compose, Material3
- Koin (dependency injection)
- SQLDelight (local database)
- Ktor (networking)
- KotlinX Serialization, KotlinX DateTime
- Coil (image loading)
- Vico (charts)
- CameraX (camera)
- ML Kit (barcode scanning)
- WorkManager (notifications)

### 3.2 Backend

- TypeScript, Bun
- SQLite (product database)
- GenAI (food image analysis)
- GitHub CI/CD (deployment)

---

## 4. UI Screen Descriptions

### 4.1 Onboarding

A step-by-step wizard presented on first launch.

- Each step shows a single input on a full screen
- Progress bar at the top shows completion
- Navigation via "Continue" / "Back" buttons or swipe gestures
- Final step shows "Done" to complete setup

### 4.2 Home Screen

The main daily tracking screen.

- Week date picker at the top for navigating between days, with a "Today" shortcut
- Calorie card showing current vs. target calories with a progress bar and streak count
- Three macronutrient cards (protein, carbs, fat) with circular progress indicators and icons
- Scrollable list of recently added foods for the selected date (image, name, calories, macros)
- Tap a food to open detail view; long-press activates selection mode (save as custom food / delete)
- Two FABs at the bottom: camera capture and barcode scanning

### 4.3 Food Detail

A full-screen food detail view.

- Upper half displays the food photograph with back and menu buttons overlaid
- Bottom card slides up with editable fields: food name, calories (read-only), protein, fat, and carbs
- "Fix Result" button re-sends the image for AI re-analysis
- "Finish" button saves changes and navigates back
- Menu offers share and delete actions

### 4.4 Nutrient Edit

A screen for adjusting daily macronutrient targets.

- Calorie card at the top showing current count and progress
- Three expandable rows for protein, carbs, and fat
- Each row has a label, current value in grams, color-coded icon, and a wheel picker (0–500g) that expands when tapped

### 4.5 Custom Food Search

A food search and selection screen.

- Search bar at the top filters results
- Tab selector switches between: All, My Foods, and Recently Used
- Food list shows items with checkboxes, images, names, calories, and macros
- "Add Manually" button navigates to manual food entry
- Online results open a portion picker with quick portion buttons (50g, 100g, 150g, 200g, 250g) and a scaled nutrition preview
- Floating add button at the bottom shows selection count and adds all selected foods

### 4.6 Manual Food Entry

A form for creating custom foods.

- Food name input field
- Ingredient search with auto-suggestions (top 5 results)
- Source foods card showing added ingredients with adjustable portion sizes and auto-calculated macros
- Calories summary showing the total
- Expandable macro editors (protein, carbs, fat) with wheel pickers
- Save button disabled until a food name is provided

### 4.7 Analytics

A data visualization screen.

- Two collapsible date range pickers (start and end) for choosing a time period
- Weight line chart displaying trends over the selected range
- Nutrition bar charts showing daily calorie and macronutrient totals

### 4.8 Settings

A configuration screen organized in sections:

- Appearance: System / Light / Dark theme
- Language: Czech and English
- Units: Metric and Imperial
- Notifications: enable/disable meal reminders
- App version display

### 4.9 Profile

User profile editor accessible from Settings.

- Editable weight, height, and age with inline pickers
- Gender selector
- BMI card showing calculated value and category
- Activity level selector with four options and visual indicators

### 4.10 Camera

A full-screen camera interface.

- Camera preview fills the screen with a back button at the top
- Mode toggle switches between photo and barcode modes
- Capture button takes photos
- In barcode mode, detected barcodes show a result card with product information, portion input, and nutritional details, or an error message with a retry option

### 4.11 Barcode Scanner

A dedicated barcode scanning screen.

- Camera preview fills the background with a close button
- Hint text ("Scan a barcode") displays while scanning
- Bottom card shows results: loading spinner during lookup, product details with quantity input and macros when found, or a not-found message with a retry button
