//
//  iOSApp.swift
//  PapaKarloSwift
//
//  Created by Марк Шавловский on 03.02.2022.
//

import FirebaseAnalytics
import FirebaseCore
import shared
import SwiftUI

let iosComponent = IosComponent()

/// Single entry for Firebase + Koin. Safe to call from App.init and/or AppDelegate
/// regardless of which runs first on device vs simulator.
enum AppBootstrap {
    private static var didStart = false

    static func startIfNeeded() {
        guard !didStart else { return }
        didStart = true

        // Must configure Firebase before Analytics / Messaging / Koin Firebase calls.
        if FirebaseApp.app() == nil {
            FirebaseApp.configure()
        }
        #if DEBUG
        Analytics.setAnalyticsCollectionEnabled(false)
        AppMetricaActivationKt.activateAppMetrica(dataSendingEnabled: false)
        #else
        Analytics.setAnalyticsCollectionEnabled(true)
        AppMetricaActivationKt.activateAppMetrica(dataSendingEnabled: true)
        #endif
        KoinKt.doInitKoin()
    }
}

@main
struct PapaKarloSwiftApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate

    init() {
        AppBootstrap.startIfNeeded()
    }

    var body: some Scene {
        WindowGroup {
            VStack {
                ComposeView()
                    .ignoresSafeArea()
            }
        }
    }
}

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        let vc = AppIosKt.MainViewController(
            flavor: Bundle.main.object(forInfoDictionaryKey: "CFBundleName") as? String ?? ""
        )

        vc.view.insetsLayoutMarginsFromSafeArea = false
        vc.additionalSafeAreaInsets = .zero

        return vc
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

protocol HasApply {}

extension HasApply {
    func applyAndReturn(closure: (Self) -> Void) -> Self {
        closure(self)
        return self
    }

    func apply(closure: (Self) -> Void) {
        closure(self)
    }
}

extension NSObject: HasApply {}
