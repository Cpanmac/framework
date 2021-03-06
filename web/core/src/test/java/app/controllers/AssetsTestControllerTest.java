/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.controllers;

import leap.core.AppContext;
import leap.lang.io.IO;
import leap.lang.net.Urls;
import leap.lang.resource.FileResource;
import leap.web.WebTestCase;
import leap.web.assets.AssetStrategy;
import org.junit.BeforeClass;
import org.junit.Test;

public class AssetsTestControllerTest extends WebTestCase {
	
	protected static AssetStrategy fingerprintStrategy;
	
	@BeforeClass
	public static void init(){
		fingerprintStrategy = AppContext.factory().getBean(AssetStrategy.class);
	}

    protected String url(String path) {
        return get("assets_test/get_asset_url?path=" + Urls.encode(path)).assertOk().getContent();
    }
	
	@Test
	public void testHelloAsset() {
		get("/assets_test/hello?locale=zh_CN").assertContentEquals("alert('你好');");
		get("/assets_test/hello?locale=en").assertContentEquals("alert('hello');");
		
		assertPathWithFingerprint("/assets/js/hello.js",get("/assets_test/get_asset_url?locale=en&path=js/hello.js&debug=true").getContent());
		assertPathWithFingerprint("/assets/js/hello.js",get("/assets_test/get_asset_url?locale=zn_CN&path=js/hello.js&debug=true").getContent());
		
		assertPathWithFingerprint("/assets/js/hello.js",get("/assets_test/get_asset_url?locale=en&path=js/hello.js").getContent());
		assertPathWithFingerprint("/assets/js/hello.js",get("/assets_test/get_asset_url?locale=zn_CN&path=js/hello.js").getContent());
	}
	
	@Test
	public void testAssetsRequest() {
		get("/assets/public/test.js").assertContentContains("var i=0;");
		get("/assets/webjars/bootstrap/2.3.0/less/accordion.less").assertContentContains("Accordion");
	}
	
	@Test
	public void testWebjars() {
		get("/assets/bootstrap/2.3.0/less/accordion.less").assertContentContains("Accordion");
		get("/assets/bootstrap/less/accordion.less").assertContentContains("Accordion");
	}
	
	@Test
	public void testServlet3Jar() {
		get("/assets/js/test_servlet3.js").assertContentEquals("//Test Servlet3");
	}
	
	@Test
	public void testSpecialPaths() {
		get("/assets/plugins/pdfjs/web/images/toolbarButton-menuArrows.png").assertOk();
		get("/assets/plugins/pdfjs/web/locale/locale.properties").assertOk();
	}

    @Test
    public void testExternalAssets() {
        FileResource tempDir = app.getTempDir();

        tempDir.createRelative("./upload").getFile().mkdirs();
        tempDir.createRelative("./assets").getFile().mkdirs();

        FileResource file1 = tempDir.createRelative("./upload/1.js");
        FileResource file2 = tempDir.createRelative("./assets/external_assets_js1.js");

        IO.writeString(file1.getFile(), "var i=0;");
        IO.writeString(file2.getFile(), "var i=0;");

        System.out.println("file : " + file1.getURLString());

        String url1 = url("/upload/1.js");
        String url2 = url("/external_assets_js1.js");
        assertNotEmpty(url1);
        assertNotEmpty(url2);

        get(url1).assertContentEquals("var i=0;");
        get(url2).assertContentEquals("var i=0;");
    }

	private void assertPathWithFingerprint(String exepctedPath,String actualPathWithFingerprint) {
		String[] values = fingerprintStrategy.splitPathAndFingerprint(actualPathWithFingerprint);
		
		assertNotNull("The path '" + actualPathWithFingerprint + "' must contains fingerprint");
		assertEquals(exepctedPath, values[0]);
		assertEquals(fingerprintStrategy.getFingerprintLength(), values[1].length());
	}
}