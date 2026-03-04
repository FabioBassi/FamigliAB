package com.fabiobassi.famigliab.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import com.fabiobassi.famigliab.MainActivity
import com.fabiobassi.famigliab.NavItem
import com.fabiobassi.famigliab.R
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.size as ComposeSize
import androidx.compose.foundation.layout.width as ComposeWidth
import androidx.compose.foundation.layout.height as ComposeHeight
import androidx.compose.foundation.layout.padding as ComposePadding
import androidx.compose.foundation.layout.fillMaxSize as ComposeFillMaxSize
import androidx.compose.foundation.layout.Row as ComposeRow
import androidx.compose.foundation.layout.Box as ComposeBox
import androidx.compose.foundation.layout.Spacer as ComposeSpacer
import androidx.compose.foundation.background as ComposeBackground
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment as ComposeAlignment
import androidx.compose.ui.Modifier as ComposeModifier
import com.fabiobassi.famigliab.ui.theme.FamigliABTheme
import androidx.glance.ColorFilter

class FamigliABWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val items = listOf(
            NavItem.Budgeting to R.drawable.ic_budgeting,
            NavItem.PoopTracker to R.drawable.ic_poop,
            NavItem.Medications to R.drawable.ic_medications,
            NavItem.Passwords to R.drawable.ic_passwords,
            NavItem.Miscellaneous to R.drawable.ic_miscellaneous
        )

        provideContent {
            GlanceTheme {
                WidgetContent(items)
            }
        }
    }

    @Composable
    internal fun WidgetContent(items: List<Pair<NavItem, Int>>) {
        // Main container: Pill-shaped with a very light surface color
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.primary)
                .cornerRadius(24.dp)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = GlanceModifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items.forEachIndexed { index, (item, iconRes) ->
                    val isFirst = index == 0
                    
                    // First item is circular, others are rounded rectangles
                    val shapeRadius = if (isFirst) 48.dp else 24.dp
                    val bgColor = if (isFirst) GlanceTheme.colors.tertiaryContainer else GlanceTheme.colors.secondaryContainer
                    val iconColor = if (isFirst) GlanceTheme.colors.onTertiaryContainer else GlanceTheme.colors.onSecondaryContainer
                    
                    Box(
                        modifier = GlanceModifier
                            .defaultWeight()
                            .fillMaxHeight()
                            .background(bgColor)
                            .cornerRadius(shapeRadius)
                            .clickable(
                                actionStartActivity<MainActivity>(
                                    actionParametersOf(
                                        NavigationAction.NavigationKey to item.route
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            provider = ImageProvider(iconRes),
                            contentDescription = null,
                            modifier = GlanceModifier.size(24.dp),
                            colorFilter = ColorFilter.tint(iconColor)
                        )
                    }

                    if (index < items.lastIndex) {
                        Spacer(modifier = GlanceModifier.width(8.dp))
                    }
                }
            }
        }
    }
}

object NavigationAction {
    val NavigationKey = ActionParameters.Key<String>("navigate_to")
}

class FamigliABWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = FamigliABWidget()
}
