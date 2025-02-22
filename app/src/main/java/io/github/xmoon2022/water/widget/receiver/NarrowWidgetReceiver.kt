package io.github.xmoon2022.water.widget.receiver

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import io.github.xmoon2022.water.widget.NarrowWidget

class NarrowWidgetReceiver : GlanceAppWidgetReceiver(){
    override val glanceAppWidget: GlanceAppWidget = NarrowWidget()
}