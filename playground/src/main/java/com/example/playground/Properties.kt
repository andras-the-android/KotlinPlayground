package com.example.playground

/**
 * In Kotlin we declare properties instead of fields. Properties may or may not use fields.
 */
open class Properties {

    init {
        log("initBlock")
    }

    var someValue = 5

    // compile error
    // var lazyp: Int by lazy {
    val lazyProp: Int by lazy {
        log("lazy property initialized")
        someValue * 2
    }

    var initBlock = run {
        log("property init block")
        5
    }

    val customImmutableAccessorWithoutBackingField get() = this.someValue * 2

    open var customMutableAccessorWithoutBackingField: Int
        get() {
            return 666
        }
        set(value) {
            // no warning if there is no meaningful operation here
        }

    var customAccessorWithBackingField: Int = 0 // the initializer assigns the backing field directly
        get() {
            log("computed get $field")
            // backing field is ignored
            return someValue
        }
        set(value) {
            log("computed set $value")
            // it's not compile error to skip setting backing field but it can break the functionality
            field = value
        }

    private var backingProperty: String? = null
    var customAccessorWithBackingProperty: Int
        get() = backingProperty?.toInt() ?: 0
        set(value) { backingProperty = value.toString()}

    var setterVisibility: String = "abc"
        private set // the setter is private and has the default implementation

    var setterWithAnnotation: Any? = null
        @Deprecated("") set // apply annotation on only one accessor

}

class DerivedProperties: Properties() {

    // we can override a property with custom accessor with a plain property
    override var customMutableAccessorWithoutBackingField: Int = 5

}

fun main() {
    val p = Properties()
    log("initialized")
    p.customAccessorWithBackingField
    p.customAccessorWithBackingField = 55
    log("customAccessor get returns ${p.customAccessorWithBackingField}")
    p.initBlock
    p.lazyProp

}
