package io.github.xmoon2022.water.widget

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class GlanceWidgetReceiver : GlanceAppWidgetReceiver(){
    override val glanceAppWidget: GlanceAppWidget = GlanceWidget()
}