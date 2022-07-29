package com.machinefi.w3bstream.common.exception

import com.google.gson.JsonParseException

class JsonSyntaxException(message: String): JsonParseException(message)