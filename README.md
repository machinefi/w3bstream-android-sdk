# W3bstream Android SDK

The W3bstream Android SDK is a framework that enables connecting data generated by devices and machines in the physical world to the blockchain world using the IoTeX blockchain. W3bStream uses a network of decentralized gateways (W3bStream nodes) to stream encrypted data from IoT devices and machines and create proofs of real-world events on different blockchains. For documentation and APIs, please visit the project [website](https://mainnet.w3bstream.com/). 

## Getting started
To get started, follow these steps:
1. Login to w3bstream [https://dev.w3bstream.com/](https://dev.w3bstream.com/)
2. Create your project on the website
3. Generate your publisher key and token
4. Consult the help document at [Geo-location with WASM Sample](https://iotex.larksuite.com/docx/UawQd67JPopjqHxlSZmuV9HjsEh)

### Setting up the dependency
The latest release is available on [Maven Central](https://search.maven.org/artifact/com.w3bstream/w3bstream-android/1.0/aar).

```
    implementation 'com.w3bstream:w3bstream-android:1.0.1'
```

### Initialization
In your app, initialize the SDK with your project values as shown below:
```
    val url = "http://dev.w3bstream.com:8889/srv-applet-mgr/v0/event/eth_0x2ee1d96cb76579e2c64c9bb045443fb3849491d2_geo_example_claim_nft"
    val publiserToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJQYXlsb2FkIjoiNjc3ODIwMjA4Mjc0MDIyNCIsImlzcyI6InczYnN0cmVhbSJ9.sN9pPsoRP-bRfKY2i1_qw9fRyigGRK6XT5osrdJbk7A"
    val w3bStream by W3bStream.build(
            HttpService(url)
                .addHeader("Authorization", publisherToken)
                .addHeader("Content-Type", "application/octet-stream")
        )
```

### Make the payload
Create the payload in JSON format and then encode it as Base64, as shown below:
```
    // The following is the server-verified location information.
    // If the uploaded location is within 100 meters of the server's location, you can mint an NFT.
    // {"latitude": "36.702977661503", "longitude": "117.13273760933" }

    val latitude = "36.7026"
    val longitude = "117.13273760933"
    val walletAddress = "0x2eE1d96CB76579e2c64C9BB045443Fb3849491D2" // NFT receiving address
    val payload = """
        {
            "latitude": latitude,
            "longitude": longitude,
            "walletAddress": walletAddress
        }
    """
```

### Publish event to Webstream server by https
To publish events to the W3bstream server via HTTPS, use the following code:
```   
    val response = w3bStream.publishEvents("DEFAULT", payload) // type: Event type
```
