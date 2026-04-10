package org.my.drivexcel.domain

import org.my.drivexcel.domain.model.LeaderUserDomainModel
import kotlin.math.abs

class LeaderUserSearch() {

    fun search(query: String, users: List<LeaderUserDomainModel>): List<LeaderUserDomainModel> {
        val queryTokens = normalize(query)

        return users
            .map { user ->
                user to score(user, queryTokens)
            }
            .filter { it.second > 0 }
            .sortedByDescending { it.second }
            .map { it.first }
    }

    private fun score(user: LeaderUserDomainModel, queryTokens: List<String>): Int {
        var score = 0

        for (q in queryTokens) {
            val best = user.tokens.maxOfOrNull { t ->
                when {
                    t == q -> 5
                    t.startsWith(q) -> 3
                    isClose(t, q) -> 1
                    else -> 0
                }
            } ?: 0

            score += best
        }

        return score
    }

    private fun isClose(a: String, b: String): Boolean {
        if (abs(a.length - b.length) > 1) return false

        return levenshtein(a, b) <= 1
    }

    private fun levenshtein(a: String, b: String): Int {
        val dp = Array(a.length + 1) { IntArray(b.length + 1) }

        for (i in 0..a.length) dp[i][0] = i
        for (j in 0..b.length) dp[0][j] = j

        for (i in 1..a.length) {
            for (j in 1..b.length) {
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + cost
                )
            }
        }

        return dp[a.length][b.length]
    }

    private fun normalize(input: String): List<String> {
        return input
            .lowercase()
            .trim()
            .split("\\s+".toRegex())
            .filter { it.isNotBlank() }
    }
}