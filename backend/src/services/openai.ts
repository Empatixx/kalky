import OpenAI from "openai";

let _client: OpenAI | null = null;

function getClient(): OpenAI {
  if (!_client) {
    _client = new OpenAI({ apiKey: process.env.OPENAI_API_KEY });
  }
  return _client;
}

export interface FoodAnalysis {
  weight: number;
  foodType: string;
  title: string;
  protein: number;
  fat: number;
  carbs: number;
  healthScore: number;
}

const SYSTEM_PROMPT = `You are a food nutrition analyst. Analyze the food in the image and return a JSON object with these exact fields:
- weight: estimated weight in grams (integer)
- foodType: category like "fruit", "main_course", "snack", "salad", "breakfast", "dessert", "drink" (string)
- title: name of the food in Czech language (string)
- protein: estimated protein in grams (integer)
- fat: estimated fat in grams (integer)
- carbs: estimated carbohydrates in grams (integer)
- healthScore: health score from 1 to 10 (integer)

Return ONLY the JSON object, no markdown, no code blocks, no explanation.`;

export async function analyzeImage(imageBase64: string): Promise<FoodAnalysis> {
  const response = await getClient().chat.completions.create(
    {
      model: "gpt-5-mini",
      messages: [
        { role: "system", content: SYSTEM_PROMPT },
        {
          role: "user",
          content: [
            {
              type: "image_url",
              image_url: {
                url: `data:image/jpeg;base64,${imageBase64}`,
                detail: "low",
              },
            },
          ],
        },
      ],
      max_tokens: 300,
    },
    { signal: AbortSignal.timeout(30_000) }
  );

  const text = response.choices[0]?.message?.content?.trim();
  if (!text) {
    throw new Error("Empty response from OpenAI");
  }

  const cleaned = text.replace(/^```json?\s*/, "").replace(/\s*```$/, "");
  const parsed = JSON.parse(cleaned);

  return {
    weight: Math.round(parsed.weight),
    foodType: String(parsed.foodType),
    title: String(parsed.title),
    protein: Math.round(parsed.protein),
    fat: Math.round(parsed.fat),
    carbs: Math.round(parsed.carbs),
    healthScore: Math.round(parsed.healthScore),
  };
}
