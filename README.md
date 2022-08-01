# webstream-android-framework

## Integration
Import `w3bstream` into your project as a module, and sync you project.


## Usage
TIPS: Android emulator does not support location service, please use a real machine to test.

### Init

```
    private val config by lazy {
        W3bStreamKitConfig(
            AUTH_HOST,
            HTTPS_UPLOAD_API,
            WEB_SOCKET_UPLOAD_API
        )
    }

    private val w3bStreamKit by lazy {
        W3bStreamKit.Builder(config).build()
    }
```


### Create device
```
    private fun create() {
        lifecycleScope.launch {
            val device = w3bStreamKit.createDevice()
        }
    }

```

### Register
```
    w3bStreamKit.register(imei, sn)
```

### Upload data
```
	w3bStreamKit.startUpload {
	    return@startUpload "{"latitude":34.09589161,"location":106.42410187}"
	}
```
TIPS: The type of data must be json string

### Other
Set the server for uploading data
```
w3bStreamKit.setHttpsServerApi(api)
w3bStreamKit.setWebSocketServerApi(api)
```

Set the interval for uploading data
```
w3bStreamKit.setUploadInterval(seconds)
```
