package dev.sunnyday.postcreator.domain.backgrounds

import android.net.Uri
import dev.sunnyday.postcreator.domain.backgrounds.db.BackgroundEntity
import dev.sunnyday.postcreator.domain.backgrounds.db.BackgroundEntityType
import javax.inject.Inject

internal class BackgroundsMapper @Inject constructor() {

    fun entityToPlain(entity: BackgroundEntity): Background = when(entity.type) {
        BackgroundEntityType.COLOR -> colorBackground(entity)
        BackgroundEntityType.GRADIENT -> gradientBackground(entity)
        BackgroundEntityType.RES -> resourceBackground(entity)
        BackgroundEntityType.STORED -> storedBackground(entity)
    }

    private fun colorBackground(entity: BackgroundEntity): Background =
        Background.Color(entity.id, entity.value.toInt())

    private fun gradientBackground(entity: BackgroundEntity): Background {
        val colors = entity.value.split(",")
            .map(String::toInt)
            .toIntArray()
        return Background.Gradient(entity.id, colors)
    }

    private fun resourceBackground(entity: BackgroundEntity): Background =
        Background.Resource(entity.id, entity.value.toInt(), entity.icon?.let(Uri::parse))

    private fun storedBackground(entity: BackgroundEntity): Background =
        Background.Stored(entity.id, Uri.parse(entity.value), entity.icon?.let(Uri::parse))

    fun plainToEntity(background: Background): BackgroundEntity {
        val (type, value) = when(background) {
            is Background.Color ->
                BackgroundEntityType.COLOR to background.color.toString()
            is Background.Gradient ->
                BackgroundEntityType.GRADIENT to background.colors.joinToString(",")
            is Background.Resource ->
                BackgroundEntityType.RES to background.resId.toString()
            is Background.Stored ->
                BackgroundEntityType.STORED to background.uri.toString()
        }

        val icon = (background as? HasBackgroundIcon)?.icon?.toString()

        return BackgroundEntity(background.id, type, value, icon)
    }

}