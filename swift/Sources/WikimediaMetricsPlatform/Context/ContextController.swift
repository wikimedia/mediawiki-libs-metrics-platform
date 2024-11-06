import Foundation

class ContextController {

    private let integration: MetricsClientIntegration

    init(integration: MetricsClientIntegration) {
        self.integration = integration
    }

    func addRequestedValues(_ event: Event, config streamConfig: StreamConfig) {
        for value in streamConfig.getRequestedValues() {
            switch value {

            /// Agent
            /// NOTE: The app install ID, client platform, and client platform family are initialized in `Event()`.
            /// See https://wikitech.wikimedia.org/wiki/Metrics_Platform/Contextual_attributes
            case .agentAppInstallId:
                break
            case .agentClientPlatform:
                break
            case .agentClientPlatformFamily:
                break
            
            /// Page
            case .pageId:
                if event.page == nil {
                    event.page = Event.Page()
                }
                event.page?.id = integration.getPageId()
            case .pageTitle:
                if event.page == nil {
                    event.page = Event.Page()
                }
                event.page?.title = integration.getPageTitle()
            case .pageNamespace:
                if event.page == nil {
                    event.page = Event.Page()
                }
                event.page?.namespace = integration.getPageNamespaceId()
            case .pageNamespaceName:
                if event.page == nil {
                    event.page = Event.Page()
                }
                event.page?.namespaceName = integration.getPageNamespaceText()
            case .pageRevisionId:
                if event.page == nil {
                    event.page = Event.Page()
                }
                event.page?.revisionId = integration.getPageRevisionId()
            case .pageWikidataId:
                if event.page == nil {
                    event.page = Event.Page()
                }
                event.page?.wikidataId = integration.getPageWikidataItemId()
            case .pageContentLanguage:
                if event.page == nil {
                    event.page = Event.Page()
                }
                event.page?.contentLanguage = integration.getPageContentLanguage()
            case .pageIsRedirect:
                if event.page == nil {
                    event.page = Event.Page()
                }
                event.page?.isRedirect = integration.getPageIsRedirect()
            case .pageUserGroupsAllowedToEdit:
                if event.page == nil {
                    event.page = Event.Page()
                }
                event.page?.userGroupsAllowedToEdit = integration.getGroupsAllowedToEditPage()
            case .pageUserGroupsAllowedToMove:
                if event.page == nil {
                    event.page = Event.Page()
                }
                event.page?.userGroupsAllowedToMove = integration.getGroupsAllowedToMovePage()

            /// MediaWiki
                
            /// Since the Wikipedia app (and others) interact with the MediaWiki instance via the API, no skin will be enabled.
            case .mediawikiSkin:
                break
                
            case .mediawikiVersion:
                if event.mediawiki == nil {
                    event.mediawiki = Event.MediaWiki()
                }
                // TODO
            case .mediawikiIsProduction:
                if event.mediawiki == nil {
                    event.mediawiki = Event.MediaWiki()
                }
                event.mediawiki?.isProduction = integration.isProduction()
            case .mediawikiIsDebugMode:
                if event.mediawiki == nil {
                    event.mediawiki = Event.MediaWiki()
                }
                // TODO
            case .mediawikiDatabase:
                if event.mediawiki == nil {
                    event.mediawiki = Event.MediaWiki()
                }
                // TODO
            case .mediawikiSiteContentLanguage:
                if event.mediawiki == nil {
                    event.mediawiki = Event.MediaWiki()
                }
                // TODO
            case .mediawikiSiteContentLanguageVariant:
                if event.mediawiki == nil {
                    event.mediawiki = Event.MediaWiki()
                }
                // TODO

            // Performer
            case .performerIsLoggedIn:
                if event.performer == nil {
                    event.performer = Event.Performer()
                }
                event.performer?.isLoggedIn = integration.getUserIsLoggedIn()
            case .performerId:
                if event.performer == nil {
                    event.performer = Event.Performer()
                }
                event.performer?.id = integration.getUserId()
            case .performerName:
                if event.performer == nil {
                    event.performer = Event.Performer()
                }
                event.performer?.name = integration.getUserName()
            
            case .performerSessionId:
                // TODO
                if event.performer == nil {
                }
                //event.performer?.sessionId = sessionController.getSessionId()
            case .performerPageviewId:
                // TODO
                if event.performer == nil {
                }
                //event.performer?.pageviewId = integration.getPageviewId()

            case .performerGroups:
                if event.performer == nil {
                    event.performer = Event.Performer()
                }
                event.performer?.groups = integration.getUserGroups()
            case .performerIsBot:
                if event.performer == nil {
                    event.performer = Event.Performer()
                }
                event.performer?.isBot = integration.getUserIsBot()
            case .performerLanguage:
                if event.performer == nil {
                    event.performer = Event.Performer()
                }
                event.performer?.language = integration.getUserLanguage()
            case .performerLanguageVariant:
                if event.performer == nil {
                    event.performer = Event.Performer()
                }
                event.performer?.languageVariant = integration.getUserLanguageVariant()
            case .performerCanProbablyEditPage:
                if event.performer == nil {
                    event.performer = Event.Performer()
                }
                event.performer?.canProbablyEditPage = integration.getUserCanProbablyEditPage()
            case .performerEditCount:
                if event.performer == nil {
                    event.performer = Event.Performer()
                }
                event.performer?.editCount = integration.getUserEditCount()
            case .performerEditCountBucket:
                if event.performer == nil {
                    event.performer = Event.Performer()
                }
                event.performer?.editCountBucket = integration.getUserEditCountBucket()
            case .performerRegistrationDt:
                if event.performer == nil {
                    event.performer = Event.Performer()
                }
                event.performer?.registrationDt = integration.getUserRegistrationTimestamp()

            }
        }
    }

}
