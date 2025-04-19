import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.kotlin.argumentCaptor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import kotlin.test.assertEquals

class NbtItemTest {

    @Mock
    lateinit var plugin: JavaPlugin

    @Mock
    lateinit var itemStack: ItemStack

    @Mock
    lateinit var meta: ItemMeta

    @Mock
    lateinit var dataContainer: PersistentDataContainer

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        
        `when`(itemStack.itemMeta).thenReturn(meta)
        `when`(meta.persistentDataContainer).thenReturn(dataContainer)
    }

    @Test
    fun `createSpecialItem should write NBT correctly`() {
        val id = "special_item"
        
        val specialItem = NbtItem.modify(itemStack, plugin)
            .applyOperation(NbtOperation.write(NbtDef.IS_SPECIAL, 1.toByte())
                .andThen(NbtOperation.write(NbtDef.ITEM_ID, id)))
            .done()

        val captor = argumentCaptor<Map<String, Any>>()
        verify(dataContainer).set(eq(NbtDef.IS_SPECIAL.path()), eq(PersistentDataType.BYTE), captor.capture())
        assertEquals(1.toByte(), captor.firstValue[NbtDef.IS_SPECIAL.path()])

        verify(dataContainer).set(eq(NbtDef.ITEM_ID.path()), eq(PersistentDataType.STRING), eq(id))
    }

    @Test
    fun `inspectItem should log correct data`() {
        val item = ItemStack(Material.DIAMOND_SWORD)
        val id = "special_item"
        
        `when`(dataContainer.get(NbtDef.ITEM_ID.path(), PersistentDataType.STRING)).thenReturn(id)
        `when`(dataContainer.get(NbtDef.IS_SPECIAL.path(), PersistentDataType.BYTE)).thenReturn(1.toByte())

        NbtItem.modify(item, plugin)
            .inspectContainer { container ->
                mapOf(
                    "special" to container.get(NbtDef.IS_SPECIAL.path(), PersistentDataType.BYTE),
                    "id" to container.get(NbtDef.ITEM_ID.path(), PersistentDataType.STRING)
                )
            }
            .run(
                { data -> plugin.logger.info("NBT Data: $data") },
                { error -> plugin.logger.warning("NBT inspection failed: $error") }
            )

        verify(plugin.logger).info("NBT Data: {special=1, id=special_item}")
    }

    @Test
    fun `createSpecialItemWithList should handle list correctly`() {
        val tags = listOf("tag1", "tag2", "tag3")
        val itemStack = ItemStack(Material.DIAMOND_SWORD)
        
        val modifiedItem = NbtItem.modify(itemStack, plugin)
            .applyOperation(NbtOperation.writeList(NbtDef.TAG_LIST, tags))
            .done()

        val captor = argumentCaptor<List<String>>()
        verify(dataContainer).set(eq(NbtDef.TAG_LIST.path()), eq(PersistentDataType.STRING_LIST), captor.capture())
        assertEquals(tags, captor.firstValue)
    }

    @Test
    fun `createSpecialItemWithMap should handle map correctly`() {
        val mapData = mapOf("key1" to "value1", "key2" to "value2")
        val itemStack = ItemStack(Material.DIAMOND_SWORD)

        val modifiedItem = NbtItem.modify(itemStack, plugin)
            .applyOperation(NbtOperation.writeMap(NbtDef.TAG_MAP, mapData))
            .done()

        val captor = argumentCaptor<Map<String, String>>()
        verify(dataContainer).set(eq(NbtDef.TAG_MAP.path()), eq(PersistentDataType.TAG_CONTAINER), captor.capture())
        assertEquals(mapData, captor.firstValue)
    }
}
