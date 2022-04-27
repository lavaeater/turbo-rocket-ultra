package turbofacts

class NewStoryManager {
    var needsChecking = true
    fun checkIfNeeded() {
        if (needsChecking) {
            needsChecking = false
            for (rule in NewFactTester.rules) {
                if (rule.checkRule()) {
                    rule.consequence(rule.criteria)
                }
            }
        }
    }
}