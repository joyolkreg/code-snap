package idv.henry;

public class SetupTeardownIncluder {
	private final StringBuilder newPageContent = new StringBuilder();

	private final PageData pageData;
	private final WikiPage testPage;
	private final PageCrawler pageCrawler;

	private SetupTeardownIncluder(PageData pageData) {

		this.pageData = pageData;
		this.testPage = pageData.getWikiPage();
		this.pageCrawler = this.testPage.getPageCrawler();
	}

	private String render(boolean isSuite) throws Exception {

		// is test page then
		if (this.pageData.hasAttribute("Test")) {
			includeSetupAndTeardownPages(isSuite);
		}

		return this.pageData.getHtml();
	}

	private void includeSetupAndTeardownPages(boolean isSuite) throws Exception {

		// 1. include setup pages
		if (isSuite) {
			includePageWithArg(SuiteResponder.SUITE_SETUP_NAME, "-setup");
		}
		includePageWithArg("SetUp", "-setup");

		// 2. include page content
		newPageContent.append(this.pageData.getContent());

		// 3. include teardown pages
		includePageWithArg("TearDown", "-teardown");
		if (isSuite) {
			include(SuiteResponder.SUITE_TEARDOWN_NAME, "-teardown");
		}

		// 4. update page content
		this.pageData.setContent(newPageContent.toString());
	}

	private void includePageWithArg(String pageName, String arg) {

		WikiPage inheritedPage = PageCrawlerImpl.getInheritedPage(pageName,
				this.testPage);

		if (inheritedPage == null) {
			return;
		}

		// found inherited page, include it into the new page content
		WikiPagePath pagePath = this.pageCrawler.getFullPath(page);
		String pagePathName = PathParser.render(pagePath);

		newPageContent.append("\n!include").append(arg).append(" .")
				.append(pagePathName).append("\n");
	}

	public static String render(PageData pageData) throws Exception {

		return render(pageData, false);
	}

	public static String render(PageData pageData, boolean isSuite)
			throws Exception {

		return new SetupTeardownIncluder(pageData).render(isSuite);
	}
}