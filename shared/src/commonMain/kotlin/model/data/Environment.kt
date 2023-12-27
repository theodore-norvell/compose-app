package model.data

import model.data.formula.Formula

class Environment( private val map : Map<String, Formula> = HashMap() ) {
    // Invariant.  The environment contains no circularity
    // in the sense that there is no nonempty, finite sequence of
    // strings x of length n+1 > 0 such that all are in the key set,
    // x(1) in map[x(0)].freeVars(), x(2) in map[x(1)].freeVars(), ...,  x(n) in x(n-1).freeVars()
    // and x(0) in map[x(n)].freeVars().
    // For example there is no string x such that x in map[x].freeVars().

    fun keys() : List<String> = map.keys.toList()

    fun has( varName : String ) : Boolean {
        return map.containsKey(varName)
    }

    fun get( varName : String ) : Formula? {
        return map[varName]
    }

    fun canPut( varName : String, formula: Formula) : Boolean {
        var fringe = formula.freeVars()
        var found : Boolean
        while( true ) {
            found = fringe.contains( varName )
            if( fringe.isEmpty() || found ) break
            val listOfSets = fringe.map {
                when( val f = map[it] ) {
                    null -> emptySet()
                    else -> f.freeVars()} }
            fringe = listOfSets.fold(
                initial = emptySet(),
                operation = { acc: Set<String>, strings: Set<String> ->  acc.union(strings)})
        }
        return ! found
    }

    fun put( varName: String, formula: Formula) : Environment {
        check( canPut( varName, formula) )
        return Environment( map + (varName to formula) )
    }
}
