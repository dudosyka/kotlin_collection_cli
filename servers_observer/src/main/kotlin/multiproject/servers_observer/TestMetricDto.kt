package multiproject.servers_observer

import io.prometheus.client.Summary

data class TestMetricDto (
    val elementsCounter: Summary,
    val totalRequests: Summary
)