//
//  AppDelegate.swift
//  PapaKarloSwift
//
//  Created by Марк Шавловский on 05.07.2022.
//

import FirebaseCore
import FirebaseMessaging
import Foundation
import shared
import SwiftUI

class AppDelegate: NSObject, UIApplicationDelegate {
    var restrictRotation: UIInterfaceOrientationMask = .portrait
    let gcmMessageIDKey = "gcm.Message_ID"

    func application(_ application: UIApplication, didFinishLaunchingWithOptions _: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        AppBootstrap.startIfNeeded()

        if #available(iOS 10.0, *) {
            UNUserNotificationCenter.current().delegate = self
            let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
            UNUserNotificationCenter.current().requestAuthorization(
                options: authOptions,
                completionHandler: { _, _ in
                    self.syncNotificationPermission()
                }
            )
        } else {
            let settings = UIUserNotificationSettings(types: [.alert, .badge, .sound], categories: nil)
            application.registerUserNotificationSettings(settings)
        }

        application.registerForRemoteNotifications()
        Messaging.messaging().delegate = self
        syncNewsSubscription()
        syncNotificationPermission()

        return true
    }

    func applicationDidBecomeActive(_: UIApplication) {
        syncNotificationPermission()
    }

    func application(_: UIApplication, supportedInterfaceOrientationsFor _: UIWindow?) -> UIInterfaceOrientationMask {
        return .portrait
    }

    func application(_: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable: Any], fetchCompletionHandler _: @escaping (UIBackgroundFetchResult) -> Void) {
        if let messageID = userInfo[gcmMessageIDKey] {
            print("Message ID: \(messageID)")
        }
    }

    private func syncNotificationPermission() {
        UNUserNotificationCenter.current().getNotificationSettings { settings in
            let enabled = settings.authorizationStatus == .authorized || settings.authorizationStatus == .provisional
            iosComponent.provideUpdateNotificationPermissionUseCase().invoke(enabled: enabled) { _ in }
        }
    }

    private func syncNewsSubscription() {
        iosComponent.provideSyncNotificationSubscriptionUseCase().invoke { _ in }
    }

    private func handleNotificationOpen(userInfo: [AnyHashable: Any]) {
        guard userInfo["destination"] as? String == "order_details",
              let notificationId = userInfo["notificationId"] as? String,
              let orderUuid = userInfo["orderUuid"] as? String else { return }

        DispatchQueue.main.async {
            iosComponent.provideNotificationNavigation().open(
                notification: NotificationOpen(
                    notificationId: notificationId,
                    orderUuid: orderUuid,
                    campaign: userInfo["campaign"] as? String ?? "",
                    touch: userInfo["touch"] as? String ?? "",
                    variant: userInfo["variant"] as? String ?? ""
                )
            )
        }
    }
}

extension AppDelegate: UNUserNotificationCenterDelegate {
    func userNotificationCenter(_: UNUserNotificationCenter,
                                willPresent notification: UNNotification) async
        -> UNNotificationPresentationOptions {
        return [.alert, .sound]
    }

    func userNotificationCenter(_: UNUserNotificationCenter,
                                didReceive response: UNNotificationResponse) async {
        handleNotificationOpen(userInfo: response.notification.request.content.userInfo)
    }

    func application(_: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        Messaging.messaging().setAPNSToken(deviceToken, type: .unknown)
    }
}

extension AppDelegate: MessagingDelegate {
    func messaging(_: Messaging, didReceiveRegistrationToken _: String?) {
        iosComponent.provideUpdateNotificationUseCase().invoke { error in
            if error != nil {
                print("Token was not updated")
            }
        }
        syncNewsSubscription()
    }
}
