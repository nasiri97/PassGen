import SwiftUI

@main
struct iOSApp: App {

    init() {
            #if DEBUG
            isIosDebugBuild = true
            #else
            isIosDebugBuild = false
            #endif
        }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}