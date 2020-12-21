package com.adam.lib.util

import android.content.Context
import android.graphics.Typeface
import android.os.Environment
import android.text.TextUtils
import java.io.*

/**
 * Created By Adam on 2020/12/2
 */
class FileUtils {
    enum class PATHTYPE {
        ROOTDIR,
        CACHEDIR,
        FILEDIR,
        FILEDIR_WITH_NAME,
        EXTERNAL_CACHEDIR,
        EXTERNAL_FILEDIR,
        EXTERNAL_STORAGE,
    }

    companion object {
        fun getRawFilePath(context: Context, fileName: String): String {
            return "android.resource://" + context.packageName + "/raw/" + fileName
        }

        fun getRawFilePath(context: Context, id: Int): String {
            return "android.resource://" + context.packageName + "/" + id
        }

        fun getRawStream(context: Context, resId: Int): InputStream {
            return context.resources.openRawResource(resId)
        }

        fun getAssetStream(context: Context, fileName: String): InputStream {
            return context.assets.open(fileName)
        }

        /**
         * EXTERNAL_FILEDIR必須要帶fileType
         * deleteFile(context: Context, name: String, type: PATHTYPE, fileType: String?)
         */
        fun deleteFile(context: Context, name: String, type: PATHTYPE): Boolean {
            return deleteFile(context, name, type, null)
        }

        fun deleteFile(context: Context, name: String, type: PATHTYPE, fileType: String?): Boolean {
            var file = readFile(context, name, type, fileType)
            return file?.delete() ?: false
        }

        fun isSDCardMounted(): Boolean {
            return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
        }

        /**
         * EXTERNAL_FILEDIR必須要帶fileType
         * writeFileString(context: Context, name: String, content: String, type:PATHTYPE, fileType: String?)
         */
        fun writeFileString(context: Context, name: String, content: String, type: PATHTYPE): Boolean {
            return writeFileString(context, name, content, type, null)
        }

        fun writeFileString(context: Context, name: String, content: String, type: PATHTYPE, fileType: String?): Boolean {
            var fileOutputStream: FileOutputStream? = null
            var succuess = true
            try {
                fileOutputStream = FileOutputStream(readFile(context, name, type, fileType))
                //直接寫比較慢
//                fileOutputStream.write(content.toByteArray())

//                var bw = BufferedWriter(FileWriter(readFile(context, name, type, fileType)))
//                bw.write(content)
//                bw.flush();
//                bw.close()

                //優化, 用緩存來寫, 讀寫涉及I/O, 用內存高速寫入緩衝區, 滿後一次性寫入到外部
                //BufferedInputStream/BufferedOutputStream的默認緩沖區大小是8192字節(8M)。當每次讀取數據量接近或遠超這個值時，兩者效率就沒有明顯差別了
                val bos = BufferedOutputStream(fileOutputStream)
                bos.write(content.toByteArray())
                bos.close()

            } catch (e: java.lang.Exception) {
                succuess = false
                e.printStackTrace()
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                return succuess
            }
        }

        /**
         * 展示另一種取得fileOutputStream的方法
         */
        fun writeFileString(context: Context, name: String, content: String): Boolean {
            var fileOutputStream: FileOutputStream? = null
            var succuess = true
            try {
                // 1.打开文件
                fileOutputStream = context.openFileOutput(name, Context.MODE_PRIVATE)
                // 2.写操作
                fileOutputStream.write(content.toByteArray())
            } catch (e: java.lang.Exception) {
                succuess = false
                e.printStackTrace()
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                return succuess
            }
        }

        /**
         * 等同readFileString(context, name, FILEDIR)
         */
        fun readFileString(context: Context, name: String): String {
            var fileInputStream: FileInputStream? = null
            val builder = StringBuilder()
            try {
                // 1.打开文件
                fileInputStream = context.openFileInput(name)
                // 2.读操作 字节流（byte 10001） ----->  字符流（编码）ASCLL 流操作
                // Stream 字节流，reader write字符流
                val reader = BufferedReader(InputStreamReader(fileInputStream))
                var line: String? = null
                while (reader.readLine().also { line = it } != null) {
                    builder.append(line)
                }

//                var bis = BufferedInputStream(fileInputStream)
//                val buffer = ByteArray(1024)
//                var bytesRead = 0
//                while (bis.read(buffer).also({ bytesRead = it }) !== -1) {
//                    builder.append(String(buffer, 0, bytesRead))
//                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            } finally {
                // 3.关闭文件
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                return builder.toString()
            }
        }

        /**
         * 若放在資料夾, path要連同資料夾路徑
         * fons/XXX.otf
         */
        fun readAssetTypeFace(context: Context, path: String): Typeface {
            return Typeface.createFromAsset(context.assets, path)
        }

        /**
         * name代null表示取該資料夾
         */
        fun readFileString(context: Context, name: String, type: PATHTYPE): String {
            return readFileString(context, name, type, null)
        }

        /**
         * 內部儲存
         * 包名com.example.lenovo.adamnote
         * Environment.getDataDirectory()   /data
         * context.cacheDir                 /data/data/com.example.lenovo.adamnote/cache
         * context.filesDir                 /data/data/com.example.lenovo.adamnote/files
         *
         * 外部儲存
         * EXTERNAL_FILEDIR必須要帶fileType
         * Android 6.0後要動態申請權限
         * type可以為Environment中的
         * public static String DIRECTORY_MUSIC = "Music";
        public static String DIRECTORY_ALARMS = "Alarms";
        public static String DIRECTORY_NOTIFICATIONS = "Notifications";
        public static String DIRECTORY_PICTURES = "Pictures";
        public static String DIRECTORY_MOVIES = "Movies";
        public static String DIRECTORY_DOWNLOADS = "Download";
        public static String DIRECTORY_DCIM = "DCIM";
        public static String DIRECTORY_DOCUMENTS = "Documents";

        根據包名
        context.externalCacheDir    /storage/emulated/0/Android/data/com.example.lenovo.adamnote/cache
        context.getExternalFilesDir("")    /storage/emulated/0/Android/data/com.example.lenovo.adamnote/files
        context.getExternalFilesDir("")    /storage/emulated/0/Android/data/com.example.lenovo.adamnote/files
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)    /storage/emulated/0/Android/data/com.example.lenovo.adamnote/Pictures

        PATHTYPE: 指定目錄
        fileType: 外部路徑可取對應檔案格式
         */
        fun readFile(context: Context, name: String, type: PATHTYPE): File? {
            return readFile(context, name, type, null)
        }

        fun readFile(context: Context, name: String, type: PATHTYPE, fileType: String?): File? {
            var file: File? = null
            var isNameEmpty = TextUtils.isEmpty(name)
            when (type) {
                /**
                 * 根目錄$rootDir 應用app無法存取!!會報失敗
                 * /data/test.txt
                 */
                PATHTYPE.ROOTDIR -> {
                    file = if (isNameEmpty) Environment.getDataDirectory() else File(Environment.getDataDirectory(), name)
                }
                /**
                 * 應用緩存目錄 $applicationDir/cache (應用程序目錄 $applicationDir: $rootDir/data/包名 (不一定, 看設被))
                 * /data/user/0/com.example.lenovo.adamnote/cache/test.txt
                 */
                PATHTYPE.CACHEDIR -> {
                    file = if (isNameEmpty) context.cacheDir else File(context.cacheDir, name)
                }
                /**
                 * 應用文件目錄 $applicationDir/files (等同openFileInput(取出直接是InputStream))
                 * /data/user/0/com.example.lenovo.adamnote/files/test.txt
                 */
                PATHTYPE.FILEDIR -> {
                    file = if (isNameEmpty) context.filesDir else File(context.filesDir, name)
                }
                //根目錄 /storage/emulated/0(不一定, 看設備)

                /**
                 * Environment.getExternalStorageDirectory() android Q已經捨棄, 用getExternalFilesDir , 但用這個才能在app uninstall後保存著
                 * /storage/emulated/0/test.txt
                 */
                PATHTYPE.EXTERNAL_STORAGE -> {
                    if (isSDCardMounted() && fileType != null) {
                        file = if (isNameEmpty) Environment.getExternalStorageDirectory() else File(Environment.getExternalStorageDirectory(), name)
                    }
                }

                /**
                 * 外部SD卡cache, 刪除app時會一併砍掉
                 * /storage/emulated/0/Android/data/com.example.lenovo.adamnote/cache/test.txt
                 */
                PATHTYPE.EXTERNAL_CACHEDIR -> {
                    if (isSDCardMounted()) {
                        file = if (isNameEmpty) context.externalCacheDir else File(context.externalCacheDir, name)
                    }
                }
                /**
                 * 外部SD卡file, 刪除app時會一併砍掉
                 * /storage/emulated/0/Android/data/com.example.lenovo.adamnote/files/Documents/test.txt
                 */
                PATHTYPE.EXTERNAL_FILEDIR -> {
                    if (isSDCardMounted() && fileType != null) {
                        file = if (isNameEmpty) context.getExternalFilesDir(fileType) else File(context.getExternalFilesDir(fileType), name)
                    }
                }
            }
            return file
        }

        fun readFileString(context: Context, name: String, type: PATHTYPE, fileType: String?): String {
            var fileInputStream: FileInputStream? = null
            val builder = StringBuilder()
            try {
                // 1.打开文件
                fileInputStream = FileInputStream(readFile(context, name, type, fileType))
                // 2.读操作 字节流（byte 10001） ----->  字符流（编码）ASCLL 流操作
                // Stream 字节流，reader write字符流
                val reader = BufferedReader(InputStreamReader(fileInputStream))
                var line: String? = null
                while (reader.readLine().also { line = it } != null) {
                    builder.append(line)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            } finally {
                // 3.关闭文件
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                return builder.toString()
            }
        }

        /**
         * 得到sdcard的路径
         * @return
         */
        fun getSDCardRoot(): String? {
            return if (isSDCardMounted()) {
                Environment.getExternalStorageDirectory().absolutePath
            } else ""
        }

        /**
         * 創建文件的路徑及文件
         * @param path 路徑，方法中以默認包含了sdcard的路徑，path格式是"/path...."
         * @param filename 文件的名稱
         * @return 返回文件的路徑，創建失敗的話返回為空
         */
        fun createMkdirsAndFiles(path: String, filename: String?): String {
            var path = path
            if (TextUtils.isEmpty(path)) {
                throw RuntimeException("路徑為空")
            }
            path = getSDCardRoot() + path
            val file = File(path)
            if (!file.exists()) {
                try {
                    file.mkdirs()
                } catch (e: Exception) {
                    throw RuntimeException("創資料夾失敗")
                }
            }
            val f = File(file, filename)
            if (!f.exists()) {
                try {
                    f.createNewFile()
                } catch (e: IOException) {
                    throw RuntimeException("創文件失敗")
                }
            }
            return f.absolutePath
        }

        /**
         * 把内容写入文件
         * @param path 文件路径
         * @param text 内容
         */
        fun write2File(path: String?, text: String?, append: Boolean) {
            var bw: BufferedWriter? = null
            try {
                //1.创建流对象
                bw = BufferedWriter(FileWriter(path, append))
                //2.写入文件
                bw.write(text)
                //换行刷新
                bw.newLine()
                bw.flush()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                //4.关闭流资源
                if (bw != null) {
                    try {
                        bw.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }


}