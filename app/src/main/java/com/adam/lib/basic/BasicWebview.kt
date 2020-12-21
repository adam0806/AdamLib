package com.adam.lib.basic

import android.content.Context
import android.content.DialogInterface
import android.net.http.SslError
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.KeyEvent.ACTION_DOWN
import android.view.KeyEvent.KEYCODE_BACK
import android.webkit.*
import com.adam.lib.handler.MyHandler
import com.adam.lib.util.AlertUtils
import com.adam.lib.util.PreferencesUtils
import java.io.IOException
import java.io.InputStream
import java.lang.Exception
import java.lang.ref.WeakReference


/**
 * Created By Adam on 2020/12/8
//        webView.goBack();//跳到上個頁面
//        webView.goForward();//跳到下個頁面
//        webView.canGoBack();//是否可以跳到上壹頁(如果返回false,說明已經是第壹頁)
//        webView.canGoForward();//是否可以跳到下壹頁(如果返回false,說明已經是最後壹頁)
 */
class BasicWebview : WebView{
    lateinit var context: WeakReference<Context>
    class JsCallAndroid{
        @JavascriptInterface
        fun actionFromJs() {
            MyHandler.post{
                AlertUtils.showToast("Js調用Android")
            }
        }

        @JavascriptInterface
        fun actionFromJsWithParam(str: String) {
            MyHandler.post{
                AlertUtils.showToast("Js調用Android($str)")
            }
        }

        //優化方法一, 注入JS
        @JavascriptInterface
        fun getLocalSrc(src: String) : String{
            return "file://storage/emulated/0/app/file/a.jpeg"
        }
    }
    constructor(context: Context) : super(context){
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        init(context)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes){
        init(context)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, privateBrowsing: Boolean) : super(context, attrs, defStyleAttr, privateBrowsing){
        init(context)
    }

    fun init(context: Context){
        this.context = WeakReference(context)
        jsCallAndroid()
        initSetting()
    }


    /**
     * Android調用JS
    1. webview.loadUrl(“javascript:callJS()”)直接调用javascript:方法名, 此方法會刷新頁面
    2. evaluateJavascript(不會刷新頁面)
     */
    fun androidCallJs(jsFunction: String){
        //方法2 相比第一種, 簡潔效率高, Androidr 4.4 後使用 if(API < 18) loadUrl else  evaluateJavascript
        evaluateJavascript(jsFunction, ValueCallback<String?> {
            s ->  if(context.get() != null ) {
                AlertUtils.showDialog(context.get()!!,
                    "", "evaluateJavascript, $s",
                    "comfirm", "",
                    DialogInterface.OnClickListener { dialog, which ->
                        AlertUtils.dismissDialog()
                    },
                    null)
            }
        })
    }

    /**
     * JS調用Android
    1. addJavascriptInterface進行對象映射, 處理javascript被執行後回調
    2. WebViewClient的shouldOverrideUrlLoading()攔截url
    3. html中js的方法調用alert()、confirm()、prompt（）, webview.setWebChromeClient()中的onJsAlert()、onJsConfirm()、onJsPrompt（）去处理JS对话框
     */
    fun jsCallAndroid(){
        //方法1, 處理javascript被執行後回調(Android 4.2(API 16)以下存在漏洞)
        //参1是回调接口的实现;参2是JavaScript回调对象的名称
        addJavascriptInterface(JsCallAndroid(), "AndroidCallback")

        //方法2
        setWebViewClient(object : WebViewClient() {
            //優化方法二, 請求攔截
            override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                // 1. 判断拦截资源的条件
                if (url != null && url!!.contains("logo.gif")) {
                    // 假设网页图片资源为：http://abc.com/image/logo.gif
                    // 图片资源文件名为：logo.gif

                    // 2. 创建输入流
                    var inputStream: InputStream ?= null

                    try {
                        // 3. 获得需要替换的资源（存放在assets文件夹中）
                        inputStream = context.get()?.getAssets()?.open("image/abc.png")
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    // 4. 替换资源
                    var response = WebResourceResponse("image/png", "utf-8", inputStream)
                    return response
                }
                return super.shouldInterceptRequest(view, request)
            }
            override fun onPageFinished(view: WebView?, url: String?) {
                try {
                    val all_cookies: String = CookieManager.getInstance().getCookie(url)
                    PreferencesUtils.putString("all_cookies", all_cookies)

                    //優化方法一, 注入JS後, 頁面加載完成時修改圖片標籤
                    var js = "javascript:(function() {\n"+
                    "var objs = document.getElementsByTagName('img');\n"+
                    "for (var i = 0; i < objs.length; i++)  {\n"+
                        "var imgUrl = objs[i].getAttribute('src');\n"+
                        "var localUrl = window.local_obj.getLocalSrc(imgUrl);\n"
                        "if (localUrl) {\n"+
                        "    objs[i].setAttribute('src', localUrl);\n"+
                        "}\n"+
                    "}\n"+
                    "})()\n"
                    view?.loadUrl(js);
                }catch (e: Exception){
                    e.printStackTrace()
                }
                super.onPageFinished(view, url)
            }
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                if(context.get() != null ) {
                    AlertUtils.showDialog(context.get()!!,
                            "", "shouldOverrideUrlLoading攔截",
                            "comfirm", "",
                            DialogInterface.OnClickListener { dialog, which ->
                                AlertUtils.dismissDialog()
                            },
                            null)
                }
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler, error: SslError) {
                if(context.get() != null ) {
                    AlertUtils.showDialog(context.get()!!, "SSL Error", "Confirm to go on?", "OK", "Cancel",
                            DialogInterface.OnClickListener { dialog, which ->
                                handler.proceed()
                            },
                            DialogInterface.OnClickListener { dialog, which ->
                                onKeyDown(KEYCODE_BACK, KeyEvent(ACTION_DOWN, KEYCODE_BACK))
                            }
                    )
                }
            }
        })


        //方法3 html中js的方法调用alert()、confirm()、prompt（）
        webChromeClient = object : WebChromeClient() {
            override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                if(context.get() != null ) {
                    AlertUtils.showDialog(context.get()!!,
                            "", "onJsAlert, $message",
                            "comfirm", "",
                            DialogInterface.OnClickListener { dialog, which ->
                                result?.confirm();
                            },
                            null
                    )
                }
                return super.onJsAlert(view, url, message, result)
            }

            override fun onJsConfirm(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                return super.onJsConfirm(view, url, message, result)
            }

            override fun onJsPrompt(view: WebView?, url: String?, message: String?, defaultValue: String?, result: JsPromptResult?): Boolean {
                return super.onJsPrompt(view, url, message, defaultValue, result)
            }
        }
    }

    override fun loadUrl(url: String) {
        val all_cookies: String = PreferencesUtils.getString("all_cookies")
        setCookie(all_cookies, url);//在loadurl之前調用
        super.loadUrl(url)
    }
    fun initSetting(){
        val webSettings: WebSettings = getSettings()

        //LOAD_CACHE_ONLY: 不使用網絡，只讀取本地緩存數據
        //LOAD_DEFAULT: （默認）根據cache-control決定是否從網絡上取數據。
        //LOAD_NO_CACHE: 不使用緩存，只從網絡獲取數據.
        //LOAD_CACHE_ELSE_NETWORK，只要本地有，無論是否過期，或者no-cache，都使用緩存中的數據。
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setAppCacheEnabled(true)
        webSettings.setDatabaseEnabled(true)


        //支持自動加載圖片
        webSettings.setLoadsImagesAutomatically(true)

        //設置可以訪問文件
        webSettings.setAllowFileAccess(true)

        //設置編碼格式
        webSettings.setDefaultTextEncodingName("utf-8")

        //======Javascript交互======
        //加載的html中有JS執行動畫等操作會浪費CPU, 電量, onStop及onResume時分別setJavaScriptEnabled設維false, true
        webSettings.setJavaScriptEnabled(true)

        //支持通過JS打開新窗口
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true)

        //======自适应屏幕======
        //沒設置手機網頁UI的, 自適應會縮很小
        //將圖片調整到適合webview的大小
//        webSettings.setUseWideViewPort(true)
//        // 縮放至屏幕的大小
//        webSettings.setLoadWithOverviewMode(true)

        //引起webview重新布局(relayout)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        } else {
//        1.NARROW_COLUMNS：可能的話使所有列的寬度不超過屏幕寬度
//        2.NORMAL：正常顯示不做任何渲染
//        3.SINGLE_COLUMN：把所有內容放大webview等寬的壹列中
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }

        //======縮放操作======
        //支持縮放，默認為true。是下面那個的前提。
        webSettings.setSupportZoom(true)
        //設置內置的縮放控件。若為false，則該WebView不可縮放
        webSettings.setBuiltInZoomControls(true)
        //隱藏原生的縮放控件
        webSettings.setDisplayZoomControls(false)

        //======解決部分網頁開啟問題======
        //開啟本地DOM存儲(解決部分網頁白頻), 持久化本地存儲, 除非主動山數據, 否則不會過期, 任何人能讀取, 敏感數據不適合, Android默認關閉
        webSettings.setDomStorageEnabled(true)

        //解決https URL 5.0以上加載不了, 可能網頁用到非https的資源
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
    }

    /**
     * @param cookie 上面獲取到的存儲在本地的cookie字符串
     * @param url 要加載的頁面url
     */
    private fun setCookie(all_cookies: String, url: String) {
        if (!TextUtils.isEmpty(all_cookies)) {
            var arrayCookies = all_cookies.split(";");
            if (arrayCookies != null && arrayCookies.size > 0) {
                val cookieManager = CookieManager.getInstance()
                for(cookie in arrayCookies){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        cookieManager.removeSessionCookies(null)
                        cookieManager.flush()
                    } else {
                        cookieManager.removeSessionCookie()
                        CookieSyncManager.getInstance().sync()
                    }
                    cookieManager.setAcceptCookie(true)
                    cookieManager.setCookie(url, cookie)
                }
            }
        }
    }
    fun clear(){
        val cookieManager = CookieManager.getInstance()
        cookieManager.removeAllCookie()

        //內核緩存是全局的, 此方法針不僅僅webview, 而是對整個應用程序
        clearCache(true)

        //清除除了當前訪問外的訪問紀錄
        clearHistory()

        //清除自動填充的表單數據, 不會清除webview儲存到本地的數據
        clearFormData()

        //也可以刪除緩存資料夾
//        val file: File = CacheManager.getCacheFileBaseDir()
//        if (file != null && file.exists() && file.isDirectory()) {
//            for (item in file.listFiles()) {
//                item.delete()
//            }
//            file.delete()
//        }

        //刪除緩存數據庫
        if(context.get() != null ) {
            context.get()!!.deleteDatabase("webview.db")
            context.get()!!.deleteDatabase("webviewCache.db")
        }
    }

    //瀏覽網頁點系統的Back鍵時, 整個瀏覽器會調用finish()結束自己
    //監聽並在還有上一頁時, 返回上一頁
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if((keyCode == KEYCODE_BACK) && canGoBack()){
            goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}