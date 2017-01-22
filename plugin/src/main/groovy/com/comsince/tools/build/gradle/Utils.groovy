// Utils.groovy
package com.comsince.tools.build.gradle

class Utils {
	static def gradleAfter(gradleVersion, targetVersion) {
       [
            gradleVersion.toString().tokenize('.')*.toInteger(),
            targetVersion.toString().tokenize('.')*.toInteger()
        ].transpose().inject(null){ result, item ->
            result != null ? result :
                    (item[0] > item[1] ?: item[0] == item[1] ? null : false)
        }
	}

}