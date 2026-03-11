const express = require('express');
const app = express();
const PORT = 8080;

const mockFoods = [
  { weight: 200, foodType: 'main_course', title: 'Grilled Chicken Breast', protein: 45, fat: 8, carbs: 0, healthScore: 90 },
  { weight: 150, foodType: 'snack',       title: 'Greek Yogurt',           protein: 15, fat: 3, carbs: 12, healthScore: 85 },
  { weight: 300, foodType: 'main_course', title: 'Pasta Bolognese',        protein: 22, fat: 14, carbs: 58, healthScore: 65 },
  { weight: 180, foodType: 'salad',       title: 'Caesar Salad',           protein: 10, fat: 18, carbs: 8,  healthScore: 72 },
  { weight: 250, foodType: 'main_course', title: 'Salmon with Rice',       protein: 35, fat: 12, carbs: 40, healthScore: 88 },
  { weight: 100, foodType: 'snack',       title: 'Banana',                 protein: 1,  fat: 0,  carbs: 27, healthScore: 80 },
  { weight: 220, foodType: 'main_course', title: 'Beef Burger',            protein: 28, fat: 22, carbs: 35, healthScore: 55 },
  { weight: 160, foodType: 'breakfast',   title: 'Scrambled Eggs',         protein: 18, fat: 14, carbs: 2,  healthScore: 78 },
];

app.post('/cal', (req, res) => {
  const food = mockFoods[Math.floor(Math.random() * mockFoods.length)];
  console.log(`[POST /cal] Returning: ${food.title}`);
  res.json(food);
});

app.listen(PORT, '0.0.0.0', () => {
  console.log(`Kalai mock backend running on http://0.0.0.0:${PORT}`);
});
