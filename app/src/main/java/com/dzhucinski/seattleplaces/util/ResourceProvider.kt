package com.dzhucinski.seattleplaces.util

/**
 * Created by Denis Zhuchinski on 4/14/19.
 *
 * The interface acts as an entry point to application resources
 * in places which is not aware of the Context.
 *
 * Only strings for now but could be scaled including images, etc.
 */
interface ResourceProvider {

    fun getString(resourceIdentifier: Int, vararg arguments: Any = arrayOf()): String
}