# webstream-android-framework

## Integration
Import `w3bstream-kotlin` into your project as a module, and sync you project.


## Usage
### Init

```
    private val config by lazy {
        W3bstreamKitConfig(
            AUTH_HOST,
            HTTPS_UPLOAD_API,
            WEB_SOCKET_UPLOAD_API
        )
    }

    private val w3bstreamKit by lazy {
        W3bstreamKit.Builder(config).build()
    }
```


### Create device
```
    private fun create() {
        lifecycleScope.launch {
            val device = w3bstreamKit.createDevice()
            mTvImei.text = "IMEI:${device.imei}"
            mTvSn.text = "SN:${device.sn}"
        }
    }

```

### Upload data
```
	w3bstreamKit.startUploading {
	    return@startUploading "{"imei":"100374242236884","latitude":34.09589161,"location":106.42410187}"
	}
```
TIPS: The type of data must be json string


### Other
Sign the device
```
w3bstreamKit.sign(imei, sn, pubKey)
```

Set the server for uploading data
```
w3bstreamKit.httpsServerApi(api)
w3bstreamKit.socketServerApi(api)
```

Set the interval for uploading data
```
w3bstreamKit.uploadFrequency(mills)
```
