package com.bunbeauty.shared

import com.bunbeauty.designsystem.FoodDeliveryCompany
import kotlinx.browser.window
import org.w3c.dom.url.URLSearchParams

// Brand shown when the host is not recognized (local dev, preview deploys, etc.).
private const val DEFAULT_FLAVOR = "papakarlo"

// Domains whose name does not contain the flavor string. Checked before the
// substring match, so a hyphenated or misspelled domain still opens its cafe.
private val DOMAIN_FLAVORS = listOf(
    "urban-vaib" to "urbanvibe",
)

// Resolves which brand (flavor) the web app should show from the current URL, so
// a single build/host can serve many cafes by domain:
//  1) an explicit ?flavor=... override (handy for local and preview testing);
//  2) otherwise an explicit domain -> flavor mapping (DOMAIN_FLAVORS);
//  3) otherwise the first known brand whose flavor name appears in the hostname
//     (works for subdomains like gustopub.example.ru and separate domains alike);
//  4) otherwise the default brand.
fun resolveWebFlavor(): String {
    val override = URLSearchParams(window.location.search).get("flavor")
    if (override != null && FoodDeliveryCompany.entries.any { company -> company.flavor == override }) {
        return override
    }

    val host = window.location.hostname.lowercase()

    DOMAIN_FLAVORS.firstOrNull { (domain, _) -> host.contains(domain) }
        ?.let { (_, flavor) -> return flavor }

    return FoodDeliveryCompany.entries
        .sortedByDescending { company -> company.flavor.length }
        .firstOrNull { company -> host.contains(company.flavor) }
        ?.flavor
        ?: DEFAULT_FLAVOR
}
