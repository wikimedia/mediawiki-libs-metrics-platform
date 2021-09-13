import Foundation

class ContextController {

    private let integration: MetricsClientIntegration

    init(integration: MetricsClientIntegration) {
        self.integration = integration
    }

    func addRequestedValues(_ event: Event, config streamConfig: StreamConfig) {
        guard let requestedValues = streamConfig.producerConfig?.clientConfig?.requestedValues else {
            return
        }
        for value in requestedValues {
            switch value {
            // Page
            case .pageId:
                if event.pageData == nil {
                    event.pageData = PageData()
                }
                event.pageData?.id = integration.getPageId()
            case .pageNamespaceId:
                if event.pageData == nil {
                    event.pageData = PageData()
                }
                event.pageData?.namespaceId = integration.getPageNamespaceId()
            case .pageNamespaceText:
                if event.pageData == nil {
                    event.pageData = PageData()
                }
                event.pageData?.namespaceText = integration.getPageNamespaceText()
            case .pageTitle:
                if event.pageData == nil {
                    event.pageData = PageData()
                }
                event.pageData?.title = integration.getPageTitle()
            case .pageIsRedirect:
                if event.pageData == nil {
                    event.pageData = PageData()
                }
                event.pageData?.isRedirect = integration.getPageIsRedirect()
            case .pageRevisionId:
                if event.pageData == nil {
                    event.pageData = PageData()
                }
                event.pageData?.revisionId = integration.getPageRevisionId()
            case .pageWikidataId:
                if event.pageData == nil {
                    event.pageData = PageData()
                }
                event.pageData?.wikidataId = integration.getPageWikidataItemId()
            case .pageContentLanguage:
                if event.pageData == nil {
                    event.pageData = PageData()
                }
                event.pageData?.contentLanguage = integration.getPageContentLanguage()
            case .pageUserGroupsAllowedToEdit:
                if event.pageData == nil {
                    event.pageData = PageData()
                }
                event.pageData?.userGroupsAllowedToEdit = integration.getGroupsAllowedToEditPage()
            case .pageUserGroupsAllowedToMove:
                if event.pageData == nil {
                    event.pageData = PageData()
                }
                event.pageData?.userGroupsAllowedToMove = integration.getGroupsAllowedToMovePage()

                // User
            case .userId:
                if event.userData == nil {
                    event.userData = UserData()
                }
                event.userData?.id = integration.getUserId()
            case .userName:
                if event.userData == nil {
                    event.userData = UserData()
                }
                event.userData?.name = integration.getUserName()
            case .userGroups:
                if event.userData == nil {
                    event.userData = UserData()
                }
                event.userData?.groups = integration.getUserGroups()
            case .userIsLoggedIn:
                if event.userData == nil {
                    event.userData = UserData()
                }
                event.userData?.isLoggedIn = integration.getUserIsLoggedIn()
            case .userIsBot:
                if event.userData == nil {
                    event.userData = UserData()
                }
                event.userData?.isBot = integration.getUserIsBot()
            case .userCanProbablyEditPage:
                if event.userData == nil {
                    event.userData = UserData()
                }
                event.userData?.canProbablyEditPage = integration.getUserCanProbablyEditPage()
            case .userEditCount:
                if event.userData == nil {
                    event.userData = UserData()
                }
                event.userData?.editCount = integration.getUserEditCount()
            case .userEditCountBucket:
                if event.userData == nil {
                    event.userData = UserData()
                }
                event.userData?.editCountBucket = integration.getUserEditCountBucket()
            case .userRegistrationTimestamp:
                if event.userData == nil {
                    event.userData = UserData()
                }
                event.userData?.registrationTimestamp = integration.getUserRegistrationTimestamp()
            case .userLanguage:
                if event.userData == nil {
                    event.userData = UserData()
                }
                event.userData?.language = integration.getUserLanguage()
            case .userLanguageVariant:
                if event.userData == nil {
                    event.userData = UserData()
                }
                event.userData?.languageVariant = integration.getUserLanguageVariant()

                // Device
            case .devicePixelRatio:
                if event.deviceData == nil {
                    event.deviceData = DeviceData()
                }
                event.deviceData?.pixelRatio = integration.getDevicePixelRatio()
            case .deviceHardwareConcurrency:
                if event.deviceData == nil {
                    event.deviceData = DeviceData()
                }
                event.deviceData?.hardwareConcurrency = integration.getDeviceHardwareConcurrency()
            case .deviceMaxTouchPoints:
                if event.deviceData == nil {
                    event.deviceData = DeviceData()
                }
                event.deviceData?.maxTouchPoints = integration.getDeviceMaxTouchPoints()

                // Other
            case .accessMethod:
                event.accessMethod = "mobile app"
            case .platform:
                event.platform = "ios"
            case .platformFamily:
                event.platformFamily = "app"
            case .isProduction:
                event.isProduction = integration.isProduction()
            }
        }
    }

}
