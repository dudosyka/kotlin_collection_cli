package lab5kotlin.house

import lab5kotlin.collection.Collection
import lab5kotlin.collection.item.EntityBuilder
import lab5kotlin.collection.item.FieldDelegate
import lab5kotlin.collection.item.FieldType
import lab5kotlin.collection.item.Validator
import lab5kotlin.human.Human
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent
import java.time.ZonedDateTime

class HouseBuilder: EntityBuilder<House>() {
    private val collection: Collection<Human> by KoinJavaComponent.inject(Collection::class.java, named("collection"))
    @Transient
    override val fields: MutableMap<String, Validator> = mutableMapOf(
        "name" to Validator(mapOf(
            "required" to false,
            "type" to FieldType.STRING
        )),
        "year" to Validator(mapOf(
            "required" to true,
            "type" to FieldType.LONG,
        )),
        "numberOfFloors" to Validator(mapOf(
            "required" to true,
            "type" to FieldType.LONG,
        )),
        "numberOfFlatsOnFloor" to Validator(mapOf(
            "required" to true,
            "type" to FieldType.LONG,
        )),
        "numberOfLifts" to Validator(mapOf(
            "required" to true,
            "type" to FieldType.LONG,
        )),
    )
    /**
     * Build
     *
     * @param map
     * @return
     */
    override fun build(map: MutableMap<String, Any?>): House {
        val id: Int = collection.getUniqueId()
        val name: String? by FieldDelegate(map = map, fields["name"]!!)
        val year: Long? by FieldDelegate(map = map, fields["year"]!!)
        val numberOfFloors: Long? by FieldDelegate(map = map, fields["numberOfFloors"]!!)
        val numberOfFlatsOnFloor: Long? by FieldDelegate(map = map, fields["numberOfFlatsOnFloor"]!!)
        val numberOfLifts: Long? by FieldDelegate(map = map, fields["numberOfLifts"]!!)
        return House(id, ZonedDateTime.now(), name, year!!, numberOfFloors!!, numberOfFlatsOnFloor!!, numberOfLifts!!)
    }
}