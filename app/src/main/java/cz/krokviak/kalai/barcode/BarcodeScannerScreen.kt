package cz.krokviak.kalai.barcode

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalai.barcode.data.OpenFoodFactsProduct
import cz.krokviak.kalai.theme.AppTheme
import io.github.alexzhirkevich.cupertino.CupertinoButton
import io.github.alexzhirkevich.cupertino.CupertinoButtonDefaults
import io.github.alexzhirkevich.cupertino.CupertinoText

@Composable
fun BarcodeScannerScreen(
    state: BarcodeScanState,
    cameraPreview: @Composable () -> Unit,
    onAddClick: (OpenFoodFactsProduct, Int) -> Unit,
    onRetryClick: () -> Unit,
    onCloseClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Camera preview as background
        cameraPreview()

        // Close button
        IconButton(
            onClick = onCloseClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Zavřít",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }

        // Scan hint at top
        if (state is BarcodeScanState.Scanning) {
            Text(
                text = "Naskenujte čárový kód",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 72.dp)
                    .background(
                        Color.Black.copy(alpha = 0.5f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            )
        }

        // Bottom card overlay
        when (state) {
            is BarcodeScanState.Loading -> {
                BottomCard(Modifier.align(Alignment.BottomCenter)) {
                    CircularProgressIndicator(
                        color = AppTheme.colors.onBackground,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Hledám produkt...")
                }
            }

            is BarcodeScanState.ProductFound -> {
                val product = state.product
                val nutriments = product.nutriments
                val defaultQuantity = product.servingSize?.filter { it.isDigit() }?.takeIf { it.isNotEmpty() }
                    ?: product.productQuantity?.filter { it.isDigit() }?.takeIf { it.isNotEmpty() }
                    ?: "100"
                var quantityText by remember { mutableStateOf(defaultQuantity) }
                val quantity = quantityText.toIntOrNull() ?: 0
                val multiplier = quantity / 100.0

                BottomCard(Modifier.align(Alignment.BottomCenter)) {
                    Text(
                        text = product.productName ?: "Neznámý produkt",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.onBackground
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { quantityText = it.filter { c -> c.isDigit() } },
                        label = { Text("Množství (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    NutrientRow("Kalorie", "${((nutriments?.energyKcal100g ?: 0.0) * multiplier).toInt()} kcal")
                    NutrientRow("Bílkoviny", "${((nutriments?.proteins100g ?: 0.0) * multiplier).toInt()} g")
                    NutrientRow("Tuky", "${((nutriments?.fat100g ?: 0.0) * multiplier).toInt()} g")
                    NutrientRow("Sacharidy", "${((nutriments?.carbohydrates100g ?: 0.0) * multiplier).toInt()} g")

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CupertinoButton(
                            onClick = onRetryClick,
                            modifier = Modifier.weight(1f),
                            colors = CupertinoButtonDefaults.filledButtonColors(
                                containerColor = AppTheme.colors.border,
                                contentColor = AppTheme.colors.onBackground
                            )
                        ) {
                            CupertinoText("Znovu", fontWeight = FontWeight.Bold)
                        }
                        CupertinoButton(
                            onClick = { onAddClick(product, quantity) },
                            modifier = Modifier.weight(1f),
                            colors = CupertinoButtonDefaults.filledButtonColors(
                                containerColor = AppTheme.colors.primary,
                                contentColor = AppTheme.colors.onPrimary
                            )
                        ) {
                            CupertinoText("Přidat", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            is BarcodeScanState.NotFound -> {
                BottomCard(Modifier.align(Alignment.BottomCenter)) {
                    Text(
                        text = "Produkt nenalezen",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.onBackground
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CupertinoButton(
                        onClick = onRetryClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = CupertinoButtonDefaults.filledButtonColors(
                            containerColor = AppTheme.colors.primary,
                            contentColor = AppTheme.colors.onPrimary
                        )
                    ) {
                        CupertinoText("Zkusit znovu", fontWeight = FontWeight.Bold)
                    }
                }
            }

            is BarcodeScanState.Error -> {
                BottomCard(Modifier.align(Alignment.BottomCenter)) {
                    Text(
                        text = "Chyba: ${state.message}",
                        fontSize = 16.sp,
                        color = Color.Red
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CupertinoButton(
                        onClick = onRetryClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = CupertinoButtonDefaults.filledButtonColors(
                            containerColor = AppTheme.colors.primary,
                            contentColor = AppTheme.colors.onPrimary
                        )
                    ) {
                        CupertinoText("Zkusit znovu", fontWeight = FontWeight.Bold)
                    }
                }
            }

            is BarcodeScanState.Scanning -> { /* No bottom card while scanning */ }
        }
    }
}

@Composable
private fun BottomCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(AppTheme.colors.surface)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        content()
    }
}

@Composable
private fun NutrientRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 16.sp, color = AppTheme.colors.onBackgroundSecondary)
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AppTheme.colors.onBackground)
    }
}
