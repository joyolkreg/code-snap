package idv.henry;

public class SetupTeardownIncluder {
	private PageData pageData;
	private boolean isSuite;
	private WikiPage testPage;
	private StringBuffer newPageContent;
	private PageCrawler pageCrawler;

	public static String render(PageData pageData) throws Exception {
		return render(pageData, false);
	}

	public static String render(PageData pageData, boolean isSuite)
			throws Exception {
		return new SetupTeardownIncluder(pageData).render(isSuite);
	}

	private SetupTeardownIncluder(PageData pageData) {
		this.pageData = pageData;
		this.testPage = pageData.getWikiPage();
		this.pageCrawler = this.testPage.getPageCrawler();
		this.newPageContent = new StringBuffer();
	}

	private String render(boolean isSuite) throws Exception {
		this.isSuite = isSuite;

		// is test page then
		if (this.pageData.hasAttribute("Test")) {
			includeSetupAndTeardownPages();
		}

		return this.pageData.getHtml();
	}

	private void includeSetupAndTeardownPages() throws Exception {

		// 1. include setup pages
		if (this.isSuite) {
			include(SuiteResponder.SUITE_SETUP_NAME, "-setup");
		}
		include("SetUp", "-setup");

		// 2. include page content
		newPageContent.append(this.pageData.getContent());

		// 3. include teardown pages
		include("TearDown", "-teardown");
		if (this.isSuite) {
			includeSuiteTeardownPage();
		}
		include(SuiteResponder.SUITE_TEARDOWN_NAME, "-teardown");

		// 4. update page content
		this.pageData.setContent(newPageContent.toString());
	}

	private void include(String pageName, String arg) {
		WikiPage inheritedPage = PageCrawlerImpl.getInheritedPage(pageName,
				this.testPage);

		if (inheritedPage == null) {
			return;
		}

		WikiPagePath pagePath = this.pageCrawler.getFullPath(page);
		String pagePathName = PathParser.render(pagePath);

		newPageContent.append("\n!include").append(arg).append(" .")
				.append(pagePathName).append("\n");
	}
}