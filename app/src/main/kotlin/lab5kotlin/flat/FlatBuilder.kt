package lab5kotlin.flat

import kotlinx.serialization.Transient
import lab5kotlin.collection.Collection
import lab5kotlin.collection.item.EntityBuilder
import lab5kotlin.collection.item.FieldDelegate
import lab5kotlin.collection.item.FieldType
import lab5kotlin.collection.item.Validator
import lab5kotlin.coordinates.CoordinatesBuilder
import lab5kotlin.house.HouseBuilder
import lab5kotlin.human.Fatness
import lab5kotlin.human.Human
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent
import java.time.ZonedDateTime

class FlatBuilder: EntityBuilder<Flat>() {
    private val collection: Collection<Human> by KoinJavaComponent.inject(Collection::class.java, named("collection"))
    @Transient
    override val fields: MutableMap<String, Validator> = mutableMapOf(
        "name" to Validator(mapOf(
            "required" to true,
            "type" to FieldType.STRING
        )),
        "area" to Validator(mapOf(
            "required" to true,
            "type" to FieldType.FLOAT,
            "min" to 0,
        )),
        "numberOfRooms" to Validator(mapOf(
            "required" to true,
            "type" to FieldType.LONG,
            "min" to 0,
        )),
        "numberOfBathrooms" to Validator(mapOf(
            "required" to true,
            "type" to FieldType.LONG,
            "min" to 0,
        )),
        "timeToMetroByTransport" to Validator(mapOf(
            "required" to true,
            "type" to FieldType.INT,
            "min" to 0,
        )),
        "coordinates" to Validator(mapOf(
            "type" to FieldType.ENTITY,
            "childEntity" to CoordinatesBuilder(),
            "required" to true
        )),
        "house" to Validator(mapOf(
            "type" to FieldType.ENTITY,
            "childEntity" to HouseBuilder(),
            "required" to false
        )),
        "furnish" to Validator(mapOf(
            "type" to FieldType.ENUM,
            "childEnum" to "Furnish",
            "childEnumVariants" to Furnish.values().map { it.toString() },
            "required" to false
        ))
    )

    /**
     * Build
     *
     * @param map
     * @return
     */
    override fun build(map: MutableMap<String, Any?>): Flat {
        val id: Int = collection.getUniqueId()
        val name: String? by FieldDelegate(map = map, fields["name"]!!)
        val area: Float? by FieldDelegate(map = map, fields["area"]!!)
        val numberOfRooms: Long? by FieldDelegate(map = map, fields["numberOfRooms"]!!)
        val numberOfBathrooms: Long? by FieldDelegate(map = map, fields["numberOfBathrooms"]!!)
        val timeToMetroByTransport: Int? by FieldDelegate(map = map, fields["timeToMetroByTransport"]!!)
        val furnish: String? by FieldDelegate(map = map, fields["furnish"]!!)
        val furnishValue = Furnish.valueOf(furnish!!)

        val coordinates: MutableMap<String, Any?>? by FieldDelegate(map = map, fields["coordinates"]!!)
        val coordinatesEntity = CoordinatesBuilder().build(coordinates!!)

        val house: MutableMap<String, Any?>? by FieldDelegate(map = map, fields["house"]!!)
        val houseEntity = HouseBuilder().build(house!!)

        return Flat(id, ZonedDateTime.now(),name!!,area!!,numberOfRooms!!,numberOfBathrooms!!,timeToMetroByTransport!!, coordinatesEntity, furnishValue, houseEntity)
    }
}