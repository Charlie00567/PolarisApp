/***************************************************************************************************
                    RutaMasCorta.kt Última modificación: 18/Noviembre/2023
***************************************************************************************************/

package mx.edu.itl.polarisapp.model
data class Edge( val nodo1 : Nodo, val nodo2 : Nodo, val distance : Int )

    //----------------------------------------------------------------------------------------------

    fun findShortestPath ( edges : List<Edge>, source : Nodo, target : Nodo ) : ShortestPathResult {

        // Note: this implementation uses similar variable names as the algorithm given do.
        // We found it more important to align with the algorithm than to use possibly more sensible naming.

        val dist = mutableMapOf<Nodo, Int>()
        val prev = mutableMapOf<Nodo, Nodo?>()
        val q = findDistinctNodes( edges )

        q.forEach { v ->
            dist[ v ] = Integer.MAX_VALUE
            prev[ v ] = null
        }
        dist[ source ] = 0

        while ( q.isNotEmpty () ) {
            val u = q.minByOrNull { dist[ it ] ?: 0 }
            q.remove( u )

            if ( u == target ) {
                break
            }
            edges
                .filter { it.nodo1 == u }
                .forEach { edge ->
                    val v = edge.nodo2
                    val alt = ( dist[ u ] ?: 0 ) + edge.distance
                    if (alt < ( dist[ v ] ?: 0 ) ) {
                        dist[ v ] = alt
                        prev[ v ] = u
                    }
                }
        }

        return ShortestPathResult ( prev, dist, source, target )
    }

    //----------------------------------------------------------------------------------------------

    private fun findDistinctNodes ( edges : List<Edge> ) : MutableSet<Nodo> {
        val nodos = mutableSetOf<Nodo>()
        edges.forEach {
            nodos.add ( it.nodo1 )
            nodos.add ( it.nodo2 )
        }
        return nodos
    }

    //----------------------------------------------------------------------------------------------

    /**
     * Traverse result
     */
    class ShortestPathResult ( val prev : Map<Nodo, Nodo?>, val dist : Map<Nodo, Int>, val source : Nodo, val target : Nodo ) {

        //------------------------------------------------------------------------------------------

        fun shortestPath ( from : Nodo = source, to : Nodo = target, list : List<Nodo> = emptyList() ) : List<Nodo> {
            val last = prev[ to ] ?: return if ( from == to ) {
                list + to
            } else {
                emptyList ()
            }
            return shortestPath ( from, last, list ) + to
        }

        //------------------------------------------------------------------------------------------

        fun shortestDistance () : Int? {
            val shortest = dist[ target ]
            if ( shortest == Integer.MAX_VALUE ) {
                return null
            }
            return shortest
        }

        //------------------------------------------------------------------------------------------

    }

