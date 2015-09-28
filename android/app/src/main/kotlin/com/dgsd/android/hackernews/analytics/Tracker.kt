package com.dgsd.android.hackernews.analytics

public class Tracker: Agent {

    private val agents = arrayListOf<Agent>()

    constructor(vararg agents: Agent) {
        this.agents.addAll(agents)
    }

    override fun trackScreenView(name: String) {
        agents.forEach { it.trackScreenView(name) }
    }

    override fun trackClick(item: String) {
        agents.forEach { it.trackClick(item) }
    }

    override fun trackSwipeRefresh(item: String) {
        agents.forEach { it.trackSwipeRefresh(item) }
    }

    fun addAgent(agent: Agent) {
        agents.add(agent)
    }
}

interface Agent {

    fun trackScreenView(name: String)

    fun trackClick(item: String)

    fun trackSwipeRefresh(item: String)
}